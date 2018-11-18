package com.study.thesuperiorstanislav.edaapp.routing

import com.study.thesuperiorstanislav.edaapp.BasePresenter
import com.study.thesuperiorstanislav.edaapp.BaseView
import com.study.thesuperiorstanislav.edaapp.UseCase
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject

interface RoutingContract {

    interface View : BaseView<Presenter> {

        var isActive: Boolean

        fun showData(circuit: Circuit, circuitName: String, drawMatrix: Array<Array<DrawObject?>>, linesList: List<List<Point>>)

        fun onError(error: UseCase.Error)

        fun onLoadingError(error: UseCase.Error)

    }

    interface Presenter : BasePresenter {

        fun getData()

        fun doRouting()
    }
}