import java.util.ArrayList;

public class Movie {
    private String title;
    private String id;
    private int year;
    private String director;
    private ArrayList<Actor> actors;
    private ArrayList<String> genres;
    public Movie(){
        actors = new ArrayList<>();
        genres = new ArrayList<>();
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setActor(Actor actor) {
        this.actors.add(actor);
    }

    public ArrayList<Actor> getActor() {
        return actors;
    }
    public void setGenre(String genre) {
        this.genres.add(genre);
    }

    public ArrayList<String> getGenre() {
        return genres;
    }
/*
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Employee Details - ");
        sb.append("Name:" + getName());
        sb.append(", ");
        sb.append("Type:" + getType());
        sb.append(", ");
        sb.append("Id:" + getId());
        sb.append(", ");
        sb.append("Age:" + getAge());
        sb.append(".");

        return sb.toString();
    }

 */
}
