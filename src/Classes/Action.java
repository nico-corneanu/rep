package Classes;

import actor.ActorsAwards;
import entertainment.Genre;
import javassist.compiler.ast.Pair;
import net.sf.saxon.type.StringConverter;
import org.checkerframework.checker.units.qual.A;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import javax.sound.midi.Soundbank;
import java.sql.SQLOutput;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import utils.*;

import static Classes.MyPair.ratingCompareDES;
import static utils.Utils.stringToGenre;

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
                if (objectType.equals("actors")) {
                    if (actors.size() == 0) {
                        JSONObject message = new JSONObject();
                        message.put("id", actionId);
                        message.put("message", "Query result: []");
                        result.add(message);
                    } else {
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
                                Collections.sort(averages, ratingCompareDES);
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
                            if (sortType.equals("asc")) {
                                Collections.sort(awards_pairs, MyPair.nameCompare);
                            } else {
                                Collections.sort(awards_pairs, MyPair.nameCompareDesc);
                            }
                            if (sortType.equals("asc")) {
                                Collections.sort(awards_pairs, MyPair.ratingCompareASC);
                            } else {
                                Collections.sort(awards_pairs, ratingCompareDES);
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
                    }

                } else if (objectType.equals("movies")) {
                    if (criteria.equals("ratings")) {
                        ArrayList<MyPair> ratings_pairs = new ArrayList<>();
                        int count1 = 0, count2 = 0;
                        for (Movies m : movies) {
                            if (!m.getRating().isNaN()) {
                                for (List<String> list : getFilters()) {
                                    if (list != null) {
                                        for (String s : list) {
                                            if ((m.getGenres().contains(s))) {
                                                count1++;
                                            }
                                            Pattern p = Pattern.compile("([0-9])");
                                            Matcher matcher = p.matcher(s);
                                            if (matcher.find()) {
                                                number = Integer.parseInt(s);
                                                if (number == m.getYear()) {
                                                    count2++;
                                                }
                                            }
                                        }
                                    }
                                }
                                if ((count1 > 0) && (count2 > 0)) {
                                    ratings_pairs.add(new MyPair(m.getTitle(), m.getRating()));
                                }
                            }
                        }
                        Collections.sort(ratings_pairs, MyPair.nameCompare);
                        if (sortType.equals("asc")) {
                            Collections.sort(ratings_pairs, MyPair.nameCompare);
                            Collections.sort(ratings_pairs, MyPair.ratingCompareASC);
                        } else {
                            Collections.sort(ratings_pairs, MyPair.nameCompare);
                            Collections.sort(ratings_pairs, ratingCompareDES);
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
                        for (Movies m : movies) {
                            int number_favorite = 0;
                            for (User u : users) {
                                if (u.getFavoriteMovies().contains(m.getTitle())) {
                                    number_favorite++;
                                }
                            }
                            boolean flagYear = true;
                            boolean flagGen = true;
                            for (List<String> list : getFilters()) {
                                if (list != null) {
                                    if (list.equals(getFilters().get(0)) && list.get(0) != null) {
                                        int the_year = Integer.parseInt(list.get(0));
                                        if (m.getYear() != the_year) {
                                            flagYear = false;
                                        }
                                    } else if (flagYear) {
                                        for (int i = 0; i < list.size(); ++i) {
                                            if (list.get(i) != null) {
                                                if (!m.getGenres().contains(list.get(i))) {
                                                    flagGen = false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (number_favorite > 0) {
                                if (flagYear && flagGen) {
                                    favorite_pairs.add(new MyPair(m.getTitle(), number_favorite));
                                }
                            }
                        }
                        if (sortType.equals("asc")) {
                            Collections.sort(favorite_pairs, MyPair.nameCompare);
                            Collections.sort(favorite_pairs, MyPair.ratingCompareASC);
                        } else {
                            Collections.sort(favorite_pairs, MyPair.nameCompareDesc);
                            Collections.sort(favorite_pairs, ratingCompareDES);
                        }
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
                    } else if (criteria.equals("longest")) {
                        ArrayList<MyPair> longest_pairs = new ArrayList<>();
                        int count1 = 0, count2 = 0, count = 0;
                        for (Movies m : movies) {
                            boolean flagYear = true;
                            boolean flagGen = true;
                            for (List<String> list : getFilters()) {
                                if (list != null) {
                                    if (list.equals(getFilters().get(0)) && list.get(0) != null) {
                                        int the_year = Integer.parseInt(list.get(0));
                                        if (m.getYear() != the_year) {
                                            flagYear = false;
                                        }
                                    } else if (flagYear) {
                                        for (int i = 0; i < list.size(); ++i) {
                                            if (list.get(i) != null) {
                                                if (!m.getGenres().contains(list.get(i))) {
                                                    flagGen = false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (flagYear && flagGen) {
                                longest_pairs.add(new MyPair(m.getTitle(), m.getDuration()));
                            }
                        }
                        if (sortType.equals("asc")) {
                            Collections.sort(longest_pairs, MyPair.nameCompare);
                            Collections.sort(longest_pairs, MyPair.ratingCompareASC);
                        } else {
                            Collections.sort(longest_pairs, MyPair.nameCompareDesc);
                            Collections.sort(longest_pairs, ratingCompareDES);
                        }
                        String queryMessage = "Query result: [";
                        for (int i = 0; i < longest_pairs.size(); ++i) {
                            if (i < number) {
                                queryMessage += longest_pairs.get(i).name + ", ";
                            } else {
                                break;
                            }
                        }
                        if (longest_pairs.size() == 0) {
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
                    } else if (criteria.equals("most_viewed")) {
                        ArrayList<MyPair> most_viewed_pairs = new ArrayList<>();

                        for (Movies m : movies) {
                            int sum = 0;
                            for (User u : users) {
                                if (u.getHistory().containsKey(m.getTitle())) {
                                    for (Map.Entry<String, Integer> entry : u.getHistory().entrySet()) {
                                        sum = entry.getValue();
                                    }
                                }
                            }
                            boolean flagYear = true;
                            boolean flagGen = true;
                            for (List<String> list : getFilters()) {
                                if (list != null) {
                                    if (list.equals(getFilters().get(0)) && list.get(0) != null) {
                                        int the_year = Integer.parseInt(list.get(0));
                                        if (m.getYear() != the_year) {
                                            flagYear = false;
                                        }
                                    } else if (flagYear) {
                                        for (int i = 0; i < list.size(); ++i) {
                                            if (list.get(i) != null) {
                                                if (!m.getGenres().contains(list.get(i))) {
                                                    flagGen = false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (sum > 0) {
                                if (flagYear && flagGen) {
                                    most_viewed_pairs.add(new MyPair(m.getTitle(), sum));
                                }
                            }

                        }
                        if (sortType.equals("asc")) {
                            Collections.sort(most_viewed_pairs, MyPair.nameCompare);
                            Collections.sort(most_viewed_pairs, MyPair.ratingCompareASC);
                        } else {
                            Collections.sort(most_viewed_pairs, MyPair.nameCompareDesc);
                            Collections.sort(most_viewed_pairs, ratingCompareDES);
                        }
                        String queryMessage = "Query result: [";
                        for (int i = 0; i < most_viewed_pairs.size(); ++i) {
                            if (i < number) {
                                queryMessage += most_viewed_pairs.get(i).name + ", ";
                            } else {
                                break;
                            }
                        }
                        if (most_viewed_pairs.size() == 0) {
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
                } else if (objectType.equals("shows")) {
                    if (criteria.equals("ratings")) {
                        ArrayList<MyPair> ratings_pairs = new ArrayList<>();
                        int count1 = 0, count2 = 0, count = 0;
                        for (Shows s : shows) {
                            boolean flagYear = true;
                            boolean flagGen = true;
                            if (s.calculateRating() != 0) {
                                for (List<String> list : getFilters()) {
                                    if (list != null) {
                                        if (list.equals(getFilters().get(0)) && list.get(0) != null) {
                                            int the_year = Integer.parseInt(list.get(0));
                                            if (s.getYear() != the_year) {
                                                flagYear = false;
                                            }
                                        } else if (flagYear) {
                                            for (int i = 0; i < list.size(); ++i) {
                                                if (list.get(i) != null) {
                                                    if (!s.getGenres().contains(list.get(i))) {
                                                        flagGen = false;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (flagYear && flagGen) {
                                    ratings_pairs.add(new MyPair(s.getTitle(), s.calculateRating()));
                                }
                            }

                        }

                        if (sortType.equals("asc")) {
                            Collections.sort(ratings_pairs, MyPair.nameCompare);
                            Collections.sort(ratings_pairs, MyPair.ratingCompareASC);
                        } else {
                            Collections.sort(ratings_pairs, MyPair.nameCompareDesc);
                            Collections.sort(ratings_pairs, ratingCompareDES);
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
                    } else if (criteria.equals("favorite")) {
                        ArrayList<MyPair> favorite_pairs = new ArrayList<>();
                        for (Shows s : shows) {
                            int number_favorite = 0;
                            for (User u : users) {
                                if (u.getFavoriteMovies().contains(s.getTitle())) {
                                    number_favorite++;
                                }
                            }
                            boolean flagYear = true;
                            boolean flagGen = true;
                            for (List<String> list : getFilters()) {
                                if (list != null) {
                                    System.out.println(list);
                                    if (list.equals(getFilters().get(0)) && list.get(0) != null) {
                                        int the_year = Integer.parseInt(list.get(0));
                                        if (s.getYear() != the_year) {
                                            flagYear = false;
                                        }
                                    } else if (flagYear) {
                                        for (int i = 0; i < list.size(); ++i) {
                                            if (list.get(i) != null) {
                                                if (!s.getGenres().contains(list.get(i))) {
                                                    flagGen = false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (number_favorite > 0) {
                                if (flagYear && flagGen) {
                                    favorite_pairs.add(new MyPair(s.getTitle(), number_favorite));
                                }
                            }
                        }
                        if (sortType.equals("asc")) {
                            Collections.sort(favorite_pairs, MyPair.nameCompare);
                            Collections.sort(favorite_pairs, MyPair.ratingCompareASC);
                        } else {
                            Collections.sort(favorite_pairs, MyPair.nameCompareDesc);
                            Collections.sort(favorite_pairs, ratingCompareDES);
                        }
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
                    } else if (criteria.equals("longest")) {
                        ArrayList<MyPair> longest_pairs = new ArrayList<>();
                        for (Shows s : shows) {
                            int sum = 0;
                            for (int i = 0; i < s.getSeasons().size(); i++) {
                                sum += s.getSeasons().get(i).getDuration();
                            }
                            boolean flagYear = true;
                            boolean flagGen = true;
                            for (List<String> list : getFilters()) {
                                if (list != null) {
                                    if (list.equals(getFilters().get(0)) && list.get(0) != null) {
                                        int the_year = Integer.parseInt(list.get(0));
                                        if (s.getYear() != the_year) {
                                            flagYear = false;
                                        }
                                    } else if (flagYear) {
                                        for (int i = 0; i < list.size(); ++i) {
                                            if (list.get(i) != null) {
                                                if (!s.getGenres().contains(list.get(i))) {
                                                    flagGen = false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (sum > 0) {
                                if (flagYear && flagGen) {
                                    longest_pairs.add(new MyPair(s.getTitle(), sum));
                                }
                            }
                        }
                        if (sortType.equals("asc")) {
                            Collections.sort(longest_pairs, MyPair.nameCompare);
                            Collections.sort(longest_pairs, MyPair.ratingCompareASC);
                        } else {
                            Collections.sort(longest_pairs, MyPair.nameCompareDesc);
                            Collections.sort(longest_pairs, ratingCompareDES);
                        }
                        String queryMessage = "Query result: [";
                        for (int i = 0; i < longest_pairs.size(); ++i) {
                            if (i < number) {
                                queryMessage += longest_pairs.get(i).name + ", ";
                            } else {
                                break;
                            }
                        }
                        if (longest_pairs.size() == 0) {
                            queryMessage = queryMessage.substring(0, queryMessage.length());
                        } else {
                            queryMessage = queryMessage.substring(0, queryMessage.length() - 2);
                        }
                        queryMessage = queryMessage + "]";
                        JSONObject message = new JSONObject();
                        message.put("id", actionId);
                        message.put("message", queryMessage);
                        result.add(message);
                    } else if (criteria.equals("most_viewed")) {
                        ArrayList<MyPair> most_viewed_pairs = new ArrayList<>();

                        for (Shows s : shows) {
                            int sum = 0;
                            for (User u : users) {
                                if (u.getHistory().containsKey(s.getTitle())) {
                                    for (Map.Entry<String, Integer> entry : u.getHistory().entrySet()) {
                                        sum = entry.getValue();
                                    }
                                }
                            }
                            boolean flagYear = true;
                            boolean flagGen = true;
                            for (List<String> list : getFilters()) {
                                if (list != null) {
                                    if (list.equals(getFilters().get(0)) && list.get(0) != null) {
                                        int the_year = Integer.parseInt(list.get(0));
                                        if (s.getYear() != the_year) {
                                            flagYear = false;
                                        }
                                    } else if (flagYear) {
                                        for (int i = 0; i < list.size(); ++i) {
                                            if (list.get(i) != null) {
                                                if (!s.getGenres().contains(list.get(i))) {
                                                    flagGen = false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (sum > 0) {
                                if (flagYear && flagGen) {
                                    most_viewed_pairs.add(new MyPair(s.getTitle(), sum));
                                }
                            }
                        }
                        if (sortType.equals("asc")) {
                            Collections.sort(most_viewed_pairs, MyPair.nameCompare);
                            Collections.sort(most_viewed_pairs, MyPair.ratingCompareASC);
                        } else {
                            Collections.sort(most_viewed_pairs, MyPair.nameCompareDesc);
                            Collections.sort(most_viewed_pairs, ratingCompareDES);
                        }
                        String queryMessage = "Query result: [";
                        for (int i = 0; i < most_viewed_pairs.size(); ++i) {
                            if (i < number) {
                                queryMessage += most_viewed_pairs.get(i).name + ", ";
                            } else {
                                break;
                            }
                        }
                        if (most_viewed_pairs.size() == 0) {
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
                } else if (objectType.equals("users")) {
                    if (criteria.equals("num_ratings")) {
                        ArrayList<MyPair> num_ratings = new ArrayList<>();
                        for (User u : users) {
                            if(!u.getRatings().isEmpty()) {
                                num_ratings.add(new MyPair(u.getUsername(), u.getRatings().size()));
                            }
                        }
                        if (sortType.equals("asc")) {
                            Collections.sort(num_ratings, MyPair.nameCompare);
                            Collections.sort(num_ratings, MyPair.ratingCompareASC);
                        } else {
                            Collections.sort(num_ratings, MyPair.nameCompareDesc);
                            Collections.sort(num_ratings, ratingCompareDES);
                        }
                        String queryMessage = "Query result: [";
                        for (int i = 0; i < num_ratings.size(); ++i) {
                            if (i < number) {
                                queryMessage += num_ratings.get(i).name + ", ";
                            } else {
                                break;
                            }
                        }
                        if (num_ratings.size() == 0) {
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
                }
            } else if (actionType.equals("recommendation")) {
                if (type.equals("standard")) {
                    boolean bool = true;
                    for (User u : users) {
                        if (u.getUsername().equals(username)) {
                            boolean flag = true;
                            for (Movies m : movies) {
                                if (!u.getHistory().containsKey(m.getTitle())) {
                                    bool = false;
                                    flag = false;
                                    JSONObject message = new JSONObject();
                                    message.put("id", actionId);
                                    message.put("message", "StandardRecommendation result: " + m.getTitle());
                                    result.add(message);
                                    break;
                                }
                            }
                            for (Shows s : shows) {
                                if (flag) {
                                    if (!u.getHistory().containsKey(s.getTitle())) {
                                        bool = false;
                                        JSONObject message = new JSONObject();
                                        message.put("id", actionId);
                                        message.put("message", "StandardRecommendation result: " + s.getTitle());
                                        result.add(message);
                                        break;
                                    }
                                }
                            }
                            if(bool) {
                                JSONObject message = new JSONObject();
                                message.put("id", actionId);
                                message.put("message", "StandardRecommendation cannot be applied!");
                                result.add(message);
                            }
                        }
                    }
                } else if (type.equals("best_unseen")) {
                    ArrayList<MyPair> rec_videos = new ArrayList<>();
                    for (User u : users) {
                        if(u.getUsername().equals(username)) {
                            for(Movies m : movies) {
                                if(!u.getHistory().containsKey(m.getTitle())) {
                                    rec_videos.add(new MyPair(m.getTitle(), m.getRating()));
                                }
                            }
                            for(Shows s : shows) {
                                if(!u.getHistory().containsKey(s.getTitle())) {
                                    rec_videos.add(new MyPair(s.getTitle(), s.calculateRating()));
                                }
                            }
                            Collections.sort(rec_videos, ratingCompareDES);
                            for(int i = 0; i < rec_videos.size(); i++) {
                                JSONObject message = new JSONObject();
                                message.put("id", actionId);
                                message.put("message", "BestRatedUnseenRecommendation result: " + rec_videos.get(0).name);
                                result.add(message);
                                break;
                            }
                            if(rec_videos.size() == 0) {
                                JSONObject message = new JSONObject();
                                message.put("id", actionId);
                                message.put("message", "BestRatedUnseenRecommendation cannot be applied!");
                                result.add(message);
                            }
                        }
                    }
                } else if(type.equals("popular")) {
                    ArrayList<MyPair> popular_genres = new ArrayList<>();
                    for (Genre g : Genre.values()) {
                        popular_genres.add(new MyPair(g.name(), 0));
                    }
                    for (User u : users) {
                        for (Map.Entry<String, Integer> entry : u.getHistory().entrySet()) {
                            for (Movies m : movies) {
                                if (m.getTitle().equals(entry.getKey())) {
                                    for (MyPair pair : popular_genres) {
                                        for (String gen : m.getGenres()) {
                                            if (pair.name.equals(stringToGenre(gen).name())) {
                                                pair.value++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    boolean bool = true;
                    for(User u : users) {
                        if (u.getUsername().equals(username) && u.getSubscriptionType().equals("PREMIUM")) {
                            Collections.sort(popular_genres, ratingCompareDES);
                            for(int i = 0; i < popular_genres.size(); i++) {
                                for(Movies m : movies) {
                                    if(!u.getHistory().containsKey(m.getTitle())) {
                                        for(String gen : m.getGenres()) {
                                            if(popular_genres.get(i).name.equals(stringToGenre(gen).name())) {
                                                bool = false;
                                                JSONObject message = new JSONObject();
                                                message.put("id", actionId);
                                                message.put("message", "PopularRecommendation result: " + m.getTitle());
                                                result.add(message);
                                                break;
                                            }
                                        }
                                    }
                                }
                                for(Shows s : shows) {
                                    if(!u.getHistory().containsKey(s.getTitle())) {
                                        for(String gen : s.getGenres()) {
                                            if(popular_genres.get(i).name.equals(stringToGenre(gen).name())) {
                                                bool = false;
                                                JSONObject message = new JSONObject();
                                                message.put("id", actionId);
                                                message.put("message", "PopularRecommendation result: " + s.getTitle());
                                                result.add(message);
                                                break;
                                            }
                                            break;
                                        }
                                        break;
                                    }
                                    break;
                                }
                            }
                            if(bool) {
                                JSONObject message = new JSONObject();
                                message.put("id", actionId);
                                message.put("message", "PopularRecommendation cannot be applied!");
                                result.add(message);
                            }
                        }
                        if (u.getUsername().equals(username) && u.getSubscriptionType().equals("BASIC")) {
                            JSONObject message = new JSONObject();
                            message.put("id", actionId);
                            message.put("message", "PopularRecommendation cannot be applied!");
                            result.add(message);
                            break;
                        }
                    }
                } else if (type.equals("favorite")) {
                    ArrayList<MyPair> favorite_rec = new ArrayList<>();
                    for (Movies m : movies) {
                        int number_fav = 0;
                        boolean bool = false;
                        for (User u : users) {
                            if (!(u.getUsername().equals(username))) {
                                if (u.getFavoriteMovies().contains(m)) {
                                    number_fav++;
                                }
                            }
                            if (u.getUsername().equals(username) && !u.getHistory().containsKey(m.getTitle()) && u.getSubscriptionType().equals("PREMIUM")) {
                                 bool = true;
                            }
                            if(u.getUsername().equals(username) && u.getSubscriptionType().equals("BASIC")) {
                                JSONObject message = new JSONObject();
                                message.put("id", actionId);
                                message.put("message", "FavoriteRecommendation cannot be applied!");
                                result.add(message);
                            }
                        }
                        if(bool == true) {
                            favorite_rec.add(new MyPair(m.getTitle(), number_fav));
                        }
                }
                    for(Shows s : shows) {
                        int number_fav = 0;
                        boolean bool = false;
                        for(User u : users) {
                                if (!(u.getUsername().equals(username))) {
                                    if (u.getFavoriteMovies().contains(s)) {
                                        number_fav++;
                                    }
                                }
                                if (u.getUsername().equals(username) && !u.getHistory().containsKey(s.getTitle()) && u.getSubscriptionType().equals("PREMIUM")) {
                                    bool = true;
                                }
                                if(u.getUsername().equals(username) && u.getSubscriptionType().equals("BASIC")) {
                                    JSONObject message = new JSONObject();
                                    message.put("id", actionId);
                                    message.put("message", "FavoriteRecommendation cannot be applied!");
                                    result.add(message);
                                }
                        }
                        if(bool == true) {
                            favorite_rec.add(new MyPair(s.getTitle(), number_fav));
                        }

                    }
                    if(favorite_rec.size() != 0) {
                        if (!(favorite_rec.get(0).value == favorite_rec.get(1).value)) {
                            Collections.sort(favorite_rec, ratingCompareDES);
                            JSONObject message = new JSONObject();
                            message.put("id", actionId);
                            message.put("message", "FavoriteRecommendation result: " + favorite_rec.get(0).name);
                            result.add(message);
                        }
                        else {
                            JSONObject message = new JSONObject();
                            message.put("id", actionId);
                            message.put("message", "FavoriteRecommendation result: " + favorite_rec.get(0).name);
                            result.add(message);
                        }
                    } else {
                        JSONObject message = new JSONObject();
                        message.put("id", actionId);
                        message.put("message", "FavoriteRecommendation cannot be applied!");
                        result.add(message);
                    }
                } else if(type.equals("search")) {
                    ArrayList<MyPair> search_rec = new ArrayList<>();
                    for (User u : users) {
                        if (u.getUsername().equals(username) && u.getSubscriptionType().equals("PREMIUM")) {
                            for (Movies m : movies) {
                                if (!u.getHistory().containsKey(m.getTitle())) {
                                    if (m.getGenres().contains(genre)) {
                                        search_rec.add(new MyPair(m.getTitle(), m.getRating()));
                                    }
                                }
                            }
                            for (Shows s : shows) {
                                if (!u.getHistory().containsKey(s.getTitle())) {
                                    if (s.getGenres().contains(genre)) {
                                        search_rec.add(new MyPair(s.getTitle(), s.calculateRating()));
                                    }
                                }
                            }
                        }
                    }
                    Collections.sort(search_rec, MyPair.nameCompare);
                    Collections.sort(search_rec, MyPair.ratingCompareASC);
                    String queryMessage = "SearchRecommendation result: [";
                    for (int i = 0; i < search_rec.size(); ++i) {
                        queryMessage += search_rec.get(i).name + ", ";
                    }
                    if (search_rec.size() != 0) {
                        queryMessage = queryMessage.substring(0, queryMessage.length() - 2);
                        queryMessage = queryMessage + "]";
                        JSONObject message = new JSONObject();
                        message.put("id", actionId);
                        message.put("message", queryMessage);
                        result.add(message);
                }
                    if(search_rec.size() == 0) {
                        JSONObject message = new JSONObject();
                        message.put("id", actionId);
                        message.put("message", "SearchRecommendation cannot be applied!");
                        result.add(message);
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
