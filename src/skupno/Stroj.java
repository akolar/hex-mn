
package skupno;

/**
 * Objekt tega vmesnika predstavlja stroj za igranje igre Hex.
 */
public interface Stroj {

    /** 
     * Ta metoda se pokliče ob pričetku vsake partije.
     *
     * @param stranica dolžina stranice igralne površine
     * @param semRdec true, če bo stroj this igral z rdečimi žetoni; 
     *                false, če bo igral z modrimi
     */
    public void novaPartija(int stranica, boolean semRdec);

    /**
     * Ta metoda se pokliče, ko je stroj this na vrsti za izbiro poteze. Metoda
     * mora v `preostaliCas' milisekundah vrniti polje, na katero želi stroj
     * postaviti žeton.
     *
     * @param preostaliCas  čas, ki ga ima stroj this na voljo do konca partije
     * 
     * @return izbrano polje
     */
    public Polje izberiPotezo(long preostaliCas);

    /** 
     * Ta metoda se pokliče, ko nasprotnik stroja this odigra potezo.
     * 
     * @param polje polje, na katero je nasprotnik postavil žeton
     */
    public void sprejmiPotezo(Polje polje);

    /**
     * Ta metoda se pokliče ob koncu vsake partije.
     *
     * @param zmagal  true, če je zmagal stroj `this';
     *                false, če je zmagal nasprotnik
     *                (remi ni mogoč)
     */
    public void rezultat(boolean zmagal);
}
