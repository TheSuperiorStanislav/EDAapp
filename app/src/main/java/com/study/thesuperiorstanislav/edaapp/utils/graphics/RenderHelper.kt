package com.study.thesuperiorstanislav.edaapp.utils.graphics

import android.graphics.*
import android.util.Log
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Element
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Pin
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.DrawObject
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.DrawPoint
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.DrawType.*
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.ObjectType

class RenderHelper(private val rect: Rect) {
    private var drawMatrix: Array<Array<DrawObject?>>
    var isMatrixInit = false

    private val linesPaint = Paint()
    private val netPaint = Paint()
    private val elementPartPaint = Paint()
    private val pinPaint = Paint()
    private val connectorPaint = Paint()

    private var sizeX = 35
    private var sizeY:Int
    private var step = 0f

    init {
        val maxX = rect.right.toFloat()
        step = maxX/sizeX
        sizeY = (rect.bottom/step).toInt()
        val nullDrawObject:DrawObject? = null
        drawMatrix = Array(sizeY){ Array(sizeX){ nullDrawObject}}
        initPaint()
    }

    fun drawLines(canvas: Canvas){
        var cordX = 0f
        var cordY = 0f

        for (i in 1 .. sizeX + 1){
            canvas.drawLine(cordX,0f,cordX,sizeY*step,linesPaint)
            cordX+= step
        }

        for (i in 1 .. sizeY + 1){
            canvas.drawLine(0f,cordY,rect.right.toFloat(),cordY,linesPaint)
            cordY+= step
        }
    }

    fun drawElement(element: Element,canvas: Canvas) {
        element.getPins().forEach {
            drawPin(it, canvas)
        }
    }

    private fun drawPin(pin: Pin, canvas: Canvas) {
        val point = pin.getPoint()
        if (point.y + point.x != -2) {
            val drawType = drawMatrix[point.y][point.x]!!.drawType
            val drawPoint = drawMatrix[point.y][point.x]!!.drawPoint
            val path = Path()
            when (drawType) {
                PIN_CORNER_UP_LEFT -> {
                    path.moveTo(drawPoint.x + step / 2, drawPoint.y + step)
                    path.moveTo(drawPoint.x + step, drawPoint.y + step / 2)
                    path.lineTo(drawPoint.x + step / 2, drawPoint.y + step / 2)
                    path.moveTo(drawPoint.x + step / 2, drawPoint.y + step / 2)
                    path.lineTo(drawPoint.x + step / 2, drawPoint.y + step)
                    path.moveTo(drawPoint.x + step / 2, drawPoint.y + step)
                }
                PIN_CORNER_UP_RIGHT -> {
                    path.moveTo(drawPoint.x, drawPoint.y + step / 2)
                    path.lineTo(drawPoint.x + step / 2, drawPoint.y + step / 2)
                    path.moveTo(drawPoint.x + step / 2, drawPoint.y + step / 2)
                    path.lineTo(drawPoint.x + step / 2, drawPoint.y + step)
                    path.moveTo(drawPoint.x + step / 2, drawPoint.y + step)
                }
                PIN_CORNER_DOWN_LEFT -> {
                    path.moveTo(drawPoint.x + step / 2, drawPoint.y)
                    path.lineTo(drawPoint.x + step / 2, drawPoint.y + step / 2)
                    path.moveTo(drawPoint.x + step / 2, drawPoint.y + step / 2)
                    path.lineTo(drawPoint.x + step, drawPoint.y + step / 2)
                    path.moveTo(drawPoint.x + step, drawPoint.y + step / 2)
                }
                PIN_CORNER_DOWN_RIGHT -> {
                    path.moveTo(drawPoint.x + step / 2, drawPoint.y)
                    path.lineTo(drawPoint.x + step / 2, drawPoint.y + step / 2)
                    path.moveTo(drawPoint.x + step / 2, drawPoint.y + step / 2)
                    path.lineTo(drawPoint.x, drawPoint.y + step / 2)
                    path.moveTo(drawPoint.x, drawPoint.y + step / 2)
                }
                PIN_SIDE_LEFT -> {
                    path.moveTo(drawPoint.x + step / 2, drawPoint.y)
                    path.lineTo(drawPoint.x + step / 2, drawPoint.y + step)
                    path.moveTo(drawPoint.x + step / 2, drawPoint.y + step)
                }
                PIN_SIDE_RIGHT -> {
                    path.moveTo(drawPoint.x + step / 2, drawPoint.y)
                    path.lineTo(drawPoint.x + step / 2, drawPoint.y + step)
                    path.moveTo(drawPoint.x + step / 2, drawPoint.y + step)
                }
                PIN_LINE_UP -> {
                    path.moveTo(drawPoint.x + step / 4, drawPoint.y + step)
                    path.lineTo(drawPoint.x + step / 4, drawPoint.y + step / 2)
                    path.moveTo(drawPoint.x + step / 4, drawPoint.y + step / 2)
                    path.lineTo(drawPoint.x + 3 * step / 4, drawPoint.y + step / 2)
                    path.moveTo(drawPoint.x + 3 * step / 4, drawPoint.y + step / 2)
                    path.lineTo(drawPoint.x + 3 * step / 4, drawPoint.y + step)
                    path.moveTo(drawPoint.x + 3 * step / 4, drawPoint.y + step)
                }
                PIN_LINE_MIDDLE -> {

                }
                PIN_LINE_DOWN -> {
                    path.moveTo(drawPoint.x + step / 4, drawPoint.y)
                    path.lineTo(drawPoint.x + step / 4, drawPoint.y + step / 2)
                    path.moveTo(drawPoint.x + step / 4, drawPoint.y + step / 2)
                    path.lineTo(drawPoint.x + 3 * step / 4, drawPoint.y + step / 2)
                    path.moveTo(drawPoint.x + 3 * step / 4, drawPoint.y + step / 2)
                    path.lineTo(drawPoint.x + 3 * step / 4, drawPoint.y)
                    path.moveTo(drawPoint.x + 3 * step / 4, drawPoint.y)
                }
                else -> {
                }
            }
            canvas.drawPath(path,elementPartPaint)
            drawPinCircle(pin, canvas)
        }else {
            Log.e("ErrorCord",pin.toString())
        }
    }

