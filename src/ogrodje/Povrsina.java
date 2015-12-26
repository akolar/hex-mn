
package ogrodje;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import skupno.Polje;

/**
 * Objekt tega razreda predstavlja stanje igre.
 */
public class Povrsina {

    /** fiktivno polje levo od levega roba površine
     * (to polje je povezano z vsemi polji na levem robu) */
    private static final Polje FIKTIVNO_POLJE_LEVO = new Polje(-101, -101);

    /** fiktivno polje nad zgornjim robom površine
     * (to polje je povezano z vsemi polji na zgornjem robu) */
    private static final Polje FIKTIVNO_POLJE_ZGORAJ = new Polje(-102, -102);

    /** fiktivno polje desno od desnega roba površine
     * (to polje je povezano z vsemi polji na desnem robu) */
    private static final Polje FIKTIVNO_POLJE_DESNO = new Polje(-103, -103);

    /** fiktivno polje pod spodnjim robom površine
     * (to polje je povezano z vsemi polji na spodnjem robu) */
    private static final Polje FIKTIVNO_POLJE_SPODAJ = new Polje(-104, -104);

    /** množica fiktivnih polj */
    private static final Set<Polje> FIKTIVNA_POLJA = new HashSet<Polje>() {{
        this.add(FIKTIVNO_POLJE_ZGORAJ);
        this.add(FIKTIVNO_POLJE_SPODAJ);
        this.add(FIKTIVNO_POLJE_LEVO);
        this.add(FIKTIVNO_POLJE_DESNO);
    }};

    /** dolžina stranice (število polj po stranici igralne površine) */
    private final int stranica;

    /** [i][j]: žeton na polju (i, j) */
    private final Zeton[][] vsebina;

    /** ~.get(p) = množica sosedov polja p */
    private final Map<Polje, Set<Polje>> sosedje;

    /**
     * Ustvari objekt, ki predstavlja vsebino igralne površine.
     * @param stranica  dolžina stranice
     */
    public Povrsina(int stranica) {
        this.stranica = stranica;
        this.vsebina = new Zeton[stranica][stranica];
        this.sosedje = new HashMap<>();

        for (int i = 0;  i < stranica;  i++) {
            for (int j = 0;  j < stranica;  j++) {
                Polje polje = new Polje(i, j);
                this.sosedje.put(polje, this.sosedjeNavadnega(polje));
                this.vsebina[i][j] = Zeton.NEOBSTOJEC;
            }
        }

        this.sosedje.put(FIKTIVNO_POLJE_ZGORAJ, this.sosedjeFiktivnega(0, 0, 0, 1));
        this.sosedje.put(FIKTIVNO_POLJE_SPODAJ, this.sosedjeFiktivnega(this.stranica - 1, 0, 0, 1));
        this.sosedje.put(FIKTIVNO_POLJE_LEVO, this.sosedjeFiktivnega(0, 0, 1, 0));
        this.sosedje.put(FIKTIVNO_POLJE_DESNO, this.sosedjeFiktivnega(0, this.stranica - 1, 1, 0));
    }

    /** Vrne množico sosedov podanega navadnega (nefiktivnega) polja. */
    private Set<Polje> sosedjeNavadnega(Polje polje) {
        int vrstica = polje.vrniVrstico();
        int stolpec = polje.vrniStolpec();

        Polje[] tPovezana = {
            new Polje(vrstica - 1, stolpec),
            new Polje(vrstica - 1, stolpec + 1),
            new Polje(vrstica,     stolpec - 1),
            new Polje(vrstica,     stolpec + 1),
            new Polje(vrstica + 1, stolpec - 1),
            new Polje(vrstica + 1, stolpec),
        };

        Set<Polje> sPovezana = new HashSet<>();

        for (Polje p: tPovezana) {
            int vr = p.vrniVrstico();
            int st = p.vrniStolpec();
            if (vr >= 0 && vr < this.stranica && st >= 0 && st < this.stranica) {
                sPovezana.add(p);

            } else {
                // sosedje robnih polj so fiktivna polja
                if (vr < 0) {
                    sPovezana.add(FIKTIVNO_POLJE_ZGORAJ);
                } else if (vr >= this.stranica) {
                    sPovezana.add(FIKTIVNO_POLJE_SPODAJ);
                }

                if (st < 0) {
                    sPovezana.add(FIKTIVNO_POLJE_LEVO);
                } else if (st >= this.stranica) {
                    sPovezana.add(FIKTIVNO_POLJE_DESNO);
                }
            }
        }

        return sPovezana;
    }

