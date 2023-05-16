import java.sql.*;
import java.util.HashMap;
import java.util.Map;
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
    HashMap<String,Movie> new_movies;
    HashMap<String,String> exist_stars;
    HashMap<String,Actor> new_stars;
    HashMap<String,Integer> exist_genres;
    //ArrayList<Movie> movies;
    ArrayList<Actor> actors;
    ArrayList<String> genres;
    String tempVal;
    Movie tempMovie;
    Actor tempActor;
    String currentDirector;
    FileWriter inconsistency;
    int star_id_index;
    String currentStar;
    String currentMovie;


    public XMLParser() {
        exist_movies = new HashMap<>();
        new_movies = new HashMap<>();
        exist_stars = new HashMap<>();
        new_stars = new HashMap<>();
        exist_genres = new HashMap<>();
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



    void prepareHashTable() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        connection.setAutoCommit(false);
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
            exist_stars.put(rs1.getString("name"),rs1.getString("id"));
        }
        rs1.close();
        query = "select id,name from genres;";
        ResultSet rs2 = statement.executeQuery(query);
        while (rs2.next()){
            exist_genres.put(rs2.getString("name"),Integer.parseInt(rs2.getString("id")));
        }

        rs2.close();
        connection.commit();
        statement.close();
        System.out.println("Tom Cr " + exist_stars.get("Tom Cruise"));
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

            //write csv file to load
            FileWriter load_movies = new FileWriter("load_movies.csv");
            CSVWriter lm = new CSVWriter(load_movies);
            FileWriter load_genres = new FileWriter("load_genres.csv");
            CSVWriter lg = new CSVWriter(load_genres);
            FileWriter load_stars = new FileWriter("load_stars.csv");
            CSVWriter ls = new CSVWriter(load_stars);
            FileWriter load_stars_in_movies = new FileWriter("load_stars_in_movies.csv");
            CSVWriter lsm = new CSVWriter(load_stars_in_movies);
            FileWriter load_genres_in_movies = new FileWriter("load_genres_in_movies.csv");
            CSVWriter lgm = new CSVWriter(load_genres_in_movies);
            FileWriter load_ratings = new FileWriter("load_ratings.csv");
            CSVWriter lr = new CSVWriter(load_ratings);
            for (String i: genres){
                String[] row = {""+exist_genres.get(i),i};
                lg.writeNext(row);
            }
            lg.close();
            for (Map.Entry<String, Actor> entry : new_stars.entrySet()){
                Actor i = entry.getValue();
                String m;
                if (i.getDob() == -1){
                    m = null;
                }else{
                    m = ""+i.getDob();
                }
                String[] row = {i.getId(),i.getName(),m};
                ls.writeNext(row);
            }
            ls.close();
            for (Map.Entry<String, Movie> entry : new_movies.entrySet()){
                Movie i = entry.getValue();
                String[] row = {i.getId(),i.getTitle(),""+i.getYear(),i.getDirector()};
                lm.writeNext(row);
                ArrayList<String> a = new ArrayList<>();
                if (i.getId() == null){
                    inconsistency.write("Movie id is null in " + i.getTitle() + "\n");
                    continue;
                }

                for (String k : i.getActor()){
                    if (k == null){
                        inconsistency.write("A movie star name is null in " + i.getTitle() + "\n");
                    }
                    else if (a.contains(k)){
                        inconsistency.write("duplicate star name "+ k +" in movie " + i.getTitle() + "\n");
                    }else if (exist_stars.containsKey(k)){
                        lsm.writeNext(new String[]{exist_stars.get(k),i.getId()});
                    } else if (new_stars.containsKey(k)){
                        lsm.writeNext(new String[]{new_stars.get(k).getId(),i.getId()});
                    }else{
                        inconsistency.write("Movie star " + k + " in movie " + i.getTitle() + " not found.\n");
                    }

                }
                //lsm.writeNext(new String[]{k.getId(),i.getId()});
                //a.add(k.getId());
                for (String k : i.getGenre()){
                    if (i.getId() != null){
                        lgm.writeNext(new String[]{""+exist_genres.get(k),i.getId()});
                    }
                }
                lr.writeNext(new String[]{i.getId()});
            }
            lm.close();
            lsm.close();
            lgm.close();
            lr.close();

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
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            String query = "LOAD DATA local INFILE 'load_movies.csv' " +
                    "INTO TABLE movies " +
                    "FIELDS TERMINATED BY ',' ENCLOSED BY '\"'";
            boolean rs = statement.execute(query);
            query = "LOAD DATA local INFILE 'load_genres.csv' " +
                    "INTO TABLE genres " +
                    "FIELDS TERMINATED BY ',' ENCLOSED BY '\"'";
            rs = statement.execute(query);
            query = "LOAD DATA local INFILE 'load_stars.csv' " +
                    "INTO TABLE stars " +
                    "FIELDS TERMINATED BY ',' ENCLOSED BY '\"'";
            rs = statement.execute(query);
            query = "LOAD DATA local INFILE 'load_ratings.csv' " +
                    "INTO TABLE ratings " +
                    "FIELDS TERMINATED BY ',' ENCLOSED BY '\"'";
            rs = statement.execute(query);
            query = "LOAD DATA local INFILE 'load_genres_in_movies.csv' " +
                    "INTO TABLE genres_in_movies " +
                    "FIELDS TERMINATED BY ',' ENCLOSED BY '\"'";
            rs = statement.execute(query);
            query = "LOAD DATA local INFILE 'load_stars_in_movies.csv' " +
                    "INTO TABLE stars_in_movies " +
                    "FIELDS TERMINATED BY ',' ENCLOSED BY '\"'";
            rs = statement.execute(query);
            connection.commit();
            statement.close();

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
            if (qName.equalsIgnoreCase("is")){
                currentDirector = "";
            } else if (qName.equalsIgnoreCase("a")) {
                currentStar = "";
            } else if (qName.equalsIgnoreCase("t")){
                currentMovie = "";
            }
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
                    new_movies.put(tempMovie.getTitle(),tempMovie);
                    //System.out.println(String.format("Movie %s put, id %s, director %s, year %d", tempMovie.getTitle(),tempMovie.getId(),tempMovie.getDirector(),tempMovie.getYear()));

                }
                else if (qName.equalsIgnoreCase("t")) {
                    if (exist_movies.containsKey(tempVal)){
                        tempMovie = null;
                        try {
                            inconsistency.write("movie " + tempVal + " already exist in database\n");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }else{tempMovie.setTitle(tempVal);}
                } else if (qName.equalsIgnoreCase("year")) {
                    try{
                        tempVal.replace("@", "");
                        tempVal.replace("+", "");
                        tempMovie.setYear(Integer.parseInt(tempVal));
                    }catch (NumberFormatException e){
                        //e.printStackTrace();
                        try {
                            inconsistency.write(tempMovie.getId() + tempMovie.getTitle() + " has inconsistent year \n");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        tempMovie = null;
                    }

                } else if (qName.equalsIgnoreCase("cat")) {
                    tempMovie.setGenre(tempVal);
                    if (!exist_genres.containsKey(tempVal) && !tempVal.equals("")){
                        exist_genres.put(tempVal,exist_genres.size());
                        genres.add(tempVal);

                    }
                }
            }
        } else if (mode == 1){
            if (tempActor != null){
                if (qName.equalsIgnoreCase("stagename")){
                    if (exist_stars.containsKey(tempVal)){
                        tempActor = null;
                    }else if (new_stars.containsKey(tempVal))
                    {
                        try {
                            inconsistency.write("duplicate actor " + tempVal);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else{
                        tempActor.setName(tempVal);
                    }
                } else if (qName.equalsIgnoreCase("dob")){
                    try{
                        tempVal.replace("@","");
                        tempVal.replace("+","");
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
                    new_stars.put(tempActor.getName(),tempActor);
                }
            }
        } else if (mode == 2){
            if (qName.equalsIgnoreCase("is")) {
                currentDirector = tempVal;
            }
                else if (qName.equalsIgnoreCase("t")) {
                    if (!new_movies.containsKey(tempVal)){
                        try {
                            inconsistency.write("Movie " + tempVal + " not found\n");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }else{currentMovie = tempVal;}
                }  else if (qName.equalsIgnoreCase("a")) {
                    //System.out.println(tempVal);
                    //System.out.println(new_stars.containsKey(tempVal));
                    if (new_stars.containsKey(tempVal)||exist_stars.containsKey(tempVal)){
                        if (new_movies.containsKey(currentMovie)){
                            new_movies.get(currentMovie).setActor(tempVal);
                        }
                        else{
                            try {
                                inconsistency.write(currentMovie + " does not exist when connecting actors and movies.\n");
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }

                    else{
                        try {
                            inconsistency.write(tempVal + " in cast does not exist in actor, omit then.\n");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }

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
