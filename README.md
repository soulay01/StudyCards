# StudyCards

Mein JavaFX-Projekt für die Vorlesung. Eine App um Lernkarten zu erstellen und zu lernen.

## Was bisher da ist

- Projektstruktur mit Maven angelegt
- Klassen für Lernkarte und Lernset erstellt
- Datenbankklasse für MySQL angefangen
- Hauptfenster und erste Ansichten begonnen

## Voraussetzungen

- Java 17+
- Maven
- MySQL (lokal installiert)

## Datenbank einrichten

```sql
CREATE DATABASE studycards;
```

Verbindungsdaten in `DatenbankManager.java` anpassen.

## Starten

```bash
mvn javafx:run
```
