
package s12345678;

import skupno.Stroj;
import skupno.Polje;
import java.util.Random;

/**
 * Objekt tega razreda je stroj, ki poteze izbira povsem naključno, vendar pa
 * nikoli ne izbere neveljavne poteze in se strogo drži predpisanih časovnih
 * omejitev.
 *
 * @author Naključko Randomè, Fakulteta za naključne študije
 */
public class Stroj_Nakljucko implements Stroj {

    /** generator naključnih števil */
    private final Random nakljucniGenerator;

    /** dolžina stranice */
    private int stranica;

    /** [i][j]: true natanko v primeru, ko je polje (i, j) zasedeno */
    private boolean[][] zasedeno;

    /**
     * V konstruktorju inicializiram zgolj generator naključnih števil.
     */
    public Stroj_Nakljucko() {
        this.nakljucniGenerator = new Random();
    }

    /** 
     * Ob pričetku partije inicializiram tabelo zasedenosti polj.  Mene sploh
     * ne zanima, ali sem rdeč ali moder, tebe, dragi(-a) tekmovalec(-ka), pa
     * najbrž bo.
     */
    @Override
    public void novaPartija(int stranica, boolean semRdec) {
        this.stranica = stranica;
        this.zasedeno = new boolean[stranica][stranica];
    }

    /**
     * Ko sem na vrsti za potezo, naključno izberem eno od prostih polj.
     */
    @Override
    public Polje izberiPotezo(long preostaliCas) {
        // tabela prostih polj;
        // [i][0]: indeks vrstice i-tega prostega polja
        // [i][1]: indeks stolpca i-tega prostega polja
        int[][] prostaPolja = new int[this.stranica * this.stranica][2];
        int stProstih = 0;

        // zberi položaje prostih polj v tabelo
        for (int i = 0;  i < this.stranica;  i++) {
            for (int j = 0;  j < this.stranica;  j++) {
                if (!this.zasedeno[i][j]) {
                    prostaPolja[stProstih][0] = i;
                    prostaPolja[stProstih][1] = j;
                    stProstih++;
                }
            }
        }

        // naključno izberi eno od prostih polj
        int izbraniIndeks = this.nakljucniGenerator.nextInt(stProstih);
        int vrstica = prostaPolja[izbraniIndeks][0];
        int stolpec = prostaPolja[izbraniIndeks][1];

        // označi, da je polje zasedeno, da ga ne boš izbral še enkrat
        this.zasedeno[vrstica][stolpec] = true;

        return new Polje(vrstica, stolpec);
    }

    /**
     * Ko potezo odigra moj nasprotnik, si zabeležim, da polje, na katero je
     * postavil žeton, ni več prosto.
     */
    @Override
    public void sprejmiPotezo(Polje polje) {
        int vrstica = polje.vrniVrstico();
        int stolpec = polje.vrniStolpec();
        this.zasedeno[vrstica][stolpec] = true;
    }

    /**
     * Ko se partija zaključi, mi ni treba storiti ničesar, saj mi točke niso
     * pomembne ...
     */
    @Override
    public void rezultat(boolean zmagal) {
    }
}
