package studycards.ansichten;

// ============================================================
// Dieses Fenster zeigt alle Karten eines Lernsets.
// Man kann Karten hinzufügen, löschen und bearbeiten.
// ============================================================

import javafx.collections.FXCollections;         // Für JavaFX Listen
import javafx.collections.ObservableList;        // Beobachtbare Liste
import javafx.geometry.Insets;                   // Für Ränder
import javafx.scene.Scene;                       // Die Szene
import javafx.scene.control.Alert;              // Popup-Nachrichten
import javafx.scene.control.Button;             // Schaltflächen
import javafx.scene.control.Label;              // Texte
import javafx.scene.control.ListView;           // Liste der Karten
import javafx.scene.control.TextField;          // Eingabefelder
import javafx.scene.layout.GridPane;            // Tabellen-Layout
import javafx.scene.layout.HBox;                // Horizontales Layout
import javafx.scene.layout.VBox;                // Vertikales Layout
import javafx.stage.Stage;                      // Das Fenster
import studycards.datenbank.DatenbankManager;  // Datenbankzugriff
import studycards.model.Lernkarte;             // Lernkarten-Klasse
import studycards.model.Lernset;               // Lernset-Klasse

import java.util.List; // Für Listen

/**
 * Fenster zum Verwalten der Karten innerhalb eines Lernsets.
 * Man kann neue Karten anlegen, bestehende bearbeiten und löschen.
 */
public class KartenVerwaltung {

    // ----- Felder -----
    private Stage fenster;                          // Das Fenster
    private DatenbankManager datenbank;             // Datenbankzugriff
    private Lernset aktuellesSet;                   // Das Lernset das wir bearbeiten
    private ListView<Lernkarte> kartenListe;        // Liste der Karten
    private ObservableList<Lernkarte> kartenDaten;  // Daten für die Liste

    /**
     * Erstellt das Kartenverwaltungs-Fenster.
     * @param fenster   Das Fenster
     * @param datenbank Der Datenbankmanager
     * @param lernset   Das Lernset dessen Karten verwaltet werden
     */
    public KartenVerwaltung(Stage fenster, DatenbankManager datenbank, Lernset lernset) {
        this.fenster      = fenster;   // Fenster merken
        this.datenbank    = datenbank; // Datenbank merken
        this.aktuellesSet = lernset;   // Lernset merken
    }

