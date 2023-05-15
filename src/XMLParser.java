import java.sql.*;
import java.util.HashMap;
import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.opencsv.CSVWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;


public class XMLParser  extends DefaultHandler {
    static int mode = 0;  //0 means mains243.xml, 1 means casts124.xml, 2 means actors63.xml.
    HashMap<String,Integer> exist_movies;
    HashMap<String,Integer> exist_stars;
    HashMap<String,Integer> exist_genres;
    ArrayList<Movie> movies;
    ArrayList<Actor> actors;
    ArrayList<String> genres;
    String tempVal;
    Movie tempMovie;
    Actor tempActor;
    String currentDirector;
    FileWriter inconsistency;
    int star_id_index;

    public XMLParser() {
        exist_movies = new HashMap<>();
        exist_stars = new HashMap<>();
        exist_genres = new HashMap<>();
        movies = new ArrayList<>();
        actors = new ArrayList<>();
        genres = new ArrayList<>();
        star_id_index = 0;
        try {
            inconsistency  =new FileWriter("inconsistency.txt");
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'") || data.contains("\\\'")) {
            data = data.replace("\"", "");
            data = data.replace(",", "");
            data = data.replace("\\\'", "");
            escapedData = data;
        }
        return escapedData;
    }

    void prepareHashTable() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        Statement statement = connection.createStatement();
        String query = "select title from movies;";
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()){
            exist_movies.put(rs.getString("title"),-1);
        }
        rs.close();
        query = "select id,name from stars;";
        ResultSet rs1 = statement.executeQuery(query);
        while (rs1.next()){
            exist_stars.put(rs1.getString("name"),-1);
        }
        rs1.close();
        query = "select id,name from genres;";
        ResultSet rs2 = statement.executeQuery(query);
        while (rs2.next()){
            exist_genres.put(rs2.getString("name"),Integer.parseInt(rs2.getString("id")));
        }
        rs2.close();
        statement.close();
        System.out.println("size of exists movies:" + exist_movies.size());
        System.out.println("size of exists stars:" + exist_stars.size());
        System.out.println("size of exists genres:" + exist_genres.size());
    }
    public void run() throws IOException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        prepareHashTable();
        parseDocument();
        close_incon();
        insertData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();
            mode = 0;
            InputSource s = new InputSource("stanford-movies/mains243.xml");
            s.setEncoding("ISO-8859-1");
            sp.parse(s, this);
            mode = 1;
            s = new InputSource("stanford-movies/actors63.xml");
            s.setEncoding("ISO-8859-1");
            sp.parse(s, this);
            mode = 2;
            s = new InputSource("stanford-movies/casts124.xml");
            s.setEncoding("ISO-8859-1");
            sp.parse(s, this);
            FileWriter load_movies = new FileWriter("load_movies.csv");
            CSVWriter lm = new CSVWriter(load_movies);
            FileWriter load_genres = new FileWriter("load_genres.csv");
            CSVWriter lg = new CSVWriter(load_genres);
            FileWriter load_stars = new FileWriter("load_stars.csv");
            CSVWriter ls = new CSVWriter(load_stars);
            FileWriter load_stars_in_movies = new FileWriter("load_stars_in_movies.csv");
            FileWriter load_genres_in_movies = new FileWriter("load_genres_in_movies.csv");
            for (String i: genres){
                String[] row = {i};
                lg.writeNext(row);
            }
            for (Movie i : movies){
                String[] row = {i.getId(),i.getTitle(),""+i.getYear(),i.getDirector()};
                lm.writeNext(row);
            }
            lm.close();
            for (Actor i : actors){
                String m;
                if (i.getDob() == -1){
                    m = null;
                }else{
                    m = ""+i.getDob();
                }
                String[] row = {i.getId(),escapeSpecialCharacters(i.getName()),m};
                ls.writeNext(row);
            }
            ls.close();

            load_genres.close();

            load_stars_in_movies.close();
            load_genres_in_movies.close();

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void insertData() {
        try {
            String loginUser = "mytestuser";
            String loginPasswd = "My6$Password";
            String loginUrl = "jdbc:mysql://localhost:3306/moviedb?allowLoadLocalInfile=true";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            Statement statement = connection.createStatement();
            String query = "LOAD DATA local INFILE 'load_movies.csv' " +
                    "INTO TABLE movies " +
                    "FIELDS TERMINATED BY ',' ENCLOSED BY '\"'";
            boolean rs = statement.execute(query);
            query = "LOAD DATA local INFILE 'load_stars.csv' " +
                    "INTO TABLE stars " +
                    "FIELDS TERMINATED BY ',' ENCLOSED BY '\"'";
            rs = statement.execute(query);
        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (mode == 0)
        {
            if (qName.equalsIgnoreCase("directorfilms")) {
                currentDirector = "";
            } else if (qName.equalsIgnoreCase("film")){
                tempMovie = new Movie();
            }
        }else if (mode == 1){
            if (qName.equalsIgnoreCase("actor")){
                tempActor = new Actor();
            }
        } else if (mode == 2){

        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (mode == 0) {
            if (qName.equalsIgnoreCase("dirname")) {
                currentDirector = tempVal;
            } else if (tempMovie != null){
                if (qName.equalsIgnoreCase("fid")) {
                    tempMovie.setId(tempVal);
                } else if (qName.equalsIgnoreCase("film")) {
                    tempMovie.setDirector(currentDirector);
                    exist_movies.put(tempMovie.getTitle(),movies.size());
                    movies.add(tempMovie);
                }
                else if (qName.equalsIgnoreCase("t")) {
                    if (exist_movies.containsKey(tempVal)){
                        tempMovie = null;
                    }else{tempMovie.setTitle(tempVal);}
                } else if (qName.equalsIgnoreCase("year")) {
                    try{
                        tempMovie.setYear(Integer.parseInt(tempVal));
                    }catch (NumberFormatException e){
                        //e.printStackTrace();
                        try {
                            inconsistency.write(tempMovie.getId() + tempMovie.getTitle() + " has inconsistent year " + tempVal + "\n");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        tempMovie = null;
                    }

                } else if (qName.equalsIgnoreCase("cat")) {
                    tempMovie.setGenre(tempVal);
                    if (!exist_genres.containsKey(tempVal)){
                        exist_genres.put(tempVal,exist_genres.size());
                        genres.add(tempVal);
                        System.out.println(tempVal);
                    }
                }
            }
        } else if (mode == 1){
            if (tempActor != null){
                if (qName.equalsIgnoreCase("stagename")){
                    if (exist_stars.containsKey(tempVal)){
                        tempActor = null;
                    }
                    else{
                        tempActor.setName(tempVal);
                    }
                } else if (qName.equalsIgnoreCase("dob")){
                    try{
                        tempActor.setDob(Integer.parseInt(tempVal));
                    }catch (NumberFormatException e){
                        //e.printStackTrace();
                        try {
                            inconsistency.write(tempActor.getName() + " has inconsistent dob " + tempVal + ", set to null\n");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        tempActor.setDob(-1);
                    }
                } else if (qName.equalsIgnoreCase("actor")){
                    String new_id = "xml" + star_id_index;
                    star_id_index += 1;
                    tempActor.setId(new_id);
                    exist_stars.put(tempActor.getName(),actors.size());
                    actors.add(tempActor);
                }
            }
        } else if (mode == 2){

        }
    }

    void close_incon() throws IOException {
        inconsistency.close();
    }

    public static void main(String[] args) {
        XMLParser spe = new XMLParser();
        try {
            spe.run();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
