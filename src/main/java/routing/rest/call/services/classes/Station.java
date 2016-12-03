package routing.rest.call.services.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import routing.rest.call.google.classes.Location;

/**
 * Created by FBeck on 08.11.2016.
 */
public class Station {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;

    public Station(){}

    public Station(String name, Double latitude, Double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;

        if (!getName().equals(station.getName())) return false;
        if (!getLatitude().equals(station.getLatitude())) return false;
        return getLongitude().equals(station.getLongitude());

    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getLatitude().hashCode();
        result = 31 * result + getLongitude().hashCode();
        return result;
    }

    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Location toLocation () {
        Location location = new Location();
        location.setLat(latitude);
        location.setLng(longitude);
        return location;
    }
}
