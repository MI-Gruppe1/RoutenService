package routing.rest.endpoint;

import routing.rest.call.google.classes.Location;
import routing.rest.call.services.classes.Station;

import java.util.Comparator;

/**
 * Created by Rutkay on 29.11.16.
 */
public class StationTupelComparator implements Comparator<StationTupel> {
    public static final double R = 6372.8;//Erdradius in km

    private Location start;
    private Location destination;
    private int walkingWeight;
    private int bikingWeight;

    public StationTupelComparator(Location start, Location destination, int walkingWeight, int bikingWeight) {
        this.start = start;
        this.destination = destination;
        this.walkingWeight = walkingWeight;
        this.bikingWeight = bikingWeight;
    }

    @Override
    public int compare(StationTupel stationTupel1, StationTupel stationTupel2) {
        double distance1 = walkingWeight * haversine(start.getLat(), start.getLng(), stationTupel1.getStationStart().getLatitude(),stationTupel1.getStationStart().getLongitude());
        distance1 += bikingWeight * haversine(stationTupel1.getStationStart().getLatitude(),stationTupel1.getStationStart().getLongitude(),stationTupel1.getStationDestination().getLatitude(),stationTupel1.getStationDestination().getLongitude());
        distance1 += walkingWeight * haversine(stationTupel1.getStationDestination().getLatitude(),stationTupel1.getStationDestination().getLongitude(),destination.getLat(), destination.getLng());

        double distance2 = walkingWeight * haversine(start.getLat(), start.getLng(), stationTupel2.getStationStart().getLatitude(),stationTupel2.getStationStart().getLongitude());
        distance2 += bikingWeight * haversine(stationTupel2.getStationStart().getLatitude(),stationTupel2.getStationStart().getLongitude(),stationTupel2.getStationDestination().getLatitude(),stationTupel2.getStationDestination().getLongitude());
        distance2 += walkingWeight * haversine(stationTupel2.getStationDestination().getLatitude(),stationTupel2.getStationDestination().getLongitude(),destination.getLat(), destination.getLng());
        //return a.age < b.age ? -1 : a.age == b.age ? 0 : 1;
        return distance1 < distance2 ? -1 : distance1 == distance2 ? 0 : 1;
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
