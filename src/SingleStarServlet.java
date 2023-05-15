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


@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;


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


        String id = request.getParameter("name");


        request.getServletContext().log("getting id: " + id);
        HttpSession session = request.getSession();

        PrintWriter out = response.getWriter();


        try (Connection conn = dataSource.getConnection()) {


            // Construct a query with parameter represented by "?"
            String query = "select u.name, u.birthYear, (select group_concat(distinct v.title order by v.title separator \",\") from (select x.title from movies as x, stars_in_movies as y where y.starId = u.id and y.movieId = x.id) as v) as allMovies from stars as u where u.name = ?;";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

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
                String starName = rs.getString("name");
                String starDob = rs.getString("birthYear");
                String allMovies = rs.getString("allMovies");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("star_name", starName);
                jsonObject.addProperty("star_dob", starDob);
                jsonObject.addProperty("movie_title", allMovies);
                jsonArray.add(jsonObject);
                System.out.println(jsonObject);
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

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

}
