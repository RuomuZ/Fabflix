package edu.uci.ics.fabflixmobile.data.model;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {
    private final String name;
    private final short year;
    private final String director;
    private final String star;
    private final String rating;
    private final String genre;
    public Movie(String name, short year, String genre, String star, String director, String rating) {
        this.name = name;
        this.year = year;
        this.director = director;
        this.rating = rating;
        this.star = star;
        this.genre = genre;
    }

    public String getName() {
        return name;
    }

    public short getYear() {
        return year;
    }
    public String getDirector() {
        return director;
    }
    public String getGenre() {
        return genre;
    }
    public String getRating() {
        return rating;
    }
    public String getStar() {
        return star;
    }
}