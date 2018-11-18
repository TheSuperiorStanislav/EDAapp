package com.study.thesuperiorstanislav.edaapp.routing.domain.usecase

import com.study.thesuperiorstanislav.edaapp.UseCase
import com.study.thesuperiorstanislav.edaapp.data.source.CircuitDataSource
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawPoint
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawType
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.ObjectType
import com.study.thesuperiorstanislav.edaapp.utils.math.LeeAlgorithm

class DoTheRouting(private val circuitRepository: CircuitDataSource): UseCase<DoTheRouting.RequestValues, DoTheRouting.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues?) {
        if (requestValues != null) {
            circuitRepository.getCircuit(object : CircuitDataSource.LoadCircuitCallback {
                override fun onCircuitLoaded(circuit: Circuit, circuitName: String, drawMatrix: Array<Array<DrawObject?>>, linesList: MutableList<List<Point>>) {
                    val leeAlgorithm = LeeAlgorithm(drawMatrix)
                    linesList.clear()
                    circuit.listPins.forEach { pin ->
                        val pinPoint = pin.getPoint()
                        val netPoint = pin.getNet()!!.getPoint()
                        val leeReturnData = leeAlgorithm.doTheThing(pinPoint, netPoint, false)
                        if (leeReturnData != null) {
                            linesList.add(leeReturnData.path)
                            leeReturnData.path.forEach {
                                if (drawMatrix[it.y][it.x] == null)
                                    drawMatrix[it.y][it.x] = DrawObject(DrawPoint(0f, 0f), ObjectType.Connector, DrawType.LINE)
                            }
                        }
                    }
                    val responseValue = ResponseValue(circuit,circuitName,drawMatrix,linesList)
                    useCaseCallback?.onSuccess(responseValue)
                }


                override fun onDataNotAvailable(error: Error) {
                    useCaseCallback?.onError(error)
                }
            })
        }
    }


    class RequestValues : UseCase.RequestValues

    class ResponseValue(val circuit: Circuit, val circuitName: String, val drawMatrix: Array<Array<DrawObject?>>, val linesList: List<List<Point>>) : UseCase.ResponseValue
}
