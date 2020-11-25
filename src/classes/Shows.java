package classes;

import entertainment.Season;

import java.util.ArrayList;

public final class Shows extends Video {
    private int numberOfSeasons = 0;

    private ArrayList<Season> seasons = new ArrayList<>();

    /**
     * @param title
     * @param year
     * @param cast
     * @param genres
     * @param numberOfSeasons
     * @param seasons
     */
    public Shows(final String title, final int year, final ArrayList<String> cast,
                 final ArrayList<String> genres,
                 final int numberOfSeasons, final ArrayList<Season> seasons) {
        super(title, year, cast, genres);
        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
    }


    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    /**
     *
     * @return
     */
    public double calculateRating() {
        double sum = 0;
        for (Season s : seasons) {
            double ratingsSeason = 0;
            if (s.getRatings().size() != 0) {
                for (int i = 0; i < s.getRatings().size(); ++i) {
                    ratingsSeason += s.getRatings().get(i);
                }
                sum += ratingsSeason / s.getRatings().size();
            }
        }
        return sum / numberOfSeasons;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }
}
