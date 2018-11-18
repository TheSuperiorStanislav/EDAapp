package com.study.thesuperiorstanislav.edaapp.routing

import com.study.thesuperiorstanislav.edaapp.UseCase
import com.study.thesuperiorstanislav.edaapp.UseCaseHandler
import com.study.thesuperiorstanislav.edaapp.editor.domain.usecase.GetData
import com.study.thesuperiorstanislav.edaapp.routing.domain.usecase.DoTheRouting

class RoutingPresenter(private val routingView: RoutingContract.View,
                       private val getData: GetData,
                       private val doTheRouting: DoTheRouting): RoutingContract.Presenter {

    override fun start() {
        routingView.isActive = true
        getData()
    }

    override fun getData() {
        val requestValue = GetData.RequestValues()
        UseCaseHandler.execute(getData, requestValue,
                object : UseCase.UseCaseCallback<GetData.ResponseValue> {
                    override fun onSuccess(response: GetData.ResponseValue) {
                        // The editorView may not be able to handle UI updates anymore
                        if (!routingView.isActive) {
                            return
                        }

                        routingView.showData(response.circuit, response.circuitName, response.drawMatrix, response.linesList)
                    }

                    override fun onError(error: UseCase.Error) {
                        // The editorView may not be able to handle UI updates anymore
                        if (!routingView.isActive) {
                            return
                        }

                        routingView.onLoadingError(error)
                    }
                })
    }

    override fun doRouting() {
        val requestValue = DoTheRouting.RequestValues()
        UseCaseHandler.execute(doTheRouting, requestValue,
                object : UseCase.UseCaseCallback<DoTheRouting.ResponseValue> {
                    override fun onSuccess(response: DoTheRouting.ResponseValue) {
                        // The editorView may not be able to handle UI updates anymore
                        if (!routingView.isActive) {
                            return
                        }

                        routingView.showData(response.circuit, response.circuitName, response.drawMatrix, response.linesList)
                    }

                    override fun onError(error: UseCase.Error) {
                        // The editorView may not be able to handle UI updates anymore
                        if (!routingView.isActive) {
                            return
                        }

                        routingView.onLoadingError(error)
                    }
                })
    }
}