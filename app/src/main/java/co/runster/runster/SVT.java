package co.runster.runster;

/**
 * Created by Christian Nageby on 2016-10-27.
 */
public class SVT{
    //the velocity in m/s and time in secods and distance in meters
    public static double s(double v,double t){return (v*t);}
    public static double v(double s, double t){return (s/t);}
    public static double t(double s, double v){return (s/v);}
}
