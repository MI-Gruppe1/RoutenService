package routing.rest.call.services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import routing.rest.call.services.classes.BestandStation;
import routing.rest.call.services.classes.Prediction;
import routing.rest.call.services.classes.Station;
import routing.rest.call.services.classes.StationPrediction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FBeck on 15.11.2016.
 */
public interface BestandsService {

    @POST("bestandUndVorhersage")
    Call<List<StationPrediction>> getPrediction(@Body List<BestandStation> stations);

}
