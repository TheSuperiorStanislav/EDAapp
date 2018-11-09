package com.study.thesuperiorstanislav.edaapp.main


import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class MainFragment : Fragment(), MainContract.View {

    override var isActive: Boolean = false

    private var presenter: MainContract.Presenter? = null

    private val READ_REQUEST_CODE = 42
    private val PERMISSION_GRANTED = 43

    private var dialog: ProgressDialog? = null
    private var circuitName = ""

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
            R.id.menu_save -> {
                presenter?.getData(false)
                true
            }
            R.id.menu_screenshot -> {
                takeScreenShot()
                true
            }
            R.id.add_element -> {
                circuitView.changeEditEvent(CircuitView.EditEvent.ADD_ELEMENT)
                true
            }
            R.id.add_net-> {
                circuitView.changeEditEvent(CircuitView.EditEvent.ADD_NET)
                true
            }
            R.id.edit_connection -> {
                circuitView.changeEditEvent(CircuitView.EditEvent.EDIT_CONNECTION)
                true
            }
            R.id.move_element -> {
                circuitView.changeEditEvent(CircuitView.EditEvent.MOVE_ELEMENT)
                true
            }
            R.id.move_net -> {
                circuitView.changeEditEvent(CircuitView.EditEvent.MOVE_NET)
                true
            }
            R.id.delete_element -> {
                circuitView.changeEditEvent(CircuitView.EditEvent.DELETE_ELEMENT)
                true
            }
            R.id.delete_net -> {
                circuitView.changeEditEvent(CircuitView.EditEvent.DELETE_NET)
                true
            }
            R.id.delete_connection -> {
                circuitView.changeEditEvent(CircuitView.EditEvent.DELETE_CONNECTION)
                true
            }
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

    override fun showData(circuit: Circuit, circuitName: String) {
        circuitView.setCircuit(circuit)
        this.circuitName = circuitName
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
                setFileName(uri)
                readTextFromUri(uri)
                dialog?.dismiss()
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                             permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_GRANTED -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("", "Permission has been denied by user")
                } else {
                    Log.i("", "Permission has been granted by user")
                }
            }
        }
    }

    private fun setFileName(uri: Uri) {
        val cursor: Cursor? = activity?.contentResolver?.query( uri, null, null, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                circuitName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                Log.i("File", "Display Name: $circuitName")
            }
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

    override fun saveFile(circuit: Circuit) {
        if (verifyStoragePermissions()) {
            val dirPath = Environment.getExternalStorageDirectory().absolutePath + "/EDA/Circuits"
            val dir = File(dirPath)
            if (!dir.exists())
                dir.mkdirs()
            val file = File(dirPath, circuitName)
            if (!file.exists())
                file.createNewFile()
            val stream = FileOutputStream(file)
            try {
                stream.write(Calay90File.write(circuit).toByteArray())
            } catch (e: Exception) {
                e.printStackTrace()
                onError(UseCase.Error(UseCase.Error.UNKNOWN_ERROR, e.localizedMessage))
            } finally {
                stream.close()
            }
        }
    }

    private fun takeScreenShot() {
        val millis = Date().time
        val timeStamp = SimpleDateFormat
                .getDateTimeInstance()
                .format(millis)
        if (verifyStoragePermissions())
            saveScreenShot(getScreenShot(circuitView), "$circuitName-$timeStamp.png")
    }

    private fun verifyStoragePermissions(): Boolean {
        return if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),PERMISSION_GRANTED)
            false
        } else {
            true
        }
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
        val dirPath = Environment.getExternalStorageDirectory().absolutePath + "/EDA/ScreenShots"
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
