package studycards.ansichten;

// ============================================================
// Der Lernmodus - hier lernt der Benutzer mit den Karten.
// Frage wird angezeigt, Antwort kann aufgedeckt werden,
// dann bewertet man ob man es gewusst hat oder nicht.
// ============================================================

import javafx.geometry.Insets;       // Für Ränder
import javafx.geometry.Pos;          // Für Ausrichtung (zentriert, links, ...)
import javafx.scene.Scene;           // Die Szene des Fensters
import javafx.scene.control.Alert;  // Für Popup-Nachrichten
import javafx.scene.control.Button; // Schaltflächen
import javafx.scene.control.Label;  // Text-Elemente
import javafx.scene.layout.HBox;    // Horizontales Layout
import javafx.scene.layout.VBox;    // Vertikales Layout
import javafx.stage.Stage;          // Das Fenster
import studycards.model.Lernkarte; // Die Lernkarten-Klasse

import java.util.List; // Für die Kartenliste

/**
 * Das Lernfenster - hier kann man die Karten eines Sets lernen.
 * Es wird eine Karte nach der anderen angezeigt.
 * Der Benutzer bewertet nach jeder Karte ob er es gewusst hat.
 */
public class LernModus {

    // ----- Felder -----
    private Stage fenster;              // Das Lernfenster
    private List<Lernkarte> karten;    // Die Karten die gelernt werden
    private int aktuellerIndex;        // Index der aktuellen Karte (0, 1, 2, ...)
    private int gewusst;               // Anzahl der Karten die man wusste
    private int nichtGewusst;          // Anzahl der Karten die man nicht wusste

    // Die UI-Elemente die wir in mehreren Methoden brauchen
    private Label fortschrittLabel;    // Zeigt "Karte 3 von 10"
    private Label frageLabel;          // Zeigt die Frage an
    private Label antwortLabel;        // Zeigt die Antwort an (erst versteckt)
    private Button btnAntwortZeigen;   // Button zum Aufdecken der Antwort
    private Button btnGewusst;         // "Ich habe es gewusst" Button
    private Button btnNichtGewusst;    // "Ich habe es nicht gewusst" Button

    /**
     * Erstellt den Lernmodus.
     * @param fenster Das Fenster
     * @param karten  Die Karten die gelernt werden sollen
     */
    public LernModus(Stage fenster, List<Lernkarte> karten) {
        this.fenster        = fenster;  // Fenster merken
        this.karten         = karten;  // Karten merken
        this.aktuellerIndex = 0;       // Bei der ersten Karte anfangen
        this.gewusst        = 0;       // Zähler auf Null
        this.nichtGewusst   = 0;       // Zähler auf Null
    }

