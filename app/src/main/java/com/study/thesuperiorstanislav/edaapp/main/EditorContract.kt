package com.study.thesuperiorstanislav.edaapp.main

import com.study.thesuperiorstanislav.edaapp.BasePresenter
import com.study.thesuperiorstanislav.edaapp.BaseView
import com.study.thesuperiorstanislav.edaapp.UseCase
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.DrawObject

interface EditorContract {

    interface View : BaseView<Presenter> {

        var isActive: Boolean

        fun showData(circuit: Circuit,circuitName: String,drawMatrix: Array<Array<DrawObject?>>)

        fun saveFile(circuit: Circuit)

        fun onError(error: UseCase.Error)

        fun onLoadingError(error: UseCase.Error)

    }

    interface Presenter : BasePresenter {

        fun getData()

        fun cacheCircuit(circuit: Circuit,circuitName: String)

        fun cacheDrawMatrix(drawMatrix: Array<Array<DrawObject?>>)

        fun saveFile()
    }
}