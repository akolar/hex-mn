package s63150020;

import java.util.ArrayList;


/**
 * Class containing various helper methods for other classes in the package.
 */
public class Utilities {

    /**
     * Finds the indexes of the largest values in an integer array.
     *
     * @param array array of integer values
     * @return list of indexes
     */
    public static ArrayList<Integer> findMax(int[] array) {
        ArrayList<Integer> fields = null;
        int value = -1;

        for(int i = 0; i < array.length; i++) {
            if(array[i] > value) {
                fields = new ArrayList<>();
                fields.add(i);
                value = array[i];
            } else if(array[i] == value) {
                fields.add(i);
            }
        }

        return fields;
    }
}
