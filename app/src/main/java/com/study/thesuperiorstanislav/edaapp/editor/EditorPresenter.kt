package com.study.thesuperiorstanislav.edaapp.editor

import com.study.thesuperiorstanislav.edaapp.usecase.UseCase
import com.study.thesuperiorstanislav.edaapp.usecase.UseCaseHandler
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject
import com.study.thesuperiorstanislav.edaapp.editor.domain.usecase.CacheDataFromFile
import com.study.thesuperiorstanislav.edaapp.editor.domain.usecase.CacheDrawMatrix
import com.study.thesuperiorstanislav.edaapp.editor.domain.usecase.GetData

class EditorPresenter(private val editorView: EditorContract.View,
                      private val getData: GetData,
                      private val cacheDataFromFile: CacheDataFromFile,
                      private val cacheDrawMatrix: CacheDrawMatrix): EditorContract.Presenter {

    override fun start() {
        editorView.isActive = true
        getData()
    }

    override fun getData() {
        val requestValue = GetData.RequestValues()
        UseCaseHandler.execute(getData, requestValue,
                object : UseCase.UseCaseCallback<GetData.ResponseValue> {
                    override fun onSuccess(response: GetData.ResponseValue) {
                        // The editorView may not be able to handle UI updates anymore
                        if (!editorView.isActive) {
                            return
                        }

                        editorView.showData(response.circuit, response.circuitName, response.drawMatrix)
                    }

                    override fun onError(error: UseCase.Error) {
                        // The editorView may not be able to handle UI updates anymore
                        if (!editorView.isActive) {
                            return
                        }

                        editorView.onLoadingError(error)
                    }
                })
    }

    override fun cacheCircuit(circuit: Circuit,circuitName: String) {

        val requestValue = CacheDataFromFile.RequestValues(circuit,circuitName)
        UseCaseHandler.execute(cacheDataFromFile, requestValue,
                object : UseCase.UseCaseCallback<CacheDataFromFile.ResponseValue> {
                    override fun onSuccess(response: CacheDataFromFile.ResponseValue) {
                        // The editorView may not be able to handle UI updates anymore
                        if (!editorView.isActive) {
                            return
                        }
                    }

                    override fun onError(error: UseCase.Error) {
                        // The editorView may not be able to handle UI updates anymore
                        if (!editorView.isActive) {
                            return
                        }

                        editorView.onError(error)
                    }
                })
    }

    override fun cacheDrawMatrix(drawMatrix: Array<Array<DrawObject?>>) {
        val requestValue = CacheDrawMatrix.RequestValues(drawMatrix)
        UseCaseHandler.execute(cacheDrawMatrix, requestValue,
                object : UseCase.UseCaseCallback<CacheDrawMatrix.ResponseValue> {
                    override fun onSuccess(response: CacheDrawMatrix.ResponseValue) {
                        // The editorView may not be able to handle UI updates anymore
                        if (!editorView.isActive) {
                            return
                        }
                    }

                    override fun onError(error: UseCase.Error) {
                        // The editorView may not be able to handle UI updates anymore
                        if (!editorView.isActive) {
                            return
                        }

                        editorView.onError(error)
                    }
                })
    }
}