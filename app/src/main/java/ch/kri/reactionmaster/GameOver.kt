package ch.kri.reactionmaster

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class GameOver : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val retryButton = findViewById<Button>(R.id.retry_btn)
        val menuBtn = findViewById<Button>(R.id.return_btn)
        val scoreView = findViewById<TextView>(R.id.score_view)
        val highscoreView = findViewById<TextView>(R.id.highscore_view)

        val prefs = getSharedPreferences("my_game_prefs", MODE_PRIVATE)
        val score = prefs.getInt("last_score", 0)
        val highscore = prefs.getInt("highscore", 0)

        scoreView.text = "Score: $score"

        if (highscore < score) {
            prefs.edit().putInt("highscore", score).apply()
            highscoreView.text = "Highscore: $score"

            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                val username = firebaseUser.displayName ?: firebaseUser.email ?: "Unbekannt"
                lifecycleScope.launch {
                    FirestoreService(applicationContext).storeNumber(username, score)
                }
            }
        } else {
            highscoreView.text = "Highscore: $highscore"
        }

        menuBtn.setOnClickListener {
            finish()
        }

        retryButton.setOnClickListener {
            finish()
            startActivity(Intent(this, Game::class.java))
        }
    }
}
