package Classes;

import actor.ActorsAwards;
import javassist.compiler.ast.Pair;
import net.sf.saxon.type.StringConverter;
import org.checkerframework.checker.units.qual.A;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.sound.midi.Soundbank;
import java.sql.SQLOutput;
import java.util.*;

public class Action {
        private int actionId;
        /**
         * Type of action
         */
        private String actionType;
        /**
         * Used for commands
         */
        private String type;
        /**
         * Username of user
         */
        private String username;
        /**
         * The type of object on which the actions will be performed
         */
        private String objectType;
        /**
         * Sorting type: ascending or descending
         */
        private String sortType;
        /**
         * The criterion according to which the sorting will be performed
         */
        private String criteria;
        /**
         * Video title
         */
        private String title;
        /**
         * Video genre
         */
        private String genre;
        /**
         * Query limit
         */
        private int number;
        /**
         * Grade for rating - aka value of the rating
         */
        private double grade;
        /**
         * Season number
         */
        private int seasonNumber;
        /**
         * Filters used for selecting videos
         */
        private List<List<String>> filters;

        //private String year;

        //recomandari
        public Action(final int actionId, final String actionType,
                         final String type, final String username, final String genre) {
            this.actionId = actionId;
            this.actionType = actionType;
            this.type = type;
            this.username = username;
            this.genre = genre;
            this.objectType = null;
            this.sortType = null;
            this.criteria = null;
            this.number = 0;
            this.title = null;
            this.grade = 0;
            this.seasonNumber = 0;
        }

        //query
        public Action(final int actionId, final String actionType, final String objectType,
                      final String sortType, final String criteria,
                      final int number, final List<List<String>> filters) {
            this.actionId = actionId;
            this.actionType = actionType;
            this.objectType = objectType;
            this.sortType = sortType;
            this.criteria = criteria;
            this.number = number;
            this.filters = filters;
            this.title = null;
            this.type = null;
            this.username = null;
            this.genre = null;
            this.grade = 0;
            this.seasonNumber = 0;
        }


        //command
        public Action(final int actionId, final String actionType, final String type,
                      final String username, final String title, final Double grade,
                      final int seasonNumber) {
            this.actionId = actionId;
            this.actionType = actionType;
            this.type = type;
            this.grade = grade;
            this.username = username;
            this.title = title;
            this.seasonNumber = seasonNumber;
            this.genre = null;
            this.objectType = null;
            this.sortType = null;
            this.criteria = null;
            this.number = 0;
        }

        // getters

        public int getActionId() {
            return actionId;
        }

        public String getActionType() {
            return actionType;
        }

        public String getType() {
            return type;
        }

        public String getUsername() {
            return username;
        }

        public String getObjectType() {
            return objectType;
        }

        public String getSortType() {
            return sortType;
        }

        public String getCriteria() {
            return criteria;
        }

        public String getTitle() {
            return title;
        }

        public String getGenre() {
            return genre;
        }

        public int getNumber() {
            return number;
        }

        public double getGrade() {
            return grade;
        }

        public int getSeasonNumber() {
            return seasonNumber;
        }

        public List<List<String>> getFilters() {
            return filters;
        }

        //public String getYear() {return year;}



