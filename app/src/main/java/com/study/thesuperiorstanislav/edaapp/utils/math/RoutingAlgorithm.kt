package com.study.thesuperiorstanislav.edaapp.utils.math

import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject

abstract class RoutingAlgorithm(protected val drawMatrix:Array<Array<DrawObject?>>) {
    private val xMax:Int = drawMatrix.first().size
    private val yMax:Int = drawMatrix.size
    protected val occupied = -1.0
    protected val empty = Double.POSITIVE_INFINITY
    protected val pointDirsOrg = arrayOf(
            Point(1, 0), Point(0, 1),
            Point(-1, 0), Point(0, -1))
    protected val pointDirsOrgDiagonal = arrayOf(
            Point(1, 0), Point(1, 1),
            Point(0, 1), Point(-1, 1),
            Point(-1, 0), Point(-1, -1),
            Point(0, -1), Point(1, -1))

    abstract fun doTheThing(startPoint: Point, endPoint: Point, isDiagonal: Boolean): AlgorithmReturnData?

    protected fun createPathNet(): Array<Array<Double>> {
        return Array(yMax) { y ->
            Array(xMax) { x ->
                if (drawMatrix[y][x] == null)
                    empty
                else
                    occupied
            }
        }
    }

    protected fun checkDirs(point: Point): Boolean {
        return point.y >= 0 && point.x >= 0 && point.y < yMax && point.x < xMax
    }

    protected fun checkSquare(pointOrg: Point, point: Point, pathNet: Array<Array<Double>>): Boolean {
        return when (point) {
            pointOrg.merge(pointDirsOrgDiagonal[1]) -> {
                val neighbor1 = pointOrg.merge(pointDirsOrgDiagonal[0])
                val neighbor2 = pointOrg.merge(pointDirsOrgDiagonal[2])
                return canPassThought(neighbor1,neighbor2,pathNet)

            }
            pointOrg.merge(pointDirsOrgDiagonal[3]) -> {
                val neighbor1 = pointOrg.merge(pointDirsOrgDiagonal[2])
                val neighbor2 = pointOrg.merge(pointDirsOrgDiagonal[4])
                return canPassThought(neighbor1,neighbor2,pathNet)
            }
            pointOrg.merge(pointDirsOrgDiagonal[5]) -> {
                val neighbor1 = pointOrg.merge(pointDirsOrgDiagonal[4])
                val neighbor2 = pointOrg.merge(pointDirsOrgDiagonal[6])
                return canPassThought(neighbor1,neighbor2,pathNet)
            }
            pointOrg.merge(pointDirsOrgDiagonal[7]) -> {
                val neighbor1 = pointOrg.merge(pointDirsOrgDiagonal[6])
                val neighbor2 = pointOrg.merge(pointDirsOrgDiagonal[0])
                return canPassThought(neighbor1,neighbor2,pathNet)
            }
            else -> {
                true
            }
        }
    }

    protected fun isPointDirDiagonal(pointOrg: Point, point:Point):Boolean{
        return when (point) {
            pointOrg.merge(pointDirsOrgDiagonal[1]),pointOrg.merge(pointDirsOrgDiagonal[3]),
            pointOrg.merge(pointDirsOrgDiagonal[5]),pointOrg.merge(pointDirsOrgDiagonal[7])-> {
                return true

            }
            else -> {
                false
            }
        }
    }

    protected fun restorePath(endPoint: Point, pathNet: Array<Array<Double>>, isDiagonal: Boolean): List<Point> {
        val pathList = mutableListOf<Point>()
        var len = pathNet[endPoint.y][endPoint.x] - 1
        var x = endPoint.x
        var y = endPoint.y
        pathList.add(endPoint)
        while (len >= 0) {
            var isFoundDir = false
            if (isDiagonal)
                pointDirsOrgDiagonal.forEach { p ->
                    val xDir = p.x + x
                    val yDir = p.y + y
                    if (checkDirs(Point(xDir, yDir)) && !isFoundDir) {
                        if (pathNet[yDir][xDir] == len) {
                            x = xDir
                            y = yDir
                            isFoundDir = true
                            pathList.add(0, Point(x, y))
                            len--
                        } else if (pathNet[yDir][xDir] == len - 0.5) {
                            x = xDir
                            y = yDir
                            isFoundDir = true
                            pathList.add(0, Point(x, y))
                            len-= 1.5
                        }
                    }
                }
            else {
                pointDirsOrg.forEach { p ->
                    val xDir = p.x + x
                    val yDir = p.y + y
                    if (checkDirs(Point(xDir, yDir)) && pathNet[yDir][xDir] == len
                            && !isFoundDir) {
                        x = xDir
                        y = yDir
                        isFoundDir = true
                        pathList.add(0, Point(x, y))
                    }
                }
                len--
            }
        }
        return pathList
    }

    //For Testing
    protected fun drawPathNet(pathNet: Array<Array<Double>>, steps: Int) {
        System.out.println("Steps $steps")
        pathNet.forEachIndexed { _, row ->
            var str = ""
            row.forEachIndexed { x, point ->
                val strPoint = when {
                    point == empty -> "     "
                    point == occupied -> " (|) "
                    point > 99 -> "$point"
                    point > 9 -> " $point"
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

    private fun canPassThought(neighbor1:Point, neighbor2: Point, pathNet: Array<Array<Double>>):Boolean{
        return if (checkDirs(neighbor1) && checkDirs(neighbor2)) {
            (pathNet[neighbor1.y][neighbor1.x] != occupied
                    || pathNet[neighbor2.y][neighbor2.x] != occupied)
        } else {
            true
        }
    }

    data class AlgorithmReturnData(val path: List<Point>, val steps: Int)
}