package com.study.thesuperiorstanislav.edaapp.main

class MainPresenter(private val mainView: MainContract.View): MainContract.Presenter {

    override fun start() {
        mainView.isActive = true
    }
}