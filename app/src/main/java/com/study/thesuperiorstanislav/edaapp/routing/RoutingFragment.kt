package com.study.thesuperiorstanislav.edaapp.routing


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.*
import android.widget.Switch
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.study.thesuperiorstanislav.edaapp.BuildConfig

import com.study.thesuperiorstanislav.edaapp.R
import com.study.thesuperiorstanislav.edaapp.usecase.UseCase
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject
import com.study.thesuperiorstanislav.edaapp.utils.file.*
import com.study.thesuperiorstanislav.edaapp.utils.view.ViewHelper
import kotlinx.android.synthetic.main.dialog_routing_progress.*
import kotlinx.android.synthetic.main.fragment_routing.*
import java.io.File


class RoutingFragment : Fragment(), RoutingContract.View {

    override var isActive: Boolean = false
    private var presenter: RoutingContract.Presenter? = null

    private val readRequestCode = 42
    private val saveScreenShotCode = 43
    private val saveFileCode = 44
    private val takeScreenShotForShareCode = 45
    private val saveFileForShareCode = 46

    private var dialogProgress: Dialog? = null
    private var circuit: Circuit = Circuit(mutableListOf(), mutableListOf(), mutableListOf())
    private var circuitName = ""
    private var isAllegro = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        retainInstance = true
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
            R.id.menu_load ->
                performFileSearch()
            R.id.menu_routing ->
                showRoutingSettings()
            R.id.menu_screenshot ->
                takeScreenShot()
            R.id.menu_share ->
                showShareDialog()
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
            saveFileCode -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    Log.i("Permission_Storage", "Permission has been denied by user")
                else {
                    saveFile()
                }
            }
            takeScreenShotForShareCode -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    Log.i("Permission_Storage", "Permission has been denied by user")
                else {
                    shareScreenShot()
                }
            }
            saveFileForShareCode -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    Log.i("Permission_Storage", "Permission has been denied by user")
                else {
                    shareFile(circuit,circuitName)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  resultData: Intent?) {
        if (requestCode == readRequestCode && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                val uri: Uri? = resultData.data
                setFileName(uri!!)
                val inputStream = activity?.contentResolver?.openInputStream(uri)
                val circuitFromFile = CircuitFileReader.readTextFromUri(inputStream)
                if (circuitFromFile != null)
                    presenter?.cacheCircuit(circuitFromFile,circuitName)
                else
                    onError(UseCase.Error(UseCase.Error.LOAD_ERROR,ViewHelper.formatResStr(resources,R.string.load_file_error,circuitName)))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter?.start()

    }

    override fun onDetach() {
        super.onDetach()
        dialogProgress?.dismiss()
    }

    override fun setPresenter(presenter: RoutingContract.Presenter) {
        this.presenter = presenter
    }

    override fun showData(circuit: Circuit, circuitName: String, drawMatrix: Array<Array<DrawObject?>>, linesList: List<List<Point>>) {
        if (circuitView != null) {
            val drawMatrixToCache = circuitView?.setCircuit(circuit, circuitName, drawMatrix, linesList)!!
            if (!drawMatrixToCache.isEmpty() && drawMatrix.isEmpty() && linesList.isEmpty())
                presenter?.cacheDrawMatrix(drawMatrixToCache)
        }
        activity?.title = "${resources.getString(R.string.app_name)}/$circuitName"
        this.circuitName = circuitName
        this.circuit = circuit
    }

    override fun postRoutingProgress(pinsCount: Int, doneCount: Int,steps :Int) {
        if (dialogProgress == null)
            dialogProgress = createProgressDialog()
        dialogProgress?.show()
        dialogProgress?.steps_count?.text = ViewHelper.formatResStr(resources, R.string.steps_count_text, steps)
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

    private fun setFileName(uri: Uri) {
        if (uri.path != null) {
            val cursor: Cursor? = activity?.contentResolver?.query(uri, null, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    circuitName =
                            it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
    }

    private fun performFileSearch() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, readRequestCode)
    }

    private fun createProgressDialog():Dialog{
        val progressDialog = Dialog(context!!)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setContentView(R.layout.dialog_routing_progress)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        return progressDialog
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

    private fun shareScreenShot(){
        val exportPath = ScreenShotTaker.takeScreenShotForShare(this,circuitView,circuitName)
        if (exportPath != null) {
            val sendIntent = Intent()
            if (context != null) {
                val photoURI = FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID + ".provider", exportPath)
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                sendIntent.type = "*/*"
                sendIntent.putExtra(Intent.EXTRA_STREAM, photoURI)
                startActivity(sendIntent)
            }
        }
    }

    private fun saveFile() {
        try {
            val dirPath = CircuitFileSaver.saveFile(this,circuit,circuitName, isAllegro)
            if (dirPath != "")
                ViewHelper.showSnackBar(main_layout, ViewHelper.formatResStr(resources,
                        R.string.saved_in, dirPath))
        } catch (e: Exception) {
            onError(UseCase.Error(UseCase.Error.UNKNOWN_ERROR, e.localizedMessage))
        }
    }

    private fun shareFile(circuit: Circuit, circuitName: String) {
        share(CircuitFileSaver.saveFileForShare(this,circuit,circuitName,isAllegro))
    }

    private fun share(exportPath: File?){
        if (exportPath != null) {
            val sendIntent = Intent()
            if (context != null) {
                val photoURI = FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID + ".provider", exportPath)
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                sendIntent.type = "*/*"
                sendIntent.putExtra(Intent.EXTRA_STREAM, photoURI)
                startActivity(sendIntent)
            }
        }
    }

    private fun showShareDialog(){
        val pairForDialog = ViewHelper.createViewShare(context!!, resources)
        val view = pairForDialog.first
        val switchTypeId = pairForDialog.second.first
        val switchFileTypeId = pairForDialog.second.second

        val shareDialog: AlertDialog = this.let {
            val builder = AlertDialog.Builder(context!!)
            builder.apply {
                setTitle(R.string.share)
                setMessage(R.string.share_message)
                setView(view)
                setPositiveButton(R.string.share) { _, _ ->
                    val switchType = view.findViewById<Switch>(switchTypeId)!!
                    val switchFileType = view.findViewById<Switch>(switchFileTypeId)!!
                    if (switchType.isChecked){
                        isAllegro = switchFileType.isChecked
                        shareFile(circuit,circuitName)
                    }else{
                        shareScreenShot()
                    }
                }
                setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
            }
            builder.create()
        }
        shareDialog.show()
    }

}
