package routing.rest.endpoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import routing.rest.call.google.GoogleApi;
import routing.rest.call.google.classes.AddressConversationAnswer;
import routing.rest.call.google.classes.Location;
import routing.rest.call.google.classes.RoutingAnswer;
import routing.rest.call.services.PredictionService;
import routing.rest.call.services.RadDB;
import routing.rest.call.services.classes.Prediction;
import routing.rest.call.services.classes.Station;
import routing.rest.call.services.classes.StationPrediction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static spark.Spark.get;

/**
 * Created by FBeck on 08.11.2016.
 */
public class Routing {

    private Gson gson;
    private GoogleApi google;
    private RadDB radD;
    private PredictionService predictionService;

    private String geocodeKey;
    private String directionsKey;

    private String BICYCLING = "bicycling";
    private String WALKING = "walking";

    public Routing(String google, String radDB, String predictionService, String geocodeKey, String directionsKey) {
        this.geocodeKey = geocodeKey;
        this.directionsKey = directionsKey;

        this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit googleRetrofit = new Retrofit.Builder()
                .baseUrl(google)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        this.google = googleRetrofit.create(GoogleApi.class);

        Retrofit radDBRetrofit = new Retrofit.Builder()
                .baseUrl(radDB)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        this.radD = radDBRetrofit.create(RadDB.class);

        Retrofit predictionServiceRetrofit = new Retrofit.Builder()
                .baseUrl(predictionService)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        this.predictionService = predictionServiceRetrofit.create(PredictionService.class);
    }

    public void startRouting() {
        get("/routing", (req, res) -> {
            String origin = req.queryParams("origin");
            String destination = req.queryParams("destination");

            origin = origin.replaceAll(" ", "+");
            destination = destination.replaceAll(" ", "+");

            return gson.toJson(routing(origin, destination));
        });
    }

    public List<Station> askStations(Location location) throws IOException {
        Call<ArrayList<Station>> call = radD.getStations(5, location.getLat(), location.getLng());
        Response<ArrayList<Station>> response = call.execute();
        return response.body();
    }

    private Prediction askPredictionStations( List<Station> stations) throws IOException {
        List<String> stationNames = new ArrayList<>();
        for (Station station: stations) {
            stationNames.add(station.getName());
        }
        Call<Prediction> call = predictionService.getPrediction(stationNames);
        Response<Prediction> response = call.execute();
        return response.body();
    }

    public Location askDestination(String wayPoint) throws IOException {
        Call<AddressConversationAnswer> call = google.convert(wayPoint, geocodeKey);
        Response<AddressConversationAnswer> answer = call.execute();
        return answer.body().getResults().get(0).getGeometry().getLocation();
    }

    private RoutingAnswer askRout(String origin, String destination, String mode) throws IOException {
        Call<RoutingAnswer> call = google.rout(origin, destination, mode, directionsKey);
        Response<RoutingAnswer> answer = call.execute();

        return answer.body();
    }

    public List<Station> orderStationsInNewList(List<Station> stationen, double latWaypoint, double lngWaypoint) {
        List<Station> orderedStations = new ArrayList<>(stationen);
        orderedStations.sort(new StationComparator(latWaypoint, lngWaypoint));
        return orderedStations;
    }

    public List<StationTupel> orderStationsForShortestPath(Location start, Location destination, List<Station> startStations, List<Station> destinationStations){
        List<StationTupel> stationTupels = new ArrayList<>();
        for (Station station1: startStations) {
            for(Station station2: destinationStations) {
                stationTupels.add(new StationTupel(station1, station2));
            }
        }
        stationTupels.sort(new StationTupelComparator(start, destination, 2, 1));
        return stationTupels;
    }

    public int calculateBikesInStationForTime(StationPrediction stationPrediction, double seconds){
        return (int) Math.nextDown(stationPrediction.getBikes() + stationPrediction.getTrend() * seconds / 3600.0);
    }

    public WholeRoute routing(String origin, String destination) throws IOException, NullPointerException {
        Location originLocation = askDestination(origin);
        Location destinationLocation = askDestination(destination);

        List<Station> stationsOriginLocation = askStations(originLocation);
        List<Station> stationsDestinationLocation = askStations(destinationLocation);
        List<Station> tested = new ArrayList<>();

        Prediction prediction = askPredictionStations(stationsOriginLocation);

        List<StationTupel> stationTupel = orderStationsForShortestPath(originLocation, destinationLocation, stationsOriginLocation, stationsDestinationLocation);
        boolean notfound = true;
        Iterator<StationTupel> stationTupelIterator = stationTupel.iterator();
        RoutingAnswer startToFirst = null;
        StationTupel foundTupel = null;
        while (notfound && stationTupelIterator.hasNext()) {
            StationTupel currentTupel = stationTupelIterator.next();
            if(!tested.contains(currentTupel.getStationStart())) {
                tested.add(currentTupel.getStationStart());
                //start Station laufzeit
                startToFirst = askRout(originLocation.toLatLongString(), currentTupel.getStationStart().toLocation().toLatLongString(), WALKING);
                //Berechnen
                int bikes = calculateBikesInStationForTime(prediction.getPrediction().get(currentTupel.getStationStart().getName()), startToFirst.getRoutes().get(0).getLegs().get(0).getDuration().getValue());
                // wenn genug räder vorhande
                if (bikes > 3) { //genug räder? min. 3-5 zB
                    notfound = false;
                    foundTupel = currentTupel;
                }
            }
        }

        if(notfound){
            foundTupel = stationTupel.get(0);
            startToFirst = askRout(originLocation.toLatLongString(), foundTupel.getStationStart().toLocation().toLatLongString(), WALKING);
        }

        RoutingAnswer firstToSecond = askRout(foundTupel.getStationStart().toLocation().toLatLongString(), foundTupel.getStationDestination().toLocation().toLatLongString(), BICYCLING);
        RoutingAnswer secondToDestination = askRout(foundTupel.getStationDestination().toLocation().toLatLongString(), destinationLocation.toLatLongString(), WALKING);

        return new WholeRoute(startToFirst.getRoutes().get(0), firstToSecond.getRoutes().get(0), secondToDestination.getRoutes().get(0));
    }
}
