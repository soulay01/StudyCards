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

Bei der Entwicklung dieses Projekts wurde **Claude (Anthropic)** als KI-Assistent genutzt.

**Verwendete KI:** Claude Sonnet (claude.ai / Claude Code CLI)

**Wofür wurde die KI eingesetzt:**
- Unterstützung bei der Strukturierung des Projekts (Aufteilung in Model, Datenbank, Ansichten)
- Hilfe bei der JavaFX-API (z. B. `ObservableList`, `ListView`, `GridPane`)
- Hilfe bei der JDBC-Verbindung zu MySQL (`PreparedStatement`, `ResultSet`)
- Fehlersuche bei Kompilierfehlern
- Formulierung von Javadoc-Kommentaren

**Eigenständigkeit:**
- Der gesamte Code wurde verstanden, besprochen und angepasst
- Jede Zeile kann erklärt werden
- Keine ganzen Klassen wurden unverändert übernommen — alle wurden eigenständig durchgearbeitet

Gemäß den Vorgaben der Lehrveranstaltung wird die KI-Nutzung hier vollständig dokumentiert.
