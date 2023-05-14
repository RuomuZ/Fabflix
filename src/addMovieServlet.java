import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "addMovieServlet", urlPatterns = "/api/addMovie")
public class addMovieServlet extends HttpServlet {

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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject responseJsonObject = new JsonObject();

        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        String genre = request.getParameter("genre");
        try (Connection conn = dataSource.getConnection()) {

            String query = "call add_movie(?,?,?,?,null,?);";
            System.out.println(query);
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, title);
            statement.setInt(2, Integer.parseInt(year));
            statement.setString(3, director);
            statement.setString(4, star);
            statement.setString(5, genre);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", rs.getString("message"));
            }
            else {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "fail somehow");
            }

            response.getWriter().write(responseJsonObject.toString());
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            System.out.println(jsonObject.toString());
            response.setStatus(500);
        }
    }
}
