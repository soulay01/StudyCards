package studycards.datenbank;

import java.sql.Connection;           // Datenbankverbindung
import java.sql.DriverManager;        // Hilfsklasse für die Verbindung
import java.sql.PreparedStatement;    // Für sichere SQL-Anfragen
import java.sql.ResultSet;            // Für die Ergebnisse aus der DB
import java.sql.Statement;            // Für normale SQL-Anfragen
import java.util.ArrayList;           // Für die Listen
import java.util.List;                // Das Listen-Interface
import studycards.model.Lernkarte;   // Unsere Lernkarten-Klasse
import studycards.model.Lernset;     // Unsere Lernset-Klasse

/**
 * Der DatenbankManager kuemmert sich um alles was mit MySQL zu tun hat.
 * Er verbindet sich mit der Datenbank und führt CRUD-Operationen aus.
 */
public class DatenbankManager {

    // ----- Verbindungsdaten für MySQL -----
    private static final String URL      = "jdbc:mysql://localhost:3306/studycards";
    private static final String BENUTZER = "root"; // MySQL-Benutzername
    private static final String PASSWORT = "";      // MySQL-Passwort

    // Die Datenbankverbindung
    private Connection verbindung; // Jetzt richtig: Connection

    // ============================================================
    // VERBINDUNG
    // ============================================================

    /**
     * Stellt die Verbindung zur MySQL-Datenbank her.
     */
    public void verbinden() {
        try {
            verbindung = DriverManager.getConnection(URL, BENUTZER, PASSWORT);
            System.out.println("Datenbank erfolgreich verbunden!");
        } catch (Exception fehler) {
            System.out.println("Fehler beim Verbinden: " + fehler.getMessage());
        }
    }

    /**
     * Erstellt die Datenbanktabellen wenn sie noch nicht existieren.
     */
    public void tabellenErstellen() {
        try {
            Statement stmt = verbindung.createStatement(); // Statement erstellen

            // Tabelle für Lernsets
            String sqlSets =
                "CREATE TABLE IF NOT EXISTS lernsets (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(200) NOT NULL" +
                ")";
            stmt.execute(sqlSets);

            // Tabelle für Lernkarten
            String sqlKarten =
                "CREATE TABLE IF NOT EXISTS lernkarten (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "frage TEXT NOT NULL, " +
                "antwort TEXT NOT NULL, " +
                "lernset_id INT, " +
                "FOREIGN KEY (lernset_id) REFERENCES lernsets(id) ON DELETE CASCADE" +
                ")";
            stmt.execute(sqlKarten);

            System.out.println("Tabellen sind bereit!");
        } catch (Exception fehler) {
            System.out.println("Fehler beim Erstellen der Tabellen: " + fehler.getMessage());
        }
    }

    // ============================================================
    // LERNSET OPERATIONEN
    // ============================================================

    /**
     * Lädt alle Lernsets aus der Datenbank.
     * @return Eine Liste mit allen Lernsets
     */
    public List<Lernset> alleLernseteLaden() {
        List<Lernset> liste = new ArrayList<>(); // Leere Liste vorbereiten

        try {
            String sql = "SELECT id, name FROM lernsets ORDER BY name";
            Statement stmt = verbindung.createStatement(); // Statement erstellen

            ResultSet ergebnis = stmt.executeUpdate(sql); // Anfrage ausführen

            while (ergebnis.next()) {
                int id      = ergebnis.getInt("id");
                String name = ergebnis.getString("name");
                Lernset neuesSet = new Lernset(id, name);
                liste.add(neuesSet);
            }
        } catch (Exception fehler) {
            System.out.println("Fehler beim Laden der Lernsets: " + fehler.getMessage());
        }

        return liste;
    }

