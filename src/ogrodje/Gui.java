
package ogrodje;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Ta razred je vstopna točka za grafični uporabniški vmesnik.
 */
public class Gui {

    /** ključ, pod katerim je shranjena lega okna v objektu tipa Properties */
    private static final String KLJUC_OKNO = "okno";

    /** datoteka, v katero se shranjuje lega okna */
    private static final File DATOTEKA_OKNO = new File("okno.xml");

    /** 
     * Objekt tega razreda je preslikava med posameznimi dolžinami stranic
     * igralne površine in shranjenimi velikosti oken.
     * Na primer, ~.get(10) == Rectangle(100, 200, 300, 400) pove, da je bila
     * igra 10 x 10 nazadnje odigrana v oknu 300 x 400 s pričetkom na 
     * zaslonskih koordinatah (100, 200).
     */
    private static class Stranica2Okno extends HashMap<Integer, Rectangle> {

        private static final String MALO_LOCILO = "|";
        private static final String VELIKO_LOCILO = "||";

        private static final String R_MALO_LOCILO = "\\|";
        private static final String R_VELIKO_LOCILO = "\\|\\|";

        /**
         * Izlušči preslikavo iz zapisa v vrstici.  Na primer, vrstica
         * 10|100|200|300|400||11|400|200|500|300
         * podaja sledečo preslikavo:
         * 10 x 10 -- Rectangle(100, 200, 300, 400)
         * 11 x 11 -- Rectangle(400, 200, 500, 300)
         * 
         */
        public static Stranica2Okno izlusci(String vrstica) {
            String[] elementi = vrstica.split(R_VELIKO_LOCILO);
            Stranica2Okno s2o = new Stranica2Okno();
            for (String element: elementi) {
                String[] sxywh = element.split(R_MALO_LOCILO);
                try {
                    int stranica = Integer.parseInt(sxywh[0]);
                    Rectangle pravokotnik = new Rectangle(
                        Integer.parseInt(sxywh[1]),
                        Integer.parseInt(sxywh[2]),
                        Integer.parseInt(sxywh[3]),
                        Integer.parseInt(sxywh[4])
                    );
                    s2o.put(stranica, pravokotnik);
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
            }
            return s2o;
        }

        /** Vrne zapis preslikave this v eni vrstici. */
        @Override
        public String toString() {
            List<Integer> stranice = new ArrayList<>(this.keySet());
            Collections.sort(stranice);
            StringBuilder sb = new StringBuilder();
            boolean prvic = true;
            for (int stranica: stranice) {
                if (!prvic) {
                    sb.append(VELIKO_LOCILO);
                }
                prvic = false;
                Rectangle pravokotnik = this.get(stranica);
                sb.append( String.format("%s%s%s%s%s%s%s%s%s",
                        stranica, MALO_LOCILO,
                        pravokotnik.x, MALO_LOCILO,
                        pravokotnik.y, MALO_LOCILO,
                        pravokotnik.width, MALO_LOCILO,
                        pravokotnik.height) );
            }
            return sb.toString();
        }
    }

    /** preslikava iz posameznih dolžin stranice igralne plošče v pripadajoče
     * lege in velikosti okna */
    private Stranica2Okno stranica2okno;

    public Gui() {
        this.stranica2okno = null;
    }

    /** Inicializira nadzornik in prikaže GUI. */
    public void inicializiraj(final Seansa seansa) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GuiNadzornik nadzornik = new GuiNadzornik(seansa);
                Gui.this.ustvariInPrikaziGUI(nadzornik);
            }
        });
    }

    /** Ustvari in prikaže celoten grafični uporabniški vmesnik. */
    private void ustvariInPrikaziGUI(final GuiNadzornik nadzornik) {
        // oznake naj bodo po privzetih nastavitvah prikazane v normalni
        // (namesto v poudarjeni) pisavi
        javax.swing.UIManager.put("swing.boldMetal", Boolean.FALSE);

        // ustvari okno
        final JFrame okno = new JFrame("Hex");
        okno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        okno.setIconImage(GuiIkona.ustvari());

        // ustvari krovno ploščo z vsemi podploščami in jo postavi na okno
        GuiKrovnaPlosca krovnaPlosca = new GuiKrovnaPlosca(nadzornik);
        okno.add(krovnaPlosca, BorderLayout.CENTER);

        // posreduj nadzorniku objekt, ki predstavlja krovno ploščo
        // (nadzornik je posrednik med ploščami ter med ploščami in stanjem
        // igre)
        nadzornik.inicializiraj(krovnaPlosca);

        // ob zaprtju okna shrani lego okna v datoteko DATOTEKA_OKNO
        okno.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int stranica = Parametri.vrniStranico();
                Gui.this.stranica2okno.put(stranica, okno.getBounds());
                Gui.this.shraniPolozajeOken();
            }
        });

        // ko se okno prikaže, prični partijo
        okno.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                nadzornik.novaPartija();
            }
        });

        // preberi preslikavo tipa Stranica2Okno iz datoteke DATOTEKA_OKNO,
        // če ta obstaja
        this.preberiPolozajeOken();

        int stranica = Parametri.vrniStranico();
        Rectangle okvir = this.stranica2okno.get(stranica);

        if (okvir == null) {
            // nimamo podatkov o legi in velikosti okna pri podani dolžini 
            // stranice igralne površine, zato nastavi okno na kvečjemu 3/4 
            // velikosti zaslona po obeh dimenzijah in ga postavi na sredino 
            // zaslona
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension velikostZaslona = toolkit.getScreenSize();
            Dimension dPref = krovnaPlosca.getPreferredSize();
            int w = Math.min(3 * velikostZaslona.width / 4, dPref.width);
            int h = Math.min(3 * velikostZaslona.height / 4, dPref.height);
            int x = (velikostZaslona.width - w) / 2;
            int y = (velikostZaslona.height - h) / 2;
            okno.setBounds(x, y, w, h);

        } else {
            okno.setBounds(okvir);
        }

        // prikaži okno s celotno vsebino
        okno.setVisible(true);
    }

    /** Prebere preslikavo this.stranica2okno iz datoteke DATOTEKA_OKNO. */
    private void preberiPolozajeOken() {
        try (InputStream vhodniTok = new FileInputStream(DATOTEKA_OKNO)) {
            Properties properties = new Properties();
            properties.loadFromXML(vhodniTok);
            this.stranica2okno = Stranica2Okno.izlusci(properties.getProperty(KLJUC_OKNO, ""));

        } catch (IOException ex) {
            this.stranica2okno = new Stranica2Okno();
        }
    }

    /** Shrani this.stranica2okno v datoteko DATOTEKA_OKNO. */
    private void shraniPolozajeOken() {
        Properties properties = new Properties();
        properties.setProperty(KLJUC_OKNO, this.stranica2okno.toString());
        try (OutputStream izhodniTok = new FileOutputStream(DATOTEKA_OKNO)) {
            properties.storeToXML(izhodniTok, "Okno", "UTF-8");
        } catch (IOException ex) {
            System.err.println("Napaka pri shranjevanju lege okna.");
        }
    }
}