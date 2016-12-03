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
            /*String origin = req.queryParams("origin");
            String destination = req.queryParams("destination");

            origin = origin.replaceAll(" ", "+");
            destination = destination.replaceAll(" ", "+");
*/
            HashMap<String, Station> test = new HashMap<String, Station>();
            test.put("station1", new Station("station1",4.4,4.5));
            test.put("station2", new Station("station2",4.4,4.5));
            return gson.toJson(test);//gson.toJson(routing(origin, destination));
        });
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

    public List<Station> askStations(Location location) throws IOException {
        Call<ArrayList<Station>> call = radD.getStations(5, location.getLat(), location.getLng());
        Response<ArrayList<Station>> response = call.execute();
        return response.body();
    }


    public List<Station> orderStationsInNewList(List<Station> stationen, double latWaypoint, double lngWaypoint) {
        List<Station> orderedStations = new ArrayList<>(stationen);
        orderedStations.sort(new StationComparator(latWaypoint, lngWaypoint));
        return orderedStations;
    }

    private Station findNearestStation(List<Station> stationen, Location startLocation, Location endLocation, boolean withAbailability) throws IOException {
        List<Station> startLocList = orderStationsInNewList(stationen, startLocation.getLat(), startLocation.getLng());
        List<Station> endLocList = orderStationsInNewList(stationen, endLocation.getLat(), endLocation.getLng());
        boolean found = false;
        Station currentStation = startLocList.get(0);
        for (int i = 0; (i < startLocList.size()) && !found; i++) {
            for (int j = 0; (j <= 2) && !found; j++) {
                if (endLocList.get(i).getName().equals(startLocList.get(j).getName())) {
                    if (withAbailability && askAvailabilityForStation(endLocList.get(i))) {
                        found = true;
                        currentStation = endLocList.get(i);
                    } else if (!withAbailability) {
                        found = true;
                        currentStation = endLocList.get(i);
                    }
                }
            }
        }
        return currentStation;
    }

<<<<<<< HEAD
    public List<StationTupel> orderStationsForShortestPath(Location start, Location destination, List<Station> startStations, List<Station> destinationStations){
        List<StationTupel> stationTupels = new ArrayList<>();
        for (Station station1: startStations) {
            for(Station station2: startStations) {
                stationTupels.add(new StationTupel(station1, station2));
            }
        }
        stationTupels.sort(new StationTupelComparator(start, destination));
        return stationTupels;
    }

    private Boolean askAvailabilityForStation(Station station) throws IOException {
=======
    public Boolean askAvailabilityForStation(Station station) throws IOException {
>>>>>>> c118aec2c2e52696d0d74fff376aff5a21f6c49f
        Call<Prediction> call = predictionService.getPrediction(station.getName());
        Response<Prediction> response = call.execute();
        if (response.body().getPrediction().get(0) >= 5) {
            return true;
        } else {
            return false;
        }
    }

    private WholeRoute buildRout(Location origin, Location startStation, Location destinationStation, Location destination) throws IOException {
        RoutingAnswer startToFirst = askRout(origin.toLatLongString(), startStation.toLatLongString(), WALKING);
        RoutingAnswer firstToSecond = askRout(startStation.toLatLongString(), destinationStation.toLatLongString(), BICYCLING);
        RoutingAnswer secondToDestination = askRout(destinationStation.toLatLongString(), destination.toLatLongString(), WALKING);

        return new WholeRoute(startToFirst.getRoutes().get(0), firstToSecond.getRoutes().get(0), secondToDestination.getRoutes().get(0));
    }

<<<<<<< HEAD
    private WholeRoute routing(String origin, String destination) throws IOException, NullPointerException {
=======
    public WholeRoute routing(String origin, String destination) throws IOException {
>>>>>>> c118aec2c2e52696d0d74fff376aff5a21f6c49f
        Location originLocation = askDestination(origin);
        Location destinationLocation = askDestination(destination);

        Station originStation = null;
        Station destinationStation = null;

        List<StationTupel> stationTupel = orderStationsForShortestPath(originLocation, destinationLocation, askStations(originLocation), askStations(destinationLocation));
        boolean notfound = true;
        Iterator<StationTupel> stationTupelIterator = stationTupel.iterator();
        RoutingAnswer startToFirst = null;
        StationTupel foundTupel = null;
        while (notfound && stationTupelIterator.hasNext()) {
            StationTupel currentTupel = stationTupelIterator.next();
            //start Station laufzeit
            startToFirst = askRout(originLocation.toLatLongString(), currentTupel.getStationStart().toLocation().toLatLongString(), WALKING);
            //Berechnen

            // wenn genug räder vorhande
            if (true) { //genug räder? min. 3-5 zB
                notfound = false;
                originStation = currentTupel.getStationStart();
                destinationStation = currentTupel.getStationDestination();
                foundTupel = currentTupel;
            }
        }

        if(notfound){
            foundTupel = stationTupel.get(0);
            originStation = foundTupel.getStationStart();
            destinationStation = foundTupel.getStationDestination();
            startToFirst = askRout(originLocation.toLatLongString(), originStation.toLocation().toLatLongString(), WALKING);

        }
//Stationtupels durchiterieren

//            int bikes = predList.get(predStationIndex).getBikes();
//            int trend = predList.get(predStationIndex).getTrend();



        RoutingAnswer firstToSecond = askRout(foundTupel.getStationStart().toLocation().toLatLongString(), foundTupel.getStationDestination().toLocation().toLatLongString(), BICYCLING);
        RoutingAnswer secondToDestination = askRout(foundTupel.getStationDestination().toLocation().toLatLongString(), destinationLocation.toLatLongString(), WALKING);


        return buildRout(originLocation, originStation.toLocation(), destinationStation.toLocation(), destinationLocation);
    }
}
