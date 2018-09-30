package com.study.thesuperiorstanislav.decisiontheorylab1

interface BaseView<T : BasePresenter> {

    fun setPresenter(presenter: T)

}
