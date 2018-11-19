package com.study.thesuperiorstanislav.edaapp.editor.domain.usecase

import com.study.thesuperiorstanislav.edaapp.usecase.UseCase
import com.study.thesuperiorstanislav.edaapp.data.source.CircuitDataSource
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Circuit

class CacheDataFromFile (private val circuitRepository: CircuitDataSource): UseCase<CacheDataFromFile.RequestValues, CacheDataFromFile.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues?) {
        if (requestValues != null) {
            val circuit = requestValues.circuit
            val circuitName = requestValues.circuitName
            circuitRepository.cacheCircuit(circuit,circuitName, object : CircuitDataSource.CacheDataCallback {
                override fun onSaved() {
                    val responseValue = ResponseValue()
                    useCaseCallback?.onSuccess(responseValue)
                }

            })
        }
    }


    class RequestValues(val circuit: Circuit,val circuitName: String) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue
}