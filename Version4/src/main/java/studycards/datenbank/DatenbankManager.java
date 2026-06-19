package studycards.datenbank;

// das hier ist die klasse die alles mit der datenbank macht
// ich benutze mysql als datenbank

import java.sql.Connection;        // das braucht man für die datenbankverbindung
import java.sql.DriverManager;     // damit kann man sich verbinden
import java.sql.PreparedStatement; // das ist für sql befehle mit variablen
import java.sql.ResultSet;         // das sind die ergebnisse aus der datenbank
import java.sql.Statement;         // das ist für einfache sql befehle
import java.util.ArrayList;        // für die listen
import java.util.List;             // das listen-interface
import studycards.model.Lernkarte; // meine lernkarten klasse
import studycards.model.Lernset;   // meine lernset klasse

/**
 * Diese Klasse macht alles was mit der Datenbank zu tun hat.
 * Hier kann man Lernsets und Karten speichern, laden, aendern und löschen.
 */
public class DatenbankManager {

    // -- verbindungsdaten für die datenbank --
    // verbindung zu mysql, muss vorher gestartet sein
    private static final String URL      = "jdbc:mysql://localhost:3306/studycards";
    private static final String BENUTZER = "root"; // mysql benutzername anpassen falls noetig
    private static final String PASSWORT = "";     // mysql passwort hier eintragen

    // die verbindung zur datenbank speichere ich hier
    private Connection verbindung;

    // -------------------------------------------------------
    // VERBINDUNG
    // -------------------------------------------------------

    /**
     * Baut die Verbindung zur Datenbank auf.
     * Muss als erstes aufgerufen werden bevor man irgendwas anderes macht.
     */
    public void verbinden() {
        try {
            // mit drivermanager kann man sich zur datenbank verbinden
            verbindung = DriverManager.getConnection(URL, BENUTZER, PASSWORT);
            System.out.println("Datenbank verbunden!"); // zur Kontrole ausgeben
        } catch (Exception fehler) {
            // wenn was schiefgeht den fehler ausgeben
            System.out.println("Fehler bei der Verbindung: " + fehler.getMessage());
        }
    }

    /**
     * Erstellt die zwei Tabellen die das Programm braucht.
     * Wenn die Tabellen schon da sind passiert nichts.
     */
    public void tabellenErstellen() {
        try {
            Statement stmt = verbindung.createStatement(); // statement objekt erstellen

            // tabelle für die lernsets anlegen
            String sqlSets =
                "CREATE TABLE IF NOT EXISTS lernsets (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " + // automatische id nummer
                "name VARCHAR(200) NOT NULL" +          // der name des sets
                ")";
            stmt.execute(sqlSets);

            // tabelle für die lernkarten anlegen
            String sqlKarten =
                "CREATE TABLE IF NOT EXISTS lernkarten (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +                                // id
                "frage TEXT NOT NULL, " +                                               // die frage
                "antwort TEXT NOT NULL, " +                                             // die antwort
                "lernset_id INT, " +                                                    // welches set
                "FOREIGN KEY (lernset_id) REFERENCES lernsets(id) ON DELETE CASCADE" + // wenn set geloescht wird, karten auch
                ")";
            stmt.execute(sqlKarten);

            System.out.println("Tabellen sind fertig!"); // kontrolle
        } catch (Exception fehler) {
            System.out.println("Fehler beim Erstellen der Tabellen: " + fehler.getMessage());
        }
    }

    // -------------------------------------------------------
    // LERNSET OPERATIONEN
    // -------------------------------------------------------

    /**
     * Lädt alle Lernsets aus der Datenbank und gibt sie als Liste zurück.
     * @return eine liste mit allen lernsets
     */
    public List<Lernset> alleLernseteLaden() {
        List<Lernset> liste = new ArrayList<>(); // leere liste

        try {
            // alle sets aus der datenbank holen, alphabetisch sortiert
            String sql = "SELECT id, name FROM lernsets ORDER BY name";
            Statement stmt     = verbindung.createStatement();
            ResultSet ergebnis = stmt.executeQuery(sql); // anfrage ausfuehren

            // alle zeilen aus dem ergebnis durchgehen
            while (ergebnis.next()) {
                int id      = ergebnis.getInt("id");      // id aus der zeile lesen
                String name = ergebnis.getString("name"); // name aus der zeile lesen
                liste.add(new Lernset(id, name));         // neues objekt erstellen und zur liste
            }
        } catch (Exception fehler) {
            System.out.println("Fehler beim Laden: " + fehler.getMessage());
        }

        return liste; // liste zurückgeben
    }

