package com.study.thesuperiorstanislav.edaapp.utils.graphics

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.Paint
import android.graphics.Color
import com.study.thesuperiorstanislav.edaapp.main.domain.model.*
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.DrawObject
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.DrawType.*
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.ObjectType

class RenderHelper(private val rect: Rect) {
    private var placer: Placer
    private var drawMatrix: Array<Array<DrawObject?>>
    var isMatrixInit = false

    private val linesPaint = Paint()
    private val netPaint = Paint()
    private val elementPartPaint = Paint()
    private val pinPaint = Paint()
    private val pinConnectedPaint = Paint()
    private val connectorPaint = Paint()
    private val selectedPaint = Paint()

    private var sizeX = 60
    private var sizeY:Int
    var step = 0f

    init {
        val maxX = rect.right.toFloat()
        step = maxX / sizeX
        sizeY = (rect.bottom / step).toInt()
        val nullDrawObject: DrawObject? = null
        drawMatrix = Array(sizeY) { Array(sizeX) { nullDrawObject } }
        initPaint()
        placer = Placer(drawMatrix, sizeX, sizeY, step)
    }

    fun initDrawMatrix(circuit: Circuit){
        drawMatrix = placer.initDrawMatrix(circuit)
        isMatrixInit = true
    }

    fun checkTouch(x:Float,y:Float):Boolean{
        return !(x>sizeX*step || y>sizeY*step)
    }

    fun isTherePin(startPoint: Point):Boolean {
        return if (drawMatrix[startPoint.y][startPoint.x] != null)
            drawMatrix[startPoint.y][startPoint.x]?.objectType == ObjectType.Pin
        else
            false
    }

    fun isThereNet(startPoint: Point):Boolean {
        return if (drawMatrix[startPoint.y][startPoint.x] != null)
            drawMatrix[startPoint.y][startPoint.x]?.objectType == ObjectType.Net
        else
            false
    }

    fun addElement(element: Element,point: Point): Boolean{
        return placer.addElement(element,point)
    }

    fun addNet(point: Point): Boolean{
        return placer.addNet(point)
    }

    fun moveObject(obj: Any,startPoint: Point,endPoint: Point): Boolean {
        return when (obj::class) {
            Element::class -> {
                placer.moveElement(obj as Element, startPoint, endPoint)
            }
            Net::class -> {
                placer.moveNet(obj as Net, startPoint, endPoint)
            }
            else -> {
                false
            }
        }
    }

