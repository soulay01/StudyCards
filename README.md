# StudyCards

Desktop-App zum Erstellen und Lernen mit Lernkarten. Entwickelt mit Java und JavaFX.

## Aktuelle Funktionen

- Lernsets anlegen, umbenennen und löschen
- Karten hinzufügen, bearbeiten und löschen
- Lernmodus mit Bewertung (gewusst / nicht gewusst)
- MySQL-Datenbankanbindung

## Voraussetzungen

- Java 17+
- Maven
- MySQL (Port 3306)

## Datenbank einrichten

```sql
CREATE DATABASE studycards;
```

Verbindungsdaten in `DatenbankManager.java` anpassen:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/studycards";
private static final String BENUTZER = "root";
private static final String PASSWORT = "";
```

## Starten

```bash
mvn javafx:run
```
