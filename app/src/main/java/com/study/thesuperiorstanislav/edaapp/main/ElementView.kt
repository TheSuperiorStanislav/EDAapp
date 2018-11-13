package com.study.thesuperiorstanislav.edaapp.main

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Element
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Pin
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.DrawObject
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.DrawType
import com.study.thesuperiorstanislav.edaapp.utils.graphics.Placer

class ElementView:View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var element: Element? = null

    private val rect = Rect()
    private var sizeX = 8
    private var sizeY = 2
    private var step = 30f
    private val nullDrawObject: DrawObject? = null
    private var drawMatrix = Array(sizeY) { Array(sizeX) { nullDrawObject } }
    private var placer = Placer(drawMatrix, sizeX, sizeY, step)

    private val elementPartPaint = Paint()
    private val pinPaint = Paint()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val desiredWidth = 16*30
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)


        //Measure Width
        val width = when (widthMode) {
            View.MeasureSpec.EXACTLY -> //Must be this size
                widthSize
            View.MeasureSpec.AT_MOST -> //Can't be bigger than...
                Math.min(desiredWidth, widthSize)
            View.MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> //Be whatever you want
                desiredWidth
        }

        step = width.toFloat() / sizeX
        val desiredHeight = (2 * step).toInt()
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        //Measure Height
        val height = when (heightMode) {
            View.MeasureSpec.EXACTLY -> //Must be this size
                heightSize
            View.MeasureSpec.AT_MOST -> //Can't be bigger than...
                Math.min(desiredHeight, heightSize)
            View.MeasureSpec.UNSPECIFIED -> desiredHeight
            else -> //Be whatever you want
                desiredHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.getLocalVisibleRect(rect)
        placer = Placer(drawMatrix, sizeX, sizeY, step)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        initPaint()
        drawMatrix = placer.initDrawMatrix(element!!)

        element?.getPins()?.forEach {
            drawPin(it,canvas)
        }
    }

    fun setElement(element: Element) {
        this.element = element
        invalidate()
    }

    private fun drawPin(pin: Pin, canvas: Canvas) {
        val point = pin.getPoint()
        val drawType = drawMatrix[point.y][point.x]!!.drawType
        val drawPoint = drawMatrix[point.y][point.x]!!.drawPoint
        val path = Path()
        when (drawType) {
            DrawType.PIN_CORNER_UP_LEFT -> {
                path.moveTo(drawPoint.x + step / 2, drawPoint.y + step)
                path.moveTo(drawPoint.x + step, drawPoint.y + step / 2)
                path.lineTo(drawPoint.x + step / 2, drawPoint.y + step / 2)
                path.moveTo(drawPoint.x + step / 2, drawPoint.y + step / 2)
                path.lineTo(drawPoint.x + step / 2, drawPoint.y + step)
                path.moveTo(drawPoint.x + step / 2, drawPoint.y + step)
            }
            DrawType.PIN_CORNER_UP_RIGHT -> {
                path.moveTo(drawPoint.x, drawPoint.y + step / 2)
                path.lineTo(drawPoint.x + step / 2, drawPoint.y + step / 2)
                path.moveTo(drawPoint.x + step / 2, drawPoint.y + step / 2)
                path.lineTo(drawPoint.x + step / 2, drawPoint.y + step)
                path.moveTo(drawPoint.x + step / 2, drawPoint.y + step)
            }
            DrawType.PIN_CORNER_DOWN_LEFT -> {
                path.moveTo(drawPoint.x + step / 2, drawPoint.y)
                path.lineTo(drawPoint.x + step / 2, drawPoint.y + step / 2)
                path.moveTo(drawPoint.x + step / 2, drawPoint.y + step / 2)
                path.lineTo(drawPoint.x + step, drawPoint.y + step / 2)
                path.moveTo(drawPoint.x + step, drawPoint.y + step / 2)
            }
            DrawType.PIN_CORNER_DOWN_RIGHT -> {
                path.moveTo(drawPoint.x + step / 2, drawPoint.y)
                path.lineTo(drawPoint.x + step / 2, drawPoint.y + step / 2)
                path.moveTo(drawPoint.x + step / 2, drawPoint.y + step / 2)
                path.lineTo(drawPoint.x, drawPoint.y + step / 2)
                path.moveTo(drawPoint.x, drawPoint.y + step / 2)
            }
            DrawType.PIN_SIDE_UP -> {
                path.moveTo(drawPoint.x, drawPoint.y + step / 2)
                path.lineTo(drawPoint.x + step, drawPoint.y + step / 2)
                path.moveTo(drawPoint.x + step, drawPoint.y + step / 2)
            }
            DrawType.PIN_SIDE_DOWN -> {
                path.moveTo(drawPoint.x, drawPoint.y + step / 2)
                path.lineTo(drawPoint.x + step, drawPoint.y + step / 2)
                path.moveTo(drawPoint.x + step, drawPoint.y + step / 2)
            }
            DrawType.PIN_SIDE_LEFT -> {
                path.moveTo(drawPoint.x + step / 2, drawPoint.y)
                path.lineTo(drawPoint.x + step / 2, drawPoint.y + step)
                path.moveTo(drawPoint.x + step / 2, drawPoint.y + step)
            }
            DrawType.PIN_SIDE_RIGHT -> {
                path.moveTo(drawPoint.x + step / 2, drawPoint.y)
                path.lineTo(drawPoint.x + step / 2, drawPoint.y + step)
                path.moveTo(drawPoint.x + step / 2, drawPoint.y + step)
            }
            DrawType.PIN_LINE_MIDDLE -> {

            }
            DrawType.PIN_LINE_UP -> {
                path.moveTo(drawPoint.x + step / 4, drawPoint.y + step)
                path.lineTo(drawPoint.x + step / 4, drawPoint.y + step / 2)
                path.moveTo(drawPoint.x + step / 4, drawPoint.y + step / 2)
                path.lineTo(drawPoint.x + 3 * step / 4, drawPoint.y + step / 2)
                path.moveTo(drawPoint.x + 3 * step / 4, drawPoint.y + step / 2)
                path.lineTo(drawPoint.x + 3 * step / 4, drawPoint.y + step)
                path.moveTo(drawPoint.x + 3 * step / 4, drawPoint.y + step)
            }
            DrawType.PIN_LINE_DOWN -> {
                path.moveTo(drawPoint.x + step / 4, drawPoint.y)
                path.lineTo(drawPoint.x + step / 4, drawPoint.y + step / 2)
                path.moveTo(drawPoint.x + step / 4, drawPoint.y + step / 2)
                path.lineTo(drawPoint.x + 3 * step / 4, drawPoint.y + step / 2)
                path.moveTo(drawPoint.x + 3 * step / 4, drawPoint.y + step / 2)
                path.lineTo(drawPoint.x + 3 * step / 4, drawPoint.y)
                path.moveTo(drawPoint.x + 3 * step / 4, drawPoint.y)
            }
            DrawType.PIN_LINE_RIGHT -> {
                path.moveTo(drawPoint.x, drawPoint.y + step / 4)
                path.lineTo(drawPoint.x + step / 2, drawPoint.y + step / 4)
                path.moveTo(drawPoint.x + step / 2, drawPoint.y + step / 4)
                path.lineTo(drawPoint.x + step / 2, drawPoint.y + 3 * step / 4)
                path.moveTo(drawPoint.x + step / 2, drawPoint.y + 3 * step / 4)
                path.lineTo(drawPoint.x, drawPoint.y + 3 * step / 4)
                path.moveTo(drawPoint.x, drawPoint.y + 3 * step / 4)
            }
            DrawType.PIN_LINE_LEFT -> {
                path.moveTo(drawPoint.x + step, drawPoint.y + step / 4)
                path.lineTo(drawPoint.x + step / 2, drawPoint.y + step / 4)
                path.moveTo(drawPoint.x + step / 2, drawPoint.y + step / 4)
                path.lineTo(drawPoint.x + step / 2, drawPoint.y + 3 * step / 4)
                path.moveTo(drawPoint.x + step / 2, drawPoint.y + 3 * step / 4)
                path.lineTo(drawPoint.x + step, drawPoint.y + 3 * step / 4)
                path.moveTo(drawPoint.x + step, drawPoint.y + 3 * step / 4)
            }
            else -> {

            }
        }
        canvas.drawPath(path, elementPartPaint)
        drawPinCircle(pin, canvas)
    }

    private fun drawPinCircle(pin: Pin, canvas: Canvas){
        val point = pin.getPoint()
        val drawPoint = drawMatrix[point.y][point.x]!!.drawPoint
        canvas.drawCircle(drawPoint.x + step/2,drawPoint.y + step/2,step/6,pinPaint)
    }

    private fun initPaint(){
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
        pinPaint.strokeWidth = 5f
    }
}