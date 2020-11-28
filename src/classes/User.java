package classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class User {

    private String username;
    private String subscriptionType;
    private Map<String, Integer> history;
    private ArrayList<String> favoriteMovies;

    public Map<String, Double> getRatings() {
        return ratings;
    }

    private Map<String, Double> ratings = new HashMap<>();

    public User(final String username, final String subscriptionType,
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

    /**
     * @param movie
     */
    public void addMovieToFavorite(final String movie) {
        favoriteMovies.add(movie);
    }

    /**
     * @param movie
     */
    public void view(final String movie) {
        if (history.containsKey(movie)) {
            history.put(movie, history.get(movie) + 1);
        } else {
            history.put(movie, 1);

        }
    }

    /**
     * @param video
     * @param rating
     * @return
     */
    public int giveRatingMovie(final Movies video, final double rating) {
        if (history.containsKey(video.getTitle())) {
            if (!ratings.containsKey(video.getTitle())) {
                ratings.put(video.getTitle(), rating);
                video.setRating(rating);
                return 1;
            } else {
                return 2;
            }
        }
        return 0;
    }

    /**
     * @param video
     * @param rating
     * @param seasonNumber
     * @return
     */
    public int giveRatingShow(final Shows video, final double rating, final int seasonNumber) {
        if (history.containsKey(video.getTitle())) {
            if (!ratings.containsKey(video.getTitle() + seasonNumber)) {
                ratings.put(video.getTitle() + seasonNumber, rating);
                video.getSeasons().get(seasonNumber - 1).getRatings().add(rating);
                return 1;
            } else {
                return 2;
            }
        }
        return 0;
    }
}
