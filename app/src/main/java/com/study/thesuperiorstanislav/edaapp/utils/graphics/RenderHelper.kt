package com.study.thesuperiorstanislav.edaapp.utils.graphics

import android.graphics.*
import android.util.Log
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Element
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Net
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Pin
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.DrawObject
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.DrawType.*

class RenderHelper(private val rect: Rect) {
    private var placer: Placer
    private var drawMatrix: Array<Array<DrawObject?>>
    var isMatrixInit = false

    private val linesPaint = Paint()
    private val netPaint = Paint()
    private val elementPartPaint = Paint()
    private val pinPaint = Paint()
    private val connectorPaint = Paint()

    private var sizeX = 50
    private var sizeY:Int
    private var step = 0f

    init {
        val maxX = rect.right.toFloat()
        step = maxX / sizeX
        sizeY = (rect.bottom / step).toInt()
        val nullDrawObject: DrawObject? = null
        drawMatrix = Array(sizeY) { Array(sizeX) { nullDrawObject } }
        initPaint()
        placer = Placer(drawMatrix, sizeX, sizeY, step)
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

    fun drawCircuit(circuit: Circuit,canvas: Canvas) {
        circuit.listElements.forEach {
            drawElement(it, canvas)
        }
        circuit.listNets.forEach {
            drawNet(it, canvas)
            it.getPins().forEach { pin ->
                drawConnectorRubber(it, pin, canvas)
            }
        }
    }

    private fun drawElement(element: Element, canvas: Canvas) {
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

    private fun drawNet(net: Net, canvas: Canvas){
        val point = net.getPoint()
        val drawPoint = drawMatrix[point.y][point.x]!!.drawPoint
        canvas.drawCircle(drawPoint.x + step/2,drawPoint.y + step/2,step/2f,netPaint)
        canvas.drawCircle(drawPoint.x + step/2,drawPoint.y + step/2,step/2.5f,netPaint)
        canvas.drawCircle(drawPoint.x + step/2,drawPoint.y + step/2,step/3.5f,netPaint)
        canvas.drawCircle(drawPoint.x + step/2,drawPoint.y + step/2,step/5.5f,netPaint)

        val path = Path()
        path.moveTo(drawPoint.x + step / 2, drawPoint.y)
        path.lineTo(drawPoint.x + step / 2, drawPoint.y + step)
        path.moveTo(drawPoint.x + step / 2, drawPoint.y + step)
        canvas.drawPath(path,netPaint)
        path.reset()
        path.moveTo(drawPoint.x, drawPoint.y + step/2)
        path.lineTo(drawPoint.x + step, drawPoint.y + step / 2)
        path.moveTo(drawPoint.x + step, drawPoint.y + step / 2)
        canvas.drawPath(path,netPaint)
        path.reset()
        path.moveTo(drawPoint.x, drawPoint.y)
        path.lineTo(drawPoint.x + step, drawPoint.y + step)
        path.moveTo(drawPoint.x + step, drawPoint.y + step)
        canvas.drawPath(path,netPaint)
        path.reset()
        path.moveTo(drawPoint.x, drawPoint.y + step)
        path.lineTo(drawPoint.x + step, drawPoint.y)
        path.moveTo(drawPoint.x + step, drawPoint.y)
        canvas.drawPath(path,netPaint)
    }

    private fun drawConnectorRubber(net: Net, pin: Pin, canvas: Canvas){
        val pointNet = net.getPoint()
        val drawPointNet = drawMatrix[pointNet.y][pointNet.x]!!.drawPoint
        val pointPin = pin.getPoint()
        val drawPointPin = drawMatrix[pointPin.y][pointPin.x]!!.drawPoint
        val path = Path()
        path.moveTo(drawPointNet.x + step / 2, drawPointNet.y + step / 2)
        path.lineTo(drawPointPin.x + step / 2, drawPointPin.y + step / 2)
        path.moveTo(drawPointPin.x + step / 2, drawPointPin.y + step / 2)
        canvas.drawPath(path,connectorPaint)
    }

    fun initDrawMatrix(circuit: Circuit){
        drawMatrix = placer.initDrawMatrix(circuit)
        isMatrixInit = true
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