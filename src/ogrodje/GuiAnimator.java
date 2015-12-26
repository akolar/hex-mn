
package ogrodje;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/** 
 * To je abstraktni nadrazred za vse animacijske razrede.
 */
public abstract class GuiAnimator {

    /** na koliko milisekund se proži animacijski časovnik */
    private static final int INTERVAL_ANIMACIJSKEGA_CASOVNIKA = 50;

    /** animacijski časovnik */
    private final Timer animacijskiCasovnik;

    /** Ustvari nov objekt tipa `GuiAnimator'.
     * @param gostitelj Objekt, ki bo uporabljal storitve novoustvarjenega
     *    animatorja.  Gostitelj mora implementirati metodi, ki se kličeta
     *    po vsakem vmesnem oziroma po končnem koraku animacije. */
    protected GuiAnimator(final GuiGostiteljAnimacije gostitelj) {
        this.animacijskiCasovnik = new Timer(
            INTERVAL_ANIMACIJSKEGA_CASOVNIKA,
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // metoda `korak' vrne true natanko tedaj, ko se izvede
                    // končni korak animacije
                    if (GuiAnimator.this.korak()) {
                        gostitelj.poKoncuAnimacije();
                    } else {
                        gostitelj.poVmesnemKorakuAnimacije();
                    }
                }
            });
    }

    /** Sproži animacijo. */
    public void sprozi() {
        this.zacetek();
        this.animacijskiCasovnik.start();
    }

    /** Ustavi animacijo. */
    public void ustavi() {
        this.animacijskiCasovnik.stop();
    }

    /** Določi, kaj naj se zgodi na začetku animacije.  V tej metodi naj
     * se inicializirajo spremenljivke animacije. */
    protected abstract void zacetek();

    /** Določi, kaj naj se zgodi v vsakem koraku animacije. V tej metodi naj
     * se posodobijo spremenljivke animacije.  Metoda `korak' ni namenjena za
     * osveževanje prikaza; to naj se izvrši v gostiteljevih metodah
     * `poVmesnemKorakuAnimacije' in `poKoncuAnimacije'.
     * @return true, če gre za zadnji korak animacije; false sicer
     */
    protected abstract boolean korak();
}