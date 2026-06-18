package studycards.ansichten;

// ============================================================
// Das Hauptfenster zeigt alle Lernsets an.
// Von hier aus kann man alles steuern.
// ============================================================

import javafx.collections.FXCollections;         // Hilfe für JavaFX Listen
import javafx.collections.ObservableList;        // Liste die JavaFX beobachten kann
import javafx.geometry.Insets;                   // Für Abstands-Einstellungen
import javafx.scene.Scene;                       // Die Szene des Fensters
import javafx.scene.control.Alert;              // Popup-Fenster
import javafx.scene.control.Button;             // Schaltflächen
import javafx.scene.control.ButtonType;         // Buttons in Alerts
import javafx.scene.control.Label;              // Texte
import javafx.scene.control.ListView;           // Liste für Lernsets
import javafx.scene.control.TextInputDialog;    // Eingabedialog
import javafx.scene.layout.HBox;                // Horizontales Layout
import javafx.scene.layout.VBox;                // Vertikales Layout
import javafx.stage.Stage;                      // Das Fenster selbst
import studycards.datenbank.DatenbankManager;  // Datenbankzugriff
import studycards.model.Lernkarte;             // Lernkarten-Klasse
import studycards.model.Lernset;               // Lernset-Klasse

import java.util.List; // Für die Liste der Lernsets

/**
 * Das Hauptfenster der Anwendung.
 * Hier werden alle Lernsets aufgelistet.
 * Man kann Sets erstellen, löschen, umbenennen und den Lernmodus starten.
 */
public class HauptFenster {

    // ----- Felder -----
    private Stage fenster;                        // Das JavaFX-Fenster
    private DatenbankManager datenbank;           // Zugriff auf die Datenbank
    private ListView<Lernset> setsListe;          // Die Liste die Sets anzeigt
    private ObservableList<Lernset> setsDaten;    // Die Daten hinter der Liste

    /**
     * Erstellt das Hauptfenster.
     * @param fenster   Das Stage-Objekt von JavaFX
     * @param datenbank Der Datenbankmanager
     */
    public HauptFenster(Stage fenster, DatenbankManager datenbank) {
        this.fenster   = fenster;   // Fenster merken
        this.datenbank = datenbank; // Datenbank merken
    }

