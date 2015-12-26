
package ogrodje;

import java.util.List;
import skupno.Polje;
import java.util.Scanner;

/**
 * Objekt tega razreda hrani in vzdržuje stanje v okviru posamezne partije.
 */
public class Partija {

    /** seansa, katere del je partija `this' (seansa = zaporedje partij) */
    private final Seansa seansa;

    /** vsebina igralne površine */
    private Povrsina povrsina;

    /** kdo je trenutno na potezi (0 ali 1) */
    private int naPotezi;

    /** zaporedna številka naslednje poteze */
    private int stPoteze;

    /** izid partije */
    private Izid izid;

    /** veriga polj, ki dokazuje zmago */
    private List<Polje> zmagovalnaVeriga;

    /** bralnik standardnega vhoda */
    private Scanner bralnik;

    /** Ustvari objekt, ki predstavlja partijo kot del podane seanse. */
    public Partija(Seansa seansa) {
        this.seansa = seansa;
    }

    /** 
     * Prične partijo.
     * @param pricnePartijo indeks igralca (0 ali 1), ki prične partijo
     */
    public void pricni(int pricnePartijo) {
        int stranica = this.seansa.vrniStranico();
        this.povrsina = new Povrsina(stranica);

        this.naPotezi = pricnePartijo;
        this.izid = null;
        this.zmagovalnaVeriga = null;
        this.stPoteze = 1;
    }

    /** Vrne indeks igralca (0 ali 1), ki je trenutno na potezi. */
    public int vrniNaPotezi() {
        return this.naPotezi;
    }

    /** Vrne število odigranih potez. */
    public int vrniSteviloOdigranihPotez() {
        return this.stPoteze - 1;
    }

    /** Vrne objekt, ki predstavlja vsebino igralne površine. */
    public Povrsina vrniPovrsino() {
        return this.povrsina;
    }

    /**
     * Vrne true natanko v primeru, če izbrano polje predstavlja legalno
     * potezo.
     * @param izbranoPolje pravkar izbrano polje
     */
    public boolean preveriLegalnost(Polje izbranoPolje) {
        if (this.povrsina == null) {
            return false;
        }
        return this.povrsina.poljeVeljavnoInProsto(izbranoPolje);
    }

    /** Posodobi stanje partije po odigrani (veljavni) potezi. */
    public void posodobiPoPotezi(Polje poteza) {
        this.seansa.vrniDnevnikPartije().dodajPotezo(this.naPotezi, poteza);

        // posodobi površino
        Zeton zeton = Zeton.zaIgralca(this.naPotezi);
        this.povrsina.nastaviVsebinoPolja(poteza, Zeton.zaIgralca(this.naPotezi));

        // preveri zmago
        List<Polje> veriga = this.povrsina.zmagovalnaVeriga(zeton);
        if (veriga != null) {
            this.izid = Izid.zmaga(this.naPotezi);
            this.zmagovalnaVeriga = veriga;
        }

        // zamenjaj stran in povečaj števec potez
        if (this.izid == null) {
            this.naPotezi = 1 - this.naPotezi;
            this.stPoteze++;
        }
    }

    /** Vrne true natanko v primeru, če se je trenutna igra že končala.  */
    public boolean jeKonec() {
        return (this.izid != null);
    }

    /** Vrne seznam polj, ki tvorijo zmagovalno verigo. */
    public List<Polje> vrniZmagovalnoVerigo() {
        return this.zmagovalnaVeriga;
    }

    /** Umetno nastavi izid, npr. po odigrani neveljavni potezi. */
    public void nastaviIzid(Izid izid) {
        this.izid = izid;
    }

    /** Vrne izid partije. */
    public Izid vrniIzid() {
        return this.izid;
    }

    /** Predčasno zaključi partijo. */
    public void predcasnoKoncaj() {
        this.izid = Izid.PREDCASEN_ZAKLJUCEK;
    }

    /**
     * Odigra celotno partijo v tekstovnem vmesniku.
     * @param pricnePartijo indeks igralca, ki prične partijo
     * @return [0]: izid za igralca z indeksom 0;
     *         [1]: izid za igralca z indeksom 1
     */
    public Izid odigrajT(int pricnePartijo) {
        Igralec[] igralca = this.seansa.vrniIgralca();

        int stranica = Parametri.vrniStranico();

        // obvesti stroja o pričetku igre
        this.pricni(pricnePartijo);
        igralca[0].novaPartija(stranica, true);
        igralca[1].novaPartija(stranica, false);

        this.stPoteze = 1;
        this.naPotezi = pricnePartijo;
        this.zmagovalnaVeriga = null;

        this.bralnik = new Scanner(System.in);

        // potek partije
        while (this.zmagovalnaVeriga == null) {

            // omogoči igralcu na potezi, da izbere polje
            Polje polje = this.izberiPotezoT();
            if (polje == null) {
                this.izid = Izid.neveljavnaPoteza(this.naPotezi);
                return this.izid;
            }

            // obvesti nasprotnega igralca o potezi
            igralca[1 - this.naPotezi].sprejmiPotezo(polje);

            // posodobi stanje igre po odigrani potezi
            this.posodobiPoPotezi(polje);

            System.out.println();
        }

        // razglasitev izida
        int zmagovalec = this.naPotezi;
        System.out.printf("Igralec %d je zmagal!%n", zmagovalec + 1);
        System.out.printf("Zmagovalna veriga polj: %s%n", this.zmagovalnaVeriga);
        System.out.println();

        this.izid = Izid.zmaga(zmagovalec);
        igralca[0].rezultat(zmagovalec == 0);
        igralca[1].rezultat(zmagovalec == 1);
        return this.izid;
    }

    /** 
     * Tekstovni vmesnik: pridobi človekovo oz. strojevo izbiro poteze.
     * @return izbrana poteza
     */
    private Polje izberiPotezoT() {
        Igralec[] igralca = this.seansa.vrniIgralca();

        if (igralca[this.naPotezi].jeClovek()) {
            // človek na potezi
            return this.clovekovaIzbiraT();

        } else {
            // stroj na potezi
            Polje izbranoPolje = igralca[this.naPotezi].izberiPotezo();
            if (this.poIzbiriPotezeStrojaT(izbranoPolje)) {
                return izbranoPolje;
            }
            return null;
        }
    }

    /** Tekstovni vmesnik: s standardnega vhoda prebere človekovo izbiro poteze. */
    private Polje clovekovaIzbiraT() {
        Polje izbranoPolje;
        boolean prvic = true;
        do {
            if (!prvic) {
                System.out.println("Neveljavno polje!");
            }
            System.out.printf("(%d) Igralec %d --> indeks vrstice = ",
                    this.stPoteze, this.naPotezi + 1);
            int vrstica = this.bralnik.nextInt();

            System.out.printf("(%d) Igralec %d --> indeks stolpca = ",
                    this.stPoteze, this.naPotezi + 1);
            int stolpec = this.bralnik.nextInt();
            izbranoPolje = new Polje(vrstica, stolpec);
            prvic = false;

        } while (!this.preveriLegalnost(izbranoPolje));

        return izbranoPolje;
    }

    /** Tekstovni vmesnik: izpiše izbrano potezo in preveri njegovo veljavnost. */
    private boolean poIzbiriPotezeStrojaT(Polje poteza) {
        System.out.printf("(%d) Igralec %d --> %s%n",
                this.stPoteze, this.naPotezi + 1, poteza);
        if (!this.preveriLegalnost(poteza)) {
            System.out.println("Neveljavna izbira!");
            return false;
        }
        return true;
    }
}