        // methods
        public void doAction(JSONArray result, ArrayList<Movies> movies, ArrayList<User> users, ArrayList<Shows> shows,
                             ArrayList<Actors> actors) {
            if (actionType.equals("command")) {
                if (type.equals("favorite")) {
                    for (User u : users) {
                        if (u.getUsername().equals(username)) {
                            if (u.getHistory().containsKey(title)) {
                                for (String movie : u.getFavoriteMovies()) {
                                    if (movie.equals(title)) {
                                        JSONObject message = new JSONObject();
                                        message.put("id", actionId);
                                        message.put("message", "error -> " + title + " is already in favourite list");
                                        result.add(message);
                                        return;
                                    }
                                }
                                u.getFavoriteMovies().add(title);
                                JSONObject message = new JSONObject();
                                message.put("id", actionId);
                                message.put("message", "success -> " + title + " was added as favourite");
                                result.add(message);
                            } else {
                                JSONObject message = new JSONObject();
                                message.put("id", actionId);
                                message.put("message", "error -> " + title + " is not seen");
                                result.add(message);
                            }
                        }
                    }
                } else if (type.equals("view")) {
                    for (User u : users) {
                        if (u.getUsername().equals(username)) {
                            u.view(title);
                            JSONObject message = new JSONObject();
                            message.put("id", actionId);
                            message.put("message", "success -> " + title + " was viewed with total views of " + u.getHistory().get(title));
                            result.add(message);
                        }
                    }
                } else if (type.equals("rating")) {
                    if (seasonNumber == 0) {
                        //this is a movie
                        for (User u : users) {
                            if (u.getUsername().equals(username)) {
                                for (Movies m : movies) {
                                    if (m.getTitle().equals(title)) {
                                        int flag = u.giveRatingMovie(m, grade);
                                        if (flag == 1) {
                                            JSONObject message = new JSONObject();
                                            message.put("id", actionId);
                                            message.put("message", "success -> " + title + " was rated with " + grade + " by " + username);
                                            result.add(message);
                                        } else if (flag == 2) {
                                            JSONObject message = new JSONObject();
                                            message.put("id", actionId);
                                            message.put("message", "error -> " + title + " has been already rated");
                                            result.add(message);
                                        } else if (flag == 0) {
                                            JSONObject message = new JSONObject();
                                            message.put("id", actionId);
                                            message.put("message", "error -> " + title + " is not seen");
                                            result.add(message);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        for (User u : users) {
                            if (u.getUsername().equals(username)) {
                                for (Shows s : shows) {
                                    if (s.getTitle().equals(title)) {
                                        int flag = u.giveRatingShow(s, grade, seasonNumber);
                                        //u.giveRatingShow(s, grade, seasonNumber);
                                        if (flag == 1) {
                                            JSONObject message = new JSONObject();
                                            message.put("id", actionId);
                                            message.put("message", "success -> " + title + " was rated with " + grade + " by " + username);
                                            result.add(message);
                                        } else if (flag == 2) {
                                            JSONObject message = new JSONObject();
                                            message.put("id", actionId);
                                            message.put("message", "error -> " + title + " has been already rated");
                                            result.add(message);
                                        } else {
                                            JSONObject message = new JSONObject();
                                            message.put("id", actionId);
                                            message.put("message", "error -> " + title + " is not seen");
                                            result.add(message);
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            } else if (actionType.equals("query")) {
                if (actors.size() == 0) {
                    JSONObject message = new JSONObject();
                    message.put("id", actionId);
                    message.put("message", "Query result: []");
                    result.add(message);
                } else {
                    if (objectType.equals("actors")) {
                        if (criteria.equals("average")) {
                            ArrayList<MyPair> averages = new ArrayList<>();
                            for (Actors a : actors) {
                                double sum = 0;
                                int div = 0;
                                for (String video : a.getFilmography()) {
                                    for (Movies m : movies) {
                                        if (m.getTitle().equals(video)) {
                                            sum += m.getRating();
                                            div++;
                                        }
                                    }
                                    for (Shows sh : shows) {
                                        if (sh.getTitle().equals(video)) {
                                            sum += sh.calculateRating();
                                            div++;
                                        }
                                    }
                                }
                                if (sum / div > 0) {
                                    averages.add(new MyPair(a.getName(), sum / div));
                                }
                            }
                            Collections.sort(averages, MyPair.nameCompare);
                            if (sortType.equals("asc")) {
                                Collections.sort(averages, MyPair.ratingCompareASC);
                            } else {
                                Collections.sort(averages, MyPair.ratingCompareDES);
                            }
                            String queryMessage = "Query result: [";
                            for (int i = 0; i < averages.size(); ++i) {
                                if (i < number) {
                                    queryMessage += averages.get(i).name + ", ";
                                } else {
                                    break;
                                }
                            }
                            queryMessage = queryMessage.substring(0, queryMessage.length() - 2);
                            queryMessage = queryMessage + "]";
                            JSONObject message = new JSONObject();
                            message.put("id", actionId);
                            message.put("message", queryMessage);
                            result.add(message);
                        } else if (criteria.equals("awards")) {
                            ArrayList<MyPair> awards_pairs = new ArrayList<>();
                            int count1 = 0;
                            int count2 = 0;
                            Integer sum = 0;
                            for (Actors a : actors) {
                                for (List<String> list : getFilters()) {
                                    if (list == null) {
                                        continue;
                                    }
                                    for (String s : list) {
                                        if (s != null) {
                                            count1++;
                                            for (Map.Entry<ActorsAwards, Integer> entry : a.getAwards().entrySet()) {
                                                if (s.equals(entry.getKey().name())) {
                                                    count2++;
                                                }
                                            }
                                        }
                                    }
                                }
                                if (count1 == count2) {
                                    for (Map.Entry<ActorsAwards, Integer> entry : a.getAwards().entrySet()) {
                                        sum += entry.getValue();
                                    }
                                }
                                count1 = 0;
                                count2 = 0;
                                if (sum != 0) {
                                    awards_pairs.add(new MyPair(a.getName(), sum));
                                }
                                sum = 0;
                            }
//                            boolean bool = false;
//                            for(int i = 0; i < awards_pairs.size(); i++) {
//                                for(int j = 0; j < awards_pairs.size(); j++) {
//                                    if(awards_pairs.get(i).value == awards_pairs.get(j).value) {
//                                        bool = true;
//                                    }
//                                }
//                            }
                            if (sortType.equals("asc")) {
                                Collections.sort(awards_pairs, MyPair.nameCompare);
                            } else {
                                Collections.sort(awards_pairs, MyPair.nameCompareDesc);
                            }
                            if (sortType.equals("asc")) {
                                Collections.sort(awards_pairs, MyPair.ratingCompareASC);
                            } else {
                                Collections.sort(awards_pairs, MyPair.ratingCompareDES);
                            }

                            String queryMessage = "Query result: [";
                            for (int i = 0; i < awards_pairs.size(); ++i) {
                                queryMessage += awards_pairs.get(i).name + ", ";
                            }
                            if (awards_pairs.size() == 0) {
                                queryMessage = queryMessage.substring(0, queryMessage.length());
                            } else {
                                queryMessage = queryMessage.substring(0, queryMessage.length() - 2);
                            }
                            queryMessage = queryMessage + "]";
                            JSONObject message = new JSONObject();
                            message.put("id", actionId);
                            message.put("message", queryMessage);
                            result.add(message);
                            sum = 0;
                        } else if (criteria.equals("filter_description")) {
                            ArrayList<MyPair> description_pairs = new ArrayList<>();
                            int count1 = 0;
                            int count2 = 0;
                            for (Actors a : actors) {
                                for (List<String> list : getFilters()) {
                                    if (list == null) {
                                        continue;
                                    }
                                    for (String s : list) {
                                        if (s != null) {
                                            count1++;
                                            if (a.getCareerDescription().contains(s)) {
                                                count2++;
                                            }
                                        }
                                    }
                                }
                                if (count1 == count2) {
                                    description_pairs.add(new MyPair(a.getName(), 0));
                                }
                                count1 = 0;
                                count2 = 0;
                            }
                            Collections.sort(description_pairs, MyPair.nameCompare);
                            if (sortType.equals("asc")) {
                                Collections.sort(description_pairs, MyPair.nameCompare);
                            } else {
                                Collections.sort(description_pairs, MyPair.nameCompareDesc);
                            }
                            String queryMessage = "Query result: [";
                            for (int i = 0; i < description_pairs.size(); ++i) {
                                queryMessage += description_pairs.get(i).name + ", ";
                            }
                            if (description_pairs.size() == 0) {
                                queryMessage = queryMessage.substring(0, queryMessage.length());
                            } else {
                                queryMessage = queryMessage.substring(0, queryMessage.length() - 2);
                            }
                            queryMessage = queryMessage + "]";
                            JSONObject message = new JSONObject();
                            message.put("id", actionId);
                            message.put("message", queryMessage);
                            result.add(message);
                        }
                    } else if (objectType.equals("movies")) {
                        if (criteria.equals("ratings")) {
                            ArrayList<MyPair> ratings_pairs = new ArrayList<>();
                            boolean bool = false;
                            int count1 = 0, count2 = 0;
                            for (Movies m : movies) {
                                if (!m.getRating().isNaN()) {
                                    for (List<String> list : getFilters()) {
                                        if (list != null) {
                                            for (String s : list) {
                                                if (s != null) {
                                                    if ((m.getGenres().contains(s))) {
                                                        count1++;
                                                    }
                                                    if (s.equals(m.getYear())) {
                                                        count2++;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if ((count1 > 0) || (count2 > 0)) {
                                        ratings_pairs.add(new MyPair(m.getTitle(), m.getRating()));
                                    }
                                }
                            }
                            Collections.sort(ratings_pairs, MyPair.nameCompare);
                            if (sortType.equals("asc")) {
                                Collections.sort(ratings_pairs, MyPair.ratingCompareASC);
                            } else {
                                Collections.sort(ratings_pairs, MyPair.ratingCompareDES);
                            }
                            String queryMessage = "Query result: [";
                            for (int i = 0; i < ratings_pairs.size(); ++i) {
                                if (i < number) {
                                    queryMessage += ratings_pairs.get(i).name + ", ";
                                } else {
                                    break;
                                }
                            }
                            if (ratings_pairs.size() == 0) {
                                queryMessage = queryMessage.substring(0, queryMessage.length());
                            } else {
                                queryMessage = queryMessage.substring(0, queryMessage.length() - 2);
                            }
                            queryMessage = queryMessage + "]";
                            JSONObject message = new JSONObject();
                            message.put("id", actionId);
                            message.put("message", queryMessage);
                            result.add(message);
                        } else if (criteria.equals("favorite")) {
                            ArrayList<MyPair> favorite_pairs = new ArrayList<>();
                            int count1 = 0, count2 = 0;
                            int number_favorite = 0;
                            for (Movies m : movies) {
                                for (User u : users) {
                                    if (u.getFavoriteMovies().contains(m.getTitle())) {
                                        number_favorite++;
                                        for (List<String> list : getFilters()) {
                                            if (list != null) {
                                                for (String s : list) {
                                                    if (s != null) {
                                                        if ((m.getGenres().contains(s))) {
                                                            count1++;
                                                        }
                                                        if (s.equals(m.getYear())) {
                                                            count2++;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (count1 > 0 || count2 > 0) {
                                    favorite_pairs.add(new MyPair(m.getTitle(), number_favorite));
                                }
                                number_favorite = 0;
                                count1 = 0;
                                count2 = 0;
                            }
                            Collections.sort(favorite_pairs, MyPair.nameCompare);
                            Collections.sort(favorite_pairs, MyPair.ratingCompareDES);

                            String queryMessage = "Query result: [";
                            for (int i = 0; i < favorite_pairs.size(); ++i) {
                                if (i < number) {
                                    queryMessage += favorite_pairs.get(i).name + ", ";
                                } else {
                                    break;
                                }
                            }
                            if (favorite_pairs.size() == 0) {
                                queryMessage = queryMessage.substring(0, queryMessage.length());
                            } else {
                                queryMessage = queryMessage.substring(0, queryMessage.length() - 2);
                            }
                            queryMessage = queryMessage + "]";
                            JSONObject message = new JSONObject();
                            message.put("id", actionId);
                            message.put("message", queryMessage);
                            result.add(message);
                        } else if (objectType.equals("shows")) {
                            if (criteria.equals("ratings")) {
                                boolean bool = false;
                                ArrayList<MyPair> ratings_pairs = new ArrayList<>();
                                for (Shows s : shows) {
                                    if (!s.getRating().isNaN()) {
                                        for (List<String> list : getFilters()) {
                                            if (list != null) {
                                                for (String filter : list) {
                                                    if (filter != null) {
                                                        if (filter.equals(s.getYear()) && s.getGenres().contains(s)) {
                                                            ratings_pairs.add(new MyPair(s.getTitle(), s.calculateRating()));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                Collections.sort(ratings_pairs, MyPair.nameCompare);
                                if (sortType.equals("asc")) {
                                    Collections.sort(ratings_pairs, MyPair.ratingCompareASC);
                                } else {
                                    Collections.sort(ratings_pairs, MyPair.ratingCompareDES);
                                }
                                String queryMessage = "Query result: [";
                                for (int i = 0; i < ratings_pairs.size(); ++i) {
                                    if (i < number) {
                                        queryMessage += ratings_pairs.get(i).name + ", ";
                                    } else {
                                        break;
                                    }
                                }
                                if (ratings_pairs.size() == 0) {
                                    queryMessage = queryMessage.substring(0, queryMessage.length());
                                } else {
                                    queryMessage = queryMessage.substring(0, queryMessage.length() - 2);
                                }
                                //queryMessage = queryMessage.substring(0, queryMessage.length() - 2);
                                queryMessage = queryMessage + "]";
                                JSONObject message = new JSONObject();
                                message.put("id", actionId);
                                message.put("message", queryMessage);
                                result.add(message);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "ActionInputData{"
                    + "actionId=" + actionId
                    + ", actionType='" + actionType + '\''
                    + ", type='" + type + '\''
                    + ", username='" + username + '\''
                    + ", objectType='" + objectType + '\''
                    + ", sortType='" + sortType + '\''
                    + ", criteria='" + criteria + '\''
                    + ", title='" + title + '\''
                    + ", genre='" + genre + '\''
                    + ", number=" + number
                    + ", grade=" + grade
                    + ", seasonNumber=" + seasonNumber
                    + ", filters=" + filters
                    + '}' + "\n";
        }
}
