# StudyCards

Lernkarten-App mit JavaFX und MySQL-Datenbank.

## Was neu dazugekommen ist

- Datenbankverbindung zu MySQL eingebaut
- Karten können jetzt gespeichert und geladen werden
- Benutzeroberfläche weiterentwickelt
- Lernmodus wird noch fertiggestellt

## Voraussetzungen

- Java 17 oder höher
- Maven
- MySQL (lokal)

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
