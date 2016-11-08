package routing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.exceptions.UnirestException;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import routing.rest.call.google.classes.AddressConversationAnswer;
import routing.rest.call.google.GoogleApi;
import routing.rest.call.google.classes.RoutingAnswer;

import java.io.IOException;

/**
 * Created by FBeck on 25.10.2016.
 */
public class RoutenService {

    public static void main(String[] args) throws UnirestException, IOException {
        //get("/hello", (req, res) -> "Hello World");


        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        GoogleApi google = retrofit.create(GoogleApi.class);

        Call<AddressConversationAnswer> a = google.convert("Hermannstal+97","AIzaSyDMvwHv9F7evXsKaDGdIhKNUyjsyviV4aU");
        Response<AddressConversationAnswer> aa = a.execute();
        System.out.println("hello");
        Call<RoutingAnswer> b = google.rout("Hermannstal+97","Berliner+Tor","bicycling","AIzaSyAWbOGw9GOWPE3PgytbNiNh011aw8_L2bQ");
        Response<RoutingAnswer> bb = b.execute();
        System.out.println("hallo");
    }
}
