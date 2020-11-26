package main;


import classes.Action;
import classes.Actors;
import classes.Movies;
import classes.Shows;
import classes.User;
import checker.Checkstyle;
import checker.Checker;
import common.Constants;
import fileio.*;
import org.json.simple.JSONArray;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * Call the main checker and the coding style checker
     *
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(Constants.TESTS_PATH);
        Path path = Paths.get(Constants.RESULT_PATH);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        File outputDirectory = new File(Constants.RESULT_PATH);

        Checker checker = new Checker();
        checker.deleteFiles(outputDirectory.listFiles());
        int i = 0;
        for (File file : Objects.requireNonNull(directory.listFiles())) {

            String filepath = Constants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                System.out.println("test numarul " + i);
                action(file.getAbsolutePath(), filepath);
                System.out.println();
                System.out.println();
                System.out.println();
            }
            i++;
        }

        checker.iterateFiles(Constants.RESULT_PATH, Constants.REF_PATH, Constants.TESTS_PATH);
        Checkstyle test = new Checkstyle();
        test.testCheckstyle();
    }

    /**
     *
     * @param input
     * @param actors
     * @param movies
     * @param shows
     * @param users
     * @param actions
     */
    //necessary function in order to get data from input provided
    public static void useInput(final Input input, final ArrayList<Actors> actors,
                                final ArrayList<Movies> movies,
                                final ArrayList<Shows> shows,
                                final ArrayList<User> users, final ArrayList<Action> actions) {

        for (int i = 0; i < input.getActors().size(); i++) {
            ActorInputData act = input.getActors().get(i);
            Actors a = new Actors(act.getName(),
                    act.getCareerDescription(), act.getFilmography(), act.getAwards());
            actors.add(a);
        }
        for (int i = 0; i < input.getMovies().size(); i++) {
            MovieInputData act = input.getMovies().get(i);
            Movies a = new Movies(act.getTitle(),
                    act.getYear(), act.getCast(),
                    act.getGenres(), act.getDuration());
            movies.add(a);
        }
        for (int i = 0; i < input.getSerials().size(); i++) {
            SerialInputData act = input.getSerials().get(i);
            Shows a = new Shows(act.getTitle(), act.getYear(),
                    act.getCast(), act.getGenres(),
                    act.getNumberSeason(), act.getSeasons());
            shows.add(a);
        }
        for (int i = 0; i < input.getUsers().size(); i++) {
            UserInputData act = input.getUsers().get(i);
            User a = new User(act.getUsername(),
                    act.getSubscriptionType(),
                    act.getHistory(), act.getFavoriteMovies());
            users.add(a);
        }
        for (int i = 0; i < input.getCommands().size(); i++) {
            ActionInputData act = input.getCommands().get(i);
            if (act.getActionType().equals("command")) {
                Action a = new Action(act.getActionId(),
                        act.getActionType(), act.getType(),
                        act.getUsername(), act.getTitle(),
                        act.getGrade(), act.getSeasonNumber());
                actions.add(a);
            } else if (act.getActionType().equals("query")) {
                Action a = new Action(act.getActionId(), act.getActionType(),
                        act.getObjectType(), act.getSortType(),
                        act.getCriteria(), act.getNumber(), act.getFilters());
                actions.add(a);
            } else {
                Action a = new Action(act.getActionId(), act.getActionType(),
                        act.getType(), act.getUsername(), act.getGenre());
                actions.add(a);
            }
        }
    }


    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */

    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        InputLoader inputLoader = new InputLoader(filePath1);
        Input input = inputLoader.readData();
        // info from input
        ArrayList<Actors> actors = new ArrayList<>();
        ArrayList<Movies> movies = new ArrayList<>();
        ArrayList<Shows> shows = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();
        ArrayList<Action> actions = new ArrayList<>();
        //now, in these variables we have the input data for the actions
        //given as tasks
        useInput(input, actors, movies, shows, users, actions);
        Writer fileWriter = new Writer(filePath2);
        JSONArray arrayResult = new JSONArray();
        for (Action o : actions) {
            o.doAction(arrayResult, movies, users, shows, actors);
        }
        System.out.println(arrayResult);
        fileWriter.closeJSON(arrayResult);
    }
}
