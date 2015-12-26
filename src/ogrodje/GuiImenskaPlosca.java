
package ogrodje;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JPanel;

/** 
 * Objekt tega razreda predstavlja ploščo za prikaz imena igralca.  Ob koncu
 * igre se -- če plošča pripada zmagovalcu ali pa če je bil dosežen remi --
 * izvede animirano spreminjanje intenzitete barve ozadja od temnega do
 * svetlega in nazaj do temnega.  Za animacijo skrbi interni animacijski
 * časovnik. 
 */
public class GuiImenskaPlosca extends JPanel implements GuiGostiteljAnimacije {

    /** razmerje med višino imenske plošče in produktom števila polj po stranici
     * igralne površine in dolžine stranice šestkotnega polja */
    private static final double R_VISINA_STRANICA_POLJE = 0.30;

    /** minimalno razmerje med širino in višino imenske plošče. */
    private static final double MIN_R_SIRINA_VISINA = 3.0;

    /** intenziteta barve ozadja (v animaciji bomo to spreminjali) */
    private static final float ZACETNA_INTENZITETA = 0.375f;

    /** REL_INTENZITETE[i][k] = relativna intenziteta barvne komponente k
     * za igralca z indeksom i */
    private static final float[][] REL_INTENZITETE = {
        {1.0f, 0.0f, 0.0f},   // rdeči
        {0.0f, 0.0f, 1.0f},   // modri
    };

    /** barva napisa, ki prikazuje ime */
    private static final Color B_PISAVA = Color.WHITE;

    /** razmerje med velikostjo pisave in višino plošče */
    private static final double R_PISAVA_VISINA = 0.5;

    /** osnovna pisava (dejanska pisava je večja ali manjša različica osnovne) */
    private static final Font OSNOVNA_PISAVA = new Font(Font.SANS_SERIF, Font.PLAIN, 10);

    /** kolikokrat se ponovi osnovna animacija (temno-svetlo-temno) ob koncu igre */
    private static final int ST_PONOVITEV_ANIMACIJE = 10;

    /** za koliko se poveča oz. zmanjša intenziteta barve ob vsaki sprožitvi
     * časovnika */
    private static final float INKREMENT = 0.025f;

    /** kolikokrat se intenziteta poveča (in zmanjša) za INKREMENT v okviru
     * ene ponovitve animacije */
    private static final int ST_POVECEVANJ = 20;

    /** ime igralca, ki mu pripada ta imenska plošča */
    private final String ime;

    /** indeks igralca, ki mu pripada ta imenska plošča */
    private final int ixIgralca;

    /** trenutna intenziteta barve ozadja (v animaciji) */
    private float intenziteta;

    /** število sprememb intenzitete (v animaciji) */
    private int stSpremembIntenzitete;

    /** objekt, ki poskrbi za izvedbo animacije */
    private GuiAnimator animator;

    /** Ustvari imensko ploščo, ki pripada podanemu igralcu. */
    public GuiImenskaPlosca(int ixIgralca, String ime) {
        this.ixIgralca = ixIgralca;
        this.ime = ime;
    }

    /** Poskrbi za enkratno inicializacijo. Ta metoda naj se pokliče takoj po
     * konstruktorju. */
    public void inicializiraj() {
        this.ponastaviBarvoOzadja();

        this.animator = new GuiAnimator(this) {
            /** Inicializira spremenljivki animacije (`stSpremembIntenzitete' 
             * in `intenziteta'). */
            @Override
            protected void zacetek() {
                GuiImenskaPlosca.this.stSpremembIntenzitete = 0;
                GuiImenskaPlosca.this.intenziteta = ZACETNA_INTENZITETA;
            }

            /** Posodobi spremenljivki animacije.  Vrne `true', če gre za 
             * zadnji korak animacije, sicer pa vrne `false'. */
            @Override
            protected boolean korak() {
                GuiImenskaPlosca ip = GuiImenskaPlosca.this;
                int razmerje = ip.stSpremembIntenzitete / ST_POVECEVANJ;
                // faktor: +1 ali -1 (odvisno od tega, ali posvetljujemo ali potemnjujemo)
                int faktor = 1 - 2 * (razmerje % 2);  
                ip.intenziteta += faktor * INKREMENT;
                ip.stSpremembIntenzitete++;

                // Smo že na koncu animacije?  Faktor 2 je potreben zato,
                // ker štejemo tako povečevanja kot zmanjševanja intenzitete.
                return (razmerje >= 2 * ST_PONOVITEV_ANIMACIJE);
            }
        };
    }

    /**
     * Izračuna minimalno širino imenske plošče glede na število polj po
     * stranici igralne površine in dolžino stranice šestkotnega polja.
     * @param stranica  število polj po stranici igralne površine
     * @param dPolje  dolžina stranice šestkotnega polja
     */
    public static double minSirina(int stranica, double dPolje) {
        return (MIN_R_SIRINA_VISINA * visina(stranica, dPolje));
    }

    /**
     * Vrne višino imenske plošče glede na število polj po stranici igralne
     * površine in dolžino stranice šestkotnega polja.
     * @param stranica  število polj po stranici igralne površine
     * @param dPolje  dolžina stranice šestkotnega polja
     */
    public static double visina(int stranica, double dPolje) {
        return (Math.sqrt((double) stranica) * dPolje * R_VISINA_STRANICA_POLJE);
    }

    /** Ponastavi ozadje in ustavi animacijski časovnik (če morda teče). */
    public void novaPartija() {
        this.ponastaviBarvoOzadja();
        this.animator.ustavi();
    }

    /** Prične z animacijo ozadja temno-svetlo-temno. */
    public void animiraj() {
        this.animator.sprozi();
    }

    /** Ta metoda se pokliče po vsakem vmesnem koraku animacije. */
    @Override
    public void poVmesnemKorakuAnimacije() {
        this.nastaviBarvoOzadja();
    }

    /** Ta metoda se pokliče po končnem koraku animacije. */
    @Override
    public void poKoncuAnimacije() {
        this.animator.ustavi();
        this.nastaviBarvoOzadja();
    }

    /** Nastavi barvo ozadja na trenutno intenziteto. */
    private void nastaviBarvoOzadja() {
        float[] faktorji = REL_INTENZITETE[this.ixIgralca];
        this.setBackground(new Color(faktorji[0] * this.intenziteta,
                faktorji[1] * this.intenziteta, faktorji[2] * this.intenziteta));
    }

    /** Nastavi barvo ozadja na začetno intenziteto. */
    private void ponastaviBarvoOzadja() {
        this.intenziteta = ZACETNA_INTENZITETA;
        this.nastaviBarvoOzadja();
    }

    /** "Nariše" ime igralca na imensko ploščo.  Če je ime predolgo, 
     * ga okrajša s tropičjem (...). */ 
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GuiRazno.nastaviAntialiasing(g, true);

        // določi velikost pisave glede na trenutno višino plošče
        int wPlosca = this.getWidth();
        int hPlosca = this.getHeight();
        float velikostPisave = (float) (hPlosca * R_PISAVA_VISINA);
        g.setFont(OSNOVNA_PISAVA.deriveFont(velikostPisave));

        // "nariši ime
        g.setColor(B_PISAVA);
        FontMetrics fm = g.getFontMetrics();
        String s = this.ime;
        String t = this.ime;
        while (fm.stringWidth(t) >= wPlosca) {
            s = s.substring(0, s.length()-1);
            t = s + "...";
        }
        GuiRazno.narisiBesedilo(g, new Rectangle(0, 0, wPlosca, hPlosca), t);
    }
}