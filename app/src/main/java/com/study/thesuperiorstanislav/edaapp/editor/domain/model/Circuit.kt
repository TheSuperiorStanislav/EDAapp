package com.study.thesuperiorstanislav.edaapp.editor.domain.model

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

    fun findElementByPinPoint(point: Point): Element?{
        listElements.forEach {
            val element = it.getPins().find {
                pin -> pin.getPoint() == point }?.getElement()
            if (element != null) {
                return element
            }
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

    fun findConnectedPinByPoint(point: Point): Pin? {
        listPins.forEach {
            if (it.getPoint() == point)
                return it
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

    fun clear(){
        listElements.clear()
        listNets.clear()
        listPins.clear()
    }
}