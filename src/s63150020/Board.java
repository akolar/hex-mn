package s63150020;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;

import skupno.Polje;


/**
 * This class is represents a playing board for a game of Hex.
 * It contains the computer-friendly representation of the board
 * itself, along with some additional information about the current
 * game.
 *
 * This class also provides methods for checking the current status of 
 * the game.
 */
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

    /**
     * Creates new Board object.
     *
     * @param dimensions number of fields on one side of the board
     * @param playerIsVertical true iff the player controlled by this 
     *                         engine tries to connect vertical edges
     */
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

    /**
     * Sets the owner for the field on coordinates described by `field`.
     *
     * @param owner new "owner" of the field
     * @param field field, to which we are setting the value to
     */
    public void play(Owner player, Polje field) {
        fields[field.vrniVrstico()][field.vrniStolpec()].setOwner(player);

        if(player == Owner.Me) {
            nMyMoves++;
            nMoves++;
        } else if(player == Owner.Other) {
            nMoves++;
        } // else: Owner.AssumePlayed
    }

    /**
     * Sets the owner for the field on coordinates `x`, `y`.
     *
     * @param owner new "owner" of the field
     * @param y the y coordinate
     * @param x the x coordinate
     * @return field where owner has placed his piece
     */
    public Polje play(Owner player, int y, int x) {
        int y1 = negativeIndex(y);
        int x1 = negativeIndex(x);

        fields[y1][x1].setOwner(player);
        return new Polje(y1, x1);
    }

    /**
     * Returns the list of free fields on the board.
     * This method does the same as calling `getFreeFields(false)`.
     *
     * @return the list of free fields.
     */
    public ArrayList<Field> getFreeFields() {
        return getFreeFields(false);
    }

    /**
     * Returns the list of free fields on the board.
     *
     * @param sim true iff the method should return fields, that are free in
     *            the simulated game, otherwise returns free fields of the
     *            real game
     * @return the list of free fields.
     */
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

    /**
     * Returns the list of fields that are implicitly connected.
     *
     * @return list of fields that are assumed connected
     */
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

    /**
     * Returns the number of moves played in the current game.
     *
     * @return number of moves in the game
     */
    public int getNumberOfMoves() {
        return nMoves;
    }

    /**
     * Returns the number of free fields on the board.
     *
     * @return number of free fields
     */
    public int getNFree() {
        return nFields - nMoves;
    }

    /**
     * Returns the dimensions of the board.
     *
     * @return dimensions of the board
     */
    public int getDimensions() {
        return this.fields.length;
    }

    /**
     * Returns true when the field specified by the coordinates `x`, `y` 
     * is free.
     *
     * @param y the y coordinate of the field we want to check status of
     * @param x the x coordinate of the field we want to check status of
     * @return true if the field is free
     */
    public boolean isFree(int y, int x) {
        int x1 = negativeIndex(x);
        int y1 = negativeIndex(y);

        return fields[y1][x1].isFree();
    }


    /**
     * Returns true iff the edges of the board are connected by the `owner`.
     * This method does the same as calling `edgesConnected(owner, false)`.
     *
     *
     * @param owner player, for whom we are checking the connection of edges
     * @return true, if player has connected the edges
     */
    public boolean edgesConnected(Owner owner) {
        return edgesConnected(owner, false);
    }

    /**
     * Returns true iff the edges of the (simulated) board are connected by 
     * the `owner`.
     *
     * @param owner player, for whom we are checking the connection of edges
     * @param sim true if we are checking the simulated game
     * @return true, if player has connected the edges
     */
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

    /**
     * Resets the simulated game to the status of the real game.
     */
    public void resetSim() {
        for(int y = 0; y < fields.length; y++) {
            for(int x = 0; x < fields.length; x++) {
                fields[y][x].resetSimOwner();
            }
        }
    }

    /**
     * Checks if there is a connection between the `start` and `finish` pieces.
     */
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

    /**
     * Converts the negative indexing of fields to their respective positive 
     * values.
     *
     * For example:
     * If we play our piece at (-1, -1), this method will convert the
     * coordinates to (boardDimensions - 1, boardDimensions - 1).
     *
     * To avoid ambiguity, the coordinate -0 is interpreted as 0, therefore
     * playing on the furthermost right/bottom column/row using negative
     * indexing is not possible.
     */
    private int negativeIndex(int idx) {
        return (idx < 0) ? (fields.length + idx - 1) : idx;
    }
}
