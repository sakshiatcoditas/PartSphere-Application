package com.example.partsphere.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

object MultipartUtils {

    fun createPartFromString(value: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), value)
    }

    fun prepareFilePart(
        context: Context,
        partName: String,
        fileUri: Uri
    ): MultipartBody.Part {

        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(fileUri)
            ?: throw IllegalStateException("Cannot open input stream from URI")

        // Create temp file
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        tempFile.outputStream().use { output ->
            inputStream.copyTo(output)
        }

        val requestFile = tempFile
            .asRequestBody("image/*".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            partName,
            tempFile.name,
            requestFile
        )
    }

}
