package routing.rest.call.services.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

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

    @SerializedName("history")
    @Expose
    private List<Integer> history;

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

    public List<Integer> getHistory() {
        return history;
    }

    public void setHistory(List<Integer> history) {
        this.history = history;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StationPrediction)) return false;

        StationPrediction that = (StationPrediction) o;

        if (getBikes() != that.getBikes()) return false;
        if (getTrend() != that.getTrend()) return false;
        if (getStationName() != null ? !getStationName().equals(that.getStationName()) : that.getStationName() != null)
            return false;
        return getHistory() != null ? getHistory().equals(that.getHistory()) : that.getHistory() == null;
    }

    @Override
    public int hashCode() {
        int result = getStationName() != null ? getStationName().hashCode() : 0;
        result = 31 * result + getBikes();
        result = 31 * result + getTrend();
        result = 31 * result + (getHistory() != null ? getHistory().hashCode() : 0);
        return result;
    }
}
