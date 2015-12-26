
package ogrodje;

import java.io.File;

/**
 * Objekt tega razreda predstavlja parametre ob zagonu programa.
 */
public class Parametri {
    
    /** dolžina stranice igralne plošče (vrednost parametra -s) */
    private int stranica;
    
    /** [0]: razred, ki mu pripada igralec 1 (null, če gre za človeka);
     *  [1]: razred, ki mu pripada igralec 2 (null, če gre za človeka) */
    private final String[] razredaIgralcev;
    
    /** true: igralca v zaporednih partijah izmenično pričenjata;
     * false: vedno prične igralec, naveden ob parametru -1 */
    private boolean izmenicnoPricenjanje;
    
    /** časovna omejitev za stroj(a) v milisekundah (vrednost parametra -t) */
    private int casovnaOmejitev;
    
    /** število odigranih partij (vrednost parametra -n); 0 pomeni neomejeno */
    private int steviloPartij;
    
    /** true: grafični vmesnik; false: besedilni vmesnik (parameter -b) */
    private boolean gui;
    
    /** datoteka, v katero se piše dnevnik (vrednost parametra -d);
     * null, če naj se dnevnik ne piše */
    private File dnevniskaDatoteka;
    
    /** časovna zakasnitev po vsaki potezi v igri stroja proti stroju
     * (vrednost parametra -z) */
    private int zakasnitevPoPotezi;
    
    /** časovna zakasnitev po vsaki igri v zaporedju iger stroja proti stroju
     * (vrednost parametra -zz) */
    private int zakasnitevPoIgri;
    
    /** edini objekt tipa Parametri v sistemu */
    private static Parametri s_parametri;
    
    private Parametri() {
        this.razredaIgralcev = new String[]{null, null};
        this.stranica = 11;
        this.casovnaOmejitev = 10000;
        this.steviloPartij = 1;
        this.gui = true;
        this.dnevniskaDatoteka = null;
        this.zakasnitevPoPotezi = 500;
        this.zakasnitevPoIgri = 3000;
    }
    
    /**
     * Izlušči parametre iz ukazne vrstice.
     * @return true v primeru pravilnih parametrov; false sicer
     */
    public static boolean izlusci(String[] args) {
        s_parametri = new Parametri();
        
        boolean uporabaParametraT = false;
        boolean uporabaParametraN = false;
        boolean uporabaParametraZ = false;
        boolean uporabaParametraZZ = false;
        int iArg = 0;
        try {
            while (iArg < args.length) {
                int ixIgralca;
                switch (args[iArg]) {
                    case "-1":
                    case "-2":
                        ixIgralca = args[iArg].charAt(1) - '1';
                        s_parametri.razredaIgralcev[ixIgralca] = args[++iArg];
                        break;
                        
                    case "-s":
                        s_parametri.stranica = Integer.parseInt(args[++iArg]);
                        break;
                        
                    case "-t":
                        uporabaParametraT = true;
                        s_parametri.casovnaOmejitev = Integer.parseInt(args[++iArg]);
                        break;
                        
                    case "-n":
                        uporabaParametraN = true;
                        s_parametri.steviloPartij = Integer.parseInt(args[++iArg]);
                        if (s_parametri.steviloPartij < 1) {
                            throw new RuntimeException("parameter izbire -n mora biti večji ali enak 1");
                        }
                        break;
                        
                    case "-b":
                        s_parametri.gui = false;
                        break;
                        
                    case "-d":
                        s_parametri.dnevniskaDatoteka = new File(args[++iArg]);
                        break;
                        
                    case "-z":
                        uporabaParametraZ = true;
                        s_parametri.zakasnitevPoPotezi = Integer.parseInt(args[++iArg]);
                        break;
                        
                    case "-zz":
                        uporabaParametraZZ = true;
                        s_parametri.zakasnitevPoIgri = Integer.parseInt(args[++iArg]);
                        break;
                        
                    case "-i":
                        s_parametri.izmenicnoPricenjanje = true;
                        break;
                        
                    default:
                        System.err.println(navodiloOUporabi());
                        return false;
                }
                iArg++;
            }
            
            if (uporabaParametraN && !strojVsStroj(s_parametri.razredaIgralcev)) {
                System.err.println("Parameter -n ima smisel samo pri igri stroja proti stroju.");
            }
            if (uporabaParametraZ && !strojVsStroj(s_parametri.razredaIgralcev)) {
                System.err.println("Parameter -z ima smisel samo pri igri stroja proti stroju.");
            }
            if (uporabaParametraZZ && !strojVsStroj(s_parametri.razredaIgralcev)) {
                System.err.println("Parameter -zz ima smisel samo pri igri stroja proti stroju.");
            }
            if (uporabaParametraZZ && strojVsStroj(s_parametri.razredaIgralcev) && s_parametri.steviloPartij <= 1) {
                System.err.println("Parameter -zz ima smisel samo v primeru, če je vrednost parametra -n enaka najmanj 2.");
            }
            if (uporabaParametraT && clovekVsClovek(s_parametri.razredaIgralcev)) {
                System.err.println("Parameter -t pri igri človeka proti človeku nima smisla.");
            }
            
            return true;

        } catch (RuntimeException ex) {
            System.err.println("Napaka pri branju parametrov");
            System.err.println();
            System.err.println(navodiloOUporabi());
            return false;
        }
    }

