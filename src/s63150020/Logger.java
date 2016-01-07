package s63150020;


/**
 * This class provides the interface with the command line interface.
 * It includes various methods for logging messages of diffrent priorities
 * and methods for setting the minimum priority level.
 */
public class Logger {
    /**
     * This enum represents different verbosity levels.
     * Verbosity.Debug   -- the most verbose setting. All messages are printed
     *                      on stdout
     * Verbosity.Info    -- holds back the debugging messages, but prints
     *                      everything else
     * Verbosity.Warning -- shows warnings and errors
     * Verbosity.Error   -- shows only error messages
     * Verbosity.Fatal   -- shows only messages which result in program 
     *                      termination
     */
    public enum Verbosity {
        Debug(5), Info(4), Warning(2), Error(1), Fatal(0);

        private int level;
        
        private Verbosity(int level) {
            this.level = level;
        }
        public int getValue() {
            return level;
        }
    }

    /**
     * Escape code for red text.
     */
    private static final String ANSI_BOLD = "\033[0;1m";

    /**
     * Escape code for resetting text formatting.
     */
    private static final String ANSI_RESET = "\033[0;0m";

    /**
     * Template for log messages.
     */
    private static final String LOG_TEMPLATE = ANSI_BOLD + "(%s)" + ANSI_RESET + " %s\n";

    /**
     * Passthrough level for messages.
     */
    private static Verbosity level = Verbosity.Fatal;

    /**
     * Sets the the verbosity level to the specified value.
     *
     * @param newLevel new verbosity level
     */
    public static void setVerbosity(Verbosity newLevel) {
        level = newLevel;
    }

    /**
     * Sets the integer value of verbosity level to the coresponding 
     * `Verbosity` level.
     *
     * @param newLevel new verbosity level
     */
    public static void setLevel(int newLevel) {
        if(newLevel == Verbosity.Debug.getValue()) {
            level = Verbosity.Debug;
        } else if(newLevel == Verbosity.Info.getValue()) {
            level = Verbosity.Info;
        } else if(newLevel == Verbosity.Warning.getValue()) {
            level = Verbosity.Warning;
        } else if(newLevel == Verbosity.Error.getValue()) {
            level = Verbosity.Error;
        } else {
            level = Verbosity.Fatal;
        }
    }

    /**
     * Prints message with priority of specified verbosity to the stdout.
     * Accepts the the message string and parameters to format the message
     * string.
     *
     * @param level verbosity level of the message
     * @param fstring string to be formatted with `fvalues`
     * @param fvalues values to format `fstring`
     */
    public static void log(Verbosity level, String fstring, Object... fvalues) {
        if(Logger.level.getValue() < level.getValue()) {
            return;
        }

        System.out.printf(LOG_TEMPLATE, Stroj_mn.NAME, String.format(fstring, fvalues));
    }

    /**
     * Alias to `log(Verbosity.Debug, fstring, fvalues)`.
     *
     * @param fstring string to be formatted with `fvalues`
     * @param fvalues values to format `fstring`
     */
    public static void debug(String fstring, Object... fvalues) {
        log(Verbosity.Debug, fstring, fvalues);
    }

    /**
     * Alias to `log(Verbosity.Info, fstring, fvalues)`.
     *
     * @param fstring string to be formatted with `fvalues`
     * @param fvalues values to format `fstring`
     */
    public static void info(String fstring, Object... fvalues) {
        log(Verbosity.Info, fstring, fvalues);
    }

    /**
     * Prints the name and basic settings of the bot to the stdout.
     */
    public static void startUpSequence() {
        if(level.getValue() < Verbosity.Info.getValue()) {
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

    /**
     * Centers the text for given boxsize.
     */
    private static String centerText(String text, int boxSize) {
        int textStart = (boxSize / 2) - (text.length() / 2);
        String padding = "";
        for(int i = 0; i < textStart; i++) {
            padding += " ";
        }

        return padding + text;
    }
}
