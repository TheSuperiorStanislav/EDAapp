package com.study.thesuperiorstanislav.edaapp.utils.graphics

import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Element
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Net
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

    private fun placeElement(element: Element){
        drawMatrix.forEachIndexed { yIndex, arrayOfDrawObjects ->
            arrayOfDrawObjects.forEachIndexed { xIndex, _ ->
                if (checkElementPositionVertical(element, xIndex, yIndex)) {
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
}