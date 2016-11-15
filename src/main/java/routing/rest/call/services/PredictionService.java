package routing.rest.call.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import routing.rest.call.services.classes.Station;

import java.util.ArrayList;

/**
 * Created by FBeck on 15.11.2016.
 */
public interface PredictionService {

    @GET("maps/api/geocode/json")
    Call<ArrayList<Station>> getPrediction(@Query("name") String name);

}
