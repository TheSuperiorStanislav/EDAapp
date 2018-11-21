package com.study.thesuperiorstanislav.edaapp.routing.domain.usecase

import com.study.thesuperiorstanislav.edaapp.usecase.UseCase
import com.study.thesuperiorstanislav.edaapp.data.source.CircuitDataSource
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawPoint
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawType
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.ObjectType
import com.study.thesuperiorstanislav.edaapp.usecase.UseCaseWithProgress
import com.study.thesuperiorstanislav.edaapp.utils.math.LeeAlgorithm

class DoTheRouting(private val circuitRepository: CircuitDataSource): UseCaseWithProgress<DoTheRouting.ProgressValue,DoTheRouting.RequestValue, DoTheRouting.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValue?) {
        if (requestValues != null) {
            circuitRepository.getCircuit(object : CircuitDataSource.LoadCircuitCallback {
                override fun onCircuitLoaded(circuit: Circuit, circuitName: String, drawMatrix: Array<Array<DrawObject?>>, linesList: MutableList<MutableList<Point>>) {
                    val isAStarAlgorithm = requestValues.isAStarAlgorithm
                    val isDiagonal = requestValues.isDiagonal
                    val isIntersectionAllowed = requestValues.isIntersectionAllowed
                    val drawMatrixCopy = createDrawMatrixCopy(drawMatrix)
                    val algorithm = LeeAlgorithm(drawMatrixCopy)
                    linesList.clear()
                    circuit.listPins.forEachIndexed { index, pin ->
                        val responseValueForProgress = ResponseValue(circuit,circuitName,drawMatrix,linesList)
                        val progressValue = ProgressValue(circuit.listPins.size,index+1,responseValueForProgress)
                        useCaseCallbackWithProgress?.onProgress(progressValue)

                        val pinPoint = pin.getPoint()
                        val netPoint = pin.getNet()!!.getPoint()
                        val algorithmReturnData = algorithm.doTheThing(pinPoint, netPoint, isDiagonal)
                        if (algorithmReturnData != null) {
                            linesList.add(algorithmReturnData.path as MutableList<Point>)
                            var firstPoint = linesList.last().first()
                            val toRemove = mutableListOf<Point>()
                            linesList.last().forEach { point ->
                                if (!isIntersectionAllowed)
                                    drawMatrixCopy[point.y][point.x] = DrawObject(DrawPoint(0f, 0f),
                                            ObjectType.Connector, DrawType.LINE)
                                if (firstPoint != point) {
                                    if (firstPoint.x == point.x || firstPoint.y == point.y) {
                                        toRemove.add(point)
                                    } else {
                                        if (!toRemove.isEmpty())
                                            toRemove.remove(toRemove.last())
                                        firstPoint = point
                                    }
                                }
                            }
                            if (!toRemove.isEmpty())
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

    private fun createDrawMatrixCopy(drawMatrix: Array<Array<DrawObject?>>): Array<Array<DrawObject?>> {
        return Array(drawMatrix.size) { y ->
            Array(drawMatrix[y].size) { x ->
                drawMatrix[y][x]?.copy()
            }
        }
    }


    class RequestValue(val isAStarAlgorithm: Boolean,val isDiagonal: Boolean,val isIntersectionAllowed: Boolean) : UseCase.RequestValues

    class ProgressValue(val pinsCount:Int,val doneCount:Int,val responseValue: ResponseValue) : UseCaseWithProgress.ProgressValue

    class ResponseValue(val circuit: Circuit, val circuitName: String, val drawMatrix: Array<Array<DrawObject?>>, val linesList: List<List<Point>>) : UseCase.ResponseValue
}
