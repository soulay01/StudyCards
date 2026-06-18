package studycards.ansichten;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import studycards.model.Lernkarte;

import java.util.List;

/**
 * Das Lernfenster - hier lernt der Benutzer mit den Karten.
 * Es wird eine Karte nach der anderen angezeigt.
 * Nach dem Aufdecken der Antwort bewertet man ob man es gewusst hat.
 */
public class LernModus {

    // ----- Felder -----
    private Stage fenster;              // Das Lernfenster
    private List<Lernkarte> karten;    // Alle Karten die gelernt werden
    private int aktuellerIndex;        // Welche Karte gerade angezeigt wird (0 = erste)
    private int gewusst;               // Wie viele Karten man gewusst hat
    private int nichtGewusst;          // Wie viele man nicht gewusst hat

    // UI-Elemente (werden in zeige() erstellt, in karteAnzeigen() verwendet)
    private Label fortschrittLabel;     // Zeigt "Karte 2 von 10"
    private Label frageLabel;           // Zeigt die Frage
    private Label antwortLabel;         // Zeigt die Antwort (erst versteckt)
    private Button btnAntwortZeigen;    // Zum Aufdecken der Antwort
    private Button btnGewusst;          // "Ich habe es gewusst"
    private Button btnNichtGewusst;     // "Ich habe es nicht gewusst"

    /**
     * Erstellt den Lernmodus.
     * @param fenster Das Fenster
     * @param karten  Die Karten die gelernt werden sollen
     */
    public LernModus(Stage fenster, List<Lernkarte> karten) {
        this.fenster        = fenster; // Fenster speichern
        this.karten         = karten; // Karten speichern
        this.aktuellerIndex = 0;      // Bei der ersten Karte anfangen
        this.gewusst        = 0;      // Zähler auf Null setzen
        this.nichtGewusst   = 0;      // Zähler auf Null setzen
    }

    /**
     * Baut das Lernfenster auf und zeigt es an.
     */
    public void zeige() {

        // ----- Fortschrittsanzeige -----
        fortschrittLabel = new Label(""); // Wird gleich mit Inhalt befüllt
        fortschrittLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;"); // Grau und kleiner

        // Optische Trennlinie
        Label trennlinie = new Label("────────────────────────────────────");

        // ----- Fragenbereich -----
        Label frageÜberschrift = new Label("Frage:"); // Kleine Überschrift über der Frage
        frageÜberschrift.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");

        frageLabel = new Label(""); // Hier kommt die eigentliche Frage rein
        frageLabel.setWrapText(true);  // Zeilenumbruch aktivieren
        frageLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;"); // Gross und fett
        frageLabel.setMaxWidth(520);   // Maximale Breite

        // ----- Antwortbereich (anfangs unsichtbar) -----
        Label antwortÜberschrift = new Label("Antwort:"); // Kleine Überschrift
        antwortÜberschrift.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");
        antwortÜberschrift.setVisible(false); // Erst versteckt

        antwortLabel = new Label(""); // Hier kommt die Antwort rein
        antwortLabel.setWrapText(true);
        antwortLabel.setStyle("-fx-font-size: 17px; -fx-text-fill: #1a6e1a;"); // Grün
        antwortLabel.setMaxWidth(520);
        antwortLabel.setVisible(false); // Erst versteckt

        // ----- Buttons -----
        btnAntwortZeigen = new Button("Antwort aufdecken"); // Zum Aufdecken der Antwort
        btnAntwortZeigen.setStyle("-fx-font-size: 14px;");

        btnGewusst      = new Button("Gewusst ✓");       // Positiver Button
        btnNichtGewusst = new Button("Nicht gewusst ✗"); // Negativer Button

        // Farben für die Bewertungs-Buttons
        btnGewusst.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold;"); // Grün
        btnNichtGewusst.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold;"); // Rot

        // Bewertungs-Buttons anfangs verstecken
        btnGewusst.setVisible(false);
        btnNichtGewusst.setVisible(false);

        // ============================================================
        // BUTTON-AKTIONEN
        // ============================================================

        // --- Antwort aufdecken ---
        btnAntwortZeigen.setOnAction(e -> {
            antwortÜberschrift.setVisible(true); // "Antwort:" anzeigen
            antwortLabel.setVisible(true);         // Antworttext anzeigen
            btnAntwortZeigen.setVisible(false);    // Aufdecken-Button verstecken
            btnGewusst.setVisible(true);           // Bewertungs-Buttons zeigen
            btnNichtGewusst.setVisible(true);
        });

        // --- Gewusst geklickt ---
        btnGewusst.setOnAction(e -> {
            gewusst++;       // Gewusst-Zähler um 1 erhöhen
            nächsteKarte(); // Nächste Karte laden
        });

        // --- Nicht gewusst geklickt ---
        btnNichtGewusst.setOnAction(e -> {
            nichtGewusst++;  // Nicht-gewusst-Zähler um 1 erhöhen
            nächsteKarte(); // Nächste Karte laden
        });

        // ============================================================
        // LAYOUT ZUSAMMENBAUEN
        // ============================================================

        HBox bewertung = new HBox(20, btnGewusst, btnNichtGewusst); // Buttons nebeneinander
        bewertung.setAlignment(Pos.CENTER); // Zentriert ausrichten

        VBox layout = new VBox(16); // Alle Elemente untereinander
        layout.setPadding(new Insets(30)); // 30px Rand
        layout.setAlignment(Pos.CENTER);   // Alles zentrieren
        layout.getChildren().addAll(
                fortschrittLabel,      // Fortschrittsanzeige
                trennlinie,            // Linie
                frageÜberschrift,     // "Frage:"-Label
                frageLabel,            // Die Frage
                btnAntwortZeigen,      // Aufdecken-Button
                antwortÜberschrift,   // "Antwort:"-Label
                antwortLabel,          // Die Antwort
                bewertung              // Bewertungs-Buttons
        );

        Scene szene = new Scene(layout, 620, 430);
        fenster.setTitle("StudyCards - Lernmodus");
        fenster.setScene(szene);
        fenster.show();

        karteAnzeigen(); // Erste Karte anzeigen
    }

