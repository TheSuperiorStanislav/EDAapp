package com.study.thesuperiorstanislav.edaapp.utils.math

import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject

class AStarAlgorithm(drawMatrix:Array<Array<DrawObject?>>): RoutingAlgorithm(drawMatrix) {
    override fun doTheThing(startPoint: Point, endPoint: Point, isDiagonal: Boolean): AlgorithmReturnData? {
        return null
    }
}