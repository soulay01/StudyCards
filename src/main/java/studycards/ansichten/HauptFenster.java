package studycards.ansichten;

// das hauptfenster zeigt alle lernsets an
// von hier aus kann man sets erstellen, löschen, umbenennen und den lernmodus starten

import javafx.collections.FXCollections;  // hilfsmethoden für javafx listen
import javafx.collections.ObservableList; // eine liste die automatisch die ansicht aktualisiert
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
 * Hier sieht man alle Lernsets und kann sie verwalten.
 */
public class HauptFenster {

    // -- variablen für das fenster und die daten --
    private Stage fenster;
    private DatenbankManager datenbank;
    private ListView<Lernset> setsListe;
    private ObservableList<Lernset> setsDaten;

    /**
     * Erstellt das Hauptfenster.
     * @param fenster   das stage objekt von javafx
     * @param datenbank der datenbankmanager
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

        // liste mit allen lernsets
        setsListe = new ListView<>();

        setsDaten = new ObservableList<>(); // datencontainer erstellen

        setsListe.setItems(setsDaten);
        setsListe.setPrefHeight(300);
        setsListeAktualisieren();

        // -- alle buttons erstellen --
        Button btnNeuesSet   = new Button("+ Neues Set");
        Button btnLöschen    = new Button("Set löschen");
        Button btnUmbenennen = new Button("Umbenennen");
        Button btnKarten     = new Button("Karten verwalten");
        Button btnLernmodus  = new Button("Lernmodus starten");

        btnLernmodus.setStyle("-fx-background-color: #3a7dc9; -fx-text-fill: white; -fx-font-weight: bold;");

        // -------------------------------------------------------
        // BUTTON AKTIONEN
        // -------------------------------------------------------

        // neues lernset erstellen
        btnNeuesSet.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Neues Lernset");
            dialog.setHeaderText("Wie soll das Lernset heissen?");
            dialog.setContentText("Name:");
            dialog.showAndWait().ifPresent(eingabe -> {
                if (!eingabe.trim().isEmpty()) {
                    datenbank.lernsetSpeichern(eingabe);
                    setsListeAktualisieren();
                }
            });
        });

        // lernset löschen
        btnLöschen.setOnAction(e -> {
            Lernset ausgewählt = setsListe.getSelectionModel().getSelectedItem();
            if (ausgewählt != null) {
                Alert bestätigung = new Alert(Alert.AlertType.CONFIRMATION);
                bestätigung.setTitle("Löschen bestätigen");
                bestätigung.setContentText("Das Set und alle Karten werden gelöscht!");
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

        // lernset umbenennen
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

        // karten verwalten
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

        // lernmodus starten
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

        // -------------------------------------------------------
        // LAYOUT ZUSAMMENBAUEN
        // -------------------------------------------------------

        HBox buttonLeiste = new HBox(8);
        buttonLeiste.getChildren().addAll(btnNeuesSet, btnLöschen, btnUmbenennen, btnKarten, btnLernmodus);

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(überschrift, setsListe, buttonLeiste);

        Scene szene = new Scene(layout, 720, 480);
        fenster.setTitle("StudyCards - Übersicht");
        fenster.setScene(szene);
        fenster.show();
    }

    /**
     * Lädt alle Lernsets neu aus der Datenbank und aktualisiert die Liste.
     */
    private void setsListeAktualisieren() {
        setsDaten.clear();
        List<Lernset> alleSets = datenbank.alleLernseteLaden();
        setsDaten.addAll(alleSets);
    }
}