    /** Vrne dolžino stranice igralne površine (število polj po stranici). */
    public static int vrniStranico() {
        return s_parametri.stranica;
    }

    /**
     * Vrne true natanko v primeru, če igramo v grafičnem uporabniškem
     * vmesniku.
     */
    public static boolean jeGUI() {
        return s_parametri.gui;
    }

    /** Vrne razreda igralcev. */
    public static String[] vrniRazredaIgralcev() {
        return s_parametri.razredaIgralcev;
    }

    public static int vrniSteviloPartij() {
        return s_parametri.steviloPartij;
    }

    /** Vrne časovno omejitev za strojna igralca. */
    public static int vrniCasovnoOmejitev() {
        return s_parametri.casovnaOmejitev;
    }

    /** Vrne datoteko, v katero se bo pisal dnevnik. */
    public static File vrniDnevniskoDatoteko() {
        return s_parametri.dnevniskaDatoteka;
    }

    /** Vrne zakasnitev po vsaki potezi v igri stroja proti stroju. */
    public static int vrniZakasnitevPoPotezi() {
        return s_parametri.zakasnitevPoPotezi;
    }

    /** Vrne zakasnitev po vsaki igri v zaporedju iger stroja proti stroju. */
    public static int vrniZakasnitevPoIgri() {
        return s_parametri.zakasnitevPoIgri;
    }

    /** Vrne true natanko v primeru, če igralca v zaporedju iger izmenično pričenjata. */
    public static boolean izmenicnoPricenjanje() {
        return s_parametri.izmenicnoPricenjanje;
    }

    /** Vrne true natanko v primeru, če človek igra proti človeku. */
    private static boolean clovekVsClovek(String[] razredaIgralcev) {
        return (razredaIgralcev[0] == null) && (razredaIgralcev[1] == null);
    }

    /** Vrne true natanko v primeru, če stroj igra proti stroju. */
    private static boolean strojVsStroj(String[] razredaIgralcev) {
        return (razredaIgralcev[0] != null) && (razredaIgralcev[1] != null);
    }

    /** Vrne niz, ki vsebuje navodilo o zagonu ogrodja Hex. */
    private static String navodiloOUporabi() {
        return String.format(
                "java Hex [neobvezni_parametri]%n%n"
                        + "neobvezni_parametri:%n%n"
                        + "    -s <stranica>: dolžina stranice igralne površine%n%n"
                        + "    -1 <stroj>   : stroj, ki bo v prvi partiji nastopal kot prvi igralec%n%n"
                        + "    -2 <stroj>   : stroj, ki bo v prvi partiji nastopal kot drugi igralec%n%n"
                        + "    -t <milisek> : časovna omejitev v milisekundah za (oba) stroj(a)%n%n"
                        + "    -n <stPartij>: število samodejno odigranih partij (samo za način stroj vs. stroj)%n%n"
                        + "    -b           : besedilni vmesnik namesto grafičnega%n%n"
                        + "    -d <datoteka>: po koncu vsake partije dodaj njen potek v podano datoteko%n%n"
                        + "    -z <milisek> : zakasnitev po vsaki odigrani potezi pri igri stroj vs. stroj%n%n"
                        + "    -zz <milisek>: zakasnitev po vsaki odigrani partiji pri igri stroj vs. stroj%n%n"
                        + "    -i           : izmenično pričenjanje v zaporednih partijah%n%n"
        );
    }
}
