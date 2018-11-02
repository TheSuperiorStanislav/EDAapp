package com.study.thesuperiorstanislav.edaapp.main


import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment

import com.study.thesuperiorstanislav.edaapp.R
import com.study.thesuperiorstanislav.edaapp.UseCase
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.utils.file.AllegroFile
import com.study.thesuperiorstanislav.edaapp.utils.file.Calay90File
import com.study.thesuperiorstanislav.edaapp.utils.graphics.RenderHelper
import kotlinx.android.synthetic.main.fragment_main.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader



@Suppress("PLUGIN_WARNING")
class MainFragment : Fragment(), MainContract.View {

    override var isActive: Boolean = false

    private var presenter: MainContract.Presenter? = null

    private val READ_REQUEST_CODE = 42

    private var dialog: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_load -> {
                performFileSearch()
                true
            }
            /*R.id.menu_history -> {
                true
            }
            R.id.menu_about -> {
                true
            }*/
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        dialog = ProgressDialog.show(context, getString(R.string.processing_data),
                getString(R.string.please_wait), true)
        presenter?.start()
    }

    override fun setPresenter(presenter: MainContract.Presenter) {
        this.presenter = presenter
    }

    override fun showData(circuit: Circuit) {
        (circuitView as CircuitView).setCircuit(circuit)
        dialog?.dismiss()
    }

    override fun onError(error: UseCase.Error) {
        dialog?.dismiss()
        val snackBar = Snackbar.make(main_layout, error.message!!, Snackbar.LENGTH_SHORT)
        snackBar.setAction("¯\\(°_o)/¯") { }
        snackBar.show()
    }

    override fun onLoadingError(error: UseCase.Error) {
        dialog?.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  resultData: Intent?) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uri: Uri?
            if (resultData != null) {
                uri = resultData.data
                readTextFromUri(uri)
                dialog?.dismiss()
            }
        }
    }

    @Throws(IOException::class)
    private fun readTextFromUri(uri: Uri) {
        val inputStream = activity?.contentResolver?.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val line = reader.readLine()
        if (line == "\$PACKAGES") {
            presenter?.cacheCircuit(AllegroFile.read(reader))
        } else {
            presenter?.cacheCircuit(Calay90File.read(line, reader))
        }
    }

    private fun performFileSearch() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, READ_REQUEST_CODE)
    }



    class CircuitView : View {
        constructor(context: Context) : super(context) {}
        constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


        private val rect = Rect()
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

        fun setCircuit(circuit: Circuit) {
            this.circuit = circuit
            invalidate()
        }
    }

}
