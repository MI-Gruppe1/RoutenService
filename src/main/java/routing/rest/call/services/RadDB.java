package routing.rest.call.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import routing.rest.call.services.classes.Station;

import java.util.ArrayList;

/**
 * Created by FBeck on 08.11.2016.
 */
public interface RadDB {

    @GET("nextXStationsofLatLong")
    Call<ArrayList<Station>> getStations(@Query("number_of_stations") int x, @Query("latitude") Double lat, @Query("longitude") Double lng);

}
