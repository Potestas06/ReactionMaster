package ch.kri.reactionmaster

import android.content.Context
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GameOver : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_over)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val retry_button = findViewById<Button>(R.id.retry_btn)
        val menuBtn = findViewById<Button>(R.id.return_btn)
        val score_view = findViewById<TextView>(R.id.score_view)
        val highscore_view = findViewById<TextView>(R.id.highscore_view)

        val prefs = getSharedPreferences("my_game_prefs", MODE_PRIVATE)
        val score = prefs.getInt("last_score", 0)
        val highscore = prefs.getInt("highscore", 0)

        if(highscore < score){
            prefs.edit().putInt("highscore", score).apply()
        }

        highscore_view.text = "Highscore: $highscore"

        score_view.text = "Score: $score"






        menuBtn.setOnClickListener {
            finish()
        }

        retry_button.setOnClickListener {
            finish()
            val intent = Intent(this, Game::class.java)
            startActivity(intent)
        }



    }
}