package com.example.lab_week_08

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.lab_week_08.worker.FirstWorker
import com.example.lab_week_08.worker.SecondWorker

class MainActivity : AppCompatActivity() {

    // Instance WorkManager
    private val workManager = WorkManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Log.d("MainActivity", "MainActivity started")

        // Buat constraint: worker tidak butuh internet (untuk testing)
        val networkConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        // ID yang akan dikirim ke worker
        val id = "001"

        // Buat request untuk FirstWorker
        val firstRequest = OneTimeWorkRequest
            .Builder(FirstWorker::class.java)
            .setConstraints(networkConstraints)
            .setInputData(getIdInputData(FirstWorker.INPUT_DATA_ID, id))
            .build()

        // Buat request untuk SecondWorker
        val secondRequest = OneTimeWorkRequest
            .Builder(SecondWorker::class.java)
            .setConstraints(networkConstraints)
            .setInputData(getIdInputData(SecondWorker.INPUT_DATA_ID, id))
            .build()

        // Eksekusi worker secara berurutan: FirstWorker dulu, baru SecondWorker
        workManager
            .beginWith(firstRequest)  // Mulai dengan FirstWorker
            .then(secondRequest)      // Lanjut ke SecondWorker
            .enqueue()                // Jalankan

        Log.d("MainActivity", "Workers enqueued")

        // Observe FirstWorker: pantau status FirstWorker
        workManager.getWorkInfoByIdLiveData(firstRequest.id)
            .observe(this) { info ->
                Log.d("MainActivity", "FirstWorker state: ${info.state}")
                if (info.state.isFinished) {
                    showResult("First process is done")
                }
            }

        // Observe SecondWorker: pantau status SecondWorker
        workManager.getWorkInfoByIdLiveData(secondRequest.id)
            .observe(this) { info ->
                Log.d("MainActivity", "SecondWorker state: ${info.state}")
                if (info.state.isFinished) {
                    showResult("Second process is done")
                }
            }
    }

    // Method untuk membuat input data yang dikirim ke worker
    private fun getIdInputData(idKey: String, idValue: String): Data {
        return Data.Builder()
            .putString(idKey, idValue)
            .build()
    }

    // Method untuk menampilkan Toast
    private fun showResult(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.d("MainActivity", message)
    }
}
