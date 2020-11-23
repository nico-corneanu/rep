package Classes;

import java.util.ArrayList;

public abstract class Video {
    private final String title;
    private final int year;
    private final ArrayList<String> cast;
    private final ArrayList<String> genres;
    private ArrayList<Double> ratings = new ArrayList<>();
    protected Video(String title, int year, ArrayList<String> cast, ArrayList<String> genres) {
        this.title = title;
        this.year = year;
        this.cast = cast;
        this.genres = genres;
    }


    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public ArrayList<String> getCast() {
        return cast;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public Double getRating() {
        double sum = 0;
        for (int i = 0; i < ratings.size(); ++i) {
            sum += ratings.get(i);
        }
        return sum /  ratings.size();
    }

    public void setRating(double r) {
        ratings.add(r);
    }
}
