
package ogrodje;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.swing.JPanel;
import skupno.Polje;

/** 
 * Objekt tega razreda predstavlja ploščo, ki prikazuje stanje igre in omogoča
 * uporabniku, da vnaša poteze.
 */

public class GuiIgralnaPlosca extends JPanel
        implements MouseListener, MouseMotionListener {

    /** true: lepši, a počasnejši izris;
     * false: grši, a hitrejši izris */
    private static final boolean ANTIALIASING = true;

    /** barva ozadja */
    private static final Color B_OZADJE = new Color(224, 232, 224);

    /** barva praznega polja */
    private static final Color B_PRAZNO_POLJE = new Color(224, 224, 224);

    /** barva obrobe polja */
    public static final Color B_OBROBA_POLJA = Color.BLACK;

    /** barva obrobe polja pod spušceno miško */
    private static final Color B_OBROBA_POLJA_POD_MISKO = Color.BLACK;

    /** [ix] = barva obrobe polja pod pritisnjeno miško za igralca ix */
    private static final Color[] B_OBROBA_POLJA_POD_PRITISKOM = {
        new Color(192, 0, 0),
        new Color(0, 0, 255),
    };

    /** [ix] = barva obrobe polja pod pritisnjeno miško za igralca ix */
    private static final Color[] B_OBROBA_RACUNALNIKOVE_POTEZE = {
        Color.WHITE,
        Color.WHITE,
    };

    /** [ix] = barva žetona, ki pripada igralcu ix */
    public static final Color[] B_POLJE = {
        new Color(255, 0, 0),
        new Color(64, 128, 255)
    };

    /** [ix] = barva osvetlitve polja za igralca ix, ko se na polje pomaknemo 
     * z miško  */
    private static final Color[] B_POLJE_POD_MISKO = {
        new Color(255, 192, 192),
        new Color(184, 200, 255)
    };

    /** barva lomljenke, ki povezuje zmagovito zaporedje žetonov */
    private static final Color B_LOMLJENKA_ZMAGE = Color.YELLOW;

    /** razmerje med debelino obrobe igralne površine in dolžino stranice polja */
    private static final double R_OBROBA_POLJE = 0.15;

    /** razmerje med debelino lomljenke, ki povezuje zmagovita polja, in dolžino
     * stranice polja */
    private static final double R_LOMLJENKA_POLJE = 0.15;

    /** razmerje med debelino roba polja in dolžino stranice polja */
    private static final double R_ROB_POLJA_POLJE = 0.03;

    /** razmerje med debelino osvetljenega roba polja in dolžino stranice polja */
    private static final double R_OSVETLJENI_ROB_POLJA_POLJE = 2 * R_ROB_POLJA_POLJE;

    /** minimalna debelina roba polja */
    private static final double MIN_DEBELINA_ROBA_POLJA = 1.0;

    /** minimalna debelina osvetljenega roba polja */
    private static final double MIN_DEBELINA_OSVETLJENEGA_ROBA_POLJA = 2.0;

    /** minimalna debelina lomljenke */
    private static final double MIN_DEBELINA_LOMLJENKE = 2.0;

    /**
     * Omejevalni okvir, ki se posreduje metodi repaint pri osvežitvi videza
     * polja, je nekoliko večji od dejanskega omejevalnega okvirja polja.
     * Konstanta R_OHLAPNOST_OMEJEVALNEGA_OKVIRJA podaja razmerje med dolžino
     * dodatka k okvirju na vsako stran in širino oz.\ višino okvirja.
     */
    private static final double R_OHLAPNOST_OMEJEVALNEGA_OKVIRJA = 0.10;

    /** referenca na povezovalni objekt */
    private final GuiNadzornik nadzornik;

    /** trenutno osvetljena poteza (= zadnja računalnikova poteza) */
    private Polje osvetljenaPoteza;

    /** veljavno polje pod miško (null, če takega polja ni) */
    private Polje poljePodMisko;

    /** true, ko je miškin gumb pritisnjen; false sicer */
    private boolean miskinGumbPritisnjen;

    /** ~[i][j]: večkotnik za polje na logičnih koordinatah (i, j) */
    private final Path2D[][] sestkotniki;

    /** trenutna dolžina stranice šestkotnega polja */
    private double dPolje;

    /** zgornji del obrobe igralne površine */
    private Path2D obrobaZgoraj;

    /** spodnji del obrobe igralne površine */
    private Path2D obrobaSpodaj;

    /** levi del obrobe igralne površine */
    private Path2D obrobaLevo;

    /** desni del obrobe igralne površine */
    private Path2D obrobaDesno;

    public GuiIgralnaPlosca(GuiNadzornik nadzornik) {
        this.nadzornik = nadzornik;
        this.setBackground(B_OZADJE);
        int stranica = Parametri.vrniStranico();
        this.sestkotniki = new Path2D[stranica][stranica];
    }

    /** 
     * Tole bi lahko šlo v konstruktor, toda stavek 
     * this.addMouseListener(this);
     * sproži svarilo `leaking this in constructor', ker med izvajanjem
     * konstruktorja objekt še ni v celoti inicializiran
     */
    public void inicializiraj() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    /**
     * Vrne širino igralne plošče pri podani dolžini stranice šestkotnega
     * polja.
     * @param dp dolžina stranice šestkotnega polja
     */
    public static double sirina(double dp) {
        return (GuiRazno.KOREN_3 * dp * (3.0 * Parametri.vrniStranico() - 1.0) / 2.0 +
                2.0 * dp * R_OBROBA_POLJE);
    }

    /**
     * Vrne višino igralne plošče pri podani dolžini stranice šestkotnega
     * polja.
     * @param dp dolžina stranice šestkotnega polja
     */
    public static double visina(double dp) {
        return ((3.0 * Parametri.vrniStranico() + 1.0) * dp / 2.0 +
                2.0 * dp * R_OBROBA_POLJE);
    }

    /** Ta metoda se pokliče ob vsakem pričetku igre. */
    public void novaPartija() {
        this.osvetljenaPoteza = null;
        this.poljePodMisko = null;
        this.miskinGumbPritisnjen = false;
    }

    /** 
     * V tej metodi, ki se pokliče ob vsaki spremembi velikosti plošče this,
     * se izračuna nova velikost polja.
     */
    public void spremembaVelikosti() {
        int stranica = Parametri.vrniStranico();

        // izračunaj dolžino stranice šestkotnega polja
        double wPlosca = (double) this.getWidth();
        double hPlosca = (double) this.getHeight();
        double dPoljeW = 2.0 * wPlosca / 
                (3.0 * GuiRazno.KOREN_3 * stranica - GuiRazno.KOREN_3 + 4.0 * R_OBROBA_POLJE);
        double dPoljeH = 2.0 * hPlosca / 
                (3.0 * stranica + 1 + 4.0 * R_OBROBA_POLJE);
        this.dPolje = Math.min(dPoljeW, dPoljeH);

        // mere igralne površine
        double wIgralnaPovrsina = (3.0 * GuiRazno.KOREN_3 * stranica - GuiRazno.KOREN_3) * 
                this.dPolje / 2.0;
        double hIgralnaPovrsina = (3.0 * stranica + 1.0) * this.dPolje / 2.0;
        double xIgralnaPovrsina = (wPlosca - wIgralnaPovrsina) / 2.0;
        double yIgralnaPovrsina = (hPlosca - hIgralnaPovrsina) / 2.0;

        // določi lege in velikosti posameznih polj
        double y = yIgralnaPovrsina;
        double x0 = xIgralnaPovrsina;

        double dx = GuiRazno.KOREN_3 * this.dPolje;
        double dx0 = dx / 2.0;
        double dy = 3.0 * this.dPolje / 2.0;

        for (int i = 0;  i < stranica;  i++) {
            double x = x0;
            for (int j = 0;  j < stranica;  j++) {
                this.sestkotniki[i][j] = GuiRazno.izdelajSestkotnik(x, y, this.dPolje);
                x += dx;
            }
            y += dy;
            x0 += dx0;
        }

        // obroba igralne površine
        dx = GuiRazno.KOREN_3 * this.dPolje / 2.0;
        dy = this.dPolje / 2.0;
        double dRob = this.dPolje * R_OBROBA_POLJE;

        this.obrobaZgoraj = izdelajObrobo(
                2 * stranica,
                xIgralnaPovrsina,
                yIgralnaPovrsina + dy,
                dx, -dy, dx, dy,
                0.0, -dRob,
                -dx, -dy, -dx, dy, false, 0, 0, 0, 0
        );

        this.obrobaSpodaj = izdelajObrobo(
                2 * stranica,
                xIgralnaPovrsina + (stranica - 1) * dx,
                yIgralnaPovrsina + 1.5 * stranica * this.dPolje,
                dx, dy, dx, -dy,
                0.0, dRob,
                -dx, dy, -dx, -dy, false, 0, 0, 0, 0
        );

        double z = GuiRazno.KOREN_3 / 3.0 * R_OBROBA_POLJE;

        this.obrobaLevo = izdelajObrobo(
                2 * stranica - 1,
                xIgralnaPovrsina,
                yIgralnaPovrsina + dy,
                0, this.dPolje, dx, dy,
                -dRob, 0.0,
                0, -this.dPolje, -dx, -dy, 
                true, 0.0, (z - 1.0) * this.dPolje, 0.0, (-z - 1.0) * this.dPolje
        );

        this.obrobaDesno = izdelajObrobo(
                2 * stranica - 1,
                xIgralnaPovrsina + 2.0 * stranica * dx,
                yIgralnaPovrsina + dy,
                0, this.dPolje, dx, dy,
                dRob, 0.0,
                0, -this.dPolje, -dx, -dy, 
                true, 0.0, (-z - 1.0) * this.dPolje, 0.0, (z - 1.0) * this.dPolje
        );
    }

    /** Vrne objekt, ki predstavlja obrobo igralne površine. */
    private static Path2D izdelajObrobo(
            int stKorakov,
            double x0, double y0,
            double dx1, double dy1, double dx2, double dy2,
            double dxObrat, double dyObrat,
            double dx3, double dy3, double dx4, double dy4,
            boolean uporabljaj56,
            double dx5, double dy5, double dx6, double dy6) {

        Path2D obroba = new Path2D.Double();
        double x = x0;
        double y = y0;
        obroba.moveTo(x, y);
        for (int i = 0;  i < stKorakov;  i++) {
            if (i % 2 == 0) {
                x += dx1;
                y += dy1;
            } else {
                x += dx2;
                y += dy2;
            }
            obroba.lineTo(x, y);
        }
        x += dxObrat;
        y += dyObrat;
        obroba.lineTo(x, y);
        for (int i = 0;  i < stKorakov;  i++) {
            if (uporabljaj56 && (i == 0 || i == stKorakov - 1)) {
                if (i == 0) {
                    x += dx5;
                    y += dy5;
                } else {
                    x += dx6;
                    y += dy6;
                }
            } else if (i % 2 == 0) {
                x += dx3;
                y += dy3;
            } else {
                x += dx4;
                y += dy4;
            }
            obroba.lineTo(x, y);
        }
        obroba.closePath();
        return obroba;
    }

    /** Ta metoda se kliče vsakokrat, ko je treba osvežiti vsebino plošče. */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = GuiRazno.nastaviAntialiasing(g, ANTIALIASING);
        Partija partija = this.nadzornik.vrniPartijo();
        if (partija == null) {
            return;
        }
        Povrsina povrsina = partija.vrniPovrsino();
        if (povrsina == null) {
            return;
        }

        int stranica = Parametri.vrniStranico();
        int naPotezi = partija.vrniNaPotezi();

        // nariši obrobo
        g2.setColor(B_POLJE[0]);
        g2.fill(this.obrobaZgoraj);
        g2.fill(this.obrobaSpodaj);

        g2.setColor(B_POLJE[1]);
        g2.fill(this.obrobaLevo);
        g2.fill(this.obrobaDesno);

        // nariši igralno površino
        for (int i = 0;  i < stranica;  i++) {
            for (int j = 0;  j < stranica;  j++) {
                Polje polje = new Polje(i, j);
                Color barvaPolnila;

                Zeton vsebina = povrsina.vrniVsebinoPolja(polje);
                if (vsebina == Zeton.NEOBSTOJEC) {
                    barvaPolnila = (polje.equals(this.poljePodMisko)) ?
                            B_POLJE_POD_MISKO[naPotezi] : B_PRAZNO_POLJE;
                } else {
                    barvaPolnila = B_POLJE[vsebina.vrniVrednost()];
                }

                g2.setColor(barvaPolnila);
                g2.fill(this.sestkotniki[i][j]);
                g2.setColor(B_OBROBA_POLJA);

                Stroke s = g2.getStroke();
                double debelina = Math.max(MIN_DEBELINA_ROBA_POLJA, R_ROB_POLJA_POLJE * dPolje);
                g2.setStroke(new BasicStroke((float) debelina));
                g2.draw(this.sestkotniki[i][j]);
                g2.setStroke(s);
            }
        }

        // nariši obrobo okrog trenutno izbranega polja
        if (this.poljePodMisko != null) {
            Color barva;
            if (this.miskinGumbPritisnjen) {
                int ixIgralca = this.nadzornik.vrniPartijo().vrniNaPotezi();
                barva = B_OBROBA_POLJA_POD_PRITISKOM[ixIgralca];
            } else {
                barva = B_OBROBA_POLJA_POD_MISKO;
            }
            this.osvetliPolje(g2, this.poljePodMisko, barva);
        }

        // nariši lomljenko čez polja, ki tvorijo zmagovalno verigo
        if (partija.jeKonec() && partija.vrniIzid().jeNavadnaZmaga()) {
            List<Polje> veriga = partija.vrniZmagovalnoVerigo();
            int stPolj = veriga.size();
            g2.setColor(B_LOMLJENKA_ZMAGE);
            Path2D lomljenka = new Path2D.Double();
            double[] polozaj = this.srediscePolja(veriga.get(0));
            lomljenka.moveTo(polozaj[0], polozaj[1]);
            for (int i = 1;  i < stPolj;  i++) {
                polozaj = this.srediscePolja(veriga.get(i));
                lomljenka.lineTo(polozaj[0], polozaj[1]);
            }

            Stroke s = g2.getStroke();
            double debelina = Math.max(MIN_DEBELINA_LOMLJENKE, R_LOMLJENKA_POLJE * this.dPolje);
            g2.setStroke(new BasicStroke((float) debelina));
            g2.draw(lomljenka);
            g2.setStroke(s);
        }

        // nariši obrobo okrog zadnje računalnikove poteze
        if (this.osvetljenaPoteza != null) {
            int ixIgralca = 1 - this.nadzornik.vrniPartijo().vrniNaPotezi();
            this.osvetliPolje(g2, this.osvetljenaPoteza, B_OBROBA_RACUNALNIKOVE_POTEZE[ixIgralca]);
        }
    }

    /** Vrne koordinati središča podanega polja. */
    private double[] srediscePolja(Polje polje) {
        int vrstica = polje.vrniVrstico();
        int stolpec = polje.vrniStolpec();
        Rectangle2D okvir = this.sestkotniki[vrstica][stolpec].getBounds2D();
        return new double[]{okvir.getCenterX(), okvir.getCenterY()};
    }

    /** Osvetli podano polje. */
    private void osvetliPolje(Graphics2D g2, Polje polje, Color barva) {
        g2.setColor(barva);
        int vr = polje.vrniVrstico();
        int st = polje.vrniStolpec();

        Stroke stroke = g2.getStroke();
        double debelina = Math.max(MIN_DEBELINA_OSVETLJENEGA_ROBA_POLJA,
                R_OSVETLJENI_ROB_POLJA_POLJE * this.dPolje);
        g2.setStroke(new BasicStroke((float) debelina));
        g2.draw(this.sestkotniki[vr][st]);
        g2.setStroke(stroke);
    }

    /** Osvetli podano potezo, ki jo je izvršil stroj. */
    public void osvetliStrojevoPotezo(Polje poteza) {
        Polje staraOsvetljenaPoteza = this.osvetljenaPoteza;
        this.osvetljenaPoteza = poteza;
        if (staraOsvetljenaPoteza != null) {
            this.repaint(this.ohlapenOmejevalniOkvir(staraOsvetljenaPoteza));
        }
        this.repaint(this.ohlapenOmejevalniOkvir(this.osvetljenaPoteza));
    }

    /** Določi, kaj se zgodi ob premiku miške. */
    @Override
    @SuppressWarnings("null")
    public void mouseMoved(MouseEvent e) {
        Partija partija = this.nadzornik.vrniPartijo();

        // če je igra že končana ali pa je na potezi stroj, se ne odzovi na 
        // premike miške
        if (partija.jeKonec() || this.nadzornik.strojNaPotezi()) {
            return;
        }

        // določi polje pod miškinim kazalcem in po potrebi
        // posodobi prejšnje in/ali trenutno osvetljeno polje
        Polje staroPoljePodMisko = this.poljePodMisko;
        this.poljePodMisko = null;
        Polje polje = this.poljeNaPoziciji(e.getPoint());
        if (polje != null && this.nadzornik.preveriLegalnost(polje)) {
            this.poljePodMisko = polje;
        }

        if ( ((staroPoljePodMisko == null) ^ (this.poljePodMisko == null)) ||
                (staroPoljePodMisko != null && !staroPoljePodMisko.equals(this.poljePodMisko)) ) {

            if (staroPoljePodMisko != null) {
                this.repaint(this.ohlapenOmejevalniOkvir(staroPoljePodMisko));
            }
            if (this.poljePodMisko != null) {
                this.repaint(this.ohlapenOmejevalniOkvir(this.poljePodMisko));
            }
        }
    }

    /**
     * Ob pritisku miškinega gumba si zapomnimo polje, na katerem leži miškin
     * gumb. Potezo sprejmemo šele ob spustu gumba, če uporabnik spusti miško
     * nad poljem, nad katerim jo je pritisnil.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        Partija partija = this.nadzornik.vrniPartijo();

        // če je igra že končana ali pa je na potezi stroj, se ne odzovi na 
        // pritiske in spuste miškinega gumba
        if (partija.jeKonec() || this.nadzornik.strojNaPotezi()) {
            return;
        }

        // odstrani morebitno osvetlitev
        if (this.osvetljenaPoteza != null) {
            this.repaint(this.ohlapenOmejevalniOkvir(this.osvetljenaPoteza));
            this.osvetljenaPoteza = null;
        }

        // zapomni si dejstvo, da je miškin gumb sedaj pritisnjen
        this.miskinGumbPritisnjen = true;

        if (this.poljePodMisko != null) {
            this.repaint(this.ohlapenOmejevalniOkvir(this.poljePodMisko));
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    /** Določi, kaj se zgodi ob spustu miškinega gumba. */
    @Override
    public void mouseReleased(MouseEvent e) {
        Partija partija = this.nadzornik.vrniPartijo();

        // če je igra že končana ali pa je na potezi stroj, se ne odzovi na 
        // pritiske in spuste miškinega gumba
        if (partija.jeKonec() || this.nadzornik.strojNaPotezi()) {
            return;
        }

        this.miskinGumbPritisnjen = false;

        if (this.poljePodMisko != null) {
            this.repaint(this.ohlapenOmejevalniOkvir(this.poljePodMisko));
        }

        // registriraj potezo
        Polje polje = this.poljeNaPoziciji(e.getPoint());
        if (polje != null && partija.preveriLegalnost(polje)) {
            this.repaint(this.ohlapenOmejevalniOkvir(polje));
            this.nadzornik.izvrsiVeljavnoPotezo(polje);
        }
    }

    /** Določi, kaj se zgodi, ko miška vstopi na igralno ploščo. */
    @Override
    public void mouseEntered(MouseEvent e) {
        this.mouseMoved(e);
    }

    /** Določi, kaj se zgodi, ko miška izstopi z igralne plošče. */
    @Override
    public void mouseExited(MouseEvent e) {
        if (this.poljePodMisko != null) {
            this.repaint(this.ohlapenOmejevalniOkvir(this.poljePodMisko));
        }
        this.poljePodMisko = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        this.mouseMoved(e);
    }

    /** Vrne polje, ki vsebuje podano točko.  Če takega polja ni, vrne null. */
    private Polje poljeNaPoziciji(Point2D tocka) {
        int stranica = Parametri.vrniStranico();
        for (int i = 0;  i < stranica;  i++) {
            for (int j = 0;  j < stranica;  j++) {
                if (this.sestkotniki[i][j].contains(tocka)) {
                    return new Polje(i, j);
                }
            }
        }
        return null;
    }

    /** Vrne ohlapen omejevalni okvir podanega polja. */
    private Rectangle ohlapenOmejevalniOkvir(Polje polje) {
        int vr = polje.vrniVrstico();
        int st = polje.vrniStolpec();
        Rectangle2D r = this.sestkotniki[vr][st].getBounds2D();
        double w0 = r.getWidth();
        double h0 = r.getHeight();
        double xOhlapnost = w0 * R_OHLAPNOST_OMEJEVALNEGA_OKVIRJA;
        double yOhlapnost = h0 * R_OHLAPNOST_OMEJEVALNEGA_OKVIRJA;
        int x = (int) Math.floor(r.getX() - xOhlapnost);
        int y = (int) Math.floor(r.getY() - yOhlapnost);
        int w = (int) Math.ceil(w0 + 2 * xOhlapnost);
        int h = (int) Math.ceil(h0 + 2 * yOhlapnost);
        return new Rectangle(x, y, w, h);
    }
}