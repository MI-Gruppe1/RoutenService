package routing.rest.call;

import org.eclipse.jetty.server.Authentication;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by FBeck on 25.10.2016.
 */
public interface AddressConverter {

    @GET("maps/api/geocode/json")
    Call<AddressConversationAnswer> convert(@Query("address") String address, @Query("key") String key);

}
