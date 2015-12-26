package s63150020;

import skupno.Polje;


/**
 * Storage of played moves.
 */
public class Moves {
    private int[][] me;
    private int[][] other;

    private int sizeMe = 0;
    private int sizeOther = 0;

    public Moves(int boardDimensions) {
        this.me = new int[boardDimensions * boardDimensions / 2 + 1][2];
        this.other = new int[boardDimensions * boardDimensions / 2 + 1][2];
    }

    public void addMove(boolean myMove, Polje move) {
        int x = move.vrniStolpec();
        int y = move.vrniVrstico();

        if(myMove) {
            me[sizeMe][0] = y;
            me[sizeMe][1] = x;
            sizeMe++;
        } else {
            other[sizeOther][0] = y;
            other[sizeOther][1] = x;
            sizeOther++;
        }
    }

    public int size(boolean myMoves) {
        return myMoves ? sizeMe : sizeOther;
    }
    
    public Polje getMoveByIdx(boolean myMoves, int index) {
        int size;
        int[][] array;

        if(myMoves) {
            size = sizeMe;
            array = me;
        } else {
            size = sizeOther;
            array = other;
        }

        if(size <= index) {
            return null;
        }

        return new Polje(array[index][0], array[index][1]);
    }
}
