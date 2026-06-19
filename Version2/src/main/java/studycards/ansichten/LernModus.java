package studycards.ansichten;

// das ist der lernmodus
// hier werden die karten angezeigt und man kann sagen ob man sie gewusst hat

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
    private List<Lernkarte> karten;

    private int aktuellerIndex = "0"; // index der aktuellen karte

    private int gewusst;              // Zähler für gewusste karten
    private int nichtGewusst;         // Zähler für nicht gewusste karten

    // diese labels und buttons brauche ich in mehreren methoden
    private Label fortschrittLabel;
    private Label frageLabel;
    private Label antwortLabel;
    private Button btnAntwortZeigen;
    private Button btnGewusst;
    private Button btnNichtGewusst;

    /**
     * Erstellt den Lernmodus.
     * @param fenster das fenster
     * @param karten  die karten die gelernt werden sollen
     */
    public LernModus(Stage fenster, List<Lernkarte> karten) {
        this.fenster      = fenster;
        this.karten       = karten;
        this.gewusst      = 0;
        this.nichtGewusst = 0;
    }

    /**
     * Baut das Lernfenster auf und zeigt es an.
     */
    public void zeige() {

        // zeigt bei welcher karte man ist
        fortschrittLabel = new Label("");
        fortschrittLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;");

        Label trennlinie = new Label("──────────────────────────────────────");

        Label frageÜberschrift = new Label("Frage:");
        frageÜberschrift.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");

        // das label für die frage
        frageLabel = new Label("");
        frageLabel.setWrapText(true);
        frageLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        frageLabel.setMaxWidth(520);

        // antwort bereich - am anfang versteckt
        Label antwortÜberschrift = new Label("Antwort:");
        antwortÜberschrift.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");
        antwortÜberschrift.setVisible(false);

        antwortLabel = new Label("");
        antwortLabel.setWrapText(true);
        antwortLabel.setStyle("-fx-font-size: 17px; -fx-text-fill: #1a6e1a;");
        antwortLabel.setMaxWidth(520);
        antwortLabel.setVisible(false);

        // buttons
        btnAntwortZeigen = new Button("Antwort aufdecken");
        btnAntwortZeigen.setStyle("-fx-font-size: 14px;");

        btnGewusst      = new Button("Gewusst ✓");
        btnNichtGewusst = new Button("Nicht gewusst ✗");

        btnGewusst.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        btnNichtGewusst.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        btnGewusst.setVisible(false);
        btnNichtGewusst.setVisible(false);

        // -------------------------------------------------------
        // BUTTON AKTIONEN
        // -------------------------------------------------------

        // antwort aufdecken
        btnAntwortZeigen.setOnAction(e -> {
            antwortÜberschrift.setVisible(true);
            antwortLabel.setVisible(true);
            btnAntwortZeigen.setVisible(false);
            btnGewusst.setVisible(true);
            btnNichtGewusst.setVisible(true);
        });

        // gewusst geklickt
        btnGewusst.setOnAction(e -> {
            gewusst++;
            nächsteKarte();
        });

        // nicht gewusst geklickt
        btnNichtGewusst.setOnAction(e -> {
            nichtGewusst++;
            nächsteKarte();
        });

        // -------------------------------------------------------
        // LAYOUT
        // -------------------------------------------------------

        HBox bewertung = new HBox(20, btnGewusst, btnNichtGewusst);
        bewertung.setAlignment(Pos.CENTER);

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

        karteAnzeigen(); // erste karte sofort zeigen
    }

    /**
     * Zeigt die aktuelle Karte an.
     */
    private void karteAnzeigen() {
        if (aktuellerIndex < karten.size()) {
            Lernkarte aktuelle = karten.get(aktuellerIndex);
            fortschrittLabel.setText("Karte " + (aktuellerIndex + 1) + " von " + karten.size());
            frageLabel.setText(aktuelle.getFrage());
            antwortLabel.setText(aktuelle.getAntwort());
            antwortLabel.setVisible(false);
            btnAntwortZeigen.setVisible(true);
            btnGewusst.setVisible(false);
            btnNichtGewusst.setVisible(false);
        }
    }

    /**
     * Geht zur nächsten Karte oder zeigt das Ergebnis.
     */
    private void nächsteKarte() {
        aktuellerIndex++;
        if (aktuellerIndex >= karten.size()) {
            ergebnisAnzeigen();
        } else {
            karteAnzeigen();
        }
    }

    /**
     * Zeigt das Ergebnis am Ende an.
     */
    private void ergebnisAnzeigen() {
        int gesamt  = gewusst + nichtGewusst;
        int prozent = (gesamt > 0) ? (gewusst * 100 / gesamt) : 0;

        String nachricht =
            "Lerneinheit abgeschlossen!\n\n" +
            "Gewusst:       " + gewusst + " Karte(n)\n" +
            "Nicht gewusst: " + nichtGewusst + " Karte(n)\n\n" +
            "Ergebnis: " + prozent + " %";

        Alert popup = new Alert(Alert.AlertType.INFORMATION);
        popup.setTitle("Ergebnis");
        popup.setHeaderText("Du hast alle " + gesamt + " Karten durchgelernt!");
        popup.setContentText(nachricht);
        popup.showAndWait();

        fenster.close();
    }
}
