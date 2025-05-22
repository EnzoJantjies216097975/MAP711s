package com.map711s.namibiahockey.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.IOException

object ImageUtils {

    fun resizeImage(context: Context, uri: Uri, maxWidth: Int, maxHeight: Int): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap != null) {
                val ratio = minOf(
                    maxWidth.toFloat() / originalBitmap.width,
                    maxHeight.toFloat() / originalBitmap.height
                )

                val newWidth = (originalBitmap.width * ratio).toInt()
                val newHeight = (originalBitmap.height * ratio).toInt()

                Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
            } else null
        } catch (e: IOException) {
            null
        }
    }

    fun getImageSize(context: Context, uri: Uri): Pair<Int, Int>? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            Pair(options.outWidth, options.outHeight)
        } catch (e: IOException) {
            null
        }
    }

    fun isImageTooLarge(context: Context, uri: Uri, maxSizeMB: Int = 5): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val size = inputStream?.available() ?: 0
            inputStream?.close()

            size > maxSizeMB * 1024 * 1024
        } catch (e: IOException) {
            true
        }
    }
}