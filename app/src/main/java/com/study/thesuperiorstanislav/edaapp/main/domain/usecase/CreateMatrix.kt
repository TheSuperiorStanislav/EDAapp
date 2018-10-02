package com.study.thesuperiorstanislav.edaapp.main.domain.usecase

import com.study.thesuperiorstanislav.edaapp.UseCase
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit

class CreateMatrix: UseCase<CreateMatrix.RequestValues, CreateMatrix.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues?) {
        if (requestValues != null) {
            val circuit = requestValues.circuit
            val matrixPair = createMatrixAB(circuit)
            val responseValue = ResponseValue(matrixPair.first,matrixPair.second)
            useCaseCallback?.onSuccess(responseValue)
        }
    }

    private fun createMatrixAB(circuit: Circuit): Pair<Array<Array<Int>>,Array<Array<Int>>> {
        val matrixA = Array(circuit.listPins.size) { connectorIndex ->
            Array(circuit.listNets.size) { netIndex ->
                if (circuit.listNets[netIndex].getPins().contains(circuit.listPins[connectorIndex]))
                    1
                else
                    0
            }
        }

        val matrixB = Array(circuit.listPins.size) { connectorIndex ->
            Array(circuit.listElements.size) { elementsIndex ->
                if (circuit.listElements[elementsIndex].getPins().contains(circuit.listPins[connectorIndex]))
                    1
                else
                    0
            }
        }

        return Pair(matrixA, matrixB)
    }

    class RequestValues(val circuit: Circuit) : UseCase.RequestValues

    class ResponseValue(val matrixA: Array<Array<Int>>,val matrixB: Array<Array<Int>>) : UseCase.ResponseValue
}