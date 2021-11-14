package pakettiseuranta;

import java.sql.*;
import java.util.Random;

public class Tehokkuustesti {
    private final Connection db;
    
    public Tehokkuustesti() throws SQLException {
        this.db = DriverManager.getConnection("jdbc:sqlite:data.db");
    }
    
    public void suoritaTehokkuustesti() {
        PreparedStatement p;
        Random r = new Random(1996);
               
        try {
            db.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println("Tapahtui virhe: " + e.getMessage());
        }
        
        try {
            long alku = System.nanoTime();
            
            for (int i = 1; i <= 1000; i++) {
                p = db.prepareStatement("INSERT INTO Paikat(nimi) VALUES (?)");
                p.setString(1, "P"+i);
                p.executeUpdate();
            }
            
            db.commit();
            long loppu = System.nanoTime();
            System.out.println("Testi 1, aikaa kului " + ((loppu-alku)/1e9) + " s");
        } catch (SQLException e) {
            System.out.println("Tapahtui virhe: " + e.getMessage());
        }
        
        try {
            long alku = System.nanoTime();
            
            for (int i = 1; i <= 1000; i++) {
                p = db.prepareStatement("INSERT INTO Asiakkaat(nimi) VALUES (?)");
                p.setString(1, "A"+i);
                p.executeUpdate();
            }
            
            db.commit();
            long loppu = System.nanoTime();
            System.out.println("Testi 2, aikaa kului " + ((loppu-alku)/1e9) + " s");
        } catch (SQLException e) {
            System.out.println("Tapahtui virhe: " + e.getMessage());
        }
        
        try {
            long alku = System.nanoTime();
            
            for (int i = 1; i <= 1000; i++) {
                p = db.prepareStatement("INSERT INTO Paketit(seurantakoodi, asiakas_id) VALUES (?,?)");
                p.setString(1, "S"+i);
                p.setString(2, String.valueOf(r.nextInt(1000)+1));
                p.executeUpdate();
            }
            
            db.commit();
            long loppu = System.nanoTime();
            System.out.println("Testi 3, aikaa kului " + ((loppu-alku)/1e9) + " s");
        } catch (SQLException e) {
            System.out.println("Tapahtui virhe: " + e.getMessage());
        }
        
        try {
            long alku = System.nanoTime();
                        
            for (int i = 1; i <= Math.pow(10, 6); i++) {
                p = db.prepareStatement("INSERT INTO Tapahtumat(paketti_id) VALUES (?)");
                p.setString(1, String.valueOf(r.nextInt(1000)+1));
                p.executeUpdate();
            }
            
            db.commit();
            long loppu = System.nanoTime();
            System.out.println("Testi 4, aikaa kului " + ((loppu-alku)/1e9) + " s");
        } catch (SQLException e) {
            System.out.println("Tapahtui virhe: " + e.getMessage());
        }
        
        try {
            db.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println("Tapahtui virhe: " + e.getMessage());
        }
        
        try {
            long alku = System.nanoTime();
            
            for (int i = 1; i <= 1000; i++) {
                p = db.prepareStatement("SELECT COUNT(id) FROM Paketit WHERE asiakas_id=?");
                p.setString(1, String.valueOf(r.nextInt(1000)+1));
                p.execute();
            }
            
            long loppu = System.nanoTime();
            System.out.println("Testi 5, aikaa kului " + ((loppu-alku)/1e9) + " s");
        } catch (SQLException e) {
            System.out.println("Tapahtui virhe: " + e.getMessage());
        }
        
        try {
            long alku = System.nanoTime();
            
            for (int i = 1; i <= 1000; i++) {
                p = db.prepareStatement("SELECT COUNT(id) FROM Tapahtumat WHERE paketti_id=?");
                p.setString(1, String.valueOf(r.nextInt(1000)+1));
                p.execute();
            }
            
            long loppu = System.nanoTime();
            System.out.println("Testi 6, aikaa kului " + ((loppu-alku)/1e9) + " s");
        } catch (SQLException e) {
            System.out.println("Tapahtui virhe: " + e.getMessage());
        }
    }
}
