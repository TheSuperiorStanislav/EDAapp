package com.study.thesuperiorstanislav.edaapp.main


import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.*
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment

import com.study.thesuperiorstanislav.edaapp.R
import com.study.thesuperiorstanislav.edaapp.UseCase
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.utils.file.AllegroFile
import com.study.thesuperiorstanislav.edaapp.utils.file.Calay90File
import kotlinx.android.synthetic.main.fragment_main.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.widget.EditText
import android.widget.Switch
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.study.thesuperiorstanislav.edaapp.utils.view.ViewHelper
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import org.jetbrains.anko.uiThread
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class MainFragment : Fragment(), MainContract.View {

    override var isActive: Boolean = false

    private var presenter: MainContract.Presenter? = null

    private val READ_REQUEST_CODE = 42
    private val SAVE_SCREENSHOT_CODE = 43
    private val SAVE_FILE_CODE = 44

    private var dialog: Dialog? = null
    private var circuitName = ""
    private var isAllegro = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        dialog = createLoadingDialog()
    }

    override fun onCreateOptionsMenu(menu:Menu, inflater:MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
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

    override fun setPresenter(presenter: MainContract.Presenter) {
        this.presenter = presenter
    }

    override fun showData(circuit: Circuit, circuitName: String) {
        circuitView.setCircuit(circuit)
        this.circuitName = circuitName
    }

    override fun saveFile(circuit: Circuit) {
        if (verifyStoragePermissions(SAVE_FILE_CODE)) {
            val dirPath = "${Environment.getExternalStorageDirectory().absolutePath}/EDA/Circuits"
            val dir = File(dirPath)
            if (!dir.exists())
                dir.mkdirs()
            val file = File(dirPath, "$circuitName.net")
            if (!file.exists())
                file.createNewFile()
            val stream = FileOutputStream(file)
            try {
                if (isAllegro)
                    stream.write(AllegroFile.write(circuit).toByteArray())
                else
                    stream.write(Calay90File.write(circuit).toByteArray())
            } catch (e: Exception) {
                e.printStackTrace()
                onError(UseCase.Error(UseCase.Error.UNKNOWN_ERROR, e.localizedMessage))
            } finally {
                stream.close()
            }
        }
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
            SAVE_SCREENSHOT_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    Log.i("Permission_Storage", "Permission has been denied by user")
                else {
                    takeScreenShot()
                }
            }
            SAVE_FILE_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    Log.i("Permission_Storage", "Permission has been denied by user")
                else {
                    presenter?.saveFile()
                }
            }
        }
    }

    private fun setFileName(uri: Uri) {
        val cursor: Cursor? = activity?.contentResolver?.query(uri,
                null,
                null,
                null,
                null,
                null)

        cursor?.use {
            if (it.moveToFirst())
                circuitName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME)).split(".").first()
        }
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
        startActivityForResult(intent, READ_REQUEST_CODE)
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
        val pairForDialog = ViewHelper.createViewWithEditTextAndSwitch(context!!, resources)

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
                    presenter?.saveFile()
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
        val millis = Date().time
        val timeStamp = SimpleDateFormat
                .getDateTimeInstance()
                .format(millis)
        if (verifyStoragePermissions(SAVE_SCREENSHOT_CODE))
            saveScreenShot(getScreenShot(circuitView), "$circuitName-$timeStamp.png")
    }

    private fun verifyStoragePermissions(requestCode: Int): Boolean {
        return if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), requestCode)
            false
        } else
            true
    }

    private fun getScreenShot(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }

    private fun saveScreenShot(bm: Bitmap, fileName: String) {
        val dirPath = "${Environment.getExternalStorageDirectory().absolutePath}/EDA/ScreenShots"
        val dir = File(dirPath)
        if (!dir.exists())
            dir.mkdirs()
        val file = File(dirPath, fileName)
        try {
            val fOut = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
            onError(UseCase.Error(UseCase.Error.UNKNOWN_ERROR,e.localizedMessage))
        }

    }

}
