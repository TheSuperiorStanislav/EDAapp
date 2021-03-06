package com.study.thesuperiorstanislav.edaapp.editor.domain.model

data class Point(var x: Int, var y: Int){

    fun merge(point: Point):Point {
        return Point(this.x + point.x, this.y + point.y)
    }

    fun merge(x: Int, y: Int):Point {
        return Point(this.x + x, this.y + y)
    }

    override fun toString(): String {
        return "X: $x, Y: $y"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (this::class != other::class) {
            return false
        }

        val otherPoint = other as Point
        if (this.x != otherPoint.x) {
            return false
        }
        if (this.y != otherPoint.y) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}