package s63150020;

import skupno.Polje;
import skupno.Stroj;


/**
 * This class provides an interface for communication with the supervisor
 * process for the match.
 *
 * It contains methods for starting and finishing a game, requesting the next
 * move and accepting moves made by others. 
 */
public class Stroj_mn implements Stroj {

    /**
     * Name of the bot.
     */
    public static final String NAME = "mn";

    /** 
     * Version of the bot.
     */
    public static final String VERSION = "0.2b";

    /**
     * Score for the current match.
     *
     * score = [nWon, nLost]
     */
    private int[] score;

    /**
     * Indicates whether a game is in progress.
     */
    private boolean playing;

    /**
     * List of moves played by both players.
     */ 
    private Board board;

    /**
     * Next move calculator. 
     */
    private MoveMaker movemaker;

    /**
     * Creates new instance of the bot.
     * Sets the verbosity for the logger, prints the welcome message and
     * sets the game score to 0:0.
     */
    public Stroj_mn() {
        String hexDebug = System.getenv("HEX_DEBUG");
        if(hexDebug != null) {
            Logger.setLevel(Integer.parseInt(hexDebug));
        }

        Logger.startUpSequence();
        this.score = new int[2];
    }

    /**
     * Starts a new match; resets the board and move maker.
     * 
     * @param dimensions dimensions of the board
     * @param isRed true iff player plays as red (vertical) player
     */
    @Override
    public void novaPartija(int dimensions, boolean isRed) {
        Logger.info("New game started. Playing as %s.", isRed ? "red" : "blue");

        board = new Board(dimensions, isRed);
        movemaker = new MoveMaker(board, isRed);
        playing = true;
    }

    /**
     * Chooses the best next move using the `MoveMaker`.
     *
     * @param remainingTime total remaining time for the player
     * @return `Polje` where next move will be played
     */
    @Override
    public Polje izberiPotezo(long remainingTime) {
        Polje move = movemaker.nextMove(remainingTime);
        return move;
    }

    /**
     * Accepts the move played by the opponent.
     *
     * @param field field where the opponent played
     */
    @Override
    public void sprejmiPotezo(Polje field) {
        board.play(Owner.Other, field);
    }

    /**
     * Updates the score after the match.
     *
     * @param won true iff the player won the match
     */
    @Override
    public void rezultat(boolean won) {
        if(won) {
            score[0]++;
        } else {
            score[1]++;
        }

        playing = false;

        Logger.info("Game %s. Current score: %d:%d", won ? "won" : "lost", score[0], score[1]);
    }
}
