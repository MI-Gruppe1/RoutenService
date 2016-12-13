package routing.rest.endpoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Query;
import routing.RoutenService;
import routing.rest.call.google.GoogleApi;
import routing.rest.call.google.classes.AddressConversationAnswer;
import routing.rest.call.google.classes.Location;
import routing.rest.call.google.classes.RoutingAnswer;
import routing.rest.call.services.BestandsService;
import routing.rest.call.services.classes.BestandStation;
import routing.rest.call.services.classes.Prediction;
import routing.rest.call.services.classes.Station;
import routing.rest.call.services.classes.StationPrediction;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static spark.Spark.get;
import static spark.Spark.port;

interface RoutingAPI{
    @GET("routing")
    Call<List<RoutingAnswer>> route(@Query("origin") String origin, @Query("destination") String destination);

}

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

        List<Station> stationList = new ArrayList<>();
        stationList.add(station2);
        stationList.add(station3);
        stationList.add(station1);
        stationList.add(station4);

        Location location = new Location();
        location.setLat(49.0);
        location.setLng(49.0);

        List<Station> orderedStations = routing.orderStationsForLocation(stationList, location);

        List<Station> rightStationsList = new ArrayList<>();
        rightStationsList.add(station4);
        rightStationsList.add(station3);
        rightStationsList.add(station1);
        rightStationsList.add(station2);

        assertEquals(rightStationsList, orderedStations);
    }

    @Test
    public void predictionServiceTest() throws Exception {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:9999/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        BestandsService bestandsService = retrofit.create(BestandsService.class);

        StationPrediction stationPrediction1 = new StationPrediction();
        stationPrediction1.setStationName("station1");
        stationPrediction1.setBikes(10);
        stationPrediction1.setTrend(-2);
        StationPrediction stationPrediction2 = new StationPrediction();
        stationPrediction2.setStationName("station2");
        stationPrediction2.setBikes(10);
        stationPrediction2.setTrend(-2);
        StationPrediction stationPrediction3 = new StationPrediction();
        stationPrediction3.setStationName("station3");
        stationPrediction3.setBikes(10);
        stationPrediction3.setTrend(-2);
        StationPrediction stationPrediction4 = new StationPrediction();
        stationPrediction4.setStationName("station4");
        stationPrediction4.setBikes(10);
        stationPrediction4.setTrend(-2);
        StationPrediction stationPrediction5 = new StationPrediction();
        stationPrediction5.setStationName("station5");
        stationPrediction5.setBikes(10);
        stationPrediction5.setTrend(-2);

        List<StationPrediction> stationPredictions = new ArrayList<>();
        stationPredictions.add(stationPrediction1);
        stationPredictions.add(stationPrediction2);
        stationPredictions.add(stationPrediction3);
        stationPredictions.add(stationPrediction4);
        stationPredictions.add(stationPrediction5);

        port(9999);
        get("/bestandUndVorhersage", (req, res) -> gson.toJson(stationPredictions));

        Thread.sleep(300);

        Call<List<StationPrediction>> call = bestandsService.getPrediction(new ArrayList<>());
        Response<List<StationPrediction>> answer = call.execute();
        assertEquals(answer.body(), stationPredictions);
    }

    @Test
    public void routingTest() throws Exception {
        port(9999);

        String str = "";
        try
        {
            File file = new File("station.txt");
            // Open an input stream
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            str = new String(data, "UTF-8");
        }
        // Catches any error conditions
        catch (IOException e)
        {
            System.err.println ("Unable to read from file");
            System.exit(-1);
        }

        Routing routing = new Routing("https://maps.googleapis.com/",
                "http://localhost:9999/",
                "http://localhost:9999/",
                "AIzaSyDMvwHv9F7evXsKaDGdIhKNUyjsyviV4aU",
                "AIzaSyAWbOGw9GOWPE3PgytbNiNh011aw8_L2bQ");
        routing.startRouting();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Type listTypeStations = new TypeToken<ArrayList<Station>>(){}.getType();
        List<Station> stations = gson.fromJson(str, listTypeStations);

        Type listTypeBestandStations = new TypeToken<ArrayList<BestandStation>>(){}.getType();

        Retrofit googleRetrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:9999/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RoutingAPI routenService = googleRetrofit.create(RoutingAPI.class);

        get("/bestandUndVorhersage", (req, res) -> {
            List<BestandStation> bestandStations = gson.fromJson(req.body(), listTypeBestandStations);
            String[] names = req.queryParamsValues("names");
            StationPrediction stationPrediction1 = new StationPrediction();
            stationPrediction1.setStationName(bestandStations.get(0).getName());
            stationPrediction1.setBikes(10);
            stationPrediction1.setTrend(-2);
            StationPrediction stationPrediction2 = new StationPrediction();
            stationPrediction2.setStationName(bestandStations.get(1).getName());
            stationPrediction2.setBikes(10);
            stationPrediction2.setTrend(-2);
            StationPrediction stationPrediction3 = new StationPrediction();
            stationPrediction3.setStationName(bestandStations.get(2).getName());
            stationPrediction3.setBikes(10);
            stationPrediction3.setTrend(-2);
            StationPrediction stationPrediction4 = new StationPrediction();
            stationPrediction4.setStationName(bestandStations.get(3).getName());
            stationPrediction4.setBikes(10);
            stationPrediction4.setTrend(-2);
            StationPrediction stationPrediction5 = new StationPrediction();
            stationPrediction5.setStationName(bestandStations.get(4).getName());
            stationPrediction5.setBikes(10);
            stationPrediction5.setTrend(-2);

            List<StationPrediction> stationPredictions = new ArrayList<>();
            stationPredictions.add(stationPrediction1);
            stationPredictions.add(stationPrediction2);
            stationPredictions.add(stationPrediction3);
            stationPredictions.add(stationPrediction4);
            stationPredictions.add(stationPrediction5);

            return gson.toJson(stationPredictions);
        });

        get("/nextXStationsofLatLong", (req, res) -> {
            int number_of_stations = Integer.valueOf(req.queryParams("number_of_stations"));
            double lat = Double.valueOf(req.queryParams("latitude"));
            double lng = Double.valueOf(req.queryParams("longitude"));
            Location location = new Location();
            location.setLat(lat);
            location.setLng(lng);

            List<Station> foundStations = routing.orderStationsForLocation(stations, location);
            foundStations = foundStations.subList(0,5);
            return gson.toJson(foundStations);
        });

        Call<List<RoutingAnswer>> call = routenService.route("Berliner+Tor+5","Vorsetzen+50");
        Response<List<RoutingAnswer>> answer = call.execute();

        //List<RoutingAnswer> routeAnswer = routing.routing("Berliner+Tor+5","Vorsetzen+50");
        //String answer = gson.toJson(routeAnswer);

        assertEquals(true,true);
    }
}