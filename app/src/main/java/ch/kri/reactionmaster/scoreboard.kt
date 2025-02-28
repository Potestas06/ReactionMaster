package ch.kri.reactionmaster

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class scoreboard : AppCompatActivity() {
    private lateinit var userRankText: TextView
    private lateinit var userUsernameText: TextView
    private lateinit var userScoreText: TextView
    private lateinit var scoreContainer: LinearLayout

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)

        // Views aus dem Layout
        userRankText = findViewById(R.id.userRankText)
        userUsernameText = findViewById(R.id.userUsernameText)
        userScoreText = findViewById(R.id.userScoreText)
        scoreContainer = findViewById(R.id.scoreContainer)

        // Aktuellen Benutzer holen und oben anzeigen
        lifecycleScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val username = currentUser.displayName ?: currentUser.email ?: "Unknown"
                val userScore = getUserScore(username)
                val userRank = getUserRank(userScore)

                userRankText.text = "$userRank."
                userUsernameText.text = username
                userScoreText.text = "$userScore"
            }
        }

        // Top-Einträge laden
        fetchTopScores()
    }

    private fun fetchTopScores() {
        firestore.collection("numbers")
            .orderBy("number", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                val inflater = LayoutInflater.from(this)
                var rank = 1
                for (document in result) {
                    val username = document.getString("username") ?: "Unknown"
                    val number = document.getLong("number")?.toInt() ?: 0
                    val scoreData = ScoreData(rank, username, number)

                    // scoreboard_item aufblaehen
                    val itemView = inflater.inflate(R.layout.scoreboard_item, scoreContainer, false)

                    val rankText = itemView.findViewById<TextView>(R.id.rankText)
                    val usernameText = itemView.findViewById<TextView>(R.id.usernameText)
                    val scoreText = itemView.findViewById<TextView>(R.id.scoreText)

                    rankText.text = "${scoreData.rank}."
                    usernameText.text = scoreData.username
                    scoreText.text = scoreData.score.toString()

                    // Platz 1, 2, 3 einfärben
                    val itemLayout = itemView.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.itemLayout)
                    when (scoreData.rank) {
                        1 -> itemLayout.setBackgroundColor(Color.parseColor("#FFD600")) // Gelb
                        2 -> itemLayout.setBackgroundColor(Color.parseColor("#D3D3D3")) // Grau (Silber)
                        3 -> itemLayout.setBackgroundColor(Color.parseColor("#FFA451")) // Bronze
                        else -> {
                            // Weißer Hintergrund via Drawable
                            itemLayout.setBackgroundResource(R.drawable.scoreboard_item_bg)
                        }
                    }

                    scoreContainer.addView(itemView)
                    rank++
                }
            }
            .addOnFailureListener {
                // Fehlerbehandlung (z. B. Toast oder Log)
            }
    }

    /**
     * Hole den Score des Benutzers aus Firestore
     */
    private suspend fun getUserScore(username: String): Int {
        val querySnapshot = firestore.collection("numbers")
            .whereEqualTo("username", username)
            .limit(1)
            .get()
            .await()

        return if (querySnapshot.documents.isNotEmpty()) {
            querySnapshot.documents[0].getLong("number")?.toInt() ?: 0
        } else {
            0
        }
    }

    /**
     * Ermittle den Rang eines Benutzers (wie viele haben mehr Punkte?)
     */
    private suspend fun getUserRank(userScore: Int): Int {
        val higherScoreCount = firestore.collection("numbers")
            .whereGreaterThan("number", userScore)
            .get()
            .await()
            .size()

        return higherScoreCount + 1
    }
}

