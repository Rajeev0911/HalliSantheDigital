package com.halliSanthe.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageCompressHelper {

    private const val MAX_WIDTH = 800
    private const val MAX_HEIGHT = 800
    private const val QUALITY = 70

    fun compressImage(
        context: Context,
        imageUri: Uri?
    ): File? {

        return try {

            val inputStream =
                context.contentResolver
                    .openInputStream(imageUri!!)

            val originalBitmap =
                BitmapFactory.decodeStream(inputStream)

            inputStream?.close()

            if (originalBitmap == null) {
                return null
            }

            val scaledBitmap = scaleBitmap(
                originalBitmap,
                MAX_WIDTH,
                MAX_HEIGHT
            )

            val outputDir = context.cacheDir

            val outputFile = File.createTempFile(
                "compressed_",
                ".jpg",
                outputDir
            )

            val fos = FileOutputStream(outputFile)

            scaledBitmap.compress(
                Bitmap.CompressFormat.JPEG,
                QUALITY,
                fos
            )

            fos.flush()
            fos.close()

            outputFile

        } catch (e: IOException) {

            e.printStackTrace()

            null
        }
    }

    private fun scaleBitmap(
        bitmap: Bitmap,
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap {

        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxWidth &&
            height <= maxHeight
        ) {
            return bitmap
        }

        val scaleWidth =
            maxWidth.toFloat() / width

        val scaleHeight =
            maxHeight.toFloat() / height

        val scale =
            minOf(scaleWidth, scaleHeight)

        val newWidth =
            (width * scale).toInt()

        val newHeight =
            (height * scale).toInt()

        return Bitmap.createScaledBitmap(
            bitmap,
            newWidth,
            newHeight,
            true
        )
    }

    fun compressToBase64(context: Context, imageUri: Uri?): String? {
        val file = compressImage(context, imageUri) ?: return null
        val bytes = file.readBytes()
        return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
    }
}