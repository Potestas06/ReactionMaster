package ch.kri.reactionmaster

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.absoluteValue
import kotlin.math.sqrt

class Game : AppCompatActivity(), SensorEventListener {

    private lateinit var countdownView: TextView
    private lateinit var scoreView: TextView
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var topButton: Button
    private lateinit var bottomButton: Button
    private lateinit var pressButton: Button

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var score = 0
    private var currentAction = 0
    private var actionInProgress = false

    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f

    private var actionDuration = 3000L
    private var cooldownDuration = 1000L

    private val SHAKE_THRESHOLD = 2.3f
    private val MOVEMENT_FAIL_THRESHOLD = 1.4f

    private val mainTimer = object : CountDownTimer(4000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            countdownView.text = (millisUntilFinished / 1000).toString()
        }
        override fun onFinish() {
            countdownView.visibility = View.GONE
            startNextAction()
        }
    }

    private var actionTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)

        countdownView = findViewById(R.id.countdown_view)
        scoreView = findViewById(R.id.score)
        leftButton = findViewById(R.id.left)
        rightButton = findViewById(R.id.right)
        topButton = findViewById(R.id.top)
        bottomButton = findViewById(R.id.down)
        pressButton = findViewById(R.id.press_btn)

        pressButton.setOnClickListener { onButtonPressed(it) }

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
     * 1 = Lean Left
     * 2 = Lean Right
     * 3 = Lean Forward (Top)
     * 4 = Lean Backward (Down)
     * 5 = Press Button
     * 6 = Shake
     */
    private fun pickAction(): Int = (1..6).random()

    private fun startNextAction() {
        resetUI()
        currentAction = pickAction()
        actionInProgress = true

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

        actionTimer = object : CountDownTimer(actionDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                if (actionInProgress) failAction()
            }
        }.start()
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
     * Sensor data changes => check for movement or shake
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
            val gForce = sqrt(gX*gX + gY*gY + gZ*gZ)

            when (currentAction) {
                1 -> {
                    if (x > 5) successAction()
                }
                2 -> if (x < -5) successAction()
                3 -> if (y < -5) successAction()
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

            lastX = x
            lastY = y
            lastZ = z
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // no-op
    }

    private fun successAction() {
        actionInProgress = false
        actionTimer?.cancel()
        incrementScore()
        resetUI()

        increaseDifficulty()

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

    private fun incrementScore() {
        score++
        scoreView.visibility = View.VISIBLE
        scoreView.text = score.toString()
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
        button.visibility = View.VISIBLE
        val black = ContextCompat.getColor(this, android.R.color.black)
        button.setBackgroundColor(black)
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
        saveScoreLocally(score)
        val intent = Intent(this, GameOver::class.java)
        startActivity(intent)
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
            vibrator.vibrate(
                VibrationEffect.createOneShot(durationMs, 255)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }
}
