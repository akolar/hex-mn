
package ogrodje;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JPanel;
import skupno.Polje;

/** 
 * Objekt tega razreda predstavlja krovno grafične plošče.  Krovna plošča je
 * sestavljena iz
 * <ul>
 *    <li>dveh časovnih plošč (za prikaz trenutnega časa)</li>
 *    <li>dveh imenskih plošč (za prikaz imen igralcev)</li>
 *    <li>`gumba' Nova igra (dejansko gre za objekt tipa JPanel, ne JButton)</li>
 *    <li>igralne plošče (za prikaz stanja igre in za uporabnikov vnos potez)</li>
 *    <li>statusne plošče (za prikaz obvestil in statusa igre)</li>
 * </ul>
 */
public class GuiKrovnaPlosca extends JPanel {

    /** barva ozadja */
    private static final Color B_OZADJE = new Color(64, 64, 64);

    /** ~[ix] = barva števk na časovni plošči za igralca z indeksom ix */
    private static final Color[] B_STEVKE_CASOVNA_PLOSCA = {
        Color.RED,
        new Color(128, 192, 255),
    };

    /** privzeta dolžina stranice polja šahovnice; 
     * velikosti vseh podplošč so relativne glede na to mero */
    private static final double D_POLJE_PRIVZETO = 50.0;

    // objekti, ki predstavljajo posamezne komponente (podplošče krovne plošče)
    private GuiCasovnaPlosca[] casovniPlosci;
    private GuiImenskaPlosca[] imenskiPlosci;
    private GuiGumbNovaPartija gmNovaPartija;
    private GuiIgralnaPlosca igralnaPlosca;

