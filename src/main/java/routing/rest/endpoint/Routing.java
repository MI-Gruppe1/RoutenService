package routing.rest.endpoint;

import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Response;
import routing.rest.call.google.GoogleApi;
import routing.rest.call.google.classes.AddressConversationAnswer;
import routing.rest.call.google.classes.Location;
import routing.rest.call.google.classes.RoutingAnswer;
import routing.rest.call.services.classes.Station;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static spark.Spark.*;

/**
 * Created by FBeck on 08.11.2016.
 */
public class Routing {
    public static final double R = 6372.8;//Erdradius in km

    private Gson gson;
    private GoogleApi google;

    private String geocodeKey;
    private String directionsKey;

    private String BICYCLING = "bicycling";
    private String WALKING = "walking";

    public Routing(Gson gson, GoogleApi google, String geocodeKey, String directionsKey){
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

            return gson.toJson(routing(origin, destination));
        });
    }

    public Location askDestination(String wayPoint) throws IOException {
        Call<AddressConversationAnswer> call = google.convert(wayPoint, geocodeKey);
        Response<AddressConversationAnswer> answer = call.execute();
        return answer.body().getResults().get(0).getGeometry().getLocation();
    }

    private RoutingAnswer askRout(String origin, String destination, String mode) throws IOException {
        Call<RoutingAnswer> call = google.rout(origin,destination,mode, directionsKey);
        Response<RoutingAnswer> answer = call.execute();

        return null;
    }

    private List<Station> askStations(Location location){
        //TODO
        return new ArrayList<>();
    }

    public List<Station> orderStations(List<Station> stationen, double latWaypoint, double lngWaypoint){

        List<Station> nearestStations = new ArrayList<Station>();
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

    private Station findNearestStation(List<Station> stationen, Location startLocation, Location endLocation, boolean withAbailability){
        List<Station> startLocList = orderStations(stationen, startLocation.getLat(), startLocation.getLng());
        List<Station> endLocList   = orderStations(stationen, endLocation.getLat(), endLocation.getLng());
        boolean found = false;
        Station currentStation = startLocList.get(0);
        for (int i = 0;(i <= startLocList.size())||found; i++){
            for(int j = 0; (j <= 2)||found; j++){
                if(endLocList.get(i).getName().equals(startLocList.get(j).getName())){
                    if(withAbailability && askAvailabilityForStation(endLocList.get(i))) {
                        found = true;
                        currentStation = endLocList.get(i);
                    } else if(!withAbailability) {
                        found = true;
                        currentStation = endLocList.get(i);
                    }
                }
            }
        }
        return currentStation;
    }

    private Boolean askAvailabilityForStation(Station station){
        //TODO
        return true;
    }

    private Boolean processAvailabilityOfStation(Station station) {return true; }

    private Rout buildRout(Location origin, Location startStation, Location destinationStation, Location destination) throws IOException {
        RoutingAnswer startToFirst = askRout(origin.toLatLongString(), startStation.toLatLongString(), WALKING);
        RoutingAnswer firstToSecond = askRout(startStation.toLatLongString(), destinationStation.toLatLongString(), BICYCLING);
        RoutingAnswer secondToDestination = askRout(destinationStation.toLatLongString(), destination.toLatLongString(), WALKING);

        return new Rout(startToFirst, firstToSecond, secondToDestination);
    }

    private Rout routing(String origin, String destination) throws IOException {
        Location originLocation = askDestination(origin);
        Location destinationLocation = askDestination(destination);

        Station originStation = findNearestStation(askStations(originLocation),originLocation,destinationLocation,true);
        Station destinationStation = findNearestStation(askStations(destinationLocation), destinationLocation, originLocation,false);

        return buildRout(originLocation,originStation.toLocation(),destinationStation.toLocation(),destinationLocation);
    }
}
