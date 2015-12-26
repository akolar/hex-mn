
package skupno;

/**
 * Objekt tega razreda predstavlja polje igralne površine.
 */
public class Polje {

    /** indeks vrstice, v kateri se nahaja polje this */
    private final int vrstica;

    /** indeks stolpca, v katerem se nahaja polje this */
    private final int stolpec;

    /** 
     * Izdela objekt, ki predstavlja polje s podanim indeksom vrstice in
     * stolpca.
     *
     * @param vrstica  indeks vrstice (med 0 in vključno (d - 1), kjer je d
     *                 število polj po stranici igralne površine)
     *
     * @param stolpec  indeks stolpca (med 0 in vključno (d - 1), kjer je d
     *                 število polj po stranici igralne površine)
     */
    public Polje(int vrstica, int stolpec) {
        this.vrstica = vrstica;
        this.stolpec = stolpec;
    }

    /**
     * Vrne indeks vrstice polja this. 
     *
     * @return indeks vrstice polja this
     */
    public int vrniVrstico() {
        return this.vrstica;
    }

    /** 
     * Vrne indeks stolpca polja this.
     *
     * @return indeks vrstice polja this
     */
    public int vrniStolpec() {
        return this.stolpec;
    }

    /**
     * Vrne predstavitev polja this v obliki niza.
     *
     * @return niz oblike (v, s), kjer sta v in s indeksa vrstice in stolpca
     *         polja this
     */
    @Override
    public String toString() {
        return String.format("(%d, %d)", this.vrstica, this.stolpec);
    }

    /**
     * Vrne zgoščevalno kodo polja this.  (Te metode najverjetneje ne
     * boste neposredno uporabljali.)
     *
     * @return zgoščevalna koda polja this
     */
    @Override
    public int hashCode() {
        return (this.vrstica << 16) | this.stolpec;
    }

    /** 
     * Preveri, ali objekt this predstavlja isto polje kot objekt obj.
     *
     * @param obj  objekt (smiselno je, da pripada razredu Polje)
     *
     * @return true, če objekta this in obj predstavljata isto polje;
     *         false sicer
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Polje)) {
            return false;
        }
        Polje polje = (Polje) obj;
        return (this.vrstica == polje.vrstica && this.stolpec == polje.stolpec);
    }
}
