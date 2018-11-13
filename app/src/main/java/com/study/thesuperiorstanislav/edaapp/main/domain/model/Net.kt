package com.study.thesuperiorstanislav.edaapp.main.domain.model

class Net(private val name:String) {
    private val pins = mutableListOf<Pin>()
    private val point = Point(-1,-1)

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

        val otherNet = other as Net
        if (this.name != otherNet.name) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var hash = 3 * this.name.hashCode()
        hash += 4 * hash + this.pins.hashCode()
        return hash
    }

    fun getPoint(): Point{
        return point
    }

    fun getPins(): ArrayList<Pin> {
        return ArrayList(pins)
    }

    fun addPin(pin: Pin){
        pins.add(pin)
    }

    fun deletePin(pin: Pin){
        pins.remove(pin)
    }

    fun move(x:Int, y:Int){
        point.x = x
        point.y = y
    }
}