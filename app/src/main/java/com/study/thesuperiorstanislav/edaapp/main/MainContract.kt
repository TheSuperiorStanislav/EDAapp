package com.study.thesuperiorstanislav.edaapp.main

import com.study.thesuperiorstanislav.decisiontheorylab1.BasePresenter
import com.study.thesuperiorstanislav.decisiontheorylab1.BaseView
import com.study.thesuperiorstanislav.edaapp.UseCase

interface MainContract {

    interface View : BaseView<Presenter> {

        var isActive: Boolean

        fun onError(error: UseCase.Error)

        fun onLoadingError(error: UseCase.Error)

    }

    interface Presenter : BasePresenter {

    }
}