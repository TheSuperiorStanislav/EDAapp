package com.study.thesuperiorstanislav.edaapp.editor


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment

import com.study.thesuperiorstanislav.edaapp.R
import com.study.thesuperiorstanislav.edaapp.usecase.UseCase
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.utils.file.AllegroFile
import com.study.thesuperiorstanislav.edaapp.utils.file.Calay90File
import kotlinx.android.synthetic.main.fragment_editor.*
import android.util.Log
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AlertDialog
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject
import com.study.thesuperiorstanislav.edaapp.utils.file.ScreenShotTaker
import com.study.thesuperiorstanislav.edaapp.utils.view.ViewHelper
import java.io.*
import androidx.core.content.FileProvider
import com.study.thesuperiorstanislav.edaapp.BuildConfig
import com.study.thesuperiorstanislav.edaapp.utils.file.CircuitFileSaver


class EditorFragment : Fragment(), EditorContract.View {

    override var isActive: Boolean = false

    private var presenter: EditorContract.Presenter? = null

    private val readRequestCode = 42
    private val saveScreenShotCode = 43
    private val saveFileCode = 44
    private val takeScreenShotForShareCode = 45
    private val saveFileForShareCode = 46

    private var dialog: Dialog? = null
    private var circuit: Circuit = Circuit(mutableListOf(), mutableListOf(), mutableListOf())
    private var circuitName = ""
    private var isAllegro = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        dialog = createLoadingDialog()
    }

    override fun onCreateOptionsMenu(menu:Menu, inflater:MenuInflater) {
        inflater.inflate(R.menu.menu_editor, menu)
        super.onCreateOptionsMenu(menu,inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_load ->
                performFileSearch()
            R.id.menu_save ->
                showSaveFileDialog()
            R.id.menu_screenshot ->
                takeScreenShot()
            R.id.menu_share ->
                showShareDialog()
            R.id.add_element ->
                circuitView.changeEditEvent(CircuitView.EditEvent.ADD_ELEMENT)
            R.id.add_net ->
                circuitView.changeEditEvent(CircuitView.EditEvent.ADD_NET)
            R.id.edit_connection ->
                circuitView.changeEditEvent(CircuitView.EditEvent.EDIT_CONNECTION)
            R.id.move_object ->
                circuitView.changeEditEvent(CircuitView.EditEvent.MOVE_OBJECT)
            R.id.delete_object ->
                circuitView.changeEditEvent(CircuitView.EditEvent.DELETE_OBJECT)
            R.id.delete_connection ->
                circuitView.changeEditEvent(CircuitView.EditEvent.DELETE_CONNECTION)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        presenter?.start()
    }


    override fun setPresenter(presenter: EditorContract.Presenter) {
        this.presenter = presenter
    }

    override fun showData(circuit: Circuit, circuitName: String, drawMatrix: Array<Array<DrawObject?>>) {
        if (circuitView != null) {
            val drawMatrixToCache = circuitView?.setCircuit(circuit, drawMatrix, listOf())!!
            if (!drawMatrixToCache.isEmpty())
                presenter?.cacheDrawMatrix(circuitView?.setCircuit(circuit, drawMatrix, listOf())!!)
        }
        activity?.title = "${resources.getString(R.string.app_name)}/$circuitName"
        this.circuitName = circuitName
        this.circuit = circuit
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
        if (requestCode == readRequestCode && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                dialog?.show()
                val uri: Uri? = resultData.data
                setFileName(uri!!)
                readTextFromUri(uri)
                dialog?.dismiss()
            }
        }
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

    private fun setFileName(uri: Uri) {
        if (uri.path != null)
            circuitName = uri.path!!.split("/").last().split(".").first()
    }

    @Throws(IOException::class)
    private fun readTextFromUri(uri: Uri) {
        val inputStream = activity?.contentResolver?.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val line = reader.readLine()
        if (line == "\$PACKAGES") {
            presenter?.cacheCircuit(AllegroFile.read(reader),circuitName)
        } else {
            presenter?.cacheCircuit(Calay90File.read(line, reader),circuitName)
        }
    }

    private fun performFileSearch() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, readRequestCode)
    }

    private fun createLoadingDialog():Dialog{
        val loadingDialog = Dialog(context!!)
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDialog.setContentView(R.layout.dialog_loading)
        loadingDialog.setCancelable(false)
        loadingDialog.setCanceledOnTouchOutside(false)
        return loadingDialog
    }

    private fun showSaveFileDialog(){
        val pairForDialog = ViewHelper.createViewSaveFile(context!!, resources)

        val saveDialog: AlertDialog = this.let {
            val builder = AlertDialog.Builder(context!!)
            builder.apply {
                setTitle(R.string.save_file)
                setMessage(R.string.message_save_file)
                setView(pairForDialog.first)
                setPositiveButton(R.string.save) { _, _ -> }
                setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
            }
            builder.create()
        }

        saveDialog.setOnShowListener { dialogInterface ->
            val button = (dialogInterface as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val editText = saveDialog.findViewById<EditText>(pairForDialog.second.first)!!
                val switch = saveDialog.findViewById<Switch>(pairForDialog.second.second)!!
                if (!editText.text.isEmpty()) {
                    isAllegro = switch.isChecked
                    circuitName = editText.text.toString()
                    saveFile()
                    dialogInterface.dismiss()
                }
            }
        }
        saveDialog.show()
        val editText = saveDialog.findViewById<EditText>(pairForDialog.second.first)!!
        editText.setText(circuitName)
        val switch = saveDialog.findViewById<Switch>(pairForDialog.second.second)!!
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                switch.text = ViewHelper.formatResStr(resources,
                        R.string.save_type, resources.getString(R.string.allegro))
            else
                switch.text = ViewHelper.formatResStr(resources,
                        R.string.save_type, resources.getString(R.string.calay90))
        }
        switch.text = ViewHelper.formatResStr(resources,
                R.string.save_type, resources.getString(R.string.calay90))
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

    private fun share(exportPath:File?){
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
                        saveFile()
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
