package pakettiseuranta;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Scanner;

public class Toiminnot {
    private final Scanner syote;
    private final String data;
    
    public Toiminnot() throws SQLException {
        this.data = "jdbc:sqlite:data.db";
        this.syote = new Scanner(System.in);
    }
    
    public void tulostaToiminnot() {
        System.out.println("Toiminnot:");
        System.out.println("- 0: Lopeta ohjelma");
        System.out.println("- 1: Luo tietokanta");
        System.out.println("- 2: Lisää uusi paikka");
        System.out.println("- 3: Lisää uusi asiakas");
        System.out.println("- 4: Lisää uusi paketti");
        System.out.println("- 5: Lisää uusi tapahtuma");
        System.out.println("- 6: Hae paketin tapahtumat seurantakoodin perusteella");
        System.out.println("- 7: Hae asiakkaan paketit ja niiden tapahtumat");
        System.out.println("- 8: Hae annetusta paikasta tapahtumien määrä tiettynä päivänä");
        System.out.println("- 9: Suorita tietokannan tehokkuustesti\n");
    }
    
    public void luoTietokanta() throws SQLException {
        Connection db = DriverManager.getConnection(data);
        try {
            Statement s = db.createStatement();
            s.execute("CREATE TABLE Asiakkaat (id INTEGER PRIMARY KEY, nimi TEXT UNIQUE)");
            s.execute("CREATE TABLE Paketit (id INTEGER PRIMARY KEY, seurantakoodi TEXT UNIQUE, asiakas_id INTEGER REFERENCES Asiakkaat)");
            s.execute("CREATE TABLE Paikat (id INTEGER PRIMARY KEY, nimi TEXT UNIQUE)");
            s.execute("CREATE TABLE Tapahtumat (id INTEGER PRIMARY KEY, kuvaus TEXT, lisayshetki INTEGER, paketti_id INTEGER REFERENCES Paketit, paikka_id INTEGER REFERENCES Paikat)");
            s.execute("CREATE INDEX idx_asiakas ON Paketit (asiakas_id)");
            s.execute("CREATE INDEX idx_paketti ON Tapahtumat (paketti_id)");
            s.execute("PRAGMA foreign_keys = ON");
            System.out.println("Tietokanta luotu");
        } catch (SQLException e) {
            System.out.println("Tapahtui virhe: " + e.getMessage());
        } finally {
            db.close();
        }
    }
    
    public void lisaaPaikka() throws SQLException {
        System.out.print("> Anna paikan nimi: ");
        String nimi = syote.nextLine();
        
        Connection db = DriverManager.getConnection(data);
        
        try {
            PreparedStatement p = db.prepareStatement("INSERT INTO Paikat(nimi) VALUES (?)");
            p.setString(1, nimi);
            p.executeUpdate();
            System.out.println("Paikka lisätty");
        } catch (SQLException e) {
            System.out.println("Tapahtui virhe: " + e.getMessage());
        } finally {
            db.close();
        }
    }
    
    public void lisaaAsiakas() throws SQLException {
        System.out.print("> Anna asiakkaan nimi: ");
        String nimi = syote.nextLine();
        
        Connection db = DriverManager.getConnection(data);
        
        try {
            PreparedStatement p = db.prepareStatement("INSERT INTO Asiakkaat(nimi) VALUES (?)");
            p.setString(1, nimi);
            p.executeUpdate();
            System.out.println("Asiakas lisätty");
        } catch (SQLException e) {
            System.out.println("Tapahtui virhe: " + e.getMessage());
        } finally {
            db.close();
        }
    }
     
