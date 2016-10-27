package co.runster.runster;

import android.location.Location;

/**
 * Created by christiannageby on 2016-10-27.
 */

public class Haversin {
    //Earths radius in meters
    private static final int EARTH_RADIUS = 6371;

    public static double distance(Location Loc2, Location Loc) {

        double deltaLat  = Math.toRadians((Loc.getLatitude() - Loc2.getLatitude()));
        double deltaLong = Math.toRadians((Loc.getLongitude() - Loc2.getLongitude()));


        //convert constructor args to radians
        double Lat2 = Math.toRadians(Loc2.getLatitude());
        double Lat = Math.toRadians(Loc.getLatitude());

        //The Haversin eqality
        //a = Sin(∆lat/2)^2 + Cos(lat2) * Cos(Lat) * Sin(∆Long)^2
        double a = haversin(deltaLat) + Math.cos(Lat2) * Math.cos(Lat) * haversin(deltaLong);

        //c = Tan^-1(√(a) - √(1-a)/1-√(a)*√(1-a));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        //distance between the points
        return EARTH_RADIUS*1000 * c;
    }

    public static double haversin(double val) {
        //Sin(val/2)^2
        return Math.pow(Math.sin(val / 2), 2);
    }
}