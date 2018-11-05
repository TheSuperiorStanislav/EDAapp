package com.study.thesuperiorstanislav.edaapp.utils.graphics

import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Element
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Net
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.DrawObject
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.DrawPoint
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.DrawType
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.ObjectType
import org.jetbrains.anko.collections.forEachReversedWithIndex

class Placer(private val drawMatrix: Array<Array<DrawObject?>>,
             private val sizeX: Int, private val sizeY: Int,
             private val step: Float) {

    fun initDrawMatrix(circuit: Circuit): Array<Array<DrawObject?>>{
        circuit.listElements.forEach {
            placeElement(it)
        }
        circuit.listNets.forEach {
            placeNet(it)
        }
        return drawMatrix
    }

    fun initDrawMatrix(element: Element): Array<Array<DrawObject?>>{
        when (element.typeElement) {
            "DD", "X" -> {
                placeElementHorizontal(element,0,1)
            }
            "SB", "HL", "C", "VD", "RX", "R" -> {
                placeElementHorizontal(element,3, 1)
            }
        }
        return drawMatrix
    }

    fun addElement(element: Element, point: Point): Boolean {
        return when {
            checkElementPositionHorizontal(element, point.x, point.y) -> {
                placeElementHorizontal(element, point.x, point.y)
                true
            }
            checkElementPositionVertical(element, point.x, point.y) -> {
                placeElementVertical(element, point.x, point.y)
                true
            }
            else -> false
        }
    }

    fun addNet(point: Point): Boolean{
        return if (!checkNetPosition(point.x,point.y)) {
            false
        } else {
            placeNet(point)
            true
        }
    }

    fun moveElement(element: Element,startPoint: Point, endPoint: Point):Boolean{
        when (element.typeElement) {
            "DD", "X" -> {
                return if (drawMatrix[startPoint.y][startPoint.x + 2] != null){
                    removeElement(element)
                    if (checkElementPositionHorizontal(element,endPoint.x,endPoint.y)) {
                        place16PartHorizontal(element, endPoint.x, endPoint.y)
                        true
                    }else{
                        place16PartHorizontal(element, startPoint.x, startPoint.y)
                        false
                    }
                }else{
                    removeElement(element)
                    if (checkElementPositionVertical(element,endPoint.x,endPoint.y)) {
                        place16PartVertical(element, endPoint.x, endPoint.y)
                        true
                    }else{
                        place16PartVertical(element, startPoint.x, startPoint.y)
                        false
                    }
                }
            }
            "SB", "HL", "C", "VD", "RX", "R" -> {
                return if (drawMatrix[startPoint.y][startPoint.x + 1] != null){
                    removeElement(element)
                    if (checkElementPositionHorizontal(element,endPoint.x,endPoint.y)) {
                        place2PartHorizontal(element, endPoint.x, endPoint.y)
                        true
                    }else{
                        place2PartHorizontal(element, startPoint.x, startPoint.y)
                        false
                    }
                }else{
                    removeElement(element)
                    if (checkElementPositionVertical(element,endPoint.x,endPoint.y)) {
                        place2PartVertical(element, endPoint.x, endPoint.y)
                        true
                    }else{
                        place2PartVertical(element, startPoint.x, startPoint.y)
                        false
                    }
                }
            }
            else -> {
                return false
            }
        }
    }

    fun moveNet(net: Net,startPoint: Point, endPoint: Point):Boolean{
        removeNet(net)
        return if (checkNetPosition(endPoint.x, endPoint.y)){
            val drawPoint = DrawPoint(endPoint.x * step,endPoint.y * step)
            val drawObject = DrawObject(drawPoint,ObjectType.Net,DrawType.NET)
            drawMatrix[endPoint.y][endPoint.x] = drawObject
            true
        }else {
            val drawPoint = DrawPoint(startPoint.x * step,startPoint.y * step)
            val drawObject = DrawObject(drawPoint,ObjectType.Net,DrawType.NET)
            drawMatrix[startPoint.y][startPoint.x] = drawObject
            false
        }

    }

    private fun placeElement(element: Element){
        drawMatrix.forEachIndexed { yIndex, arrayOfDrawObjects ->
            arrayOfDrawObjects.forEachIndexed { xIndex, _ ->
                if (checkElementPositionHorizontal(element, xIndex, yIndex)){
                    placeElementHorizontal(element, xIndex, yIndex)
                    return
                } else if (checkElementPositionVertical(element, xIndex, yIndex)) {
                    placeElementVertical(element, xIndex, yIndex)
                    return
                }
            }
        }
    }

    private fun checkElementPositionVertical(element: Element, x: Int, y: Int): Boolean {
        when (element.typeElement) {
            "DD", "X" -> {
                return if (y + 1 > sizeY - 1 || y + 2 > sizeY - 1 || y + 3 > sizeY - 1
                        || y + 4 > sizeY - 1 || y + 5 > sizeY - 1 || y + 6 > sizeY - 1
                        || y + 7 > sizeY - 1
                        || x - 1 < 0 || x + 1 > sizeX - 1 || x + 2 > sizeX - 1)
                    false
                else (drawMatrix[y][x] == null && drawMatrix[y][x + 1] == null
                        && drawMatrix[y + 1][x] == null && drawMatrix[y + 1][x + 1] == null
                        && drawMatrix[y + 2][x] == null && drawMatrix[y + 2][x + 1] == null
                        && drawMatrix[y + 3][x] == null && drawMatrix[y + 3][x + 1] == null
                        && drawMatrix[y + 4][x] == null && drawMatrix[y + 4][x + 1] == null
                        && drawMatrix[y + 5][x] == null && drawMatrix[y + 5][x + 1] == null
                        && drawMatrix[y + 6][x] == null && drawMatrix[y + 6][x + 1] == null
                        && drawMatrix[y + 7][x] == null && drawMatrix[y + 7][x + 1] == null
                        && drawMatrix[y][x - 1] == null && drawMatrix[y][x + 2] == null
                        && drawMatrix[y + 1][x - 1] == null && drawMatrix[y + 1][x + 2] == null
                        && drawMatrix[y + 2][x - 1] == null && drawMatrix[y + 2][x + 2] == null
                        && drawMatrix[y + 3][x - 1] == null && drawMatrix[y + 3][x + 2] == null
                        && drawMatrix[y + 4][x - 1] == null && drawMatrix[y + 4][x + 2] == null
                        && drawMatrix[y + 5][x - 1] == null && drawMatrix[y + 5][x + 2] == null
                        && drawMatrix[y + 6][x - 1] == null && drawMatrix[y + 6][x + 2] == null
                        && drawMatrix[y + 7][x - 1] == null && drawMatrix[y + 7][x + 2] == null)
            }
            "SB", "HL", "C", "VD", "RX", "R" -> {
                return if (y + 1 > sizeY - 1
                        || x - 1 < 0 || x + 1 > sizeX - 1)
                    false
                else (drawMatrix[y][x] == null && drawMatrix[y + 1][x] == null
                        && drawMatrix[y][x - 1] == null && drawMatrix[y + 1][x - 1] == null
                        && drawMatrix[y][x + 1] == null && drawMatrix[y + 1][x + 1] == null)
            }
            else -> {
                return false
            }
        }
    }

    private fun checkElementPositionHorizontal(element: Element, x: Int, y: Int): Boolean {
        when (element.typeElement) {
            "DD", "X" -> {
                return if (x + 1 > sizeX - 1 || x + 2 > sizeX - 1 || x + 3 > sizeX - 1
                        || x + 4 > sizeX - 1 || x + 5 > sizeX - 1 || x + 6 > sizeX - 1
                        || x + 7 > sizeX - 1
                        || y - 1 < 0 || y + 1 > sizeY - 1 || y - 2 < 0)
                    false
                else (drawMatrix[y][x] == null && drawMatrix[y - 1][x] == null
                        && drawMatrix[y][x] == null && drawMatrix[y - 1][x] == null
                        && drawMatrix[y][x + 2] == null && drawMatrix[y - 1][x + 2] == null
                        && drawMatrix[y][x + 3] == null && drawMatrix[y - 1][x + 3] == null
                        && drawMatrix[y][x + 4] == null && drawMatrix[y - 1][x + 4] == null
                        && drawMatrix[y][x + 5] == null && drawMatrix[y - 1][x + 5] == null
                        && drawMatrix[y][x + 6] == null && drawMatrix[y - 1][x + 6] == null
                        && drawMatrix[y][x + 7] == null && drawMatrix[y - 1][x + 7] == null
                        && drawMatrix[y + 1][x] == null && drawMatrix[y - 2][x] == null
                        && drawMatrix[y + 1][x + 1] == null && drawMatrix[y - 2][x + 1] == null
                        && drawMatrix[y + 1][x + 2] == null && drawMatrix[y - 2][x + 2] == null
                        && drawMatrix[y + 1][x + 3] == null && drawMatrix[y - 2][x + 3] == null
                        && drawMatrix[y + 1][x + 4] == null && drawMatrix[y - 2][x + 4] == null
                        && drawMatrix[y + 1][x + 5] == null && drawMatrix[y - 2][x + 5] == null
                        && drawMatrix[y + 1][x + 6] == null && drawMatrix[y - 2][x + 6] == null
                        && drawMatrix[y + 1][x + 7] == null && drawMatrix[y - 2][x + 7] == null)
            }
            "SB", "HL", "C", "VD", "RX", "R" -> {
                return if (x + 1 > sizeX - 1
                        || y - 1 < 0 || y + 1 > sizeY - 1)
                    false
                else (drawMatrix[y][x] == null && drawMatrix[y][x + 1] == null
                        && drawMatrix[y - 1][x] == null && drawMatrix[y - 1][x + 1] == null
                        && drawMatrix[y + 1][x] == null && drawMatrix[y + 1][x + 1] == null)
            }
            else -> {
                return false
            }
        }
    }

    private fun placeElementVertical(element: Element, x: Int, y: Int) {
        when (element.typeElement) {
            "DD", "X" -> {
                place16PartVertical(element, x, y)
            }
            "SB", "HL", "C", "VD", "RX", "R" -> {
                place2PartVertical(element, x, y)
            }
        }

    }

    private fun placeElementHorizontal(element: Element, x: Int, y: Int) {
        when (element.typeElement) {
            "DD", "X" -> {
                place16PartHorizontal(element, x, y)
            }
            "SB", "HL", "C", "VD", "RX", "R" -> {
                place2PartHorizontal(element, x, y)
            }
        }

    }

    private fun place16PartVertical(element: Element, x: Int, y: Int) {
        element.getPins().forEachIndexed { index, pin ->
            when (index + 1) {
                1 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * y),
                            ObjectType.Pin, DrawType.PIN_CORNER_UP_LEFT)
                    drawMatrix[y][x] = drawObject
                    pin.move(x, y)
                }
                2 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * y),
                            ObjectType.Pin, DrawType.PIN_CORNER_UP_RIGHT)
                    drawMatrix[y][x + 1] = drawObject
                    pin.move(x + 1, y)
                }
                3 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * (y + 1)),
                            ObjectType.Pin, DrawType.PIN_SIDE_LEFT)
                    drawMatrix[y + 1][x] = drawObject
                    pin.move(x, y + 1)
                }
                4 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * (y + 1)),
                            ObjectType.Pin, DrawType.PIN_SIDE_RIGHT)
                    drawMatrix[y + 1][x + 1] = drawObject
                    pin.move(x + 1, y + 1)
                }
                5 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * (y + 2)),
                            ObjectType.Pin, DrawType.PIN_SIDE_LEFT)
                    drawMatrix[y + 2][x] = drawObject
                    pin.move(x, y + 2)
                }
                6 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * (y + 2)),
                            ObjectType.Pin, DrawType.PIN_SIDE_RIGHT)
                    drawMatrix[y + 2][x + 1] = drawObject
                    pin.move(x + 1, y + 2)
                }
                7 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * (y + 3)),
                            ObjectType.Pin, DrawType.PIN_SIDE_LEFT)
                    drawMatrix[y + 3][x] = drawObject
                    pin.move(x, y + 3)
                }
                8 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * (y + 3)),
                            ObjectType.Pin, DrawType.PIN_SIDE_RIGHT)
                    drawMatrix[y + 3][x + 1] = drawObject
                    pin.move(x + 1, y + 3)
                }
                9 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * (y + 4)),
                            ObjectType.Pin, DrawType.PIN_SIDE_LEFT)
                    drawMatrix[y + 4][x] = drawObject
                    pin.move(x, y + 4)
                }
                10 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * (y + 4)),
                            ObjectType.Pin, DrawType.PIN_SIDE_RIGHT)
                    drawMatrix[y + 4][x + 1] = drawObject
                    pin.move(x + 1, y + 4)
                }
                11 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * (y + 5)),
                            ObjectType.Pin, DrawType.PIN_SIDE_LEFT)
                    drawMatrix[y + 5][x] = drawObject
                    pin.move(x, y + 5)
                }
                12 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * (y + 5)),
                            ObjectType.Pin, DrawType.PIN_SIDE_RIGHT)
                    drawMatrix[y + 5][x + 1] = drawObject
                    pin.move(x + 1, y + 5)
                }
                13 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * (y + 6)),
                            ObjectType.Pin, DrawType.PIN_SIDE_LEFT)
                    drawMatrix[y + 6][x] = drawObject
                    pin.move(x, y + 6)
                }
                14 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * (y + 6)),
                            ObjectType.Pin, DrawType.PIN_SIDE_RIGHT)
                    drawMatrix[y + 6][x + 1] = drawObject
                    pin.move(x + 1, y + 6)
                }
                15 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * (y + 7)),
                            ObjectType.Pin, DrawType.PIN_CORNER_DOWN_LEFT)
                    drawMatrix[y + 7][x] = drawObject
                    pin.move(x, y + 7)
                }
                16 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * (y + 7)),
                            ObjectType.Pin, DrawType.PIN_CORNER_DOWN_RIGHT)
                    drawMatrix[y + 7][x + 1] = drawObject
                    pin.move(x + 1, y + 7)
                }
            }
        }
        element.move(x, y)
    }

    private fun place16PartHorizontal(element: Element, x: Int, y: Int) {
        element.getPins().forEachIndexed { index, pin ->
            when (index + 1) {
                1 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * y),
                            ObjectType.Pin, DrawType.PIN_CORNER_DOWN_LEFT)
                    drawMatrix[y][x] = drawObject
                    pin.move(x, y)
                }
                2 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * (y - 1)),
                            ObjectType.Pin, DrawType.PIN_CORNER_UP_LEFT)
                    drawMatrix[y - 1][x] = drawObject
                    pin.move(x, y - 1)
                }
                3 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * y),
                            ObjectType.Pin, DrawType.PIN_SIDE_DOWN)
                    drawMatrix[y][x + 1] = drawObject
                    pin.move(x + 1, y)
                }
                4 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * (y - 1)),
                            ObjectType.Pin, DrawType.PIN_SIDE_UP)
                    drawMatrix[y - 1][x + 1] = drawObject
                    pin.move(x + 1, y - 1)
                }
                5 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 2), step * y),
                            ObjectType.Pin, DrawType.PIN_SIDE_DOWN)
                    drawMatrix[y][x + 2] = drawObject
                    pin.move(x + 2, y)
                }
                6 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 2), step * (y - 1)),
                            ObjectType.Pin, DrawType.PIN_SIDE_UP)
                    drawMatrix[y - 1][x + 2] = drawObject
                    pin.move(x + 2, y - 1)
                }
                7 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 3), step * y),
                            ObjectType.Pin, DrawType.PIN_SIDE_DOWN)
                    drawMatrix[y][x + 3] = drawObject
                    pin.move(x + 3, y)
                }
                8 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 3), step * (y - 1)),
                            ObjectType.Pin, DrawType.PIN_SIDE_UP)
                    drawMatrix[y - 1][x + 3] = drawObject
                    pin.move(x + 3, y - 1)
                }
                9 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 4), step * y),
                            ObjectType.Pin, DrawType.PIN_SIDE_DOWN)
                    drawMatrix[y][x + 4] = drawObject
                    pin.move(x + 4, y)
                }
                10 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 4), step * (y - 1)),
                            ObjectType.Pin, DrawType.PIN_SIDE_UP)
                    drawMatrix[y - 1][x + 4] = drawObject
                    pin.move(x + 4, y - 1)
                }
                11 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 5), step * y),
                            ObjectType.Pin, DrawType.PIN_SIDE_DOWN)
                    drawMatrix[y][x + 5] = drawObject
                    pin.move(x + 5, y)
                }
                12 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 5), step * (y - 1)),
                            ObjectType.Pin, DrawType.PIN_SIDE_UP)
                    drawMatrix[y - 1][x + 5] = drawObject
                    pin.move(x + 5, y - 1)
                }
                13 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 6), step * y),
                            ObjectType.Pin, DrawType.PIN_SIDE_DOWN)
                    drawMatrix[y][x + 6] = drawObject
                    pin.move(x + 6, y)
                }
                14 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 6), step * (y - 1)),
                            ObjectType.Pin, DrawType.PIN_SIDE_UP)
                    drawMatrix[y - 1][x + 6] = drawObject
                    pin.move(x + 6, y - 1)
                }
                15 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 7), step * y),
                            ObjectType.Pin, DrawType.PIN_CORNER_DOWN_RIGHT)
                    drawMatrix[y][x + 7] = drawObject
                    pin.move(x + 7, y)
                }
                16 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 7), step * (y - 1)),
                            ObjectType.Pin, DrawType.PIN_CORNER_UP_RIGHT)
                    drawMatrix[y - 1][x + 7] = drawObject
                    pin.move(x + 7, y - 1)
                }
            }
        }
        element.move(x, y)
    }

    private fun place8PartVertical(element: Element, x: Int, y: Int) {
        element.getPins().forEachIndexed { index, pin ->
            when (index + 1) {
                1 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * y),
                            ObjectType.Pin, DrawType.PIN_CORNER_UP_LEFT)
                    drawMatrix[y][x] = drawObject
                    pin.move(x, y)
                }
                2 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * y),
                            ObjectType.Pin, DrawType.PIN_CORNER_UP_RIGHT)
                    drawMatrix[y][x + 1] = drawObject
                    pin.move(x + 1, y)
                }
                3 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * (y + 1)),
                            ObjectType.Pin, DrawType.PIN_SIDE_LEFT)
                    drawMatrix[y + 1][x] = drawObject
                    pin.move(x, y + 1)
                }
                4 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * (y + 1)),
                            ObjectType.Pin, DrawType.PIN_SIDE_RIGHT)
                    drawMatrix[y + 1][x + 1] = drawObject
                    pin.move(x + 1, y + 1)
                }
                5 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * (y + 2)),
                            ObjectType.Pin, DrawType.PIN_SIDE_LEFT)
                    drawMatrix[y + 2][x] = drawObject
                    pin.move(x, y + 2)
                }
                6 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * (y + 2)),
                            ObjectType.Pin, DrawType.PIN_SIDE_RIGHT)
                    drawMatrix[y + 2][x + 1] = drawObject
                    pin.move(x + 1, y + 2)
                }
                7 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * (y + 3)),
                            ObjectType.Pin, DrawType.PIN_CORNER_DOWN_LEFT)
                    drawMatrix[y + 3][x] = drawObject
                    pin.move(x, y + 3)
                }
                8 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * (y + 3)),
                            ObjectType.Pin, DrawType.PIN_CORNER_DOWN_RIGHT)
                    drawMatrix[y + 3][x + 1] = drawObject
                    pin.move(x + 1, y + 3)
                }
            }
        }
        element.move(x, y)
    }

    private fun place8PartHorizontal(element: Element, x: Int, y: Int) {
        element.getPins().forEachIndexed { index, pin ->
            when (index + 1) {
                1 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * y),
                            ObjectType.Pin, DrawType.PIN_CORNER_DOWN_LEFT)
                    drawMatrix[y][x] = drawObject
                    pin.move(x, y)
                }
                2 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * (y - 1)),
                            ObjectType.Pin, DrawType.PIN_CORNER_UP_LEFT)
                    drawMatrix[y - 1][x] = drawObject
                    pin.move(x, y - 1)
                }
                3 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * y),
                            ObjectType.Pin, DrawType.PIN_SIDE_DOWN)
                    drawMatrix[y][x + 1] = drawObject
                    pin.move(x + 1, y)
                }
                4 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * (y - 1)),
                            ObjectType.Pin, DrawType.PIN_SIDE_UP)
                    drawMatrix[y - 1][x + 1] = drawObject
                    pin.move(x + 1, y - 1)
                }
                5 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 2), step * y),
                            ObjectType.Pin, DrawType.PIN_SIDE_DOWN)
                    drawMatrix[y][x + 2] = drawObject
                    pin.move(x + 2, y)
                }
                6 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 2), step * (y - 1)),
                            ObjectType.Pin, DrawType.PIN_SIDE_UP)
                    drawMatrix[y - 1][x + 2] = drawObject
                    pin.move(x + 2, y - 1)
                }
                7 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 3), step * (y + 7)),
                            ObjectType.Pin, DrawType.PIN_CORNER_DOWN_RIGHT)
                    drawMatrix[y][x + 3] = drawObject
                    pin.move(x + 3, y)
                }
                8 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 3), step * (y - 1)),
                            ObjectType.Pin, DrawType.PIN_CORNER_UP_RIGHT)
                    drawMatrix[y - 1][x + 3] = drawObject
                    pin.move(x + 3, y - 1)
                }
            }
        }
        element.move(x, y)
    }

    private fun place2PartVertical(element: Element, x: Int, y: Int) {
        element.getPins().forEachIndexed { index, pin ->
            when (index + 1) {
                1 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * y),
                            ObjectType.Pin, DrawType.PIN_LINE_UP)
                    drawMatrix[y][x] = drawObject
                    pin.move(x, y)
                }
                2 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * (y + 1)),
                            ObjectType.Pin, DrawType.PIN_LINE_DOWN)
                    drawMatrix[y + 1][x] = drawObject
                    pin.move(x, y + 1)
                }
            }
        }
        element.move(x, y)
    }

    private fun place2PartHorizontal(element: Element, x: Int, y: Int) {
        element.getPins().forEachIndexed { index, pin ->
            when (index + 1) {
                1 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * y),
                            ObjectType.Pin, DrawType.PIN_LINE_LEFT)
                    drawMatrix[y][x] = drawObject
                    pin.move(x, y)
                }
                2 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * y),
                            ObjectType.Pin, DrawType.PIN_LINE_RIGHT)
                    drawMatrix[y][x + 1] = drawObject
                    pin.move(x + 1, y)
                }
            }
        }
        element.move(x, y)
    }

    private fun placeNet(net: Net) {
        drawMatrix.forEachReversedWithIndex { yIndex, arrayOfDrawObjects ->
            arrayOfDrawObjects.forEachReversedWithIndex { xIndex, _ ->
                if (checkNetPosition(xIndex, yIndex)) {
                    val drawObject = DrawObject(DrawPoint(step * xIndex, step * yIndex),
                            ObjectType.Net, DrawType.NET)
                    drawMatrix[yIndex][xIndex] = drawObject
                    net.move(xIndex, yIndex)
                    return
                }
            }
        }
    }

    private fun placeNet(point: Point){
        val xIndex = point.x
        val yIndex = point.y
        val drawObject = DrawObject(DrawPoint(step * xIndex, step * yIndex),
                ObjectType.Net, DrawType.NET)
        drawMatrix[yIndex][xIndex] = drawObject
    }

    private fun checkNetPosition(x: Int, y: Int): Boolean {
        return if (y + 1 > sizeY - 1 || y - 1 < 0
                || x - 1 < 0 || x + 1 > sizeX - 1)
            false
        else (drawMatrix[y][x] == null
                && drawMatrix[y + 1][x] == null && drawMatrix[y - 1][x] == null
                && drawMatrix[y][x - 1] == null && drawMatrix[y][x + 1] == null
                && drawMatrix[y + 1][x + 1] == null && drawMatrix[y + 1][x - 1] == null
                && drawMatrix[y - 1][x + 1] == null && drawMatrix[y - 1][x - 1] == null)
    }

    fun removeElement(element: Element){
        element.getPins().forEach { pin ->
            val point = pin.getPoint()
            drawMatrix[point.y][point.x] = null
        }
    }

    fun removeNet(net: Net){
        drawMatrix[net.getPoint().y][net.getPoint().x] = null
    }
}