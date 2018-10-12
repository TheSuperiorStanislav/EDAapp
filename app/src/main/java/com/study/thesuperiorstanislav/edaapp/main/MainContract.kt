package com.study.thesuperiorstanislav.edaapp.main

import com.study.thesuperiorstanislav.decisiontheorylab1.BasePresenter
import com.study.thesuperiorstanislav.decisiontheorylab1.BaseView
import com.study.thesuperiorstanislav.edaapp.UseCase
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit

interface MainContract {

    interface View : BaseView<Presenter> {

        var isActive: Boolean

        fun showData(matrixA: Array<Array<Int>>,matrixB: Array<Array<Int>>,
                     matrixQ: Array<Array<Int>>, matrixR: Array<Array<Int>>)

        fun onError(error: UseCase.Error)

        fun onLoadingError(error: UseCase.Error)

    }

    interface Presenter : BasePresenter {

        fun getData()

        fun cacheCircuit(circuit: Circuit)

    }
}