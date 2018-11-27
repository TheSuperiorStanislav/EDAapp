package com.study.thesuperiorstanislav.edaapp.utils.math

import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject

class LeeAlgorithm(drawMatrix:Array<Array<DrawObject?>>): RoutingAlgorithm(drawMatrix) {

    override fun findPath(startPoint: Point, endPoint: Point, isDiagonal: Boolean): AlgorithmReturnData {
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
                                        val curNode = Point(x, y)
                                        val newPoint = p.merge(curNode)
                                        if (checkSquare(curNode, newPoint, pathNet)) {
                                            if (isPointDirDiagonal(curNode, newPoint))
                                                fillSquareDiagonal(newPoint, pathNet, curPoint)
                                            else
                                                fillSquare(newPoint, pathNet, curPoint)
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
                                        val curNode = Point(x, y)
                                        val newPoint = p.merge(curNode)
                                        if (checkSquare(curNode, newPoint, pathNet)) {
                                            if (isPointDirDiagonal(curNode, newPoint))
                                                fillSquareDiagonal(newPoint, pathNet, curPointDig)
                                            else
                                                fillSquare(newPoint, pathNet, curPointDig)
                                            stillSearching = true
                                            steps++
                                        }
                                    }
                    }
                }
            }
            curPoint++
            curPointDig = curPoint + 0.5
        } while (stillSearching && pathNet[endPoint.y][endPoint.x] == empty)
        return if (pathNet[endPoint.y][endPoint.x] == empty)
            return AlgorithmReturnData(null,steps)
        else
            AlgorithmReturnData(restorePath(endPoint, pathNet, isDiagonal), steps)
    }

    private fun fillSquare(point: Point, pathNet: Array<Array<Double>>, curPoint: Double): Boolean {
        return if (pathNet[point.y][point.x] == empty || curPoint + 1 < pathNet[point.y][point.x]) {
            pathNet[point.y][point.x] = 1.0 + curPoint
            true
        } else
            false
    }

    private fun fillSquareDiagonal(point: Point, pathNet: Array<Array<Double>>, curPoint: Double):Boolean{
        return if (pathNet[point.y][point.x] == empty || curPoint + 1.5 < pathNet[point.y][point.x]) {
            pathNet[point.y][point.x] = 1.5 + curPoint
            true
        } else
            false
    }
}