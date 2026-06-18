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
 * Hier sind alle Methoden für das Erstellen, Lesen, Ändern und Löschen (CRUD).
 */
public class DatenbankManager {

    // ----- Verbindungsdaten für H2 (eingebettete Datenbank, kein Server nötig) -----
    private static final String URL      = "jdbc:h2:./studycards_db;MODE=MySQL"; // Datei im Projektordner
    private static final String BENUTZER = "sa";   // Standard-Benutzername für H2
    private static final String PASSWORT = "";      // Kein Passwort nötig

    // Die Datenbankverbindung - wird in verbinden() gesetzt
    private Connection verbindung;

    // ============================================================
    // VERBINDUNG HERSTELLEN
    // ============================================================

    /**
     * Stellt die Verbindung zur MySQL-Datenbank her.
     * Muss vor allen anderen Methoden aufgerufen werden.
     */
    public void verbinden() {
        try {
            // Mit DriverManager verbinden wir uns zur Datenbank
            verbindung = DriverManager.getConnection(URL, BENUTZER, PASSWORT);
            System.out.println("Datenbank erfolgreich verbunden!"); // Erfolgsmeldung
        } catch (Exception fehler) {
            // Wenn die Verbindung fehlschlägt, Fehler anzeigen
            System.out.println("Fehler beim Verbinden: " + fehler.getMessage());
        }
    }

    /**
     * Erstellt die Datenbanktabellen - aber nur wenn sie noch nicht existieren.
     * Kann beliebig oft aufgerufen werden ohne Probleme.
     */
    public void tabellenErstellen() {
        try {
            Statement stmt = verbindung.createStatement(); // Statement-Objekt erstellen

            // Tabelle für die Lernsets erstellen
            String sqlSets =
                "CREATE TABLE IF NOT EXISTS lernsets (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +  // Automatische eindeutige Nummer
                "name VARCHAR(200) NOT NULL" +            // Der Name des Lernsets
                ")";
            stmt.execute(sqlSets); // SQL-Befehl ausführen

            // Tabelle für die Lernkarten erstellen
            String sqlKarten =
                "CREATE TABLE IF NOT EXISTS lernkarten (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +                               // Auto-ID
                "frage TEXT NOT NULL, " +                                              // Die Frage
                "antwort TEXT NOT NULL, " +                                            // Die Antwort
                "lernset_id INT, " +                                                   // Welches Set
                "FOREIGN KEY (lernset_id) REFERENCES lernsets(id) ON DELETE CASCADE" +// Wenn Set gelöscht -> Karte auch
                ")";
            stmt.execute(sqlKarten); // SQL-Befehl ausführen

            System.out.println("Tabellen sind bereit!"); // Erfolgsmeldung
        } catch (Exception fehler) {
            System.out.println("Fehler beim Erstellen der Tabellen: " + fehler.getMessage());
        }
    }

    // ============================================================
    // LERNSET OPERATIONEN (Erstellen, Lesen, Ändern, Löschen)
    // ============================================================

    /**
     * Lädt alle Lernsets aus der Datenbank und gibt sie als Liste zurück.
     * @return Liste mit allen Lernsets (kann leer sein)
     */
    public List<Lernset> alleLernseteLaden() {
        List<Lernset> liste = new ArrayList<>(); // Leere Liste vorbereiten

        try {
            // SQL-Anfrage: Alle Sets alphabetisch holen
            String sql = "SELECT id, name FROM lernsets ORDER BY name";
            Statement stmt      = verbindung.createStatement(); // Statement erstellen
            ResultSet ergebnis  = stmt.executeQuery(sql);       // Anfrage ausführen

            // Alle Zeilen aus der Datenbank durchgehen
            while (ergebnis.next()) {
                int id      = ergebnis.getInt("id");           // ID aus der Zeile lesen
                String name = ergebnis.getString("name");      // Name aus der Zeile lesen
                liste.add(new Lernset(id, name));              // Objekt erstellen und zur Liste
            }
        } catch (Exception fehler) {
            System.out.println("Fehler beim Laden: " + fehler.getMessage());
        }

        return liste; // Liste zurückgeben
    }

    /**
     * Speichert ein neues Lernset in der Datenbank.
     * @param name Der Name des neuen Lernsets
     */
    public void lernsetSpeichern(String name) {
        try {
            String sql = "INSERT INTO lernsets (name) VALUES (?)";
            PreparedStatement ps = verbindung.prepareStatement(sql); // Richtig: prepareStatement
            ps.setString(1, name); // Den Namen an Stelle 1 (das erste ?) einsetzen
            ps.executeUpdate();    // Den INSERT-Befehl ausführen
        } catch (Exception fehler) {
            System.out.println("Fehler beim Speichern: " + fehler.getMessage());
        }
    }

