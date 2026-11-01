package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File
import java.io.FileOutputStream

object ImageHelper {
  fun saveUriToInternalStorage(context: Context, uri: Uri): String? {
    return try {
      val inputStream = context.contentResolver.openInputStream(uri) ?: return null
      val file = File(context.filesDir, "chat_img_${System.currentTimeMillis()}.jpg")
      val outputStream = FileOutputStream(file)
      inputStream.use { input ->
        outputStream.use { output ->
          input.copyTo(output)
        }
      }
      file.absolutePath
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  fun loadBitmap(path: String, maxDim: Int = 800): ImageBitmap? {
    return try {
      val file = File(path)
      if (!file.exists()) return null

      val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
      BitmapFactory.decodeFile(path, options)

      var sampleSize = 1
      while (options.outWidth / sampleSize > maxDim || options.outHeight / sampleSize > maxDim) {
        sampleSize *= 2
      }

      val decodeOptions = BitmapFactory.Options().apply {
        inSampleSize = sampleSize
        inPreferredConfig = Bitmap.Config.ARGB_8888
      }

      val bitmap = BitmapFactory.decodeFile(path, decodeOptions) ?: return null
      bitmap.asImageBitmap()
    } catch (e: Exception) {
      null
    }
  }
}