    /**
     * Baut das Fenster auf und zeigt es an.
     */
    public void zeige() {

        // ----- Überschrift -----
        Label überschrift = new Label("Karten verwalten: " + aktuellesSet.getName());
        überschrift.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;"); // Gross und fett

        // ----- Liste der vorhandenen Karten -----
        kartenListe = new ListView<>();                           // Neue Liste erstellen
        kartenDaten = FXCollections.observableArrayList();       // Datencontainer
        kartenListe.setItems(kartenDaten);                       // Liste mit Daten verbinden
        kartenListe.setPrefHeight(250);                          // Listenhöhe setzen
        kartenListeAktualisieren();                              // Karten aus DB laden

        // ----- Eingabebereich für neue Karten -----
        Label frageLabel = new Label("Frage:");          // Beschriftung
        TextField frageEingabe = new TextField();         // Textfeld für die Frage
        frageEingabe.setPromptText("Frage eingeben ...");// Platzhaltertext
        frageEingabe.setPrefWidth(350);                  // Breite setzen

        Label antwortLabel = new Label("Antwort:");     // Beschriftung
        TextField antwortEingabe = new TextField();      // Textfeld für die Antwort
        antwortEingabe.setPromptText("Antwort eingeben ...");
        antwortEingabe.setPrefWidth(350);

        // ----- Buttons -----
        Button btnHinzufügen = new Button("Karte hinzufügen");  // Neue Karte speichern
        Button btnLöschen    = new Button("Karte löschen");      // Karte löschen
        Button btnBearbeiten  = new Button("Karte bearbeiten");    // Karte bearbeiten

        // ============================================================
        // BUTTON-AKTIONEN
        // ============================================================

        // --- Neue Karte hinzufügen ---
        btnHinzufügen.setOnAction(e -> {
            String frage   = frageEingabe.getText().trim();   // Eingabe auslesen
            String antwort = antwortEingabe.getText().trim(); // Eingabe auslesen

            if (!frage.isEmpty() && !antwort.isEmpty()) { // Beide Felder benötigt
                datenbank.karteSpeichern(frage, antwort, aktuellesSet.getId()); // In DB speichern
                frageEingabe.clear();   // Frage-Feld leeren
                antwortEingabe.clear(); // Antwort-Feld leeren
                kartenListeAktualisieren(); // Liste aktualisieren
            } else {
                // Fehlermeldung wenn Felder leer
                new Alert(Alert.AlertType.WARNING,
                        "Bitte sowohl Frage als auch Antwort eingeben!").show();
            }
        });

        // --- Karte löschen ---
        btnLöschen.setOnAction(e -> {
            Lernkarte ausgewählt = kartenListe.getSelectionModel().getSelectedItem();
            if (ausgewählt != null) { // Nur wenn eine Karte ausgewählt ist
                datenbank.karteLöschen(ausgewählt.getId()); // Aus DB löschen
                kartenListeAktualisieren();                   // Liste aktualisieren
            } else {
                new Alert(Alert.AlertType.WARNING, "Bitte zuerst eine Karte auswählen!").show();
            }
        });

        // --- Karte bearbeiten - Felder mit aktuellen Werten füllen ---
        btnBearbeiten.setOnAction(e -> {
            Lernkarte ausgewählt = kartenListe.getSelectionModel().getSelectedItem();
            if (ausgewählt != null) {
                // Aktuelle Werte in die Felder laden
                frageEingabe.setText(ausgewählt.getFrage());
                antwortEingabe.setText(ausgewählt.getAntwort());

                // Den Hinzufügen-Button umfunktionieren zum Speichern
                btnHinzufügen.setText("Änderungen speichern");
                btnHinzufügen.setOnAction(speichernEvent -> {
                    String neueFrage   = frageEingabe.getText().trim();
                    String neueAntwort = antwortEingabe.getText().trim();

                    if (!neueFrage.isEmpty() && !neueAntwort.isEmpty()) {
                        // Karte in der DB aktualisieren
                        datenbank.karteAktualisieren(ausgewählt.getId(), neueFrage, neueAntwort);
                        frageEingabe.clear();                    // Felder leeren
                        antwortEingabe.clear();
                        btnHinzufügen.setText("Karte hinzufügen"); // Button zurücksetzen
                        kartenListeAktualisieren();              // Liste aktualisieren
                    }
                });
            } else {
                new Alert(Alert.AlertType.WARNING, "Bitte zuerst eine Karte auswählen!").show();
            }
        });

        // ============================================================
        // LAYOUT ZUSAMMENBAUEN
        // ============================================================

        // Eingabefelder in einem Tabellen-Layout anordnen
        GridPane eingabeBereich = new GridPane(); // Tabellen-Layout
        eingabeBereich.setHgap(10); // Horizontaler Abstand
        eingabeBereich.setVgap(8);  // Vertikaler Abstand
        eingabeBereich.add(frageLabel, 0, 0);    // Oben links: Frage-Label
        eingabeBereich.add(frageEingabe, 1, 0);  // Oben rechts: Frage-Feld
        eingabeBereich.add(antwortLabel, 0, 1);  // Unten links: Antwort-Label
        eingabeBereich.add(antwortEingabe, 1, 1);// Unten rechts: Antwort-Feld

        // Buttons nebeneinander
        HBox buttons = new HBox(8, btnHinzufügen, btnLöschen, btnBearbeiten);

        // Alles untereinander im Hauptlayout
        VBox layout = new VBox(12); // 12 Pixel Abstand
        layout.setPadding(new Insets(20)); // 20 Pixel Rand
        layout.getChildren().addAll(überschrift, kartenListe, eingabeBereich, buttons);

        // Fenster anzeigen
        Scene szene = new Scene(layout, 620, 520);
        fenster.setTitle("Karten verwalten - " + aktuellesSet.getName());
        fenster.setScene(szene);
        fenster.show();
    }

    /**
     * Lädt alle Karten des aktuellen Sets neu und aktualisiert die Liste.
     */
    private void kartenListeAktualisieren() {
        kartenDaten.clear(); // Alte Einträge löschen
        List<Lernkarte> karten = datenbank.kartenLadenFürSet(aktuellesSet.getId()); // Aus DB laden
        kartenDaten.addAll(karten); // Neue Einträge hinzufügen
    }
}
