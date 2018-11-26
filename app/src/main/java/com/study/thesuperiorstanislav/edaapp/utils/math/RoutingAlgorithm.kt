package com.study.thesuperiorstanislav.edaapp.utils.math

import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject

abstract class RoutingAlgorithm(protected val drawMatrix:Array<Array<DrawObject?>>) {
    protected val xMax:Int = drawMatrix.first().size
    protected val yMax:Int = drawMatrix.size
    protected val occupied = -1.0
    protected val empty = -2.0
    protected val pointDirsOrg = arrayOf(
            Point(1, 0), Point(0, 1),
            Point(-1, 0), Point(0, -1))
    protected val pointDirsOrgDiagonal = arrayOf(
            Point(1, 0), Point(1, 1),
            Point(0, 1), Point(-1, 1),
            Point(-1, 0), Point(-1, -1),
            Point(0, -1), Point(1, -1))

    abstract fun doTheThing(startPoint: Point, endPoint: Point, isDiagonal: Boolean): AlgorithmReturnData?

    //For Testing
    protected fun drawPathNet(pathNet: Array<Array<Double>>, steps: Int) {
        System.out.println("Steps $steps")
        pathNet.forEachIndexed { _, row ->
            var str = ""
            row.forEachIndexed { x, point ->
                val strPoint = when {
                    point > 99 -> "$point"
                    point > 9 -> " $point"
                    point == occupied -> " (|) "
                    point == empty -> "     "
                    else -> "  $point"
                }
                str += if (x == 0)
                    "|$strPoint|"
                else
                    "$strPoint|"
            }
            System.out.println(str)
        }
    }

    data class AlgorithmReturnData(val path: List<Point>, val steps: Int)
}