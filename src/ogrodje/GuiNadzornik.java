
package ogrodje;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import skupno.Polje;

/** 
 * Objekt tega razreda je namenjen komunikaciji med objekti uporabniškega
 * vmesnika (igralna plošča, imenska plošča itd.) ter komunikaciji med objekti
 * uporabniškega vmesnika in objektom, ki hrani stanje igre.
 */
public class GuiNadzornik {

    /** na koliko milisekund se bo prožil časovnik za spremljanje
     * porabljenega časa obeh igralcev */
    private static final int ZAKASNITEV_GLAVNEGA_CASOVNIKA = 50;

    /** krovna plošča -- plošča, ki vsebuje druge plošče, t.j., obe
     * imenski, obe časovni, igralno in statusno */
    private GuiKrovnaPlosca krovnaPlosca;

    /** objekt, ki predstavlja celotno seanso (množico partij) */
    private final Seansa seansa;

    /** objekta, ki predstavljata oba igralca ([0]: beli, [1]: črni) */
    private final Igralec[] igralca;

    /** objekt, ki predstavlja stanje igre */
    private final Partija partija;

    /** časovnik, v okviru katerega se preverja poraba časa */
    private Timer glavniCasovnik;

    /** Časovnik za realizacijo igre stroja proti stroju. Ob vsaki sprožitvi
     * časovnika stroj odigra svojo potezo v posebni niti. Po vsaki odigrani
     * potezi časovnik počaka `s_zakasnitev' milisekund. */
    private Timer casovnikStrojProtiStroju;

    /** nit, v kateri se vrši izbiranje poteze ("delovna nit") */
    private Thread delovnaNit = null;

    /** poteza, izbrana v delovni niti */
    private Polje izbranoPolje;

    /** ali v danem trenutku čakamo zaradi zakasnitve med potezami v igri 
     * stroja proti stroju */
    private boolean vDobiZakasnitve;

    /** `true' natanko v primeru, ko čakamo na enega od igralcev, 
     * da izbere potezo */
    private boolean cakamoNaPotezo;

    /**
     * Ustvari nov objekt.
     * @param seansa  objekt, ki predstavlja celotno seanso (množico partij)
     */
    public GuiNadzornik(Seansa seansa) {
        this.seansa = seansa;
        this.igralca = seansa.vrniIgralca();
        this.partija = seansa.vrniPartijo();
    }

