package studycards.ansichten;

// das ist der lernmodus
// hier werden die karten eine nach der anderen angezeigt
// man sieht zuerst die frage, dann kann man die antwort aufdecken
// am ende sieht man wie viele man gewusst hat

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
 * Der Lernmodus zeigt die Karten nacheinander an.
 * Man kann die Antwort aufdecken und dann sagen ob man es gewusst hat.
 */
public class LernModus {

    // -- variablen --
    private Stage fenster;
    private List<Lernkarte> karten;  // alle karten die gelernt werden sollen
    private int aktuellerIndex;      // welche karte gerade angezeigt wird, fängt bei 0 an
    private int gewusst;             // Zähler für karten die man gewusst hat
    private int nichtGewusst;        // Zähler für karten die man nicht gewusst hat

    // diese ui-elemente brauche ich in mehreren methoden deshalb sind sie hier oben
    private Label fortschrittLabel;  // zeigt "karte 1 von 10"
    private Label frageLabel;        // zeigt die frage
    private Label antwortLabel;      // zeigt die antwort wenn sie aufgedeckt wird
    private Button btnAntwortZeigen; // button zum aufdecken der antwort
    private Button btnGewusst;       // "habe ich gewusst"
    private Button btnNichtGewusst;  // "habe ich nicht gewusst"

    /**
     * Erstellt den Lernmodus.
     * @param fenster das fenster für den lernmodus
     * @param karten  die karten die gelernt werden sollen
     */
    public LernModus(Stage fenster, List<Lernkarte> karten) {
        this.fenster        = fenster;
        this.karten         = karten;
        this.aktuellerIndex = 0; // bei der ersten karte anfangen
        this.gewusst        = 0; // Zähler auf null
        this.nichtGewusst   = 0; // Zähler auf null
    }

    /**
     * Baut das Lernfenster auf und zeigt es an.
     */
    public void zeige() {

        // zeigt an bei welcher karte man gerade ist
        fortschrittLabel = new Label("");
        fortschrittLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;");

        // trennlinie
        Label trennlinie = new Label("──────────────────────────────────────");

        // Überschrift "Frage:"
        Label frageÜberschrift = new Label("Frage:");
        frageÜberschrift.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");

        // das label das die frage anzeigt
        frageLabel = new Label("");
        frageLabel.setWrapText(true);
        frageLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        frageLabel.setMaxWidth(520);

        // Überschrift "Antwort:" - am anfang versteckt
        Label antwortÜberschrift = new Label("Antwort:");
        antwortÜberschrift.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");
        antwortÜberschrift.setVisible(false); // erst verstecken

        // das label das die antwort anzeigt - am anfang auch versteckt
        antwortLabel = new Label("");
        antwortLabel.setWrapText(true);
        antwortLabel.setStyle("-fx-font-size: 17px; -fx-text-fill: #1a6e1a;"); // grün damit es auffällt
        antwortLabel.setMaxWidth(520);
        antwortLabel.setVisible(false); // erst verstecken

        // button zum aufdecken
        btnAntwortZeigen = new Button("Antwort aufdecken");
        btnAntwortZeigen.setStyle("-fx-font-size: 14px;");

        // bewertungs-buttons
        btnGewusst      = new Button("Gewusst ✓");
        btnNichtGewusst = new Button("Nicht gewusst ✗");

        // farben für die buttons
        btnGewusst.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        btnNichtGewusst.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        // die bewertungs-buttons am anfang auch verstecken
        btnGewusst.setVisible(false);
        btnNichtGewusst.setVisible(false);

        // -------------------------------------------------------
        // BUTTON AKTIONEN
        // -------------------------------------------------------

        // antwort aufdecken - label sichtbar machen und buttons tauschen
        btnAntwortZeigen.setOnAction(e -> {
            antwortÜberschrift.setVisible(true); // "antwort:" anzeigen
            antwortLabel.setVisible(true);        // antwort anzeigen
            btnAntwortZeigen.setVisible(false);   // aufdecken-button verstecken
            btnGewusst.setVisible(true);          // bewertungs-buttons anzeigen
            btnNichtGewusst.setVisible(true);
        });

        // wenn der nutzer "gewusst" drückt
        btnGewusst.setOnAction(e -> {
            gewusst++;       // Zähler erhöhen
            nächsteKarte(); // nächste karte laden
        });

        // wenn der nutzer "nicht gewusst" drückt
        btnNichtGewusst.setOnAction(e -> {
            nichtGewusst++;  // Zähler erhöhen
            nächsteKarte(); // nächste karte laden
        });

        // -------------------------------------------------------
        // LAYOUT
        // -------------------------------------------------------

        // bewertungs-buttons nebeneinander und zentriert
        HBox bewertung = new HBox(20, btnGewusst, btnNichtGewusst);
        bewertung.setAlignment(Pos.CENTER);

        // alles untereinander und zentriert
        VBox layout = new VBox(16);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(
            fortschrittLabel,
            trennlinie,
            frageÜberschrift,
            frageLabel,
            btnAntwortZeigen,
            antwortÜberschrift,
            antwortLabel,
            bewertung
        );

        Scene szene = new Scene(layout, 620, 430);
        fenster.setTitle("StudyCards - Lernmodus");
        fenster.setScene(szene);
        fenster.show();

        karteAnzeigen(); // erst Karte sofort anzeigen
    }

