package routing.rest.call.google;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import routing.rest.call.google.classes.AddressConversationAnswer;
import routing.rest.call.google.classes.RoutingAnswer;

/**
 * Created by FBeck on 25.10.2016.
 */
public interface GoogleApi {

    @GET("maps/api/geocode/json")
    Call<AddressConversationAnswer> convert(@Query("address") String address, @Query("key") String key);

    @GET("maps/api/directions/json")
    Call<RoutingAnswer> rout(@Query("origin") String origin, @Query("destination") String destination, @Query("mode") String mode, @Query("key") String key);
}
