package routing.rest.call.services.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by FBeck on 15.11.2016.
 */
public class Prediction {

    @SerializedName("prediction")
    @Expose
    private List<Integer> prediction;

    public Prediction(List<Integer> prediction) {
        this.prediction = prediction;
    }

    public List<Integer> getPrediction() {
        return prediction;
    }

    public void setPrediction(List<Integer> prediction) {
        this.prediction = prediction;
    }
}
