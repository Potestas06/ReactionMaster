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

    // Prüft, ob eine Internetverbindung vorhanden ist
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Speichert den Benutzernamen und die Zahl in Firestore,
    // wartet so lange, bis eine Internetverbindung besteht
    suspend fun storeNumber(username: String, number: Int) {
        while (!isInternetAvailable()) {
            delay(5000) // 5 Sekunden warten, bevor erneut geprüft wird
        }

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
