package com.study.thesuperiorstanislav.edaapp.editor.domain.usecase

import com.study.thesuperiorstanislav.edaapp.usecase.UseCase
import com.study.thesuperiorstanislav.edaapp.data.source.CircuitDataSource
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject

class CacheDrawMatrix (private val circuitRepository: CircuitDataSource): UseCase<CacheDrawMatrix.RequestValues, CacheDrawMatrix.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues?) {
        if (requestValues != null) {
            val drawMatrix = requestValues.drawMatrix
            circuitRepository.cacheDrawMatrix(drawMatrix, object : CircuitDataSource.CacheDataCallback {
                override fun onSaved() {
                    val responseValue = ResponseValue()
                    useCaseCallback?.onSuccess(responseValue)
                }

            })
        }
    }


    class RequestValues(val drawMatrix: Array<Array<DrawObject?>>) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue
}