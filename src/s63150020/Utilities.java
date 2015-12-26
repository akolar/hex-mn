package s63150020;


import java.util.ArrayList;


public class Utilities {

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