    /** Vrne množico sosedov fiktivnega polja.
     * @param vrZac     začetna vrstica množice sosedov
     * @param stZac     začetni stolpec množice sosedov
     * @param vrPremik  razlika med vrstičnima koordinatama dveh sosedov (0 ali 1)
     * @param stPremik  razlika med stolpčnima koordinatama dveh sosedov (0 ali 1)
     */
    private Set<Polje> sosedjeFiktivnega(int vrZac, int stZac, int vrPremik, int stPremik) {
        Set<Polje> mnozicaSosedov = new HashSet<>();
        int vr = vrZac;
        int st = stZac;
        for (int i = 0;  i < this.stranica;  i++) {
            mnozicaSosedov.add(new Polje(vr, st));
            vr += vrPremik;
            st += stPremik;
        }
        return mnozicaSosedov;
    }

    /** Vrne žeton na podanem polju. */
    public Zeton vrniVsebinoPolja(Polje polje) {
        return this.vsebina[polje.vrniVrstico()][polje.vrniStolpec()];
    }

    /** Nastavi vsebino podanega polja na podani žeton. */
    public void nastaviVsebinoPolja(Polje polje, Zeton zeton) {
        this.vsebina[polje.vrniVrstico()][polje.vrniStolpec()] = zeton;
    }

    /** Vrne true natanko v primeru, če je podano polje veljavno in prosto. */
    public boolean poljeVeljavnoInProsto(Polje polje) {
        int vrstica = polje.vrniVrstico();
        int stolpec = polje.vrniStolpec();
        return (vrstica >= 0 && vrstica < this.stranica &&
                stolpec >= 0 && stolpec < this.stranica &&
                ! this.vsebina[vrstica][stolpec].obstaja());
    }

    /** Vrne true natanko v priimeru, če je podano polje fiktivno. */
    private boolean jePoljeFiktivno(Polje polje) {
        return FIKTIVNA_POLJA.contains(polje);
    }

    /** Vrne zmagovalno verigo polj za igralca z barvo `zeton', če obstaja. V
     * nasprotnem primeru vrne null.  */
    public List<Polje> zmagovalnaVeriga(Zeton zeton) {
        List<Polje> veriga = 
                (zeton == Zeton.MODER) ?
                this.veriga(zeton, FIKTIVNO_POLJE_LEVO, FIKTIVNO_POLJE_DESNO) :
                this.veriga(zeton, FIKTIVNO_POLJE_ZGORAJ, FIKTIVNO_POLJE_SPODAJ);
        return (veriga == null) ? (null) : (veriga.subList(1, veriga.size() - 1));
    }

    /** Vrne verigo polj barve `zeton' od polja `zacetno' do polja `koncno',
     * če obstaja.  V nasprotnem primeru vrne null.  */
    public List<Polje> veriga(Zeton zeton, Polje zacetno, Polje koncno) {
        Set<Polje> obiskana = new HashSet<>();
        List<Polje> delnaVeriga = new ArrayList<>();
        return this.veriga(zeton, zacetno, koncno, obiskana, delnaVeriga);
    }

    /**
     * S pomočjo iskanja v globino poišče verigo polj barve `zeton' od polja
     * `zacetno' do polja `koncno'.  Če veriga obstaja, vrne eno od njih, sicer
     * pa vrne null.
     * 
     * @param zacetno  začetno polje na trenutnem nivoju rekurzije
     * @param obiskana polja, ki smo jih že obiskali (in jih ne bomo še enkrat)
     * @param delnaVeriga  veriga polj do trenutnega začetnega polja
     */
    private List<Polje> veriga(Zeton zeton, Polje zacetno, Polje koncno, 
            Set<Polje> obiskana, List<Polje> delnaVeriga) {

        delnaVeriga.add(zacetno);
        obiskana.add(zacetno);

        if (zacetno.equals(koncno)) {
            return delnaVeriga;
        }
        for (Polje sosed: this.sosedje.get(zacetno)) {
            boolean fiktivno = this.jePoljeFiktivno(sosed);
            if (fiktivno && sosed.equals(koncno) || 
                    !fiktivno && this.vrniVsebinoPolja(sosed) == zeton && !obiskana.contains(sosed)) {

                List<Polje> veriga = this.veriga(zeton, sosed, koncno, obiskana, delnaVeriga);
                if (veriga != null) {
                    return veriga;
                }
            }
        }
        delnaVeriga.remove(delnaVeriga.size() - 1);

        return null;
    }
}