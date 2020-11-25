package classes;

import java.util.ArrayList;

public abstract class Video {
    private final String title;
    private final int year;
    private final ArrayList<String> cast;
    private final ArrayList<String> genres;
    private boolean viewed = false;
    private ArrayList<Double> ratings = new ArrayList<>();

    protected Video(final String title, final int year,
                    final ArrayList<String> cast, final ArrayList<String> genres) {
        this.title = title;
        this.year = year;
        this.cast = cast;
        this.genres = genres;
    }


    public final String getTitle() {
        return title;
    }

    public final int getYear() {
        return year;
    }

    public final ArrayList<String> getCast() {
        return cast;
    }

    public final ArrayList<String> getGenres() {
        return genres;
    }

    /**
     *
     * @return
     */
    public final Double getRating() {
        double sum = 0;
        for (int i = 0; i < ratings.size(); ++i) {
            sum += ratings.get(i);
        }
        return sum / ratings.size();
    }

    /**
     *
     * @param r
     */
    public final void setRating(final double r) {
        ratings.add(r);
    }

    /**
     *
     */
    public final void viewed() {
        viewed = true;
    }

    public final boolean getViewed() {
        return viewed;
    }
}
