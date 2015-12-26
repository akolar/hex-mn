
package ogrodje;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;

/** 
 * Objekt tega razreda predstavlja ploščo, ki služi kot gumb za pričetek nove
 * partije.
 */
public class GuiGumbNovaPartija extends JPanel {

    /** razmerje med širino `gumba' (plošče) in dolžino stranice polja šahovnice */
    private static final double R_SIRINA_POLJE = 0.4;

    /** ozadje plošče, ko nanjo ne kaže miškin kurzor */
    private static final Color B_OZADJE_SVETLO = Color.LIGHT_GRAY;

    /** ozadje plošče, ko nanjo kaže miškin kurzor */
    private static final Color B_OZADJE_TEMNO = Color.GRAY;

    /** rob plošče, ko ta ni `pritisnjena' (ko uporabnik ne drži miškinega gumba nad ploščo) */
    private static final Border ROB_DVIGNJEN = BorderFactory.createRaisedBevelBorder();

    /** rob plošče, ko je ta `pritisnjena' */
    private static final Border ROB_VTISNJEN = BorderFactory.createLoweredBevelBorder();

    /** rob plošče, ko je ta onemogočena */
    private static final Border ROB_PRAZEN = BorderFactory.createEmptyBorder();

    /** besedilo, ki se prikaže, ko uporabnik premakne miško nad gumbom 
     * (angl. `tool tip') */
    private static final String BESEDILO_NAMIGA = "Nova partija";

    /** koliko milisekund naj se počaka po prekinitvi igre (da se morebitne
     * poteze, ki še čakajo na izvedbo, ne bi izvedle v novi igri) */
    private static final int PREMOR_PO_PREKINITVI_IGRE = 100;

    /** true natanko v primeru, če je gumb onemogočen */
    private boolean onemogocen;

    /** Ustvari ploščo.
     * @param nadzornik objekt, preko katerega se sproži postopek za
     * vzpostavitev začetnega stanja igre
     */
    public GuiGumbNovaPartija(final GuiNadzornik nadzornik) {
        this.setBackground(B_OZADJE_SVETLO);
        this.setBorder(ROB_DVIGNJEN);
        this.setToolTipText(BESEDILO_NAMIGA);
        this.onemogocen = false;

        // določi, kaj se zgodi, ko uporabnik z miško pokaže na ploščo itd.
        this.addMouseListener(new MouseAdapter() {
            boolean misNaGumbu = false;

            @Override
            public void mouseEntered(MouseEvent e) {
                if (GuiGumbNovaPartija.this.onemogocen) {
                    return;
                }
                this.misNaGumbu = true;
                GuiGumbNovaPartija.this.setBackground(B_OZADJE_TEMNO);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (GuiGumbNovaPartija.this.onemogocen) {
                    return;
                }
                this.misNaGumbu = false;
                GuiGumbNovaPartija.this.setBackground(B_OZADJE_SVETLO);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (GuiGumbNovaPartija.this.onemogocen) {
                    return;
                }
                GuiGumbNovaPartija.this.setBorder(ROB_VTISNJEN);
            }

            /** Ob kliku na ploščo prekinemo obstoječo igro, nato počakamo
             * PREMOR_PO_PREKINITVI_IGRE milisekund (da `poplaknemo' morebitne
             * ostanke prejšnje igre), šele zatem pa pričnemo z novo igro. */
            @Override
            public void mouseReleased(MouseEvent e) {
                if (GuiGumbNovaPartija.this.onemogocen) {
                    return;
                }
                if (this.misNaGumbu) {
                    nadzornik.prekiniPartijo();
                    Timer casovnik = new Timer(PREMOR_PO_PREKINITVI_IGRE, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            nadzornik.novaPartija();
                        }
                    });
                    casovnik.setRepeats(false);
                    casovnik.start();
                }
                GuiGumbNovaPartija.this.setBorder(ROB_DVIGNJEN);
            }
        });
    }

    /** Onemogoči pritiske na gumb in spremeni njegov videz. */
    public void onemogoci() {
        this.onemogocen = true;
        this.setBackground(B_OZADJE_SVETLO);
        this.setBorder(ROB_PRAZEN);
        this.repaint();
    }

    /** Vrne širino plošče pri podanem premeru gumba. */
    public static double sirina(double dPolje) {
        return (R_SIRINA_POLJE * dPolje);
    }

    /** Nariše podobo gumba. */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!this.onemogocen) {
            GuiIkona.narisiSliko((Graphics2D) g, this.getWidth(), this.getHeight());
        }
    }
}