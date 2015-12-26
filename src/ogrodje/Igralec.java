
package ogrodje;

import skupno.Stroj;
import skupno.Polje;

/**
 * Objekt tega razreda predstavlja osnovne podatke o igralcu: njegovo
 * `naravo' (človek ali stroj) in preostali čas do konca trenutne partije.
 */
public class Igralec {

    private static final String IME_CLOVEKA = "Človek";

    /** objekt, ki bo deloval kot stroj; null, če bo to človek */
    private final Stroj stroj;

    /** razred, ki mu pripada stroj */
    private final String razred;

    /** čas v milisekundah, ki ga ima igralec `this' na voljo do konca partije */
    private long preostaliCas;

    /** milisekundni sistemski čas (System.currentTimeMillis()) ob pričetku 
     * odštevanja časa; -1, če časa trenutno ne odštevamo */
    private long stoparica;

    /** časovne razlike, manjše od MIN_CASOVNA_RAZLIKA sekund, zanemarimo */
    private static final long MIN_CASOVNA_RAZLIKA = 50;

    /**
     * Ustvari objekt, ki predstavlja igralca.
     * @param razred  polno ime javanskega razreda, čigar objekt bo deloval kot 
     *                stroj (null: človeški igralec)
     * @param stroj  objekt, ki bo deloval kot stroj
     */
    private Igralec(String razred, Stroj stroj) {
        this.razred = razred;
        this.stroj = stroj;
        this.preostaliCas = 0L;
        this.stoparica = -1;
    }

    /**
     * Ustvari in vrne nov objekt tipa igralec.
     * @param razred ime razreda, ki mu pripada stroj; null, če gre za
     *               človeškega igralca
     */
    public static Igralec ustvari(String razred) {
        if (razred == null) {
            // človek
            return new Igralec(null, null);
        }
        // stroj
        try {
            Stroj stroj = (Stroj) Class.forName(razred).newInstance();
            return new Igralec(razred, stroj);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            System.err.printf("Napaka pri ustvarjanju objekta razreda %s. "
                    + "Morda razred ne obstaja?%n", razred);
            return null;
        }
    }

    /** Vrne niz IME_CLOVEKA, če gre za človeka, sicer pa vrne polno ime razreda,
     * ki mu pripada stroj. */
    public String ime() {
        if (this.stroj == null) {
            return IME_CLOVEKA;
        }
        return this.razred;
    }

    /** Vrne niz IME_CLOVEKA, če gre za človeka, sicer pa vrne kratko ime
     * razreda, ki mu pripada stroj (torej ime brez naziva paketa in predpone
     * Stroj_). */
    public String kratkoIme() {
        if (this.stroj == null) {
            return IME_CLOVEKA;
        }
        String str = this.razred;
        int indeks;
        if ((indeks = str.indexOf('_')) >= 0) {
            str = str.substring(indeks + 1);
        }
        return str;
    }

    /** Vrne true natanko v primeru, če je igralec this stroj. */
    public boolean jeStroj() {
        return this.stroj != null;
    }

    /** Vrne true natanko v primeru, če je igralec this človek. */
    public boolean jeClovek() {
        return !this.jeStroj();
    }

    /** Vrne true natanko v primeru, če ima igralec this časovno omejitev. 
     * Človek nima nikoli časovne omejitve. */
    public boolean imaOmejenCas() {
        return (this.jeStroj() && Parametri.vrniCasovnoOmejitev() > 0);
    }

    /** Nastavi preostali čas na podano število milisekund. */
    public void nastaviPreostaliCas(long preostaliCas) {
        this.preostaliCas = preostaliCas;
        this.stoparica = -1;
    }

    /** Vrne preostali čas. */
    public long vrniPreostaliCas() {
        if (this.stoparica >= 0) {
            return this.preostaliCas - (System.currentTimeMillis() - this.stoparica);
        }
        return this.preostaliCas;
    }

    /** Sproži odštevanje časa. */
    private void sproziStoparico() {
        this.stoparica = System.currentTimeMillis();
    }

    /** Ustavi odštevanje časa in posodobi preostali čas. */
    private void ustaviStoparico() {
        long razlika = (System.currentTimeMillis() - this.stoparica);
        // zanemari časovne razlike, manjše od MIN_CASOVNA_RAZLIKA milisekund
        if (razlika < MIN_CASOVNA_RAZLIKA) {
            razlika = 0;
        }
        this.preostaliCas -= razlika;
        this.stoparica = -1;
    }

    /** Če je igralec this stroj, metoda pokliče njegovo metodo novaPartija
     * in odšteje porabljeni čas od preostalega časa, ki ga ima stroj na
     * voljo za preostanek partije.  Če je igralec this človek, metoda ne
     * naredi ničesar. */
    public void novaPartija(int stranica, boolean semRdec) {
        if (this.jeStroj()) {
            this.sproziStoparico();
            this.stroj.novaPartija(stranica, semRdec);
            this.ustaviStoparico();
        }
    }

    /** Deluje enako kot metoda novaPartija, le da kliče strojevo metodo vrzi. */
    public Polje izberiPotezo() {
        if (this.jeStroj()) {
            this.sproziStoparico();
            Polje polje = this.stroj.izberiPotezo(this.preostaliCas);
            this.ustaviStoparico();
            return polje;
        }
        return null;
    }

    /** Deluje enako kot metoda novaPartija, le da kliče strojevo metodo sprejmiPotezo. */
    public void sprejmiPotezo(Polje polje) {
        if (this.jeStroj()) {
            this.sproziStoparico();
            this.stroj.sprejmiPotezo(polje);
            this.ustaviStoparico();
        }
    }

    /** Če je igralec this stroj, kliče strojevo metodo `rezultat', sicer pa
     * ne naredi ničesar. */
    public void rezultat(boolean zmagal) {
        if (this.jeStroj()) {
            this.stroj.rezultat(zmagal);
        }
    }
}
