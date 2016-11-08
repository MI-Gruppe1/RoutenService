package routing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import routing.rest.call.google.classes.AddressConversationAnswer;
import routing.rest.call.google.GoogleApi;
import routing.rest.call.google.classes.RoutingAnswer;
import routing.rest.endpoint.Routing;

import java.io.IOException;

/**
 * Created by FBeck on 25.10.2016.
 */
public class RoutenService {
    private static String geocodeKey = "AIzaSyDMvwHv9F7evXsKaDGdIhKNUyjsyviV4aU";
    private static String directionsKey = "AIzaSyAWbOGw9GOWPE3PgytbNiNh011aw8_L2bQ";


    public static void main(String[] args) throws IOException {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        GoogleApi googleApi = retrofit.create(GoogleApi.class);
        Routing routing = new Routing(googleApi, geocodeKey, directionsKey);

        routing.startRouting();
    }
}
