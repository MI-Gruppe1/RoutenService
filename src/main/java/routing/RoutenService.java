package routing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import routing.rest.call.AddressConversationAnswer;
import routing.rest.call.AddressConverter;

import java.io.IOException;
import java.util.List;

import static spark.Spark.*;

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

        AddressConverter service = retrofit.create(AddressConverter.class);

        Call<AddressConversationAnswer> a = service.convert("Hermannstal+97","AIzaSyDMvwHv9F7evXsKaDGdIhKNUyjsyviV4aU");
        Response<AddressConversationAnswer> aa = a.execute();
        System.out.println("hello");

        HttpResponse<JsonNode> jsonResponse = Unirest.post("https://maps.googleapis.com/maps/api/geocode/json")
                .header("accept", "application/json")
                .queryString("address", "Hermannstal+97")
                .queryString("key", "AIzaSyDMvwHv9F7evXsKaDGdIhKNUyjsyviV4aU")
                .asJson();
        AddressConversationAnswer aaaa = gson.fromJson(jsonResponse.getBody().toString(),AddressConversationAnswer.class);
        System.out.println("hello");
    }
}
