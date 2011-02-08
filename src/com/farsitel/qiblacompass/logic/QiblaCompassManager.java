package com.farsitel.qiblacompass.logic;

/*
 * This class is responsible for gathering information about north and qibla angles and deliver it to QiblaActivity.
 * Note that this class is not responsible for any synchronization of animation because of deadlock issues.
 * 
 * Written by: Majid Kalkatehchi
 * Email: majid@farsitel.com
 */
import java.util.HashMap;
import java.util.Map;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.farsitel.qiblacompass.activities.QiblaActivity;
import com.farsitel.qiblacompass.util.ConcurrencyUtil;
import com.farsitel.qiblacompass.util.ConstantUtilInterface;

public class QiblaCompassManager implements SensorEventListener,
        LocationListener, ConstantUtilInterface {

    private double qiblaNewAngle;
    private double northNewAngle;
    private boolean isNorthChanged = false;
    private boolean isQiblaChanged = false;
    private double lastQiblaAngle = 0;
    private double lastNorthAngle = 0;
    private final QiblaActivity qiblaActivity;

    // These variables are used to recognize north direction. please refer to
    // android Sensor documentation
    private final float[] mGData = new float[3];
    private final float[] mMData = new float[3];
    private final float[] mR = new float[16];
    private final float[] mI = new float[16];
    private final float[] mOrientation = new float[3];
    private int mCount;
    // End

    // This variable is used for performance issues.
    private Double previousNorth = null;

    public QiblaCompassManager(QiblaActivity qiblaActivity) {
        this.qiblaActivity = qiblaActivity;
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            int what = message.what;
            switch (what) {
            case MESSAGE_ON_DEVICE_HEAD_DIRECTION:
                // ConcurrencyUtil.directionChangedLock.writeLock().lock();
                try {
                    Double deviceDirection = (Double) message.getData().get(
                            MESSAGE_NORTH_DIRECTION_ANGEL_KEY);

                    changeNorthDirection(deviceDirection.doubleValue());
                    lastNorthAngle = deviceDirection.doubleValue();
                    isNorthChanged = true;
                    qiblaActivity.signalForAngleChange();

                } finally {
                    // ConcurrencyUtil.directionChangedLock.writeLock().unlock();
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
                Log.d(NAMAZ_QIBLA_LOG_TAG,
                        "Unhandled Message for QiblaCompassManager message.what="
                                + what);
            }
        }
    };

    private double changeQiblaDirection(double value) {
        double returnValue = value;
        ConcurrencyUtil.directionChangedLock.writeLock().lock();
        try {

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

            northNewAngle = value;
            // if(toZero) northNewAngle=0;
        } finally {
            ConcurrencyUtil.directionChangedLock.writeLock().unlock();
        }
        return returnValue;
    }

    public Map<String, Double> fetchDeltaAngles() {
        Map<String, Double> returnValue = new HashMap<String, Double>();
        ConcurrencyUtil.directionChangedLock.readLock().lock();
        try {
            if (isNorthChanged)
                returnValue.put(NORTH_CHANGED_MAP_KEY, northNewAngle);
            if (isQiblaChanged)
                returnValue.put(QIBLA_CHANGED_MAP_KEY, qiblaNewAngle);

            isNorthChanged = false;
            isQiblaChanged = false;
        } finally {
            ConcurrencyUtil.directionChangedLock.readLock().unlock();
        }
        return returnValue;
    }

    public void onAccuracyChanged(int sensor, int accuracy) {
    }

    public void onLocationChanged(Location location) {

        if (previousLocation == null || isFarEnough(location, previousLocation)) {
            qiblaActivity.onNewLocationFromGPS(location);
            new LocationChangedAsyncTask(mHandler).execute(location);
        }

    }

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
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

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
            double yaw = mOrientation[0] * rad2deg * 1d;
            double pitch = mOrientation[1] * rad2deg * 1d;
            double roll = mOrientation[2] * rad2deg * 1d;
            double incl2 = incl * rad2deg * 1d;

            // double pitch = mOrientation[1] * rad2deg;
            // if (pitch > 75) { // downside
            // qiblaActivity.onScreenDown();
            // } else if (pitch < 30) { // upside
            // qiblaActivity.onScreenUp();
            // }
            if ((pitch < -45 || pitch > 45) || (roll > -45 || roll < -135)) {
                qiblaActivity.onScreenDown();
            } else {
                qiblaActivity.onScreenUp();
            }
            double newDegree = yaw - 90d;
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

    private boolean isDeltaDegreeEnough(Double newDegree, Double previousDegree) {
        double delta = (Math.abs(newDegree - previousDegree) % 360);
        if (delta > MIN_DEGREE_FROM_NORTH
                && delta < (360 - MIN_DEGREE_FROM_NORTH)) {
            return true;
        } else {
            return false;
        }
    }

    public static int a = 0;
}

class LocationChangedAsyncTask extends AsyncTask<Location, Void, Double> {
    private final Handler mHandler;

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
        message.setData(bundle);
        this.mHandler.sendMessage(message);
    }

}