    public void lisaaPaketti() throws SQLException {
        String seurantakoodi, asiakas;
        int asiakasId;
        
        Connection db = DriverManager.getConnection(data);
        
        while (true) {
            System.out.print("> Anna paketin seurantakoodi: ");
            seurantakoodi = syote.nextLine();
            try {
                PreparedStatement p = db.prepareStatement("SELECT COUNT(*) AS lkm FROM Paketit WHERE seurantakoodi=?");
                p.setString(1, seurantakoodi);
                ResultSet r = p.executeQuery();
                if (r.getInt("lkm") == 0) {
                    break;
                } else {
                    System.out.println("Seurantakoodilla " + seurantakoodi + " löytyy jo paketti. Valitse uusi seurantakoodi.");
                }
            } catch (SQLException e) {
                System.out.println("Tapahtui virhe: " + e.getMessage());
            }
        }
        
        while (true) {
            System.out.print("> Anna asiakkaan nimi: ");
            asiakas = syote.nextLine();
            try {
                PreparedStatement p = db.prepareStatement("SELECT COUNT(*) AS lkm FROM Asiakkaat WHERE nimi=?");
                p.setString(1, asiakas);
                ResultSet r = p.executeQuery();
                if (r.getInt("lkm") == 1) {
                    p = db.prepareStatement("SELECT id FROM Asiakkaat WHERE nimi=?");
                    p.setString(1, asiakas);
                    r = p.executeQuery();
                    asiakasId = r.getInt("id");
                    break;
                } else {
                    System.out.println("Asiakasta " + asiakas + " ei löytynyt. Yritä uudestaan.");
                }
            } catch (SQLException e) {
                System.out.println("Tapahtui virhe: " + e.getMessage());
            }
        }
        
        try {
            PreparedStatement p = db.prepareStatement("INSERT INTO Paketit(seurantakoodi, asiakas_id) VALUES (?,?)");
            p.setString(1, seurantakoodi);
            p.setInt(2, asiakasId);
            p.executeUpdate();
            System.out.println("Paketti lisätty");
        } catch (SQLException e) {
            System.out.println("Tapahtui virhe: " + e.getMessage());
        } finally {
            db.close();
        }
    }
    
    public void lisaaTapahtuma() throws SQLException {
        String seurantakoodi, paikka, kuvaus;
        int pakettiId, paikkaId;
        
        Connection db = DriverManager.getConnection(data);
        
        while (true) {
            System.out.print("> Anna paketin seurantakoodi: ");
            seurantakoodi = syote.nextLine();
            try {
                PreparedStatement p = db.prepareStatement("SELECT COUNT(*) AS lkm FROM Paketit WHERE seurantakoodi=?");
                p.setString(1, seurantakoodi);
                ResultSet r = p.executeQuery();
                if (r.getInt("lkm") == 1) {
                    p = db.prepareStatement("SELECT id FROM Paketit WHERE seurantakoodi=?");
                    p.setString(1, seurantakoodi);
                    r = p.executeQuery();
                    pakettiId = r.getInt("id");
                    break;
                } else {
                    System.out.println("Seurantakoodilla " + seurantakoodi + " ei löydy pakettia. Tarkista koodi.");
                }
            } catch (SQLException e) {
                System.out.println("Tapahtui virhe: " + e.getMessage());
            }
        }
        
        while (true) {
            System.out.print("> Anna tapahtuman paikka: ");
            paikka = syote.nextLine();
            try {
                PreparedStatement p = db.prepareStatement("SELECT COUNT(*) AS lkm FROM Paikat WHERE nimi=?");
                p.setString(1, paikka);
                ResultSet r = p.executeQuery();
                if (r.getInt("lkm") == 1) {
                    p = db.prepareStatement("SELECT id FROM Paikat WHERE nimi=?");
                    p.setString(1, paikka);
                    r = p.executeQuery();
                    paikkaId = r.getInt("id");
                    break;
                } else {
                    System.out.println("Paikkaa " + paikka + " ei löytynyt. Yritä uudestaan.");
                }
            } catch (SQLException e) {
                System.out.println("Tapahtui virhe: " + e.getMessage());
            }
        }
        
        System.out.print("> Anna tapahtuman kuvaus: ");
        kuvaus = syote.nextLine();
               
        long lisayshetki = Instant.now().getEpochSecond();
        
        try {
            PreparedStatement p = db.prepareStatement("INSERT INTO Tapahtumat(kuvaus, lisayshetki, paketti_id, paikka_id) VALUES (?,?,?,?)");
            p.setString(1, kuvaus);
            p.setLong(2, lisayshetki);
            p.setInt(3, pakettiId);
            p.setInt(4, paikkaId);
            p.executeUpdate();
            System.out.println("Tapahtuma lisätty");
        } catch (SQLException e) {
            System.out.println("Tapahtui virhe: " + e.getMessage());
        } finally {
            db.close();
        }
    }
    
