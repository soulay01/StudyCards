package studycards.model;

/**
 * Das hier ist eine Lernkarte.
 * Eine Karte hat eine Frage und eine Antwort und gehört zu einem Lernset.
 */
public class Lernkarte {

    // das sind alle informationen die eine lernkarte hat
    private int id;         // die nummer in der datenbank
    private String frage;   // die frage auf der karte
    private String antwort; // die antwort auf die frage
    private int lernsetId;  // zu welchem lernset gehört die karte

    /**
     * Erstellt eine neue Lernkarte mit allen Infos.
     * @param id        die nummer aus der datenbank
     * @param frage     die frage
     * @param antwort   die antwort
     * @param lernsetId die id des lernsets zu dem die karte gehört
     */
    public Lernkarte(int id, String frage, String antwort, int lernsetId) {
        // alles in die variablen speichern
        this.id = id;
        this.frage = frage;
        this.antwort = antwort;
        this.lernsetId = lernsetId;
    }

    // -- getter methoden, die geben die werte zurück --

    /** Gibt die ID zurück */
    public int getId() {
        return id;
    }

    /** Gibt die Frage zurück */
    public String getFrage() {
        return frage;
    }

    /** Gibt die Antwort zurück */
    public String getAntwort() {
        return antwort;
    }

    /** Gibt die Lernset-ID zurück */
    public int getLernsetId() {
        return lernsetId;
    }

    // -- setter methoden, die aendern die werte --

    /** Setzt eine neue Frage */
    public void setFrage(String frage) {
        this.frage = frage; // neue frage speichern
    }

    /** Setzt eine neue Antwort */
    public void setAntwort(String antwort) {
        this.antwort = antwort; // neue antwort speichern
    }

    /**
     * Das hier brauche ich damit die karte in der liste als text angezeigt wird.
     * @return frage und antwort als text
     */
    @Override
    public String toString() {
        return "F: " + frage + "  |  A: " + antwort; // so sieht es in der liste aus
    }
}
