package com.study.thesuperiorstanislav.edaapp.routing.domain.usecase

import com.study.thesuperiorstanislav.edaapp.usecase.UseCase
import com.study.thesuperiorstanislav.edaapp.data.source.CircuitDataSource
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject
import com.study.thesuperiorstanislav.edaapp.usecase.UseCaseWithProgress
import com.study.thesuperiorstanislav.edaapp.utils.math.LeeAlgorithm

class DoTheRouting(private val circuitRepository: CircuitDataSource): UseCaseWithProgress<DoTheRouting.ProgressValue,DoTheRouting.RequestValue, DoTheRouting.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValue?) {
        if (requestValues != null) {
            circuitRepository.getCircuit(object : CircuitDataSource.LoadCircuitCallback {
                override fun onCircuitLoaded(circuit: Circuit, circuitName: String, drawMatrix: Array<Array<DrawObject?>>, linesList: MutableList<MutableList<Point>>) {
                    val leeAlgorithm = LeeAlgorithm(drawMatrix)
                    linesList.clear()
                    circuit.listPins.forEachIndexed { index, pin ->
                        val responseValueForProgress = ResponseValue(circuit,circuitName,drawMatrix,linesList)
                        val progressValue = ProgressValue(circuit.listPins.size,index+1,responseValueForProgress)
                        useCaseCallbackWithProgress?.onProgress(progressValue)

                        val pinPoint = pin.getPoint()
                        val netPoint = pin.getNet()!!.getPoint()
                        val leeReturnData = leeAlgorithm.doTheThing(pinPoint, netPoint, false)
                        if (leeReturnData != null) {
                            linesList.add(leeReturnData.path as MutableList<Point>)
                            var firstPoint = linesList.last().first()
                            val toRemove = mutableListOf<Point>()
                            linesList.last().forEach {point ->
                                if (firstPoint != point){
                                    if (firstPoint.x == point.x || firstPoint.y == point.y){
                                        toRemove.add(point)
                                    }else {
                                        if (!toRemove.isEmpty())
                                            toRemove.remove(toRemove.last())
                                        firstPoint = point
                                    }
                                }
                            }
                            toRemove.remove(toRemove.last())
                            toRemove.forEach {
                                linesList.last().remove(it)
                            }
                        }
                    }
                    val responseValue = ResponseValue(circuit,circuitName,drawMatrix,linesList)
                    useCaseCallbackWithProgress?.onSuccess(responseValue)
                }


                override fun onDataNotAvailable(error: Error) {
                    useCaseCallbackWithProgress?.onError(error)
                }
            })
        }
    }


    class RequestValue : UseCase.RequestValues

    class ProgressValue(val pinsCount:Int,val doneCount:Int,val responseValue: ResponseValue) : UseCaseWithProgress.ProgressValue

    class ResponseValue(val circuit: Circuit, val circuitName: String, val drawMatrix: Array<Array<DrawObject?>>, val linesList: List<List<Point>>) : UseCase.ResponseValue
}
