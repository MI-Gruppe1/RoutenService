package routing;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;

import static spark.Spark.*;

/**
 * Created by FBeck on 25.10.2016.
 */
public class RoutenService {
    OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) throws IOException {
        get("/hello", (req, res) -> "Hello World");

        RoutenService routenService = new RoutenService();
        System.out.println(routenService.run("c"));



    }

    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/geocode/json?address=Hermannstal+97&key=AIzaSyDMvwHv9F7evXsKaDGdIhKNUyjsyviV4aU")
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
