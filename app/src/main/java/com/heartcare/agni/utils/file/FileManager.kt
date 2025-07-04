package com.heartcare.agni.utils.file

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.heartcare.agni.BuildConfig
import com.heartcare.agni.R
import com.heartcare.agni.utils.file.FileManager.createFolder
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.io.InputStream

object FileManager {

    fun createFolder(context: Context): File {
        val internalStorageDir = context.filesDir
        val parentFolder = File(internalStorageDir, context.getString(R.string.app_name))
        if (!parentFolder.exists()) {
            parentFolder.mkdir()
        }
        return parentFolder
    }

    fun insertFileToInternalStorage(
        folder: File,
        title: String,
        url: String,
        context: Context
    ): String {
        val cr = context.contentResolver
        val sourceUri = Uri.parse(url)
        val inputStream = cr.openInputStream(sourceUri)
        inputStream?.let {
            writeFileToInternalStorage(folder, title, it)
        }
        return title
    }

    fun String.removeTimeStamp(): String {
        return this.substringAfter("_")
    }

    fun insertFileToExternalStorage(
        context: Context,
        fileName: String,
        destinationFileName: String
    ): Boolean {
        val sourceFile = File(createFolder(context), fileName)
        if (sourceFile.exists()) {
            val externalStorageDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            } else {
                Environment.getExternalStorageDirectory()
            }
            val destinationDir = File(externalStorageDir, context.getString(R.string.app_name))

            if (!destinationDir.exists()) {
                if (!destinationDir.mkdirs()) {
                    return false
                } else {
                    Timber.tag("attach").d("created")
                }
            }
            val destinationFile = File(destinationDir, destinationFileName.removeTimeStamp())

            try {

                sourceFile.inputStream().use { input ->
                    destinationFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: IOException) {
                Timber.tag("attach").d("$e")
            }

            return destinationFile.exists()
        } else {
            Timber.d("not exist")
        }
        return false

    }

    // Function to write an InputStream to a file in internal storage
    private fun writeFileToInternalStorage(
        uploadFolder: File,
        fileName: String,
        inputStream: InputStream
    ) {
        val file = File(uploadFolder, fileName)

        try {
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getFileWithDirectory(
        context: Context,
        fileName: String
    ): File {
        val externalStorageDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        } else {
            Environment.getExternalStorageDirectory()
        }
        val destinationDir = File(externalStorageDir, context.getString(R.string.app_name))
        return File(destinationDir, fileName.removeTimeStamp())
    }

    fun removeFromInternalStorage(context: Context, fileName: String) {
        File(createFolder(context), fileName).delete()
    }

    fun shareImageToOtherApps(context: Context, uri: Uri) {
        val contentUri = FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID + ".provider",
            uri.toFile()
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Image"))
    }

    fun String.getUriFromFileName(context: Context): Uri {
        return File(createFolder(context), this).toUri()
    }
}

class DeleteFileManager(private val context: Context) {
    fun removeFromInternalStorage(fileName: String) {
        File(createFolder(context), fileName).delete()
    }
}