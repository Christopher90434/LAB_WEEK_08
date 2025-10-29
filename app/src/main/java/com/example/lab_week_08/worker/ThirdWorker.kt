package com.example.lab_week_08.worker

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class ThirdWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("ThirdWorker", "ThirdWorker started")

        // Ambil input data ID
        val id = inputData.getString(INPUT_DATA_ID)
        Log.d("ThirdWorker", "Processing ID: $id")

        // Simulasi proses berat selama 3 detik
        Thread.sleep(3000L)

        // Buat output data
        val outputData = Data.Builder()
            .putString(OUTPUT_DATA_ID, id)
            .build()

        Log.d("ThirdWorker", "ThirdWorker completed")

        // Return success dengan output data
        return Result.success(outputData)
    }

    companion object {
        const val INPUT_DATA_ID = "inId"
        const val OUTPUT_DATA_ID = "outId"
    }
}
