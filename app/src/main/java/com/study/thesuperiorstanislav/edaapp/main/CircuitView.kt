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
import com.study.thesuperiorstanislav.edaapp.utils.graphics.RenderHelper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import android.widget.TextView
import androidx.annotation.LayoutRes
import android.widget.ArrayAdapter
import androidx.annotation.Nullable
import android.widget.Toast
import android.widget.AdapterView.OnItemClickListener
import com.study.thesuperiorstanislav.edaapp.main.domain.model.*


class CircuitView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    private val rect = Rect()
    private var renderHelper: RenderHelper? = null
    private var circuit: Circuit? = null

    private var editEvent = EditEvent.MOVE_ELEMENT
    private var drawTouch = false
    private var startPoint = Point(-1, -1)

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
                    EditEvent.ADD_ELEMENT -> addElement(x, y)
                    EditEvent.ADD_NET -> addNet(x, y)
                    EditEvent.EDIT_CONNECTION -> editConnection(x,y)
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

    private fun addElement(x: Float, y: Float) {
        startPoint = makePoint(x,y)
        val adapter = ElementAdapter(context,resources.getStringArray(R.array.elements_array))

        val addDialog: AlertDialog = this.let {
            val builder = AlertDialog.Builder(context)
            builder.apply {
                setTitle(R.string.add_element)
                builder.setAdapter(adapter) { _, _ -> }
                setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
            }
            builder.create()
        }

        addDialog.setOnShowListener { dialogInterface ->
            val listView = (dialogInterface as AlertDialog).listView
            listView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
                val elementName = circuit?.generateElementName(adapter.getItem(position)!!)
                val element = Element(elementName!!)
                if (renderHelper?.addElement(element,startPoint)!!){
                    circuit?.listElements?.add(element)
                    invalidate()
                    dialogInterface.dismiss()

                }else{
                    onErrorToast(formatResStr(R.string.error_place, resources.getString(R.string.element)))
                }

            }
        }

        addDialog.show()
    }

    private fun addNet(x: Float, y: Float) {
        startPoint = makePoint(x,y)
        val pairForDialog = createViewForAddNetDialogAndId()

        val addDialog: AlertDialog = this.let {
            val builder = AlertDialog.Builder(context)
            builder.apply {
                setTitle(R.string.add_net)
                setMessage(R.string.message_add_net)
                setPositiveButton(R.string.add) { _, _ -> }
                setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
            }
            builder.create()
        }

        addDialog.setOnShowListener { dialogInterface ->
            val button = (dialogInterface as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val editText = dialogInterface.findViewById<EditText>(pairForDialog.second)
                if (!editText?.text?.isEmpty()!!) {
                    if (renderHelper?.addNet(startPoint)!!) {
                        val net = Net(editText.text.toString())
                        net.move(startPoint.x, startPoint.y)
                        circuit?.listNets?.add(net)
                        invalidate()
                        dialogInterface.dismiss()
                    } else {
                        onErrorToast(formatResStr(R.string.error_place, resources.getString(R.string.net)))
                    }
                }
            }
        }

        addDialog.setView(pairForDialog.first)
        addDialog.show()
    }

    private fun editConnection(x: Float, y: Float) {
        if (drawTouch) {
            val endPoint = makePoint(x,y)
            if (renderHelper?.isThereNet(endPoint)!! && renderHelper?.isTherePin(startPoint)!!) {
                val pin = circuit?.findPinByPoint(startPoint)!!
                val net = circuit?.findNetByPoint(endPoint)!!
                pin.setNet(net)
                if (!net.getPins().contains(pin))
                    net.addPin(pin)
                if (!circuit?.listPins?.contains(pin)!!)
                    circuit?.listPins?.add(pin)
                drawTouch = false
                invalidate()
            } else if (renderHelper?.isThereNet(startPoint)!! && renderHelper?.isTherePin(endPoint)!!) {
                val pin = circuit?.findPinByPoint(endPoint)!!
                val net = circuit?.findNetByPoint(startPoint)!!
                if (!pin.isConnected()){
                    if (!net.getPins().contains(pin))
                        net.addPin(pin)
                    pin.setNet(net)
                    if (!circuit?.listPins?.contains(pin)!!)
                        circuit?.listPins?.add(pin)
                    drawTouch = false
                    invalidate()
                }else{
                    onError(formatResStr(R.string.error_edit_connection,pin.getName()))
                }
            }

        } else {
            startPoint = makePoint(x,y)
            if (renderHelper?.isThereNet(startPoint)!!) {
                drawTouch = true
                invalidate()
            } else if (renderHelper?.isTherePin(startPoint)!!) {
                drawTouch = true
                invalidate()
            }
        }
    }

    private fun moveElement(x: Float, y: Float) {
        if (drawTouch) {
            val endPoint = makePoint(x,y)
            val obj = circuit?.findElementByPoint(startPoint)!!
            if (renderHelper?.moveObject(obj, startPoint, endPoint)!!) {
                obj.move(endPoint.x, endPoint.y)
                drawTouch = false
                invalidate()
            } else {
                onError(formatResStr(R.string.error_place, obj))
            }
        } else {
            startPoint = makePoint(x,y)
            if (renderHelper?.isTherePin(startPoint)!!) {
                val point = circuit?.findPinByPoint(startPoint)?.getElement()?.getPoint()
                if (point != null) {
                    startPoint = point
                    drawTouch = true
                    invalidate()
                }
            }
        }
    }

    private fun moveNet(x: Float, y: Float) {
        if (drawTouch) {
            val endPoint = makePoint(x,y)
            val obj = circuit?.findNetByPoint(startPoint)!!
            if (renderHelper?.moveObject(obj, startPoint, endPoint)!!) {
                obj.move(endPoint.x, endPoint.y)
                drawTouch = false
                invalidate()
            } else {
                onError(formatResStr(R.string.error_place, obj))
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
        startPoint = makePoint(x,y)
        if (renderHelper?.isTherePin(startPoint)!!) {
            val obj = circuit?.findElementByPinPoint(startPoint)
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
            }
        }
    }

    private fun deleteNet(x: Float, y: Float) {
        startPoint = makePoint(x,y)
        if (renderHelper?.isThereNet(startPoint)!!) {
            val obj = circuit?.findNetByPoint(startPoint)!!
            val deleteDialog: AlertDialog = this.let {
                val builder = AlertDialog.Builder(context)
                builder.apply {
                    setTitle(formatResStr(R.string.delete_net, obj))
                    setMessage(R.string.sure_delete_net)
                    setPositiveButton(R.string.yes) { dialog, _ ->
                        renderHelper?.removeObject(obj)
                        obj.getPins().forEach { pin ->
                            pin.removeFromNet()
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
            val endPoint = makePoint(x,y)
            if (renderHelper?.isTherePin(endPoint)!!) {
                val pin = circuit?.findConnectedPinByPoint(endPoint)
                if (pin != null) {
                    val net = circuit?.findNetByPoint(startPoint)!!
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
            startPoint = makePoint(x,y)
            if (renderHelper?.isThereNet(startPoint)!!) {
                drawTouch = true
                invalidate()
            } else if (renderHelper?.isTherePin(startPoint)!!) {
                val obj = circuit?.findConnectedPinByPoint(startPoint)
                if (obj != null) {
                    val net = obj.getNet()!!
                    val deleteDialog: AlertDialog = this.let {
                        val builder = AlertDialog.Builder(context)
                        builder.apply {
                            setTitle(formatResStr(R.string.delete_connection, obj, net))
                            setMessage(R.string.sure_delete_connection)
                            setPositiveButton(R.string.yes) { dialog, _ ->
                                obj.setIsConnected(false)
                                circuit?.listPins?.remove(obj)
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

    private fun makePoint(x:Float,y:Float): Point {
        return Point((x / renderHelper?.step!!).toInt(),
                (y / renderHelper?.step!!).toInt())
    }

    private fun createViewForAddNetDialogAndId(): Pair<View, Int> {
        val linearLayout = LinearLayout(context)
        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        val scale = resources.displayMetrics.density
        val dpAsPixels16 = (16 * scale + 0.5f).toInt()
        val dpAsPixels8 = (8 * scale + 0.5f).toInt()
        lp.setMargins(dpAsPixels16, dpAsPixels8, dpAsPixels16, dpAsPixels8)
        val input = EditText(context)
        input.id = View.generateViewId()
        input.layoutParams = lp
        input.hint = resources.getString(R.string.hint_add_net)
        linearLayout.addView(input, lp)
        return Pair(linearLayout, input.id)
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

    private fun onErrorToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    enum class EditEvent {
        VIEW,
        ADD_ELEMENT,
        ADD_NET,
        EDIT_CONNECTION,
        MOVE_ELEMENT,
        MOVE_NET,
        DELETE_ELEMENT,
        DELETE_NET,
        DELETE_CONNECTION
    }

    inner class ElementAdapter(private val mContext: Context, @LayoutRes list: Array<String>) : ArrayAdapter<String>(mContext, 0, list) {
        private var elementList = arrayOf("")

        init {
            elementList = list
        }

        override fun getView(position: Int, @Nullable convertView: View?, parent: ViewGroup): View {
            var listItem = convertView
            if (listItem == null)
                listItem = LayoutInflater.from(mContext).inflate(R.layout.layout_element, parent, false)

            val str = elementList[position]

            val element = Element(str)

            val elementView = listItem!!.findViewById(R.id.elementView) as ElementView
            val elementFullName = listItem.findViewById(R.id.element_full_name) as TextView
            val elementType = listItem.findViewById(R.id.element_type) as TextView
            val maxPins = listItem.findViewById(R.id.max_pins) as TextView

            elementView.setElement(element)
            elementFullName.text = formatResStr(R.string.element_name, "¯\\(°_o)/¯")
            elementType.text = formatResStr(R.string.element_name, str)
            maxPins.text = formatResStr(R.string.element_max_pins, element.getPinArraySize())

            return listItem
        }
    }
}