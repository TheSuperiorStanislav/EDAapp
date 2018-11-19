package com.study.thesuperiorstanislav.edaapp.utils.file

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Environment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


object ScreenShotTaker {
    private const val saveScreenShotCode = 43

    fun takeScreenShot(fragment: Fragment,view: View,circuitName:String): String {
        val millis = Date().time
        val timeStamp = SimpleDateFormat
                .getDateTimeInstance()
                .format(millis)
        return if (verifyStoragePermissions(fragment,saveScreenShotCode))
            saveScreenShot(getScreenShot(view), "$circuitName-$timeStamp.png")
        else
            ""
    }

    private fun verifyStoragePermissions(fragment: Fragment,requestCode: Int): Boolean {
        return if (ContextCompat.checkSelfPermission(fragment.context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            fragment.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), requestCode)
            false
        } else
            true
    }

    private fun getScreenShot(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }

    private fun saveScreenShot(bm: Bitmap, fileName: String):String {
        val dirPath = "${Environment.getExternalStorageDirectory().absolutePath}/EDA/ScreenShots"
        val dir = File(dirPath)
        if (!dir.exists())
            dir.mkdirs()
        val file = File(dirPath, fileName)
        val fOut = FileOutputStream(file)
        bm.compress(Bitmap.CompressFormat.PNG, 100, fOut)
        fOut.flush()
        fOut.close()
        return dirPath
    }
}