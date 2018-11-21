package com.study.thesuperiorstanislav.edaapp.editor.domain.model

import android.util.Log
import java.lang.Exception


class Element(private val name: String){
    private val point = Point(-1,-1)
    val typeElement = makeType(name)
    private val pinArraySize = initPinArraySize()
    private val pins = Array(pinArraySize){ num ->
        Pin("$name.${num + 1}",this)}


    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (this::class != other::class) {
            return false
        }

        val otherElement = other as Element
        if (this.name != otherElement.name) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var hash = 3 * this.name.hashCode()
        hash += 4 * hash + this.typeElement.hashCode()
        return hash
    }

    fun getPinArraySize(): Int{
        return pinArraySize
    }

    fun getPoint():Point {
        return point
    }

    fun getPins(): Array<Pin> {
        return pins
    }

    fun setPin(num: Int,boolean: Boolean, net: Net) {
        try {
            pins[num].setIsConnected(boolean)
            pins[num].setNet(net)
        }catch (e:Exception){
            Log.e("PIN",name)
        }
    }

    fun getPinByPoint(point: Point): Pin? {
        return pins.find { it.getPoint() == point }
    }

    fun move(x:Int, y:Int){
        point.x = x
        point.y = y
    }

    fun getDrawType():DrawType {
        return when (typeElement) {
            "DD" -> DrawType.TWENTY_FOUR_PART
            "X" -> DrawType.TWENTY_PART
            "DA" -> DrawType.EIGHTEEN_PART
            "SNP", "R" -> DrawType.TEN_PART
            "U" -> DrawType.SIX_PART
            "VD" -> DrawType.FOUR_PART
            "VT", "SA", "VS", "SB", "HA", "MLTP" -> DrawType.THREE_PART
            "HL", "C", "RX", "HLB" -> DrawType.TWO_PART
            else -> DrawType.TWO_PART
        }
    }

    private fun makeType(name: String): String{
        var type = ""
        name.forEach {
            if (it.isLetter())
                type += it
        }
        return type

    }

    private fun initPinArraySize(): Int {
        return when (getDrawType()) {
            DrawType.TWENTY_FOUR_PART -> 24
            DrawType.TWENTY_PART -> 20
            DrawType.EIGHTEEN_PART -> 18
            DrawType.SIXTEEN_PART -> 16
            DrawType.TEN_PART -> 10
            DrawType.EIGHT_PART -> 8
            DrawType.SIX_PART -> 6
            DrawType.FOUR_PART -> 4
            DrawType.THREE_PART -> 3
            DrawType.TWO_PART -> 2
        }
    }

    enum class DrawType{
        TWO_PART,
        THREE_PART,
        FOUR_PART,
        SIX_PART,
        EIGHT_PART,
        TEN_PART,
        SIXTEEN_PART,
        EIGHTEEN_PART,
        TWENTY_PART,
        TWENTY_FOUR_PART

    }

}