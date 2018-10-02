package com.study.thesuperiorstanislav.edaapp.main.domain.usecase

import com.study.thesuperiorstanislav.edaapp.UseCase
import com.study.thesuperiorstanislav.edaapp.data.source.CircuitDataSource
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit

class GetCircuit (private val circuitRepository: CircuitDataSource): UseCase<GetCircuit.RequestValues, GetCircuit.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues?) {
        if (requestValues != null) {
            circuitRepository.getCircuit(object : CircuitDataSource.LoadCircuitCallback {
                override fun onCircuitLoaded(circuit: Circuit) {
                    val responseValue = ResponseValue(circuit)
                    useCaseCallback?.onSuccess(responseValue)
                }


                override fun onDataNotAvailable(error: Error) {
                    useCaseCallback?.onError(error)
                }
            })
        }
    }


    class RequestValues : UseCase.RequestValues

    class ResponseValue(val circuit: Circuit) : UseCase.ResponseValue
}