package ch.kri.reactionmaster

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)



        val start_button = findViewById<Button>(R.id.Start)
        val scoreboard_btn = findViewById<Button>(R.id.Scoreboard_btn)
        val tutorial_btn = findViewById<Button>(R.id.tutorial_btn)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val userID = firebaseUser!!.uid
        lifecycleScope.launch {
            val prefs = getSharedPreferences("my_game_prefs", MODE_PRIVATE)
            val highscore = prefs.getInt("highscore", 0)
            val username = firebaseUser.displayName ?: firebaseUser.email ?: "Unbekannt"
            FirestoreService(applicationContext).CompareNumber(highscore, firebaseUser.uid,username )
        }

        scoreboard_btn.setOnClickListener{
            val intent = Intent(this, scoreboard::class.java)
            startActivity(intent)
        }

        tutorial_btn.setOnClickListener{
            val intent = Intent(this, Tutorial::class.java)
            startActivity(intent)
        }

        start_button.setOnClickListener {
            val intent = Intent(this, Game::class.java)
            startActivity(intent)
        }

        val loginButton = findViewById<Button>(R.id.Login_btn)
        val googleAuthClient = GoogleAuthClient(applicationContext)


        loginButton.setOnClickListener {
            lifecycleScope.launch {
                val signInSuccessful = googleAuthClient.signIn()
                if (signInSuccessful == true){
                    Toast.makeText(
                        applicationContext,
                        "Login successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    scoreboard_btn.visibility = View.VISIBLE
                    FirestoreService(applicationContext).syncNumberOnLogin(userID)
                }
            }
        }

    }

}
