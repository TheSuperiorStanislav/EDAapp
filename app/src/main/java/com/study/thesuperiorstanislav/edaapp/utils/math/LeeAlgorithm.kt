package com.study.thesuperiorstanislav.edaapp.utils.math

import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject

class LeeAlgorithm(drawMatrix:Array<Array<DrawObject?>>): RoutingAlgorithm(drawMatrix) {

    override fun doTheThing(startPoint: Point, endPoint: Point, isDiagonal: Boolean): AlgorithmReturnData? {
        val pathNet = createPathNet()
        pathNet[startPoint.y][startPoint.x] = 0.0
        pathNet[endPoint.y][endPoint.x] = empty
        var curPoint = 0.0
        var curPointDig = 0.0
        var steps = 0
        do {
            var stillSearching = false
            pathNet.forEachIndexed { y, row ->
                row.forEachIndexed { x, point ->
                    if (point == curPoint)
                        if (isDiagonal) {
                            pointDirsOrgDiagonal.filter { checkDirs(it.merge(x,y)) }
                                    .forEach { p ->
                                        if (checkSquare(Point(x, y), p.merge(x,y), pathNet, curPoint)) {
                                            stillSearching = true
                                            steps++
                                        }
                                    }
                        } else
                            pointDirsOrg.filter { checkDirs(it.merge(x,y)) }
                                    .forEach { p ->
                                        if (fillSquare(p.merge(x,y), pathNet, curPoint)) {
                                            stillSearching = true
                                            steps++
                                        }
                                    }

                    if (point == curPointDig) {
                        if (isDiagonal)
                            pointDirsOrgDiagonal.filter { checkDirs(it.merge(x,y))}
                                    .forEach { p ->
                                        if (checkSquare(Point(x, y), p.merge(x,y), pathNet, curPointDig)) {
                                            stillSearching = true
                                            steps++
                                        }
                                    }
                    }
                }
            }
            //drawPathNet(pathNet, steps)
            curPoint++
            curPointDig = curPoint + 0.5
        } while (stillSearching && pathNet[endPoint.y][endPoint.x] == empty)
        return if (pathNet[endPoint.y][endPoint.x] == empty)
            null
        else
            AlgorithmReturnData(restorePath(endPoint, pathNet, isDiagonal), steps)
    }

    private fun createPathNet(): Array<Array<Double>> {
        return Array(yMax) { y ->
            Array(xMax) { x ->
                if (drawMatrix[y][x] == null)
                    empty
                else
                    occupied
            }
        }
    }

    private fun checkDirs(point: Point): Boolean {
        return point.y >= 0 && point.x >= 0 && point.y < yMax && point.x < xMax
    }

    private fun fillSquare(point: Point, pathNet: Array<Array<Double>>, curPoint: Double): Boolean {
        return if (pathNet[point.y][point.x] == empty || curPoint + 1 < pathNet[point.y][point.x]) {
            pathNet[point.y][point.x] = 1.0 + curPoint
            true
        } else
            false
    }

    private fun checkSquare(pointOrg: Point, point: Point, pathNet: Array<Array<Double>>, curPoint: Double): Boolean {
        return when (point) {
            pointOrg.merge(pointDirsOrgDiagonal[1]) -> {
                val neighbor1 = pointOrg.merge(pointDirsOrgDiagonal[0])
                val neighbor2 = pointOrg.merge(pointDirsOrgDiagonal[2])
                if (checkDirs(neighbor1) && checkDirs(neighbor2)) {
                    if (pathNet[neighbor1.y][neighbor1.x] != occupied
                            || pathNet[neighbor2.y][neighbor2.x] != occupied) {
                        fillSquareDiagonal(point, pathNet, curPoint)
                        true
                    } else
                        false
                } else {
                    fillSquareDiagonal(point, pathNet, curPoint)
                    true
                }

            }
            pointOrg.merge(pointDirsOrgDiagonal[3]) -> {
                val neighbor1 = pointOrg.merge(pointDirsOrgDiagonal[2])
                val neighbor2 = pointOrg.merge(pointDirsOrgDiagonal[4])
                if (checkDirs(neighbor1) && checkDirs(neighbor2)) {
                    if (pathNet[neighbor1.y][neighbor1.x] != occupied
                            || pathNet[neighbor2.y][neighbor2.x] != occupied) {
                        fillSquareDiagonal(point, pathNet, curPoint)
                        true
                    } else
                        false
                } else {
                    fillSquareDiagonal(point, pathNet, curPoint)
                    true
                }
            }
            pointOrg.merge(pointDirsOrgDiagonal[5]) -> {
                val neighbor1 = pointOrg.merge(pointDirsOrgDiagonal[4])
                val neighbor2 = pointOrg.merge(pointDirsOrgDiagonal[6])
                if (checkDirs(neighbor1) && checkDirs(neighbor2)) {
                    if (pathNet[neighbor1.y][neighbor1.x] != occupied
                            || pathNet[neighbor2.y][neighbor2.x] != occupied) {
                        fillSquareDiagonal(point, pathNet, curPoint)
                        true
                    } else
                        false
                } else {
                    fillSquareDiagonal(point, pathNet, curPoint)
                    true
                }
            }
            pointOrg.merge(pointDirsOrgDiagonal[7]) -> {
                val neighbor1 = pointOrg.merge(pointDirsOrgDiagonal[6])
                val neighbor2 = pointOrg.merge(pointDirsOrgDiagonal[0])
                if (checkDirs(neighbor1) && checkDirs(neighbor2)) {
                    if (pathNet[neighbor1.y][neighbor1.x] != occupied
                            || pathNet[neighbor2.y][neighbor2.x] != occupied) {
                        fillSquareDiagonal(point, pathNet, curPoint)
                        true
                    } else
                        false
                } else {
                    fillSquareDiagonal(point, pathNet, curPoint)
                    true
                }
            }
            else -> {
                fillSquare(point, pathNet, curPoint)
                true
            }
        }
    }

    private fun fillSquareDiagonal(point: Point, pathNet: Array<Array<Double>>, curPoint: Double):Boolean{
        return if (pathNet[point.y][point.x] == empty || curPoint + 1.5 < pathNet[point.y][point.x]) {
            pathNet[point.y][point.x] = 1.5 + curPoint
            true
        } else
            false
    }

    private fun restorePath(endPoint: Point, pathNet: Array<Array<Double>>, isDiagonal: Boolean): List<Point> {
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
}