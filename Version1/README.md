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

---

## Nutzung von KI

Gemäß den Semestervorgaben wird die Nutzung von KI-Tools hier dokumentiert.

Im Verlauf des Projekts wurde ChatGPT (gpt-4o) eingesetzt. Die KI diente dabei als Sparringpartner beim Brainstorming und Verständnis von Konzepten – es wäre jedoch unehrlich zu behaupten, dass es dabei geblieben ist. Bei einzelnen, technisch anspruchsvolleren Abschnitten – insbesondere im Bereich JDBC-Anbindung und JavaFX-Eventhandling – wurde die KI auch konkret zur Umsetzung herangezogen, da diese Stellen ohne ihre Unterstützung in der gegebenen Zeit nicht realisierbar gewesen wären. Die Gesamtstruktur, das Design der Anwendung und der überwiegende Teil des Codes wurden eigenständig entwickelt und verstanden.