    private fun drawPinCircle(pin: Pin, canvas: Canvas){
        val point = pin.getPoint()
        val drawPoint = drawMatrix[point.y][point.x]!!.drawPoint
        canvas.drawCircle(drawPoint.x + step/2,drawPoint.y + step/2,step/6,pinPaint)
    }

    fun initDrawMatrix(circuit: Circuit){
        circuit.listElements.forEach {
            placeElement(it)
        }
        isMatrixInit = true
    }

    private fun placeElement(element: Element){
        drawMatrix.forEachIndexed { yIndex, arrayOfDrawObjects ->
            arrayOfDrawObjects.forEachIndexed { xIndex, _ ->
                if (checkPositionVertical(element,xIndex,yIndex)){
                    placeElementVertical(element, xIndex, yIndex)
                    return
                }

            }
        }
    }

    private fun checkPositionVertical(element: Element, x:Int, y:Int):Boolean {
        when (element.typeElement) {
            "DD", "X" -> {
                return if (y + 1 > sizeY - 1 || y + 2 > sizeY-1 || y + 3 > sizeY -1
                        || x - 1 < 0 || x + 1 > sizeX - 1 || x + 2 > sizeX - 1)
                    false
                else (drawMatrix[y][x] == null && drawMatrix[y][x + 1] == null
                        && drawMatrix[y + 1][x] == null && drawMatrix[y + 1][x + 1] == null
                        && drawMatrix[y + 2][x] == null && drawMatrix[y + 2][x + 1] == null
                        && drawMatrix[y + 3][x] == null && drawMatrix[y + 3][x + 1] == null
                        && drawMatrix[y][x - 1] == null && drawMatrix[y][x + 2] == null
                        && drawMatrix[y + 1][x - 1] == null && drawMatrix[y + 1][x + 2] == null
                        && drawMatrix[y + 2][x - 1] == null && drawMatrix[y + 2][x + 2] == null
                        && drawMatrix[y + 3][x - 1] == null && drawMatrix[y + 3][x + 2] == null)
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

    private fun placeElementVertical(element: Element, x:Int, y:Int){
        when (element.typeElement) {
            "DD", "X" -> {
                place8PartVertical(element,x,y)
            }
            "SB", "HL", "C", "VD", "RX", "R" -> {
                place2PartVertical(element,x,y)
            }
        }

    }

    private fun place8PartVertical(element: Element, x:Int, y:Int) {
        element.getPins().forEachIndexed { index, pin ->
            when (index + 1) {
                1 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * y), ObjectType.Pin, PIN_CORNER_UP_LEFT)
                    drawMatrix[y][x] = drawObject
                    pin.move(x, y)
                }
                2 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * y), ObjectType.Pin, PIN_CORNER_UP_RIGHT)
                    drawMatrix[y][x + 1] = drawObject
                    pin.move(x + 1, y)
                }
                3 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * (y + 1)), ObjectType.Pin, PIN_SIDE_LEFT)
                    drawMatrix[y + 1][x] = drawObject
                    pin.move(x, y + 1)
                }
                4 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * (y + 1)), ObjectType.Pin, PIN_SIDE_RIGHT)
                    drawMatrix[y + 1][x + 1] = drawObject
                    pin.move(x + 1, y + 1)
                }
                5 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * (y + 2)), ObjectType.Pin, PIN_SIDE_LEFT)
                    drawMatrix[y + 2][x] = drawObject
                    pin.move(x, y + 2)
                }
                6 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * (y + 2)), ObjectType.Pin, PIN_SIDE_RIGHT)
                    drawMatrix[y + 2][x + 1] = drawObject
                    pin.move(x + 1, y + 2)
                }
                7 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * (y + 3)), ObjectType.Pin, PIN_CORNER_DOWN_LEFT)
                    drawMatrix[y + 3][x] = drawObject
                    pin.move(x, y + 3)
                }
                8 -> {
                    val drawObject = DrawObject(DrawPoint(step * (x + 1), step * (y + 3)), ObjectType.Pin, PIN_CORNER_DOWN_RIGHT)
                    drawMatrix[y + 3][x + 1] = drawObject
                    pin.move(x + 1, y + 3)
                }
            }
        }
        element.move(x, y)
    }

    private fun place2PartVertical(element: Element, x:Int, y:Int) {
        element.getPins().forEachIndexed { index, pin ->
            when (index + 1) {
                1-> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * y), ObjectType.Pin, PIN_LINE_UP)
                    drawMatrix[y][x] = drawObject
                    pin.move(x, y)
                }
                2 -> {
                    val drawObject = DrawObject(DrawPoint(step * x, step * (y + 1)), ObjectType.Pin, PIN_LINE_DOWN)
                    drawMatrix[y + 1][x] = drawObject
                    pin.move(x, y + 1)
                }
            }
        }
        element.move(x, y)
    }

    private fun initPaint(){
        linesPaint.isAntiAlias = true
        linesPaint.isDither = true
        linesPaint.color = Color.DKGRAY
        linesPaint.style = Paint.Style.STROKE
        linesPaint.strokeJoin = Paint.Join.ROUND
        linesPaint.strokeCap = Paint.Cap.ROUND
        linesPaint.strokeWidth = 2f

        netPaint.isAntiAlias = true
        netPaint.isDither = true
        netPaint.color = Color.MAGENTA
        netPaint.style = Paint.Style.FILL
        netPaint.strokeJoin = Paint.Join.ROUND
        netPaint.strokeCap = Paint.Cap.ROUND
        netPaint.strokeWidth = 3f

        elementPartPaint.isAntiAlias = true
        elementPartPaint.isDither = true
        elementPartPaint.color = Color.BLACK
        elementPartPaint.style = Paint.Style.STROKE
        elementPartPaint.strokeJoin = Paint.Join.ROUND
        elementPartPaint.strokeCap = Paint.Cap.ROUND
        elementPartPaint.strokeWidth = 5f

        pinPaint.isAntiAlias = true
        pinPaint.isDither = true
        pinPaint.color = Color.RED
        pinPaint.style = Paint.Style.FILL
        pinPaint.strokeJoin = Paint.Join.ROUND
        pinPaint.strokeCap = Paint.Cap.ROUND
        pinPaint.strokeWidth = 3f

        connectorPaint.isAntiAlias = true
        connectorPaint.isDither = true
        connectorPaint.color = Color.GREEN
        connectorPaint.style = Paint.Style.STROKE
        connectorPaint.strokeJoin = Paint.Join.ROUND
        connectorPaint.strokeCap = Paint.Cap.ROUND
        connectorPaint.strokeWidth = 3f
    }
}