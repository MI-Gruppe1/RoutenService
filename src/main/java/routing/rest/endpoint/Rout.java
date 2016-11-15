package routing.rest.endpoint;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import routing.rest.call.google.classes.RoutingAnswer;

/**
 * Created by FBeck on 15.11.2016.
 */
public class Rout {

    @SerializedName("startToFirst")
    @Expose
    private RoutingAnswer startToFirst;

    @SerializedName("firstToSecond")
    @Expose
    private RoutingAnswer firstToSecond;

    @SerializedName("SecondToDestination")
    @Expose
    private RoutingAnswer SecondToDestination;

    public Rout(){}

    public Rout(RoutingAnswer startToFirst, RoutingAnswer firstToSecond, RoutingAnswer secondToDestination) {
        this.startToFirst = startToFirst;
        this.firstToSecond = firstToSecond;
        SecondToDestination = secondToDestination;
    }

    public RoutingAnswer getStartToFirst() {
        return startToFirst;
    }

    public void setStartToFirst(RoutingAnswer startToFirst) {
        this.startToFirst = startToFirst;
    }

    public RoutingAnswer getFirstToSecond() {
        return firstToSecond;
    }

    public void setFirstToSecond(RoutingAnswer firstToSecond) {
        this.firstToSecond = firstToSecond;
    }

    public RoutingAnswer getSecondToDestination() {
        return SecondToDestination;
    }

    public void setSecondToDestination(RoutingAnswer secondToDestination) {
        SecondToDestination = secondToDestination;
    }
}
