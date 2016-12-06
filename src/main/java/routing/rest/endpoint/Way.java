package routing.rest.endpoint;

import routing.rest.call.google.classes.Location;
import routing.rest.call.services.classes.Station;

/**
 * Created by Rutkay on 29.11.16.
 */
public class Way {

    private Station stationStart;
    private Station stationDestination;

    private double startToStation;
    private double stationToStation;
    private double stationToDestination;

    public Way(Location start, Location destination, Station stationStart, Station stationDestination) {
        this.stationStart = stationStart;
        this.stationDestination = stationDestination;
        this.startToStation = Haversine.haversine(start.getLat(), start.getLng(), stationStart.getLatitude(), stationStart.getLongitude());
        this.stationToStation = Haversine.haversine(stationStart.getLatitude(), stationStart.getLongitude(), stationDestination.getLatitude(), stationDestination.getLongitude());
        this.stationToDestination = Haversine.haversine(stationDestination.getLatitude(), stationDestination.getLongitude(),destination.getLat(), destination.getLng());
    }

    public Station getStationStart() {
        return stationStart;
    }

    public void setStationStart(Station stationStart) {
        this.stationStart = stationStart;
    }

    public Station getStationDestination() {
        return stationDestination;
    }

    public void setStationDestination(Station stationDestination) {
        this.stationDestination = stationDestination;
    }

    public double getStartToStation() {
        return startToStation;
    }

    public void setStartToStation(double startToStation) {
        this.startToStation = startToStation;
    }

    public double getStationToStation() {
        return stationToStation;
    }

    public void setStationToStation(double stationToStation) {
        this.stationToStation = stationToStation;
    }

    public double getStationToDestination() {
        return stationToDestination;
    }

    public void setStationToDestination(double stationToDestination) {
        this.stationToDestination = stationToDestination;
    }
}
