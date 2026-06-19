package studycards.model;

/**
 * Ein Lernset ist eine Sammlung von Lernkarten.
 * Man kann einem Lernset mehrere Karten hinzufügen.
 */
public class Lernset {

    // ein lernset hat eine nummer, einen namen und eine liste mit karten
    private int id;                  // nummer in der datenbank
    private String name;             // der name des lernsets
    private List<Lernkarte> karten;  // die karten die in diesem set sind

    /**
     * Erstellt ein neues Lernset.
     * @param id   die nummer aus der datenbank
     * @param name der name des lernsets
     */
    public Lernset(int id, String name) {
        this.id = id;
        this.name = name;
        this.karten = new ArrayList<>(); // leere liste erstellen
    }

    // -- getter --

    /** Gibt die ID zurück */
    public int getId() {
        return id;
    }

    /** Gibt den Namen zurück */
    public String getName() {
        return name;
    }

    /** Gibt die Liste mit den Karten zurück */
    public List<Lernkarte> getKarten() {
        return karten;
    }

    // -- setter --

    /** Setzt einen neuen Namen */
    public void setName(String name) {
        this.name = name;
    }

    /** Fügt eine Karte zur Liste hinzu */
    public void karteHinzufügen(Lernkarte karte) {
        karten.add(karte);
    }

    /** Gibt zurück wie viele Karten im Set sind */
    public int getAnzahlKarten() {
        return karten.size();
    }

    /**
     * Das brauche ich damit der name in der liste angezeigt wird.
     * @return name und anzahl der karten
     */
    @Override
    public String toString() {
        return name + "  (" + karten.size() + " Karten)";
    }
}
