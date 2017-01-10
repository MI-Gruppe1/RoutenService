package routing;

import routing.rest.endpoint.Routing;

import java.io.IOException;

/**
 * Created by FBeck on 25.10.2016.
 */
public class RoutenService {
    private static String geocodeKey = "AIzaSyDMvwHv9F7evXsKaDGdIhKNUyjsyviV4aU";
    private static String directionsKey = "AIzaSyAWbOGw9GOWPE3PgytbNiNh011aw8_L2bQ";


    public static void main(String[] args) throws IOException {

        Routing routing = new Routing("https://maps.googleapis.com/", "http://stadtraddbservice:6000/", "http://localhost:5000/", geocodeKey, directionsKey);
        routing.startRouting();
        
    }
}
