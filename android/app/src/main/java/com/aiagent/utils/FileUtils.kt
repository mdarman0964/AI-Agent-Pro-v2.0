package com.aiagent.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.aiagent.data.model.GeneratedFile
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object FileUtils {
    
    fun createZipFile(context: Context, projectName: String, files: List<GeneratedFile>): File? {
        return try {
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val zipFile = File(downloadsDir, "$projectName.zip")
            
            ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
                files.forEach { file ->
                    val entry = ZipEntry(file.path)
                    zipOut.putNextEntry(entry)
                    zipOut.write(file.content.toByteArray())
                    zipOut.closeEntry()
                }
            }
            
            zipFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun shareFile(context: Context, file: File, mimeType: String = "application/zip") {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "AI Generated Project")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Share Project"))
    }
    
    fun openFile(context: Context, file: File, mimeType: String) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(intent)
    }
    
    fun saveFileToDownloads(context: Context, fileName: String, content: String): Boolean {
        return try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            file.writeText(content)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun getLanguageFromExtension(fileName: String): String {
        val ext = fileName.substringAfterLast('.', "").lowercase()
        return when (ext) {
            "kt" -> "kotlin"
            "java" -> "java"
            "py" -> "python"
            "js" -> "javascript"
            "ts" -> "typescript"
            "xml" -> "xml"
            "gradle" -> "groovy"
            "json" -> "json"
            "yaml", "yml" -> "yaml"
            "md" -> "markdown"
            "txt" -> "text"
            "sh" -> "bash"
            "dockerfile" -> "dockerfile"
            else -> "text"
        }
    }
    
    fun formatFileSize(size: Long): String {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            else -> "${size / (1024 * 1024)} MB"
        }
    }
}
