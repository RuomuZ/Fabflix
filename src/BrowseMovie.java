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
import java.util.ArrayList;


@WebServlet(name = "BrowseMovie", urlPatterns = "/api/BrowseMovie.html")
public class BrowseMovie extends HttpServlet {
    private static final long serialVersionUID = 1L;

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
        String url = request.getRequestURL().toString();
        String para = request.getQueryString();
        ArrayList<String> paras = new ArrayList<String>();
        if (para != null) {
            url += "?" + para.toString();
        }
        url = url.replace("api/", "");
        session.setAttribute("previousURL", url);

        try (Connection conn = dataSource.getConnection()) {
            String query = "select u.title, u.year, u.director, v.rating, (select group_concat(distinct v.name order by v.name separator \",\") from (select x.name from stars as x, stars_in_movies as y where y.movieId = u.id and y.starId = x.id order by x.name limit 3) as v) as movie_stars, (select group_concat(distinct v.name order by v.name separator \",\") from (select x.name from genres as x, genres_in_movies as y where y.movieId = u.id and y.genreId = x.id order by x.name limit 3) as v) as movie_genres from movies as u, ratings as v ";
            int flag = 0;
            if (star != null&& !star.equals(""))
            {
                query+=", stars as k,stars_in_movies as l ";
            }
            query += "where ";
            if(title != null&& !title.equals("")) {

                query += "(match(title) against (? IN BOOLEAN MODE)";
                //for fuzzy search
                query += " or edth(?, title, ?))";
                //
                flag = 1;
                paras.add("title");
                paras.add("fuzzy");
                paras.add("norm_fuzzy");
            }
            if(year != null&& !year.equals("")) {
                if (flag == 0){
                    flag = 1;
                } else {
                    query += " and ";
                }
                query += "u.year = ?";
                paras.add("year");
            }
            if(director != null&& !director.equals("")) {
                if (flag == 0){
                    flag = 1;
                } else {
                    query += " and ";
                }
                    query += "u.director like ?";
                paras.add("director");
            }
            if(star != null&& !star.equals("")) {
                if (flag == 0){
                    flag = 1;
                } else {
                    query += " and ";
                }
                    query += "k.name like ? and k.id = l.starId and l.movieId = u.id";
                paras.add("star");
            }
            if (genre != null)
            {
                query = "select u.title, u.year, u.director, v.rating, " +
                        "(select group_concat(distinct v.name order by v.name separator \",\") from (select x.name from stars as x, " +
                        "stars_in_movies as y where y.movieId = u.id and y.starId = x.id order by x.name limit 3) as v) as movie_stars, " +
                        "(select group_concat(distinct v.name order by v.name separator \",\") " +
                        "from (select x.name from genres as x, genres_in_movies as y where y.movieId = u.id" +
                        " and y.genreId = x.id order by x.name limit 3) as v) as movie_genres from movies as u," +
                        " ratings as v, genres as g, genres_in_movies as l where g.name = ?  and g.id = l.genreId and l.movieId = u.id";
               paras.add("genre");
                // query += String.format("g.name = \"%s\" ", genre);
               // query += " and g.id = l.genreId and l.movieId = u.id";
                flag = 1;
            }
            if (cha != null)
            {
                if (cha.equals("*"))
                {
                    query = "select u.title, u.year, u.director, v.rating, " +
                            "(select group_concat(distinct v.name order by v.name separator \",\")" +
                            " from (select x.name from stars as x, stars_in_movies as y where y.movieId = u.id" +
                            " and y.starId = x.id order by x.name limit 3) as v) as movie_stars," +
                            " (select group_concat(distinct v.name order by v.name separator \",\")" +
                            " from (select x.name from genres as x, genres_in_movies as y where y.movieId" +
                            " = u.id and y.genreId = x.id order by x.name limit 3) as v) as movie_genres" +
                            " from movies as u, ratings as v where ";
                    query += "u.title not regexp  \"^[a-zA-Z0-9]\"";
                    flag = 1;
                }
                else {
                    query = "select u.title, u.year, u.director, v.rating, (select group_concat(distinct v.name order by v.name separator \",\") from (select x.name from stars as x, stars_in_movies as y where y.movieId = u.id and y.starId = x.id order by x.name limit 3) as v) as movie_stars, (select group_concat(distinct v.name order by v.name separator \",\") from (select x.name from genres as x, genres_in_movies as y where y.movieId = u.id and y.genreId = x.id order by x.name limit 3) as v) as movie_genres from movies as u, ratings as v where ";
                    query += "u.title like ?";
                    paras.add("cha");
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
                //prepare statement to count records
                PreparedStatement statement = conn.prepareStatement(query2);
                for (int i = 0; i < paras.size(); ++i){
                    if (paras.get(i).equals("title")){
                        String[] t = title.split(" ");
                        StringBuffer sb = new StringBuffer();
                        for(int k = 0; k < t.length; k++) {
                            sb.append("+" + t[k] + "* ");
                        }
                        String str = sb.toString();
                        System.out.println(str);
                        statement.setString(i+1,str);
                    }
                    else if (paras.get(i).equals("fuzzy")){
                        statement.setString(i+1,title);
                    }
                    else if (paras.get(i).equals("norm_fuzzy")){
                        statement.setInt(i+1,title.length()/2);
                    }
                    else if (paras.get(i).equals("year")){
                        statement.setString(i+1,year);
                    }
                    else if (paras.get(i).equals("director")){
                        statement.setString(i+1,"%" + director + "%");
                    }
                    else if (paras.get(i).equals("star")){
                        statement.setString(i+1,"%" + star + "%");
                    }
                    else if (paras.get(i).equals("genre")){
                        statement.setString(i+1,genre);
                    }
                    else if (paras.get(i).equals("cha")){
                        statement.setString(i+1,"" + cha + "%");
                    }
                }

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
            paras.add("load");
            paras.add("offset");
            query += " limit ? offset ?;";
            System.out.println(query);
            PreparedStatement statement = conn.prepareStatement(query);

            for (int i = 0; i < paras.size(); ++i){
                if (paras.get(i).equals("title")){
                    String[] t = title.split(" ");
                    StringBuffer sb = new StringBuffer();
                    for(int k = 0; k < t.length; k++) {
                        sb.append("+" + t[k] + "* ");
                    }
                    String str = sb.toString();
                    statement.setString(i+1,str);
                }
                else if (paras.get(i).equals("fuzzy")){
                    statement.setString(i+1,title);
                }
                else if (paras.get(i).equals("norm_fuzzy")){
                    statement.setInt(i+1,title.length()/2);
                }
                else if (paras.get(i).equals("year")){
                    statement.setString(i+1,year);
                }
                else if (paras.get(i).equals("director")){
                    statement.setString(i+1,"%" + director + "%");
                }
                else if (paras.get(i).equals("star")){
                    statement.setString(i+1,"%" + star + "%");
                }
                else if (paras.get(i).equals("genre")){
                    statement.setString(i+1,genre);
                }
                else if (paras.get(i).equals("cha")){
                    statement.setString(i+1,cha + "%");
                }
                else if (paras.get(i).equals("load")){
                    statement.setInt(i+1,Integer.parseInt(load));
                }
                else if (paras.get(i).equals("offset")){
                    statement.setInt(i+1,Integer.parseInt(offset));
                }
            }

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
