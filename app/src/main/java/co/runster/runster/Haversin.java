package co.runster.runster;
/**
 * Created by christiannageby on 2016-10-27.
 */

public class Haversin {
    //Earths radius in meters
    private static final int EARTH_RADIUS = 6371;

    public static double distance(double Lat2, double Long2, double Lat, double Long) {

        double deltaLat  = Math.toRadians((Lat - Lat2));
        double deltaLong = Math.toRadians((Long - Long2));


        //convert constructor args to radians
        Lat2 = Math.toRadians(Lat2);
        Lat = Math.toRadians(Lat);

        //The Haversin eqality
        //a = Sin(∆lat/2)^2 + Cos(lat2) * Cos(Lat) * Sin(∆Long)^2
        double a = haversin(deltaLat) + Math.cos(Lat2) * Math.cos(Lat) * haversin(deltaLong);

        //c = Tan^-1(√(a) - √(1-a)/1-√(a)*√(1-a));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        //distance between the points
        return EARTH_RADIUS * c;
    }

    public static double haversin(double val) {
        //Sin(val/2)^2
        return Math.pow(Math.sin(val / 2), 2);
    }
}