    public void haePaketinTapahtumat() throws SQLException {
        String seurantakoodi;
        int pakettiId;
        
        Connection db = DriverManager.getConnection(data);
        
        while (true) {
            System.out.print("> Anna paketin seurantakoodi: ");
            seurantakoodi = syote.nextLine();
            try {
                PreparedStatement p = db.prepareStatement("SELECT id FROM Paketit WHERE seurantakoodi=?");
                p.setString(1, seurantakoodi);
                ResultSet r = p.executeQuery();
                pakettiId = r.getInt("id");
                break;
            } catch (SQLException e) {
                System.out.println("Tapahtui virhe: " + e.getMessage());
            }
        }
        
        try {
            PreparedStatement p = db.prepareStatement("SELECT P.nimi, T.lisayshetki, T.kuvaus FROM Paikat P LEFT JOIN Tapahtumat T ON P.id = T.paikka_id WHERE paketti_id=?");
            p.setInt(1, pakettiId);
            ResultSet r = p.executeQuery();
            
            while (r.next()) {
                Date date = new Date(r.getInt("lisayshetki") * 1000L);
                SimpleDateFormat paivamaara = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                String pvm = paivamaara.format(date);
                System.out.println(pvm + " - " + r.getString("nimi") + " - " + r.getString("kuvaus"));
            }
        } catch (SQLException e) {
            System.out.println("Tapahtui virhe: " + e.getMessage());
        } finally {
            db.close();
        }
    }
    
    public void haeAsiakkaanPaketit() throws SQLException {
        int asiakasId;
        
        Connection db = DriverManager.getConnection(data);
        
        while (true) {
            System.out.print("> Anna asiakkaan nimi: ");
            String asiakas = syote.nextLine();
            try {
                PreparedStatement p = db.prepareStatement("SELECT id FROM Asiakkaat WHERE nimi=?");
                p.setString(1, asiakas);
                ResultSet r = p.executeQuery();
                asiakasId = r.getInt("id");
                break;
            } catch (SQLException e) {
                System.out.println("Tapahtui virhe: " + e.getMessage());
            }
        }
        
        try {
            PreparedStatement p = db.prepareStatement("SELECT P.seurantakoodi, COUNT(T.id) AS lkm FROM Paketit P LEFT JOIN Tapahtumat T ON P.id = T.paikka_id WHERE P.asiakas_id=? GROUP BY P.seurantakoodi");
            p.setInt(1, asiakasId);
            ResultSet r = p.executeQuery();
            
            while (r.next()) {
                System.out.println("Paketti " + r.getString("seurantakoodi") + " - " + r.getString("lkm") + " tapahtumaa");
            }
        } catch (SQLException e) {
            System.out.println("Tapahtui virhe: " + e.getMessage());
        } finally {
            db.close();
        }
    }
    
    public void haePaikanTapahtumat() throws SQLException {
        int paikkaId;
        
        Connection db = DriverManager.getConnection(data);
        
        while (true) {
            System.out.print("> Anna paikan nimi: ");
            String paikka = syote.nextLine();
            try {
                PreparedStatement p = db.prepareStatement("SELECT id FROM Paikat WHERE nimi=?");
                p.setString(1, paikka);
                try (ResultSet r = p.executeQuery()) {
                    paikkaId = r.getInt("id");
                }
                break;
            } catch (SQLException e) {
                System.out.println("Tapahtui virhe: " + e.getMessage());
            }
        }
              
        System.out.print("> Anna päivämäärä (esim. 2.2.2020): ");
        String pvm = syote.nextLine() + "";
        String[] osat = pvm.split("\\.");
        
        int pv = Integer.valueOf(osat[0]);
        int kk = Integer.valueOf(osat[1]);
        int v = Integer.valueOf(osat[2]);
                     
        LocalDateTime alussaSyote = LocalDate.of(v, kk, pv).atStartOfDay();
        Instant alussa = alussaSyote.atZone(ZoneId.of("Europe/Helsinki")).toInstant();
        long alku = alussa.getEpochSecond();
           
        LocalDateTime lopussaSyote = LocalDate.of(v, kk, pv+1).atStartOfDay();
        Instant lopussa = lopussaSyote.atZone(ZoneId.of("Europe/Helsinki")).toInstant();
        long loppu = lopussa.getEpochSecond();

        try {
            PreparedStatement p = db.prepareStatement("SELECT COUNT(*) AS lkm FROM Tapahtumat WHERE paikka_id=? AND lisayshetki>? AND lisayshetki<?");
            p.setInt(1, paikkaId);
            p.setLong(2, alku);
            p.setLong(3, loppu);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    System.out.println("Tapahtumien määrä: " + r.getString("lkm"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Tapahtui virhe: " + e.getMessage());
        } finally {
            db.close();
        }
    }
 
}
