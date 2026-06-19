package studycards;

// das hier ist mein hauptprogramm, hier startet alles

import javafx.application.Application; // das muss man importieren damit javafx funktioniert
import javafx.stage.Stage;             // das ist das fenster
import studycards.datenbank.DatenbankManager; // meine klasse die die datenbank macht
import studycards.ansichten.HauptFenster;     // das erste fenster das der nutzer sieht

/**
 * Das ist die Hauptklasse von StudyCards.
 * Man muss Application erweitern damit JavaFX funktioniert.
 */
public class Main extends Application {

    // den datenbankmanager speichere ich hier damit alle anderen klassen ihn benutzen können
    private static DatenbankManager datenbankManager;

    /**
     * Diese Methode wird automatisch aufgerufen wenn das Programm startet.
     * @param primaryStage das ist das hauptfenster das javafx uns automatisch gibt
     */
    @Override
    public void start(Stage primaryStage) {

        // zuerst die datenbank starten, ohne datenbank läuft gar nichts
        datenbankManager = new DatenbankManager(); // neues objekt erstellen
        datenbankManager.verbinden();              // verbindung zur datenbank aufbauen
        datenbankManager.tabellenErstellen();      // tabellen erstellen wenn sie noch nicht da sind

        // jetzt das erste fenster erstellen und anzeigen
        HauptFenster hauptFenster = new HauptFenster(primaryStage, datenbankManager);
        hauptFenster.zeige(); // fenster anzeigen
    }

    /**
     * Gibt den DatenbankManager zurück.
     * Habe ich als static gemacht damit ich ihn von überall aufrufen kann.
     * @return der datenbankmanager
     */
    public static DatenbankManager getDatenbankManager() {
        return datenbankManager; // zurückgeben
    }

    /**
     * Hier fängt das Programm an.
     * @param args die brauche ich nicht
     */
    public static void main(String[] args) {
        launch(args); // javafx starten
    }
}
