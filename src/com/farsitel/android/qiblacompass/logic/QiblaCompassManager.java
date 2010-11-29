package com.farsitel.android.qiblacompass.logic;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import android.hardware.Camera.PreviewCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.farsitel.android.qiblacompass.activities.QiblaActivity;
import com.farsitel.android.qiblacompass.data.AthanTime;
import com.farsitel.android.qiblacompass.util.ConcurrencyUtil;
import com.farsitel.android.qiblacompass.util.ConstantUtil;

public class QiblaCompassManager implements SensorEventListener,
        LocationListener {
    public static final int MESSAGE_ON_DEVICE_HEAD_DIRECTION = 2;
    public static final int MESSAGE_ON_QIBLA_DIRECTION = 3;
    public static final String MESSAGE_NORTH_DIRECTION_ANGEL_KEY = "northDirection";
    public static final String MESSAGE_DEVICE_LOCATION_KEY = "deviceLocation";
    private double qiblaNewAngle;
    private double northNewAngle;
    private boolean isNorthChanged = false;
    private boolean isQiblaChanged = false;
    private double lastQiblaAngle = 0;
    private double lastNorthAngle = 0;
    private QiblaActivity qiblaActivity;

    public QiblaCompassManager(QiblaActivity qiblaActivity) {
        this.qiblaActivity = qiblaActivity;
    }

    // public static final String providerString = LocationManager.GPS_PROVIDER;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            Log.w(ConstantUtil.NAMAZ_QIBLA_LOG_TAG,
                    "New message recieved message:" + message.what);
            int what = message.what;
            switch (what) {
            case MESSAGE_ON_DEVICE_HEAD_DIRECTION:
                ConcurrencyUtil.directionChangedLock.writeLock().lock();
                try {
                    Double deviceDirection = (Double) message.getData().get(
                            MESSAGE_NORTH_DIRECTION_ANGEL_KEY);
                    Log.w(ConstantUtil.NAMAZ_QIBLA_LOG_TAG,
                            "new Device direction to write with lock:"
                                    + deviceDirection);
                    changeNorthDirection(deviceDirection.doubleValue());
                    lastNorthAngle = deviceDirection.doubleValue();
                    isNorthChanged = true;
                    qiblaActivity.signalForAngleChange();

                } finally {
                    ConcurrencyUtil.directionChangedLock.writeLock().unlock();
                }

                break;
            case MESSAGE_ON_QIBLA_DIRECTION:

                Double qiblaDirection = (Double) message.getData().get(
                        MESSAGE_DEVICE_LOCATION_KEY);
                changeQiblaDirection(qiblaDirection.doubleValue());
                lastQiblaAngle = qiblaDirection.doubleValue();
                isQiblaChanged = true;
                qiblaActivity.signalForAngleChange();

                break;
            default:
                Log.i(ConstantUtil.NAMAZ_QIBLA_LOG_TAG,
                        "Unhandled Message for QiblaCompassManager message.what="
                                + what);
            }
        }
    };

    private double changeQiblaDirection(double value) {
        double returnValue = value;
        ConcurrencyUtil.directionChangedLock.writeLock().lock();
        try {
            Log.i(ConstantUtil.NAMAZ_QIBLA_LOG_TAG,
                    "changing qibla Delta degree  value: " + value);
            qiblaNewAngle = value;

            // if(toZero) qiblaNewAngle=0;
        } finally {
            ConcurrencyUtil.directionChangedLock.writeLock().unlock();
        }
        return returnValue;
    }

    private double changeNorthDirection(double value) {
        double returnValue = northNewAngle;
        ConcurrencyUtil.directionChangedLock.writeLock().lock();
        try {
            Log.i(ConstantUtil.NAMAZ_QIBLA_LOG_TAG,
                    "changing north Delta degree value: " + value);
            northNewAngle = value;
            // if(toZero) northNewAngle=0;
        } finally {
            ConcurrencyUtil.directionChangedLock.writeLock().unlock();
        }
        return returnValue;
    }

    public static final String QIBLA_CHANGED_MAP_KEY = "qibla";
    public static final String NORTH_CHANGED_MAP_KEY = "north";

    public Map<String, Double> fetchDeltaAngles() {
        Map<String, Double> returnValue = new HashMap<String, Double>();
        ConcurrencyUtil.directionChangedLock.readLock().lock();
        try {
            if (isNorthChanged)
                returnValue.put(NORTH_CHANGED_MAP_KEY, northNewAngle);
            if (isQiblaChanged)
                returnValue.put(QIBLA_CHANGED_MAP_KEY, qiblaNewAngle);
            Log.i(ConstantUtil.NAMAZ_QIBLA_LOG_TAG,
                    "SomeOne is fetching delta angles ");
            isNorthChanged = false;
            isQiblaChanged = false;
        } finally {
            ConcurrencyUtil.directionChangedLock.readLock().unlock();
        }
        return returnValue;
    }

    // provider for location changing

    // public void onSensorChanged(int sensor, float[] values) {
    // Log.i(ConstantUtil.NAMAZ_QIBLA_LOG_TAG, "sensor is changed  "
    // + values[0] + " " + values[1]);
    // if (sensor == SensorManager.SENSOR_ORIENTATION) {
    // // setNorthDirectionFromDeviceHead(values[0]);
    // Message message = mHandler.obtainMessage();
    // message.what = MESSAGE_ON_DEVICE_HEAD_DIRECTION;
    // Bundle bundle = new Bundle();
    // bundle.putDouble(MESSAGE_NORTH_DIRECTION_ANGEL_KEY, -values[0]);
    // message.setData(bundle);
    // mHandler.sendMessage(message);
    // }
    // }

    public void onAccuracyChanged(int sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    private Location previousLocation = null;

    public void onLocationChanged(Location location) {
        Log.i(ConstantUtil.NAMAZ_QIBLA_LOG_TAG, "Location changed ");
        // //
        // c.setTimeZone(TimeZone.getDefault());
        // int year = c.get(Calendar.YEAR);
        // int month = c.get(Calendar.MONTH);
        // int day = c.get(Calendar.DAY_OF_MONTH);
        // AthanTime athanTime = athanTimeCalculator.getDatePrayerTimes(year,
        // month, day, location.getLatitude(), location.getLongitude(),
        // TimeZone.getDefault());
        // qiblaActivity.onPrayerTimeChangeListener(athanTime);
        if (previousLocation == null || isFarEnough(location, previousLocation)) {
            qiblaActivity.currentLocation = location;
            qiblaActivity.setLocationText();
            new LocationChangedAsyncTask(mHandler).execute(location);
        }

    }

    private static double MIN_DISTANCE_BETWEEN_LOCATIONS = 10000;

    private boolean isFarEnough(Location newLocation, Location prevLocation) {
        double newLatitude = newLocation.getLatitude();
        double newLongitude = newLocation.getLongitude();

        double prevLatitude = prevLocation.getLatitude();
        double prevLongitude = prevLocation.getLongitude();
        double R = 6371000;
        double deltaLat = Math.toRadians(newLatitude - prevLatitude);
        double deltaLong = Math.toRadians(newLongitude - prevLongitude);
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(Math.toRadians(prevLatitude))
                * Math.cos(Math.toRadians(newLatitude))
                * Math.sin(deltaLong / 2) * Math.sin(deltaLong / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        if (d > MIN_DISTANCE_BETWEEN_LOCATIONS) {
            return true;
        } else {
            return false;
        }
    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    private float[] mGData = new float[3];
    private float[] mMData = new float[3];
    private float[] mR = new float[16];
    private float[] mI = new float[16];
    private float[] mOrientation = new float[3];
    private int mCount;
    private Double previousNorth = null;

    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        float[] data;
        if (type == Sensor.TYPE_ACCELEROMETER) {
            data = mGData;
        } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            data = mMData;
        } else {
            // we should not be here.
            return;
        }
        for (int i = 0; i < 3; i++)
            data[i] = event.values[i];

        SensorManager.getRotationMatrix(mR, mI, mGData, mMData);
        // some test code which will be used/cleaned up before we ship this.
        SensorManager.remapCoordinateSystem(mR, SensorManager.AXIS_X,
                SensorManager.AXIS_Z, mR);
        SensorManager.remapCoordinateSystem(mR, SensorManager.AXIS_Y,
                SensorManager.AXIS_MINUS_X, mR);
        SensorManager.getOrientation(mR, mOrientation);
        float incl = SensorManager.getInclination(mI);

        if (mCount++ > 50) {
            final float rad2deg = (float) (180.0f / Math.PI);
            mCount = 0;
            Log.d("Compass", "yaw: " + (int) (mOrientation[0] * rad2deg)
                    + "  pitch: " + (int) (mOrientation[1] * rad2deg)
                    + "  roll: " + (int) (mOrientation[2] * rad2deg)
                    + "  incl: " + (int) (incl * rad2deg));
            double newDegree = mOrientation[0] * rad2deg -90d ;
            if (previousNorth == null
                    || isDeltaDegreeEnough(new Double(newDegree), previousNorth)) {
                Message message = mHandler.obtainMessage();
                message.what = MESSAGE_ON_DEVICE_HEAD_DIRECTION;
                Bundle bundle = new Bundle();
                bundle.putDouble(MESSAGE_NORTH_DIRECTION_ANGEL_KEY,
                        -(newDegree));
                message.setData(bundle);
                mHandler.sendMessage(message);
                previousNorth = newDegree;
            }

        }

        // TODO Auto-generated method stub

    }

    private final double MIN_DEGREE_FROM_NORTH = 3;

    private boolean isDeltaDegreeEnough(Double newDegree, Double previousDegree) {
        if (Math.abs(newDegree - previousDegree) > MIN_DEGREE_FROM_NORTH) {
            return true;
        } else {
            return false;
        }
    }

    public static int a = 0;
}

class LocationChangedAsyncTask extends AsyncTask<Location, Void, Double> {
    private Handler mHandler;

    public LocationChangedAsyncTask(Handler handler) {
        super();
        this.mHandler = handler;
    }

    @Override
    protected Double doInBackground(Location... params) {
        assert (params[0] != null);
        double northDirection = QiblaDirectionCalculator
                .getQiblaDirectionFromNorth(params[0].getLatitude(),
                        params[0].getLongitude());
        return new Double(northDirection);
    }

    @Override
    protected void onPostExecute(Double result) {
        // TODO Auto-generated method stub
        Message message = mHandler.obtainMessage();
        message.what = QiblaCompassManager.MESSAGE_ON_QIBLA_DIRECTION;
        Bundle bundle = new Bundle();
        bundle.putDouble(QiblaCompassManager.MESSAGE_DEVICE_LOCATION_KEY,
                result.doubleValue());
        // bundle.putDouble(QiblaCompassManager.MESSAGE_DEVICE_LOCATION_KEY,
        // 83d);
        message.setData(bundle);
        this.mHandler.sendMessage(message);
    }

}
