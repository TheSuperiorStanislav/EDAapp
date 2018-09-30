package com.study.thesuperiorstanislav.edaapp.main


import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import com.study.thesuperiorstanislav.edaapp.R
import com.study.thesuperiorstanislav.edaapp.UseCase
import kotlinx.android.synthetic.main.fragment_main.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class MainFragment : Fragment(), MainContract.View {

    override var isActive: Boolean = false

    private var presenter: MainContract.Presenter? = null

    private val READ_REQUEST_CODE = 42

    private var dialog: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        retainInstance = true;
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
        dialog?.dismiss()
    }

    override fun setPresenter(presenter: MainContract.Presenter) {
        this.presenter = presenter
    }

    override fun onError(error: UseCase.Error) {
        dialog?.dismiss()
        val snackBar = Snackbar.make(main_layout, error.message!!, Snackbar.LENGTH_SHORT)
        snackBar.setAction("¯\\(°_o)/¯") { _ ->  }
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
            readAllegro(reader)
        }
    }

    private fun readAllegro(reader :BufferedReader) {
        val listElements = mutableListOf<String>()
        val listNets = mutableListOf<String>()
        val listConnectors = mutableListOf<String>()
        val listConnectorsElements = mutableListOf<MutableList<String>>()
        val listConnectorsNets = mutableListOf<MutableList<String>>()

        var line = reader.readLine()
        while (line != "\$NETS") {
            line = reader.readLine()
        }
        line = reader.readLine()
        var lastNet = ""
        var isNetOver = true
        while (line != "\$END") {
            val splitLine = line
                    .split(" ")
                    .asSequence()
                    .filter { it != "" }
                    .toMutableList()

            if (isNetOver) {
                listNets.add(splitLine[0]
                        .replace(";", ""))
                listConnectorsNets.add(mutableListOf())
                lastNet = listNets.last()
                splitLine.remove(listNets.last() + ";")
            }

            isNetOver = line.last() != ','

            splitLine.forEach {

                val splitIt = it
                        .replace(",", "")
                        .split(".")

                if (!listElements.contains(splitIt[0])) {
                    listElements.add(splitIt[0])
                    listConnectorsElements.add(mutableListOf())
                }

                listConnectorsElements[listElements.indexOf(splitIt[0])].add(it)
                listConnectorsNets[listNets.indexOf(lastNet)].add(it)
                listConnectors.add(it)
            }
            line = reader.readLine()
        }
//        listElements.forEach {
//            Log.i("Elements", it)
//        }
//        listNets.forEach {
//            Log.i("Nets", it)
//        }
//        listConnectorsElements.forEach { mutableList ->
//            var text = ""
//            mutableList.forEach {
//                text += "$it "
//            }
//            Log.i("ElementsConnectors", text)
//        }
//        listConnectorsNets.forEach { mutableList ->
//            var text = ""
//            mutableList.forEach {
//                text += "$it "
//            }
//            Log.i("NetsConnectors", text)
//        }

        val matrixA = Array(listConnectors.size) { connectorIndex ->
            Array(listNets.size) { netIndex ->
                if (listConnectorsNets[netIndex].contains(listConnectors[connectorIndex]))
                    1
                else
                    0
            }
        }

        val matrixB = Array(listConnectors.size) { connectorIndex ->
            Array(listElements.size) { elementsIndex ->
                if (listConnectorsElements[elementsIndex].contains(listConnectors[connectorIndex]))
                    1
                else
                    0
            }
        }

//        var textMatrixA = "\n"
//
//        matrixA.forEach { mutableList ->
//            var text = ""
//            mutableList.forEach {
//                text += "$it "
//            }
//            textMatrixA += "$text \n"
//
//        }
//
//        Log.i("matrix A", textMatrixA)
//
//        var textMatrixB = "\n"
//
//        matrixB.forEach { mutableList ->
//            var text = ""
//            mutableList.forEach {
//                text += "$it "
//            }
//            textMatrixB += "$text \n"
//
//        }
//
//        Log.i("matrix B", textMatrixB)

    }

    private fun performFileSearch() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, READ_REQUEST_CODE)
    }


}
