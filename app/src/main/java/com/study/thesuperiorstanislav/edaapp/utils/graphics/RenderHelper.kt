package com.study.thesuperiorstanislav.edaapp.utils.graphics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.Paint
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.study.thesuperiorstanislav.edaapp.R
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.*
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawType.*
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.ObjectType

class RenderHelper(private val rect: Rect,private val context: Context) {
    var drawMatrix: Array<Array<DrawObject?>> = emptyArray()
        set(value) {
            if (value.isEmpty()) {
                val nullDrawObject: DrawObject? = null
                val fixedVal = Array(sizeY) { Array(sizeX) { nullDrawObject } }
                field = fixedVal
                placer = Placer(fixedVal, sizeX, sizeY, step)

            } else {
                field = value
                placer = Placer(field, sizeX, sizeY, step)
            }
        }
    var isMatrixInit = false
    var step = 0f

    private var placer: Placer
    private val linesPaint = Paint()
    private val netPaint = Paint()
    private val elementPartPaint = Paint()
    private val pinPaint = Paint()
    private val pinConnectedPaint = Paint()
    private val connectorPaint = Paint()
    private val selectedPaint = Paint()
    private var sizeX = 60
    private var sizeY:Int

    init {
        val maxX = rect.right.toFloat()
        step = maxX / sizeX
        sizeY = (rect.bottom / step).toInt()
        val nullDrawObject: DrawObject? = null
        drawMatrix = Array(sizeY) { Array(sizeX) { nullDrawObject } }
        placer = Placer(drawMatrix , sizeX, sizeY, step)
        initPaint()

    }