    /** Posreduje objekte uporabniškega vmesnika nadzorniku in inicializira oba
     * časovnika. */
    public void inicializiraj(GuiKrovnaPlosca krovnaPlosca) {

        this.krovnaPlosca = krovnaPlosca;

        // časovnik za merjenje časa; prožil se bo ob rednih intervalih
        this.glavniCasovnik = new Timer(ZAKASNITEV_GLAVNEGA_CASOVNIKA, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiNadzornik gn = GuiNadzornik.this;
                if (gn.cakamoNaPotezo && gn.igralecNaPotezi().imaOmejenCas() 
                        && !gn.vDobiZakasnitve && !gn.partija.jeKonec()) {
                    gn.preveriCas();
                }
            }
        });
        this.glavniCasovnik.setRepeats(true);
        this.glavniCasovnik.setCoalesce(true);

        // časovnik, ki realizira igro stroja proti stroju
        this.casovnikStrojProtiStroju = new Timer(Parametri.vrniZakasnitevPoPotezi(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiNadzornik gn = GuiNadzornik.this;
                if (!gn.partija.jeKonec()) {
                    gn.vDobiZakasnitve = false;
                    gn.odigrajPotezoStroja();
                }
            }
        });
        this.casovnikStrojProtiStroju.setRepeats(false);
        this.casovnikStrojProtiStroju.setCoalesce(false);
    }

    /** Vrne objekta, ki vsebujeta podatke o obeh igralcih. */
    public Igralec[] vrniIgralca() { return this.igralca; }

    /** Vrne objekt, ki predstavlja stanje igre. */
    public Partija vrniPartijo() {
        return this.partija;
    }

    /** Vrne `true' natanko v primeru igre stroja proti stroju. */
    public boolean igraStrojaProtiStroju() {
        return (this.igralca[0].jeStroj() && this.igralca[1].jeStroj());
    }

    /** Vrne ime igralca s podanim indeksom. */
    public String imeIgralca(int ixIgralca) {
        return this.igralca[ixIgralca].ime();
    }

    /** Vrne `true' natanko v primeru, če je na potezi stroj. */
    public boolean strojNaPotezi() {
        return this.igralecNaPotezi().jeStroj();
    }

    /** Vrne objekt, ki predstavlja igralca na potezi. */
    private Igralec igralecNaPotezi() {
        return this.igralca[this.partija.vrniNaPotezi()];
    }

    /** Vzpostavi stanje ob pričetku nove igre. */
    public void novaPartija() {
        // posodobi stanje igre
        if (!this.seansa.pricniPartijo()) {
            return;
        }

        // ponastavi objekte grafičnega vmesnika
        this.krovnaPlosca.novaPartija();

        this.glavniCasovnik.start();
        this.vDobiZakasnitve = false;

        // obvesti oba igralca, da se je pričela nova igra
        int stranica = this.seansa.vrniStranico();

        // ponastavi razpoložljivi čas za oba igralca
        for (int i = 0;  i < 2;  i++) {
            if (this.igralca[i].imaOmejenCas()) {
                this.igralca[i].nastaviPreostaliCas(Parametri.vrniCasovnoOmejitev());
            }
        }
        this.igralca[0].novaPartija(stranica, true);
        this.igralca[1].novaPartija(stranica, false);

        // odigraj potezo stroja, če je ta na potezi
        if (this.strojNaPotezi()) {
            this.odigrajPotezoStroja();
        }

    }

    /** Prekine trenutno partijo. */
    @SuppressWarnings("CallToThreadStopSuspendOrResumeManager")
    public void prekiniPartijo() {
        // prekini (morebitno) nit, v kateri se izvaja iskanje strojeve poteze
        if (this.delovnaNit != null && this.delovnaNit.isAlive()) {
            // Vem, da je ta metoda zastarela, a žal ne poznam nobenega
            // drugega načina, kako ustaviti nit, ki se je ujela v brezizhodno
            // računsko zanko.
            this.delovnaNit.stop();
        }

        // ustavi glavni časovnik
        this.glavniCasovnik.stop();

        // ustavi igro stroja proti stroju, če morda še poteka
        this.casovnikStrojProtiStroju.stop();

        // partijo predčasno zaključimo, da morebitna poteza prekinjene igre, 
        // ki bi se izvršila po tej metodi, ne bi vplivala na stanje naslednje igre
        if (! this.partija.jeKonec()) {
            this.partija.predcasnoKoncaj();
        }
    }

    /** Normalni zaključek partije znotraj seanse. */
    public void zakljuciPartijo(Izid izid) {
        this.glavniCasovnik.stop();
        this.seansa.zakljuciPartijo(izid);
    }

    /**
     * Ta metoda se kliče ob vsaki sprožitvi časovnika za spremljanje porabe
     * časa. V metodi posodobimo in preverimo razpoložljivi čas igralca na
     * potezi.
     * @return true, če je čas potekel
     */
    @SuppressWarnings("CallToThreadStopSuspendOrResumeManager")
    public boolean preveriCas() {
        // posodobi preostali čas igralca na potezi in osveži prikazovalnik 
        // preostalega časa
        int naPotezi = this.partija.vrniNaPotezi();
        this.krovnaPlosca.posodobiCasovnoPlosco(naPotezi, this.igralca[naPotezi].vrniPreostaliCas());

        if (this.igralca[naPotezi].vrniPreostaliCas() < 0) {
            // igralec na potezi je prekoračil čas in posledično izgubil
            this.partija.nastaviIzid(Izid.prekoracitevCasa(naPotezi));
            this.razglasiKonec(this.partija.vrniIzid());

            this.krovnaPlosca.osveziIgralnoPlosco();

            if (this.delovnaNit != null && this.delovnaNit.isAlive()) {
                // glej opombo pri prvi uporabi metode `stop'
                this.delovnaNit.stop();
            }
            return true;
        }
        return false;
    }

    /** Izbere in odigra potezo stroja v posebni niti. */
    private void odigrajPotezoStroja() {
        if (this.partija.jeKonec()) {
            return;
        }

        // ustvari delovno nit in v njej poišči potezo
        this.delovnaNit = new Thread() {
            @Override
            public void run() {
                // sporoči stroju, naj izbere potezo
                GuiNadzornik gn = GuiNadzornik.this;
                gn.cakamoNaPotezo = true;
                gn.izbranoPolje = gn.igralecNaPotezi().izberiPotezo();
                gn.cakamoNaPotezo = false;

                // izvrši potezo v dogodkovni niti (to je nujno, saj metoda
                // `izvrsiPotezo' posodablja uporabniški vmesnik)
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        GuiNadzornik gn = GuiNadzornik.this;
                        // preveri, ali je izbrana poteza veljavna;
                        // če je, jo izvrši, sicer pa dodeli zmago nasprotniku
                        if (gn.izbranoPolje == null || !gn.preveriLegalnost(gn.izbranoPolje)) {
                            gn.obravnavajNeveljavnoPotezo(gn.izbranoPolje);
                        } else {
                            gn.izvrsiVeljavnoPotezo(gn.izbranoPolje);
                        }
                    }
                });
            }
        };

        // sproži delovno nit
        this.delovnaNit.start();
    }

    /** Vrne true natanko v primeru, če je podana poteza veljavna. */
    public boolean preveriLegalnost(Polje poteza) {
        return this.partija.preveriLegalnost(poteza);
    }

    /** Izvrši dano potezo.  Metoda predpostavlja, da je poteza veljavna. */
    public void izvrsiVeljavnoPotezo(Polje poteza) {
        this.cakamoNaPotezo = false;

        // posodobi stanje igre
        int akterPoteze = this.partija.vrniNaPotezi();
        this.partija.posodobiPoPotezi(poteza);

        // posodobi podobo igralne plošče
        if (this.igralca[akterPoteze].jeStroj()) {
            this.krovnaPlosca.osvetliStrojevoPotezo(poteza);
        }

        // obvesti nasprotnika igralca, ki je pravkar odigral potezo, o odigrani potezi
        if (this.igralca[1 - akterPoteze].jeStroj()) {
            this.cakamoNaPotezo = true;
            this.igralca[1 - akterPoteze].sprejmiPotezo(poteza);
            this.cakamoNaPotezo = false;
            if (this.preveriCas()) {
                return;
            }
        }

        // v primeru zaključka igre prikaži ustrezna obvestila
        if (this.partija.jeKonec()) {
            this.razglasiKonec(this.partija.vrniIzid());
            return;
        }

        if (this.igraStrojaProtiStroju()) {
            // (ponovno) sproži časovnik za igro stroja proti stroju
            this.casovnikStrojProtiStroju.setDelay(Parametri.vrniZakasnitevPoPotezi());
            this.casovnikStrojProtiStroju.start();
            this.vDobiZakasnitve = true;

        } else if (this.strojNaPotezi()) {
            // človek proti stroju: odigraj potezo stroja, če je ta na potezi;
            // ura se bo sprožila tik pred klicem metode objekta Stroj
            this.odigrajPotezoStroja();

        } else {
            // na potezi je človek; zapomni si čas ob začetku poteze
            this.cakamoNaPotezo = true;
        }
    }

    /** Obravnava neveljavno potezo stroja (človek ne more vnesti neveljavne
     * poteze).  Igra se takoj zaključi z zmago nasprotnika. */
    public void obravnavajNeveljavnoPotezo(Polje poteza) {
        int naPotezi = this.partija.vrniNaPotezi();
        this.partija.nastaviIzid(Izid.neveljavnaPoteza(naPotezi));
        this.razglasiKonec(this.partija.vrniIzid());

        // prikaži sporočilo o predčasnem koncu v preprostem dialogu
        final String[] HTML_BARVA = {"red", "green"};
        String sporocilo = String.format(
                "<html>Igralec <font color=\"%s\">%s</font> je odigral neveljavno potezo: %s</html>",
                HTML_BARVA[naPotezi], this.imeIgralca(naPotezi),
                (poteza == null ? "null" : poteza.toString()));

        JOptionPane.showMessageDialog(this.krovnaPlosca,
                sporocilo, "Obvestilo", JOptionPane.PLAIN_MESSAGE);
    }

    /** 
     * Razglasi konec partije.
     *
     * @param ixZmagovalca indeks zmagovalca (0: beli, 1: črni, -1: remi)
     * @param statusKonca status zaključka igre
     * @see StatusKoncaIgre
     */
    private void razglasiKonec(final Izid izid) {
        this.krovnaPlosca.razglasiKonec(izid);
        int ixZmagovalca = izid.zmagovalec();
        this.igralca[ixZmagovalca].rezultat(true);
        this.igralca[1 - ixZmagovalca].rezultat(false);
        this.zakljuciPartijo(izid);

        if (this.igraStrojaProtiStroju() && Parametri.vrniSteviloPartij() > 1) {
            Timer casovnik = new Timer(Parametri.vrniZakasnitevPoIgri(), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GuiNadzornik.this.novaPartija();
                }
            });
            casovnik.setRepeats(false);
            casovnik.start();
        }
    }
}
