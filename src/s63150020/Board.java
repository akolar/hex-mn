package s63150020;

import java.util.ArrayList;

import skupno.Polje;


public class Board {
    /**
     * List of fields on the board.
     */
    private Field[][] fields;

    /**
     * Number of moves played by me.
     */
    private byte nMyMoves = 0;

    /**
     * Total number of moves.
     */
    private byte nMoves = 0;

    public Board(int dimensions) {
        this.fields = new Field[dimensions][dimensions];

        for(int y = 0; y < dimensions; y++) {
            for(int x = 0; x < dimensions; x++) {
                this.fields[y][x] = new Field(y, x, dimensions);
            }
        }

        // novaPartija() is hopefully not timed... :?
        for(int y = 0; y < dimensions; y++) {
            for(int x = 0; x < dimensions; x++) {
                for(int linkY = Math.max(y - 1, 0); linkY < Math.min(y + 2, dimensions); linkY++) {
                    for(int linkX = Math.max(x - 1, 0); linkX < Math.min(x + 2, dimensions); linkX++) {
                        this.fields[y][x].addNeighbour(this.fields[linkY][linkX]);
                    }
                }
            }
        }
    }

    public void play(Owner player, Polje field) {
        fields[field.vrniVrstico()][field.vrniStolpec()].setOwner(player);

        if(player == Owner.Me) {
            nMyMoves++;
            nMoves++;
        } else if(player == Owner.Other) {
            nMoves++;
        } // else: Owner.AssumePlayed
    }

    public Polje play(Owner player, int y, int x) {
        int y1 = negativeIndex(y);
        int x1 = negativeIndex(x);

        fields[y1][x1].setOwner(player);
        return new Polje(y1, x1);
    }

    public ArrayList<Field> getFreeFields() {
        ArrayList<Field> free = new ArrayList<>();

        for(Field[] row : fields) {
            for(Field f : row) {
                if(f.isFree()) {
                    free.add(f);
                }
            }
        }

        return free;
    }

    public ArrayList<Field> getAssumePlayed() {
        ArrayList<Field> free = new ArrayList<>();

        for(Field[] row : fields) {
            for(Field f : row) {
                if(f.isAssumed()) {
                    free.add(f);
                }
            }
        }

        return free;
    }

    public byte getNumberOfMoves() {
        return nMoves;
    }

    public int getDimensions() {
        return this.fields.length;
    }

    public boolean isFree(int y, int x) {
        int x1 = negativeIndex(x);
        int y1 = negativeIndex(y);

        return fields[y1][x1].isFree();
    }

    private int negativeIndex(int idx) {
        return (idx < 0) ? (fields.length + idx - 1) : idx;
    }
}
