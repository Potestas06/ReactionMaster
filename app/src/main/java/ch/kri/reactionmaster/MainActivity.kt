package ch.kri.reactionmaster

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val start_button = findViewById<Button>(R.id.Start)
        val scoreboard_btn = findViewById<Button>(R.id.Scoreboard_btn)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        scoreboard_btn.setOnClickListener{
            val intent = Intent(this, scoreboard::class.java)
            startActivity(intent)
        }

        start_button.setOnClickListener {
            val intent = Intent(this, Game::class.java)
            startActivity(intent)
        }

        val googleAuthClient = GoogleAuthClient(applicationContext)
        val loginButton = findViewById<Button>(R.id.Login_btn)



        loginButton.setOnClickListener {
            lifecycleScope.launch {
                val signInSuccessful = googleAuthClient.signIn()
                if (signInSuccessful == true){
                    Toast.makeText(
                        applicationContext,
                        "Login successful",
                        Toast.LENGTH_SHORT
                    ).show()



                }
            }
        }

    }

}
