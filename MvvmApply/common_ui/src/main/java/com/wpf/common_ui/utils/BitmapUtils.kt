package com.anker.common.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File
import java.io.IOException


object BitmapUtils {
    fun equalRatioScale(path: String, targetW: Int, targetH: Int): Bitmap? {
        // 获取option
        val options = BitmapFactory.Options()
        // inJustDecodeBounds设置为true,这样使用该option decode出来的Bitmap是null，
        // 只是把长宽存放到option中
        options.inJustDecodeBounds = true
        // 此时bitmap为null
        var bitmap = BitmapFactory.decodeFile(path, options)
        var inSampleSize = 1 // 1是不缩放
        // 计算宽高缩放比例
        val inSampleSizeW = options.outWidth / targetW
        val inSampleSizeH = options.outHeight / targetH
        // 最终取大的那个为缩放比例，这样才能适配，例如宽缩放3倍才能适配屏幕，而
        // 高不缩放就可以，那样的话如果按高缩放，宽在屏幕内就显示不下了
        inSampleSize = if (inSampleSizeW > inSampleSizeH) {
            inSampleSizeW
        } else {
            inSampleSizeH
        }
        // 设置缩放比例
        options.inSampleSize = inSampleSize
        // 一定要记得将inJustDecodeBounds设为false，否则Bitmap为null
        options.inJustDecodeBounds = false
        bitmap = BitmapFactory.decodeFile(path, options)

        val degree = BitmapUtils.readPictureDegree(path)
        if (degree != 0) {
            bitmap = BitmapUtils.rotateImage(bitmap, degree.toFloat())
        }
        return bitmap
    }

    fun equalRatioScale(path: String, sampleSize: Int): Bitmap? {
        // 获取option
        val options = BitmapFactory.Options()
        // inJustDecodeBounds设置为true,这样使用该option decode出来的Bitmap是null，
        // 只是把长宽存放到option中
        options.inJustDecodeBounds = true
        // 此时bitmap为null
        var bitmap = BitmapFactory.decodeFile(path, options)
        // 设置缩放比例
        options.inSampleSize = sampleSize
        // 一定要记得将inJustDecodeBounds设为false，否则Bitmap为null
        options.inJustDecodeBounds = false
        bitmap = BitmapFactory.decodeFile(path, options)

        val degree = BitmapUtils.readPictureDegree(path)
        if (degree != 0) {
            bitmap = BitmapUtils.rotateImage(bitmap, degree.toFloat())
        }
        return bitmap
    }

    fun handleImage(context: Context, uri: Uri?): String {
        var imagePath: String? = null
        if (DocumentsContract.isDocumentUri(context, uri)) {
            //如果是document类型的uri，则通过document id处理
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                val id = docId.split(":".toRegex()).toTypedArray()[1] //解析出数字格式的id
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android,providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), docId.toLong())
                imagePath = getImagePath(context, contentUri, null)
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            imagePath = getImagePath(context, uri, null)
        }
        return imagePath ?: ""
    }

    //获得图片路径
    fun getImagePath(context: Context, uri: Uri?, selection: String?): String? {
        var path: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri!!, null, selection, null, null) //内容提供器
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)) //获取路径
            }
        }
        cursor?.close()
        return path
    }


    fun rotateImage(bitmap: Bitmap, degree: Float): Bitmap {
        //create new matrix
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    //获取图片的旋转角度
    fun readPictureDegree(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation: Int = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }

    /**
     * 将bitmap集合上下拼接,纵向(多个)
     * @param bgColor #4088F0
     * @param bitmaps
     * @return
     */
    fun drawMultiV(bitmaps: List<Bitmap>): Bitmap {
        var width = bitmaps[0].width
        var height = bitmaps[0].height
        for (i in 1 until bitmaps.size) {
            if (width < bitmaps[i].width) {
                width = bitmaps[i].width
            }
            height += bitmaps[i].height
        }
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        canvas.drawColor(Color.TRANSPARENT)
        val paint = Paint()
        paint.isDither = true
        canvas.drawBitmap(bitmaps[0], 0f, 0f, paint)
        var h = 0f
        for (j in 1 until bitmaps.size) {
            h += bitmaps[j].height
            canvas.drawBitmap(bitmaps[j], 0f, h, paint)
        }
        return result
    }


    /*过滤bitmap中的透明区域*/
    fun CropBitmapTransparency(sourceBitmap: Bitmap): Bitmap? {
        var minX = sourceBitmap.width
        var minY = sourceBitmap.height
        var maxX = -1
        var maxY = -1
        for (y in 0 until sourceBitmap.height) {
            for (x in 0 until sourceBitmap.width) {
                val alpha = sourceBitmap.getPixel(x, y) shr 24 and 255
                if (alpha > 0) // pixel is not 100% transparent
                {
                    if (x < minX) minX = x
                    if (x > maxX) maxX = x
                    if (y < minY) minY = y
                    if (y > maxY) maxY = y
                }
            }
        }
        return if (maxX < minX || maxY < minY) sourceBitmap else Bitmap.createBitmap(
            sourceBitmap,
            minX,
            minY,
            maxX - minX + 1,
            maxY - minY + 1
        ) // Bitmap is entirely transparent

        // crop bitmap to non-transparent area and return:
    }
}