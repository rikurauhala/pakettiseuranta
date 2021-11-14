package pakettiseuranta;

import java.sql.*;
import java.util.*;

public class Pakettiseuranta {
    public static void main(String[] args) throws SQLException {
        Scanner syote = new Scanner(System.in);
        Toiminnot t = new Toiminnot();
        Tehokkuustesti tt = new Tehokkuustesti();

        t.tulostaToiminnot();
        
        ohjelma: while (true) {
            System.out.print("> Valitse toiminto (0-9): ");
            int komento = syote.nextInt();
            
            switch (komento) {
                case 0:
                    break ohjelma;
                case 1:
                    t.luoTietokanta();
                    break;
                case 2:
                    t.lisaaPaikka();
                    break;
                case 3:
                    t.lisaaAsiakas();
                    break;
                case 4:
                    t.lisaaPaketti();
                    break;
                case 5:
                    t.lisaaTapahtuma();
                    break;
                case 6:
                    t.haePaketinTapahtumat();
                    break;
                case 7:
                    t.haeAsiakkaanPaketit();
                    break;
                case 8:
                    t.haePaikanTapahtumat();
                    break;
                case 9:
                    tt.suoritaTehokkuustesti();
                    break;
                default:
                    System.out.println("Virheellinen komento, yritä uudelleen");
            }
        }
    }
}
