# ReactionMaster
![image](https://github.com/user-attachments/assets/98e1b274-1889-49a9-89a9-b39788117572)

Eine Spiel mit der man die Reactionszeit messen, vergleichen und verbessern kann


## Reflexion

### Ziel:
Das Ziel war eine App, bei der man die Reaction-Zeit spielerisch verbessern, messen und vergleichen kann. Es sollte einen Startbildschirm haben mit Login, Startknopf und einem Scoreboard-Knopf (Scoreboard nur, wenn eingeloggt). Beim Spiel sollten auf der Seite schwarze Balken erscheinen, und wenn man das Smartphone in die Richtung neigt, gibt es einen Punkt. Es sollte auch ein paar weitere Elemente geben, wie wenn das Smartphone vibriert, soll man es schütteln, oder wenn ein Knopf erscheint, muss man ihn drücken. Die Schwierigkeit bestand darin, dass es immer schneller wird, bis man es nicht mehr schafft, und sobald man es nicht mehr in der richtigen Zeitspanne schafft, wird man zu einem Game-Over-Screen weitergeleitet, wo einem der Highscore und der Score angezeigt werden. Falls man eingeloggt ist, wird der Score in Firestore gespeichert, sodass man ihn im Scoreboard betrachten kann. Wenn man allerdings kein Internet hat, wird der Service so lange warten, bis man wieder mit dem Internet verbunden ist und dann den Score speichern. Zurück zum Menü: Dort, wenn man sich per Google einloggt, soll der Knopf Scoreboard erscheinen. Ganz oben sieht man sich selbst, und darunter sind die Top 10 besten Spieler mit ihrem jeweiligen Score.

### Resultat/was fehlt noch?:
Das Resultat ist sehr nahe am Ziel; es hat sogar mehr, da ich mich während der Entwicklung entschieden habe, ein Tutorial hinzuzufügen, um die Nutzererfahrung zu verbessern. Das Einzige, was mich aktuell noch stört, ist, dass es nur ein Login, aber kein Logout gibt – man ist also in der App gefangen. Dafür habe ich hinzugefügt, dass, wenn man sich einloggt, der Highscore von Firestore (sofern verfügbar) synchronisiert wird.

### Persönliches Fazit + wie lief es:
Das Projekt war ein einziger riesiger Krampf. Ursprünglich wollte ich es in Flutter machen, da es mir eine komplett unbekannte Sprache war. Ich habe auch einen Prototypen, bei dem die Logik schon funktioniert hat, gemacht, doch leider hat Flutter nicht meine Anforderung erfüllt, dass die Reaction in 50 ms erfolgen soll; daher musste ich zu Kotlin wechseln. Anfangs lief alles super mit Kotlin; ich konnte sehr schnell Fortschritte erzielen, doch dann kam die Firebase-Integration, die mich komplett auseinander genommen hat. Ich war total über 4 Stunden nur mit dem Login beschäftigt, da die "automatische" Integration bei mir nicht funktionierte. Ich habe das lange nicht begriffen, bis ich dieses Tutorial gefunden habe https://www.youtube.com/watch?v=WFTsyOKMUKU&t=1050s, das mir dabei geholfen hat, Firebase zum Laufen zu bringen. Insgesamt bin ich mit meiner ersten Android-App zufrieden, da ich alle meine Anforderungen erfüllen konnte und in den 2 Wochen Kotlin lernen musste (ich habe allgemein schon fast keine Erfahrung mit Java, aber Kotlin war da auf einem anderen Level). Total kann man sagen, es hat Spass gemacht, aber die schlaflosen Nächte, in denen ich an der App gearbeitet habe, um die Deadline einzuhalten, haben dazu geführt, dass ich 2 neue Programmiersprachen (Kotlin + Dart) gelernt habe und auch, wie man eine App an sich entwickelt sowie Firebase integriert.
Wenn ich etwas anderes machen könnte, würde ich mir ein weniger komplexes Projekt suchen und dafür mehr Zeit in die Planung und Schlaf investieren. Mein Hauptproblem bei der Planung war, dass ich einseitig Flutter im Hinterkopf hatte und gleichzeitig nicht wirklich wusste, wie man eine so grosse App wirklich macht, da ich nicht wirklich Erfahrung mit Mobile-Apps hatte.


| Testfall-ID | Testfallbeschreibung | Vorbedingungen | Schritt für Schritt Vorgehen | Erwartetes Ergebnis | Resultat  |
| ----------- | -------------------- | -------------- | --------------------------- | -------------------- |--------------------|
| TF-001 | Spiel starten durch Klick auf „Start“ | App ist installiert | 1. App öffnen 2. Start-Button klicken | Das Spiel beginnt, und der Countdown beginnt zu zählen | ✅ |
| TF-002 | Neigen des Smartphones nach links | Spiel läuft | 1. Smartphone nach links neigen | Das System erkennt die Bewegung korrekt. | ✅ |
| TF-003 | Neigen des Smartphones nach rechts | Spiel läuft | 1. Smartphone nach rechts neigen | Das System erkennt die Bewegung korrekt. | ✅ |
| TF-004 | Neigen des Smartphones nach vorne | Spiel läuft | 1. Smartphone nach vorne neigen | Das System erkennt die Bewegung korrekt. |✅  |
| TF-005 | Neigen des Smartphones nach hinten | Spiel läuft | 1. Smartphone nach hinten neigen | Das System erkennt die Bewegung korrekt. |✅  |
| TF-006 | Schütteln des Smartphones | Spiel läuft | 1. Smartphone schütteln | Das System erkennt die Bewegung korrekt. | ✅ |
| TF-007 | Schütteln des Smartphones bei Vibration | Spiel läuft und vibriert | 1. Auf Vibration warten 2. Smartphone schütteln | Die Aktion wird innerhalb von 50 ms validiert. | ✅ |
| TF-008 | Highscore wird nach erfolgreichem Login synchronisiert | Login erfolgreich | 1. Einloggen 2. Spiel spielen 3. Highscore prüfen | Highscore erscheint im Online-Scoreboard. | ✅ |
| TF-009 | Drücken auf den Login-Button | App geöffnet | 1. Login-Button drücken 2. Google Login | Du kannst dich erfolgreich einloggen. | ✅ |


| Testfall-ID | Testfallbeschreibung | Vorbedingungen | Schritt für Schritt Vorgehen | Erwartetes Ergebnis | Resultat  |
| ----------- | ------------------- | -------------- | --------------------------- | -------------------- |--------------------|
| NF-001 | Sensordatenverarbeitung in Echtzeit | Spiel läuft | 1. Bewegung ausführen 2. Reaktionszeit messen | Feedback erfolgt in mindestens 95 % der Fälle innerhalb von 50 ms. |✅  |
| NF-002 | Offline-Modus testen | App ist im Offline-Modus | 1. Spiel starten 3. neuer Highscore im Spiel ereichen 4. app schliessen 5. app öffnen 6. scoreboard drücken 7. score validieren | App bleibt voll funktionsfähig, Highscore wird lokal gespeichert. | ✅ |
| NF-003 | Usability-Test mit Testpersonen | Testpersonen nutzen die App | 1. Testpersonen führen verschiedene Aktionen aus 2. Feedback sammeln | Mindestens 90 % der Tester bewerten die App als intuitiv. | ✅(testpersonen bürgis,Mugli) |






  ## linter:
  ./gradlew lint
