# ReactionMaster
![image](https://github.com/user-attachments/assets/98e1b274-1889-49a9-89a9-b39788117572)

A game for measuring, comparing and improving reaction time.

## Reflections

### Objective
The goal was an app where you can playfully improve, measure and compare your reaction time. It should have a start screen with login, a “Start” button and a “Scoreboard” button (the scoreboard only appears when logged in). During the game, black bars appear on the side, and when you tilt the smartphone in that direction you earn a point. There should also be a few other elements: when the phone vibrates, you have to shake it; when a button appears, you have to press it. The difficulty increases continuously until you can no longer keep up, and as soon as you fail to react within the required time window you are taken to a Game-Over screen showing your score and high score. If you’re logged in, the score is saved to Firestore so you can view it on the scoreboard. If you have no internet connection, the service waits until you’re back online and then saves the score. Back on the menu: when you log in with Google, the “Scoreboard” button appears. At the top you see your own avatar, and below are the Top 10 players with their respective scores.

### Result / What’s Still Missing?
The result is very close to the goal; in fact it has more, because during development I decided to add a tutorial to improve the user experience. The only thing that still bothers me is that there is a login but no logout—you’re stuck in the app. To compensate, I added synchronization of the high score from Firestore (if available) upon login.

### Personal Conclusion & How It Went
The project was one huge struggle. Originally I wanted to do it in Flutter since it was completely new to me. I even built a prototype with working logic, but unfortunately Flutter didn’t meet my requirement of a 50 ms reaction time; so I had to switch to Kotlin. At first everything went great with Kotlin; I was able to make rapid progress, but then came the Firebase integration, which completely threw me off. I spent over four hours just on the login, because the “automatic” integration wouldn’t work for me. I didn’t understand it for a long time until I found this tutorial, which helped me get Firebase up and running:  
https://www.youtube.com/watch?v=WFTsyOKMUKU

Overall I’m satisfied with my first Android app, since I was able to meet all my requirements and learn Kotlin in just two weeks (I had almost no Java experience, and Kotlin was on another level). All in all, it was fun, but the sleepless nights working on the app to meet the deadline taught me two new programming languages (Kotlin + Dart) and how to develop an app and integrate Firebase. If I could do it again, I’d choose a less complex project so I could spend more time planning and sleeping. My main planning issue was that I had Flutter in mind from the start, and at the same time I didn’t really know how to build such a large app because I had no real experience with mobile apps.

| Test Case ID | Description                                    | Preconditions   | Step-by-Step Procedure                     | Expected Result                                                  | Result |
| ------------ | ---------------------------------------------- | --------------- | ------------------------------------------ | ---------------------------------------------------------------- | ------ |
| TF-001       | Start game by clicking “Start”                 | App is installed| 1. Open app  2. Click “Start”              | Game starts and countdown begins                                 | ✅     |
| TF-002       | Tilt smartphone to the left                    | Game is running | 1. Tilt smartphone to the left             | System correctly detects the movement                            | ✅     |
| TF-003       | Tilt smartphone to the right                   | Game is running | 1. Tilt smartphone to the right            | System correctly detects the movement                            | ✅     |
| TF-004       | Tilt smartphone forward                        | Game is running | 1. Tilt smartphone forward                 | System correctly detects the movement                            | ✅     |
| TF-005       | Tilt smartphone backward                       | Game is running | 1. Tilt smartphone backward                | System correctly detects the movement                            | ✅     |
| TF-006       | Shake the smartphone                           | Game is running | 1. Shake the smartphone                    | System correctly detects the movement                            | ✅     |
| TF-007       | Shake when the phone vibrates                  | Game is running and vibrating | 1. Wait for vibration  2. Shake the phone | Action validated within 50 ms                                     | ✅     |
| TF-008       | High score syncs after successful login        | Logged in       | 1. Log in  2. Play game  3. Check high score | High score appears in the online scoreboard                       | ✅     |
| TF-009       | Press the login button                         | App open        | 1. Press login button  2. Complete Google login | Successful login                                               | ✅     |

| Test Case ID | Description                                    | Preconditions       | Step-by-Step Procedure                              | Expected Result                                                                 | Result |
| ------------ | ---------------------------------------------- | ------------------- | --------------------------------------------------- | ------------------------------------------------------------------------------- | ------ |
| NF-001       | Real-time sensor data processing               | Game is running     | 1. Perform movement  2. Measure reaction time       | Feedback in at least 95 % of cases within 50 ms                                 | ✅     |
| NF-002       | Offline mode                                   | App is offline      | 1. Start game  2. Achieve new high score  3. Close and reopen app  4. Open scoreboard  5. Validate score | App remains fully functional; high score stored locally                  | ✅     |
| NF-003       | Usability test with users                      | Test users available| 1. Users perform various actions  2. Collect feedback | At least 90 % of testers rate the app as intuitive (testers: Bürgis, Mugli)    | ✅     |
