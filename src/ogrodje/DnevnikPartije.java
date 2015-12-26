
package ogrodje;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import skupno.Polje;

/**
 * Objekt tega razreda predstavlja dnevnik posamezne partije.  Dnevnik se
 * zapiše v datoteko, ki je podana ob opcijskem parametru -d.
 */
public class DnevnikPartije {

    /** Objekt tega razreda predstavlja zapis o potezi v dnevniku. */
    private static class ZapisPoteze {
        /** indeks igralca, ki je odigral potezo this */
        private final int akter;

        /** polje, na katero je akter postavil žeton */
        private final Polje polje;

        public ZapisPoteze(int akter, Polje polje) {
            this.akter = akter;
            this.polje = polje;
        }

        @Override
        public String toString() {
            return String.format("%d --> %s", this.akter + 1, this.polje);
        }
    }

    /** dolžina stranice igralne površine */
    private int stranica;

    /** številka partije, na katero se nanaša dnevnik this. */
    private int stevilkaPartije;

    /** ime prvega igralca */
    private String prviIgralec;

    /** ime drugega igralca */
    private String drugiIgralec;

    /** indeks začetnika partije: 0 (prvi igralec) / 1 (drugi igralec) */
    private int ixZacetnikaPartije;

    /** datum in čas ob pričetku partije */
    private Calendar casovnaZnacka;

    /** posamezne poteze v partiji */
    private final List<ZapisPoteze> poteze;

    /** izid partije */
    private Izid izid;

    /** ~[ix]: točkovni izkupiček igralca ix */
    private int[] skupneTocke;

    public DnevnikPartije() {
        this.poteze = new ArrayList<>();
    }

    /**
     * Inicializira dnevnik ob pričetku partije.
     * 
     * @param stranica  število polj po stranici igralne površine
     * @param igralca  podatki o igralcih
     * @param ixZacetnikaPartije  indeks začetnika partije (0 ali 1)
     * @param stevilkaPartije  zaporedna številka partije
     */
    public void pricni(int stranica, Igralec[] igralca, int ixZacetnikaPartije, int stevilkaPartije) {
        this.stranica = stranica;
        this.stevilkaPartije = stevilkaPartije;
        this.prviIgralec = igralca[0].ime();
        this.drugiIgralec = igralca[1].ime();
        this.casovnaZnacka = Calendar.getInstance();
        this.poteze.clear();
        this.izid = null;
        this.skupneTocke = null;
        this.ixZacetnikaPartije = ixZacetnikaPartije;
    }

    /** 
     * Doda podano potezo partije v dnevnik this.
     * 
     * @param akter kdo je odigral potezo (0 ali 1)
     * @param polje na katero polje je akter postavil žeton
     */
    public void dodajPotezo(int akter, Polje polje) {
        this.poteze.add(new ZapisPoteze(akter, polje));
    }

    /** 
     * Doda podatke o zaključku partije v dnevnik this.
     * 
     * @param izid  izid partije
     * @param skupneTocke  trenutno število točk za oba igralca
     */
    public void zakljuci(Izid izid, int[] skupneTocke) {
        this.izid = izid;
        this.skupneTocke = skupneTocke;
    }

    /** Na podlagi podatkov v dnevniku this vrne niz za izpis v datoteko. */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("---------- Partija %d ----------%n", this.stevilkaPartije));
        builder.append(String.format("Stranica: %d%n", this.stranica));
        builder.append(String.format("Igralec 1: %s%n", this.prviIgralec));
        builder.append(String.format("Igralec 2: %s%n", this.drugiIgralec));
        builder.append(String.format("Prične: %d%n", this.ixZacetnikaPartije + 1));
        builder.append(String.format("Čas pričetka: %02d.%02d.%d %02d:%02d:%02d.%03d%n", 
                this.casovnaZnacka.get(Calendar.DAY_OF_MONTH),
                this.casovnaZnacka.get(Calendar.MONTH) + 1,
                this.casovnaZnacka.get(Calendar.YEAR),
                this.casovnaZnacka.get(Calendar.HOUR_OF_DAY),
                this.casovnaZnacka.get(Calendar.MINUTE),
                this.casovnaZnacka.get(Calendar.SECOND),
                this.casovnaZnacka.get(Calendar.MILLISECOND)
        ));
        builder.append(String.format("Število polpotez: %d%n", this.poteze.size()));
        int i = 1;
        for (ZapisPoteze poteza: this.poteze) {
            builder.append(String.format("(%d) %s%n", (i + 1) / 2, poteza.toString()));
            i++;
        }
        if (this.skupneTocke != null) {
            builder.append(String.format("Zmagovalec: %s%n", 
                    (this.izid == null) ? ("?") : (Integer.toString(this.izid.zmagovalec() + 1))));
            builder.append(String.format("Opis izida: %s%n", this.izid));
            builder.append(String.format("Točke: %d / %d%n", this.skupneTocke[0], this.skupneTocke[1]));
        }
        builder.append(Razno.NL);
        return builder.toString();
    }
}