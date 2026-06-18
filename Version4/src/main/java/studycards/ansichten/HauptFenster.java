package studycards.ansichten;

import javafx.collections.FXCollections;         // Hilfsmethoden für Listen
import javafx.collections.ObservableList;        // Das Listen-Interface
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import studycards.datenbank.DatenbankManager;
import studycards.model.Lernkarte;
import studycards.model.Lernset;

import java.util.List;

/**
 * Das Hauptfenster der Anwendung.
 * Zeigt alle Lernsets an und ermöglicht deren Verwaltung.
 */
public class HauptFenster {

    // ----- Felder -----
    private Stage fenster;                     // Das JavaFX-Fenster
    private DatenbankManager datenbank;        // Zugriff auf die Datenbank
    private ListView<Lernset> setsListe;       // Die angezeigte Liste
    private ObservableList<Lernset> setsDaten; // Die Daten hinter der Liste

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
        Label überschrift = new Label("Meine Lernsets"); // Titel-Text
        überschrift.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;"); // Gross und fett

        // ----- Liste der Lernsets -----
        setsListe = new ListView<>();                             // Neue Liste erstellen
        setsDaten = FXCollections.observableArrayList();         // Richtig: Factory-Methode benutzen
        setsListe.setItems(setsDaten);                           // Liste mit Daten verbinden
        setsListe.setPrefHeight(300);                            // Höhe festlegen
        setsListeAktualisieren();                                // Daten aus DB laden

        // ----- Buttons erstellen -----
        Button btnNeuesSet   = new Button("+ Neues Set");       // Neues Set anlegen
        Button btnLöschen   = new Button("Set löschen");      // Set löschen
        Button btnUmbenennen = new Button("Umbenennen");        // Set umbenennen
        Button btnKarten     = new Button("Karten verwalten");  // Karten bearbeiten
        Button btnLernmodus  = new Button("Lernmodus starten"); // Lernen starten

        // Lernmodus-Button hervorheben
        btnLernmodus.setStyle("-fx-background-color: #3a7dc9; -fx-text-fill: white; -fx-font-weight: bold;");

        // ============================================================
        // BUTTON-AKTIONEN
        // ============================================================

        // --- Neues Lernset erstellen ---
        btnNeuesSet.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog(); // Eingabefenster erstellen
            dialog.setTitle("Neues Lernset erstellen");
            dialog.setHeaderText("Wie soll das neue Lernset heissen?");
            dialog.setContentText("Name des Lernsets:");
            dialog.showAndWait().ifPresent(eingabe -> { // Warten und Eingabe verarbeiten
                if (!eingabe.trim().isEmpty()) {          // Leere Namen abfangen
                    datenbank.lernsetSpeichern(eingabe);  // In der Datenbank speichern
                    setsListeAktualisieren();             // Liste neu laden
                }
            });
        });

        // --- Lernset löschen ---
        btnLöschen.setOnAction(e -> {
            Lernset ausgewählt = setsListe.getSelectionModel().getSelectedItem(); // Ausgewahltes holen
            if (ausgewählt != null) {
                Alert bestätigung = new Alert(Alert.AlertType.CONFIRMATION); // Sicherheitsfrage
                bestätigung.setTitle("Löschen bestätigen");
                bestätigung.setContentText("Das Set und alle Karten werden gelöscht!");
                bestätigung.showAndWait().ifPresent(antwort -> {
                    if (antwort == ButtonType.OK) {                    // Nur bei OK löschen
                        datenbank.lernsetLöschen(ausgewählt.getId()); // Aus DB löschen
                        setsListeAktualisieren();                       // Liste aktualisieren
                    }
                });
            } else {
                new Alert(Alert.AlertType.WARNING, "Bitte zuerst ein Set auswählen!").show();
            }
        });

        // --- Lernset umbenennen ---
        btnUmbenennen.setOnAction(e -> {
            Lernset ausgewählt = setsListe.getSelectionModel().getSelectedItem();
            if (ausgewählt != null) {
                TextInputDialog dialog = new TextInputDialog(ausgewählt.getName()); // Aktuellen Namen vorausfüllen
                dialog.setTitle("Lernset umbenennen");
                dialog.setContentText("Neuer Name:");
                dialog.showAndWait().ifPresent(neuerName -> {
                    if (!neuerName.trim().isEmpty()) {
                        datenbank.lernsetAktualisieren(ausgewählt.getId(), neuerName); // In DB ändern
                        setsListeAktualisieren(); // Liste aktualisieren
                    }
                });
            } else {
                new Alert(Alert.AlertType.WARNING, "Bitte zuerst ein Set auswählen!").show();
            }
        });

        // --- Karten verwalten ---
        btnKarten.setOnAction(e -> {
            Lernset ausgewählt = setsListe.getSelectionModel().getSelectedItem();
            if (ausgewählt != null) {
                Stage kartenStage = new Stage(); // Neues Fenster erstellen
                KartenVerwaltung kv = new KartenVerwaltung(kartenStage, datenbank, ausgewählt);
                kv.zeige(); // Fenster anzeigen
            } else {
                new Alert(Alert.AlertType.WARNING, "Bitte zuerst ein Set auswählen!").show();
            }
        });

        // --- Lernmodus starten ---
        btnLernmodus.setOnAction(e -> {
            Lernset ausgewählt = setsListe.getSelectionModel().getSelectedItem();
            if (ausgewählt != null) {
                List<Lernkarte> karten = datenbank.kartenLadenFürSet(ausgewählt.getId()); // Karten laden
                if (karten.isEmpty()) { // Keine Karten vorhanden?
                    new Alert(Alert.AlertType.WARNING,
                            "Dieses Set hat noch keine Karten! Bitte zuerst Karten hinzufügen.").show();
                } else {
                    Stage lernStage = new Stage(); // Neues Fenster für den Lernmodus
                    LernModus lm = new LernModus(lernStage, karten);
                    lm.zeige(); // Lernmodus anzeigen
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Bitte zuerst ein Set auswählen!").show();
            }
        });

        // ============================================================
        // LAYOUT ZUSAMMENBAUEN
        // ============================================================

        HBox buttonLeiste = new HBox(8); // Alle Buttons nebeneinander mit 8px Abstand
        buttonLeiste.getChildren().addAll(btnNeuesSet, btnLöschen, btnUmbenennen, btnKarten, btnLernmodus);

        VBox layout = new VBox(12); // Alles untereinander mit 12px Abstand
        layout.setPadding(new Insets(20)); // 20px Rand rundum
        layout.getChildren().addAll(überschrift, setsListe, buttonLeiste); // Elemente reinpacken

        // Fenster anzeigen
        Scene szene = new Scene(layout, 720, 480);
        fenster.setTitle("StudyCards - Lernset-Übersicht");
        fenster.setScene(szene);
        fenster.show();
    }

    /**
     * Lädt alle Lernsets aus der Datenbank und aktualisiert die angezeigte Liste.
     */
    private void setsListeAktualisieren() {
        setsDaten.clear();                                      // Alte Einträge löschen
        List<Lernset> alleSets = datenbank.alleLernseteLaden(); // Frisch aus DB laden
        setsDaten.addAll(alleSets);                             // Neue Einträge hinzufügen
    }
}
