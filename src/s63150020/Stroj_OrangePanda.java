package s63150020;

import skupno.Stroj;
import skupno.Polje;


public class Stroj_OrangePanda implements Stroj {

    public static final String NAME = "OrangePanda";
    public static final String VERSION = "0.1b";

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


    public Stroj_OrangePanda() {
        String hexDebug = System.getenv("HEX_DEBUG");
        if(hexDebug != null) {
            Logger.setLevel(Integer.parseInt(hexDebug));
        }

        Logger.startUpSequence();
        this.score = new int[2];
    }

    @Override
    public void novaPartija(int dimensions, boolean isRed) {
        Logger.log("New game started. Playing as %s.", isRed ? "red" : "blue");

        board = new Board(dimensions, isRed);
        movemaker = new MoveMaker(board, isRed);
        playing = true;
    }

    @Override
    public Polje izberiPotezo(long remainingTime) {
        Polje move = movemaker.nextMove(remainingTime);
        return move;
    }

    @Override
    public void sprejmiPotezo(Polje field) {
        board.play(Owner.Other, field);
    }

    @Override
    public void rezultat(boolean won) {
        if(won) {
            Logger.stihDance();
            score[0]++;
        } else {
            score[1]++;
        }

        playing = false;

        Logger.log("Game %s. Current score: %d:%d", won ? "won" : "lost", score[0], score[1]);
    }
}
