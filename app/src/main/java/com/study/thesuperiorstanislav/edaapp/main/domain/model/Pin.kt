package com.study.thesuperiorstanislav.edaapp.main.domain.model


class Pin(private val name: String,private var element: Element) {
    private val point = Point(-1,-1)
    private var isConnected = false
    private var net: Net? = null

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

    fun getName():String{
        return name
    }

    fun getPoint(): Point{
        return point
    }

    fun getElement():Element{
        return element
    }

    fun setNet(net: Net){
        isConnected = true
        this.net?.deletePin(this)
        this.net = net
    }

    fun getNet():Net? {
        return net
    }

    fun setIsConnected(boolean: Boolean){
        isConnected = boolean
        if (!isConnected())
            removeFromNet()

    }

    fun removeFromNet(){
        isConnected = false
        net?.deletePin(this)
        net = null
    }

    fun isConnected():Boolean{
        return isConnected
    }

    fun move(x:Int, y:Int){
        point.x = x
        point.y = y
    }


}