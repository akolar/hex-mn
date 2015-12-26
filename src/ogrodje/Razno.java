
package ogrodje;

/**
 * Razne drobnarije, ne nujno vezane na igro Hex.
 */
public class Razno {

    /** znak za skok v novo vrstico */
    public static final String NL = String.format("%n");

    /** Vrne niz, sestavljen iz `stPonovitev' zaporednih ponovitev niza `niz'. */
    public static String zmnozekNiza(int stPonovitev, String niz) {
        String rezultat = "";
        for (int i = 0;  i < stPonovitev;  i++) {
            rezultat += niz;
        }
        return rezultat;
    }

    /** Vrne maksimum podanih števil. */
    public static int max(int... stevila) {
        int m = stevila[0];
        for (int n: stevila) {
            m = Math.max(m, n);
        }
        return m;
    }

    /** Vrne maksimum podanih števil. */
    public static double max(double... stevila) {
        double m = stevila[0];
        for (double n: stevila) {
            m = Math.max(m, n);
        }
        return m;
    }

    /** Vrne minimum podanih števil. */
    public static int min(int... stevila) {
        int m = stevila[0];
        for (int n: stevila) {
            m = Math.min(m, n);
        }
        return m;
    }

    /** Vrne minimum podanih števil. */
    public static double min(double... stevila) {
        double m = stevila[0];
        for (double n: stevila) {
            m = Math.min(m, n);
        }
        return m;
    }

    /** Vrne celo število, ki je najbližje podanemu številu. */
    public static int ri(double d) {
        return (int) Math.round(d);
    }

    /** Vrne najmanjše celo število, ki je večje ali enako podanemu številu. */
    public static int ci(double d) {
        return (int) Math.ceil(d);
    }

    /** Vrne največje celo število, ki je manjše ali enako podanemu številu. */
    public static int fi(double d) {
        return (int) Math.floor(d);
    }

    /** Vrne kopijo podanega niza, le da prvi znak niza pretvori v veliko začetnico. */
    public static String velikaZacetnica(String niz) {
        return Character.toUpperCase(niz.charAt(0)) + niz.substring(1);
    }
}