    /**
     * Baut das Lernfenster auf und zeigt es an.
     */
    public void zeige() {

        // ----- Fortschrittsanzeige oben -----
        fortschrittLabel = new Label(""); // Wird gleich gesetzt
        fortschrittLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;"); // Grau und kleiner

        // ----- Trennlinie -----
        Label trennlinie = new Label("────────────────────────────────────"); // Optische Trennung

        // ----- Fragenbereich -----
        Label frageÜberschrift = new Label("Frage:"); // Kleine Überschrift
        frageÜberschrift.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");

        frageLabel = new Label(""); // Wird mit der Frage befüllt
        frageLabel.setWrapText(true);  // Zeilenumbruch wenn Text zu lang
        frageLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;"); // Gross und fett
        frageLabel.setMaxWidth(520);   // Maximale Breite damit Text umbricht

        // ----- Antwortbereich (am Anfang versteckt) -----
        Label antwortÜberschrift = new Label("Antwort:"); // Kleine Überschrift
        antwortÜberschrift.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");
        antwortÜberschrift.setVisible(false); // Anfangs unsichtbar

        antwortLabel = new Label(""); // Wird mit der Antwort befüllt
        antwortLabel.setWrapText(true); // Zeilenumbruch
        antwortLabel.setStyle("-fx-font-size: 17px; -fx-text-fill: #1a6e1a;"); // Grün
        antwortLabel.setMaxWidth(520);
        antwortLabel.setVisible(false); // Anfangs unsichtbar

        // ----- Buttons -----
        btnAntwortZeigen = new Button("Antwort aufdecken"); // Antwort zeigen
        btnAntwortZeigen.setStyle("-fx-font-size: 14px;");

        btnGewusst     = new Button("Gewusst ✓");           // Positiv-Button
        btnNichtGewusst = new Button("Nicht gewusst ✗");    // Negativ-Button

        // Farben für die Bewertungs-Buttons
        btnGewusst.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold;");
        btnNichtGewusst.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold;");

        // Bewertungs-Buttons anfangs verstecken
        btnGewusst.setVisible(false);
        btnNichtGewusst.setVisible(false);

        // ============================================================
        // BUTTON-AKTIONEN
        // ============================================================

        // --- Antwort aufdecken ---
        btnAntwortZeigen.setOnAction(e -> {
            antwortÜberschrift.setVisible(true); // "Antwort:"-Label anzeigen
            antwortLabel.setVisible(true);         // Die Antwort anzeigen
            btnAntwortZeigen.setVisible(false);    // Aufdecken-Button verstecken
            btnGewusst.setVisible(true);           // Bewertungs-Buttons anzeigen
            btnNichtGewusst.setVisible(true);
        });

        // --- Benutzer hat es gewusst ---
        btnGewusst.setOnAction(e -> {
            gewusst++;        // Gewusst-Zähler erhöhen
            nächsteKarte();  // Zur nächsten Karte gehen
        });

        // --- Benutzer hat es nicht gewusst ---
        btnNichtGewusst.setOnAction(e -> {
            nichtGewusst++;   // Nicht-gewusst-Zähler erhöhen
            nächsteKarte();  // Zur nächsten Karte gehen
        });

        // ============================================================
        // LAYOUT ZUSAMMENBAUEN
        // ============================================================

        // Bewertungs-Buttons nebeneinander
        HBox bewertung = new HBox(20, btnGewusst, btnNichtGewusst); // 20px Abstand
        bewertung.setAlignment(Pos.CENTER); // Zentriert ausrichten

        // Alles untereinander
        VBox layout = new VBox(16); // 16 Pixel Abstand zwischen Elementen
        layout.setPadding(new Insets(30)); // 30 Pixel Rand rundum
        layout.setAlignment(Pos.CENTER);   // Alles zentrieren
        layout.getChildren().addAll(
                fortschrittLabel,      // Fortschritt ganz oben
                trennlinie,            // Linie
                frageÜberschrift,     // "Frage:"-Label
                frageLabel,            // Die eigentliche Frage
                btnAntwortZeigen,      // Button zum Aufdecken
                antwortÜberschrift,   // "Antwort:"-Label
                antwortLabel,          // Die eigentliche Antwort
                bewertung              // Gewusst/Nicht-gewusst Buttons
        );

        // Fenster anzeigen
        Scene szene = new Scene(layout, 620, 430); // Fenstergrösse
        fenster.setTitle("StudyCards - Lernmodus");
        fenster.setScene(szene);
        fenster.show();

        // Die erste Karte anzeigen
        karteAnzeigen();
    }

    /**
     * Zeigt die Karte am aktuellen Index an.
     */
    private void karteAnzeigen() {
        if (aktuellerIndex < karten.size()) { // Noch Karten vorhanden?
            Lernkarte aktuelle = karten.get(aktuellerIndex); // Aktuelle Karte holen

            // Fortschritt aktualisieren
            fortschrittLabel.setText("Karte " + (aktuellerIndex + 1) + " von " + karten.size());

            // Frage setzen und anzeigen
            frageLabel.setText(aktuelle.getFrage());

            // Antwort schon mal eintragen aber versteckt lassen
            antwortLabel.setText(aktuelle.getAntwort());
            antwortLabel.setVisible(false);        // Antwort verstecken
            btnAntwortZeigen.setVisible(true);     // Aufdecken-Button zeigen
            btnGewusst.setVisible(false);          // Bewertungs-Buttons verstecken
            btnNichtGewusst.setVisible(false);
        }
    }

    /**
     * Geht zur nächsten Karte oder beendet den Lernmodus.
     */
    private void nächsteKarte() {
        aktuellerIndex++; // Eine Karte weiter

        if (aktuellerIndex >= karten.size()) {
            // Alle Karten durchgegangen - Ergebnis anzeigen
            ergebnisAnzeigen();
        } else {
            karteAnzeigen(); // Nächste Karte anzeigen
        }
    }

    /**
     * Zeigt das Lernergebnis am Ende an.
     */
    private void ergebnisAnzeigen() {
        // Prozentsatz berechnen
        int gesamt  = gewusst + nichtGewusst; // Gesamtzahl der Karten
        int prozent = (gesamt > 0) ? (gewusst * 100 / gesamt) : 0; // Prozentwert

        // Text für das Ergebnis-Popup
        String nachricht =
                "Lerneinheit abgeschlossen!\n\n" +
                "Gewusst:       " + gewusst + " Karte(n)\n" +
                "Nicht gewusst: " + nichtGewusst + " Karte(n)\n\n" +
                "Ergebnis: " + prozent + " %";

        // Popup-Fenster mit dem Ergebnis
        Alert ergebnisPopup = new Alert(Alert.AlertType.INFORMATION);
        ergebnisPopup.setTitle("Lernergebnis");
        ergebnisPopup.setHeaderText("Du hast alle " + gesamt + " Karten durchgelernt!");
        ergebnisPopup.setContentText(nachricht);
        ergebnisPopup.showAndWait(); // Warten bis Benutzer OK geklickt hat

        fenster.close(); // Lernfenster schliessen
    }
}
