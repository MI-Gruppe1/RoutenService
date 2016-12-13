package routing.rest.call.services.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by FBeck on 03.12.2016.
 */
public class StationPrediction {
    @SerializedName("name")
    @Expose
    private String stationName;

    @SerializedName("bikes")
    @Expose
    private int bikes;

    @SerializedName("vorhersage")
    @Expose
    private int trend;

    public StationPrediction() {}

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StationPrediction)) return false;

        StationPrediction that = (StationPrediction) o;

        if (getBikes() != that.getBikes()) return false;
        if (getTrend() != that.getTrend()) return false;
        return getStationName().equals(that.getStationName());

    }

    @Override
    public int hashCode() {
        int result = getStationName().hashCode();
        result = 31 * result + getBikes();
        result = 31 * result + getTrend();
        return result;
    }
}
