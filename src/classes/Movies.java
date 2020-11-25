package classes;

import java.util.ArrayList;

public final class Movies extends Video {

    private final int duration;

    public Movies(final String title, final int year, final ArrayList<String> cast,
                  final ArrayList<String> genres, final int duration) {
        super(title, year, cast, genres);
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
}
