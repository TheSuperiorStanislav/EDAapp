package com.study.thesuperiorstanislav.edaapp.main.domain.model


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

    fun setPin(num: Int,boolean: Boolean, net: Net){
        pins[num].setIsConnected(boolean)
        pins[num].setNet(net)
    }

    fun getPinByPoint(point: Point): Pin? {
        return pins.find { it.getPoint() == point }
    }

    fun move(x:Int, y:Int){
        point.x = x
        point.y = y
    }

    fun getDrawType():DrawType{
        return when (typeElement) {
            "DD", "X" -> {
                DrawType.SIXTEEN_PART
            }
            "SNP"-> {
                DrawType.TEN_PART
            }
            "DA"-> {
                DrawType.EIGHT_PART
            }
            "VT","SA" -> {
                DrawType.THREE_PART
            }
            "SB", "HL", "C", "VD", "RX" , "R" -> {
                DrawType.TWO_PART
            }
            else -> {
                DrawType.SIXTEEN_PART
            }
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

    private fun initPinArraySize(): Int{
        return when (getDrawType()) {
            DrawType.SIXTEEN_PART -> {
                16
            }
            DrawType.TEN_PART -> {
                10
            }
            DrawType.EIGHT_PART -> {
                8
            }
            DrawType.THREE_PART -> {
                3
            }
            DrawType.TWO_PART -> {
                2
            }
        }
    }

    enum class DrawType{
        TWO_PART,
        THREE_PART,
        EIGHT_PART,
        TEN_PART,
        SIXTEEN_PART
    }

}