package routing;

import static spark.Spark.*;

/**
 * Created by FBeck on 25.10.2016.
 */
public class RoutenService {
    public static void main(String[] args){
        get("/hello", (req, res) -> "Hello World");
    }
}
