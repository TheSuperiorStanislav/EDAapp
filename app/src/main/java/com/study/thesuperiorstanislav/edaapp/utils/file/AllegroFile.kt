package com.study.thesuperiorstanislav.edaapp.utils.file

import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Element
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Net
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Pin
import java.io.BufferedReader
import java.lang.StringBuilder

object AllegroFile {
    fun read(reader: BufferedReader):Circuit {

        val listElements = mutableListOf<Element>()
        val listNets = mutableListOf<Net>()
        val listPins = mutableListOf<Pin>()

        var line = reader.readLine()
        while (line != "\$NETS") {
            line = reader.readLine()
        }
        line = reader.readLine()
        var lastNet = Net("")
        var isNetOver = true
        while (line != "\$END") {
            val splitLine = line
                    .split(" ")
                    .asSequence()
                    .filter { it != "" }
                    .toMutableList()

            if (isNetOver) {
                listNets.add(Net(splitLine[0]
                        .replace(";", "")))
                lastNet = listNets.last()
                splitLine.remove(listNets.last().toString() + ";")
            }

            isNetOver = line.last() != ','

            splitLine.forEach { str ->

                val splitIt = str
                        .replace(",", "")
                        .split(".")

                if (!listElements.contains(Element(splitIt.first()))) {
                    listElements.add(Element(splitIt.first()))
                }

                val curElement = listElements.find { it == Element(splitIt.first()) }

                curElement?.setPin(splitIt.last().toInt() - 1,true,lastNet)
                lastNet.addPin(curElement?.getPins()!![splitIt.last().toInt() - 1])
                listPins.add(curElement.getPins()[splitIt.last().toInt() - 1])
            }
            line = reader.readLine()
        }
        return Circuit(listElements,listNets,listPins)
    }

    fun write(circuit: Circuit):String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("\$PACKAGES\n")
        circuit.listElements.forEach { element->
            stringBuilder.append("¯\\(°_o)/¯! ¯\\(°_o)/¯; $element\n")
        }
        stringBuilder.append("\$NETS\n")
        circuit.listNets.forEach { net ->
            stringBuilder.append("$net;  ")
            var count = 0
            net.getPins().forEach { pin ->
                if (count == 3){
                    if (pin != net.getPins().last())
                        stringBuilder.append("$pin,\n     ")
                    else
                        stringBuilder.append("$pin\n")
                    count = 0
                }else {
                    if (pin != net.getPins().last())
                        stringBuilder.append("$pin ")
                    else
                        stringBuilder.append("$pin\n")
                    count++
                }
            }
        }
        stringBuilder.append("\$END\n")
        return stringBuilder.toString()
    }
}