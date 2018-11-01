package com.study.thesuperiorstanislav.edaapp.main

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.utils.graphics.RenderHelper


class CircuitView : View {
    constructor(context: Context) : super(context){}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    val rect = Rect()
    var renderHelper: RenderHelper? = null
    private var circuit: Circuit? = null

    var drawTouch = false
    var xT = 0f
    var yT = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.getLocalVisibleRect(rect)
        renderHelper = RenderHelper(rect)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        renderHelper?.drawLines(canvas)

        if (circuit != null && !renderHelper?.isMatrixInit!!)
            renderHelper?.initDrawMatrix(circuit!!)

        if (circuit != null)
            renderHelper?.drawCircuit(circuit!!, canvas)

        val paint = Paint()
        paint.color = Color.CYAN
        paint.style = Paint.Style.FILL
        if (drawTouch) {
            canvas.drawCircle(xT, yT, 10f, paint)
        }


//        if (drawMatrix != null){
//            drawMatrix?.forEachIndexed { indexY, booleans ->
//                booleans.forEachIndexed { indexX, drawn ->
//                    if (drawn) {
//                        val path = Path()
//                        path.moveTo(step * indexX, step * indexY + step / 2)
//                        path.lineTo(step * indexX + step / 2, step * indexY + step)
//                        path.moveTo(step * indexX + step / 2, step * indexY + step)
//                        path.lineTo(step * indexX + step, step * indexY + step / 2)
//                        path.moveTo(step * indexX + step, step * indexY + step / 2)
//                        path.lineTo(step * indexX + step, step * indexY)
//                        path.moveTo(step * indexX + step, step * indexY)
//                        path.lineTo(step * indexX + step / 2, step * indexY + step / 2)
//                        path.moveTo(step * indexX + step / 2, step * indexY + step / 2)
//                        path.lineTo(step * indexX, step * indexY)
//                        path.moveTo(step * indexX, step * indexY)
//                        path.lineTo(step * indexX, step * indexY + step / 2)
//                        path.close()
//                        canvas.drawPath(path, mPaint)
//                    }
//                }
//            }
//        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                drawTouch = true
                xT = x
                yT = y
                invalidate()
            }
        }
        return true
    }

    fun setCircuit(circuit: Circuit){
        this.circuit = circuit
        invalidate()
    }

}