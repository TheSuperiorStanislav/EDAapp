package com.study.thesuperiorstanislav.edaapp.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.study.thesuperiorstanislav.edaapp.R
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.utils.graphics.RenderHelper

class CircuitView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    private val rect = Rect()
    var renderHelper: RenderHelper? = null
    private var circuit: Circuit? = null

    var editEvent = EditEvent.DELETE_CONNECTION
    var drawTouch = false
    var startPoint = Point(-1, -1)

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

        if (drawTouch)
            renderHelper?.drawSelectedSquare(startPoint, canvas)


        if (circuit != null)
            renderHelper?.drawCircuit(circuit!!, canvas)


    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                when (editEvent) {
                    EditEvent.VIEW -> TODO()
                    EditEvent.ADD_ELEMENT -> TODO()
                    EditEvent.ADD_NET -> TODO()
                    EditEvent.ADD_CONNECTION -> TODO()
                    EditEvent.MOVE_ELEMENT -> moveElement(x, y)
                    EditEvent.MOVE_NET -> moveNet(x, y)
                    EditEvent.DELETE_ELEMENT -> deleteElement(x, y)
                    EditEvent.DELETE_NET -> deleteNet(x, y)
                    EditEvent.DELETE_CONNECTION -> deleteConnection(x, y)
                }
            }
        }
        performClick()
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun setCircuit(circuit: Circuit) {
        this.circuit = circuit
        invalidate()
    }

    fun changeEditEvent(editEvent: EditEvent) {
        this.editEvent = editEvent
        drawTouch = false
    }

    private fun moveElement(x: Float, y: Float) {
        if (drawTouch) {
            val endPoint = Point((x / renderHelper?.step!!).toInt(),
                    (y / renderHelper?.step!!).toInt())
            val obj = circuit?.listPins?.find {
                it.getPoint() == startPoint
            }!!.getElement()
            if (renderHelper?.moveObject(obj, startPoint, endPoint)!!) {
                obj.move(endPoint.x, endPoint.y)
                drawTouch = false
                invalidate()
            } else {
                onError(formatResStr(R.string.error_move, obj))
            }
        } else {
            startPoint = Point((x / renderHelper?.step!!).toInt(),
                    (y / renderHelper?.step!!).toInt())
            if (renderHelper?.isTherePin(startPoint)!!) {
                circuit?.listElements?.forEach { element ->
                    val point = element.getPins().find { it.getPoint() == startPoint }?.getElement()?.getPoint()
                    if (point != null) {
                        startPoint = point
                        drawTouch = true
                        invalidate()
                        return
                    }
                }
            }
        }
    }

    private fun moveNet(x: Float, y: Float) {
        if (drawTouch) {
            val endPoint = Point((x / renderHelper?.step!!).toInt(),
                    (y / renderHelper?.step!!).toInt())
            val obj = circuit?.listNets?.find {
                it.getPoint() == startPoint
            }!!
            if (renderHelper?.moveObject(obj, startPoint, endPoint)!!) {
                obj.move(endPoint.x, endPoint.y)
                drawTouch = false
                invalidate()
            } else {
                onError(formatResStr(R.string.error_move, obj))
            }
        } else {
            startPoint = Point((x / renderHelper?.step!!).toInt(),
                    (y / renderHelper?.step!!).toInt())
            if (renderHelper?.isThereNet(startPoint)!!) {
                drawTouch = true
                invalidate()
            }
        }
    }

    private fun deleteElement(x: Float, y: Float) {
        startPoint = Point((x / renderHelper?.step!!).toInt(),
                (y / renderHelper?.step!!).toInt())
        if (renderHelper?.isTherePin(startPoint)!!) {
            circuit?.listElements?.forEach { element ->
                val obj = element.getPins().find { it.getPoint() == startPoint }?.getElement()
                if (obj != null) {
                    val deleteDialog: AlertDialog = this.let {
                        val builder = AlertDialog.Builder(context)
                        builder.apply {
                            setTitle(formatResStr(R.string.delete_element, obj))
                            setMessage(R.string.sure_delete_net)
                            setPositiveButton(R.string.yes) { dialog, _ ->
                                renderHelper?.removeObject(obj)
                                obj.getPins().forEach { pin ->
                                    pin.removeFromNet()
                                    circuit?.listPins?.remove(pin)
                                }
                                circuit?.listElements?.remove(obj)
                                invalidate()
                                dialog.dismiss()
                            }
                            setNegativeButton(R.string.no) { dialog, _ ->
                                dialog.dismiss()
                            }
                        }
                        builder.create()
                    }
                    deleteDialog.show()
                    return
                }
            }
        }
    }

    private fun deleteNet(x: Float, y: Float) {
        startPoint = Point((x / renderHelper?.step!!).toInt(),
                (y / renderHelper?.step!!).toInt())
        if (renderHelper?.isThereNet(startPoint)!!) {
            val obj = circuit?.listNets?.find {
                it.getPoint() == startPoint
            }!!

            val deleteDialog: AlertDialog = this.let {
                val builder = AlertDialog.Builder(context)
                builder.apply {
                    setTitle(formatResStr(R.string.delete_net, obj))
                    setMessage(R.string.sure_delete_net)
                    setPositiveButton(R.string.yes) { dialog, _ ->
                        renderHelper?.removeObject(obj)
                        obj.getPins().forEach { pin ->
                            circuit?.listPins?.remove(pin)
                        }
                        circuit?.listNets?.remove(obj)
                        invalidate()
                        dialog.dismiss()
                    }
                    setNegativeButton(R.string.no) { dialog, _ ->
                        dialog.dismiss()
                    }
                }
                builder.create()
            }
            deleteDialog.show()
        }
    }

    private fun deleteConnection(x: Float, y: Float) {
        if (drawTouch) {
            val endPoint = Point((x / renderHelper?.step!!).toInt(),
                    (y / renderHelper?.step!!).toInt())
            if (renderHelper?.isTherePin(endPoint)!!) {
                val pin = circuit?.listPins?.find { pin -> pin.getPoint() == endPoint }
                if (pin != null) {
                    val net = circuit?.listNets?.find {
                        it.getPoint() == startPoint }!!
                    if (pin.getNet() == net) {
                        pin.removeFromNet()
                        circuit?.listPins?.remove(pin)
                        drawTouch = false
                        invalidate()
                    }
                } else
                    onError(R.string.error_delete_connection)
            }

        } else {
            startPoint = Point((x / renderHelper?.step!!).toInt(),
                    (y / renderHelper?.step!!).toInt())
            if (renderHelper?.isThereNet(startPoint)!!) {
                drawTouch = true
                invalidate()
            } else if (renderHelper?.isTherePin(startPoint)!!) {
                val obj = circuit?.listPins?.find {
                    it.getPoint() == startPoint
                }
                if (obj != null) {
                    val net = obj.getNet()!!
                    val deleteDialog: AlertDialog = this.let {
                        val builder = AlertDialog.Builder(context)
                        builder.apply {
                            setTitle(formatResStr(R.string.delete_connection, obj, net))
                            setMessage(R.string.sure_delete_connection)
                            setPositiveButton(R.string.yes) { dialog, _ ->
                                obj.setIsConnected(false)
                                invalidate()
                                dialog.dismiss()
                            }
                            setNegativeButton(R.string.no) { dialog, _ ->
                                dialog.dismiss()
                            }
                        }
                        builder.create()
                    }
                    deleteDialog.show()
                } else
                    onError(R.string.error_delete_connection)
            }
        }
    }

    private fun formatResStr(idStr: Int, obj: Any): String {
        return String.format(resources.getString(idStr), obj)
    }

    private fun formatResStr(idStr: Int, obj1: Any, obj2: Any): String {
        return String.format(resources.getString(idStr), obj1, obj2)
    }

    private fun onError(message: String) {
        val snackBar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
        snackBar.setAction("¯\\(°_o)/¯") { }
        snackBar.show()
    }

    private fun onError(idStr: Int) {
        val snackBar = Snackbar.make(this, idStr, Snackbar.LENGTH_SHORT)
        snackBar.setAction("¯\\(°_o)/¯") { }
        snackBar.show()
    }

    enum class EditEvent {
        VIEW,
        ADD_ELEMENT,
        ADD_NET,
        ADD_CONNECTION,
        MOVE_ELEMENT,
        MOVE_NET,
        DELETE_ELEMENT,
        DELETE_NET,
        DELETE_CONNECTION
    }
}