package s63150020;

import java.util.ArrayList;

import skupno.Polje;


enum Owner { 
    Me, Other, AssumePlayed, Empty 
}

public class Field {

    /**
     * Field is on the red edge.
     */
    private final boolean redBorder;

    /**
     * Field is on the blue edge.
     */
    private final boolean blueBorder;

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
    private ArrayList<Field> neighbours = new ArrayList<>();

    public Field(int y, int x, int boardDimensions) {
        if((y == 0) || (y == (boardDimensions - 1))) {
            this.redBorder = true;
        } else {
            this.redBorder = false;
        }

        if((x == 0) || (x == (boardDimensions - 1))) {
            this.blueBorder = true;
        } else {
            this.blueBorder = false;
        }

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