    /**
     * Zeigt die Karte am aktuellen Index an und setzt alle Elemente zurück.
     */
    private void karteAnzeigen() {
        if (aktuellerIndex < karten.size()) { // Noch Karten vorhanden?
            Lernkarte aktuelle = karten.get(aktuellerIndex); // Aktuelle Karte holen

            // Fortschrift anzeigen
            fortschrittLabel.setText("Karte " + (aktuellerIndex + 1) + " von " + karten.size());

            // Frage anzeigen, Antwort verstecken
            frageLabel.setText(aktuelle.getFrage());     // Frage setzen
            antwortLabel.setText(aktuelle.getAntwort()); // Antwort schon mal setzen (aber versteckt)
            antwortLabel.setVisible(false);               // Antwort verstecken
            btnAntwortZeigen.setVisible(true);            // Aufdecken-Button zeigen
            btnGewusst.setVisible(false);                 // Bewertungs-Buttons noch verstecken
            btnNichtGewusst.setVisible(false);
        }
    }

    /**
     * Geht zur nächsten Karte oder zeigt das Endergebnis.
     */
    private void nächsteKarte() {
        aktuellerIndex++; // Index um 1 erhöhen - nächste Karte

        if (aktuellerIndex >= karten.size()) {
            ergebnisAnzeigen(); // Alle Karten durch - Ergebnis zeigen
        } else {
            karteAnzeigen(); // Nächste Karte anzeigen
        }
    }

    /**
     * Berechnet und zeigt das Lernergebnis am Ende an.
     */
    private void ergebnisAnzeigen() {
        int gesamt  = gewusst + nichtGewusst;                          // Gesamtzahl
        int prozent = (gesamt > 0) ? (gewusst * 100 / gesamt) : 0;    // Prozentwert berechnen

        // Ergebnistext zusammenstellen
        String nachricht =
                "Lerneinheit abgeschlossen!\n\n" +
                "Gewusst:       " + gewusst + " Karte(n)\n" +
                "Nicht gewusst: " + nichtGewusst + " Karte(n)\n\n" +
                "Ergebnis: " + prozent + " %";

        // Popup mit dem Ergebnis anzeigen
        Alert ergebnisPopup = new Alert(Alert.AlertType.INFORMATION);
        ergebnisPopup.setTitle("Lernergebnis");
        ergebnisPopup.setHeaderText("Du hast alle " + gesamt + " Karten durchgelernt!");
        ergebnisPopup.setContentText(nachricht);
        ergebnisPopup.showAndWait(); // Warten bis Benutzer OK klickt

        fenster.close(); // Lernfenster schliessen
    }
}
