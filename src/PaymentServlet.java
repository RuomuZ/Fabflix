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
import java.util.ArrayList;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/pay")
public class PaymentServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (Connection conn = dataSource.getConnection()) {
            String first_name = request.getParameter("first_name");
            String last_name = request.getParameter("last_name");
            String cc = request.getParameter("cc");
            String exp = request.getParameter("exp");
            JsonObject responseJsonObject = new JsonObject();
            String query = "select * from creditcards where id = ? and firstName = ? and lastName = ? and expiration=?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, cc);
            statement.setString(2, first_name);
            statement.setString(3, last_name);
            statement.setString(4, exp);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                request.getSession().setAttribute("previousOrder",request.getSession().getAttribute("previousItems"));
                request.getSession().setAttribute("previousItems",new ArrayList<String>());
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success payed");


            } else {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "fail payed");
                request.getServletContext().log("Payment failed");
            }
            response.getWriter().write(responseJsonObject.toString());
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("message", "fail!");
            jsonObject.addProperty("errorMessage", e.getMessage());
            System.out.println(jsonObject.toString());
            response.getWriter().write(jsonObject.toString());
            response.setStatus(500);
        }
    }
}
