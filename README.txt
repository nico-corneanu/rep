CORNEANU NICOLETA, 324CD

In solving the given tasks, I implemented my own classes, similar to the ones already given in package fileio.
In order to easily and efficiently compare the data, I made a Comparator class: in MyPair, I implemented the methods necessary
to sort any given key by a required value, according to a the given sorting type (ascending, descending, by value or by name).
Because both movies and shows identify as videos, I decided that making an abstract class Video that implements the similar methods between the two classes would be helpful.

I have also written some methods directly linked to the user, such as viewing a video (thus adding to history), giving rating to a movie or to a show, in my class User.
Each class has a constructor, getters and setters when needed and other methods belonging to the specified type of object.

All the actions are implemented in the Action class, in function doAction, which I use to pass the data to the arrayResult.
The actions are divided in three categories: command, query, recommendation, each of them being divided by a given type of object (Actors, Videos, Users) and by a spefic type of their own.
In consequence, I structured my function in conditional statements, in order to determine the type of action that is to be made and any other parameters mentioned above.

For priting the result, I use JSON objects, given for each situation of a criteria (success, errors, cannot be applied, other sides of the problem).

For any interaction with videos in the implementation of an action (reffering to a subtype of actions), I am approaching both cases: movie or show.
In various points, I am using flags or booleans in order to make sure that a condition is met.
For example, when certain filters apply, I go through the filters list by list and verifying: if the first filter (year) does not apply, I go no further, as the next list of filters are conditioned by a boolean.
If both flags, the one for year and the one for genres, remain valid, I add the correspondent pairs.

Depedening on what it is to be implemented, I make an array of the object type with the values that has to be sorted.
After applying all the conditions and iterating through all the objects to see if they meet the criteria given, if the array of pairs is empty,
I print either an empty result or an error message. Otherwise, I print a JSON Object containing all the names of the objects that met the conditions.

I sort everything by applying the methods from the Comparator class. 
