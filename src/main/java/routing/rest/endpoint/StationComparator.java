package routing.rest.endpoint;

import routing.rest.call.google.classes.Location;
import routing.rest.call.services.classes.Station;

import java.util.Comparator;

/**
 * Created by FBeck on 19.11.2016.
 */
public class StationComparator implements Comparator<Station> {

    private Location location;

    public StationComparator(Location location) {
        this.location = location;
    }

    @Override
    public int compare(Station o1, Station o2) {
        double distanceO1 = Haversine.haversine(o1.getLatitude(), o1.getLongitude(), location.getLat(), location.getLng());
        double distanceO2 = Haversine.haversine(o2.getLatitude(), o2.getLongitude(), location.getLat(), location.getLng());
        //return a.age < b.age ? -1 : a.age == b.age ? 0 : 1;
        return distanceO1 < distanceO2 ? -1 : distanceO1 == distanceO2 ? 0 : 1;
    }
}
