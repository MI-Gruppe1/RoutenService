package routing.rest.call.services.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by FBeck on 03.12.2016.
 */
public class StationPrediction {
    @SerializedName("name")
    @Expose
    private String StationName;

    @SerializedName("bikes")
    @Expose
    private int bikes;

    @SerializedName("trend")
    @Expose
    private int trend;

    public StationPrediction() {}

    public String getStationName() {
        return StationName;
    }

    public void setStationName(String stationName) {
        StationName = stationName;
    }

    public int getBikes() {
        return bikes;
    }

    public void setBikes(int bikes) {
        this.bikes = bikes;
    }

    public int getTrend() {
        return trend;
    }

    public void setTrend(int trend) {
        this.trend = trend;
    }
}
