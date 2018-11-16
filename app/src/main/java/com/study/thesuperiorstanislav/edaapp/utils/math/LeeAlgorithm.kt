package com.study.thesuperiorstanislav.edaapp.utils.math

import com.study.thesuperiorstanislav.edaapp.main.domain.model.Pin
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.DrawObject

class LeeAlgorithm(var xMax:Int,var yMax:Int,var listOfPins: List<Pin>,var drawMatrix:Array<Array<DrawObject?>>) {
    private val occupied = -1
    private val pointDirs = arrayOf(Point(1,0), Point(0,1), Point(-1,0),Point(0,-1))


    fun doTheThing(startPoint: Point, endPoint: Point) {
        val pathNet = createPathNet()
        pathNet[startPoint.y][startPoint.x] = 0
        pathNet[endPoint.y][endPoint.x] = null
        var curPoint = 0
        do {
            var stillSearching = true
            pathNet.forEachIndexed { y, row ->
                row.forEachIndexed { x, point ->
                    if (point == curPoint)
                        pointDirs.forEach {p->
                            val xDir = p.x + x
                            val yDir = p.y + y
                            if (checkDirs(xDir,yDir) && pathNet[yDir][xDir] == null){
                                stillSearching = false
                                pathNet[yDir][xDir] =1 + curPoint
                            }
                        }
                }
            }
            drawPathNet(pathNet)
            curPoint++
        }while(stillSearching && pathNet[endPoint.y][endPoint.x] == null)

    }

    private fun createPathNet(): Array<Array<Int?>> {
        return Array(drawMatrix.size) { y ->
            Array(drawMatrix.first().size) { x ->
                if (drawMatrix[y][x] == null)
                    null
                else
                    occupied
            }
        }
    }

    private fun checkDirs(xDir:Int, yDir:Int):Boolean{
        return yDir>=0 && xDir>=0 && yDir<yMax && xDir<xMax
    }

    private fun drawPathNet(pathNet:Array<Array<Int?>>){
        pathNet.forEachIndexed { y, row ->
            var str = ""
            row.forEachIndexed { x, point ->
                val strPoint = when {
                    point == null -> "null"
                    point > 99 -> " $point"
                    point > 9 -> "  $point"
                    else -> "   $point"
                }
                str += if (row.first() == point || row.last() == point)
                    strPoint
                else
                    "|$strPoint|"
            }
            System.out.println(str)
        }
    }
}