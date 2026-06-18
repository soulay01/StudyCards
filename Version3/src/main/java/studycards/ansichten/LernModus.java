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
 * Das Lernfenster - hier lernt man mit den Karten.
 */
public class LernModus {

    // ----- Felder -----
    private Stage fenster;
    private List<Lernkarte> karten;
    private int aktuellerIndex = 0; // Jetzt richtig: int ohne Anführungszeichen
    private int gewusst;
    private int nichtGewusst;

    // UI-Elemente
    private Label fortschrittLabel;
    private Label frageLabel;
    private Label antwortLabel;
    private Button btnAntwortZeigen;
    private Button btnGewusst;
    private Button btnNichtGewusst;

    /**
     * Erstellt den Lernmodus.
     * @param fenster Das Fenster
     * @param karten  Die Karten zum Lernen
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

        fortschrittLabel = new Label("");
        fortschrittLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;");

        Label trennlinie = new Label("────────────────────────────────────");

        Label frageÜberschrift = new Label("Frage:");
        frageÜberschrift.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");

        frageLabel = new Label("");
        frageLabel.setWrapText(true);
        frageLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        frageLabel.setMaxWidth(520);

        Label antwortÜberschrift = new Label("Antwort:");
        antwortÜberschrift.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");
        antwortÜberschrift.setVisible(false);

        antwortLabel = new Label("");
        antwortLabel.setWrapText(true);
        antwortLabel.setStyle("-fx-font-size: 17px; -fx-text-fill: #1a6e1a;");
        antwortLabel.setMaxWidth(520);
        antwortLabel.setVisible(false);

        btnAntwortZeigen = new Button("Antwort aufdecken");
        btnAntwortZeigen.setStyle("-fx-font-size: 14px;");

        btnGewusst      = new Button("Gewusst ✓");
        btnNichtGewusst = new Button("Nicht gewusst ✗");

        btnGewusst.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        btnNichtGewusst.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        btnGewusst.setVisible(false);
        btnNichtGewusst.setVisible(false);

        btnAntwortZeigen.setOnAction(e -> {
            antwortÜberschrift.setVisible(true);
            antwortLabel.setVisible(true);
            btnAntwortZeigen.setVisible(false);
            btnGewusst.setVisible(true);
            btnNichtGewusst.setVisible(true);
        });

        btnGewusst.setOnAction(e -> {
            gewusst++;
            nächsteKarte();
        });

        btnNichtGewusst.setOnAction(e -> {
            nichtGewusst++;
            nächsteKarte();
        });

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

        karteAnzeigen(); // Erste Karte zeigen
    }

    @Override
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
     * Geht zur nächsten Karte.
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
     * Zeigt das Lernergebnis an.
     */
    private void ergebnisAnzeigen() {
        int gesamt  = gewusst + nichtGewusst;
        int prozent = (gesamt > 0) ? (gewusst * 100 / gesamt) : 0;

        String nachricht =
                "Lerneinheit abgeschlossen!\n\n" +
                "Gewusst:       " + gewusst + " Karte(n)\n" +
                "Nicht gewusst: " + nichtGewusst + " Karte(n)\n\n" +
                "Ergebnis: " + prozent + " %";

        Alert ergebnisPopup = new Alert(Alert.AlertType.INFORMATION);
        ergebnisPopup.setTitle("Lernergebnis");
        ergebnisPopup.setHeaderText("Du hast alle " + gesamt + " Karten durchgelernt!");
        ergebnisPopup.setContentText(nachricht);
        ergebnisPopup.showAndWait();

        fenster.close();
    }
}
