package ch.kri.reactionmaster

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class FirestoreService(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun syncNumberOnLogin(uid: String) {
        val querySnapshot = firestore.collection("numbers")
            .whereEqualTo("UID", uid)
            .limit(1)
            .get()
            .await()

        if (querySnapshot.documents.isNotEmpty()) {
            val document = querySnapshot.documents.first()
            val newHighScore = document.getLong("highscore")?.toInt() ?: 0
            val prefs = context.getSharedPreferences("my_game_prefs", Context.MODE_PRIVATE)

            if(newHighScore != 0){
                prefs.edit().putInt("highscore", newHighScore).apply()
            }
        }
    }

    suspend fun CompareNumber(number: Int, UID: String, username: String) {
        while (!isInternetAvailable()) {
            delay(5000)
        }

        val existingQuery = firestore.collection("numbers")
            .whereEqualTo("UID", UID)
            .limit(1)
            .get()
            .await()

        if(existingQuery.documents.isNotEmpty()){
            val document = existingQuery.documents.first()
            val DBHighScore = document.getLong("highscore")?.toInt()
            if(DBHighScore != number){
                storeNumber(username, number, UID)
            }            }
    }


    suspend fun storeNumber(username: String, number: Int, UID: String) {
        while (!isInternetAvailable()) {
            delay(5000)
        }

        val existingQuery = firestore.collection("numbers")
            .whereEqualTo("UID", UID)
            .limit(1)
            .get()
            .await()

        if (existingQuery.documents.isNotEmpty()) {
            val docSnapshot = existingQuery.documents[0]
            val docId = docSnapshot.id

            firestore.collection("numbers")
                .document(docId)
                .update(
                    mapOf(
                        "number" to number,
                        "timestamp" to FieldValue.serverTimestamp()
                    )
                )
                .await()

        } else {
            val data = hashMapOf(
                "UID" to UID,
                "username" to username,
                "number" to number,
                "timestamp" to FieldValue.serverTimestamp()
            )

            firestore.collection("numbers")
                .add(data)
                .await()
        }
    }
}
