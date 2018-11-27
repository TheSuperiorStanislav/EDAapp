package com.study.thesuperiorstanislav.edaapp.editor

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.study.thesuperiorstanislav.edaapp.R
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Element
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Pin
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawType.*
import com.study.thesuperiorstanislav.edaapp.utils.graphics.Placer

class ElementView:View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var element: Element = Element("")

    private val rect = Rect()
    private var sizeX = 12
    private var sizeY = 2
    private var step = 30f
    private val nullDrawObject: DrawObject? = null
    private var drawMatrix = Array(sizeY) { Array(sizeX) { nullDrawObject } }
    private var placer = Placer(drawMatrix, sizeX, sizeY, step)

    private val elementPartPaint = Paint()
    private val pinPaint = Paint()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val desiredWidth = 24*30
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
        drawMatrix = placer.initDrawMatrix(element)
        drawElement(element,canvas)
    }

    fun setElement(element: Element) {
        this.element = element
        invalidate()
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
        canvas.drawCircle(drawPoint.x + step / 2, drawPoint.y + step / 2, step / 6,
                pinPaint)
    }

    private fun initPaint(){
        elementPartPaint.isAntiAlias = true
        elementPartPaint.isDither = true
        elementPartPaint.color = ContextCompat.getColor(context, R.color.colorElement)
        elementPartPaint.style = Paint.Style.FILL_AND_STROKE
        elementPartPaint.strokeJoin = Paint.Join.ROUND
        elementPartPaint.strokeCap = Paint.Cap.ROUND
        elementPartPaint.strokeWidth = 7f

        pinPaint.isAntiAlias = true
        pinPaint.isDither = true
        pinPaint.color = ContextCompat.getColor(context, R.color.colorPinDisConnected)
        pinPaint.style = Paint.Style.FILL
        pinPaint.strokeJoin = Paint.Join.ROUND
        pinPaint.strokeCap = Paint.Cap.ROUND
        pinPaint.strokeWidth = 5f
    }
}