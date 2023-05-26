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


@WebServlet(name = "autocomplete", urlPatterns = "/api/autocomplete")
public class autocompleteServlet extends HttpServlet {
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
    private static JsonObject generateJsonObject(String id, String title) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", title);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("id", id);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String tt = request.getParameter("query");

        try (Connection conn = dataSource.getConnection()) {
            String query = "select * from movies where match(title) against (? IN BOOLEAN MODE) limit 10;";
            PreparedStatement statement = conn.prepareStatement(query);
            String[] t = tt.split(" ");
            StringBuffer sb = new StringBuffer();
            for(int k = 0; k < t.length; k++) {
                sb.append("+" + t[k] + "* ");
            }
            String str = sb.toString();
            System.out.println(str);
            statement.setString(1,str);
            ResultSet rs = statement.executeQuery();
            JsonArray jsonArray = new JsonArray();
            while (rs.next()) {
                jsonArray.add(generateJsonObject(rs.getString("id"),rs.getString("title")));
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


