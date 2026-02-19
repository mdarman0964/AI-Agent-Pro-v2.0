package com.aiagent.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.aiagent.R
import com.aiagent.data.model.GeneratedFile
import com.aiagent.network.RetrofitClient
import com.aiagent.utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class DownloadService : Service() {
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    
    companion object {
        const val CHANNEL_ID = "download_channel"
        const val NOTIFICATION_ID = 1
        
        const val ACTION_DOWNLOAD = "action_download"
        const val ACTION_SHARE = "action_share"
        
        const val EXTRA_PROJECT_NAME = "project_name"
        const val EXTRA_FILES = "files"
        
        fun startDownload(context: Context, projectName: String, files: List<GeneratedFile>) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = ACTION_DOWNLOAD
                putExtra(EXTRA_PROJECT_NAME, projectName)
                putParcelableArrayListExtra(EXTRA_FILES, ArrayList(files))
            }
            context.startService(intent)
        }
        
        fun startShare(context: Context, projectName: String, files: List<GeneratedFile>) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = ACTION_SHARE
                putExtra(EXTRA_PROJECT_NAME, projectName)
                putParcelableArrayListExtra(EXTRA_FILES, ArrayList(files))
            }
            context.startService(intent)
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val projectName = intent?.getStringExtra(EXTRA_PROJECT_NAME) ?: return START_NOT_STICKY
        val files = intent.getParcelableArrayListExtra<GeneratedFile>(EXTRA_FILES) ?: return START_NOT_STICKY
        
        when (intent.action) {
            ACTION_DOWNLOAD -> downloadProject(projectName, files)
            ACTION_SHARE -> shareProject(projectName, files)
        }
        
        return START_NOT_STICKY
    }
    
    private fun downloadProject(projectName: String, files: List<GeneratedFile>) {
        startForeground(NOTIFICATION_ID, createProgressNotification("Downloading $projectName..."))
        
        serviceScope.launch {
            try {
                // Create local ZIP
                val zipFile = createLocalZip(projectName, files)
                
                if (zipFile != null) {
                    showCompleteNotification(
                        "Download Complete",
                        "$projectName.zip saved to Downloads",
                        zipFile
                    )
                } else {
                    showErrorNotification("Download failed")
                }
            } catch (e: Exception) {
                showErrorNotification("Error: ${e.message}")
            } finally {
                stopSelf()
            }
        }
    }
    
    private fun shareProject(projectName: String, files: List<GeneratedFile>) {
        startForeground(NOTIFICATION_ID, createProgressNotification("Preparing $projectName..."))
        
        serviceScope.launch {
            try {
                val zipFile = createLocalZip(projectName, files)
                
                if (zipFile != null) {
                    // Share
                    FileUtils.shareFile(this@DownloadService, zipFile)
                    stopSelf()
                } else {
                    showErrorNotification("Share failed")
                    stopSelf()
                }
            } catch (e: Exception) {
                showErrorNotification("Error: ${e.message}")
                stopSelf()
            }
        }
    }
    
    private suspend fun downloadFromServer(projectName: String, files: List<GeneratedFile>): File? {
        return try {
            val response = RetrofitClient.apiService.downloadZip(projectName, files)
            
            if (response.isSuccessful && response.body() != null) {
                saveResponseToFile(projectName, response.body()!!)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun createLocalZip(projectName: String, files: List<GeneratedFile>): File? {
        return FileUtils.createZipFile(this, projectName, files)
    }
    
    private fun saveResponseToFile(projectName: String, body: ResponseBody): File? {
        return try {
            val downloadsDir = getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, "$projectName.zip")
            
            FileOutputStream(file).use { output ->
                body.byteStream().use { input ->
                    input.copyTo(output)
                }
            }
            
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Downloads",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Project download notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createProgressNotification(message: String) = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("AI Agent Pro")
        .setContentText(message)
        .setSmallIcon(R.drawable.ic_download)
        .setProgress(0, 0, true)
        .setOngoing(true)
        .build()
    
    private fun showCompleteNotification(title: String, message: String, file: File) {
        val intent = FileUtils.getOpenFileIntent(this, file)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_check)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID + 1, notification)
    }
    
    private fun showErrorNotification(message: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Download Failed")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_error)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID + 2, notification)
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
