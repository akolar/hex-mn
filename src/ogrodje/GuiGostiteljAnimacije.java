
package ogrodje;

/**
 * Ta vmesnik morajo implementirati vsi razredi, ki si pri animaciji želijo
 * pomagati z objektom razreda `GuiAnimator'.
 */
public interface GuiGostiteljAnimacije {

    /** Ta metoda se pokliče po vsakem vmesnem koraku animacije. */
    public void poVmesnemKorakuAnimacije();

    /** Ta metoda se pokliče po zadnjem koraku animacije. */
    public void poKoncuAnimacije();
}