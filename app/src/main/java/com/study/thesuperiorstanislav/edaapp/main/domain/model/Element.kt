package com.study.thesuperiorstanislav.edaapp.main.domain.model



class Element(private val name: String){
    private val pins = Array(16){ _ -> ""}
    private val typeElement = makeType(name)

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

    fun getPins(): Array<String> {
        return pins
    }

    fun setPin(num: Int,pin: String){
        pins[num] = pin
    }

    private fun makeType(name: String): String{
        var type = ""
        name.forEach {
            if (it.isLetter())
                type += it
        }
        return type
    }

}