    /**
     * Speichert ein neues Lernset in der Datenbank.
     * @param name Der Name des neuen Lernsets
     */
    public void lernsetSpeichern(String name) {
        try {
            String sql = "INSERT INTO lernsets (name) VALUES (?)";
            PreparedStatement ps = verbindung.prepareStatement(sql);
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (Exception fehler) {
            System.out.println("Fehler beim Speichern: " + fehler.getMessage());
        }
    }

    /**
     * Löscht ein Lernset aus der Datenbank.
     * @param id Die ID des Sets
     */
    public void lernsetLöschen(int id) {
        try {
            String sql = "DELETE FROM lernsets WHERE id = ?";
            PreparedStatement ps = verbindung.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception fehler) {
            System.out.println("Fehler beim Löschen: " + fehler.getMessage());
        }
    }

    /**
     * Ändert den Namen eines Lernsets.
     * @param id        Die ID des Sets
     * @param neuerName Der neue Name
     */
    public void lernsetAktualisieren(int id, String neuerName) {
        try {
            String sql = "UPDATE lernsets SET name = ? WHERE id = ?";
            PreparedStatement ps = verbindung.prepareStatement(sql);
            ps.setString(1, neuerName);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception fehler) {
            System.out.println("Fehler beim Aktualisieren: " + fehler.getMessage());
        }
    }

    // ============================================================
    // LERNKARTEN OPERATIONEN
    // ============================================================

    /**
     * Lädt alle Karten die zu einem Lernset gehören.
     * @param lernsetId Die ID des Lernsets
     * @return Liste mit den Lernkarten
     */
    public List<Lernkarte> kartenLadenFürSet(int lernsetId) {
        List<Lernkarte> liste = new ArrayList<>();

        try {
            String sql = "SELECT id, frage, antwort, lernset_id FROM lernkarten WHERE lernset_id = ?";
            PreparedStatement ps = verbindung.prepareStatement(sql);
            ps.setInt(1, lernsetId);
            ResultSet ergebnis = ps.executeQuery();

            while (ergebnis.next()) {
                int id         = ergebnis.getInt("id");
                String frage   = ergebnis.getString("frage");
                String antwort = ergebnis.getString("antwort");
                int setId      = ergebnis.getInt("lernset_id");
                Lernkarte karte = new Lernkarte(id, frage, antwort, setId);
                liste.add(karte);
            }
        } catch (Exception fehler) {
            System.out.println("Fehler beim Laden der Karten: " + fehler.getMessage());
        }

        return liste;
    }

    /**
     * Speichert eine neue Lernkarte.
     * @param frage     Die Frage
     * @param antwort   Die Antwort
     * @param lernsetId Die Set-ID
     */
    public void karteSpeichern(String frage, String antwort, int lernsetId) {
        try {
            String sql = "INSERT INTO lernkarten (frage, antwort, lernset_id) VALUES (?, ?, ?)";
            PreparedStatement ps = verbindung.prepareStatement(sql);
            ps.setString(1, frage);
            ps.setString(2, antwort);
            ps.setInt(3, lernsetId);
            ps.executeUpdate();
        } catch (Exception fehler) {
            System.out.println("Fehler beim Speichern der Karte: " + fehler.getMessage());
        }
    }

    /**
     * Löscht eine Lernkarte.
     * @param id Die ID der Karte
     */
    public void karteLöschen(int id) {
        try {
            String sql = "DELETE FROM lernkarten WHERE id = ?";
            PreparedStatement ps = verbindung.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception fehler) {
            System.out.println("Fehler beim Löschen der Karte: " + fehler.getMessage());
        }
    }

    /**
     * Ändert Frage und Antwort einer Karte.
     * @param id          Die Karten-ID
     * @param neueFrage   Neue Frage
     * @param neueAntwort Neue Antwort
     */
    public void karteAktualisieren(int id, String neueFrage, String neueAntwort) {
        try {
            String sql = "UPDATE lernkarten SET frage = ?, antwort = ? WHERE id = ?";
            PreparedStatement ps = verbindung.prepareStatement(sql);
            ps.setString(1, neueFrage);
            ps.setString(2, neueAntwort);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (Exception fehler) {
            System.out.println("Fehler beim Aktualisieren der Karte: " + fehler.getMessage());
        }
    }
}
