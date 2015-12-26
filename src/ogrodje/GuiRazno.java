
package ogrodje;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 * Splošne zadeve, povezane z grafičnim uporabniškim vmesnikom.
 */

public class GuiRazno {

    public static final double KOREN_3 = Math.sqrt(3.0);

    /** 
     * V podanem grafičnem kontekstu vklopi oz. izklopi glajenje robov za lepši
     * izris.
     * @param nastavi true: vklopi; false: izklopi
     */
    public static Graphics2D nastaviAntialiasing(Graphics g, boolean nastavi) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                (nastavi ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF));
        return g2;
    }

    /** V podanem grafičnem kontekstu `nariše' podano besedilo na sredino
     * podanega pravokotnika. */
    public static void narisiBesedilo(Graphics g, Rectangle2D rect, String besedilo) {
        FontMetrics fm = g.getFontMetrics();
        double wBesedilo = fm.stringWidth(besedilo);
        double xIme = rect.getX() + (rect.getWidth() - wBesedilo) / 2;
        double yIme = rect.getY() + (rect.getHeight() + fm.getAscent()) / 2;
        g.drawString(besedilo, Razno.ri(xIme), Razno.ri(yIme));
    }

    /**
     * Izdela in vrne šestkotnik kot objekt tipa Path2D.
     * @param xOkvir  koordinata x zgornjega levega kota omejevalnega okvirja šestkotnika
     * @param yOkvir  koordinata y zgornjega levega kota omejevalnega okvirja šestkotnika
     * @param dPolje  dolžina stranice šestkotnika
     */
    public static Path2D izdelajSestkotnik(double xOkvir, double yOkvir, double dPolje) {
        // odmiki posameznih oglišč šestkotnika od zgornjega levega kota
        // omejevalnega pravokotnika šestkotnika
        double[] xOdmik = {
            dPolje * GuiRazno.KOREN_3 / 2.0,
            dPolje * GuiRazno.KOREN_3,
            dPolje * GuiRazno.KOREN_3,
            dPolje * GuiRazno.KOREN_3 / 2.0,
            0,
            0,
        };
        double[] yOdmik = {
            0,
            dPolje / 2.0,
            3.0 * dPolje / 2.0,
            2.0 * dPolje,
            3.0 * dPolje / 2.0,
            dPolje / 2.0,
        };

        // izdelaj šestkotnik
        Path2D sestkotnik = new Path2D.Double();
        sestkotnik.moveTo(xOkvir + xOdmik[0], yOkvir + yOdmik[0]);
        for (int k = 1;  k < xOdmik.length;  k++) {
            sestkotnik.lineTo(xOkvir + xOdmik[k], yOkvir + yOdmik[k]);
        }
        sestkotnik.closePath();
        return sestkotnik;
    }
}