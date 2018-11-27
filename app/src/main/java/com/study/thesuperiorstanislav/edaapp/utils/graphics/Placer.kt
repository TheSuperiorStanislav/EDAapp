package com.study.thesuperiorstanislav.edaapp.utils.graphics

import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Element
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Element.DrawType.*
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Net
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawPoint
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawType
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.ObjectType
import org.jetbrains.anko.collections.forEachReversedWithIndex

class Placer(private val drawMatrix: Array<Array<DrawObject?>>,
             private val sizeX: Int, private val sizeY: Int,
             private val step: Float) {

    fun initDrawMatrix(circuit: Circuit): Array<Array<DrawObject?>>{
        circuit.listElements.forEach {
            placeElement(it)
        }
        circuit.listNets.forEachReversedWithIndex { _, net ->
            placeNet(net)
        }
        return drawMatrix
    }

    fun initDrawMatrix(element: Element): Array<Array<DrawObject?>>{
        when (element.getDrawType()) {
            TWENTY_FOUR_PART -> placeElementHorizontal(element,0,1)
            TWENTY_PART -> placeElementHorizontal(element,1,1)
            EIGHTEEN_PART -> placeElementHorizontal(element,1,1)
            SIXTEEN_PART -> placeElementHorizontal(element,2,1)
            TEN_PART -> placeElementHorizontal(element,3,1)
            EIGHT_PART -> placeElementHorizontal(element,3, 1)
            SIX_PART -> placeElementHorizontal(element,3,1)
            FOUR_PART -> placeElementHorizontal(element,3,1)
            THREE_PART -> placeElementHorizontal(element,3, 1)
            TWO_PART -> placeElementHorizontal(element,3, 1)
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

    fun moveElement(element: Element,startPoint: Point, endPoint: Point):Boolean {
        return when {
            startPoint == endPoint -> {
                if (isElementPlacedHorizontal(element, startPoint)){
                    removeElement(element)
                    if (checkElementPositionVertical(element, startPoint.x, startPoint.y))
                        placeElementVertical(element,startPoint.x, startPoint.y)
                    else
                        placeElementHorizontal(element,startPoint.x, startPoint.y)
                }else{
                    removeElement(element)
                    if (checkElementPositionHorizontal(element, startPoint.x, startPoint.y))
                        placeElementHorizontal(element,startPoint.x, startPoint.y)
                    else
                        placeElementVertical(element,startPoint.x, startPoint.y)
                }
                return true
            }
            isElementPlacedHorizontal(element, startPoint) -> {
                removeElement(element)
                when {
                    checkElementPositionHorizontal(element, endPoint.x, endPoint.y) -> {
                        placeElementHorizontal(element, endPoint.x, endPoint.y)
                        true
                    }
                    checkElementPositionVertical(element, endPoint.x, endPoint.y) -> {
                        placeElementVertical(element, endPoint.x, endPoint.y)
                        true
                    }
                    else -> {
                        placeElementHorizontal(element, startPoint.x, startPoint.y)
                        false
                    }
                }
            }
            else -> {
                removeElement(element)
                when {
                    checkElementPositionVertical(element, endPoint.x, endPoint.y) -> {
                        placeElementVertical(element, endPoint.x, endPoint.y)
                        true
                    }
                    checkElementPositionHorizontal(element, endPoint.x, endPoint.y) -> {
                        placeElementHorizontal(element, endPoint.x, endPoint.y)
                        true
                    }
                    else -> {
                        placeElementVertical(element, startPoint.x, startPoint.y)
                        false
                    }
                }
            }
        }
    }

    private fun isElementPlacedHorizontal(element: Element, point: Point):Boolean{
        return when (element.getDrawType()) {
            TWO_PART, THREE_PART, FOUR_PART -> drawMatrix[point.y][point.x + 1] != null
            else -> drawMatrix[point.y][point.x + 2] != null
        }
    }

    fun moveNet(net: Net,startPoint: Point, endPoint: Point):Boolean{
        removeNet(net)
        return if (checkNetPosition(endPoint.x, endPoint.y)){
            val drawPoint = DrawPoint(endPoint.x * step,endPoint.y * step)
            val drawObject = DrawObject(drawPoint,ObjectType.Net,DrawType.NET)
            drawMatrix[endPoint.y][endPoint.x] = drawObject
            net.move(endPoint.x, endPoint.y)
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
        when (element.getDrawType()) {
            THREE_PART -> {
                return if (y + 1 > sizeY - 1 || y + 2 > sizeY - 1
                        || x - 1 < 0 || x + 1 > sizeX - 1)
                    false
                else (drawMatrix[y][x] == null && drawMatrix[y + 1][x] == null && drawMatrix[y + 2][x] == null
                        && drawMatrix[y][x - 1] == null && drawMatrix[y][x + 1] == null
                        && drawMatrix[y + 1][x - 1] == null && drawMatrix[y + 1][x + 1] == null
                        && drawMatrix[y + 2][x - 1] == null && drawMatrix[y + 2][x + 1] == null)
            }
            TWO_PART -> {
                return if (y + 1 > sizeY - 1
                        || x - 1 < 0 || x + 1 > sizeX - 1)
                    false
                else (drawMatrix[y][x] == null && drawMatrix[y + 1][x] == null
                        && drawMatrix[y][x - 1] == null && drawMatrix[y][x + 1] == null
                        && drawMatrix[y + 1][x - 1] == null && drawMatrix[y + 1][x + 1] == null)
            }
            else -> {
                if (x - 1 < 0 || x + 1 > sizeX - 1 || x + 2 > sizeX - 1)
                    return false
                for (i in 0 until element.getPins().size/2) {
                    if (y + i > sizeY - 1)
                        return false
                    if (drawMatrix[y + i][x] != null || drawMatrix[y + i][x + 1] != null)
                        return false
                    if (drawMatrix[y + i][x - 1] != null || drawMatrix[y + i][x + 2] != null)
                        return false
                }
                return true
            }
        }
    }

    private fun checkElementPositionHorizontal(element: Element, x: Int, y: Int): Boolean {
        when (element.getDrawType()) {
            THREE_PART -> {
                return if (x + 1 > sizeX - 1 || x + 2 > sizeX - 1
                        || y - 1 < 0 || y + 1 > sizeY - 1)
                    false
                else (drawMatrix[y][x] == null && drawMatrix[y][x + 1] == null && drawMatrix[y][x + 2] == null
                        && drawMatrix[y - 1][x] == null && drawMatrix[y + 1][x] == null
                        && drawMatrix[y - 1][x + 1] == null && drawMatrix[y + 1][x + 1] == null
                        && drawMatrix[y - 1][x + 2] == null && drawMatrix[y + 1][x + 2] == null)
            }
            TWO_PART -> {
                return if (x + 1 > sizeX - 1
                        || y - 1 < 0 || y + 1 > sizeY - 1)
                    false
                else (drawMatrix[y][x] == null && drawMatrix[y][x + 1] == null
                        && drawMatrix[y - 1][x] == null && drawMatrix[y + 1][x] == null
                        && drawMatrix[y - 1][x + 1] == null && drawMatrix[y + 1][x + 1] == null)
            }
            else -> {
                if (y - 1 < 0 || y + 1 > sizeY - 1 || y - 2 < 0)
                    return false
                for (i in 0 until element.getPins().size/2) {
                    if (x + i > sizeX - 1)
                        return false
                    if (drawMatrix[y][x + i] != null || drawMatrix[y - 1][x + i] != null)
                        return false
                    if (drawMatrix[y + 1][x + i] != null || drawMatrix[y - 2][x + i] != null)
                        return false
                }
                return true
            }
        }
    }

    private fun placeElementVertical(element: Element, x: Int, y: Int) {
        when (element.getDrawType()) {
            TWO_PART -> place2PartVertical(element, x, y)
            THREE_PART -> place3PartVertical(element, x, y)
            else -> placeTwoLineElementVertical(element, x, y)
        }

    }

    private fun placeElementHorizontal(element: Element, x: Int, y: Int) {
        when (element.getDrawType()) {
            TWO_PART -> place2PartHorizontal(element, x, y)
            THREE_PART -> place3PartHorizontal(element, x, y)
            else -> placeTwoLineElementHorizontal(element, x, y)
        }

    }

    private fun placeTwoLineElementVertical(element: Element, x: Int, y: Int){
        element.getPins().forEachIndexed { index, pin ->
            val pinNum = index + 1
            val place = index / 2
            var drawObject: DrawObject? = null
            when {
                pinNum < 3 -> when (pinNum) {
                    1 -> {
                        drawObject = DrawObject(DrawPoint(step * x, step * y),
                                ObjectType.Pin, DrawType.PIN_CORNER_UP_LEFT)
                    }
                    2 -> {
                        drawObject = DrawObject(DrawPoint(step * (x + 1), step * y),
                                ObjectType.Pin, DrawType.PIN_CORNER_UP_RIGHT)
                    }
                }
                pinNum > element.getPins().size - 2 -> when (pinNum) {
                    element.getPins().size - 1 -> {
                        drawObject = DrawObject(DrawPoint(step * x, step * (y + place)),
                                ObjectType.Pin, DrawType.PIN_CORNER_DOWN_LEFT)
                    }
                    element.getPins().size -> {
                        drawObject = DrawObject(DrawPoint(step * (x + 1), step * (y + place)),
                                ObjectType.Pin, DrawType.PIN_CORNER_DOWN_RIGHT)
                    }
                }
                else -> drawObject = if (pinNum % 2 == 0) {
                    DrawObject(DrawPoint(step * (x + 1), step * (y + place)),
                            ObjectType.Pin, DrawType.PIN_SIDE_LEFT)
                } else {
                    DrawObject(DrawPoint(step * x, step * (y + place)),
                            ObjectType.Pin, DrawType.PIN_SIDE_RIGHT)
                }
            }
            if (pinNum % 2 == 0){
                drawMatrix[y + place][x + 1] = drawObject
                pin.move(x + 1, y + place)
            }else{
                drawMatrix[y + place][x] = drawObject
                pin.move(x, y + place)
            }
        }
        element.move(x, y)
    }

    private fun placeTwoLineElementHorizontal(element: Element, x: Int, y: Int){
        element.getPins().forEachIndexed { index, pin ->
            val pinNum = index + 1
            val place = index / 2
            var drawObject: DrawObject? = null
            when {
                pinNum < 3 -> when (pinNum) {
                    1 -> {
                        drawObject = DrawObject(DrawPoint(step * x, step * y),
                                ObjectType.Pin, DrawType.PIN_CORNER_DOWN_LEFT)
                    }
                    2 -> {
                        drawObject = DrawObject(DrawPoint(step * x, step * (y - 1)),
                                ObjectType.Pin, DrawType.PIN_CORNER_UP_LEFT)
                    }
                }
                pinNum > element.getPins().size - 2 -> when (pinNum) {
                    element.getPins().size - 1 -> {
                        drawObject = DrawObject(DrawPoint(step * (x + place), step * y),
                                ObjectType.Pin, DrawType.PIN_CORNER_DOWN_RIGHT)
                    }
                    element.getPins().size -> {
                        drawObject = DrawObject(DrawPoint(step * (x + place), step * (y - 1)),
                                ObjectType.Pin, DrawType.PIN_CORNER_UP_RIGHT)
                    }
                }
                else -> drawObject = if (pinNum % 2 != 0) {
                    DrawObject(DrawPoint(step * (x + place), step * y),
                            ObjectType.Pin, DrawType.PIN_SIDE_DOWN)
                } else {
                    DrawObject(DrawPoint(step * (x + place), step * (y - 1)),
                            ObjectType.Pin, DrawType.PIN_SIDE_UP)
                }
            }
            if (pinNum % 2 != 0) {
                drawMatrix[y][x + place] = drawObject
                pin.move(x + place, y)

            } else {
                drawMatrix[y - 1][x + place] = drawObject
                pin.move(x + place, y - 1)
            }
        }
        element.move(x, y)
    }

    private fun place3PartVertical(element: Element, x: Int, y: Int) {
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
                            ObjectType.Pin, DrawType.PIN_LINE_MIDDLE_VERTICAL)
                    drawMatrix[y + 1][x] = drawObject
                    pin.move(x, y + 1)
                }
                3 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * (y + 2)),
                            ObjectType.Pin, DrawType.PIN_LINE_DOWN)
                    drawMatrix[y + 2][x] = drawObject
                    pin.move(x, y + 2)
                }
            }
        }
        element.move(x, y)
    }

    private fun place3PartHorizontal(element: Element, x: Int, y: Int) {
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
                            ObjectType.Pin, DrawType.PIN_LINE_MIDDLE_HORIZONTAL)
                    drawMatrix[y][x + 1] = drawObject
                    pin.move(x + 1, y)
                }
                3 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 2), step * y),
                            ObjectType.Pin, DrawType.PIN_LINE_RIGHT)
                    drawMatrix[y][x + 2] = drawObject
                    pin.move(x + 2, y)
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