    /**
     * Ustvari objekt, ki predstavlja krovno ploščo. 
     * @param nadzornik objekt, ki skrbi za komunikacijo med podploščami in
     *                  za dostop do stanja igre
     */
    public GuiKrovnaPlosca(final GuiNadzornik nadzornik) {
        this.setBackground(B_OZADJE);

        // Delali bomo brez razporejevalnika, da bomo imeli več svobode. 
        // Zaradi tega bomo morali ročno poskrbeti za prilagajanje lege 
        // podplošč ob spremembi velikosti krovne plošče.
        this.setLayout(null);

        Igralec[] igralca = nadzornik.vrniIgralca();

        // časovni plošči
        this.casovniPlosci = new GuiCasovnaPlosca[2];
        for (int igr = 0;  igr < this.casovniPlosci.length;  igr++) {
            long zacetniCas = (igralca[igr].jeClovek()) ? (0) : (Parametri.vrniCasovnoOmejitev());
            this.casovniPlosci[igr] = new GuiCasovnaPlosca(zacetniCas, true, B_STEVKE_CASOVNA_PLOSCA[igr]);
            this.add(this.casovniPlosci[igr]);
        }

        // imenski plošči
        this.imenskiPlosci = new GuiImenskaPlosca[2];
        for (int igr = 0;  igr < this.imenskiPlosci.length;  igr++) {
            this.imenskiPlosci[igr] = new GuiImenskaPlosca(igr, nadzornik.imeIgralca(igr));
            this.imenskiPlosci[igr].inicializiraj();
            this.add(this.imenskiPlosci[igr]);
        }

        // gumb za pričetek nove partije
        this.gmNovaPartija = new GuiGumbNovaPartija(nadzornik);
        this.add(this.gmNovaPartija);
        if (nadzornik.igraStrojaProtiStroju() && Parametri.vrniSteviloPartij() > 1) {
            this.gmNovaPartija.onemogoci();
        }

        // igralna plošča
        this.igralnaPlosca = new GuiIgralnaPlosca(nadzornik);
        this.igralnaPlosca.inicializiraj();
        this.add(igralnaPlosca);

        // opredeli obnašanje ob spremembi velikosti krovne plošče;
        // to moramo storiti zato, ker delamo brez razporejevalnika
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int stranica = Parametri.vrniStranico();

                GuiKrovnaPlosca kp = GuiKrovnaPlosca.this;
                // na novo izračunaj dolžino stranice polja šahovnice; 
                // skoraj vse mere so vezane na ta podatek
                double dPolje = kp.dolociStranicoPolja();
                int xLevo = (int) ((kp.getWidth() - kp.sirina(dPolje)) / 2);
                int yZgoraj = (int) ((kp.getHeight() - kp.visina(dPolje)) / 2);

                // določi velikosti posameznih podplošč
                int hm = Razno.ri(GuiImenskaPlosca.visina(stranica, dPolje));
                int wig = Razno.ri(GuiIgralnaPlosca.sirina(dPolje));
                int hig = Razno.ri(GuiIgralnaPlosca.visina(dPolje));
                int x = xLevo;
                int y = yZgoraj;
                int wu1 = Razno.ri(kp.casovniPlosci[0].sirina(stranica, dPolje));
                int wu2 = Razno.ri(kp.casovniPlosci[1].sirina(stranica, dPolje));
                int wg = hm;
                int wim = (wig - wg - wu1 - wu2) / 2;

                // razporedi podplošče
                kp.casovniPlosci[0].setBounds(x, y, wu1, hm);
                x += wu1;
                kp.imenskiPlosci[0].setBounds(x, y, wim, hm);
                x += wim;
                kp.gmNovaPartija.setBounds(x, y, wg, hm);
                x += wg;
                kp.imenskiPlosci[1].setBounds(x, y, wig - wg - wu1 - wu2 - wim, hm);
                x += wim;
                kp.casovniPlosci[1].setBounds(x, y, wu2, hm);
                x = xLevo;
                y += hm;
                kp.igralnaPlosca.setBounds(x, y, wig, hig);

                // obvesti igralno ploščo o spremembi velikosti, da lahko
                // pripravi vse potrebno za izris igralne površine 
                // pri novi velikosti
                kp.igralnaPlosca.spremembaVelikosti();
            }
        });
    }

    /** Vrne tabelo dveh objektov, ki predstavljata časovni plošči. */
    public GuiCasovnaPlosca[] vrniCasovniPlosci() {
        return this.casovniPlosci;
    }

    /** Vrne tabelo dveh objektov, ki predstavljata imenski plošči. */
    public GuiImenskaPlosca[] vrniImenskiPlosci() {
        return this.imenskiPlosci;
    }

    /** Vrne objekt, ki predstavlja igralno ploščo. */
    public GuiIgralnaPlosca vrniIgralnoPlosco() {
        return this.igralnaPlosca;
    }

    /** Vrne `najlepšo' velikost krovne plošče.  Ta metoda se pokliče,
     * ko velikost okna ni določena vnaprej (torej ko je datoteka DATOTEKA_OKNO
     * prazna ali neveljavna). */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(Razno.ri(this.sirina(D_POLJE_PRIVZETO)), 
                Razno.ri(this.visina(D_POLJE_PRIVZETO)));
    }

    /** Vrne širino krovne plošče pri podani dolžini stranice polja šahovnice. */
    public double sirina(double dPolje) {
        int stranica = Parametri.vrniStranico();
        double w1 = GuiIgralnaPlosca.sirina(dPolje);
        double w2 = 2 * GuiImenskaPlosca.minSirina(stranica, dPolje) + 
                this.casovniPlosci[0].sirina(stranica, dPolje) +
                this.casovniPlosci[1].sirina(stranica, dPolje) +
                GuiGumbNovaPartija.sirina(dPolje);
        return Math.max(w1, w2);
    }

    /** Vrne višino krovne plošče pri podani dolžini stranice polja šahovnice. */
    public double visina(double dPolje) {
        int stranica = Parametri.vrniStranico();
        return (GuiImenskaPlosca.visina(stranica, dPolje) + GuiIgralnaPlosca.visina(dPolje));
    }

    /** Določi optimalno dolžino stranice polja glede na trenutno velikost
     * krovne plošče. */
    public double dolociStranicoPolja() {
        double w = ((double) this.getWidth()) / this.sirina(1.0);
        double h = ((double) this.getHeight()) / this.visina(1.0);
        return Math.min(w, h);
    }

    /** Posodobi stanje plošč ob pričetku partije. */
    public void novaPartija() {
        this.imenskiPlosci[0].novaPartija();
        this.imenskiPlosci[1].novaPartija();
        this.casovniPlosci[0].novaPartija();
        this.casovniPlosci[1].novaPartija();
        this.igralnaPlosca.novaPartija();
        this.igralnaPlosca.repaint(); 
    }

    /** Posodobi prikaz časa na časovni plošči s podanim indeksom.
     * @param indeks  indeks časovne plošče (0 ali 1)
     * @param cas     novi prikaz časa
     */
    public void posodobiCasovnoPlosco(int indeks, long cas) {
        this.casovniPlosci[indeks].posodobiCas(cas);
    }

    /** Osveži celotno igralno ploščo. */
    public void osveziIgralnoPlosco() {
        this.igralnaPlosca.repaint();
    }

    /** Osvetli podano strojevo potezo na igralni plošči. */
    public void osvetliStrojevoPotezo(Polje poteza) {
        this.igralnaPlosca.osvetliStrojevoPotezo(poteza);
    }

    /** 
     * Razglasi konec partije.
     * @param izid  izid partije
     */
    public void razglasiKonec(Izid izid) {
        this.igralnaPlosca.repaint();
        this.imenskiPlosci[izid.zmagovalec()].animiraj();
    }
}
