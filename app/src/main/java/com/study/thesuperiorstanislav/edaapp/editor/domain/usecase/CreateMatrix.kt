package com.study.thesuperiorstanislav.edaapp.editor.domain.usecase

import com.study.thesuperiorstanislav.edaapp.UseCase
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.utils.math.MatrixUtils

class CreateMatrix: UseCase<CreateMatrix.RequestValues, CreateMatrix.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues?) {
        if (requestValues != null) {
            val circuit = requestValues.circuit
            val matrixPair = createMatrixAB(circuit)
            val matrixQ = MatrixUtils.createMatrixQ(matrixPair.first, matrixPair.second)
            val matrixR = MatrixUtils.createMatrixR(matrixQ)
            val responseValue = ResponseValue(MatrixUtils.transpose(matrixPair.first), matrixPair.second, matrixQ, matrixR)
            useCaseCallback?.onSuccess(responseValue)
        }
    }

    private fun createMatrixAB(circuit: Circuit): Pair<Array<Array<Int>>, Array<Array<Int>>> {
        val matrixA = Array(circuit.listNets.size) { netIndex ->
            Array(circuit.listPins.size) { connectorIndex ->
                if (circuit.listNets[netIndex].getPins().contains(circuit.listPins[connectorIndex]))
                    1
                else
                    0
            }
        }

        val matrixB = Array(circuit.listElements.size) { elementsIndex ->
            Array(circuit.listPins.size) { connectorIndex ->
                if (circuit.listElements[elementsIndex].getPins().contains(circuit.listPins[connectorIndex]))
                    1
                else
                    0
            }
        }

        return Pair(matrixA, matrixB)
    }

    class RequestValues(val circuit: Circuit) : UseCase.RequestValues

    class ResponseValue(val matrixA: Array<Array<Int>>, val matrixB: Array<Array<Int>>,
                        val matrixQ: Array<Array<Int>>, val matrixR: Array<Array<Int>>) : UseCase.ResponseValue
}