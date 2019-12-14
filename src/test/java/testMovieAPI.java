import com.google.gson.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Test;

import java.util.ArrayList;

public class testMovieAPI {
    @Test
    public void testGetAllMoviesByGivenName() {
        //Getting All Movies From API, Under "BySearch" methods.
        Response allMoviesResponse = RestAssured.get("http://www.omdbapi.com/?s=Harry+Potter&apikey=f344f3ab");
        System.out.println("allMoviesResponse Status Code: " + allMoviesResponse.getStatusCode());
        String moviesResponseBody = allMoviesResponse.getBody().asString();

        //Used Library Objects
        JsonParser jsonParser = new JsonParser();
        Gson googleJson = new Gson();
        Movie selectedMovie = new Movie();

        //Deserialization API response to a list object
        JsonObject moviesResponseBodyJsonObject = (JsonObject) jsonParser.parse(moviesResponseBody);
        JsonArray allMovies = moviesResponseBodyJsonObject.getAsJsonArray("Search");
        ArrayList MovieList = googleJson.fromJson(allMovies, ArrayList.class);

        //Searching movie has "Harry Potter and the Sorcerer's Stone" title in all movies
        for (var movie : MovieList) {
            var object = googleJson.fromJson(googleJson.toJson(movie), Movie.class);
            //When found movie, set it to an object
            if (object.Title.contains("Harry Potter and the Sorcerer's Stone")) {
                selectedMovie = object;
            }
        }

        if (selectedMovie.imdbID == null) {
            throw null;
        } else {
            //Printing founded movie info.
            System.out.println("---------------------------------");
            System.out.println("Title: " + selectedMovie.Title);
            System.out.println("IMDB ID: " + selectedMovie.imdbID);

            //Searching movie via founded movies imdbID under "ByIDorTitle" methods.
            Response movieResponse = RestAssured.get("http://www.omdbapi.com/?i=" + selectedMovie.imdbID + "&apikey=f344f3ab");

            //Printing response info.
            System.out.println("---------------------------------");
            System.out.println("movieResponse Status Code: " + movieResponse.statusCode());

            //Converting movie on response into an object and print info.
            JsonObject movieObject = jsonParser.parse(movieResponse.getBody().asString()).getAsJsonObject();
            String title = movieObject.get("Title").getAsString();
            String year = movieObject.get("Year").getAsString();
            String released = movieObject.get("Released").getAsString();
            System.out.println("Title: " + title + " Year: " + year + " Released: " + released);
        }
    }
}

