package ch.kri.reactionmaster

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.sqrt



/*

    Dieser Code Wurde mit chatgpt erstellt und minimal angepasst

    query:
    "make me a tutorial out of it insted of the score should be a explelation what to do and insted of the countdown it will display a short explelation on that u have limited time and it gets faster etc.
    the user has unlimited time for evry action and the button turns green if he susceed the score displays well done and then the next action is shown till evrythin is done than the user gets redirectet back to the home page:
    {Game.kt}

 */

class Tutorial : AppCompatActivity(), SensorEventListener {

    private lateinit var countdownView: TextView
    private lateinit var scoreView: TextView
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var topButton: Button
    private lateinit var bottomButton: Button
    private lateinit var pressButton: Button

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var currentAction = 0
    private var actionInProgress = false
    private var actionsCompleted = 0
    private val MAX_ACTIONS = 6

    // Urspruengliche Zeiten, die zur Erhoehung der Schwierigkeit genutzt werden.
    // Auch wenn der Spieler unbegrenzte Zeit hat, wird die Herausforderung sichtbar ansteigen.
    private var actionDuration = 3000L
    private var cooldownDuration = 1000L

    private val SHAKE_THRESHOLD = 2.3f
    private val MOVEMENT_FAIL_THRESHOLD = 1.4f

