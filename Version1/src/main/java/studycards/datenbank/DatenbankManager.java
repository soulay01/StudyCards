package studycards.datenbank;

import java.sql.Conection;            // Verbindung zur Datenbank
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
 * CRUD bedeutet: Erstellen, Lesen, Ändern, Löschen.
 */
public class DatenbankManager {

    // ----- Verbindungsdaten für MySQL -----
    private static final String URL      = "jdbc:mysql://localhost:3306/studycards"; // Adresse
    private static final String BENUTZER = "root";  // MySQL-Benutzername
    private static final String PASSWORT = "";       // MySQL-Passwort (hier leer)

    // Die Datenbankverbindung - wird beim Verbinden gesetzt
    private Conection verbindung; // Die Verbindung zur Datenbank

    // ============================================================
    // VERBINDUNG
    // ============================================================

    /**
     * Stellt die Verbindung zur MySQL-Datenbank her.
     */
    public void verbinden() {
        try {
            // Wir versuchen uns mit der Datenbank zu verbinden
            verbindung = DriverManager.getConnection(URL, BENUTZER, PASSWORT);
            System.out.println("Datenbank erfolgreich verbunden!"); // Erfolgsmeldung
        } catch (Exception fehler) {
            // Falls etwas schief laeuft, zeigen wir den Fehler an
            System.out.println("Fehler beim Verbinden: " + fehler.getMessage());
        }
    }

    /**
     * Erstellt die Datenbanktabellen wenn sie noch nicht existieren.
     */
    public void tabellenErstellen() {
        try {
            Statement stmt = verbindung.createStatement(); // Statement erstellen

            // Tabelle für die Lernsets anlegen
            String sqlSets =
                "CREATE TABLE IF NOT EXISTS lernsets (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +  // Automatische ID
                "name VARCHAR(200) NOT NULL" +            // Name des Sets
                ")";
            stmt.execute(sqlSets); // SQL ausführen

            // Tabelle für die Lernkarten anlegen
            String sqlKarten =
                "CREATE TABLE IF NOT EXISTS lernkarten (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +                               // Auto-ID
                "frage TEXT NOT NULL, " +                                              // Die Frage
                "antwort TEXT NOT NULL, " +                                            // Die Antwort
                "lernset_id INT, " +                                                   // Zugehöriges Set
                "FOREIGN KEY (lernset_id) REFERENCES lernsets(id) ON DELETE CASCADE"  // Verknuepfung
                + ")";
            stmt.execute(sqlKarten); // SQL ausführen

            System.out.println("Tabellen sind bereit!"); // Erfolgsmeldung
        } catch (Exception fehler) {
            System.out.println("Fehler beim Erstellen der Tabellen: " + fehler.getMessage());
        }
    }

    // ============================================================
    // LERNSET OPERATIONEN (Erstellen, Lesen, Ändern, Löschen)
    // ============================================================

    /**
     * Lädt alle Lernsets aus der Datenbank.
     * @return Eine Liste mit allen Lernsets
     */
    public List<Lernset> alleLernseteLaden() {
        List<Lernset> liste = new ArrayList<>(); // Leere Liste vorbereiten

        try {
            // SQL: Alle Sets alphabetisch sortiert holen
            String sql = "SELECT id, name FROM lernsets ORDER BY name";
            Statement stmt = verbindung.createStatement();  // Statement erstellen
            ResultSet ergebnis = stmt.executeQuery(sql);    // Anfrage ausführen

            // Alle Zeilen aus der Datenbank durchgehen
            while (ergebnis.next()) {
                int id        = ergebnis.getInt("id");           // ID lesen
                String name   = ergebnis.getString("name");      // Name lesen
                Lernset neuesSet = new Lernset(id, name);        // Objekt erstellen
                liste.add(neuesSet);                             // Zur Liste hinzufügen
            }
        } catch (Exception fehler) {
            System.out.println("Fehler beim Laden der Lernsets: " + fehler.getMessage());
        }

        return liste; // Liste mit allen Sets zurückgeben
    }