    /**
     * Löscht ein Lernset aus der Datenbank.
     * Alle zugehörigen Karten werden automatisch mit gelöscht (CASCADE).
     * @param id Die ID des zu löschenden Sets
     */
    public void lernsetLöschen(int id) {
        try {
            String sql = "DELETE FROM lernsets WHERE id = ?";
            PreparedStatement ps = verbindung.prepareStatement(sql); // Richtig: prepareStatement
            ps.setInt(1, id); // Die ID einsetzen
            ps.executeUpdate(); // DELETE ausführen
        } catch (Exception fehler) {
            System.out.println("Fehler beim Löschen: " + fehler.getMessage());
        }
    }

    /**
     * Ändert den Namen eines bestehenden Lernsets.
     * @param id        Die ID des Sets
     * @param neuerName Der neue Name
     */
    public void lernsetAktualisieren(int id, String neuerName) {
        try {
            String sql = "UPDATE lernsets SET name = ? WHERE id = ?";
            PreparedStatement ps = verbindung.prepareStatement(sql); // Richtig: prepareStatement
            ps.setString(1, neuerName); // Neuen Namen einsetzen
            ps.setInt(2, id);           // ID einsetzen
            ps.executeUpdate();         // UPDATE ausführen
        } catch (Exception fehler) {
            System.out.println("Fehler beim Aktualisieren: " + fehler.getMessage());
        }
    }

    // ============================================================
    // LERNKARTEN OPERATIONEN
    // ============================================================

    /**
     * Lädt alle Karten die zu einem bestimmten Lernset gehören.
     * @param lernsetId Die ID des Lernsets
     * @return Liste mit den Lernkarten des Sets
     */
    public List<Lernkarte> kartenLadenFürSet(int lernsetId) {
        List<Lernkarte> liste = new ArrayList<>(); // Leere Liste

        try {
            // Nur Karten für das angegebene Set laden
            String sql = "SELECT id, frage, antwort, lernset_id FROM lernkarten WHERE lernset_id = ?";
            PreparedStatement ps = verbindung.prepareStatement(sql); // Richtig: prepareStatement
            ps.setInt(1, lernsetId);        // Lernset-ID einsetzen
            ResultSet ergebnis = ps.executeQuery(); // SELECT ausführen

            // Alle gefundenen Karten verarbeiten
            while (ergebnis.next()) {
                int id         = ergebnis.getInt("id");           // ID lesen
                String frage   = ergebnis.getString("frage");     // Frage lesen
                String antwort = ergebnis.getString("antwort");   // Antwort lesen
                int setId      = ergebnis.getInt("lernset_id");   // Set-ID lesen
                liste.add(new Lernkarte(id, frage, antwort, setId)); // Objekt erstellen
            }
        } catch (Exception fehler) {
            System.out.println("Fehler beim Laden der Karten: " + fehler.getMessage());
        }

        return liste; // Liste zurückgeben
    }

    /**
     * Speichert eine neue Lernkarte in der Datenbank.
     * @param frage     Die Frage der Karte
     * @param antwort   Die Antwort der Karte
     * @param lernsetId Die ID des zugehörigen Sets
     */
    public void karteSpeichern(String frage, String antwort, int lernsetId) {
        try {
            String sql = "INSERT INTO lernkarten (frage, antwort, lernset_id) VALUES (?, ?, ?)";
            PreparedStatement ps = verbindung.prepareStatement(sql); // Richtig: prepareStatement
            ps.setString(1, frage);   // Frage einsetzen
            ps.setString(2, antwort); // Antwort einsetzen
            ps.setInt(3, lernsetId);  // Lernset-ID einsetzen
            ps.executeUpdate();       // INSERT ausführen
        } catch (Exception fehler) {
            System.out.println("Fehler beim Speichern der Karte: " + fehler.getMessage());
        }
    }

    /**
     * Löscht eine Lernkarte aus der Datenbank.
     * @param id Die ID der zu löschenden Karte
     */
    public void karteLöschen(int id) {
        try {
            String sql = "DELETE FROM lernkarten WHERE id = ?";
            PreparedStatement ps = verbindung.prepareStatement(sql); // Richtig: prepareStatement
            ps.setInt(1, id);   // ID einsetzen
            ps.executeUpdate(); // DELETE ausführen
        } catch (Exception fehler) {
            System.out.println("Fehler beim Löschen der Karte: " + fehler.getMessage());
        }
    }

    /**
     * Ändert Frage und Antwort einer bestehenden Karte.
     * @param id          Die ID der Karte
     * @param neueFrage   Die neue Frage
     * @param neueAntwort Die neue Antwort
     */
    public void karteAktualisieren(int id, String neueFrage, String neueAntwort) {
        try {
            String sql = "UPDATE lernkarten SET frage = ?, antwort = ? WHERE id = ?";
            PreparedStatement ps = verbindung.prepareStatement(sql); // Richtig: prepareStatement
            ps.setString(1, neueFrage);   // Neue Frage einsetzen
            ps.setString(2, neueAntwort); // Neue Antwort einsetzen
            ps.setInt(3, id);             // Karten-ID einsetzen
            ps.executeUpdate();           // UPDATE ausführen
        } catch (Exception fehler) {
            System.out.println("Fehler beim Aktualisieren der Karte: " + fehler.getMessage());
        }
    }
}
