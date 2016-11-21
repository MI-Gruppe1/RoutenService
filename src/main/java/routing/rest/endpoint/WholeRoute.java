package routing.rest.endpoint;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import routing.rest.call.google.classes.Leg;
import routing.rest.call.google.classes.Route;

/**
 * Created by FBeck on 15.11.2016.
 */
public class WholeRoute {

    @SerializedName("startToFirst")
    @Expose
    private Route startToFirst;

    @SerializedName("firstToSecond")
    @Expose
    private Route firstToSecond;

    @SerializedName("SecondToDestination")
    @Expose
    private Route SecondToDestination;

    public WholeRoute(){}

    public WholeRoute(Route startToFirst, Route firstToSecond, Route secondToDestination) {
        this.startToFirst = startToFirst;
        this.firstToSecond = firstToSecond;
        SecondToDestination = secondToDestination;
    }

    public Route getStartToFirst() {
        return startToFirst;
    }

    public void setStartToFirst(Route startToFirst) {
        this.startToFirst = startToFirst;
    }

    public Route getFirstToSecond() {
        return firstToSecond;
    }

    public void setFirstToSecond(Route firstToSecond) {
        this.firstToSecond = firstToSecond;
    }

    public Route getSecondToDestination() {
        return SecondToDestination;
    }

    public void setSecondToDestination(Route secondToDestination) {
        SecondToDestination = secondToDestination;
    }
}
