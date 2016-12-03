package routing.rest.endpoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import routing.rest.call.google.classes.Location;
import routing.rest.call.services.PredictionService;
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

        List orderedStations = routing.orderStationsInNewList(stationList, 49.0, 49.0);

        List rightStationsList = new ArrayList();
        rightStationsList.add(station4);
        rightStationsList.add(station3);
        rightStationsList.add(station1);
        rightStationsList.add(station2);

        assertEquals(rightStationsList, orderedStations);
    }

    @Test
    public void predictionServiceTest() throws Exception {
        Routing routing = new Routing("https://localhost:9999/", "https://maps.googleapis.com/", "https://localhost:9999/", null, null);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:9999/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        PredictionService predictionService = retrofit.create(PredictionService.class);

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

        Map<String, StationPrediction> stationPredictionMap = new HashMap<>();
        stationPredictionMap.put("station1", stationPrediction1);
        stationPredictionMap.put("station2", stationPrediction2);
        stationPredictionMap.put("station3", stationPrediction3);
        stationPredictionMap.put("station4", stationPrediction4);
        stationPredictionMap.put("station5", stationPrediction5);

        Prediction prediction = new Prediction();
        prediction.setPrediction(stationPredictionMap);

        port(9999);
        get("/testP", (req, res) -> {
            return gson.toJson(prediction);
        });

        Thread.sleep(300);

        Call<Prediction> call = predictionService.getPrediction(new ArrayList<>());
        Response<Prediction> answer = call.execute();
        for (Map.Entry<String, StationPrediction> entry: answer.body().getPrediction().entrySet()) {
            assertEquals(entry.getValue().getStationName(), prediction.getPrediction().get(entry.getKey()).getStationName());
        }
    }

    @Test
    public void routingTest() throws Exception {

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

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Type listType = new TypeToken<ArrayList<Station>>(){}.getType();
        List<Station> stations = gson.fromJson(str, listType);

        port(9999);
        get("/testP", (req, res) -> {
            String[] names = req.queryParamsValues("names");
            StationPrediction stationPrediction1 = new StationPrediction();
            stationPrediction1.setStationName(names[0]);
            stationPrediction1.setBikes(10);
            stationPrediction1.setTrend(-2);
            StationPrediction stationPrediction2 = new StationPrediction();
            stationPrediction2.setStationName(names[1]);
            stationPrediction2.setBikes(10);
            stationPrediction2.setTrend(-2);
            StationPrediction stationPrediction3 = new StationPrediction();
            stationPrediction3.setStationName(names[2]);
            stationPrediction3.setBikes(10);
            stationPrediction3.setTrend(-2);
            StationPrediction stationPrediction4 = new StationPrediction();
            stationPrediction4.setStationName(names[3]);
            stationPrediction4.setBikes(10);
            stationPrediction4.setTrend(-2);
            StationPrediction stationPrediction5 = new StationPrediction();
            stationPrediction5.setStationName(names[4]);
            stationPrediction5.setBikes(10);
            stationPrediction5.setTrend(-2);

            Map<String, StationPrediction> stationPredictionMap = new HashMap<>();
            stationPredictionMap.put(names[0], stationPrediction1);
            stationPredictionMap.put(names[1], stationPrediction2);
            stationPredictionMap.put(names[2], stationPrediction3);
            stationPredictionMap.put(names[3], stationPrediction4);
            stationPredictionMap.put(names[4], stationPrediction5);

            Prediction prediction = new Prediction();
            prediction.setPrediction(stationPredictionMap);

            return gson.toJson(prediction);
        });

        get("/testS", (req, res) -> {
            String x = req.queryParams("x");
            double lat = Double.valueOf(req.queryParams("lat"));
            double lng = Double.valueOf(req.queryParams("lng"));

            List<Station> foundStations = routing.orderStationsInNewList(stations, lat, lng);
            foundStations = foundStations.subList(0,5);
            return gson.toJson(foundStations);
        });

        Location location1 = routing.askDestination("Berliner+Tor+5");
        List<Station> ss1 = routing.askStations(location1);

        Location location2 = routing.askDestination("Vorsetzen+50");
        List<Station> ss2 = routing.askStations(location2);

        WholeRoute wholeRoute = routing.routing("Berliner+Tor+5","Vorsetzen+50");

        System.out.println("h");
    }
}