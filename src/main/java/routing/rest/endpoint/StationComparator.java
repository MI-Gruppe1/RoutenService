package routing.rest.endpoint;

import routing.rest.call.services.classes.Station;

import java.util.Comparator;

/**
 * Created by FBeck on 19.11.2016.
 */
public class StationComparator implements Comparator<Station> {
    public static final double R = 6372.8;//Erdradius in km

    private double latWaypoint;
    private double lngWaypoint;

    public StationComparator(double latWaypoint, double lngWaypoint) {
        this.latWaypoint = latWaypoint;
        this.lngWaypoint = lngWaypoint;
    }

    @Override
    public int compare(Station o1, Station o2) {
        double distanceO1 = haversine(o1.getLatitude(),o1.getLongitude(),latWaypoint,lngWaypoint);
        double distanceO2 = haversine(o2.getLatitude(),o2.getLongitude(),latWaypoint,lngWaypoint);
        //return a.age < b.age ? -1 : a.age == b.age ? 0 : 1;
        return distanceO1 < distanceO2 ? -1 : distanceO1 == distanceO2 ? 0 : 1;
    }

    /*
     * Berechnet die Entfernung zwischen zwei Punkten mit Lat/Long
     */
    private double haversine(double latStation, double lngStation, double latWaypoint, double lngWaypoint){
        double dLat = Math.toRadians(latWaypoint - latStation);
        double dLon = Math.toRadians(lngWaypoint - lngStation);
        latStation = Math.toRadians(latStation);
        latWaypoint = Math.toRadians(latWaypoint);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(latStation) * Math.cos(latWaypoint);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
}
