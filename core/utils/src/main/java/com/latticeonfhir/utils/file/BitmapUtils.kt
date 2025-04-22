package com.latticeonfhir.core.utils.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.latticeonfhir.core.utils.file.FileManager.createFolder
import com.latticeonfhir.core.utils.file.FileManager.removeFromInternalStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.sqrt

object BitmapUtils {
    private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun compressImage(context: Context, uri: Uri, quality: Int = 25): Boolean {
        val tempFile = getTempFile(context, uri)
        return if (uri.sizeInKB() > 1000 && quality >= 5) {
            // reduce the quality factor
            val originalBitmap = getBitmapFromUri(context, uri)
            val orientation = getExifOrientation(context, uri)
            val rotatedBitmap = originalBitmap?.let { rotateBitmap(it, orientation) }
            val byteArrayOutputStream = ByteArrayOutputStream()
            rotatedBitmap?.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            // Write the compressed byte array to a file
            val fileOutputStream = FileOutputStream(tempFile)
            fileOutputStream.write(byteArray)
            fileOutputStream.flush()
            fileOutputStream.close()
            compressImage(context, tempFile.toUri(), quality - 5)
        } else if (uri.sizeInKB() > 1000 && quality < 5) {
            removeFromInternalStorage(context, tempFile.name)
            // if the quality factor is less than 0 and size is still greater than 1 mb
            // reduce the size of original image
            reduceSize(context, getOriginalFile(context, uri).toUri())
        } else {
            if (uri.toFile().name.contains("temp")) {
                copyImageFile(
                    sourceFile = tempFile,
                    destinationFile = getOriginalFile(context, uri)
                )
            }
            removeFromInternalStorage(context, tempFile.name)
            true
        }
    }

    private fun reduceSize(context: Context, uri: Uri): Boolean {
        val originalBitmap = getBitmapFromUri(context, uri)
        val orientation = getExifOrientation(context, uri)
        val rotatedBitmap = originalBitmap?.let { rotateBitmap(it, orientation) }
        val resizedBitmap = rotatedBitmap?.let { resizeBitmap(it) }
        val byteArrayOutputStream = ByteArrayOutputStream()
        resizedBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        // check if size is less than 1 mb
        return if (byteArray.size < 1000000) {
            val fileOutputStream = FileOutputStream(uri.toFile())
            fileOutputStream.write(byteArray)
            fileOutputStream.flush()
            fileOutputStream.close()
            true
        } else false
    }

    private fun getOriginalFile(context: Context, uri: Uri): File {
        val originalFileName =
            if (uri.toFile().name.contains("temp")) uri.toFile().name.substringAfter("_") else uri.toFile().name
        return File(createFolder(context), originalFileName)
    }

    private fun getTempFile(context: Context, uri: Uri): File {
        val tempName =
            if (uri.toFile().name.contains("temp")) uri.toFile().name else "temp_${uri.toFile().name}"
        return File(createFolder(context), tempName)
    }

    private fun Uri.sizeInKB(): Long {
        return this.toFile().length() / 1000
    }

    private fun getExifOrientation(context: Context, uri: Uri): Int {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val exif = ExifInterface(inputStream!!)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            inputStream.close()
            orientation
        } catch (e: IOException) {
            e.printStackTrace()
            ExifInterface.ORIENTATION_NORMAL
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun resizeBitmap(bitmap: Bitmap, maxSizeInKB: Int = 1024): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val aspectRatio: Float = width.toFloat() / height.toFloat()

        // Calculate new dimensions maintaining aspect ratio
        val targetWidth = sqrt((maxSizeInKB * 1024).toDouble() * aspectRatio).toInt()
        val targetHeight = (targetWidth / aspectRatio).toInt()

        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    private fun copyImageFile(sourceFile: File, destinationFile: File) {
        var inputStream: FileInputStream? = null
        var outputStream: FileOutputStream? = null

        try {
            inputStream = FileInputStream(sourceFile)
            outputStream = FileOutputStream(destinationFile)

            val buffer = ByteArray(1024)
            var length: Int

            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }
}