    /**
     * Baut das Fenster komplett auf und zeigt es an.
     */
    public void zeige() {

        // ----- Überschrift -----
        Label überschrift = new Label("Meine Lernsets");                     // Text erstellen
        überschrift.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;"); // Gross und fett

        // ----- Die Liste der Lernsets -----
        setsListe = new ListView<>();                             // Neue leere Liste
        setsDaten = FXCollections.observableArrayList();         // Datencontainer erstellen
        setsListe.setItems(setsDaten);                           // Liste mit Daten verbinden
        setsListe.setPrefHeight(300);                            // Höhe der Liste
        setsListeAktualisieren();                                // Daten aus der DB laden

        // ----- Buttons erstellen -----
        Button btnNeuesSet    = new Button("+ Neues Set");       // Neues Set anlegen
        Button btnLöschen    = new Button("Set löschen");      // Set löschen
        Button btnUmbenennen  = new Button("Umbenennen");        // Set umbenennen
        Button btnKarten      = new Button("Karten verwalten");  // Karten eines Sets verwalten
        Button btnLernmodus   = new Button("Lernmodus starten"); // Lernen starten

        // Lernmodus-Button hervorheben
        btnLernmodus.setStyle("-fx-background-color: #3a7dc9; -fx-text-fill: white; -fx-font-weight: bold;");

        // ============================================================
        // BUTTON-AKTIONEN
        // ============================================================

        // --- Neues Lernset erstellen ---
        btnNeuesSet.setOnAction(e -> {
            // Eingabefenster anzeigen
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Neues Lernset erstellen");
            dialog.setHeaderText("Wie soll das neue Lernset heissen?");
            dialog.setContentText("Name des Lernsets:");

            // Wenn der Benutzer einen Namen eingegeben hat
            dialog.showAndWait().ifPresent(eingabe -> {
                if (!eingabe.trim().isEmpty()) {          // Nur speichern wenn nicht leer
                    datenbank.lernsetSpeichern(eingabe);  // In der DB speichern
                    setsListeAktualisieren();             // Liste neu laden
                }
            });
        });

        // --- Lernset löschen ---
        btnLöschen.setOnAction(e -> {
            Lernset ausgewählt = setsListe.getSelectionModel().getSelectedItem(); // Ausgewahltes Set holen
            if (ausgewählt != null) { // Nur wenn etwas ausgewählt ist
                // Sicherheitsfrage stellen
                Alert bestätigung = new Alert(Alert.AlertType.CONFIRMATION);
                bestätigung.setTitle("Löschen bestätigen");
                bestätigung.setHeaderText("Lernset löschen?");
                bestätigung.setContentText("Das Set \"" + ausgewählt.getName() + "\" und alle\n" +
                        "enthaltenen Karten werden gelöscht!");

                bestätigung.showAndWait().ifPresent(antwort -> {
                    if (antwort == ButtonType.OK) {                   // Wenn Benutzer OK geklickt hat
                        datenbank.lernsetLöschen(ausgewählt.getId()); // Aus DB löschen
                        setsListeAktualisieren();                       // Liste aktualisieren
                    }
                });
            } else {
                // Fehlermeldung - nichts ausgewählt
                new Alert(Alert.AlertType.WARNING, "Bitte zuerst ein Lernset aus der Liste auswählen!").show();
            }
        });

        // --- Lernset umbenennen ---
        btnUmbenennen.setOnAction(e -> {
            Lernset ausgewählt = setsListe.getSelectionModel().getSelectedItem();
            if (ausgewählt != null) {
                // Dialog mit aktuellem Namen vorausgefüllt
                TextInputDialog dialog = new TextInputDialog(ausgewählt.getName());
                dialog.setTitle("Lernset umbenennen");
                dialog.setHeaderText("Neuen Namen eingeben:");
                dialog.setContentText("Name:");

                dialog.showAndWait().ifPresent(neuerName -> {
                    if (!neuerName.trim().isEmpty()) {                          // Nur wenn Name nicht leer
                        datenbank.lernsetAktualisieren(ausgewählt.getId(), neuerName); // In DB speichern
                        setsListeAktualisieren();                               // Liste neu laden
                    }
                });
            } else {
                new Alert(Alert.AlertType.WARNING, "Bitte zuerst ein Lernset auswählen!").show();
            }
        });

        // --- Karten verwalten ---
        btnKarten.setOnAction(e -> {
            Lernset ausgewählt = setsListe.getSelectionModel().getSelectedItem();
            if (ausgewählt != null) {
                // Neues Fenster für die Kartenverwaltung oeffnen
                Stage kartenStage = new Stage();
                KartenVerwaltung kv = new KartenVerwaltung(kartenStage, datenbank, ausgewählt);
                kv.zeige(); // Fenster anzeigen
            } else {
                new Alert(Alert.AlertType.WARNING, "Bitte zuerst ein Lernset auswählen!").show();
            }
        });

        // --- Lernmodus starten ---
        btnLernmodus.setOnAction(e -> {
            Lernset ausgewählt = setsListe.getSelectionModel().getSelectedItem();
            if (ausgewählt != null) {
                // Karten für das Set aus der DB laden
                List<Lernkarte> karten = datenbank.kartenLadenFürSet(ausgewählt.getId());

                if (karten.isEmpty()) { // Wenn keine Karten vorhanden
                    new Alert(Alert.AlertType.WARNING, "Dieses Lernset hat noch keine Karten!\n" +
                            "Bitte zuerst Karten hinzufügen.").show();
                } else {
                    // Lernmodus starten
                    Stage lernStage = new Stage();
                    LernModus lm = new LernModus(lernStage, karten);
                    lm.zeige(); // Lernfenster anzeigen
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Bitte zuerst ein Lernset auswählen!").show();
            }
        });

        // ============================================================
        // LAYOUT ZUSAMMENBAUEN
        // ============================================================

        // Alle Buttons nebeneinander in eine horizontale Box
        HBox buttonLeiste = new HBox(8); // 8 Pixel Abstand zwischen den Buttons
        buttonLeiste.getChildren().addAll(btnNeuesSet, btnLöschen, btnUmbenennen, btnKarten, btnLernmodus);

        // Alles untereinander in eine vertikale Box
        VBox layout = new VBox(12); // 12 Pixel Abstand zwischen den Elementen
        layout.setPadding(new Insets(20)); // 20 Pixel Rand rundum
        layout.getChildren().addAll(überschrift, setsListe, buttonLeiste); // Alles reinpacken

        // ----- Fenster konfigurieren und anzeigen -----
        Scene szene = new Scene(layout, 720, 480); // Szene mit Grösse erstellen
        fenster.setTitle("StudyCards - Lernset-Übersicht"); // Fenstertitel setzen
        fenster.setScene(szene); // Szene ins Fenster laden
        fenster.show();          // Fenster anzeigen
    }

    /**
     * Lädt alle Lernsets neu aus der Datenbank und aktualisiert die Liste.
     * Diese Methode wird immer aufgerufen wenn sich etwas geändert hat.
     */
    private void setsListeAktualisieren() {
        setsDaten.clear();                                   // Alte Einträge löschen
        List<Lernset> alleSets = datenbank.alleLernseteLaden(); // Neu aus DB laden
        setsDaten.addAll(alleSets);                          // Neue Einträge hinzufügen
    }
}