    /**
     * Zeigt die aktuelle Karte an und setzt die Ansicht zurück.
     */
    private void karteAnzeigen() {
        if (aktuellerIndex < karten.size()) {
            Lernkarte aktuelle = karten.get(aktuellerIndex); // aktuelle karte holen

            // fortschritt aktualisieren
            fortschrittLabel.setText("Karte " + (aktuellerIndex + 1) + " von " + karten.size());

            // frage anzeigen und antwort wieder verstecken
            frageLabel.setText(aktuelle.getFrage());
            antwortLabel.setText(aktuelle.getAntwort()); // antwort schon setzen aber noch nicht zeigen
            antwortLabel.setVisible(false);
            btnAntwortZeigen.setVisible(true); // aufdecken-button wieder anzeigen
            btnGewusst.setVisible(false);      // bewertungs-buttons wieder verstecken
            btnNichtGewusst.setVisible(false);
        }
    }

    /**
     * Geht zur nächsten Karte oder zeigt das Ergebnis wenn alle durch sind.
     */
    private void nächsteKarte() {
        aktuellerIndex++; // nächste karte

        if (aktuellerIndex >= karten.size()) {
            ergebnisAnzeigen(); // alle karten durch, ergebnis zeigen
        } else {
            karteAnzeigen(); // nächste karte anzeigen
        }
    }

    /**
     * Zeigt ein Popup mit dem Ergebnis am Ende des Lernens.
     */
    private void ergebnisAnzeigen() {
        int gesamt  = gewusst + nichtGewusst;
        int prozent = (gesamt > 0) ? (gewusst * 100 / gesamt) : 0; // prozent ausrechnen

        // ergebnistext zusammenstellen
        String nachricht =
            "Lerneinheit abgeschlossen!\n\n" +
            "Gewusst:       " + gewusst + " Karte(n)\n" +
            "Nicht gewusst: " + nichtGewusst + " Karte(n)\n\n" +
            "Ergebnis: " + prozent + " %";

        // popup mit dem ergebnis anzeigen
        Alert popup = new Alert(Alert.AlertType.INFORMATION);
        popup.setTitle("Ergebnis");
        popup.setHeaderText("Du hast alle " + gesamt + " Karten durchgelernt!");
        popup.setContentText(nachricht);
        popup.showAndWait(); // warten bis ok geklickt wird

        fenster.close(); // lernfenster schliessen
    }
}
