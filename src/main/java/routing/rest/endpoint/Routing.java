package routing.rest.endpoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.restassured.RestAssured;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import routing.rest.call.google.GoogleApi;
import routing.rest.call.google.classes.AddressConversationAnswer;
import routing.rest.call.google.classes.Location;
import routing.rest.call.google.classes.RoutingAnswer;
import routing.rest.call.services.BestandsService;
import routing.rest.call.services.RadDB;
import routing.rest.call.services.classes.BestandStation;
import routing.rest.call.services.classes.Prediction;
import routing.rest.call.services.classes.Station;
import routing.rest.call.services.classes.StationPrediction;
import spark.Spark;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static spark.Spark.get;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;


/**
 * Created by FBeck on 08.11.2016.
 */
public class Routing {

    private Gson gson;
    private GoogleApi google;
    private RadDB radD;
    private BestandsService bestandsService;
    private String predictionService;
    private String radDB;

    private String geocodeKey;
    private String directionsKey;

    private String BICYCLING = "bicycling";
    private String WALKING = "walking";

    public Routing(String google, String radDB, String predictionService, String geocodeKey, String directionsKey) {
        this.geocodeKey = geocodeKey;
        this.directionsKey = directionsKey;
        this.predictionService = predictionService;
        this.radDB = radDB;

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
        this.bestandsService = predictionServiceRetrofit.create(BestandsService.class);
    }

    public void startRouting() {
        //Spark.port(7000);
        System.out.println("XXXXX");
        get("/routing", (req, res) -> {
            System.out.println("Neue Anfrage!");
            String origin = req.queryParams("origin");
            String destination = req.queryParams("destination");

            origin = origin.replaceAll(" ", "+");
            destination = destination.replaceAll(" ", "+");

            return gson.toJson(routing(origin, destination));
        });
    }

    public List<Station> askStations(Location location) throws UnirestException {
        String string = radDB + "nextXStationsofLatLong?number_of_stations=5&latitude=" + location.getLat() + "&longitude=" + location.getLng();
        System.out.println(string);
        HttpResponse<JsonNode> jsonResponse = Unirest.get(string)
    //            .queryString("number_of_stations", 5)
    //            .queryString("latitude", location.getLat())
    //            .queryString("longitude", location.getLng())
                .asJson();

        io.restassured.response.Response response = RestAssured.given().contentType("application/json").get(string);


        System.out.println("AskStations: " + jsonResponse.getStatus());
        System.out.println("AskStations: " + jsonResponse.getBody().toString() );

        if (jsonResponse.getStatus() != 200){
            throw new UnirestException("" + jsonResponse.getStatus());
        }

        Type listTypeStations = new TypeToken<ArrayList<Station>>() {}.getType();
        return gson.fromJson(jsonResponse.getBody().toString(), listTypeStations);
    }

