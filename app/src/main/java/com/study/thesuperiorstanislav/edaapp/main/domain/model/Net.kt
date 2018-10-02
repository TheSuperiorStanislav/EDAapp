package com.study.thesuperiorstanislav.edaapp.main.domain.model

class Net(private val name:String) {
    private val pins = mutableListOf<String>()

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

    fun getPins(): ArrayList<String> {
        return ArrayList(pins)
    }

    fun addPin(pin: String){
        pins.add(pin)
    }

    fun deletePin(pin: String){
        pins.remove(pin)
    }
}