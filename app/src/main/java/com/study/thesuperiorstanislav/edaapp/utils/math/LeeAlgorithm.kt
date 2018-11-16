package com.study.thesuperiorstanislav.edaapp.utils.math

import com.study.thesuperiorstanislav.edaapp.main.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.DrawObject

class LeeAlgorithm(private val xMax:Int,
                   private val yMax:Int,
                   private val drawMatrix:Array<Array<DrawObject?>>) {
    private val occupied = -1
    private val empty = -2
    private val pointDirs = arrayOf(Point(1,0), Point(0,1), Point(-1,0),Point(0,-1))
    private var step = 0

    fun doTheThing(startPoint: Point, endPoint: Point):List<Point>? {
        val pathNet = createPathNet()
        pathNet[startPoint.y][startPoint.x] = 0
        pathNet[endPoint.y][endPoint.x] = empty
        var curPoint = 0
        step = 0
        do {
            var stillSearching = false
            pathNet.forEachIndexed { y, row ->
                row.forEachIndexed { x, point ->
                    if (point == curPoint)
                        pointDirs.forEach { p ->
                            val xDir = p.x + x
                            val yDir = p.y + y
                            if (checkDirs(xDir, yDir) && pathNet[yDir][xDir] == empty) {
                                stillSearching = true
                                pathNet[yDir][xDir] = 1 + curPoint
                                step++
                                drawPathNet(pathNet, step)
                            }
                        }
                }
            }
            curPoint++
        } while (stillSearching && pathNet[endPoint.y][endPoint.x] == empty)
        return if (pathNet[endPoint.y][endPoint.x] == empty)
            null
        else
            restorePath(endPoint, pathNet)

    }

    private fun createPathNet(): Array<Array<Int>> {
        return Array(drawMatrix.size) { y ->
            Array(drawMatrix.first().size) { x ->
                if (drawMatrix[y][x] == null)
                    empty
                else
                    occupied
            }
        }
    }

    private fun checkDirs(xDir:Int, yDir:Int):Boolean{
        return yDir>=0 && xDir>=0 && yDir<yMax && xDir<xMax
    }

    private fun restorePath(endPoint: Point,pathNet:Array<Array<Int>>):List<Point>{
        val pathList = mutableListOf<Point>()
        var len = pathNet[endPoint.y][endPoint.x]
        var x = endPoint.x
        var y = endPoint.y
        while (len>0){
            len--
            var isFoundDir = false
            pointDirs.forEach { p ->
                val xDir = p.x + x
                val yDir = p.y + y
                if (checkDirs(xDir, yDir) && pathNet[yDir][xDir] == len
                && !isFoundDir) {
                    x = xDir
                    y = yDir
                    isFoundDir = true
                    pathList.add(0, Point(x,y))
                }
            }
        }
        return pathList
    }

    //For Testing
    private fun drawPathNet(pathNet:Array<Array<Int>>,step :Int){
        pathNet.forEachIndexed { y, row ->
            var str = ""
            row.forEachIndexed { x, point ->
                val strPoint = when {
                    point > 99 -> "$point"
                    point > 9 || point < 0 -> " $point"
                    else -> "  $point"
                }
                str += if (x == 0)
                    "|$strPoint|"
                else
                    "$strPoint|"
            }
            System.out.println(str)
        }
        System.out.println("Step $step")
    }
}