package com.study.thesuperiorstanislav.edaapp.main

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.MotionEvent
import com.study.thesuperiorstanislav.edaapp.utils.graphics.RenderHelper


class CircuitView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var drawMatrix: Array<Array<Boolean>>? = null

    var sizeX = 30
    var sizeY = 0
    var step = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val rect = Rect()
        this.getLocalVisibleRect(rect)

        val renderHelper = RenderHelper(rect)

        renderHelper.drawLines(canvas)

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


}