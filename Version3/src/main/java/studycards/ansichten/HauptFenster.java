package studycards.ansichten;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
 * Zeigt alle Lernsets und ermöglicht deren Verwaltung.
 */
public class HauptFenster {

    // ----- Felder -----
    private Stage fenster;
    private DatenbankManager datenbank;
    private ListView<Lernset> setsListe;
    private ObservableList<Lernset> setsDaten;

    /**
     * Erstellt das Hauptfenster.
     * @param fenster   Das Stage-Objekt
     * @param datenbank Der Datenbankmanager
     */
    public HauptFenster(Stage fenster, DatenbankManager datenbank) {
        this.fenster   = fenster;
        this.datenbank = datenbank;
    }

    /**
     * Baut das Fenster auf und zeigt es an.
     */
    public void zeige() {

        // Überschrift
        Label überschrift = new Label("Meine Lernsets");
        überschrift.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Liste der Lernsets
        setsListe = new ListView<>();

        setsDaten = new ObservableList<>(); // Datencontainer erstellen

        setsListe.setItems(setsDaten);
        setsListe.setPrefHeight(300);
        setsListeAktualisieren();

        // Buttons
        Button btnNeuesSet   = new Button("+ Neues Set");
        Button btnLöschen   = new Button("Set löschen");
        Button btnUmbenennen = new Button("Umbenennen");
        Button btnKarten     = new Button("Karten verwalten");
        Button btnLernmodus  = new Button("Lernmodus starten");

        btnLernmodus.setStyle("-fx-background-color: #3a7dc9; -fx-text-fill: white; -fx-font-weight: bold;");

        // Neues Lernset erstellen
        btnNeuesSet.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Neues Lernset erstellen");
            dialog.setHeaderText("Wie soll das neue Lernset heissen?");
            dialog.setContentText("Name:");
            dialog.showAndWait().ifPresent(eingabe -> {
                if (!eingabe.trim().isEmpty()) {
                    datenbank.lernsetSpeichern(eingabe);
                    setsListeAktualisieren();
                }
            });
        });

        // Löschen
        btnLöschen.setOnAction(e -> {
            Lernset ausgewählt = setsListe.getSelectionModel().getSelectedItem();
            if (ausgewählt != null) {
                Alert bestätigung = new Alert(Alert.AlertType.CONFIRMATION);
                bestätigung.setTitle("Löschen bestätigen");
                bestätigung.setContentText("Wirklich löschen?");
                bestätigung.showAndWait().ifPresent(antwort -> {
                    if (antwort == ButtonType.OK) {
                        datenbank.lernsetLöschen(ausgewählt.getId());
                        setsListeAktualisieren();
                    }
                });
            } else {
                new Alert(Alert.AlertType.WARNING, "Bitte zuerst ein Set auswählen!").show();
            }
        });

        // Umbenennen
        btnUmbenennen.setOnAction(e -> {
            Lernset ausgewählt = setsListe.getSelectionModel().getSelectedItem();
            if (ausgewählt != null) {
                TextInputDialog dialog = new TextInputDialog(ausgewählt.getName());
                dialog.setTitle("Umbenennen");
                dialog.setContentText("Neuer Name:");
                dialog.showAndWait().ifPresent(neuerName -> {
                    if (!neuerName.trim().isEmpty()) {
                        datenbank.lernsetAktualisieren(ausgewählt.getId(), neuerName);
                        setsListeAktualisieren();
                    }
                });
            } else {
                new Alert(Alert.AlertType.WARNING, "Bitte zuerst ein Set auswählen!").show();
            }
        });

        // Karten verwalten
        btnKarten.setOnAction(e -> {
            Lernset ausgewählt = setsListe.getSelectionModel().getSelectedItem();
            if (ausgewählt != null) {
                Stage kartenStage = new Stage();
                KartenVerwaltung kv = new KartenVerwaltung(kartenStage, datenbank, ausgewählt);
                kv.zeige();
            } else {
                new Alert(Alert.AlertType.WARNING, "Bitte zuerst ein Set auswählen!").show();
            }
        });

        // Lernmodus starten
        btnLernmodus.setOnAction(e -> {
            Lernset ausgewählt = setsListe.getSelectionModel().getSelectedItem();
            if (ausgewählt != null) {
                List<Lernkarte> karten = datenbank.kartenLadenFürSet(ausgewählt.getId());
                if (karten.isEmpty()) {
                    new Alert(Alert.AlertType.WARNING, "Keine Karten vorhanden!").show();
                } else {
                    Stage lernStage = new Stage();
                    LernModus lm = new LernModus(lernStage, karten);
                    lm.zeige();
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Bitte zuerst ein Set auswählen!").show();
            }
        });

        // Layout
        HBox buttonLeiste = new HBox(8);
        buttonLeiste.getChildren().addAll(btnNeuesSet, btnLöschen, btnUmbenennen, btnKarten, btnLernmodus);

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(überschrift, setsListe, buttonLeiste);

        Scene szene = new Scene(layout, 720, 480);
        fenster.setTitle("StudyCards - Lernset-Übersicht");
        fenster.setScene(szene);
        fenster.show();
    }

    /**
     * Aktualisiert die Lernset-Liste aus der Datenbank.
     */
    private void setsListeAktualisieren() {
        setsDaten.clear();
        List<Lernset> alleSets = datenbank.alleLernseteLaden();
        setsDaten.addAll(alleSets);
    }
}
