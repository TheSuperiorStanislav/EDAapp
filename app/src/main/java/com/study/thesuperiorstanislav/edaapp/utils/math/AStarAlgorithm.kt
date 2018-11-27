package com.study.thesuperiorstanislav.edaapp.utils.math

import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject
import kotlin.collections.HashSet

class AStarAlgorithm(drawMatrix:Array<Array<DrawObject?>>): RoutingAlgorithm(drawMatrix) {
    override fun doTheThing(startPoint: Point, endPoint: Point, isDiagonal: Boolean): AlgorithmReturnData {
        val openSet = HashSet<Point>()
        openSet.add(startPoint)
        val closeSet= HashSet<Point>()
        val gScore = createPathNet()
        gScore[startPoint.y][startPoint.x] = 0.0
        gScore[endPoint.y][endPoint.x] = empty
        val fScore = createPathNet()
        fScore[startPoint.y][startPoint.x] = calculateHeuristicCost(startPoint, endPoint)


        var steps = 0
        while (!openSet.isEmpty()){
            val curNode = findLowestFScoreValue(openSet,fScore)
            if (curNode == endPoint)
                return AlgorithmReturnData(restorePath(endPoint,gScore,isDiagonal),steps)

            openSet.remove(curNode)
            closeSet.add(curNode)

            if (isDiagonal) {
                pointDirsOrgDiagonal.filter { checkDirs(it.merge(curNode)) && !closeSet.contains(it.merge(curNode)) }
                        .forEach { point ->
                            val neighbor = curNode.merge(point)
                            if (gScore[neighbor.y][neighbor.x] != occupied && checkSquare(curNode, neighbor, gScore)) {
                                val tentativeGScore = if (isPointDirDiagonal(curNode, neighbor))
                                    gScore[curNode.y][curNode.x] + 1.5
                                else
                                    gScore[curNode.y][curNode.x] + 1.0

                                steps++

                                openSet.add(neighbor)
                                if (tentativeGScore < gScore[neighbor.y][neighbor.x]) {
                                    gScore[neighbor.y][neighbor.x] = tentativeGScore
                                    fScore[neighbor.y][neighbor.x] = gScore[neighbor.y][neighbor.x] + calculateHeuristicCost(neighbor, endPoint)
                                }
                            }
                        }
            }else{
                pointDirsOrg.filter { checkDirs(it.merge(curNode)) && !closeSet.contains(it.merge(curNode)) }
                        .forEach { point ->
                            val neighbor = curNode.merge(point)
                            if (gScore[neighbor.y][neighbor.x] != occupied) {
                                val tentativeGScore = gScore[curNode.y][curNode.x] + 1.0

                                steps++

                                openSet.add(neighbor)
                                if (tentativeGScore < gScore[neighbor.y][neighbor.x]) {
                                    gScore[neighbor.y][neighbor.x] = tentativeGScore
                                    fScore[neighbor.y][neighbor.x] = gScore[neighbor.y][neighbor.x] + calculateHeuristicCost(neighbor, endPoint)
                                }
                            }
                        }
            }
        }
        return AlgorithmReturnData(null,steps)
    }

    private fun findLowestFScoreValue(openSet: Set<Point>, fScore: Array<Array<Double>>): Point {
        var value = Double.POSITIVE_INFINITY
        var pointToReturn = Point(0,0)
        openSet.forEach {point->
            if (fScore[point.y][point.x] < value) {
                value = fScore[point.y][point.x]
                pointToReturn = point
            }
        }
        return pointToReturn
    }

    private fun calculateHeuristicCost(startPoint: Point, endPoint: Point):Double{
        val x2 = Math.pow((startPoint.x - endPoint.x).toDouble(),2.0)
        val y2 = Math.pow((startPoint.y - endPoint.y).toDouble(),2.0)
        return Math.sqrt(x2 + y2)
    }
}