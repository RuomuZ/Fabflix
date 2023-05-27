package edu.uci.ics.fabflixmobile.ui.single;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.*;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import android.util.Log;



public class singleMovie extends AppCompatActivity {
    String movie_title;
    TextView title;
    TextView year;
    TextView director;
    TextView rating;
    TextView genres;
    TextView stars;
    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "cs122b_project1_api_example_war";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;
    //api/single-movie?title=" + movieTitle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_movie);
        title = findViewById(R.id.title);
        year = findViewById(R.id.year);
        director = findViewById(R.id.director);
        rating = findViewById(R.id.rating);
        genres = findViewById(R.id.genres);
        stars = findViewById(R.id.stars);
        Bundle b = getIntent().getExtras();
        movie_title = b.getString("title");
        title.setText(movie_title);
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/single-movie?title=" + movie_title,
                response -> {
                    Log.d("response",response);
                    JsonParser parser = new JsonParser();
                    JsonArray jsonArray = (JsonArray) parser.parse(response);
                    JsonObject movie = jsonArray.get(1).getAsJsonObject();
                    year.setText("year:\n "+movie.get("year").getAsString());
                    director.setText("director:\n "+movie.get("director").getAsString());
                    rating.setText("rating: \n"+movie.get("rating").getAsString());
                    genres.setText("genres: \n"+ movie.get("genres").getAsString());
                    stars.setText("stars:\n"+movie.get("stars").getAsString());
                },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                }) {
        };
        queue.add(searchRequest);


    }
}
