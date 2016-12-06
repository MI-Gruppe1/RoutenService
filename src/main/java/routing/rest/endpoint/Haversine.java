package routing.rest.endpoint;

/**
 * Created by FBeck on 06.12.2016.
 */
public class Haversine {
    public static final double R = 6372.8;//Erdradius in km

    public static double haversine(double latStation, double lngStation, double latWaypoint, double lngWaypoint){
        double dLat = Math.toRadians(latWaypoint - latStation);
        double dLon = Math.toRadians(lngWaypoint - lngStation);
        latStation = Math.toRadians(latStation);
        latWaypoint = Math.toRadians(latWaypoint);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(latStation) * Math.cos(latWaypoint);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
}
