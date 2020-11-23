package Classes;

import java.util.ArrayList;

public class Movies extends Video {

    private final int duration;
    public Movies(String title, int year, ArrayList<String> cast, ArrayList<String> genres, int duration) {
        super(title, year, cast, genres);
        this.duration =  duration;
    }

    public int getDuration() {
        return duration;
    }
}
