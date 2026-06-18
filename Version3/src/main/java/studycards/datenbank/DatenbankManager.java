package studycards.datenbank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import studycards.model.Lernkarte;
import studycards.model.Lernset;

/**
 * Der DatenbankManager verbindet sich mit MySQL und führt alle Datenbankoperationen aus.
 */
public class DatenbankManager {

    // Verbindungsdaten
    private static final String URL      = "jdbc:mysql://localhost:3306/studycards";
    private static final String BENUTZER = "root";
    private static final String PASSWORT = "";

    private Connection verbindung; // Die Datenbankverbindung

    /**
     * Stellt die Verbindung zur Datenbank her.
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
     * Erstellt die Datenbanktabellen wenn noch nicht vorhanden.
     */
    public void tabellenErstellen() {
        try {
            Statement stmt = verbindung.createStatement();

            String sqlSets =
                "CREATE TABLE IF NOT EXISTS lernsets (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(200) NOT NULL" +
                ")";
            stmt.execute(sqlSets);

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
            System.out.println("Fehler: " + fehler.getMessage());
        }
    }

    // ============================================================
    // LERNSET OPERATIONEN
    // ============================================================

    /**
     * Lädt alle Lernsets aus der Datenbank.
     * @return Liste mit allen Lernsets
     */
    public List<Lernset> alleLernseteLaden() {
        List<Lernset> liste = new ArrayList<>();

        try {
            String sql = "SELECT id, name FROM lernsets ORDER BY name";
            Statement stmt = verbindung.createStatement();
            ResultSet ergebnis = stmt.executeQuery(sql); // Anfrage ausführen

            while (ergebnis.next()) {
                int id      = ergebnis.getInt("id");
                String name = ergebnis.getString("name");
                liste.add(new Lernset(id, name));
            }
        } catch (Exception fehler) {
            System.out.println("Fehler beim Laden: " + fehler.getMessage());
        }

        return liste;
    }

    /**
     * Speichert ein neues Lernset.
     * @param name Der Name des Sets
     */
    public void lernsetSpeichern(String name) {
        try {
            String sql = "INSERT INTO lernsets (name) VALUES (?)";
            PreparedStatement ps = verbindung.preparedStatement(sql); // Statement vorbereiten
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (Exception fehler) {
            System.out.println("Fehler beim Speichern: " + fehler.getMessage());
        }
    }

    /**
     * Löscht ein Lernset.
     * @param id Die ID des Sets
     */
    public void lernsetLöschen(int id) {
        try {
            String sql = "DELETE FROM lernsets WHERE id = ?";
            PreparedStatement ps = verbindung.preparedStatement(sql); // Statement vorbereiten
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception fehler) {
            System.out.println("Fehler beim Löschen: " + fehler.getMessage());
        }
    }

    /**
     * Ändert den Namen eines Lernsets.
     * @param id        Die ID
     * @param neuerName Der neue Name
     */
    public void lernsetAktualisieren(int id, String neuerName) {
        try {
            String sql = "UPDATE lernsets SET name = ? WHERE id = ?";
            PreparedStatement ps = verbindung.preparedStatement(sql); // Statement vorbereiten
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
     * Lädt alle Karten eines Lernsets.
     * @param lernsetId Die Set-ID
     * @return Liste der Karten
     */
    public List<Lernkarte> kartenLadenFürSet(int lernsetId) {
        List<Lernkarte> liste = new ArrayList<>();

        try {
            String sql = "SELECT id, frage, antwort, lernset_id FROM lernkarten WHERE lernset_id = ?";
            PreparedStatement ps = verbindung.preparedStatement(sql); // Statement vorbereiten
            ps.setInt(1, lernsetId);
            ResultSet ergebnis = ps.executeQuery();

            while (ergebnis.next()) {
                int id         = ergebnis.getInt("id");
                String frage   = ergebnis.getString("frage");
                String antwort = ergebnis.getString("antwort");
                int setId      = ergebnis.getInt("lernset_id");
                liste.add(new Lernkarte(id, frage, antwort, setId));
            }
        } catch (Exception fehler) {
            System.out.println("Fehler beim Laden der Karten: " + fehler.getMessage());
        }

        return liste;
    }

    /**
     * Speichert eine neue Karte.
     * @param frage     Die Frage
     * @param antwort   Die Antwort
     * @param lernsetId Die Set-ID
     */
    public void karteSpeichern(String frage, String antwort, int lernsetId) {
        try {
            String sql = "INSERT INTO lernkarten (frage, antwort, lernset_id) VALUES (?, ?, ?)";
            PreparedStatement ps = verbindung.preparedStatement(sql); // Statement vorbereiten
            ps.setString(1, frage);
            ps.setString(2, antwort);
            ps.setInt(3, lernsetId);
            ps.executeUpdate();
        } catch (Exception fehler) {
            System.out.println("Fehler beim Speichern: " + fehler.getMessage());
        }
    }

    /**
     * Löscht eine Karte.
     * @param id Die Karten-ID
     */
    public void karteLöschen(int id) {
        try {
            String sql = "DELETE FROM lernkarten WHERE id = ?";
            PreparedStatement ps = verbindung.preparedStatement(sql); // Statement vorbereiten
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception fehler) {
            System.out.println("Fehler beim Löschen: " + fehler.getMessage());
        }
    }

    /**
     * Ändert eine Karte.
     * @param id          Die Karten-ID
     * @param neueFrage   Neue Frage
     * @param neueAntwort Neue Antwort
     */
    public void karteAktualisieren(int id, String neueFrage, String neueAntwort) {
        try {
            String sql = "UPDATE lernkarten SET frage = ?, antwort = ? WHERE id = ?";
            PreparedStatement ps = verbindung.preparedStatement(sql); // Statement vorbereiten
            ps.setString(1, neueFrage);
            ps.setString(2, neueAntwort);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (Exception fehler) {
            System.out.println("Fehler beim Aktualisieren: " + fehler.getMessage());
        }
    }
}
