package com.study.thesuperiorstanislav.edaapp.main.domain.model


class Pin(private val name: String,private var element: Element) {
    private val point = Point(-1,-1)
    private var isConnected = false

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

        val otherPin = other as Pin
        if (this.name != otherPin.name) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + element.hashCode()
        result = 31 * result + point.hashCode()
        return result
    }

    fun getPoint(): Point{
        return point
    }

    fun setIsConnected(boolean: Boolean){
        isConnected = boolean
    }

    fun IsConnected():Boolean{
        return isConnected
    }

    fun move(x:Int, y:Int){
        point.x = x
        point.y = y
    }


}