package com.study.thesuperiorstanislav.edaapp.routing

import com.study.thesuperiorstanislav.edaapp.usecase.UseCase
import com.study.thesuperiorstanislav.edaapp.usecase.UseCaseHandler
import com.study.thesuperiorstanislav.edaapp.editor.domain.usecase.GetData
import com.study.thesuperiorstanislav.edaapp.routing.domain.usecase.DoTheRouting
import com.study.thesuperiorstanislav.edaapp.usecase.UseCaseHandlerWithPostProgress
import com.study.thesuperiorstanislav.edaapp.usecase.UseCaseWithProgress

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

    override fun doRouting(isAStarAlgorithm: Boolean, isDiagonal: Boolean, isIntersectionAllowed: Boolean) {
        val requestValue = DoTheRouting.RequestValue(isAStarAlgorithm,isDiagonal,isIntersectionAllowed)
        UseCaseHandlerWithPostProgress.execute(doTheRouting, requestValue,
                object : UseCaseWithProgress.UseCaseCallback<DoTheRouting.ProgressValue,DoTheRouting.ResponseValue> {
                    override fun onSuccess(response: DoTheRouting.ResponseValue) {
                        // The editorView may not be able to handle UI updates anymore
                        if (!routingView.isActive) {
                            return
                        }

                        routingView.closeProgressDialog()
                        routingView.showData(response.circuit, response.circuitName, response.drawMatrix, response.linesList)
                    }

                    override fun onProgress(progress: DoTheRouting.ProgressValue) {
                        if (!routingView.isActive) {
                            return
                        }

                        val response = progress.responseValue
                        routingView.postRoutingProgress(progress.pinsCount,progress.doneCount)
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