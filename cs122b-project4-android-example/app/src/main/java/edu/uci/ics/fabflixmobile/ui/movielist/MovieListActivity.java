package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
import edu.uci.ics.fabflixmobile.ui.single.singleMovie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieListActivity extends AppCompatActivity {
    private final String host = "ec2-3-139-236-103.us-east-2.compute.amazonaws.com";
    private final String port = "8443";
    private final String domain = "cs122b-project1-api-example";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;
    private int total;
    private String offset;
    private String title;
    private TextView page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        final ArrayList<Movie> movies = new ArrayList<>();
        Bundle b = getIntent().getExtras();
        title = b.getString("title");
        offset = b.getString("offset");
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final Button prevButton = findViewById(R.id.prev);
        prevButton.setOnClickListener(view -> prev());
        final Button nextButton = findViewById(R.id.next);
        nextButton.setOnClickListener(view -> next());
        page = findViewById(R.id.page);
        page.setText(""+(Integer.parseInt(offset)/10 + 1));
        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/BrowseMovie.html?title="+title+"&year=&director=&star=&load_size=10&sorted=tara&offset="+offset,
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
                        Intent singleMoviePage = new Intent(MovieListActivity.this, singleMovie.class);
                        singleMoviePage.putExtra("title", movie.getName());
                        startActivity(singleMoviePage);
                    });
                },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                }) {
        };
        queue.add(searchRequest);
    }
    @SuppressLint("SetTextI18n")
    public void prev() {

        if (Integer.parseInt(offset) == 0){
            Toast.makeText(getApplicationContext(), "already at page 1", Toast.LENGTH_SHORT).show();
        } else {
            finish();
            Intent MovieListPage = new Intent(MovieListActivity.this, MovieListActivity.class);
            MovieListPage.putExtra("title", title);
            MovieListPage.putExtra("offset", "" + (Integer.parseInt(offset) - 10));
            startActivity(MovieListPage);
        }
    }

    @SuppressLint("SetTextI18n")
    public void next() {

        if (Integer.parseInt(offset) + 10 >= total){
            Toast.makeText(getApplicationContext(), "already at the last page", Toast.LENGTH_SHORT).show();
        }else {
            finish();
            Intent MovieListPage = new Intent(MovieListActivity.this, MovieListActivity.class);
            MovieListPage.putExtra("title", title);
            MovieListPage.putExtra("offset", "" + (Integer.parseInt(offset) + 10));
            startActivity(MovieListPage);
        }
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }


}