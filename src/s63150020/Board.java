package s63150020;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;

import skupno.Polje;


public class Board {
    /**
     * List of fields on the board.
     */
    private Field[][] fields;

    /**
     * Number of moves played by me.
     */
    private int nMyMoves = 0;

    /**
     * Total number of moves.
     */
    private int nMoves = 0;

    /**
     * Total number of fields.
     */
    private int nFields;

    /**
     * Top edge of the board.
     * Default owner is red.
     */
    private Field topEdge;

    /**
     * Bottom edge of the board.
     * Default owner is red.
     */
    private Field bottomEdge;
    
    /**
     * Left edge of the board.
     * Default owner is blue.
     */
    private Field leftEdge;

    /**
     * Left edge of the board.
     * Default owner is blue.
     */
    private Field rightEdge;

    public Board(int dimensions, boolean playerIsVertical) {
        this.fields = new Field[dimensions][dimensions];
        this.nFields = (int) (dimensions * dimensions);

        Owner verticalPlayer = playerIsVertical ? Owner.Me : Owner.Other;
        Owner horizontalPlayer = playerIsVertical ? Owner.Other : Owner.Me;

        this.leftEdge = new Field(-2, -2, horizontalPlayer);
        this.bottomEdge = new Field(-1, -2, verticalPlayer);
        this.rightEdge = new Field(-1, -1, horizontalPlayer);
        this.topEdge = new Field(-2, -1, verticalPlayer);

        for(int y = 0; y < dimensions; y++) {
            for(int x = 0; x < dimensions; x++) {
                this.fields[y][x] = new Field(y, x);
            }
        }

        // novaPartija() is hopefully not timed... :?
        for(int y = 0; y < dimensions; y++) {
            for(int x = 0; x < dimensions; x++) {
                Field f = fields[y][x];

                if(y == 0) {
                    f.addNeighbour(this.topEdge);
                    this.topEdge.addNeighbour(f);
                } else if(y == (dimensions - 1)) {
                    f.addNeighbour(this.bottomEdge);
                    this.bottomEdge.addNeighbour(f);
                }

                if(x == 0) {
                    f.addNeighbour(this.leftEdge);
                    this.leftEdge.addNeighbour(f);
                } else if(x == (dimensions - 1)) {
                    f.addNeighbour(this.rightEdge);
                    this.rightEdge.addNeighbour(f);
                }

                for(int linkY = Math.max(y - 1, 0); linkY < Math.min(y + 2, dimensions); linkY++) {
                    for(int linkX = Math.max(x - 1, 0); linkX < Math.min(x + 2, dimensions); linkX++) {
                        if((linkY == y) && (linkX == x)) {
                            continue;
                        }
                        if((linkX - x) == (linkY - y)) {
                            continue;
                        }

                        f.addNeighbour(this.fields[linkY][linkX]);
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
        return getFreeFields(false);
    }

    public ArrayList<Field> getFreeFields(boolean sim) {
        ArrayList<Field> free = new ArrayList<>();

        for(Field[] row : fields) {
            for(Field f : row) {
                if(f.isFree(sim)) {
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

    public int getNumberOfMoves() {
        return nMoves;
    }

    public int getNFree() {
        return nFields - nMoves;
    }

    public int getDimensions() {
        return this.fields.length;
    }

    public boolean isFree(int y, int x) {
        int x1 = negativeIndex(x);
        int y1 = negativeIndex(y);

        return fields[y1][x1].isFree();
    }

    public boolean edgesConnected(Owner owner) {
        return edgesConnected(owner, false);
    }

    public boolean edgesConnected(Owner owner, boolean sim) {
        Field first;
        Field second;

        if(bottomEdge.getOwner(sim) == owner) {
            first = topEdge;
            second = bottomEdge;
        } else {
            first = leftEdge;
            second = rightEdge;
        }

        HashSet<Field> visited = new HashSet<>();
        return connectionExists(first, second, visited, sim);
    }

    public void resetSim() {
        for(int y = 0; y < fields.length; y++) {
            for(int x = 0; x < fields.length; x++) {
                fields[y][x].resetSimOwner();
            }
        }
    }

    private boolean connectionExists(Field start, Field finish, HashSet<Field> visited, boolean sim) {
        ArrayList<Field> neighbours = start.getNeighbours(start.getOwner(sim), sim);

        for(Field neighbour : neighbours) {
            if(visited.contains(neighbour)) {
                continue;
            }

            if(neighbour.equals(finish)) {
                return true;
            }

            visited.add(neighbour);
            boolean connected = connectionExists(neighbour, finish, visited, sim);
            if(connected) {
                return true;
            }
        }

        return false;
    }

    private int negativeIndex(int idx) {
        return (idx < 0) ? (fields.length + idx - 1) : idx;
    }
}
