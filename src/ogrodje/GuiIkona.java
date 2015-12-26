
package ogrodje;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Ta razred vsebuje statično metodo za izdelavo ikone programa.
 */

public class GuiIkona {

    /** velikost ikone */
    private static final int VELIKOST = 256;

    /** razmerje med dolžino roba in stranico šestkotnika */
    private static final double R_ROB_POLJE = 0.5;

    /** razmerje med debelino črte in dolžino stranice */
    private static final double R_DEBELINA_CRTE_POLJE = 0.01;

    /** razmerje med velikostjo pisave in okvirjem */
    private static final double R_PISAVA_OKVIR = 0.25;

    /** barva črk H in E */
    private static final Color B_BESEDILO_ZGORAJ = Color.BLACK;

    /** barva črke X */
    private static final Color B_BESEDILO_SPODAJ = Color.WHITE;

    /** razmerje med povišanjem okvirja črke in njegovo višino
     * (to potrebujemo zato, da se bo črka resnično nahajala na sredini okvirja) */
    private static final double R_POVISANJE_OKVIR_CRKE = 0.1;

    /** Ustvari in vrne sliko ikone. */
    public static Image ustvari() {
        BufferedImage slika = new BufferedImage(VELIKOST, VELIKOST, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = slika.createGraphics();
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 0, VELIKOST, VELIKOST);
        narisiSliko(g2, VELIKOST, VELIKOST);
        g2.dispose();
        return slika;
    }

    /**
     * Nariše sliko ikone podane velikosti v podanem grafičnem kontekstu. 
     * @param g2 grafični kontekst
     * @param wSlika širina ikone
     * @param hSlika višina ikone
     */
    public static void narisiSliko(Graphics2D g2, double wSlika, double hSlika) {
        GuiRazno.nastaviAntialiasing(g2, true);
        double dPoljeW = wSlika / (2.0 * GuiRazno.KOREN_3 + 2.0 * R_ROB_POLJE);
        double dPoljeH = hSlika / (3.5 + 2.0 * R_ROB_POLJE);
        double dPolje = Math.min(dPoljeW, dPoljeH);

        // določi položaj `jedra' slike znotraj površine slike
        double wJedro = dPolje * 2.0 * GuiRazno.KOREN_3;
        double hJedro = dPolje * 3.5;
        double xJedro = (wSlika - wJedro) / 2.0;
        double yJedro = (hSlika - hJedro) / 2.0;

        // izračunaj lege šestkotnikov, ki vsebujejo črke H, E in X
        Path2D leviSestkotnik = GuiRazno.izdelajSestkotnik(
                xJedro, yJedro, dPolje);
        Path2D desniSestkotnik = GuiRazno.izdelajSestkotnik(
                xJedro + dPolje * GuiRazno.KOREN_3, yJedro, dPolje);
        Path2D spodnjiSestkotnik = GuiRazno.izdelajSestkotnik(
                xJedro + dPolje * GuiRazno.KOREN_3 / 2.0, yJedro + 3.0 * dPolje / 2.0, dPolje);

        // nariši šestkotnike
        g2.setColor(GuiIgralnaPlosca.B_POLJE[0]);
        g2.fill(leviSestkotnik);
        g2.fill(desniSestkotnik);
        g2.setColor(GuiIgralnaPlosca.B_POLJE[1]);
        g2.fill(spodnjiSestkotnik);

        double dSlika = Math.min(wSlika, hSlika);
        float debelina = (float) (dSlika * R_DEBELINA_CRTE_POLJE);
        g2.setStroke(new BasicStroke(Math.max(debelina, 1.0f)));
        g2.setColor(GuiIgralnaPlosca.B_OBROBA_POLJA);
        g2.draw(leviSestkotnik);
        g2.draw(desniSestkotnik);
        g2.draw(spodnjiSestkotnik);

        // nariši napise
        g2.setFont(new Font("SansSerif", Font.BOLD, (int) (R_PISAVA_OKVIR * dSlika)));
        g2.setColor(B_BESEDILO_ZGORAJ);
        GuiRazno.narisiBesedilo(g2, vrniOmejevalniOkvir(leviSestkotnik), "H");
        GuiRazno.narisiBesedilo(g2, vrniOmejevalniOkvir(desniSestkotnik), "E");
        g2.setColor(B_BESEDILO_SPODAJ);
        GuiRazno.narisiBesedilo(g2, vrniOmejevalniOkvir(spodnjiSestkotnik), "X");
    }

    /**
     * Vrne nekoliko dvignjen omejevalni okvir podanega šestkotnika, tako da je
     * besedilo možno postaviti točno na njegovo sredino.
     */
    private static Rectangle2D vrniOmejevalniOkvir(Path2D sestkotnik) {
        Rectangle2D okvir = sestkotnik.getBounds2D();
        double x = okvir.getX();
        double y = okvir.getY();
        double w = okvir.getWidth();
        double h = okvir.getHeight();
        double d = R_POVISANJE_OKVIR_CRKE * h;
        return new Rectangle.Double(x, y - d, w, h + d);
    }
}