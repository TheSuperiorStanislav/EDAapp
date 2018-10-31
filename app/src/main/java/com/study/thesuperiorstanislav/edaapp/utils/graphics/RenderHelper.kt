package com.study.thesuperiorstanislav.edaapp.utils.graphics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.DrawObject

class RenderHelper(private val rect: Rect) {
    private var drawMatrix: Array<Array<DrawObject?>>

    private val linesPaint = Paint()
    private val netPaint = Paint()
    private val pinPaint = Paint()
    private val connectorPaint = Paint()

    var sizeX = 35
    var sizeY = 0
    var step = 0f

    init {
        val maxX = rect.right.toFloat()
        step = maxX/sizeX
        sizeY = (rect.bottom/step).toInt()
        val nullDrawObject:DrawObject? = null
        drawMatrix = Array(sizeY){_ -> Array(sizeX){_ -> nullDrawObject}}
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

    private fun initPaint(){
        linesPaint.isAntiAlias = true
        linesPaint.isDither = true
        linesPaint.color = Color.BLACK
        linesPaint.style = Paint.Style.FILL
        linesPaint.strokeJoin = Paint.Join.ROUND
        linesPaint.strokeCap = Paint.Cap.ROUND
        linesPaint.strokeWidth = 2f
    }
}