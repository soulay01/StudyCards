package studycards.ansichten;

// in diesem fenster kann man karten zu einem lernset hinzufügen, bearbeiten und löschen

import javafx.collections.FXCollections;  // für die beobachtbare liste
import javafx.collections.ObservableList; // liste die sich automatisch aktualisiert
import javafx.geometry.Insets;            // Abstände
import javafx.scene.Scene;               // die szene
import javafx.scene.control.Alert;       // popups
import javafx.scene.control.Button;      // buttons
import javafx.scene.control.Label;       // texte
import javafx.scene.control.ListView;    // liste der karten
import javafx.scene.control.TextField;   // eingabefelder
import javafx.scene.layout.GridPane;     // tabellen-layout für die eingabefelder
import javafx.scene.layout.HBox;         // elemente nebeneinander
import javafx.scene.layout.VBox;         // elemente untereinander
import javafx.stage.Stage;               // das fenster
import studycards.datenbank.DatenbankManager; // datenbankzugriff
import studycards.model.Lernkarte;            // lernkarten klasse
import studycards.model.Lernset;              // lernset klasse

import java.util.List; // für listen

/**
 * Fenster zum Verwalten der Karten in einem Lernset.
 * Man kann neue Karten anlegen, bestehende bearbeiten und löschen.
 */
public class KartenVerwaltung {

    // -- variablen --
    private Stage fenster;
    private DatenbankManager datenbank;
    private Lernset aktuellesSet;                    // das set dessen karten wir gerade bearbeiten
    private ListView<Lernkarte> kartenListe;
    private ObservableList<Lernkarte> kartenDaten;

    // ich speichere hier die id der karte die gerade bearbeitet wird
    // -1 bedeutet: es wird gerade keine karte bearbeitet
    private int bearbeitenId = -1;

    /**
     * Erstellt das Kartenverwaltungs-Fenster.
     * @param fenster   das fenster
     * @param datenbank der datenbankmanager
     * @param lernset   das lernset dessen karten verwaltet werden
     */
    public KartenVerwaltung(Stage fenster, DatenbankManager datenbank, Lernset lernset) {
        this.fenster      = fenster;
        this.datenbank    = datenbank;
        this.aktuellesSet = lernset;
    }

    /**
     * Baut das Fenster auf und zeigt es an.
     */
    public void zeige() {

        // Überschrift mit dem namen des sets
        Label überschrift = new Label("Karten verwalten: " + aktuellesSet.getName());
        überschrift.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // liste mit allen karten des sets
        kartenListe = new ListView<>();
        kartenDaten = FXCollections.observableArrayList();
        kartenListe.setItems(kartenDaten);
        kartenListe.setPrefHeight(250);
        kartenListeAktualisieren(); // karten aus der datenbank laden

        // eingabefelder für neue karten oder bearbeitung
        Label frageLabel  = new Label("Frage:");
        TextField frageEingabe = new TextField();
        frageEingabe.setPromptText("Frage eingeben...");
        frageEingabe.setPrefWidth(350);

        Label antwortLabel = new Label("Antwort:");
        TextField antwortEingabe = new TextField();
        antwortEingabe.setPromptText("Antwort eingeben...");
        antwortEingabe.setPrefWidth(350);

        // -- buttons --
        Button btnSpeichern  = new Button("Karte hinzufügen"); // speichert neue karte oder Änderungen
        Button btnLöschen    = new Button("Karte löschen");
        Button btnBearbeiten = new Button("Karte bearbeiten"); // lädt die karte in die felder

        // button aktionen

        // speichern button - entweder neue karte oder Änderung speichern
        btnSpeichern.setOnAction(e -> {
            String frage   = frageEingabe.getText().trim();
            String antwort = antwortEingabe.getText().trim();

            if (!frage.isEmpty() && !antwort.isEmpty()) {
                if (bearbeitenId != -1) {
                    // wenn bearbeitenId gesetzt ist dann eine bestehende karte aktualisieren
                    datenbank.karteAktualisieren(bearbeitenId, frage, antwort);
                    bearbeitenId = -1;                         // zurücksetzen
                    btnSpeichern.setText("Karte hinzufügen"); // button text zurücksetzen
                } else {
                    // sonst eine neue karte anlegen
                    datenbank.karteSpeichern(frage, antwort, aktuellesSet.getId());
                }
                frageEingabe.clear();   // felder leeren
                antwortEingabe.clear();
                kartenListeAktualisieren(); // liste neu laden
            } else {
                new Alert(Alert.AlertType.WARNING, "Bitte Frage und Antwort eingeben!").show();
            }
        });

        // karte löschen
        btnLöschen.setOnAction(e -> {
            Lernkarte ausgewählt = kartenListe.getSelectionModel().getSelectedItem();
            if (ausgewählt != null) {
                datenbank.karteLöschen(ausgewählt.getId());
                kartenListeAktualisieren();
            } else {
                new Alert(Alert.AlertType.WARNING, "Bitte zuerst eine Karte auswählen!").show();
            }
        });

        // karte bearbeiten - werte in die felder laden damit man sie aendern kann
        btnBearbeiten.setOnAction(e -> {
            Lernkarte ausgewählt = kartenListe.getSelectionModel().getSelectedItem();
            if (ausgewählt != null) {
                // werte der ausgewählten karte in die felder laden
                frageEingabe.setText(ausgewählt.getFrage());
                antwortEingabe.setText(ausgewählt.getAntwort());

                // id merken damit ich beim speichern weiss welche karte ich aendere
                bearbeitenId = ausgewählt.getId();

                // button umbenennen damit man weiss was passiert wenn man speichert
                btnSpeichern.setText("Änderungen speichern");
            } else {
                new Alert(Alert.AlertType.WARNING, "Bitte zuerst eine Karte auswählen!").show();
            }
        });

        // layout

        // eingabefelder in einem tabellen-layout anordnen
        GridPane eingabeBereich = new GridPane();
        eingabeBereich.setHgap(10);
        eingabeBereich.setVgap(8);
        eingabeBereich.add(frageLabel, 0, 0);
        eingabeBereich.add(frageEingabe, 1, 0);
        eingabeBereich.add(antwortLabel, 0, 1);
        eingabeBereich.add(antwortEingabe, 1, 1);

        // buttons nebeneinander
        HBox buttons = new HBox(8, btnSpeichern, btnLöschen, btnBearbeiten);

        // alles untereinander
        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(überschrift, kartenListe, eingabeBereich, buttons);

        // fenster anzeigen
        Scene szene = new Scene(layout, 620, 520);
        fenster.setTitle("Karten verwalten - " + aktuellesSet.getName());
        fenster.setScene(szene);
        fenster.show();
    }

    /**
     * Lädt alle Karten des aktuellen Sets und aktualisiert die Liste.
     */
    private void kartenListeAktualisieren() {
        kartenDaten.clear();
        List<Lernkarte> karten = datenbank.kartenLadenFürSet(aktuellesSet.getId());
        kartenDaten.addAll(karten);
    }
}
