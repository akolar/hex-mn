package s63150020;

import java.util.ArrayList;
import java.util.Random;

import skupno.Polje;


/**
 * Magic.
 */
public class MoveMaker {

    /**
     * Time treshold for falling back to emergency strategy.
     * In milliseconds.
     */
    private final int TIME_CRITICAL = 500;

    /**
     * Boolean indicating whether the player tries to connect edges vertically.
     */
    private boolean playsVertically;

    /**
     * Game board.
     */
    private Board board;

    /**
     * Dimensions of the game board.
     */
    private int boardDimensions;

   /**
    * RNG for Monte Carlo-based algorithm
    */
    private Random generator;


    /**
     * Indicates whether fields (center, center), (-1, 1) and (1, -1) are still free.
     */
    private boolean strongPointsFree = true;


    public MoveMaker(Board board, boolean playsVertically) {
        this.board = board;
        this.boardDimensions = board.getDimensions();
        this.playsVertically = playsVertically;
        this.generator = new Random();
    }

    /**
     * Chooses the next move by using predefined presets or using MC-UCT algorithm.
     *
     * Note: If move is made in under 50 ms, supervisor does not substract time!
     */
    public Polje nextMove(long remainingTime) {
        long startTime = System.currentTimeMillis();
        Polje move = null;

        if(strongPointsFree) {
            move = makeFirstMove();
        }

        if(move == null) {
        //if(remainingTime < TIME_CRITICAL) {
            move = makeRandom();
        }

        Logger.log(String.format("Me: %s, Other: %s", board.edgesConnected(Owner.Me), board.edgesConnected(Owner.Other)));
        long elapsed = System.currentTimeMillis() - startTime;
        Logger.log(String.format("Made move in %-3d ms. %-5d ms remaining", elapsed, remainingTime - elapsed));
        return move;
    }

    private Polje makeFirstMove() {
        Polje move;

        if(board.isFree(boardDimensions / 2, boardDimensions / 2)) {
            move = board.play(Owner.Me, boardDimensions / 2, boardDimensions / 2);
        } else if(board.isFree(1, -1)) {
            move = board.play(Owner.Me, 1, -1);
        } else if(board.isFree(-1, 1)) {
            move = board.play(Owner.Me, -1, 1);
        } else {
            strongPointsFree = false;
            return null;
        }

        int x = move.vrniStolpec();
        int y = move.vrniVrstico();

        board.play(Owner.AssumePlayed, y - 1, x);
        board.play(Owner.AssumePlayed, y - 1, x + 1);
        board.play(Owner.AssumePlayed, y + 1, x);
        board.play(Owner.AssumePlayed, y + 1, x - 1);

        return move;
    }

    private Polje makeRandom() {
        Field move;

        ArrayList<Field> assumed = board.getAssumePlayed();
        if(assumed.size() > 0) {
            int randomIdx = generator.nextInt(assumed.size()); 
            move = assumed.get(randomIdx);
        } else {
            ArrayList<Field> free = board.getFreeFields();
            int randomIdx = generator.nextInt(free.size()); 
            move = free.get(randomIdx);
        }

        move.setOwner(Owner.Me);
        return move.toPolje();
    }
}
