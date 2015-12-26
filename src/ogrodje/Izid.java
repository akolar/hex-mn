
package ogrodje;

/**
 * Objekt tega razreda predstavlja enega od možnih izidov partije.
 */
public enum Izid {

    ZMAGA_PRVEGA("Zmagal je igralec 1", 1),
    ZMAGA_DRUGEGA("Zmagal je igralec 2", 0),
    PREKORACITEV_CASA_PRVEGA("Igralec 1 je prekoračil čas", 0),
    PREKORACITEV_CASA_DRUGEGA("Igralec 2 je prekoračil čas", 1),
    NEVELJAVNA_POTEZA_PRVEGA("Igralec 1 je odigral neveljavno potezo", 0),
    NEVELJAVNA_POTEZA_DRUGEGA("Igralec 2 je odigral neveljavno potezo", 1),
    PREDCASEN_ZAKLJUCEK("Predčasen zaključek", -1);

    /** besedni opis izida */
    private final String pojasnilo;

    /** število točk, ki jih ob izidu this prejme prvi igralec (igralec z indeksom 0) */
    private final int tockePrvega;

    private Izid(String pojasnilo, int tockePrvega) {
        this.pojasnilo = pojasnilo;
        this.tockePrvega = tockePrvega;
    }

    /** Vrne besedni opis izida. */
    public String vrniPojasnilo() {
        return this.pojasnilo;
    }

    /** 
     * Vrne število točk, ki jih je dosegel igralec s podanim indeksom.
     * @param igralec  indeks igralca (0 ali 1)
     * @return število točk (0 ali 1)
     */
    public int vrniTocke(int igralec) {
        return (igralec == 0) ? (this.tockePrvega) : (1 - this.tockePrvega);
    }

    /** Vrne indeks zmagovalca (0 oziroma 1). */
    public int zmagovalec() {
        return (this.tockePrvega == 1) ? 0 : 1;
    }

    /**
     * Vrne true natanko v primeru, če objekt this predstavlja izid z `navadno'
     * zmago (torej ne zmage po prekoračitvi časa ali po neveljavni potezi).
     */
    public boolean jeNavadnaZmaga() {
        return (this == ZMAGA_PRVEGA || this == ZMAGA_DRUGEGA);
    }

    /** 
     * Vrne objekt tipa Izid, ki predstavlja zmago igralca s podanim indeksom. 
     * @param igralec  indeks igralca (0 ali 1)
     */
    public static Izid zmaga(int igralec) {
        return (igralec == 0) ? (ZMAGA_PRVEGA) : (ZMAGA_DRUGEGA);
    }

    /** 
     * Vrne objekt tipa Izid, ki predstavlja zaključek partije zaradi 
     * neveljavne poteze, ki jo je izbral igralec s podanim indeksom.
     * @param igralec  indeks igralca (0 ali 1)
     */
    public static Izid neveljavnaPoteza(int igralec) {
        return (igralec == 0) ? (NEVELJAVNA_POTEZA_PRVEGA) : (NEVELJAVNA_POTEZA_DRUGEGA);
    }

    /** 
     * Vrne objekt tipa Izid, ki predstavlja zaključek partije zaradi 
     * prekoračitve časa, ki se je primerila igralcu s podanim indeksom.
     * @param igralec  indeks igralca (0 ali 1)
     */
    public static Izid prekoracitevCasa(int igralec) {
        return (igralec == 0) ? (PREKORACITEV_CASA_PRVEGA) : (PREKORACITEV_CASA_DRUGEGA);
    }

    /** Vrne besedni opis izida. */
    @Override
    public String toString() {
        return this.pojasnilo;
    }
}