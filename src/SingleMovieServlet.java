import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }



    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String title = request.getParameter("title");
        System.out.println(title);
        // The log message can be found in localhost log
        request.getServletContext().log("getting title: " + title);
        HttpSession session = request.getSession();
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "select u.title, u.year, u.director, v.rating, (select group_concat(distinct v.name order by v.name separator \",\") from (select x.name from stars as x, stars_in_movies as y where y.movieId = u.id and y.starId = x.id) as v) as movie_stars, (select group_concat(distinct v.name order by v.name separator \",\") from (select x.name from genres as x, genres_in_movies as y where y.movieId = u.id and y.genreId = x.id) as v) as movie_genres from movies as u, ratings as v where u.title = ? and u.id = v.MovieId;";


            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, title);

            // Perform the query
            ResultSet rs = statement.executeQuery();
            JsonArray jsonArray = new JsonArray();
            String back_url = (String) session.getAttribute("previousURL");
            JsonObject jsonO = new JsonObject();
            jsonO.addProperty("back",back_url);
            System.out.println(jsonO);
            jsonArray.add(jsonO);

            // Iterate through each row of rs
            while (rs.next()) {
                System.out.println(rs.getString("movie_stars"));
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_stars = rs.getString("movie_stars");
                String movie_genres = rs.getString("movie_genres");
                String movie_rating = rs.getString("rating");
                String movie_director = rs.getString("director");
                JsonObject x = new JsonObject();
                x.addProperty("title", movie_title);
                x.addProperty("year", movie_year);
                x.addProperty("director", movie_director);
                x.addProperty("stars", movie_stars);
                x.addProperty("genres", movie_genres);
                x.addProperty("rating", movie_rating);
                jsonArray.add(x);
            }
            rs.close();
            statement.close();
            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }



    }

}

