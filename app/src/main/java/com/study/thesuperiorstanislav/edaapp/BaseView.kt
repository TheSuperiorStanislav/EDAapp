package com.study.thesuperiorstanislav.edaapp

interface BaseView<T : BasePresenter> {

    fun setPresenter(presenter: T)

}
