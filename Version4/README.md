# StudyCards

Eine Desktop-Anwendung zur Erstellung, Verwaltung und Nutzung digitaler Lernkarten.
Entwickelt als Semesterprojekt im Kurs „Java Development with JavaFX".

---

## Kurzbeschreibung

StudyCards ermöglicht es, Lernsets mit Karten (Frage + Antwort) anzulegen und diese
im Lernmodus interaktiv abzufragen. Nach jeder Karte bewertet der Benutzer ob er die
Antwort gewusst hat oder nicht. Am Ende wird ein Ergebnis angezeigt.

---

## Funktionen

**Verwaltung**
- Lernsets erstellen, umbenennen und löschen
- Karten einem Lernset hinzufügen, bearbeiten und löschen

**Lernmodus**
- Frage anzeigen
- Antwort aufdecken
- Bewertung: „Gewusst" oder „Nicht gewusst"
- Ergebnisanzeige am Ende

---

## Technische Anforderungen

| Technologie | Version  |
|-------------|----------|
| Java        | 17+      |
| JavaFX      | 21.0.2   |
| Maven       | 3.x      |
| Datenbank   | MySQL 8  |

---

## Projektstruktur

```
src/main/java/studycards/
├── Main.java                        # Einstiegspunkt der Anwendung
├── model/
│   ├── Lernkarte.java               # Datenmodell: eine Lernkarte (Frage + Antwort)
│   └── Lernset.java                 # Datenmodell: eine Sammlung von Karten
├── datenbank/
│   └── DatenbankManager.java        # MySQL-Verbindung und alle CRUD-Operationen
└── ansichten/
    ├── HauptFenster.java            # Übersicht aller Lernsets
    ├── KartenVerwaltung.java        # Karten eines Sets verwalten
    └── LernModus.java               # Lernmodus mit Bewertung
```

---

## Datenbank einrichten (MySQL)

1. MySQL starten
2. Datenbank anlegen:

```sql
CREATE DATABASE studycards;
```

3. Verbindungsdaten in `DatenbankManager.java` anpassen:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/studycards";
private static final String BENUTZER = "root";    // eigenen Benutzernamen eintragen
private static final String PASSWORT = "";         // eigenes Passwort eintragen
```

Die Tabellen (`lernsets` und `lernkarten`) werden beim ersten Start automatisch erstellt.

---

## Starten

```bash
# Im Projektordner (Version4/)
mvn javafx:run
```

---

## Javadoc generieren

```bash
mvn javadoc:javadoc
```

Die generierte Dokumentation liegt danach unter `target/site/apidocs/index.html`.

---

## Nutzung von KI

Gemäß den Vorgaben der Lehrveranstaltung dokumentiere ich hier wo ich KI-Tools eingesetzt habe.

**Genutzte Tools:** ChatGPT (gpt-4o), gelegentlich auch Claude (claude.ai)

---

KI habe ich hauptsächlich als eine Art "schlauerer Suchmotor" benutzt, nicht zum Coden:

**Brainstorming am Anfang**
Bevor ich angefangen habe zu coden habe ich ChatGPT gefragt was man in einer Lernkarten-App alles einbauen könnte und wie man das grob aufteilen könnte (Model, Datenbank, Ansichten). Das hat mir geholfen einen ersten Überblick zu bekommen. Die eigentliche Umsetzung habe ich dann selbst gemacht.

**JavaFX-Fragen**
JavaFX hat am Anfang einige Fragezeichen hinterlassen, zum Beispiel wie `ObservableList` funktioniert und warum man die braucht statt einer normalen Liste. Da habe ich kurz nachgefragt anstatt ewig in der Doku zu suchen.

**Ein spezifischer Bug (Version 1 → Version 2)**
In Version 1 hat die Datenbankverbindung einfach nicht funktioniert obwohl der Code eigentlich richtig aussah. Ich habe bestimmt eine Stunde damit verbracht den Fehler zu suchen. Irgendwann habe ich den Code bei ChatGPT eingefügt und gefragt wo der Fehler ist — es stellte sich heraus dass ich `Connection` als `Conection` geschrieben hatte (ein `n` zu wenig im Import). Das hätte ich alleine auch irgendwann gefunden aber es hat einfach zu viel Zeit gekostet.

---

Den gesamten Code habe ich selbst geschrieben. Ich habe keine fertigen Code-Blöcke übernommen und kann jede Zeile erklären.
