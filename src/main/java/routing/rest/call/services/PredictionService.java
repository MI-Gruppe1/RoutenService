package routing.rest.call.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import routing.rest.call.services.classes.Prediction;
import routing.rest.call.services.classes.Station;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FBeck on 15.11.2016.
 */
public interface PredictionService {

    @GET("testP")
    Call<Prediction> getPrediction(@Query("names") List<String> name);

}
