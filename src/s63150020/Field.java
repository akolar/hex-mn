package s63150020;

import java.util.ArrayList;

import skupno.Polje;


/**
 * This class represent a single field on the Hex' game board.
 * It contains information about the fields's position and the owner,
 * list of neighbouring fields and provides the option to play game simulations.
 */
public class Field {

    /**
     * Owner of this field.
     */
    private Owner owner = Owner.Empty;

    /**
     * Owner of this field when running simulations.
     */
    private Owner simOwner = Owner.Empty;

    /**
     * Vertical position of this field.
     */
    private final int y;

    /**
     * Horizontal position of this field
     */
    private final int x;

    /**
     * List of neighbouring fields.
     */
    private ArrayList<Field> neighbours; 

    /**
     * Creates new instance of a field.
     *
     * @param y the y coordinate on the board
     * @param x the x coordinate on the board 
     * @param owner default owner of the field
     */
    public Field(int y, int x, Owner owner) {
        this(y, x);
        this.owner = owner;
        this.simOwner = owner;
    }

    /**
     * Creates new instance of a field.
     *
     * @param y the y coordinate on the board
     * @param x the x coordinate on the board 
     */
    public Field(int y, int x) {
        this.y = y;
        this.x = x;

        neighbours = new ArrayList<>(8);
    }

    /**
     * Adds a neighbouring field to the current one.
     *
     * @param other the neighbour
     */
    public void addNeighbour(Field other) {
        neighbours.add(other);
    }

    /**
     * Sets the owner information to the value of `newOwner` for the current 
     * field.
     *
     * @param owner the new owner of this field
     */
    public void setOwner(Owner newOwner) {
        if((owner == Owner.Me) || (owner == Owner.Other)) {
            return;
        }

        this.owner = newOwner;
        this.simOwner = newOwner;
    }

    /**
     * Sets the owner information to the value of `newOwner` for the current 
     * field when running game sims.
     *
     * @param owner the simulated owner of this field
     */
    public void simOwner(Owner newOwner) {
        if((simOwner == Owner.Me) || (simOwner == Owner.Other)) {
            return;
        }

        this.simOwner = newOwner;
    }

    /**
     * Resets the value of the simulated owner to the real one.
     */
    public void resetSimOwner() {
        simOwner = owner;
    }

    /**
     * Returns true iff this field has no owner in the real game.
     *
     * Analogue to `isFree(false)`.
     *
     * @return true if this field has no owner
     */
    public boolean isFree() {
        return isFree(false);
    }

    /**
     * Returns true iff this field has no owner in the real or simulated game.
     *
     * @param sim true if checking for the simulated game
     * @return true if this field has no (simulated) owner
     */
    public boolean isFree(boolean sim) {
        if(sim) {
            return (simOwner == Owner.Empty) || (simOwner == Owner.AssumePlayed);
        }

        return (owner == Owner.Empty) || (owner == Owner.AssumePlayed);
    }

    /**
     * Returns true iff this field is assumed as played.
     *
     * @return true if assumed as played
     */
    public boolean isAssumed() {
        return owner == Owner.AssumePlayed;
    }
    
    /**
     * Converts this field to the `Polje` object.
     *
     * @return `Polje` representation of this field
     */
    public Polje toPolje() {
        return new Polje(y, x);
    }


    /**
     * Returns the owner of this field for the real game.
     *
     * @return owner of this field
     */
    public Owner getOwner() {
        return getOwner(false);
    }
    
    /**
     * Returns the owner of this field for the real or simulated game.
     *
     * @param sim true if checking for the simulated game
     * @return owner of this field in real/simulated game
     */
    public Owner getOwner(boolean sim) {
        return sim ? simOwner : owner;
    }

    /**
     * Returns the list of all neighbours for the current field. 
     *
     * @return list of neighbours
     */
    public ArrayList<Field> getNeighbours() {
        return neighbours;
    }

    /**
     * Returns the list of neighbours owned by `owner` for the current field 
     * in the live game.
     *
     * Preforms same as `getNeighbours(owner, false)`.
     *
     * @param owner owner that has to own the neighbouring fields
     * @return list of neighbours
     */
    public ArrayList<Field> getNeighbours(Owner owner) {
        return getNeighbours(owner, false);
    }

    /**
     * Returns the list of neighbours owned by `owner` for the current field 
     * in the live or simulated game.
     *
     * Preforms same as `getNeighbours(owner, false)`.
     *
     * @param owner owner that has to own the neighbouring fields
     * @param sim true if checking for the simulated game
     * @return list of neighbours
     */
    public ArrayList<Field> getNeighbours(Owner owner, boolean sim) {
        ArrayList<Field> fields = new ArrayList<>();
        
        for(Field f : neighbours) {
            if(f.getOwner(sim) == owner) {
                fields.add(f);
            }
        }

        return fields;
    }

    /**
     * Returns the y coordinate of the current field.
     *
     * @return y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the x coordinate of the current field.
     *
     * @return x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns a `String` representation of the current field.
     *
     * The y and x coordinates are separated by colon (":").
     * If this is an virtual field that represents an edge, returns
     * its description.
     */
    public String toString() {
        if((x == -2) && (y == -2)) {
            return "Left edge";
        } else if(y == -2) {
            return "Top edge";
        } else if((y == -1) && (x == -1)) {
            return "Right edge";
        } else if(y == -1) {
            return "Bottom edge";
        }

        return String.format("Field: %d:%d", y, x);
    }

    /**
     * Compares the specified object with this field for equality. 
     * Returns true iff the other object is also type of `Field` and has
     * matching y and x coordinates (respectivly).
     *
     * @param o the other object
     * @return true iff the objects are equal
     */
    public boolean equals(Object o) {
        if(!(o instanceof Field)) {
            return false;
        }

        Field other = (Field) o;

        if(o == this) {
            return true;
        }

        return (this.x == other.getX()) && (this.y == other.getY());
    }

    /**
     * Returns the hash code for this field.
     *
     * @return the hash code
     */
    public int hashCode() {
        return (y << 4) + x;
    }
}
