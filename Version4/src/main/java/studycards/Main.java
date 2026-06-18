package studycards;

// ============================================================
// ============================================================

import javafx.application.Application;          // Richtig: Application (nicht Applikation)
import javafx.stage.Stage;                      // Das Fenster von JavaFX
import studycards.datenbank.DatenbankManager;  // Unsere Datenbank-Klasse
import studycards.ansichten.HauptFenster;      // Das Hauptfenster

/**
 * Die Hauptklasse des Programms.
 * Sie erbt von Application - das ist bei JavaFX Pflicht.
 */
public class Main extends Application { // Jetzt richtig: Application

    // Der Datenbank-Manager wird hier gespeichert
    private static DatenbankManager datenbankManager;

    /**
     * Die start()-Methode wird von JavaFX automatisch aufgerufen.
     * @param primaryStage Das Fenster das JavaFX uns gibt
     */
    @Override
    public void start(Stage primaryStage) {
        // Zuerst die Datenbank verbinden
        datenbankManager = new DatenbankManager();  // Neues Objekt erstellen
        datenbankManager.verbinden();               // Verbindung herstellen
        datenbankManager.tabellenErstellen();       // Tabellen anlegen

        // Das Hauptfenster erstellen und anzeigen
        HauptFenster hauptFenster = new HauptFenster(primaryStage, datenbankManager);
        hauptFenster.zeige(); // Fenster anzeigen
    }

    /**
     * Gibt den DatenbankManager zurück damit andere Klassen ihn nutzen können.
     * @return Der DatenbankManager
     */
    public static DatenbankManager getDatenbankManager() {
        return datenbankManager; // Manager zurückgeben
    }

    /**
     * Hier fängt das Programm an.
     * @param args Programmargumente (brauchen wir nicht)
     */
    public static void main(String[] args) {
        launch(args); // JavaFX starten
    }
}
