# StudyCards – Version 3

Dritte Version der StudyCards-Anwendung.
Die Fehler aus Version 2 wurden behoben, es gibt aber noch letzte kleinere Probleme.

## Beschreibung

StudyCards ist eine Desktop-Anwendung zum Erstellen, Verwalten und Lernen mit digitalen Lernkarten.
Der Benutzer kann Lernsets anlegen, Karten hinzufügen und im Lernmodus die Karten abfragen.

## Aktueller Stand

- Modell-Klassen (Lernkarte, Lernset) fertig
- Datenbankanbindung (MySQL) fertig
- Hauptfenster mit Lernset-Übersicht fertig
- Kartenverwaltung fertig
- Lernmodus fast fertig
- Letzte Fehler werden in Version 4 behoben

## Voraussetzungen

- Java 17 oder höher
- Maven
- MySQL (lokal, Port 3306)

## Datenbank einrichten

```sql
CREATE DATABASE studycards;
```

Verbindungsdaten in `src/main/java/studycards/datenbank/DatenbankManager.java` anpassen:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/studycards";
private static final String BENUTZER = "root";    // Benutzername anpassen
private static final String PASSWORT = "";         // Passwort anpassen
```

## Starten

```bash
mvn javafx:run
```

> Hinweis: Diese Version kompiliert noch nicht vollständig fehlerfrei.
> Die finale lauffähige Version ist Version 4.
