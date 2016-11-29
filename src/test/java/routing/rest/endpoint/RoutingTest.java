package routing.rest.endpoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import routing.rest.call.google.GoogleApi;
import routing.rest.call.google.classes.AddressConversationAnswer;
import routing.rest.call.services.PredictionService;
import routing.rest.call.services.RadDB;
import routing.rest.call.services.classes.Prediction;
import routing.rest.call.services.classes.Station;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static spark.Spark.get;
import static spark.Spark.port;

/**
 * Created by FBeck on 09.11.2016.
 */
public class RoutingTest {
    @Test
    public void orderStationsTest() throws Exception {
        Routing routing = new Routing(null, null, null, null, null);

        Station station1 = new Station();
        station1.setName("1");
        station1.setLatitude(49.5);
        station1.setLongitude(49.5);
        Station station2 = new Station();
        station2.setName("2");
        station2.setLatitude(51.0);
        station2.setLongitude(51.0);
        Station station3 = new Station();
        station3.setName("3");
        station3.setLatitude(48.9);
        station3.setLongitude(48.5);
        Station station4 = new Station();
        station4.setName("4");
        station4.setLatitude(48.91);
        station4.setLongitude(48.5);

        List stationList = new ArrayList();
        stationList.add(station2);
        stationList.add(station3);
        stationList.add(station1);
        stationList.add(station4);

        List orderedStations = routing.orderStationsInNewList(stationList,49.0,49.0);

        List rightStationsList = new ArrayList();
        rightStationsList.add(station4);
        rightStationsList.add(station3);
        rightStationsList.add(station1);
        rightStationsList.add(station2);

        assertEquals(rightStationsList,orderedStations);
    }

    @Test
    public void predictionServiceTest() throws Exception {
        Routing routing = new Routing("https://localhost:9999/", "https://localhost:9999/", "https://localhost:9999/", null, null);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:9999/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        PredictionService predictionService = retrofit.create(PredictionService.class);

        List<Integer> list = new ArrayList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        port(9999);
        get("/testP", (req, res) -> {
            return gson.toJson(new Prediction(list));
        });

        Thread.sleep(300);

        Call<Prediction> call = predictionService.getPrediction("hallo");
        Response<Prediction> answer = call.execute();
        assertEquals(list, answer.body().getPrediction());
    }

    /*@Test
    public void radDBServiceTest() throws Exception {
        Routing routing = new Routing(null, null, null, null);
    }*/
}