package s63150020;

import java.util.ArrayList;

import skupno.Polje;


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

    public Field(int y, int x, Owner owner) {
        this(y, x);
        this.owner = owner;
        this.simOwner = owner;
    }

    public Field(int y, int x) {
        this.y = y;
        this.x = x;

        neighbours = new ArrayList<>(8);
    }

    public void addNeighbour(Field other) {
        neighbours.add(other);
    }

    public void setOwner(Owner newOwner) {
        if((owner == Owner.Me) || (owner == Owner.Other)) {
            return;
        }

        this.owner = newOwner;
        this.simOwner = newOwner;
    }

    public void simOwner(Owner newOwner) {
        if((simOwner == Owner.Me) || (simOwner == Owner.Other)) {
            return;
        }

        this.simOwner = newOwner;
    }

    public void resetSimOwner() {
        simOwner = owner;
    }

    public boolean isFree() {
        return isFree(false);
    }

    public boolean isFree(boolean sim) {
        if(sim) {
            return (simOwner == Owner.Empty) || (simOwner == Owner.AssumePlayed);
        }

        return (owner == Owner.Empty) || (owner == Owner.AssumePlayed);
    }

    public boolean isAssumed() {
        return owner == Owner.AssumePlayed;
    }
    
    public Polje toPolje() {
        return new Polje(y, x);
    }

    public Owner getOwner() {
        return getOwner(false);
    }
    
    public Owner getOwner(boolean sim) {
        return sim ? simOwner : owner;
    }

    public ArrayList<Field> getNeighbours() {
        return neighbours;
    }

    public ArrayList<Field> getNeighbours(Owner owner) {
        return getNeighbours(owner, false);
    }

    public ArrayList<Field> getNeighbours(Owner owner, boolean sim) {
        ArrayList<Field> fields = new ArrayList<>();
        
        for(Field f : neighbours) {
            if(f.getOwner(sim) == owner) {
                fields.add(f);
            }
        }

        return fields;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

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

    public int hashCode() {
        return (y << 4) + x;
    }
}
