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
import java.sql.Statement;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "BrowseMovie", urlPatterns = "/api/BrowseMovie.html")
public class BrowseMovie extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        String genre = request.getParameter("genre");
        System.out.print(year);
        String cha = request.getParameter("char");
        String load = request.getParameter("load_size");
        String offset = request.getParameter("offset");
        String order = request.getParameter("sorted");

        try (Connection conn = dataSource.getConnection()) {
            String query = "select u.title, u.year, u.director, v.rating, (select group_concat(distinct v.name order by v.name separator \",\") from (select x.name from stars as x, stars_in_movies as y where y.movieId = u.id and y.starId = x.id order by x.name limit 3) as v) as movie_stars, (select group_concat(distinct v.name order by v.name separator \",\") from (select x.name from genres as x, genres_in_movies as y where y.movieId = u.id and y.genreId = x.id order by x.name limit 3) as v) as movie_genres from movies as u, ratings as v ";
            int flag = 0;
            if (star != null&& !star.equals(""))
            {
                query+=", stars as k,stars_in_movies as l ";
            }
            query += "where ";
            if(title != null&& !title.equals("")) {query += "u.title like " + "\"%" + title + "%\"";flag = 1;}
            if(year != null&& !year.equals("")) {
                if (flag == 0){
                    flag = 1;
                } else {
                    query += " and ";
                }
                query += "u.year = " + "\"" + year + "\"";
            }
            if(director != null&& !director.equals("")) {
                if (flag == 0){
                    flag = 1;
                } else {
                    query += " and ";
                }
                query += "u.director like " + "\"%" + director + "%\"";
            }
            if(star != null&& !star.equals("")) {
                if (flag == 0){
                    flag = 1;
                } else {
                    query += " and ";
                }
                query += "k.name like " + "\"%" + star + "%\" " + " and k.id = l.starId and l.movieId = u.id";
            }
            if (genre != null)
            {
                query = "select u.title, u.year, u.director, v.rating, (select group_concat(distinct v.name order by v.name separator \",\") from (select x.name from stars as x, stars_in_movies as y where y.movieId = u.id and y.starId = x.id order by x.name limit 3) as v) as movie_stars, (select group_concat(distinct v.name order by v.name separator \",\") from (select x.name from genres as x, genres_in_movies as y where y.movieId = u.id and y.genreId = x.id order by x.name limit 3) as v) as movie_genres from movies as u, ratings as v, genres as g, genres_in_movies as l where ";
                query += String.format("g.name = \"%s\" ", genre);
                query += " and g.id = l.genreId and l.movieId = u.id";
                flag = 1;
            }
            if (cha != null)
            {
                if (cha.equals("*"))
                {
                    query = "select u.title, u.year, u.director, v.rating, (select group_concat(distinct v.name order by v.name separator \",\") from (select x.name from stars as x, stars_in_movies as y where y.movieId = u.id and y.starId = x.id order by x.name limit 3) as v) as movie_stars, (select group_concat(distinct v.name order by v.name separator \",\") from (select x.name from genres as x, genres_in_movies as y where y.movieId = u.id and y.genreId = x.id order by x.name limit 3) as v) as movie_genres from movies as u, ratings as v where ";
                    query += "u.title not regexp  \"^[a-zA-Z0-9]\"";
                    flag = 1;
                }
                else {
                    query = "select u.title, u.year, u.director, v.rating, (select group_concat(distinct v.name order by v.name separator \",\") from (select x.name from stars as x, stars_in_movies as y where y.movieId = u.id and y.starId = x.id order by x.name limit 3) as v) as movie_stars, (select group_concat(distinct v.name order by v.name separator \",\") from (select x.name from genres as x, genres_in_movies as y where y.movieId = u.id and y.genreId = x.id order by x.name limit 3) as v) as movie_genres from movies as u, ratings as v where ";
                    query += "u.title like " + "\"" + cha + "%\"";
                    flag = 1;
                }
            }
            if (flag == 1) {query += " and ";}
            if (session.getAttribute(title+year+director+star+genre+cha) == null)

            {
                System.out.println("attribute not exist");
                String query2 = query +"u.id = v.MovieId;";
                query2 = query2.replace("u.title, u.year, u.director, v.rating, (select group_concat(distinct v.name order by v.name separator \",\") from (select x.name from stars as x, stars_in_movies as y where y.movieId = u.id and y.starId = x.id order by x.name limit 3) as v) as movie_stars, (select group_concat(distinct v.name order by v.name separator \",\") from (select x.name from genres as x, genres_in_movies as y where y.movieId = u.id and y.genreId = x.id order by x.name limit 3) as v) as movie_genres ", "count(*) as total ");
                System.out.println("query 2 = " + query2);
                PreparedStatement statement = conn.prepareStatement(query2);
                ResultSet rs = statement.executeQuery();
                String t = "";
                if (rs.next()){
                    t = rs.getString("total");
                }
                System.out.println("set total to " + t);
                session.setAttribute(title+year+director+star+genre+cha, t);
                statement.close();
                rs.close();
            }
            String total = (String) session.getAttribute(title+year+director+star+genre+cha);
            System.out.println("get total " + total);
            query += "u.id = v.MovieId ";
            if (order.equals("tara"))
            {
                query += " order by title ASC,rating ASC ";
            }
            else if (order.equals("tard")){
                query += " order by title ASC,rating DESC ";
            }
            else if(order.equals("tdra")){
                query += " order by title DESC,rating ASC ";
            }
            else if (order.equals("tdrd")){
                query += " order by title DESC,rating DESC ";

            }else if (order.equals("rata")){
                query += " order by rating ASC,title ASC ";

            }else if (order.equals("ratd")){
                query += " order by rating ASC,title DESC ";

            }else if (order.equals("rdta")){
                query += " order by rating DESC,title ASC ";
            }else if (order.equals("rdtd")){
                query += ", order by rating DESC,title DESC ";
            }
            query += " limit " + load + " offset " + offset + ";";
            System.out.println(query);
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            JsonArray jsonArray = new JsonArray();
            JsonObject jsonNum = new JsonObject();
            jsonNum.addProperty("total",total);
            jsonArray.add(jsonNum);
            while (rs.next()) {
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");
                String movie_genres = rs.getString("movie_genres");
                String movie_stars = rs.getString("movie_stars");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_stars", movie_stars);
                jsonObject.addProperty("movie_genres", movie_genres);
                jsonObject.addProperty("movie_rating", movie_rating);
                System.out.println(jsonObject);
                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();
            request.getServletContext().log("getting " + jsonArray.size() + " results");
            out.write(jsonArray.toString());
            response.setStatus(200);
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        } finally {
            out.close();
        }

    }
}
