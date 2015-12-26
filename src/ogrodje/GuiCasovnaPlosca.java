
package ogrodje;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import javax.swing.JPanel;

/**
 * Objekt tega razreda predstavlja ploščo z `digitalnim' prikazovalnikom
 * preostalega časa. Čas se odšteva po sekundah ali desetinkah sekunde. Plošča
 * nima svojega časovnika; krmili jo zunanji časovnik.
 */
public class GuiCasovnaPlosca extends JPanel {

    /** relativna širina pravokotnika za prikaz ene "digitalne" števke */
    private static final double D_PRAVOKOTNIK = 4.0;

    /** relativni razmak med števkami pred decimalno piko */
    private static final double W_RAZMAK = 1.5;

    /** relativni razmak med zadnjo števko pred decimalno piko in prvo
     * števko za decimalno piko */
    private static final double W_DEC_RAZMAK = 3.0;

    /** relativna širina levega in desnega roba */
    private static final double W_ROB = 2.0;

    /** relativna višina zgornjega in spodnjega roba */
    private static final double H_ROB = 2.0;

    /** relativna širina števke */
    private static final double W_STEVKA = D_PRAVOKOTNIK + 2.0;

    /** relativna višina števke */
    private static final double H_STEVKA = 2 * D_PRAVOKOTNIK + 3;

    /** relativna višina celotnega prikazovalnika (širina je statična
     * spremenljivka s_wVse) */
    private static final double H_VSE = H_STEVKA + 2 * H_ROB;

    private static final boolean T = true;
    private static final boolean F = false;

    /*
        --     // črtica 0
       |  |    // črtici 1 in 2
        --     // črtica 3
       |  |    // črtici 4 in 5
        --     // črtica 6
     */

    /** STEVKE[s][c] == true :==: črtica `c' pri števki `s' je prižgana */
    private static final boolean[][] STEVKE = {
        {T, T, T, F, T, T, T},  // 0
        {F, F, T, F, F, T, F},  // 1
        {T, F, T, T, T, F, T},  // 2
        {T, F, T, T, F, T, T},  // 3
        {F, T, T, T, F, T, F},  // 4
        {T, T, F, T, F, T, T},  // 5
        {T, T, F, T, T, T, T},  // 6
        {T, F, T, F, F, T, F},  // 7
        {T, T, T, T, T, T, T},  // 8
        {T, T, T, T, F, T, T},  // 9
        {F, F, F, T, F, F, F},  // znak minus (-)
    };

    /** indeks podatkov za znak `minus' v tabeli STEVKE */
    private static final int MINUS = 10;

    /** ozadje prikazovalnika */
    private static final Color B_OZADJE = Color.BLACK;

    /** privzeto število mest na prikazovalniku */
    private static final int PRIVZETO_STEVILO_MEST = 3;

    /** število mest prikazovalnika (brez morebitnega mesta za desetinke) */
    private final int stMest;

    /** ali naj se prikazujejo desetinke (true) ali samo celi del (false) */
    private final boolean prikaziDesetinke;

    /** relativna širina celotnega prikazovalnika */
    private double wVse;

    /** začetni čas v milisekundah (0: neomejen) */
    private final long zacetniCas;

    /** true: čas ni omejen; prikazana bo črtica namesto ure */
    private final boolean neomejenCas;

    /** barva, s katero so prikazane števke */
    private final Color barvaStevk;

    /** trenutni čas v milisekundah, prikazan v prikazovalniku */
    private long trenutniCas;

