package routing.rest.call.services.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FBeck on 15.11.2016.
 */
public class Prediction {

    @SerializedName("prediction")
    @Expose
    private Map<String, StationPrediction> prediction;

    public Prediction(){}

    public Prediction(List<StationPrediction> predictions){
        this.prediction = new HashMap<>();
        for (StationPrediction prediciton: predictions) {
            this.prediction.put(prediciton.getStationName(), prediciton);
        }
    }

    public Map<String, StationPrediction> getPrediction() {
        return prediction;
    }

    public void setPrediction(Map<String, StationPrediction> prediction) {
        this.prediction = prediction;
    }
}
