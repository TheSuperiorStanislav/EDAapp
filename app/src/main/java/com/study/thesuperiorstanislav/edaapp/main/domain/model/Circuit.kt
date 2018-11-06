package com.study.thesuperiorstanislav.edaapp.main.domain.model

class Circuit(val listElements: MutableList<Element>,
              val listNets: MutableList<Net>,
              val listPins: MutableList<Pin>) {

    fun findElementByPoint(point: Point): Element? {
        listElements.forEach {
            if (it.getPoint() == point)
                return it
        }
        return null
    }

    fun findNetByPoint(point: Point): Net? {
        listNets.forEach {
            if (it.getPoint() == point)
                return it
        }
        return null
    }

    fun findPinByPoint(point: Point): Pin? {
        listElements.forEach {
            val pin: Pin? = it.getPinByPoint(point)
            if (pin != null)
                return pin
        }
        return null
    }

    fun generateElementName(type: String): String {
        var count = 1
        listElements.forEach {
            if (it.typeElement == type)
                count++
        }
        return "$type$count"
    }
}