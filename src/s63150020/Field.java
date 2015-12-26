package s63150020;

import java.util.ArrayList;

import skupno.Polje;


enum Owner { 
    Me, Other, AssumePlayed, Empty 
}

public class Field {

    /**
     * Owner of this field.
     */
    private Owner owner = Owner.Empty;

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
    private ArrayList<Field> neighbours = new ArrayList<>(8);

    public Field(int y, int x, Owner owner) {
        this(y, x);
        this.owner = owner;
    }

    public Field(int y, int x) {
        this.y = y;
        this.x = x;
    }

    public void addNeighbour(Field other) {
        neighbours.add(other);
    }

    public void setOwner(Owner newOwner) {
        if((owner == Owner.Me) || (owner == Owner.Other)) {
            return;
        }

        this.owner = newOwner;
    }

    public boolean isFree() {
        return (owner == Owner.Empty) || (owner == Owner.AssumePlayed);
    }

    public boolean isAssumed() {
        return owner == Owner.AssumePlayed;
    }
    
    public Polje toPolje() {
        return new Polje(y, x);
    }
}
