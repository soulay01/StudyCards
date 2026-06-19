package studycards.ansichten;

// das hauptfenster zeigt alle lernsets an
// von hier aus kann man sets erstellen, löschen, umbenennen und den lernmodus starten

import javafx.collections.FXCollections;  // hilfsmethoden für javafx listen
import javafx.collections.ObservableList; // eine liste die automatisch die ansicht aktualisiert
import javafx.geometry.Insets;            // für Abstände
import javafx.scene.Scene;               // das ist die szene im fenster
import javafx.scene.control.Alert;       // für popups
import javafx.scene.control.Button;      // die buttons
import javafx.scene.control.ButtonType;  // für ok/abbrechen bei dialogen
import javafx.scene.control.Label;       // texte die man nicht bearbeiten kann
import javafx.scene.control.ListView;    // die liste mit den lernsets
import javafx.scene.control.TextInputDialog; // dialog zum text eingeben
import javafx.scene.layout.HBox;         // elemente nebeneinander
import javafx.scene.layout.VBox;         // elemente untereinander
import javafx.stage.Stage;               // das fenster
import studycards.datenbank.DatenbankManager; // für die datenbankoperationen
import studycards.model.Lernkarte;            // die lernkarten klasse
import studycards.model.Lernset;              // die lernset klasse

import java.util.List;             // für die liste
import java.util.Optional;         // das brauche ich für den Rückgabewert von dialogen

/**
 * Das Hauptfenster der Anwendung.
 * Hier sieht man alle Lernsets und kann sie verwalten.
 */
public class HauptFenster {

    // -- variablen für das fenster und die daten --
    private Stage fenster;                     // das javafx fenster
    private DatenbankManager datenbank;        // zugriff auf die datenbank
    private ListView<Lernset> setsListe;       // die liste die man sieht
    private ObservableList<Lernset> setsDaten; // die daten hinter der liste

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

        // Überschrift oben im fenster
        Label überschrift = new Label("Meine Lernsets :D");
        überschrift.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // liste mit allen lernsets
        setsListe = new ListView<>();
        setsDaten = FXCollections.observableArrayList(); // so erstellt man eine beobachtbare liste
        setsListe.setItems(setsDaten);                  // liste mit daten verbinden
        setsListe.setPrefHeight(300);
        setsListeAktualisieren(); // daten aus der datenbank laden

        // -- alle buttons erstellen --
        Button btnNeuesSet   = new Button("+ Neues Set");
        Button btnLöschen    = new Button("Set löschen");
        Button btnUmbenennen = new Button("Umbenennen");
        Button btnKarten     = new Button("Karten verwalten");
        Button btnLernmodus  = new Button("Lernmodus starten");

        // lernmodus button sollte blau sein damit man ihn sofort sieht
        btnLernmodus.setStyle("-fx-background-color: #3a7dc9; -fx-text-fill: white; -fx-font-weight: bold;");

        // -------------------------------------------------------
        // BUTTON AKTIONEN
        // -------------------------------------------------------

        // neues lernset erstellen
        btnNeuesSet.setOnAction(e -> {
            // eingabedialog oeffnen
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Neues Lernset");
            dialog.setHeaderText("Wie soll das Lernset heissen?");
            dialog.setContentText("Name:");

            // warten bis der benutzer etwas eingegeben hat oder abgebrochen hat
            Optional<String> ergebnis = dialog.showAndWait();
            if (ergebnis.isPresent()) { // nur wenn der benutzer ok gedrückt hat
                String eingabe = ergebnis.get();
                if (!eingabe.trim().isEmpty()) { // nur wenn kein leerer name
                    datenbank.lernsetSpeichern(eingabe); // in der datenbank speichern
                    setsListeAktualisieren();             // liste neu laden
                }
            }
        });

        // lernset löschen
        btnLöschen.setOnAction(e -> {
            Lernset ausgewählt = setsListe.getSelectionModel().getSelectedItem();
            if (ausgewählt != null) {
                // erstmal nachfragen ob der nutzer wirklich löschen will
                Alert bestätigung = new Alert(Alert.AlertType.CONFIRMATION);
                bestätigung.setTitle("Löschen bestätigen");
                bestätigung.setContentText("Das Set und alle Karten werden gelöscht!");

                Optional<ButtonType> antwort = bestätigung.showAndWait();
                if (antwort.isPresent() && antwort.get() == ButtonType.OK) { // nur bei ok löschen
                    datenbank.lernsetLöschen(ausgewählt.getId());
                    setsListeAktualisieren();
                }
            } else {
                // kein set ausgewählt - hinweis anzeigen
                new Alert(Alert.AlertType.WARNING, "Bitte zuerst ein Set auswählen!").show();
            }
        });

        // lernset umbenennen
        btnUmbenennen.setOnAction(e -> {
            Lernset ausgewählt = setsListe.getSelectionModel().getSelectedItem();
            if (ausgewählt != null) {
                // dialog mit dem aktuellen namen als Startwert
                TextInputDialog dialog = new TextInputDialog(ausgewählt.getName());
                dialog.setTitle("Umbenennen");
                dialog.setContentText("Neuer Name:");

                Optional<String> ergebnis = dialog.showAndWait();
                if (ergebnis.isPresent()) {
                    String neuerName = ergebnis.get();
                    if (!neuerName.trim().isEmpty()) {
                        datenbank.lernsetAktualisieren(ausgewählt.getId(), neuerName);
                        setsListeAktualisieren();
                    }
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Bitte zuerst ein Set auswählen!").show();
            }
        });

        // karten verwalten - öffnet ein neues fenster
        btnKarten.setOnAction(e -> {
            Lernset ausgewählt = setsListe.getSelectionModel().getSelectedItem();
            if (ausgewählt != null) {
                Stage kartenStage = new Stage(); // neues fenster erstellen
                KartenVerwaltung kv = new KartenVerwaltung(kartenStage, datenbank, ausgewählt);
                kv.zeige();
            } else {
                new Alert(Alert.AlertType.WARNING, "Bitte zuerst ein Set auswählen!").show();
            }
        });

        // lernmodus starten - öffnet den lernmodus
        btnLernmodus.setOnAction(e -> {
            Lernset ausgewählt = setsListe.getSelectionModel().getSelectedItem();
            if (ausgewählt != null) {
                // karten für dieses set laden
                List<Lernkarte> karten = datenbank.kartenLadenFürSet(ausgewählt.getId());
                if (karten.isEmpty()) {
                    // wenn keine karten da sind kann man nicht lernen
                    new Alert(Alert.AlertType.WARNING,
                            "Dieses Set hat noch keine Karten!").show();
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

        // alle buttons nebeneinander
        HBox buttonLeiste = new HBox(8);
        buttonLeiste.getChildren().addAll(btnNeuesSet, btnLöschen, btnUmbenennen, btnKarten, btnLernmodus);

        // alles untereinander
        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(überschrift, setsListe, buttonLeiste);

        // fenster anzeigen
        Scene szene = new Scene(layout, 720, 480);
        fenster.setTitle("StudyCards - Übersicht");
        fenster.setScene(szene);
        fenster.show();
    }

    /**
     * Lädt alle Lernsets neu aus der Datenbank und aktualisiert die Liste.
     * Das muss ich nach jeder Änderung aufrufen damit die Liste aktuell ist.
     */
    private void setsListeAktualisieren() {
        setsDaten.clear();                                       // alte einträge löschen
        List<Lernset> alleSets = datenbank.alleLernseteLaden(); // neu aus der datenbank laden
        setsDaten.addAll(alleSets);                             // neue einträge hinzufügen
    }
}
