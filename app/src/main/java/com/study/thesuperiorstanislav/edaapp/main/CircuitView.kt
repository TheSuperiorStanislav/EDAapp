package com.study.thesuperiorstanislav.edaapp.main

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.utils.graphics.RenderHelper


class CircuitView : View {
    constructor(context: Context) : super(context){}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    var sizeX = 30
    var sizeY = 0
    var step = 0f
    val rect = Rect()
    var renderHelper: RenderHelper? = null
    private var circuit: Circuit? = null

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

        circuit?.listElements?.forEach {
            renderHelper?.drawElement(it, canvas)
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

//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        val x = event.x
//        val y = event.y
//
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                drawRect(x, y)
//                invalidate()
//            }
//        }
//        return true
//    }

//    fun drawRect(x:Float, y:Float){
//        //TODO: check X and Y
//        if (drawMatrix != null){
//            drawMatrix?.forEachIndexed { indexY, booleans ->
//                booleans.forEachIndexed { indexX, drawn ->
//                    if (drawn){
//                        drawMatrix!![indexY][indexX] = false
//                    }
//                }
//            }
//            drawMatrix!![(y/step).toInt()][(x/step).toInt()] = true
//        }
//    }

    fun setCircuit(circuit: Circuit){
        this.circuit = circuit
        invalidate()
    }

}