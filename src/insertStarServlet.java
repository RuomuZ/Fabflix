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
import java.sql.*;

import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "insertStarServlet", urlPatterns = "/api/insertStar")
public class insertStarServlet extends HttpServlet {

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


        String star = request.getParameter("star");
        String birthday = request.getParameter("birthday");
        System.out.println(star + "   " + birthday);
        try (Connection conn = dataSource.getConnection()) {


            String query = "call insert_star(?,?,?);";
            CallableStatement statement = conn.prepareCall(query);
            statement.setString(1, star);
            if (birthday.equals("")){
                statement.setNull(2, Types.NULL);
            }
            else{
                statement.setInt(2, Integer.parseInt(birthday));
            }
            statement.registerOutParameter(3,java.sql.Types.VARCHAR);
            statement.executeUpdate();
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "insert star successfully, new id is " + statement.getString(3));


            response.getWriter().write(responseJsonObject.toString());
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            System.out.println(jsonObject.toString());
            response.setStatus(500);
        }
    }
}
