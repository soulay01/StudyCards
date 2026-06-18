# StudyCards – Version 2

Zweite Version der StudyCards-Anwendung.
Die Fehler aus Version 1 wurden größtenteils behoben, es gibt aber noch offene Probleme.

## Beschreibung

StudyCards ist eine Desktop-Anwendung zum Erstellen und Lernen mit digitalen Lernkarten.
Lernsets können angelegt werden, denen einzelne Karten (Frage + Antwort) zugeordnet werden.

## Aktueller Stand

- Grundlegende Klassen funktionieren
- Datenbankverbindung zu MySQL eingebaut
- Benutzeroberfläche größtenteils fertig
- Lernmodus in Arbeit
- Einige Fehler noch vorhanden

## Voraussetzungen

- Java 17 oder höher
- Maven
- MySQL-Datenbank (lokal installiert)

## Datenbank einrichten

```sql
CREATE DATABASE studycards;
```

Verbindungsdaten in `DatenbankManager.java` anpassen:
```java
private static final String BENUTZER = "root";
private static final String PASSWORT = "";
```

## Starten

```bash
mvn javafx:run
```

> Hinweis: Diese Version enthält noch Kompilierfehler die in Version 3 behoben werden.
