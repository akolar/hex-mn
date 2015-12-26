
package ogrodje;

/**
 * Ogrodje za tekmovanje strojev v igri Hex.
 * <p/>
 * Ta razred služi kot vstopna točka v ogrodje.
 * 
 * @author Luka Fürst, 2015
 * @version 1.0
 */
public class Hex {

    public static void main(String[] args) {
        // izlušči parametre ukazne vrstice
        if (!Parametri.izlusci(args)) {
            System.exit(1);
        }
        Seansa seansa = Seansa.ustvari();
        if (seansa == null) {
            System.exit(1);
        }

        if (Parametri.jeGUI()) {
            // prični seanso v grafičnem vmesniku
            Gui gui = new Gui();
            gui.inicializiraj(seansa);

        } else {
            // prični seanso v tekstovnem vmesniku
            seansa.izvediT();
        }
    }
}