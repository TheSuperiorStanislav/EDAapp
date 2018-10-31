package com.study.thesuperiorstanislav.edaapp.main

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.MotionEvent




class CircuitView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private val rectf = Rect()

    private var drawMatrix: Array<Array<Boolean>>? = null

    var sizeX = 30
    var sizeY = 0
    var step = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.getLocalVisibleRect(rectf)
        val mPaint = Paint()
        mPaint.setAntiAlias(true)
        mPaint.setDither(true)
        mPaint.setColor(-0x10000)
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE)
        mPaint.setStrokeJoin(Paint.Join.ROUND)
        mPaint.setStrokeCap(Paint.Cap.ROUND)
        mPaint.setStrokeWidth(3f)

        val maxX = rectf.right.toFloat()
        val maxY = rectf.bottom.toFloat()

        step = maxX/sizeX

        sizeY = (rectf.bottom/step).toInt()

        if (drawMatrix == null){
            drawMatrix = Array(sizeY){_ -> Array(sizeX){_ -> false}}
        }

        var cordX = 0f
        var cordY = 0f

        for (i in 1 .. sizeX + 1){
            canvas.drawLine(cordX,0f,cordX,sizeY*step,mPaint)
            cordX+= step
        }

        for (i in 1 .. sizeY + 1){
            canvas.drawLine(0f,cordY,rectf.right.toFloat(),cordY,mPaint)
            cordY+= step
        }

        if (drawMatrix != null){
            drawMatrix?.forEachIndexed { indexY, booleans ->
                booleans.forEachIndexed { indexX, drawn ->
                    if (drawn) {
                        val path = Path()
                        path.moveTo(step * indexX, step * indexY + step / 2)
                        path.lineTo(step * indexX + step / 2, step * indexY + step)
                        path.moveTo(step * indexX + step / 2, step * indexY + step)
                        path.lineTo(step * indexX + step, step * indexY + step / 2)
                        path.moveTo(step * indexX + step, step * indexY + step / 2)
                        path.lineTo(step * indexX + step, step * indexY)
                        path.moveTo(step * indexX + step, step * indexY)
                        path.lineTo(step * indexX + step / 2, step * indexY + step / 2)
                        path.moveTo(step * indexX + step / 2, step * indexY + step / 2)
                        path.lineTo(step * indexX, step * indexY)
                        path.moveTo(step * indexX, step * indexY)
                        path.lineTo(step * indexX, step * indexY + step / 2)
                        path.close()
                        canvas.drawPath(path, mPaint)
                    }
                }
            }
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                drawRect(x, y)
                invalidate()
            }
        }
        return true
    }

    fun drawRect(x:Float, y:Float){
        //TODO: check X and Y
        if (drawMatrix != null){
            drawMatrix?.forEachIndexed { indexY, booleans ->
                booleans.forEachIndexed { indexX, drawn ->
                    if (drawn){
                        drawMatrix!![indexY][indexX] = false
                    }
                }
            }
            drawMatrix!![(y/step).toInt()][(x/step).toInt()] = true
        }
    }


}