    /**
     * Speichert ein neues Lernset in der Datenbank.
     * @param name Der Name des neuen Lernsets
     */
    public void lernsetSpeichern(String name) {
        try {
            // PreparedStatement verhindert SQL-Injection Angriffe
            String sql = "INSERT INTO lernsets (name) VALUES (?)";
            PreparedStatement ps = verbindung.prepareStatement(sql); // Vorbereiten
            ps.setString(1, name); // Den Namen an Stelle 1 einsetzen
            ps.executeUpdate();    // Ausführen und speichern
            System.out.println("Lernset gespeichert: " + name);
        } catch (Exception fehler) {
            System.out.println("Fehler beim Speichern: " + fehler.getMessage());
        }
    }

    /**
     * Löscht ein Lernset aus der Datenbank.
     * Alle Karten darin werden auch gelöscht (CASCADE).
     * @param id Die ID des zu löschenden Sets
     */
    public void lernsetLöschen(int id) {
        try {
            String sql = "DELETE FROM lernsets WHERE id = ?"; // SQL zum Löschen
            PreparedStatement ps = verbindung.prepareStatement(sql);
            ps.setInt(1, id); // Die ID einsetzen
            ps.executeUpdate(); // Löschen ausführen
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
            ps.setString(1, neuerName); // Neuen Namen setzen
            ps.setInt(2, id);           // ID setzen
            ps.executeUpdate();         // Ausführen
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
     * @return Liste mit den Lernkarten
     */
    public List<Lernkarte> kartenLadenFürSet(int lernsetId) {
        List<Lernkarte> liste = new ArrayList<>(); // Leere Liste

        try {
            // Nur Karten für das angegebene Set holen
            String sql = "SELECT id, frage, antwort, lernset_id FROM lernkarten WHERE lernset_id = ?";
            PreparedStatement ps = verbindung.prepareStatement(sql);
            ps.setInt(1, lernsetId);       // Lernset-ID einsetzen
            ResultSet ergebnis = ps.executeQuery(); // Anfrage ausführen

            // Alle gefundenen Karten verarbeiten
            while (ergebnis.next()) {
                int id         = ergebnis.getInt("id");           // ID lesen
                String frage   = ergebnis.getString("frage");     // Frage lesen
                String antwort = ergebnis.getString("antwort");   // Antwort lesen
                int setId      = ergebnis.getInt("lernset_id");   // Set-ID lesen

                Lernkarte karte = new Lernkarte(id, frage, antwort, setId); // Objekt bauen
                liste.add(karte); // Zur Liste hinzufügen
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
            PreparedStatement ps = verbindung.prepareStatement(sql);
            ps.setString(1, frage);   // Frage einsetzen
            ps.setString(2, antwort); // Antwort einsetzen
            ps.setInt(3, lernsetId);  // Lernset-ID einsetzen
            ps.executeUpdate();       // Speichern
        } catch (Exception fehler) {
            System.out.println("Fehler beim Speichern der Karte: " + fehler.getMessage());
        }
    }

    /**
     * Löscht eine Lernkarte aus der Datenbank.
     * @param id Die ID der Karte
     */
    public void karteLöschen(int id) {
        try {
            String sql = "DELETE FROM lernkarten WHERE id = ?";
            PreparedStatement ps = verbindung.prepareStatement(sql);
            ps.setInt(1, id);   // ID einsetzen
            ps.executeUpdate(); // Löschen ausführen
        } catch (Exception fehler) {
            System.out.println("Fehler beim Löschen der Karte: " + fehler.getMessage());
        }
    }

    /**
     * Ändert Frage und Antwort einer bestehenden Karte.
     * @param id          Die Karten-ID
     * @param neueFrage   Die neue Frage
     * @param neueAntwort Die neue Antwort
     */
    public void karteAktualisieren(int id, String neueFrage, String neueAntwort) {
        try {
            String sql = "UPDATE lernkarten SET frage = ?, antwort = ? WHERE id = ?";
            PreparedStatement ps = verbindung.prepareStatement(sql);
            ps.setString(1, neueFrage);   // Neue Frage
            ps.setString(2, neueAntwort); // Neue Antwort
            ps.setInt(3, id);             // Karten-ID
            ps.executeUpdate();           // Ausführen
        } catch (Exception fehler) {
            System.out.println("Fehler beim Aktualisieren der Karte: " + fehler.getMessage());
        }
    }
}
