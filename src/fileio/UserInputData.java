package fileio;

import entertainment.Season;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Information about an user, retrieved from parsing the input test files
 * <p>
 * DO NOT MODIFY
 */
public final class UserInputData {
    /**
     * User's username
     */
    private final String username;
    /**
     * Subscription Type
     */
    private final String subscriptionType;
    /**
     * The history of the movies seen
     */
    private final Map<String, Integer> history;
    /**
     * Movies added to favorites
     */
    private final ArrayList<String> favoriteMovies;

    //aici pun comm
    private Map<String, Double> ratings;
    public UserInputData(final String username, final String subscriptionType,
                         final Map<String, Integer> history,
                         final ArrayList<String> favoriteMovies) {
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.favoriteMovies = favoriteMovies;
        this.history = history;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, Integer> getHistory() {
        return history;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public ArrayList<String> getFavoriteMovies() {
        return favoriteMovies;
    }


    //actions made by a user
    public void addMovieToFavorite(String movie) {
        favoriteMovies.add(movie);
    }

    public void view(String movie) {
        if(history.containsKey(movie)) {
            history.put(movie, history.get(movie) + 1);
        } else {
            history.put(movie, 1);
        }
    }

    public void giveRatingMovie(MovieInputData video, double rating) {
        if(history.containsKey(video.getTitle())) {
            if(!ratings.containsKey(video.getTitle())) {
                ratings.put(video.getTitle(), rating);
                video.setRating(rating);
            }
        }
    }

    public void giveRatingShow(SerialInputData video, List<Double> ratings, Season s) {
        if(history.containsKey(video.getTitle())) {
            for(int i = 0; i < video.getNumberSeason(); i++) {
                if(video.getSeasons().get(i).getRatings().get(i).equals(0)) {
                    video.getSeasons().get(i).setRatings(ratings);
                }
            }
        }
    }


    @Override
    public String toString() {
        return "UserInputData{" + "username='"
                + username + '\'' + ", subscriptionType='"
                + subscriptionType + '\'' + ", history="
                + history + ", favoriteMovies="
                + favoriteMovies + '}';
    }
}
