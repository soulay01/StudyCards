package studycards.model;

// ============================================================
// Der falsche Generic-Typ "List<Lernset>" wurde zu "List<Lernkarte>" korrigiert.
// In Version 3 gibt es in dieser Datei keine neuen Fehler.
// ============================================================

import java.util.ArrayList; // Import für ArrayList
import java.util.List;      // Import für List

/**
 * Ein Lernset ist eine Sammlung von Lernkarten.
 */
public class Lernset {

    // ----- Felder -----
    private int id;                  // Die Datenbank-ID
    private String name;             // Der Name des Sets
    private List<Lernkarte> karten;  // Die Karten in diesem Set

    // ----- Konstruktor -----

    /**
     * Erstellt ein neues Lernset.
     * @param id   Die Datenbank-ID
     * @param name Der Name
     */
    public Lernset(int id, String name) {
        this.id = id;                    // ID speichern
        this.name = name;                // Name speichern
        this.karten = new ArrayList<>(); // Leere Liste erstellen
    }

    // ----- Getter -----

    /** Gibt die ID zurück */
    public int getId() {
        return id; // ID zurückgeben
    }

    /** Gibt den Namen zurück */
    public String getName() {
        return name; // Name zurückgeben
    }

    /** Gibt die Kartenliste zurück */
    public List<Lernkarte> getKarten() { // Jetzt richtig: List<Lernkarte>
        return karten; // Liste zurückgeben
    }

    // ----- Setter -----

    /** Setzt einen neuen Namen */
    public void setName(String name) {
        this.name = name; // Neuen Namen setzen
    }

    /** Fügt eine Karte zur Liste hinzu */
    public void karteHinzufügen(Lernkarte karte) {
        karten.add(karte); // Karte hinzufügen
    }

    /** Gibt die Anzahl der Karten zurück */
    public int getAnzahlKarten() {
        return karten.size(); // Grösse zurückgeben
    }

    /**
     * Gibt das Lernset als Text aus.
     * @return Name und Anzahl Karten
     */
    @Override
    public String toString() {
        return name + "  (" + karten.size() + " Karten)"; // Name + Anzahl
    }
}
