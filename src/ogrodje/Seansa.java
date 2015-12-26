
package ogrodje;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Objekt tega razreda predstavlja celotno `seanso' igre Hex. Seansa je
 * sestavljena iz posameznih partij. Za pravilen potek in vzdrževanje stanja
 * posamezne partije skrbita objekta tipa Partija in GuiNadzornik.
 */
public class Seansa {

    /** osnovni podatki o igralcih;
     * [0]: prvi igralec (igralec, ki prične prvo partijo seanse)
     * [1]: drugi igralec */
    private final Igralec[] igralca;

    /** trenutna partija */
    private Partija partija;

    /** kdo prične trenutno partijo (0 ali 1) */
    private int zacetnikPartije;

    /** zaporedna številka trenutne partije */
    private int stevilkaPartije;

    /** dnevnik trenutne partije */
    private DnevnikPartije dnevnikPartije;

    /** ~[0]: skupno število točk prvega igralca;
     *  ~[1]: skupno število točk drugega igralca */
    private int[] stTock;

    /** Na podlagi podanih parametrov ustvari oba objekta tipa Igralec in vrne
     * objekt tipa Seansa. Če objektov podanih razredov ni mogoče ustvariti,
     * vrne null. */
    public static Seansa ustvari() {
        Igralec[] igralca = new Igralec[2];
        String[] razredaIgralcev = Parametri.vrniRazredaIgralcev();
        igralca[0] = Igralec.ustvari(razredaIgralcev[0]);
        igralca[1] = Igralec.ustvari(razredaIgralcev[1]);
        if (igralca[0] == null || igralca[1] == null) {
            return null;
        }
        int zacetnikPrvePartije = 0;

        // v igri človeka proti stroju naj bo človek vedno prvi igralec
        if (igralca[0].jeStroj() && igralca[1].jeClovek()) {
            Igralec zacasni = igralca[0];
            igralca[0] = igralca[1];
            igralca[1] = zacasni;
            zacetnikPrvePartije = 1;
        }

        Seansa seansa = new Seansa(igralca, zacetnikPrvePartije);
        seansa.partija = new Partija(seansa);

        return seansa;
    }

    /**
     * Ustvari objekt, ki predstavlja seanso.
     * @param igralca  igralca v seansi
     * @param zacetnikPrvePartije  indeks igralca (0 ali 1), ki prične prvo partijo
     */
    private Seansa(Igralec[] igralca, int zacetnikPrvePartije) {
        this.igralca = igralca;
        this.stevilkaPartije = 1;
        this.zacetnikPartije = zacetnikPrvePartije;
        this.stTock = new int[2];
        this.dnevnikPartije = new DnevnikPartije();
    }

    /**
     * Posodobi atribute ob pričetku partije.
     * @return true natanko v primeru, če se nova partija lahko prične
     *         (ker njena zaporedna številka ni večja od ciljnega števila partij)
     */
    public boolean pricniPartijo() {
        int stPartij = Parametri.vrniSteviloPartij();
        if (this.strojVsStroj() && stPartij > 1 && this.stevilkaPartije > stPartij) {
            return false;
        }
        this.partija.pricni(this.zacetnikPartije);
        this.dnevnikPartije.pricni(Parametri.vrniStranico(), this.igralca,
                this.zacetnikPartije, this.stevilkaPartije);
        return true;
    }

    /** Vrne referenco na objekt, ki predstavlja trenutno partijo. */
    public Partija vrniPartijo() {
        return this.partija;
    }

    /** Vrni indeks igralca (0 ali 1), ki prične trenutno partijo. */
    public int vrniZacetnikaPartije() {
        return this.zacetnikPartije;
    }

    /** Vrne zaporedno številko trenutne partije. */
    public int vrniStevilkoPartije() {
        return this.stevilkaPartije;
    }

    /** Vrne dnevnik partije. */
    public DnevnikPartije vrniDnevnikPartije() {
        return this.dnevnikPartije;
    }

    /** Vrne oba igralca. */
    public Igralec[] vrniIgralca() {
        return this.igralca;
    }

    /** Vrne skupno število točk za prvega [0] in drugega [1] igralca. */
    public int[] tocke() {
        return this.stTock;
    }

    /** Vrne true natanko v primeru, ko stroj igra proti stroju. */
    public boolean strojVsStroj() {
        return this.igralca[0].jeStroj() && this.igralca[1].jeStroj();
    }

    /** Vrne true natanko v primeru, ko človek igra proti stroju. */
    public boolean clovekVsStroj() {
        return (!this.strojVsStroj() && this.vsajEnStroj());
    }

    /** Vrne true natanko v primeru, ko v igri sodeluje vsaj en stroj. */
    public boolean vsajEnStroj() {
        return (this.igralca[0].jeStroj() || this.igralca[1].jeStroj());
    }

    /** Vrne true natanko v primeru, ko človek igra proti človeku. */
    public boolean clovekVsClovek() {
        return !this.vsajEnStroj();
    }

    /** Vrne dolžino stranice igralne površine. */
    public int vrniStranico() {
        return Parametri.vrniStranico();
    }

    /** 
     * Posodobi atribute ob zaključku partije.
     * @param izida [0]: izid prvega igralca; [1]: izid drugega igralca
     */
    public void zakljuciPartijo(Izid izid) {
        this.stevilkaPartije++;
        if (Parametri.izmenicnoPricenjanje()) {
            this.zacetnikPartije = 1 - this.zacetnikPartije;
        }
        this.stTock[0] += izid.vrniTocke(0);
        this.stTock[1] += izid.vrniTocke(1);
        this.dnevnikPartije.zakljuci(izid, this.stTock);
        this.zapisiDnevnikVDatoteko();
    }

    /** Vrne true natanko v primeru, če je seanse konec. */
    public boolean jeKonec() {
        return (this.strojVsStroj() && Parametri.vrniSteviloPartij() >= 1 && 
                this.stevilkaPartije > Parametri.vrniSteviloPartij());
    }

    /** Izvede heksaško seanso v tekstovnem vmesniku. */
    public void izvediT() {
        this.dnevnikPartije = new DnevnikPartije();
        int steviloPartij = Math.max(Parametri.vrniSteviloPartij(), 1);
        int stranica = Parametri.vrniStranico();

        this.partija = new Partija(this);
        this.stTock = new int[2];
        this.stevilkaPartije = 1;

        while (this.stevilkaPartije <= steviloPartij) {
            // odigraj partijo in jo sproti beleži v dnevnik
            System.out.printf("Partija %d%n%n", stevilkaPartije);

            this.dnevnikPartije.pricni(stranica, this.igralca, this.zacetnikPartije, this.stevilkaPartije);
            Izid izid = this.partija.odigrajT(this.zacetnikPartije);

            System.out.println("Izid: " + izid);
            this.stTock[0] += izid.vrniTocke(0);
            this.stTock[1] += izid.vrniTocke(1);

            System.out.printf("Točke: %s%n", Arrays.toString(this.stTock));
            if (Parametri.izmenicnoPricenjanje()) {
                this.zacetnikPartije = 1 - this.zacetnikPartije;
            }
            System.out.println(Razno.zmnozekNiza(79, "-"));

            this.dnevnikPartije.zakljuci(izid, this.stTock);
            this.zapisiDnevnikVDatoteko();

            this.stevilkaPartije++;
        }
    }

    /** Shrani dnevnik v datoteko, če je parameter tako nastavljen. */
    private void zapisiDnevnikVDatoteko() {
        File dnevniskaDatoteka = Parametri.vrniDnevniskoDatoteko();
        if (dnevniskaDatoteka != null) {
            try (FileWriter fos = new FileWriter(dnevniskaDatoteka, true)) {
                fos.write(this.dnevnikPartije.toString());
            } catch (IOException ex) {
                System.err.printf("Napaka pri pisanju v datoteko %s%n", dnevniskaDatoteka);
            }
        }
    }
}