    /**
     * Speichert ein neues Lernset in der Datenbank.
     * @param name der name des neuen lernsets
     */
    public void lernsetSpeichern(String name) {
        try {
            // ich benutze preparedstatement damit man variablen sicher einsetzen kann
            String sql = "INSERT INTO lernsets (name) VALUES (?)";
            PreparedStatement ps = verbindung.prepareStatement(sql);
            ps.setString(1, name); // das fragezeichen durch den namen ersetzen
            ps.executeUpdate();    // speichern
        } catch (Exception fehler) {
            System.out.println("Fehler beim Speichern: " + fehler.getMessage());
        }
    }

    /**
     * Loescht ein Lernset aus der Datenbank.
     * Die dazugehoerigen Karten werden auch automatisch geloescht.
     * @param id die id des lernsets das geloescht werden soll
     */
    public void lernsetLöschen(int id) {
        try {
            String sql = "DELETE FROM lernsets WHERE id = ?";
            PreparedStatement ps = verbindung.prepareStatement(sql);
            ps.setInt(1, id);   // id einsetzen
            ps.executeUpdate(); // löschen
        } catch (Exception fehler) {
            System.out.println("Fehler beim Loeschen: " + fehler.getMessage());
        }
    }

    /**
     * Ändert den Namen eines Lernsets.
     * @param id        die id des lernsets
     * @param neuerName der neue name
     */
    public void lernsetAktualisieren(int id, String neuerName) {
        try {
            String sql = "UPDATE lernsets SET name = ? WHERE id = ?";
            PreparedStatement ps = verbindung.prepareStatement(sql);
            ps.setString(1, neuerName); // neuen namen einsetzen
            ps.setInt(2, id);           // id einsetzen
            ps.executeUpdate();         // Änderung speichern
        } catch (Exception fehler) {
            System.out.println("Fehler beim Umbenennen: " + fehler.getMessage());
        }
    }

    // -------------------------------------------------------
    // LERNKARTEN OPERATIONEN
    // -------------------------------------------------------

    /**
     * Lädt alle Karten die zu einem bestimmten Lernset gehoeren.
     * @param lernsetId die id des lernsets
     * @return liste mit den karten des sets
     */
    public List<Lernkarte> kartenLadenFürSet(int lernsetId) {
        List<Lernkarte> liste = new ArrayList<>();

        try {
            // nur die karten des angegebenen sets laden
            String sql = "SELECT id, frage, antwort, lernset_id FROM lernkarten WHERE lernset_id = ?";
            PreparedStatement ps = verbindung.prepareStatement(sql);
            ps.setInt(1, lernsetId);        // die set-id einsetzen
            ResultSet ergebnis = ps.executeQuery(); // anfrage ausfuehren

            // alle gefundenen karten durchgehen
            while (ergebnis.next()) {
                int id         = ergebnis.getInt("id");
                String frage   = ergebnis.getString("frage");
                String antwort = ergebnis.getString("antwort");
                int setId      = ergebnis.getInt("lernset_id");
                liste.add(new Lernkarte(id, frage, antwort, setId)); // karte zur liste
            }
        } catch (Exception fehler) {
            System.out.println("Fehler beim Laden der Karten: " + fehler.getMessage());
        }

        return liste;
    }

    /**
     * Speichert eine neue Lernkarte in der Datenbank.
     * @param frage     die frage
     * @param antwort   die antwort
     * @param lernsetId die id des lernsets zu dem die karte gehört
     */
    public void karteSpeichern(String frage, String antwort, int lernsetId) {
        try {
            String sql = "INSERT INTO lernkarten (frage, antwort, lernset_id) VALUES (?, ?, ?)";
            PreparedStatement ps = verbindung.prepareStatement(sql);
            ps.setString(1, frage);   // frage einsetzen
            ps.setString(2, antwort); // antwort einsetzen
            ps.setInt(3, lernsetId);  // set-id einsetzen
            ps.executeUpdate();       // speichern
        } catch (Exception fehler) {
            System.out.println("Fehler beim Speichern der Karte: " + fehler.getMessage());
        }
    }

    /**
     * Loescht eine Lernkarte.
     * @param id die id der karte
     */
    public void karteLöschen(int id) {
        try {
            String sql = "DELETE FROM lernkarten WHERE id = ?";
            PreparedStatement ps = verbindung.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception fehler) {
            System.out.println("Fehler beim Loeschen der Karte: " + fehler.getMessage());
        }
    }

    /**
     * Ändert die Frage und Antwort einer Lernkarte.
     * @param id          die id der karte
     * @param neueFrage   die neue frage
     * @param neueAntwort die neue antwort
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