    fun removeObject(obj: Any){
        when (obj::class) {
            Element::class -> {
                placer.removeElement(obj as Element)
            }
            Net::class -> {
                placer.removeNet(obj as Net)
            }
            else -> {
                return
            }
        }
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

    fun drawSelectedSquare(point: Point, canvas: Canvas) {
        if (drawMatrix[point.y][point.x] != null) {
            val drawPoint = drawMatrix[point.y][point.x]!!.drawPoint
            canvas.drawCircle(drawPoint.x + step / 2, drawPoint.y + step / 2, step / 2, selectedPaint)
        }
    }

    fun drawCircuit(circuit: Circuit,canvas: Canvas) {
        circuit.listElements.forEach {
            drawElement(it, canvas)
        }
        circuit.listNets.forEach {
            drawNet(it, canvas)
        }
    }

    private fun drawElement(element: Element, canvas: Canvas) {
        element.getPins().forEach {
            drawPin(it, canvas)
        }
    }

    private fun drawPin(pin: Pin, canvas: Canvas) {
        val point = pin.getPoint()
        val drawType = drawMatrix[point.y][point.x]!!.drawType
        val drawPoint = drawMatrix[point.y][point.x]!!.drawPoint
        if (pin.isConnected())
            drawConnectorRubber(pin.getNet()!!, pin, canvas)
        when (drawType) {
            PIN_CORNER_UP_LEFT -> {
                canvas.drawLine(drawPoint.x + step, drawPoint.y + step / 2,
                        drawPoint.x + step / 2, drawPoint.y + step / 2,elementPartPaint)
                canvas.drawLine(drawPoint.x + step / 2, drawPoint.y + step / 2,
                        drawPoint.x + step / 2, drawPoint.y + step,elementPartPaint)
            }
            PIN_CORNER_UP_RIGHT -> {
                canvas.drawLine(drawPoint.x, drawPoint.y + step / 2,
                        drawPoint.x + step / 2, drawPoint.y + step / 2,elementPartPaint)
                canvas.drawLine(drawPoint.x + step / 2, drawPoint.y + step / 2,
                        drawPoint.x + step / 2, drawPoint.y + step,elementPartPaint)
            }
            PIN_CORNER_DOWN_LEFT -> {
                canvas.drawLine(drawPoint.x + step / 2, drawPoint.y,
                        drawPoint.x + step / 2, drawPoint.y + step / 2,elementPartPaint)
                canvas.drawLine(drawPoint.x + step / 2, drawPoint.y + step / 2,
                        drawPoint.x + step, drawPoint.y + step / 2,elementPartPaint)
            }
            PIN_CORNER_DOWN_RIGHT -> {
                canvas.drawLine(drawPoint.x + step / 2, drawPoint.y,
                        drawPoint.x + step / 2, drawPoint.y + step / 2,elementPartPaint)
                canvas.drawLine(drawPoint.x + step / 2, drawPoint.y + step / 2,
                        drawPoint.x, drawPoint.y + step / 2,elementPartPaint)
            }
            PIN_SIDE_UP -> {
                canvas.drawLine(drawPoint.x + step / 2, drawPoint.y,
                        drawPoint.x + step / 2, drawPoint.y + step,elementPartPaint)
            }
            PIN_SIDE_DOWN -> {
                canvas.drawLine(drawPoint.x, drawPoint.y + step / 2,
                        drawPoint.x + step, drawPoint.y + step / 2,elementPartPaint)
            }
            PIN_SIDE_LEFT -> {
                canvas.drawLine(drawPoint.x + step / 2, drawPoint.y,
                        drawPoint.x + step / 2, drawPoint.y + step,elementPartPaint)
            }
            PIN_SIDE_RIGHT -> {
                canvas.drawLine(drawPoint.x + step / 2, drawPoint.y,
                        drawPoint.x + step / 2, drawPoint.y + step,elementPartPaint)
            }
            PIN_LINE_MIDDLE_VERTICAL -> {
                canvas.drawLine(drawPoint.x + step / 4, drawPoint.y,
                        drawPoint.x + 3 * step / 4, drawPoint.y + step,elementPartPaint)
                canvas.drawLine(drawPoint.x + 3 * step / 4, drawPoint.y,
                        drawPoint.x + step / 4, drawPoint.y + step,elementPartPaint)
            }
            PIN_LINE_MIDDLE_HORIZONTAL -> {
                canvas.drawLine(drawPoint.x, drawPoint.y + step / 4,
                        drawPoint.x + step, drawPoint.y + 3 * step / 4,elementPartPaint)
                canvas.drawLine(drawPoint.x, drawPoint.y + 3 * step / 4,
                        drawPoint.x + step, drawPoint.y + step / 4,elementPartPaint)
            }
            PIN_LINE_UP -> {
                canvas.drawLine(drawPoint.x + step / 4, drawPoint.y + step,
                        drawPoint.x + step / 4, drawPoint.y + step / 2,elementPartPaint)
                canvas.drawLine(drawPoint.x + step / 4, drawPoint.y + step / 2,
                        drawPoint.x + 3 * step / 4, drawPoint.y + step / 2,elementPartPaint)
                canvas.drawLine(drawPoint.x + 3 * step / 4, drawPoint.y + step / 2,
                        drawPoint.x + 3 * step / 4, drawPoint.y + step,elementPartPaint)
            }
            PIN_LINE_DOWN -> {
                canvas.drawLine(drawPoint.x + step / 4, drawPoint.y,
                        drawPoint.x + step / 4, drawPoint.y + step / 2,elementPartPaint)
                canvas.drawLine(drawPoint.x + step / 4, drawPoint.y + step / 2,
                        drawPoint.x + 3 * step / 4, drawPoint.y + step / 2,elementPartPaint)
                canvas.drawLine(drawPoint.x + 3 * step / 4, drawPoint.y + step / 2,
                        drawPoint.x + 3 * step / 4, drawPoint.y,elementPartPaint)
            }
            PIN_LINE_RIGHT -> {
                canvas.drawLine(drawPoint.x, drawPoint.y + step / 4,
                        drawPoint.x + step / 2, drawPoint.y + step / 4,elementPartPaint)
                canvas.drawLine(drawPoint.x + step / 2, drawPoint.y + step / 4,
                        drawPoint.x + step / 2, drawPoint.y + 3 * step / 4,elementPartPaint)
                canvas.drawLine(drawPoint.x + step / 2, drawPoint.y + 3 * step / 4,
                        drawPoint.x, drawPoint.y + 3 * step / 4,elementPartPaint)
            }
            PIN_LINE_LEFT -> {
                canvas.drawLine(drawPoint.x + step, drawPoint.y + step / 4,
                        drawPoint.x + step / 2, drawPoint.y + step / 4,elementPartPaint)
                canvas.drawLine(drawPoint.x + step / 2, drawPoint.y + step / 4,
                        drawPoint.x + step / 2, drawPoint.y + 3 * step / 4,elementPartPaint)
                canvas.drawLine(drawPoint.x + step / 2, drawPoint.y + 3 * step / 4,
                        drawPoint.x + step, drawPoint.y + 3 * step / 4,elementPartPaint)
            }
            else -> {
            }
        }
        drawPinCircle(pin, canvas)
    }

    private fun drawPinCircle(pin: Pin, canvas: Canvas) {
        val point = pin.getPoint()
        val drawPoint = drawMatrix[point.y][point.x]!!.drawPoint
        if (pin.isConnected())
            canvas.drawCircle(drawPoint.x + step / 2, drawPoint.y + step / 2, step / 6,
                    pinConnectedPaint)
        else
            canvas.drawCircle(drawPoint.x + step / 2, drawPoint.y + step / 2, step / 6,
                    pinPaint)
    }

    private fun drawNet(net: Net, canvas: Canvas) {
        val point = net.getPoint()
        val drawPoint = drawMatrix[point.y][point.x]!!.drawPoint
        canvas.drawCircle(drawPoint.x + step / 2, drawPoint.y + step / 2, step / 2f, netPaint)
        canvas.drawCircle(drawPoint.x + step / 2, drawPoint.y + step / 2, step / 2.5f, netPaint)
        canvas.drawCircle(drawPoint.x + step / 2, drawPoint.y + step / 2, step / 3.5f, netPaint)
        canvas.drawCircle(drawPoint.x + step / 2, drawPoint.y + step / 2, step / 5.5f, netPaint)

        canvas.drawLine(drawPoint.x + step / 2, drawPoint.y,
                drawPoint.x + step / 2, drawPoint.y + step,netPaint)
        canvas.drawLine(drawPoint.x, drawPoint.y + step / 2,
                drawPoint.x + step, drawPoint.y + step / 2,netPaint)
        //From left up to right down
        var degree = 225
        val orgX = (drawPoint.x + step) - (drawPoint.x + step / 2)
        val orgY = (drawPoint.y + step / 2) - (drawPoint.y + step / 2)
        var rotatedXStart = orgX * Math.cos(degree * Math.PI / 180) -
                orgY * Math.sin(degree * Math.PI / 180) + (drawPoint.x + step / 2)
        var rotatedYStart = orgX * Math.sin(degree * Math.PI / 180) +
                orgY * Math.cos(degree * Math.PI / 180) + (drawPoint.y + step / 2)
        degree = 45
        var rotatedXEnd = orgX * Math.cos(degree * Math.PI / 180) -
                orgY * Math.sin(degree * Math.PI / 180) + (drawPoint.x + step / 2)
        var rotatedYEnd = orgX * Math.sin(degree * Math.PI / 180) +
                orgY * Math.cos(degree * Math.PI / 180) + (drawPoint.y + step / 2)
        canvas.drawLine(rotatedXStart.toFloat(), rotatedYStart.toFloat(),
                rotatedXEnd.toFloat(), rotatedYEnd.toFloat(),netPaint)
        //From right up to left down
        degree = 315
        rotatedXStart = orgX * Math.cos(degree * Math.PI / 180) -
                orgY * Math.sin(degree * Math.PI / 180) + (drawPoint.x + step / 2)
        rotatedYStart = orgX * Math.sin(degree * Math.PI / 180) +
                orgY * Math.cos(degree * Math.PI / 180) + (drawPoint.y + step / 2)
        degree = 135
        rotatedXEnd = orgX * Math.cos(degree * Math.PI / 180) -
                orgY * Math.sin(degree * Math.PI / 180) + (drawPoint.x + step / 2)
        rotatedYEnd = orgX * Math.sin(degree * Math.PI / 180) +
                orgY * Math.cos(degree * Math.PI / 180) + (drawPoint.y + step / 2)
        canvas.drawLine(rotatedXStart.toFloat(), rotatedYStart.toFloat(),
                rotatedXEnd.toFloat(), rotatedYEnd.toFloat(),netPaint)
    }

    private fun drawConnectorRubber(net: Net, pin: Pin, canvas: Canvas){
        val pointNet = net.getPoint()
        val drawPointNet = drawMatrix[pointNet.y][pointNet.x]!!.drawPoint
        val pointPin = pin.getPoint()
        val drawPointPin = drawMatrix[pointPin.y][pointPin.x]!!.drawPoint
        canvas.drawLine(drawPointNet.x + step / 2,drawPointNet.y + step / 2,
                drawPointPin.x + step / 2, drawPointPin.y + step / 2,connectorPaint)
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
        netPaint.style = Paint.Style.STROKE
        netPaint.strokeJoin = Paint.Join.ROUND
        netPaint.strokeCap = Paint.Cap.ROUND
        netPaint.strokeWidth = 2f

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
        pinPaint.strokeWidth = 4f

        pinConnectedPaint.isAntiAlias = true
        pinConnectedPaint.isDither = true
        pinConnectedPaint.color = Color.GREEN
        pinConnectedPaint.style = Paint.Style.FILL
        pinConnectedPaint.strokeJoin = Paint.Join.ROUND
        pinConnectedPaint.strokeCap = Paint.Cap.ROUND
        pinConnectedPaint.strokeWidth = 4f

        connectorPaint.isAntiAlias = true
        connectorPaint.isDither = true
        connectorPaint.color = Color.GREEN
        connectorPaint.style = Paint.Style.STROKE
        connectorPaint.strokeJoin = Paint.Join.ROUND
        connectorPaint.strokeCap = Paint.Cap.ROUND
        connectorPaint.strokeWidth = 3f

        selectedPaint.isAntiAlias = true
        selectedPaint.isDither = true
        selectedPaint.color = Color.CYAN
        selectedPaint.style = Paint.Style.FILL
        selectedPaint.strokeJoin = Paint.Join.ROUND
        selectedPaint.strokeCap = Paint.Cap.ROUND
        selectedPaint.strokeWidth = 3f
    }
}