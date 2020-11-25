package classes;

import java.util.Comparator;

public class MyPair {
    public String name;
    public double value;

    MyPair(final String n, final double v) {
        name = n;
        value = v;
    }

    public static Comparator<MyPair> ratingCompareASC = new Comparator<MyPair>() {
        @Override
        public int compare(final MyPair o1, final MyPair o2) {
            return Double.compare(o1.value, o2.value);
        }
    };

    public static Comparator<MyPair> ratingCompareDES = new Comparator<MyPair>() {
        @Override
        public int compare(final MyPair o1, final MyPair o2) {
            return Double.compare(o2.value, o1.value);
        }
    };

    public static Comparator<MyPair> nameCompare = new Comparator<MyPair>() {
        @Override
        public int compare(final MyPair o1, final MyPair o2) {
            return o1.name.compareTo(o2.name);
        }
    };

    public static Comparator<MyPair> nameCompareDesc = new Comparator<MyPair>() {
        @Override
        public int compare(final MyPair o1, final MyPair o2) {
            return o2.name.compareTo(o1.name);
        }
    };
}
