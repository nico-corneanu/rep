package Classes;

import entertainment.Season;

import java.util.ArrayList;

public class Shows extends Video {
    private int numberOfSeasons = 0;

    private ArrayList<Season> seasons = new ArrayList<>();
    public Shows(String title, int year, ArrayList<String> cast, ArrayList<String> genres,
                 final int numberOfSeasons, final ArrayList<Season> seasons) {
        super(title, year, cast, genres);
        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
    }


    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }
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
