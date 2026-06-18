package studycards.model;

// ============================================================
// Diese Klasse stellt eine einzelne Lernkarte dar.
// Jede Karte hat eine Frage und eine Antwort.
// ============================================================

/**
 * Eine Lernkarte mit Frage und Antwort.
 * Sie gehört immer zu einem Lernset.
 */
public class Lernkarte {

    // ----- Die Felder der Klasse -----
    private int id;         // Die eindeutige Nummer in der Datenbank
    private String frage;   // Die Frage auf der Vorderseite der Karte
    private String antwort; // Die Antwort auf der Rückseite der Karte
    private int lernsetId;  // Zu welchem Lernset gehört diese Karte

    // ----- Konstruktor -----

    /**
     * Erstellt eine neue Lernkarte.
     * @param id        Die Datenbank-ID
     * @param frage     Die Frage
     * @param antwort   Die Antwort
     * @param lernsetId Die ID des zugehörigen Lernsets
     */
    public Lernkarte(int id, String frage, String antwort, int lernsetId)
        this.id = id;               // ID speichern
        this.frage = frage;         // Frage speichern
        this.antwort = antwort;     // Antwort speichern
        this.lernsetId = lernsetId; // Lernset-ID speichern
    }

    // ----- Getter - Methoden zum Auslesen der Felder -----

    /** Gibt die ID zurück */
    public int getId() {
        return id; // ID zurückgeben
    }

    /** Gibt die Frage zurück */
    public String getFrage() {
        return frage; // Frage zurückgeben
    }

    /** Gibt die Antwort zurück */
    public String getAntwort() {
        return antwort; // Antwort zurückgeben
    }

    /** Gibt die Lernset-ID zurück */
    public int getLernsetId() {
        return lernsetId; // Lernset-ID zurückgeben
    }

    // ----- Setter - Methoden zum Ändern der Felder -----

    /** Setzt eine neue Frage */
    public void setFrage(String frage) {
        this.frage = frage; // Neue Frage speichern
    }

    /** Setzt eine neue Antwort */
    public void setAntwort(String antwort) {
        this.antwort = antwort; // Neue Antwort speichern
    }

    /**
     * Gibt die Karte als lesbaren Text aus.
     * Nuetzlich für die ListView-Anzeige.
     * @return Frage und Antwort als Text
     */
    @Override
    public String toString() {
        return "F: " + frage + "  |  A: " + antwort; // Karte als Text ausgeben
    }
}
