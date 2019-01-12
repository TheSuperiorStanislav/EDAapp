package com.study.thesuperiorstanislav.edaapp.utils.file

import android.database.Cursor
import android.provider.OpenableColumns
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Circuit
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object CircuitFileReader {
    fun getFileName(cursor: Cursor?):String {
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME)).replace(".net", " ", true)
            }
        }
        return ""
    }

    @Throws(IOException::class)
    fun readTextFromInputStream(inputStream: InputStream?):Circuit? {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val line = reader.readLine()
        return try {
            if (line == "\$PACKAGES")
                AllegroFile.read(reader)
            else
                Calay90File.read(line,reader)

        }catch (e:ReadNetFileException){
            null
        }
    }
}