    /** 
     * Ustvari objekt, ki predstavlja časovno ploščo.
     * @param zacetniCas  začetni čas, prikazan na časovni plošči 
     *                    (0: čas ni omejen, prikaže se samo črtica)
     * @param prikaziDesetinke  true: prikaži tudi desetinke;
     *                          false: prikaži samo celi del
     */
    public GuiCasovnaPlosca(long zacetniCas, boolean prikaziDesetinke, Color barvaStevk) {
        this.setBackground(B_OZADJE);
        this.zacetniCas = zacetniCas;
        this.neomejenCas = (zacetniCas == 0);
        this.prikaziDesetinke = prikaziDesetinke;
        this.stMest = izracunajSteviloMest(zacetniCas);
        this.barvaStevk = barvaStevk;

        // izračunaj relativno širino celotnega prikazovalnika
        // (odvisna je od števila mest in prikaza decimalk)
        this.wVse = this.stMest * W_STEVKA + (this.stMest - 1) * W_RAZMAK + 2 * W_ROB;
        if (prikaziDesetinke) {
            this.wVse += W_DEC_RAZMAK - W_RAZMAK;
        }
    }

    /** Vrne najprimernejše število mest na časovni plošči. */
    private static int izracunajSteviloMest(long zacetniCas) {
        if (zacetniCas == 0) {
            return PRIVZETO_STEVILO_MEST;
        }
        int s = (int) Math.ceil(Math.log10((double) (zacetniCas / 1000 + 1)));
        if (zacetniCas > 0) {
            s++;
        }
        return Math.max(s, PRIVZETO_STEVILO_MEST);
    }

    /** 
     * Izračuna absolutno širino časovne plošče pri podanem številu polj po
     * stranici igralne površine in dolžini šestkotnega polja. Širina časovne
     * plošče je odvisna od višine imenske plošče.
     * @param stranica  število polj po stranici igralne površine
     * @param dPolje  dolžina stranice šestkotnega polja
     */
    public double sirina(int stranica, double dPolje) {
        return (this.wVse * GuiImenskaPlosca.visina(stranica, dPolje) / H_VSE);
    }

    /** Nastavi prikazani čas na začetni čas. */
    public void novaPartija() {
        this.trenutniCas = this.zacetniCas;
        this.repaint();
    }

    /** Nastavi prikazani čas na podani čas (v milisekundah). */
    public void posodobiCas(long noviCas) {
        long prejsnjiCas = this.trenutniCas;
        this.trenutniCas = noviCas;

        if (!this.prikazaniCas(this.trenutniCas).equals(this.prikazaniCas(prejsnjiCas))) {
            this.repaint();
        }
    }

    /** Nariše vsebino časovne plošče ("digitalni" prikaz časa). */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = GuiRazno.nastaviAntialiasing(g, true);

        // določi ključne mere;
        // dKvadrat: absolutna dolžina stranice kvadrata, glede na katerega
        // so določene vse ostale mere
        double wPlosca = (double) this.getWidth();
        double hPlosca = (double) this.getHeight();
        double dKvadrat = Math.max(1.0, Math.min(wPlosca / this.wVse, hPlosca / H_VSE));
        double xLevo = (wPlosca - this.wVse * dKvadrat) / 2;
        double yZgoraj = (hPlosca - H_VSE * dKvadrat) / 2;

        // izdelaj večkotnik za vodoravno črtico in večkotnik za navpično črtico,
        // nato pa jih premikaj in riši
        int x0 = Razno.ri(dKvadrat / 2);
        int x1 = Razno.ri(dKvadrat);
        int x2 = Razno.ri((D_PRAVOKOTNIK + 1) * dKvadrat);
        int x3 = Razno.ri((D_PRAVOKOTNIK + 1.5) * dKvadrat);
        int y0 = 0;
        int y1 = Razno.ri(dKvadrat / 2);
        int y2 = Razno.ri(dKvadrat);
        int[] xp = {x0, x1, x2, x3, x2, x1};
        int[] yp = {y1, y0, y0, y1, y2, y2};
        int dx = Razno.ri(xLevo + W_ROB * dKvadrat);
        int dy = Razno.ri(yZgoraj + H_ROB * dKvadrat);
        int stOglisc = xp.length;

        // večkotnik za vodoravno črtico 
        Polygon pVodoravni = new Polygon(xp, yp, stOglisc);
        pVodoravni.translate(dx, dy);

