package routing.rest.endpoint;

import retrofit2.Call;
import retrofit2.Response;
import routing.rest.call.google.GoogleApi;
import routing.rest.call.google.classes.AddressConversationAnswer;
import routing.rest.call.google.classes.Location;
import routing.rest.call.google.classes.RoutingAnswer;
import routing.rest.call.services.classes.Station;

import java.io.IOException;
import java.util.ArrayList;

import static spark.Spark.*;

/**
 * Created by FBeck on 08.11.2016.
 */
public class Routing {
    public static final double R = 6372.8;//Erdradius in km

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

    private ArrayList<Station> orderStations(ArrayList<Station> stationen, double latWaypoint, double lngWaypoint){

        ArrayList<Station> nearestStations = new ArrayList<Station>();
        boolean firstrun = true;
        double minDistancen = 0;

        for(Station s:stationen){
            double distanceBetween = haversine(s.getLatitude(), s.getLongitude(), latWaypoint, lngWaypoint);

            if(firstrun){
                nearestStations.add(0, s);
                minDistancen = distanceBetween;
                firstrun = false;
            }else if (distanceBetween < minDistancen){
                nearestStations.add(0, s);
                minDistancen = distanceBetween;
            }else{
                for(int i = 0; i <= nearestStations.size();i++){
                    if(distanceBetween < haversine(nearestStations.get(i).getLatitude(), nearestStations.get(i).getLongitude(), latWaypoint, lngWaypoint)){
                        nearestStations.add(i, s);
                    }else if(nearestStations.get(i+1).equals(null)){
                        nearestStations.add(i+1, s);
                    }
                }
            }
        }

        return nearestStations;
    }

    /*
     * Berechnet die Entfernung zwischen zwei Punkten mit Lat/Long
     */
    private double haversine(double latStation, double lngStation, double latWaypoint, double lngWaypoint){
        double dLat = Math.toRadians(latWaypoint - latStation);
        double dLon = Math.toRadians(lngWaypoint - lngStation);
        latStation = Math.toRadians(latStation);
        latWaypoint = Math.toRadians(latWaypoint);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(latStation) * Math.cos(latWaypoint);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

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

        //askStations(originLocation);
        orderStations(askStations(originLocation), originLocation.getLat(), originLocation.getLng());
        orderStations(askStations(destinationLocation), destinationLocation.getLat(), destinationLocation.getLng());
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
