package ch.kri.reactionmaster

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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

    suspend fun storeNumber(username: String, number: Int) {
        while (!isInternetAvailable()) {
            delay(5000)
        }

        val existingQuery = firestore.collection("numbers")
            .whereEqualTo("username", username)
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
