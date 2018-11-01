package com.study.thesuperiorstanislav.edaapp.main

import com.study.thesuperiorstanislav.edaapp.UseCase
import com.study.thesuperiorstanislav.edaapp.UseCaseHandler
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.main.domain.usecase.CacheDataFromFile
import com.study.thesuperiorstanislav.edaapp.main.domain.usecase.GetData

class MainPresenter(private val mainView: MainContract.View,
                    private val getData: GetData,
                    private val cacheDataFromFile: CacheDataFromFile): MainContract.Presenter {


    override fun start() {
        mainView.isActive = true
        getData()
    }

    override fun getData() {
        val requestValue = GetData.RequestValues()
        UseCaseHandler.execute(getData, requestValue,
                object : UseCase.UseCaseCallback<GetData.ResponseValue> {
                    override fun onSuccess(response: GetData.ResponseValue) {
                        // The mainView may not be able to handle UI updates anymore
                        if (!mainView.isActive) {
                            return
                        }

                        mainView.showData(response.circuit)
                    }

                    override fun onError(error: UseCase.Error) {
                        // The mainView may not be able to handle UI updates anymore
                        if (!mainView.isActive) {
                            return
                        }

                        mainView.onLoadingError(error)
                    }
                })
    }

    override fun cacheCircuit(circuit: Circuit) {

        val requestValue = CacheDataFromFile.RequestValues(circuit)
        UseCaseHandler.execute(cacheDataFromFile, requestValue,
                object : UseCase.UseCaseCallback<CacheDataFromFile.ResponseValue> {
                    override fun onSuccess(response: CacheDataFromFile.ResponseValue) {
                        // The mainView may not be able to handle UI updates anymore
                        if (!mainView.isActive) {
                            return
                        }
                    }

                    override fun onError(error: UseCase.Error) {
                        // The mainView may not be able to handle UI updates anymore
                        if (!mainView.isActive) {
                            return
                        }

                        mainView.onError(error)
                    }
                })
    }
}