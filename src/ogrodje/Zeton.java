
package ogrodje;

/**
 * Objekt tega naštevnega razreda predstavlja žeton na igralni površini
 * (rdeč, moder ali neobstoječ).
 */
public enum Zeton {

    RDEC(0), MODER(1), NEOBSTOJEC(-1);

    /** številska oznaka žetona */
    private final int oznaka;

    private Zeton(int oznaka) {
        this.oznaka = oznaka;
    }

    /** Vrne oznako, pripisano žetonu. */
    public int vrniVrednost() {
        return this.oznaka;
    }

    /** Če žeton obstaja (this != NEOBSTOJEC), vrne pripadajočo oznako v obliki
     * niza, sicer pa vrne niz "-". */
    @Override
    public String toString() {
        return (this.oznaka >= 0) ? (Integer.toString(this.oznaka)) : ("-");
    }

    /** Vrne true natanko v primeru, če žeton this obstaja (this != NEOBSTOJEC). */
    public boolean obstaja() {
        return (this.oznaka >= 0);
    }

    /** 
     * Vrne žeton za igralca s podanim indeksom.
     * @param igralec  indeks igralca (0 ali 1)
     */
    public static Zeton zaIgralca(int igralec) {
        return (igralec == 0) ? (RDEC) : (MODER);
    }
}