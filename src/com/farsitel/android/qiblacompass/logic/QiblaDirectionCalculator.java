package com.farsitel.android.qiblacompass.logic;

public class QiblaDirectionCalculator {
    private static final double QIBLA_LATITUDE = Math.toRadians(21.423333);
    private static final double QIBLA_LONGITUDE = Math.toRadians(39.823333);

    public static double getQiblaDirectionFromNorth(double degLatitude, double degLongitude){
        double latitude = Math.toRadians(degLatitude);
        double longitude = Math.toRadians(degLongitude);
        
        double soorat = Math.sin(QIBLA_LONGITUDE - longitude);
        double makhraj = Math.cos(latitude)*Math.tan(QIBLA_LATITUDE) - Math.sin(latitude)*Math.cos(QIBLA_LONGITUDE - longitude);
        double returnValue = Math.toDegrees(Math.atan(soorat / makhraj));
        //Math.atan will return value between -90...90 but arc tan of +180 degree plus is also the same( if longitude is greater than qibla we must add +180)
        if (longitude > QIBLA_LONGITUDE)
            returnValue += 180;
        // at last we put +19 degree to returnValue. because actual north is +19 from magnetic north. and here we had ma
        return returnValue;
    }

}
