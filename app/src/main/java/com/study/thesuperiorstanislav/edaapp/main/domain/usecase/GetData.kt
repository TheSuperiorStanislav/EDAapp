package com.study.thesuperiorstanislav.edaapp.main.domain.usecase

import com.study.thesuperiorstanislav.edaapp.UseCase
import com.study.thesuperiorstanislav.edaapp.UseCaseHandler
import com.study.thesuperiorstanislav.edaapp.data.source.CircuitDataSource
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit

class GetData (private val circuitRepository: CircuitDataSource,
               private val createMatrix: CreateMatrix): UseCase<GetData.RequestValues, GetData.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues?) {
        if (requestValues != null) {
            circuitRepository.getCircuit(object : CircuitDataSource.LoadCircuitCallback {
                override fun onCircuitLoaded(circuit: Circuit) {

                    val requestValue = CreateMatrix.RequestValues(circuit)
                    UseCaseHandler.execute(createMatrix, requestValue,
                            object : UseCase.UseCaseCallback<CreateMatrix.ResponseValue> {
                                override fun onSuccess(response: CreateMatrix.ResponseValue) {
                                    val responseValue = ResponseValue(response.matrixA,response.matrixB)
                                    useCaseCallback?.onSuccess(responseValue)
                                }

                                override fun onError(error: UseCase.Error) {
                                    useCaseCallback?.onError(error)
                                }
                            })
                }


                override fun onDataNotAvailable(error: Error) {
                    useCaseCallback?.onError(error)
                }
            })
        }
    }


    class RequestValues : UseCase.RequestValues

    class ResponseValue(val matrixA: Array<Array<Int>>,val matrixB: Array<Array<Int>>) : UseCase.ResponseValue
}