    // MainTimer: Zeigt zu Beginn eine Erklaerung an, bevor die erste Aktion startet.
    private val mainTimer = object : CountDownTimer(4000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            countdownView.text =
                "Achtung: Die Herausforderung wird schneller, aber du hast unbegrenzte Zeit!"
        }
        override fun onFinish() {
            countdownView.visibility = View.GONE
            startNextAction()
        }
    }

    // Da der Spieler unbegrenzte Zeit hat, entfällt der Timer, der ein Scheitern ausloest.
    private var actionTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tutorial)

        countdownView = findViewById(R.id.countdown_view)
        scoreView = findViewById(R.id.score)
        leftButton = findViewById(R.id.left)
        rightButton = findViewById(R.id.right)
        topButton = findViewById(R.id.top)
        bottomButton = findViewById(R.id.down)
        pressButton = findViewById(R.id.press_btn)

        pressButton.setOnClickListener { onButtonPressed(it) }

        // Startet den Timer, der eine kurze Erklaerung anzeigt, bevor das Spiel beginnt
        mainTimer.start()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    /**
     * Aktionen:
     * 1 = Lehne dich nach links
     * 2 = Lehne dich nach rechts
     * 3 = Lehne dich nach vorne
     * 4 = Lehne dich nach hinten
     * 5 = Druecke den Button
     * 6 = Schuettel das Telefon
     */
    private var nextAction = 1
    private fun pickAction(): Int {
        val action = nextAction
        nextAction = if (nextAction < 6) nextAction + 1 else 1
        return action
    }


    private fun startNextAction() {
        resetUI()
        currentAction = pickAction()
        actionInProgress = true
        scoreView.bringToFront()

        // Setze die entsprechende Anweisung in der scoreView
        when (currentAction) {
            1 -> scoreView.text = "Lehne dich nach links!"
            2 -> scoreView.text = "Lehne dich nach rechts!"
            3 -> scoreView.text = "Lehne dich nach vorne!"
            4 -> scoreView.text = "Lehne dich nach hinten!"
            5 -> scoreView.text = "Drücke den Button!"
            6 -> scoreView.text = "Schüttel das Telefon!"
        }
        scoreView.visibility = View.VISIBLE

        // Zeige die aktive Taste oder den Hinweis an
        when (currentAction) {
            1 -> showAndColor(leftButton)
            2 -> showAndColor(rightButton)
            3 -> showAndColor(topButton)
            4 -> showAndColor(bottomButton)
            5 -> showAndColor(pressButton)
            6 -> {
                vibratePhone(200)
                countdownView.visibility = View.VISIBLE
                countdownView.text = "Shake!"
                countdownView.setTextColor(ContextCompat.getColor(this, android.R.color.black))
            }
        }
        // Der actionTimer entfällt, da der Spieler unbegrenzte Zeit hat.
    }

    fun onButtonPressed(view: View) {
        if (!actionInProgress) return
        if (currentAction == 5 && view.id == R.id.press_btn) {
            successAction()
        } else {
            failAction()
        }
    }

    /**
     * SensorAenderungen: Ueberpruefung, ob die richtige Bewegung ausgefuehrt wird.
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (!actionInProgress) return
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH
            val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

            when (currentAction) {
                1 -> if (x > 5) successAction()
                2 -> if (x < -5) successAction()
                3 -> if (y < -4) successAction()
                4 -> if (y > 5) successAction()
                6 -> {
                    if (gForce > SHAKE_THRESHOLD) {
                        successAction()
                    }
                }
                5 -> {
                    if (gForce > MOVEMENT_FAIL_THRESHOLD) {
                        failAction()
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // keine Aktion noetig
    }

    private fun successAction() {
        actionInProgress = false
        actionTimer?.cancel()
        actionsCompleted++

        // Zeige Erfolgsmeldung und markiere den aktiven Button gruen
        scoreView.text = "Well done!"
        markActiveButtonGreen()

        // Erhoehe die Schwierigkeit
        increaseDifficulty()

        // Bei Erreichen der MAX_ACTIONS wird zur Home-Seite umgeleitet
        if (actionsCompleted >= MAX_ACTIONS) {
            Handler(Looper.getMainLooper()).postDelayed({
                goToHomePage()
            }, cooldownDuration)
            return
        }

        // Nacheinander die naechste Aktion starten
        Handler(Looper.getMainLooper()).postDelayed({
            startNextAction()
        }, cooldownDuration)
    }

    private fun failAction() {
        actionInProgress = false
        actionTimer?.cancel()
        highlightFail()

        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                goToGameOver()
            }
        }.start()
    }

    private fun increaseDifficulty() {
        if (actionDuration > 1000) {
            actionDuration -= 200
        }
        if (cooldownDuration > 500) {
            cooldownDuration -= 50
        }
    }

    private fun showAndColor(button: Button) {
        // Setze den Button sichtbar und mit Standardfarbe
        button.visibility = View.VISIBLE
        button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black))
    }

    private fun markActiveButtonGreen() {
        // Markiere den aktuell aktiven Button gruen
        when (currentAction) {
            1 -> showAndColorSuccess(leftButton)
            2 -> showAndColorSuccess(rightButton)
            3 -> showAndColorSuccess(topButton)
            4 -> showAndColorSuccess(bottomButton)
            5 -> showAndColorSuccess(pressButton)
            // Bei der Shake-Aktion wird kein Button genutzt – hier koennte man den countdownView einfuegen.
        }
    }

    private fun showAndColorSuccess(button: Button) {
        val green = ContextCompat.getColor(this, android.R.color.holo_green_dark)
        button.setBackgroundColor(green)
    }

    private fun resetUI() {
        countdownView.visibility = View.INVISIBLE
        leftButton.visibility = View.INVISIBLE
        rightButton.visibility = View.INVISIBLE
        topButton.visibility = View.INVISIBLE
        bottomButton.visibility = View.INVISIBLE
        pressButton.visibility = View.INVISIBLE
        countdownView.setTextColor(ContextCompat.getColor(this, android.R.color.black))
    }

    private fun highlightFail() {
        val red = ContextCompat.getColor(this, android.R.color.holo_red_light)
        when (currentAction) {
            1 -> leftButton.setBackgroundColor(red)
            2 -> rightButton.setBackgroundColor(red)
            3 -> topButton.setBackgroundColor(red)
            4 -> bottomButton.setBackgroundColor(red)
            5 -> pressButton.setBackgroundColor(red)
            6 -> {
                countdownView.visibility = View.VISIBLE
                countdownView.setTextColor(red)
                countdownView.text = "Fail!"
            }
        }
    }

    private fun goToGameOver() {
        saveScoreLocally(actionsCompleted)
        val intent = Intent(this, GameOver::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToHomePage() {
        finish()
    }

    private fun saveScoreLocally(finalScore: Int) {
        val prefs = getSharedPreferences("my_game_prefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("last_score", finalScore).apply()
    }

    private fun vibratePhone(durationMs: Long) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(durationMs, 255))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }
}
