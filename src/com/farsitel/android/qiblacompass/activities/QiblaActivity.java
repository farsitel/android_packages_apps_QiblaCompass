package com.farsitel.android.qiblacompass.activities;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.format.Jalali;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.farsitel.android.qiblacompass.R;
import com.farsitel.android.qiblacompass.logic.QiblaCompassManager;
import com.farsitel.android.qiblacompass.util.ConcurrencyUtil;
import com.farsitel.android.qiblacompass.util.ConstantUtil;

public class QiblaActivity extends Activity implements AnimationListener,
        OnSharedPreferenceChangeListener {
    public Location currentLocation = null;
    private double lastQiblaAngle = 0;
    private double lastNorthAngle = 0;
    private static final int QIBLA_COMPASS_TAB_INDEX = 0;
    private static final int OGHAT_TAB_INDEX = 1;

    private RotateAnimation animation;
    // Informing Location manager how many meters or seconds to wait for
    // updating device location

    private ImageView compassImageView;
    private ImageView qiblaImageView;
    private QiblaCompassManager qiblaManager = new QiblaCompassManager(this);

    private TabHost mTabHost;
    private boolean angleSignaled = false;
    private Timer timer = null;
    private static final int ROTATE_IMAGES_MESSAGE = 1;
    private static final String QIBLA_BUNDLE_DELTA_KEY = "qiblaDelta";
    private static final String COMPASS_BUNDLE_DELTA_KEY = "compassDelta";
    private static final String IS_QIBLA_CHANGED = "qibla";
    private static final String IS_COMPASS_CHANGED = "compass";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == ROTATE_IMAGES_MESSAGE) {
                Bundle bundle = message.getData();
                boolean isQiblaChanged = bundle.getBoolean(IS_QIBLA_CHANGED);
                boolean isCompassChanged = bundle
                        .getBoolean(IS_COMPASS_CHANGED);
                double qiblaNewAngle = 0;
                double compassNewAngle = 0;
                if (isQiblaChanged)
                    qiblaNewAngle = (Double) bundle.get(QIBLA_BUNDLE_DELTA_KEY);
                if (isCompassChanged) {
                    compassNewAngle = (Double) bundle
                            .get(COMPASS_BUNDLE_DELTA_KEY);
                    // isQiblaChanged = true;
                    // qiblaNewAngle = lastQiblaAngle;
                }

                syncQiblaAndNorthArrow(compassNewAngle, qiblaNewAngle,
                        isCompassChanged, isQiblaChanged);
                angleSignaled = false;
            }
        }

    };
    public void setLocationText(){
        TextView textView = (TextView)findViewById(R.id.location_text);
        if(currentLocation == null){
            textView.setText(getString(R.string.current_location_not_set));
        }else{
            textView.setText(getString(R.string.current_location) + " " + getLocationForPrint(currentLocation.getLatitude(), true) +" "+ getString(R.string.and)+ " " + getLocationForPrint(currentLocation.getLongitude(), false) );
        }
    }
    
    private String getLocationForPrint(double value, boolean isLatitude) {
        int degree = (new Double(Math.floor(value))).intValue();
        String end = getString(((isLatitude) ? R.string.latitude_south
                : R.string.longitude_west));
        if (degree > 0) {
            end = getString(((isLatitude) ? R.string.latitude_north
                    : R.string.longitude_east));
        }
        double second = (value - degree) * 100;
        double minDouble = (second * 3d / 5d);
        int minute = new Double(Math.floor(minDouble)).intValue();
        return String.format(" %Ld"+ '\u00B0' + " %Ld "+ '\u00B4' + "" + end,degree,minute);
//        return ""+degree+"\u00B0 " + minute + "\u00B4 " + end;

    }
    
    private TimerTask getTimerTask() {
        TimerTask timerTask = new TimerTask() {
            public void run() {

                if (angleSignaled && !ConcurrencyUtil.isAnyAnimationOnRun()) {
                    Log.i(ConstantUtil.NAMAZ_LOG_TAG,
                            "running new backgroundTask tor run every 1sec isSignaled: "
                                    + angleSignaled + " numOfAnimation: "
                                    + ConcurrencyUtil.getNumAimationsOnRun());
                    // numAnimationOnRun += 2;
                    Map<String, Double> newAnglesMap = qiblaManager
                            .fetchDeltaAngles();
                    Double newNorthAngle = newAnglesMap
                            .get(QiblaCompassManager.NORTH_CHANGED_MAP_KEY);
                    Double newQiblaAngle = newAnglesMap
                            .get(QiblaCompassManager.QIBLA_CHANGED_MAP_KEY);
                    Log.i(ConstantUtil.NAMAZ_LOG_TAG,
                            "TimerTask found new signal for rotation and no Animation is running. rotating: comapssDelta ");
                    Message message = mHandler.obtainMessage();
                    message.what = ROTATE_IMAGES_MESSAGE;
                    Bundle b = new Bundle();
                    if (newNorthAngle == null) {
                        b.putBoolean(IS_COMPASS_CHANGED, false);
                    } else {
                        ConcurrencyUtil.incrementAnimation();
                        b.putBoolean(IS_COMPASS_CHANGED, true);

                        b.putDouble(COMPASS_BUNDLE_DELTA_KEY, newNorthAngle);
                    }
                    if (newQiblaAngle == null) {
                        b.putBoolean(IS_QIBLA_CHANGED, false);

                    } else {
                        ConcurrencyUtil.incrementAnimation();
                        b.putBoolean(IS_QIBLA_CHANGED, true);
                        b.putDouble(QIBLA_BUNDLE_DELTA_KEY, newQiblaAngle);
                    }

                    message.setData(b);
                    mHandler.sendMessage(message);
                } else if (ConcurrencyUtil.getNumAimationsOnRun() < 0) {
                    Log.i(ConstantUtil.NAMAZ_LOG_TAG,
                            " Number of animations are negetive numOfAnimation: "
                                    + ConcurrencyUtil.getNumAimationsOnRun());
                }
            }
        };
        return timerTask;
    }

    private void schedule() {
        Log.i(ConstantUtil.NAMAZ_LOG_TAG,
                "Scheduling a timerTask for every 1sec");
        if (timer == null) {
            timer = new Timer();
            this.timer.schedule(getTimerTask(), 0, 200);
        } else {
            timer.cancel();
            timer = new Timer();
            timer.schedule(getTimerTask(), 0, 200);
        }
    }

    private void cancelSchedule() {
        Log.i(ConstantUtil.NAMAZ_LOG_TAG, "Canceling a schedule of timerTask");
        if (timer == null)
            return;
        timer.cancel();
    }

    private SharedPreferences perfs;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(ConstantUtil.NAMAZ_LOG_TAG,
                "creating Namaz main Activity (onCreate started)");
        Log.i("Chert", "Salam gumbuli jun!!!!!!!!!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        /**
         * Creating a tab container on the screen for users to choose different
         * functionality. Adding three tab for three functionality of program 1.
         * Showing Qibla Compass 2. Showing Prayer times of day 3. Showing
         * Islamic activities of the day
         **/
        Context context = getApplicationContext();
        perfs = PreferenceManager.getDefaultSharedPreferences(context);
        perfs.registerOnSharedPreferenceChangeListener(this);
        this.qiblaImageView = (ImageView) findViewById(R.id.arrowImage);
        this.compassImageView = (ImageView) findViewById(R.id.compassImage);
        registerListeners();
        Log.i(ConstantUtil.NAMAZ_LOG_TAG,
                "Namaz activity created with tab view, Qibla tab with index"
                        + QIBLA_COMPASS_TAB_INDEX + " oghat tab with index: "
                        + OGHAT_TAB_INDEX);
    }

    @Override
    protected void onStart() {
        Log.i(ConstantUtil.NAMAZ_LOG_TAG, "Namaz activity onStart");
        super.onStart();

    }

    public static final int MENU_ARAK = 1;
    public static final int MENU_ARDABIL = 2;
    public static final int MENU_ORUMIYEH = 3;
    public static final int MENU_ESFEHAN = 4;
    public static final int MENU_AHVAZ = 5;
    public static final int MENU_ILAM = 6;
    public static final int MENU_BOJNURD = 7;
    public static final int MENU_BANDAR_ABAS = 8;
    public static final int MENU_BUSHEHR = 9;
    public static final int MENU_BIRJAND = 10;
    public static final int MENU_TABRIZ = 11;
    public static final int MENU_TEHRAN = 12;
    public static final int MENU_KHORAM_ABAD = 13;
    public static final int MENU_RASHT = 14;
    public static final int MENU_ZAHEDAN = 15;
    public static final int MENU_ZANJAN = 16;
    public static final int MENU_SARI = 17;
    public static final int MENU_SEMNAN = 18;
    public static final int MENU_SANANDAJ = 19;
    public static final int MENU_SHAHREKORD = 20;
    public static final int MENU_SHIRAZ = 21;
    public static final int MENU_GHAZVIN = 22;
    public static final int MENU_GHOM = 23;
    public static final int MENU_KARAJ = 24;
    public static final int MENU_KERMAN = 25;
    public static final int MENU_KERMANSHAH = 26;
    public static final int MENU_GORGAN = 27;
    public static final int MENU_MASHHAD = 28;
    public static final int MENU_HAMEDAN = 29;
    public static final int MENU_YASUJ = 30;
    public static final int MENU_YAZD = 31;
    public static final Map<Integer, Integer> STATE_VALUE_KEY = new HashMap<Integer, Integer>();
    public static final int MENU_HELP = 35;
    public static final int MENU_PREFS = 34;
    public static final int MENU_ABOUT = 33;

    public boolean isRegistered = false;
    public boolean isGPSRegistered = false;

    private void unregisterListeners(boolean justGPS) {
        if (isRegistered) {
            if (justGPS) {
                ((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                .removeUpdates(qiblaManager);
                isGPSRegistered= false;
            } else {
                Log.i(ConstantUtil.NAMAZ_LOG_TAG,
                        "Unregistering of location service, sensor Service and canceling schedules");
                ((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                        .removeUpdates(qiblaManager);
                SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                Sensor gsensor = mSensorManager
                        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                Sensor msensor = mSensorManager
                        .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                mSensorManager.unregisterListener(qiblaManager, gsensor);
                mSensorManager.unregisterListener(qiblaManager, msensor);
                cancelSchedule();
                isRegistered = false;
            }
        }
    }

    private void registerListeners() {
        if (!isRegistered) {
            Log.i(ConstantUtil.NAMAZ_LOG_TAG,
                    "Registering of location service, sensor Service and scheduling");
            SharedPreferences perfs = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            if (perfs.getBoolean(getString(R.string.gps_pref_key), false)) {
                ((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                        .requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                ConstantUtil.MIN_LOCATION_TIME,
                                ConstantUtil.MIN_LOCATION_DISTANCE,
                                qiblaManager);
                isGPSRegistered= true;
            } else {
                int stateID = Integer.parseInt(perfs.getString(
                        getString(R.string.state_location_pref_key), ""
                                + MENU_TEHRAN));
            }
            SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            Sensor gsensor = mSensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor msensor = mSensorManager
                    .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            mSensorManager.registerListener(qiblaManager, gsensor,
                    SensorManager.SENSOR_DELAY_GAME);
            mSensorManager.registerListener(qiblaManager, msensor,
                    SensorManager.SENSOR_DELAY_GAME);
            schedule();
            isRegistered = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // numAnimationOnRun = 0;
        Log.i(ConstantUtil.NAMAZ_LOG_TAG, "Namaz activity onResume");
        perfs = PreferenceManager.getDefaultSharedPreferences(this);
        perfs.registerOnSharedPreferenceChangeListener(this);
        setLocationText();
        registerListeners(); 
        
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(ConstantUtil.NAMAZ_LOG_TAG, "Namaz activity onStop");
        unregisterListeners(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        int aboutGroupID = 0;
        int helpGroupID = 1;
        int prefsGroupID = 2;
        int aboutItemOrder = Menu.FIRST;
        int helpItemOrder = 2;
        int prefsItemOrder = 3;
        MenuItem aboutMenuItem = menu.add(aboutGroupID, MENU_ABOUT,
                aboutItemOrder, R.string.about_menu_title);
//        MenuItem helpMenuItem = menu.add(helpGroupID, MENU_HELP, helpItemOrder,
//                R.string.help_menu_title);
        MenuItem prefsMenuItem = menu.add(prefsGroupID, MENU_PREFS,
                prefsItemOrder, R.string.prefs_menu_title);
        return true;
    }

   
    private double lastQiblaAngleFromN = 0;
    public void syncQiblaAndNorthArrow(double northNewAngle,
            double qiblaNewAngle, boolean northChanged, boolean qiblaChanged) {
        if (northChanged) {
            lastNorthAngle = rotateImageView(northNewAngle, lastNorthAngle,
                    compassImageView);
            if(qiblaChanged== false && qiblaNewAngle != 0){
                lastQiblaAngleFromN = qiblaNewAngle;
                lastQiblaAngle = rotateImageView(qiblaNewAngle + northNewAngle,
                    lastQiblaAngle, qiblaImageView);
            }else if(qiblaChanged == false && qiblaNewAngle ==0)
                
                lastQiblaAngle = rotateImageView(lastQiblaAngleFromN + northNewAngle,
                        lastQiblaAngle, qiblaImageView);
                
        }
        if (qiblaChanged) {
            lastQiblaAngleFromN = qiblaNewAngle;
            lastQiblaAngle = rotateImageView(qiblaNewAngle + lastNorthAngle,
                    lastQiblaAngle, qiblaImageView);
            
               
        }
    }

    private double rotateImageView(double newAngle, double fromDegree,
            ImageView imageView) {
        newAngle = newAngle % 360;
        double rotationDegree = fromDegree - newAngle;
        rotationDegree = rotationDegree % 360;
        long duration = new Double(Math.abs(rotationDegree) * 2000 / 360)
                .longValue();
        if (rotationDegree > 180)
            rotationDegree -= 360;
        float toDegree = new Double(newAngle % 360).floatValue();
        final int width = Math.abs(imageView.getRight() - imageView.getLeft());
        final int height = Math.abs(imageView.getBottom() - imageView.getTop());

        LinearLayout main = (LinearLayout) findViewById(R.id.mainLayout);
        float pivotX = Math.abs(main.getRight() - imageView.getRight())
                + imageView.getLeft() + width / 2f;
        float pivotY = height / 2f;
        Log.i("NAMAZ_ROTATION",
                "imageView: right" + imageView.getRight() + " imageView.left: "
                        + imageView.getLeft() + " imageView.top"
                        + imageView.getTop() + " imageview.bottom: "
                        + imageView.getBottom() + " Display width: "
                        + main.getRight() + " Display height: "
                        + ((FrameLayout) imageView.getParent()).getPaddingTop()
                        + " pivotX: " + pivotX + " pivotY: " + pivotY + " ");
        animation = new RotateAnimation(new Double(fromDegree).floatValue(),
                toDegree, pivotX, pivotY);
        animation.setRepeatCount(0);
        animation.setDuration(duration);
        animation.setInterpolator(new LinearInterpolator());
        animation.setFillEnabled(true);
        animation.setFillAfter(true);
        animation.setAnimationListener(this);
        Log.i(ConstantUtil.NAMAZ_LOG_TAG, "rotating image from degree:"
                + fromDegree + " degree to rotate: " + rotationDegree
                + " ImageView: " + imageView.getId());
        imageView.startAnimation(animation);
        return toDegree;
    }

    int count = 0;

    public void signalForAngleChange() {
        this.angleSignaled = true;
    }

    public void onAnimationEnd(Animation animation) {
        Log.i(ConstantUtil.NAMAZ_LOG_TAG, "Animation ended");
        if (ConcurrencyUtil.getNumAimationsOnRun() <= 0) {
            Log.d(ConstantUtil.NAMAZ_LOG_TAG,
                    "An animation ended but no animation was on run!!!!!!!!!");
        } else {
            ConcurrencyUtil.decrementAnimation();
        }
        schedule();
    }

    public void onAnimationRepeat(Animation animation) {
        Log.d(ConstantUtil.NAMAZ_LOG_TAG, "An animation is repeating ");
    }

    public void onAnimationStart(Animation animation) {
        Log.i(ConstantUtil.NAMAZ_LOG_TAG, "Animation started");
        // numAnimationOnRun++;
        cancelSchedule();

    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        // TODO Auto-generated method stub
        String gpsPerfKey = getString(R.string.gps_pref_key);
        String defaultLocationPerfKey = getString(R.string.state_location_pref_key);
        if (gpsPerfKey.equals(key)) {
            boolean isGPS =false;
            try{
                isGPS = Boolean.parseBoolean(sharedPreferences.getString(
                    key, "false"));
            }catch(ClassCastException e){
                isGPS = sharedPreferences.getBoolean(key, false);
            }
            if (isGPS) {
                registerListeners();
            } else {
                unregisterListeners(true);
                useDefaultLocation(sharedPreferences, defaultLocationPerfKey);
            }
        } else if (defaultLocationPerfKey.equals(key)) {
            sharedPreferences.edit().putString(gpsPerfKey, "false");
            sharedPreferences.edit().commit();
            unregisterListeners(true);
            useDefaultLocation(sharedPreferences, key);
        } else {
            Log.w(ConstantUtil.NAMAZ_LOG_TAG, "preference with key:" + key
                    + " is changed and it is not handled properly");
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
        // Check for each known menu item
        case (MENU_PREFS):
            Intent i = new Intent(this, UserPreferenceActivity.class);
            startActivityForResult(i, 1);
            return true;
        case (MENU_ABOUT):
            Intent i2 = new Intent(this, AboutActivity.class);
            startActivityForResult(i2, 2);
            return true;
//        case (MENU_HELP):
//            Intent i3 = new Intent(this, HelpActivity.class);
//            startActivityForResult(i3, 3);
//            return true;
        default:
            return false;
        }
        // return false;
    }
    private void useDefaultLocation(SharedPreferences perfs, String key) {
        int defLocationID = Integer.parseInt(perfs.getString(key, ""
                + MENU_TEHRAN));
        // Log.w(ATHAN_TIME_LOG,"Guz: " + defLocationID.getClass().getName());
        Location location = getLocationByID(defLocationID);
        qiblaManager.onLocationChanged(location);
    }

    private Location getLocationByID(int locationID) {
        Location returnLocation = new Location("GPS");
        switch (locationID) {
        case MENU_TABRIZ:
            returnLocation.setLatitude(38.08d);
            returnLocation.setLongitude(46.3);

            break;
        case MENU_ORUMIYEH:
            returnLocation.setLatitude(37.53);
            returnLocation.setLongitude(45);
            break;
        case MENU_ARDABIL:
            returnLocation.setLatitude(38.25);
            returnLocation.setLongitude(48.28);
            break;
        case MENU_ESFEHAN:
            returnLocation.setLatitude(32.65);
            returnLocation.setLongitude(51.67);
            break;
        case MENU_KARAJ:
            returnLocation.setLatitude(35.82);
            returnLocation.setLongitude(50.97);
            break;
        case MENU_ILAM:
            returnLocation.setLatitude(33.63);
            returnLocation.setLongitude(46.42);
            break;
        case MENU_BUSHEHR:
            returnLocation.setLatitude(28.96);
            returnLocation.setLongitude(50.84);
            break;
        default:
        case MENU_TEHRAN:
            returnLocation.setLatitude(35.68);
            returnLocation.setLongitude(51.42);
            break;
        case MENU_SHAHREKORD:
            returnLocation.setLatitude(32.32);
            returnLocation.setLongitude(50.85);
            break;
        case MENU_BIRJAND:
            returnLocation.setLatitude(32.88);
            returnLocation.setLongitude(59.22);
            break;
        case MENU_MASHHAD:
            returnLocation.setLatitude(34.3);
            returnLocation.setLongitude(59.57);
            break;
        case MENU_BOJNURD:
            returnLocation.setLatitude(37.47);
            returnLocation.setLongitude(57.33);
            break;
        case MENU_AHVAZ:
            returnLocation.setLatitude(31.52);
            returnLocation.setLongitude(48.68);
            break;
        case MENU_ZANJAN:
            returnLocation.setLatitude(36.67);
            returnLocation.setLongitude(48.48);
            break;
        case MENU_SEMNAN:
            returnLocation.setLatitude(35.57);
            returnLocation.setLongitude(53.38);
            break;
        case MENU_ZAHEDAN:
            returnLocation.setLatitude(29.5);
            returnLocation.setLongitude(60.85);
            break;
        case MENU_SHIRAZ:
            returnLocation.setLatitude(29.62);
            returnLocation.setLongitude(52.53);
            break;
        case MENU_GHAZVIN:
            returnLocation.setLatitude(36.45);
            returnLocation.setLongitude(50);
            break;
        case MENU_GHOM:
            returnLocation.setLatitude(34.65);
            returnLocation.setLongitude(50.95);
            break;
        case MENU_SANANDAJ:
            returnLocation.setLatitude(35.3);
            returnLocation.setLongitude(47.02);
            break;
        case MENU_KERMAN:
            returnLocation.setLatitude(30.28);
            returnLocation.setLongitude(57.06);
            break;
        case MENU_KERMANSHAH:
            returnLocation.setLatitude(34.32);
            returnLocation.setLongitude(47.06);
            break;
        case MENU_YASUJ:
            returnLocation.setLatitude(30.82);
            returnLocation.setLongitude(51.68);
            break;
        case MENU_GORGAN:
            returnLocation.setLatitude(36.83);
            returnLocation.setLongitude(54.48);
            break;
        case MENU_RASHT:
            returnLocation.setLatitude(37.3);
            returnLocation.setLongitude(49.63);
            break;
        case MENU_KHORAM_ABAD:
            returnLocation.setLatitude(33.48);
            returnLocation.setLongitude(48.35);
            break;
        case MENU_SARI:
            returnLocation.setLatitude(36.55);
            returnLocation.setLongitude(53.1);
            break;
        case MENU_ARAK:
            returnLocation.setLatitude(34.08);
            returnLocation.setLongitude(49.7);
            break;
        case MENU_BANDAR_ABAS:
            returnLocation.setLatitude(27.18);
            returnLocation.setLongitude(56.27);
            break;
        case MENU_HAMEDAN:
            returnLocation.setLatitude(34.77);
            returnLocation.setLongitude(48.58);
            break;
        case MENU_YAZD:
            returnLocation.setLatitude(31.90);
            returnLocation.setLongitude(54.37);
            break;

        }
        return returnLocation;
    }
}