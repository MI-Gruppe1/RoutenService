package routing.rest.call.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import routing.rest.call.services.classes.Station;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;

/**
 * Created by FBeck on 08.11.2016.
 */
public interface RadDB {

    @GET("maps/api/geocode/json")
    Call<ArrayList<Station>> getStations(@Query("x") int x, @Query("lat") Double lat, @Query("long") Double lng);

}