    fun initDrawMatrix(circuit: Circuit){
        placer.initDrawMatrix(circuit)
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

    fun drawCircuit(circuit: Circuit,routingLines:List<List<Point>>,canvas: Canvas) {
        if (routingLines.isEmpty())
            circuit.listPins.forEach { pin ->
                drawConnectorRubber(pin.getNet()!!, pin, canvas)
            }
        else
            routingLines.forEach { line ->
                drawConnectorRout(line, canvas)
            }
        circuit.listElements.forEach {
            drawElement(it, canvas)
        }
        circuit.listNets.forEach {
            drawNet(it, canvas)
        }
    }

    private fun drawElement(element: Element, canvas: Canvas) {
        val pinFirst = element.getPins().first().getPoint()
        val pinLast = element.getPins().last().getPoint()
        val drawFirst = drawMatrix[pinFirst.y][pinFirst.x]!!.drawPoint
        val drawLast = drawMatrix[pinLast.y][pinLast.x]!!.drawPoint
        val drawRect = Rect()
        when (element.getDrawType()) {
            Element.DrawType.TWO_PART -> {
                if (drawFirst.y != drawLast.y) {
                    drawRect.top = (drawFirst.y + step / 2).toInt()
                    drawRect.bottom = (drawLast.y + step / 2).toInt()
                    drawRect.left = (drawFirst.x + step / 4).toInt()
                    drawRect.right = (drawFirst.x + 3 * step / 4).toInt()
                }else{
                    drawRect.top = (drawFirst.y + step / 4).toInt()
                    drawRect.bottom = (drawLast.y + 3 * step / 4).toInt()
                    drawRect.left = (drawFirst.x + step / 2).toInt()
                    drawRect.right = (drawLast.x + step / 2).toInt()
                }
                canvas.drawRect(drawRect,elementPartPaint)
                element.getPins().forEach {
                    drawPinCircle(it, canvas)
                }
            }
            Element.DrawType.THREE_PART -> {
                element.getPins().forEach {
                    drawPin(it, canvas)
                }
            }
            else -> {
                if (drawFirst.y > drawLast.y) {
                    drawRect.top = (drawLast.y + step / 2).toInt()
                    drawRect.bottom = (drawFirst.y + step / 2).toInt()
                }else{
                    drawRect.top = (drawFirst.y + step / 2).toInt()
                    drawRect.bottom = (drawLast.y + step / 2).toInt()
                }
                drawRect.left = (drawFirst.x + step / 2).toInt()
                drawRect.right = (drawLast.x + step / 2).toInt()
                canvas.drawRect(drawRect,elementPartPaint)
                element.getPins().forEach {
                    drawPinCircle(it, canvas)
                }
            }
        }

    }

    private fun drawPin(pin: Pin, canvas: Canvas) {
        val point = pin.getPoint()
        val drawType = drawMatrix[point.y][point.x]!!.drawType
        val drawPoint = drawMatrix[point.y][point.x]!!.drawPoint
        when (drawType) {
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
                val drawRect = Rect()
                drawRect.top = (drawPoint.y + step / 2).toInt()
                drawRect.bottom = (drawPoint.y + step).toInt()
                drawRect.left = (drawPoint.x + step / 4).toInt()
                drawRect.right = (drawPoint.x + 3 * step / 4).toInt()
                canvas.drawRect(drawRect,elementPartPaint)
            }
            PIN_LINE_DOWN -> {
                val drawRect = Rect()
                drawRect.top = (drawPoint.y).toInt()
                drawRect.bottom = (drawPoint.y + + step / 2).toInt()
                drawRect.left = (drawPoint.x + step / 4).toInt()
                drawRect.right = (drawPoint.x + 3 * step / 4).toInt()
                canvas.drawRect(drawRect,elementPartPaint)
            }
            PIN_LINE_RIGHT -> {
                val drawRect = Rect()
                drawRect.top = (drawPoint.y + step / 4).toInt()
                drawRect.bottom = (drawPoint.y + 3 * step / 4).toInt()
                drawRect.left = (drawPoint.x).toInt()
                drawRect.right = (drawPoint.x + step / 2).toInt()
                canvas.drawRect(drawRect,elementPartPaint)
            }
            PIN_LINE_LEFT -> {
                val drawRect = Rect()
                drawRect.top = (drawPoint.y + step / 4).toInt()
                drawRect.bottom = (drawPoint.y + 3 * step / 4).toInt()
                drawRect.left = (drawPoint.x + step / 2).toInt()
                drawRect.right = (drawPoint.x + step).toInt()
                canvas.drawRect(drawRect, elementPartPaint)
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

    private fun drawConnectorRout(line:List<Point>,canvas: Canvas) {
        var startPoint = line.first()
        line.forEach { endPoint ->
            if (startPoint != endPoint) {
                val xStart = step * startPoint.x + step / 2
                val yStart = step * startPoint.y + step / 2
                val xEnd = step * endPoint.x + step / 2
                val yEnd = step * endPoint.y + step / 2
                canvas.drawLine(xStart, yStart,
                        xEnd, yEnd, connectorPaint)
                startPoint = endPoint
            }
        }
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
        netPaint.color = ContextCompat.getColor(context, R.color.colorNet)
        netPaint.style = Paint.Style.STROKE
        netPaint.strokeJoin = Paint.Join.ROUND
        netPaint.strokeCap = Paint.Cap.ROUND
        netPaint.strokeWidth = 2f

        elementPartPaint.isAntiAlias = true
        elementPartPaint.isDither = true
        elementPartPaint.color = ContextCompat.getColor(context, R.color.colorElement)
        elementPartPaint.style = Paint.Style.FILL_AND_STROKE
        elementPartPaint.strokeJoin = Paint.Join.ROUND
        elementPartPaint.strokeCap = Paint.Cap.ROUND
        elementPartPaint.strokeWidth = 5f

        pinPaint.isAntiAlias = true
        pinPaint.isDither = true
        pinPaint.color = ContextCompat.getColor(context, R.color.colorPinDisConnected)
        pinPaint.style = Paint.Style.FILL
        pinPaint.strokeJoin = Paint.Join.ROUND
        pinPaint.strokeCap = Paint.Cap.ROUND
        pinPaint.strokeWidth = 5f

        pinConnectedPaint.isAntiAlias = true
        pinConnectedPaint.isDither = true
        pinConnectedPaint.color = ContextCompat.getColor(context, R.color.colorPinConnected)
        pinConnectedPaint.style = Paint.Style.FILL
        pinConnectedPaint.strokeJoin = Paint.Join.ROUND
        pinConnectedPaint.strokeCap = Paint.Cap.ROUND
        pinConnectedPaint.strokeWidth = 5f

        connectorPaint.isAntiAlias = true
        connectorPaint.isDither = true
        connectorPaint.color = ContextCompat.getColor(context, R.color.colorConnector)
        connectorPaint.style = Paint.Style.STROKE
        connectorPaint.strokeJoin = Paint.Join.ROUND
        connectorPaint.strokeCap = Paint.Cap.ROUND
        connectorPaint.strokeWidth = 4f

        selectedPaint.isAntiAlias = true
        selectedPaint.isDither = true
        selectedPaint.color = ContextCompat.getColor(context, R.color.colorSelect)
        selectedPaint.style = Paint.Style.FILL
        selectedPaint.strokeJoin = Paint.Join.ROUND
        selectedPaint.strokeCap = Paint.Cap.ROUND
        selectedPaint.strokeWidth = 3f
    }
}