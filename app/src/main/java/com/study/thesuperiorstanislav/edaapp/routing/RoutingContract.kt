package com.study.thesuperiorstanislav.edaapp.routing

import com.study.thesuperiorstanislav.edaapp.BasePresenter
import com.study.thesuperiorstanislav.edaapp.BaseView
import com.study.thesuperiorstanislav.edaapp.usecase.UseCase
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject

interface RoutingContract {

    interface View : BaseView<Presenter> {

        var isActive: Boolean

        fun showData(circuit: Circuit, circuitName: String, drawMatrix: Array<Array<DrawObject?>>, linesList: List<List<Point>>)

        fun postRoutingProgress(pinsCount:Int, doneCount:Int, steps :Int)

        fun onError(error: UseCase.Error)

        fun onLoadingError(error: UseCase.Error)

        fun closeProgressDialog()
    }

    interface Presenter : BasePresenter {

        fun getData()

        fun cacheCircuit(circuit: Circuit,circuitName: String)

        fun cacheDrawMatrix(drawMatrix: Array<Array<DrawObject?>>)

        fun doRouting(isAStarAlgorithm:Boolean,isDiagonal:Boolean,isIntersectionAllowed:Boolean)
    }
}