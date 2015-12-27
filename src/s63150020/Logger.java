package s63150020;


public class Logger {
    public enum Verbosity {
        All(5), Log(4), Warning(2), Error(1), Fatal(0);

        private int level;
        
        private Verbosity(int level) {
            this.level = level;
        }
        public int getValue() {
            return level;
        }
    }

    private static Verbosity level = Verbosity.Fatal;

    public static void setVerbosity(Verbosity newLevel) {
        level = newLevel;
    }

    public static void setLevel(int newLevel) {
        if(newLevel == Verbosity.All.getValue()) {
            level = Verbosity.All;
        } else if(newLevel == Verbosity.Log.getValue()) {
            level = Verbosity.Log;
        } else if(newLevel == Verbosity.Warning.getValue()) {
            level = Verbosity.Warning;
        } else if(newLevel == Verbosity.Error.getValue()) {
            level = Verbosity.Error;
        } else {
            level = Verbosity.Fatal;
        }
    }

    public static void log(String fstring, Object... fvalues) {
        if(level.getValue() < Verbosity.Log.getValue()) {
            return;
        }

        System.out.printf("(%s) %s\n", Stroj_OrangePanda.NAME, String.format(fstring, fvalues));
    }

    public static void startUpSequence() {
        if(level.getValue() < Verbosity.All.getValue()) {
            return;
        }

        System.out.printf ("---------------------> %s <---------------------\n", Stroj_OrangePanda.NAME);
        System.out.printf ("                          v%-4s                          \n", Stroj_OrangePanda.VERSION);
        System.out.println("---------------------------------------------------------");
    }

    public static void stihDance() {
        if(level.getValue() < Verbosity.All.getValue()) {
            return;
        }

        System.out.println("    \\o-");
        System.out.println("    -o/");
        System.out.println("    -o.");
        System.out.println("    .o-");
    }
}
