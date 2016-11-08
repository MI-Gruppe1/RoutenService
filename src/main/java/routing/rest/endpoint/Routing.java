package routing.rest.endpoint;

import retrofit2.Call;
import retrofit2.Response;
import routing.rest.call.google.GoogleApi;
import routing.rest.call.google.classes.AddressConversationAnswer;
import routing.rest.call.google.classes.Location;
import routing.rest.call.google.classes.RoutingAnswer;

import java.io.IOException;

import static spark.Spark.*;

/**
 * Created by FBeck on 08.11.2016.
 */
public class Routing {
    private GoogleApi google;

    private String geocodeKey;
    private String directionsKey;

    private String BICYCLING = "bicycling";
    private String WALKING = "walking";

    public Routing(GoogleApi google, String geocodeKey, String directionsKey){
        this.google = google;
        this.geocodeKey = geocodeKey;
        this.directionsKey = directionsKey;
    }

    public void startRouting(){
        get("/routing", (req, res) -> {
            String origin = req.queryParams("origin");
            String destination = req.queryParams("destination");

            origin = origin.replaceAll(" ","+");
            destination = destination.replaceAll(" ","+");

            return routing(origin, destination);
        });
    }

    public Location askDestination(String wayPoint) throws IOException {
        Call<AddressConversationAnswer> call = google.convert(wayPoint, geocodeKey);
        Response<AddressConversationAnswer> answer = call.execute();
        return answer.body().getResults().get(0).getGeometry().getLocation();
    }

    private void askRout(String origin, String destination, String mode) throws IOException {
        Call<RoutingAnswer> call = google.rout(origin,destination,mode, directionsKey);
        Response<RoutingAnswer> answer = call.execute();

    }

    private void askStations(Location location){}

    private void orderStations(){}

    private void findNearestStation(){}

    private Boolean askAvailabilityForStation(){
        return true;
    }

    private void buildRout(Location origin, Location startStation, Location destinationStation, Location destination) throws IOException {
        askRout(origin.toLatLongString(), startStation.toLatLongString(), WALKING);
        askRout(startStation.toLatLongString(), destinationStation.toLatLongString(), BICYCLING);
        askRout(destinationStation.toLatLongString(), destination.toLatLongString(), WALKING);

    }

    private String routing(String origin, String destination) throws IOException {
        Location originLocation = askDestination(origin);
        Location destinationLocation = askDestination(destination);

        askStations(originLocation);
        orderStations();
        Boolean stationPossible = false;
        while(!stationPossible) {
            stationPossible = askAvailabilityForStation();
        }

        askStations(destinationLocation);
        findNearestStation();

        buildRout(originLocation,null,null,destinationLocation);


        return "";
    }
}
