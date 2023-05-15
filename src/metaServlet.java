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


@WebServlet(name = "metaServlet", urlPatterns = "/api/meta")
public class metaServlet extends HttpServlet {
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


        try (Connection conn = dataSource.getConnection()) {
            String query = "show tables;";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            ArrayList<String> tablenames = new ArrayList<String>();
            JsonArray jsonArray = new JsonArray();
            while (rs.next()) {
                tablenames.add(rs.getString("Tables_in_moviedb"));
            }
            rs.close();

            for (String i : tablenames)
            {
                JsonObject table = new JsonObject();
                table.addProperty("table",i);
                JsonArray fields = new JsonArray();
                String query2 = "describe "+ i + ";";
                ResultSet rs1 = statement.executeQuery(query2);
                while (rs1.next()) {
                    JsonObject eachField = new JsonObject();
                    eachField.addProperty("field",rs1.getString("Field"));
                    eachField.addProperty("type",rs1.getString("Type"));
                    fields.add(eachField);
                }
                rs1.close();
                table.addProperty("fields",fields.toString());
                jsonArray.add(table);
            }
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

