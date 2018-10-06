package com.study.thesuperiorstanislav.edaapp.main.domain.usecase

import com.study.thesuperiorstanislav.edaapp.UseCase
import com.study.thesuperiorstanislav.edaapp.data.source.CircuitDataSource
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit

class CacheDataFromFile (private val circuitRepository: CircuitDataSource): UseCase<CacheDataFromFile.RequestValues, CacheDataFromFile.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues?) {
        if (requestValues != null) {
            val pointList = requestValues.circuit
            circuitRepository.cacheCircuit(pointList, object : CircuitDataSource.CacheCircuitCallback {
                override fun onSaved() {
                    val responseValue = ResponseValue()
                    useCaseCallback?.onSuccess(responseValue)
                }

            })
        }
    }


    class RequestValues(val circuit: Circuit) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue
}