        // večkotnik za navpično črtico 
        Polygon pNavpicni = new Polygon(yp, xp, stOglisc);
        pNavpicni.translate(dx, dy);

        // nariši vse števke števila (vključno s predznakom)
        String strCas = this.prikazaniCas(this.trenutniCas);
        int stZnakov = strCas.length();
        g.setColor(Color.GRAY);
        int iZnak = Math.max(0, stZnakov - this.stMest);

        // sprehod po številskih mestih prikazovalnika
        for (int iMesto = 0;  iMesto < this.stMest;  iMesto++) {
            // preveri, ali je na trenutnem mestu števka; če ni, pusti prazno
            int znak = -1;
            if (iMesto >= this.stMest - stZnakov) {
                char c = strCas.charAt(iZnak++);
                znak = (c == '-') ? MINUS : (c - '0');
            }
            if (znak == -1) continue;

            // nariši posamezne črtice števke
            dy = Razno.ri((D_PRAVOKOTNIK + 1) * dKvadrat);
            Polygon p = new Polygon(pVodoravni.xpoints, pVodoravni.ypoints, stOglisc);
            double ddx0 = (W_STEVKA + W_RAZMAK) * dKvadrat * iMesto;
            if (this.prikaziDesetinke && iMesto == this.stMest-1) {
                ddx0 += (W_DEC_RAZMAK - W_RAZMAK) * dKvadrat;
            }
            int dx0 = Razno.ri(ddx0);

            p.translate(dx0, 0);
            this.narisiCrtico(g, p, STEVKE[znak][0]);
            p.translate(0, dy);
            this.narisiCrtico(g, p, STEVKE[znak][3]);
            p.translate(0, dy);
            this.narisiCrtico(g, p, STEVKE[znak][6]);

            p = new Polygon(pNavpicni.xpoints, pNavpicni.ypoints, stOglisc);
            p.translate(dx0, 0);
            this.narisiCrtico(g, p, STEVKE[znak][1]);
            dy = Razno.ri((D_PRAVOKOTNIK + 1) * dKvadrat);
            p.translate(0, dy);
            this.narisiCrtico(g, p, STEVKE[znak][4]);
            dx = Razno.ri((D_PRAVOKOTNIK + 1) * dKvadrat);
            p.translate(dx, 0);
            this.narisiCrtico(g, p, STEVKE[znak][5]);
            p.translate(0, -dy);
            this.narisiCrtico(g, p, STEVKE[znak][2]);
        }

        // nariši decimalno piko
        if (this.prikaziDesetinke && !this.neomejenCas) {
            double xc = xLevo + (W_ROB + (this.stMest - 1) * W_STEVKA + 
                    (this.stMest - 2) * W_RAZMAK + W_DEC_RAZMAK / 2) * dKvadrat;
            double yc = yZgoraj + (H_ROB + H_STEVKA) * dKvadrat;
            double xl = xc - 0.4 * dKvadrat;
            double yz = yc - 1.0 * dKvadrat;
            g.setColor(this.barvaStevk);
            g2.fill(new Ellipse2D.Double(xl, yz, 1.5*dKvadrat, 1.5*dKvadrat));
        }
    }

    /** Vrne trenutni čas v obliki niza, primernega za prikaz. */
    private String prikazaniCas(long cas) {
        if (this.neomejenCas) {
            return "-";
        }
        if (this.prikaziDesetinke) {
            return String.format("%02d", cas / 100);
        } else {
            return Long.toString(cas / 1000);
        }
    }

    /** Če ima parameter `pobarvaj' vrednost `true', metoda pobarva podani
     * večkotnik s podano barvo, sicer pa ne naredi ničesar. Uporabno za
     * risanje posameznih "digitalnih" črtic. */
    private void narisiCrtico(Graphics g, Polygon p, boolean pobarvaj) {
        if (pobarvaj) {
            g.setColor(this.barvaStevk);
            g.fillPolygon(p);
        }
    }
}