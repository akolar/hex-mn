package s63150020;


public class Logger {
    public static void log(String text) {
        System.out.printf("(%s) %s\n", Stroj_OrangePanda.NAME, text);
    }

    public static void startUpSequence() {
        System.out.printf ("---------------------> %s <---------------------\n", Stroj_OrangePanda.NAME);
        System.out.printf ("                          v%-4s                          \n", Stroj_OrangePanda.VERSION);
        System.out.println("---------------------------------------------------------");
    }
}
