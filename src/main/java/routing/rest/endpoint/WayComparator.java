package routing.rest.endpoint;

import routing.rest.call.google.classes.Location;

import java.util.Comparator;

/**
 * Created by Rutkay on 29.11.16.
 */
public class WayComparator implements Comparator<Way> {

    private int walkingWeight;
    private int bikingWeight;

    public WayComparator(int walkingWeight, int bikingWeight) {
        this.walkingWeight = walkingWeight;
        this.bikingWeight = bikingWeight;
    }

    @Override
    public int compare(Way way1, Way way2) {
        double distance1 = walkingWeight * way1.getStartToStation() + bikingWeight * way1.getStationToStation() + walkingWeight * way1.getStationToDestination();
        double distance2 = walkingWeight * way2.getStartToStation() + bikingWeight * way2.getStationToStation() + walkingWeight * way2.getStationToDestination();

        return distance1 < distance2 ? -1 : distance1 == distance2 ? 0 : 1;
    }
}