    private Prediction askPredictionStations(List<Station> stations) throws IOException {

        List<BestandStation> bestandStations = new ArrayList<>();
        for (Station station : stations) {
            bestandStations.add(new BestandStation(station.getName()));
        }
        String string = predictionService + "bestand";
        System.out.println(string);
        io.restassured.response.Response response = RestAssured.given().contentType("application/json").body(gson.toJson(bestandStations)).get(string);

        System.out.println("Prediction: " + response.statusCode());
        System.out.println("Prediction: " + response.toString());

        Type listTypeStations = new TypeToken<ArrayList<StationPrediction>>() {
        }.getType();

        if(response.hashCode() != 200){
            throw new IOException();
        }
        return new Prediction(gson.fromJson(response.asString(), listTypeStations));
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

    public List<Station> orderStationsForLocation(List<Station> stationen, Location location) {
        List<Station> orderedStations = new ArrayList<>(stationen);
        orderedStations.sort(new StationComparator(location));
        return orderedStations;
    }

    public List<Way> orderStationsForShortestPath(Location start, Location destination, List<Station> startStations, List<Station> destinationStations) {
        List<Way> ways = new ArrayList<>();
        for (Station station1 : startStations) {
            for (Station station2 : destinationStations) {
                ways.add(new Way(start, destination, station1, station2));
            }
        }
        ways.sort(new WayComparator(2, 1));
        return ways;
    }

    public int calculateBikesInStationForTime(StationPrediction stationPrediction, double seconds) {
        return (int) Math.nextDown(stationPrediction.getBikes() + stationPrediction.getTrend() * seconds / 3600.0);
    }

    public List<RoutingAnswer> routing(String origin, String destination) throws IOException, UnirestException, NullPointerException {
        System.out.println("Routing starten!");
        Location originLocation = askDestination(origin);
        Location destinationLocation = askDestination(destination);

        try {
            List<Station> stationsOriginLocation = askStations(originLocation);
            System.out.println("Stationen bekommen!");

            List<Station> stationsOriginLocationOrdered = orderStationsForLocation(stationsOriginLocation, originLocation);
            double shortestPathToStation = Haversine.haversine(originLocation.getLat(), originLocation.getLng(), stationsOriginLocationOrdered.get(0).getLatitude(), stationsOriginLocationOrdered.get(0).getLongitude());
            double stationToStation = Haversine.haversine(originLocation.getLat(), originLocation.getLng(), destinationLocation.getLat(), destinationLocation.getLng());

            if (stationToStation < shortestPathToStation) {
                List<RoutingAnswer> routingAnswers = new ArrayList<>();
                RoutingAnswer rout = askRout(origin, destination, WALKING);
                routingAnswers.add(rout);
                return routingAnswers;
            }

            List<Station> stationsDestinationLocation = askStations(destinationLocation);
            System.out.println("Stationen bekommen");
            List<Station> tested = new ArrayList<>();

            Prediction prediction = askPredictionStations(stationsOriginLocation);

            List<Way> way = orderStationsForShortestPath(originLocation, destinationLocation, stationsOriginLocation, stationsDestinationLocation);
            boolean notfound = true;
            Iterator<Way> stationTupelIterator = way.iterator();
            RoutingAnswer startToFirst = null;
            Way foundTupel = null;
            while (notfound && stationTupelIterator.hasNext()) {
                Way currentTupel = stationTupelIterator.next();
                if (!tested.contains(currentTupel.getStationStart())) {
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

            if (notfound) {
                foundTupel = way.get(0);
                startToFirst = askRout(originLocation.toLatLongString(), foundTupel.getStationStart().toLocation().toLatLongString(), WALKING);
            }

            RoutingAnswer firstToSecond = askRout(foundTupel.getStationStart().toLocation().toLatLongString(), foundTupel.getStationDestination().toLocation().toLatLongString(), BICYCLING);
            RoutingAnswer secondToDestination = askRout(foundTupel.getStationDestination().toLocation().toLatLongString(), destinationLocation.toLatLongString(), WALKING);

            List<RoutingAnswer> routingAnswers = new ArrayList<>();
            routingAnswers.add(startToFirst);
            routingAnswers.add(firstToSecond);
            routingAnswers.add(secondToDestination);

            return routingAnswers;
        } catch (IOException e) {
            System.out.println("Routing fallback!");
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            System.out.println(e);

            List<RoutingAnswer> routingAnswers = new ArrayList<>();

            RoutingAnswer rout = askRout(originLocation.toLatLongString(), destinationLocation.toLatLongString(), BICYCLING);

            routingAnswers.add(rout);
            return routingAnswers;
        } catch (UnirestException e) {
            System.out.println("Routing fallback!");
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            System.out.println(e);
            List<RoutingAnswer> routingAnswers = new ArrayList<>();

            RoutingAnswer rout = askRout(originLocation.toLatLongString(), destinationLocation.toLatLongString(), BICYCLING);

            routingAnswers.add(rout);
            return routingAnswers;
        }
    }
}
