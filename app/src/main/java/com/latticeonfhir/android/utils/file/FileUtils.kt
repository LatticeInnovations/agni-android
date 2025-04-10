package com.latticeonfhir.core.utils.file

import okhttp3.ResponseBody
import java.io.File
import java.util.zip.ZipFile

object FileUtils {

    internal fun ResponseBody.saveFile(destinationFile: File) {
        byteStream().use { inputStream ->
            destinationFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    internal fun unzipFile(file: File) {
        ZipFile(file.absolutePath).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->
                    File(file.parent, entry.name).outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
    }

    internal fun deleteZipFile(file: File): Boolean {
        if (file.exists()) {
            return file.delete()
        }
        return false;
    }

    internal fun String.toFileName(): String {
        val splitUrl = split("/")
        return splitUrl[splitUrl.size - 1]
    }
}