package com.study.thesuperiorstanislav.edaapp.routing


import android.app.Dialog
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Switch
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

import com.study.thesuperiorstanislav.edaapp.R
import com.study.thesuperiorstanislav.edaapp.usecase.UseCase
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject
import com.study.thesuperiorstanislav.edaapp.utils.file.ScreenShotTaker
import com.study.thesuperiorstanislav.edaapp.utils.view.ViewHelper
import kotlinx.android.synthetic.main.dialog_routing_progress.*
import kotlinx.android.synthetic.main.fragment_routing.*
import android.view.Display



class RoutingFragment : Fragment(), RoutingContract.View {

    override var isActive: Boolean = false
    private var presenter: RoutingContract.Presenter? = null

    private val saveScreenShotCode = 43

    private var dialogProgress: Dialog? = null
    private var circuitName = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_routing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater:MenuInflater) {
        inflater.inflate(R.menu.menu_routing, menu)
        super.onCreateOptionsMenu(menu,inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_routing ->
                showRoutingSettings()
            R.id.menu_screenshot ->
                takeScreenShot()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            saveScreenShotCode -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    Log.i("Permission_Storage", "Permission has been denied by user")
                else {
                    takeScreenShot()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter?.start()

    }

    override fun setPresenter(presenter: RoutingContract.Presenter) {
        this.presenter = presenter
    }

    override fun showData(circuit: Circuit, circuitName: String, drawMatrix: Array<Array<DrawObject?>>, linesList: List<List<Point>>) {
        circuitView?.setCircuit(circuit, drawMatrix, linesList)
        activity?.title = "${resources.getString(R.string.app_name)}/$circuitName"
        this.circuitName = circuitName
    }

    override fun postRoutingProgress(pinsCount: Int, doneCount: Int) {
        if (dialogProgress == null)
            dialogProgress = createProgressDialog()
        dialogProgress?.show()
        dialogProgress?.progressBar?.max = pinsCount
        dialogProgress?.progressBar?.progress = doneCount
    }

    override fun closeProgressDialog(){
        dialogProgress?.dismiss()
    }

    override fun onError(error: UseCase.Error) {
        dialogProgress?.dismiss()
        val snackBar = Snackbar.make(main_layout, error.message!!, Snackbar.LENGTH_SHORT)
        snackBar.setAction("¯\\(°_o)/¯") { }
        snackBar.show()
    }

    override fun onLoadingError(error: UseCase.Error) {
        dialogProgress?.dismiss()
    }

    private fun createProgressDialog():Dialog{
        val progressDialog = Dialog(context!!)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setContentView(R.layout.dialog_routing_progress)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        return progressDialog
    }

    private fun takeScreenShot() {
        try {
            val dirPath = ScreenShotTaker.takeScreenShot(this, circuitView, circuitName)
            if (dirPath != "")
                ViewHelper.showSnackBar(main_layout, ViewHelper.formatResStr(resources,
                        R.string.saved_in, dirPath))
        } catch (e: Exception) {
            onError(UseCase.Error(UseCase.Error.UNKNOWN_ERROR, e.localizedMessage))
        }
    }

    private fun showRoutingSettings(){
        val pairForDialog = ViewHelper.createViewRoutingSettings(context!!, resources)
        val view = pairForDialog.first

        val routingDialog: AlertDialog = this.let {
            val builder = AlertDialog.Builder(context!!)
            builder.apply {
                setTitle(R.string.start_routing)
                setMessage(R.string.routing_settings)
                setView(view)
                setPositiveButton(R.string.start) { _, _ ->
                    val switchAlgorithm = view.findViewById<Switch>(pairForDialog.second[0])!!.isChecked
                    val switchDirection = view.findViewById<Switch>(pairForDialog.second[1])!!.isChecked
                    val switchIntersection = view.findViewById<Switch>(pairForDialog.second[2])!!.isChecked
                    presenter?.doRouting(switchAlgorithm,switchDirection,switchIntersection)
                }
                setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
            }
            builder.create()
        }

        routingDialog.show()
    }

}
