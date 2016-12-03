package routing.rest.endpoint;

import routing.rest.call.services.classes.Station;

/**
 * Created by Rutkay on 29.11.16.
 */
public class StationTupel {

    private Station stationStart;

    private Station stationDestination;

    public StationTupel(Station stationStart, Station stationDestination) {
        this.stationStart = stationStart;
        this.stationDestination = stationDestination;
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
}
