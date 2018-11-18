package com.study.thesuperiorstanislav.edaapp.utils.math

import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject

class LeeAlgorithm(private val xMax:Int,
                   private val yMax:Int,
                   private val drawMatrix:Array<Array<DrawObject?>>) {
    private val occupied = -1
    private val empty = -2
    private val pointDirsOrg = arrayOf(Point(1, 0), Point(0, 1),
            Point(-1, 0), Point(0, -1))
    private val pointDirsOrgDiagonal = arrayOf(Point(1, 0), Point(1, 1),
            Point(0, 1), Point(-1, 1),
            Point(-1, 0), Point(-1, -1),
            Point(0, -1), Point(1, -1))

    fun doTheThing(startPoint: Point, endPoint: Point, isDiagonal: Boolean): LeeReturnData? {
        val pathNet = createPathNet()
        pathNet[startPoint.y][startPoint.x] = 0
        pathNet[endPoint.y][endPoint.x] = empty
        var curPoint = 0
        var steps = 0
        do {
            var stillSearching = false
            pathNet.forEachIndexed { y, row ->
                row.forEachIndexed { x, point ->
                    if (point == curPoint)
                        if (isDiagonal)
                            pointDirsOrgDiagonal.filter { checkDirs(it.x + x, it.y + y) }
                                    .forEach { p ->
                                        if (fillSquare(p.x + x, p.y + y, pathNet, curPoint)) {
                                            stillSearching = true
                                            steps++
                                        }
                                    }
                        else
                            pointDirsOrg.filter { checkDirs(it.x + x, it.y + y) }
                                    .forEach { p ->
                                        if (fillSquare(p.x + x, p.y + y, pathNet, curPoint)) {
                                            stillSearching = true
                                            steps++
                                        }
                                    }
                }
            }
            drawPathNet(pathNet, steps)
            curPoint++
        } while (stillSearching && pathNet[endPoint.y][endPoint.x] == empty)
        return if (pathNet[endPoint.y][endPoint.x] == empty)
            null
        else
            LeeReturnData(restorePath(endPoint, pathNet, isDiagonal), steps)
    }

    private fun createPathNet(): Array<Array<Int>> {
        return Array(yMax) { y ->
            Array(xMax) { x ->
                if (drawMatrix[y][x] == null)
                    empty
                else
                    occupied
            }
        }
    }

    private fun checkDirs(xDir: Int, yDir: Int): Boolean {
        return yDir >= 0 && xDir >= 0 && yDir < yMax && xDir < xMax
    }

    private fun fillSquare(xDir: Int, yDir: Int, pathNet: Array<Array<Int>>, curPoint: Int): Boolean {
        return if (pathNet[yDir][xDir] == empty) {
            pathNet[yDir][xDir] = 1 + curPoint
            true
        } else
            false
    }

    private fun restorePath(endPoint: Point, pathNet: Array<Array<Int>>, isDiagonal: Boolean): List<Point> {
        val pathList = mutableListOf<Point>()
        var len = pathNet[endPoint.y][endPoint.x]
        var x = endPoint.x
        var y = endPoint.y
        pathList.add(endPoint)
        while (len > 0) {
            len--
            var isFoundDir = false
            if (isDiagonal)
                pointDirsOrgDiagonal.forEach { p ->
                    val xDir = p.x + x
                    val yDir = p.y + y
                    if (checkDirs(xDir, yDir) && pathNet[yDir][xDir] == len
                            && !isFoundDir) {
                        x = xDir
                        y = yDir
                        isFoundDir = true
                        pathList.add(0, Point(x, y))
                    }
                }
            else
                pointDirsOrg.forEach { p ->
                    val xDir = p.x + x
                    val yDir = p.y + y
                    if (checkDirs(xDir, yDir) && pathNet[yDir][xDir] == len
                            && !isFoundDir) {
                        x = xDir
                        y = yDir
                        isFoundDir = true
                        pathList.add(0, Point(x, y))
                    }
                }
        }
        return pathList
    }

    //For Testing
    private fun drawPathNet(pathNet: Array<Array<Int>>, steps: Int) {
        System.out.println("Steps $steps")
        pathNet.forEachIndexed { _, row ->
            var str = ""
            row.forEachIndexed { x, point ->
                val strPoint = when {
                    point > 99 -> "$point"
                    point > 9 -> " $point"
                    point == occupied -> "(|)"
                    point == empty -> "   "
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

    data class LeeReturnData(val path: List<Point>, val steps: Int)
}