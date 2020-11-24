package Classes;

import entertainment.Season;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class User {

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

    public int giveRatingMovie(Movies video, double rating) {
        if(history.containsKey(video.getTitle())) {
            if(!ratings.containsKey(video.getTitle())) {
                ratings.put(video.getTitle(), rating);
                video.setRating(rating);
                return 1;
            } else {
                return 2;
            }
        }
        return 0;
    }

    public int giveRatingShow(Shows video, double rating, int seasonNumber) {
        if(history.containsKey(video.getTitle())) {
            if(!ratings.containsKey(video.getTitle() + seasonNumber)) {
                ratings.put(video.getTitle(), rating);
                video.getSeasons().get(seasonNumber - 1).getRatings().add(rating);
                return 1;
            }
            else {
                return 2;
            }
        }
        return 0;
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
