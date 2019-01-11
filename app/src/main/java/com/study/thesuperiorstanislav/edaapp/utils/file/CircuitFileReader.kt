package com.study.thesuperiorstanislav.edaapp.utils.file

import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Circuit
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object CircuitFileReader {
    @Throws(IOException::class)
    fun readTextFromUri(inputStream: InputStream?):Circuit? {
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