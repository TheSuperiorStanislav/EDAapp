package com.study.thesuperiorstanislav.edaapp.main


import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import com.study.thesuperiorstanislav.edaapp.R
import com.study.thesuperiorstanislav.edaapp.UseCase
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Element
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Net
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
        retainInstance = true
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

    override fun showData(matrixA: Array<Array<Int>>, matrixB: Array<Array<Int>>) {
        var textMatrix = ""

        matrixA.forEach { mutableList ->
            var text = ""
            mutableList.forEach {
                text += "$it "
            }
            textMatrix += "$text \n"

        }

        textMatrix += "\n"

        matrixB.forEach { mutableList ->
            var text = ""
            mutableList.forEach {
                text += "$it "
            }
            textMatrix += "$text \n"

        }

        test.text = textMatrix
        dialog?.dismiss()
    }

    override fun onError(error: UseCase.Error) {
        dialog?.dismiss()
        val snackBar = Snackbar.make(main_layout, error.message!!, Snackbar.LENGTH_SHORT)
        snackBar.setAction("¯\\(°_o)/¯") { _ -> }
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
        } else {
            readCalay90(line, reader)
        }
    }

    private fun readAllegro(reader: BufferedReader) {

        val listElements = mutableListOf<Element>()
        val listNets = mutableListOf<Net>()
        val listPins = mutableListOf<String>()

        var line = reader.readLine()
        while (line != "\$NETS") {
            line = reader.readLine()
        }
        line = reader.readLine()
        var lastNet = Net("")
        var isNetOver = true
        while (line != "\$END") {
            val splitLine = line
                    .split(" ")
                    .asSequence()
                    .filter { it != "" }
                    .toMutableList()

            if (isNetOver) {
                listNets.add(Net(splitLine[0]
                        .replace(";", "")))
                lastNet = listNets.last()
                splitLine.remove(listNets.last().toString() + ";")
            }

            isNetOver = line.last() != ','

            splitLine.forEach { str ->

                val splitIt = str
                        .replace(",", "")
                        .split(".")

                if (!listElements.contains(Element(splitIt.first()))) {
                    listElements.add(Element(splitIt.first()))
                }

                listElements.find { it == Element(splitIt.first()) }
                        ?.setPin(splitIt.last().toInt() - 1, str)
                lastNet.addPin(str)
                listPins.add(str)
            }
            line = reader.readLine()
        }

        presenter?.cacheCircuit(Circuit(listElements,listNets,listPins))

    }

    private fun readCalay90(firstLine: String?, reader: BufferedReader) {
        val listElements = mutableListOf<Element>()
        val listNets = mutableListOf<Net>()
        val listPins = mutableListOf<String>()

        var line = firstLine
        var lastNet = Net("")
        var isNetOver = true
        while (line != null) {
            val splitLine = line
                    .split(" ")
                    .asSequence()
                    .filter { it != "" }
                    .toMutableList()

            if (isNetOver) {
                listNets.add(Net(splitLine[0]))
                lastNet = listNets.last()
                splitLine.remove(listNets.last().toString())
            }

            isNetOver = line.last() == ';'

            splitLine.forEach { str ->

                val splitIt = str
                        .replace(",", "")
                        .replace(";", "")
                        .replace("(", "")
                        .replace(")", "")
                        .split("'")

                if (!listElements.contains(Element(splitIt.first()))) {
                    listElements.add(Element(splitIt.first()))
                }

                val strPin = str.replace(";","")

                listElements.find { it == Element(splitIt.first()) }
                        ?.setPin(splitIt.last().toInt() - 1, strPin)
                lastNet.addPin(strPin)
                listPins.add(strPin)
            }
            line = reader.readLine()
        }

        presenter?.cacheCircuit(Circuit(listElements,listNets,listPins))
    }



    private fun performFileSearch() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, READ_REQUEST_CODE)
    }


}
