package routing.rest.call.services.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by FBeck on 13.12.2016.
 */
public class BestandStation {
    @SerializedName("name")
    @Expose
    private String name;

    public BestandStation() {}

    public BestandStation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
