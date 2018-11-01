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

    fun getPins(): Array<Pin> {
        return pins
    }

    fun setPin(num: Int,boolean: Boolean){
        pins[num].setIsConnected(boolean)
    }

    fun move(x:Int, y:Int){
        point.x = x
        point.y = y
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
        return when (typeElement) {
            "DD", "X" -> {
                16
            }
            "SB", "HL", "C", "VD", "RX" , "R" -> {
                2
            }
            else -> {
                0
            }
        }
    }

}