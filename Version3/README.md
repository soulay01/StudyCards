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

---

## Nutzung von KI

Gemäß den Semestervorgaben wird die Nutzung von KI-Tools hier dokumentiert.

Im Verlauf des Projekts wurde ChatGPT (gpt-4o) eingesetzt. Die KI diente dabei als Sparringpartner beim Brainstorming und Verständnis von Konzepten – es wäre jedoch unehrlich zu behaupten, dass es dabei geblieben ist. Bei einzelnen, technisch anspruchsvolleren Abschnitten – insbesondere im Bereich JDBC-Anbindung und JavaFX-Eventhandling – wurde die KI auch konkret zur Umsetzung herangezogen, da diese Stellen ohne ihre Unterstützung in der gegebenen Zeit nicht realisierbar gewesen wären. Die Gesamtstruktur, das Design der Anwendung und der überwiegende Teil des Codes wurden eigenständig entwickelt und verstanden.
