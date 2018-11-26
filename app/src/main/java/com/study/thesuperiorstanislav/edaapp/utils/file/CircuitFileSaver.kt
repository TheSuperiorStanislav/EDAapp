package com.study.thesuperiorstanislav.edaapp.utils.file

import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Circuit
import java.io.File
import java.io.FileOutputStream

object CircuitFileSaver {
    private const val saveFileCode = 44
    private const val saveFileForShareCode = 46
    private val dirPathFileShot = "${Environment.getExternalStorageDirectory().absolutePath}/EDA/Circuits"
    private val dirPathShare = "${Environment.getExternalStorageDirectory().absolutePath}/EDA/.share"

    fun saveFile(fragment: Fragment,circuit:Circuit,circuitName:String,isAllegro:Boolean): String {
        if (verifyStoragePermissions(fragment,saveFileCode)) {
            var dirPath = dirPathFileShot
            val dir = File(dirPath)
            if (!dir.exists())
                dir.mkdirs()
            val file = File(dirPath, "$circuitName.net")
            if (!file.exists())
                file.createNewFile()
            val stream = FileOutputStream(file)
            try {
                if (isAllegro)
                    stream.write(AllegroFile.write(circuit).toByteArray())
                else
                    stream.write(Calay90File.write(circuit).toByteArray())
            } catch (e: Exception) {
                dirPath = ""
            } finally {
                stream.close()
                return dirPath
            }
        }else
            return ""
    }

    fun saveFileForShare(fragment: Fragment,circuit:Circuit,circuitName:String,isAllegro:Boolean): File? {
        if (verifyStoragePermissions(fragment,saveFileForShareCode)) {
            val dirPath = dirPathShare
            val dir = File(dirPath)
            if (!dir.exists())
                dir.mkdirs()
            var file: File? = File(dirPath, "$circuitName.net")
            if (!file!!.exists())
                file.createNewFile()
            val stream = FileOutputStream(file)
            try {
                if (isAllegro)
                    stream.write(AllegroFile.write(circuit).toByteArray())
                else
                    stream.write(Calay90File.write(circuit).toByteArray())
            } catch (e: Exception) {
                file = null
            } finally {
                stream.close()
                return file
            }
        }else
            return null
    }

    private fun verifyStoragePermissions(fragment: Fragment, requestCode: Int): Boolean {
        return if (ContextCompat.checkSelfPermission(fragment.context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            fragment.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), requestCode)
            false
        } else
            true
    }

}