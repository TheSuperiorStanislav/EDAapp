package com.study.thesuperiorstanislav.edaapp.utils.file

import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Element
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Net
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Pin
import java.io.BufferedReader

object Calay90File {
    fun read(firstLine: String?, reader: BufferedReader): Circuit {
        val listElements = mutableListOf<Element>()
        val listNets = mutableListOf<Net>()
        val listPins = mutableListOf<Pin>()

        var line = firstLine
        var lastNet = Net("")
        var isNetOver = true
        while (line != null) {
            val splitLine = line
                    .split(" ")
                    .asSequence()
                    .filter { it != "" }
                    .toMutableList()

            if (isNetOver) {
                listNets.add(Net(splitLine[0]))
                lastNet = listNets.last()
                splitLine.remove(listNets.last().toString())
            }

            isNetOver = line.last() == ';'

            splitLine.forEach { str ->

                val splitIt = str
                        .replace(",", "")
                        .replace(";", "")
                        .replace("(", "")
                        .replace(")", "")
                        .split("'")

                if (!listElements.contains(Element(splitIt.first()))) {
                    listElements.add(Element(splitIt.first()))
                }

                val curElement = listElements.find { it == Element(splitIt.first()) }

                curElement?.setPin(splitIt.last().toInt() - 1, true)
                lastNet.addPin(curElement?.getPins()!![splitIt.last().toInt() - 1])
                listPins.add(curElement.getPins()[splitIt.last().toInt() - 1])

            }
            line = reader.readLine()
        }
        return Circuit(listElements,listNets,listPins)
    }
}