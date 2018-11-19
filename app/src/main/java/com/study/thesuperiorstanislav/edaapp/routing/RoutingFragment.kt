package com.study.thesuperiorstanislav.edaapp.routing


import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

import com.study.thesuperiorstanislav.edaapp.R
import com.study.thesuperiorstanislav.edaapp.usecase.UseCase
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject
import kotlinx.android.synthetic.main.dialog_routing_progress.*
import kotlinx.android.synthetic.main.fragment_routing.*

class RoutingFragment : Fragment(), RoutingContract.View {

    override var isActive: Boolean = false
    private var presenter: RoutingContract.Presenter? = null

    private val readRequestCode = 42
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
                presenter?.doRouting()
        }
        return super.onOptionsItemSelected(item)
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

}
