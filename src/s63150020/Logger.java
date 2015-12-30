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

        int boxSize = 46 + Stroj_mn.NAME.length();
        int center = 23 + Stroj_mn.NAME.length() / 2; 

        String bottomBorder = "<<<";
        for(int i = 0; i < ((center * 2) - 6); i++) {
            bottomBorder += "-";
        }
        bottomBorder += ">>>";

        System.out.printf ("---------------------> %s <---------------------\n", Stroj_mn.NAME);
        System.out.println(centerText(String.format("v%-4s", Stroj_mn.VERSION), boxSize) + "\n");

        System.out.println(centerText("Settings", boxSize));
        System.out.printf ("Subset size:   1/%d\n", Node.SUBSET_SIZE);
        System.out.printf ("MC-UCT const:  %.02f\n", Node.UCT_CONSTANT);
        System.out.printf ("Max depth:     %d\n", MoveMaker.MAX_DEPTH);
        System.out.printf ("Critical time: %d ms\n", MoveMaker.TIME_CRITICAL);
        System.out.println(bottomBorder);
    }

    private static String centerText(String text, int boxSize) {
        int textStart = (boxSize / 2) - (text.length() / 2);
        String padding = "";
        for(int i = 0; i < textStart; i++) {
            padding += " ";
        }

        return padding + text;
    }
}
