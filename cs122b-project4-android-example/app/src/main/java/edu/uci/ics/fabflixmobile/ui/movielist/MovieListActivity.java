package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.*;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import android.util.Log;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.search.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieListActivity extends AppCompatActivity {
    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "cs122b_project1_api_example_war";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;
    private int total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        final ArrayList<Movie> movies = new ArrayList<>();
        Bundle b = getIntent().getExtras();
        String title = b.getString("title");
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/BrowseMovie.html?title="+title+"&year=&director=&star=&load_size=10&sorted=tara&offset=0",
                response -> {
                    Log.d("response",response);
                    JsonParser parser = new JsonParser();
                    JsonArray jsonArray = (JsonArray) parser.parse(response);
                    JsonObject count = (JsonObject) jsonArray.get(0);
                    total = Integer.parseInt(count.get("total").getAsString());
                    Log.d("total records", count.get("total").getAsString());
                    for (int i = 1; i < jsonArray.size();++i){
                        JsonObject current = (JsonObject) jsonArray.get(i);
                        movies.add(new Movie(current.get("movie_title").getAsString(),
                                current.get("movie_year").getAsShort(), current.get("movie_genres").getAsString(),
                                current.get("movie_stars").getAsString(),current.get("movie_director").getAsString(),
                                current.get("movie_rating").getAsString()));
                    }
                    Log.d("first movie", "title" + movies.get(0).getName());
                    Log.d("l",""+movies.size());
                    MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
                    ListView listView = findViewById(R.id.list);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        Movie movie = movies.get(position);
                        @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    });
                },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                }) {
        };
        queue.add(searchRequest);
    }

}