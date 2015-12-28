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

    private static final String ANSI_BOLD = "\033[0;1m";
    private static final String ANSI_RESET = "\033[0;0m";
    private static final String LOG_TEMPLATE = ANSI_BOLD + "(%s)" + ANSI_RESET + " %s\n";

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

        System.out.printf(LOG_TEMPLATE, Stroj_mn.NAME, String.format(fstring, fvalues));
    }

    public static void startUpSequence() {
        if(level.getValue() < Verbosity.All.getValue()) {
            return;
        }

        int center = 23 + (Stroj_mn.NAME.length() / 2);
        String verPadding = "";
        for(int i = 0; i < (center - (Stroj_mn.VERSION.length() / 2)); i++) {
            verPadding += " ";
        }

        String bottomBorder = "<<<";
        for(int i = 0; i < ((center * 2) - 6); i++) {
            bottomBorder += "-";
        }
        bottomBorder += ">>>";

        System.out.printf ("---------------------> %s <---------------------\n", Stroj_mn.NAME);
        System.out.printf ("%sv%-4s\n", verPadding, Stroj_mn.VERSION);
        System.out.println(bottomBorder);
    }
}
