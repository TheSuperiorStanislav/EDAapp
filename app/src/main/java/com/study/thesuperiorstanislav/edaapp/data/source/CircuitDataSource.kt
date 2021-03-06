package com.study.thesuperiorstanislav.edaapp.data.source

import com.study.thesuperiorstanislav.edaapp.usecase.UseCase
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject

interface CircuitDataSource {
    interface LoadCircuitCallback {

        fun onCircuitLoaded(circuit: Circuit,circuitName: String,drawMatrix: Array<Array<DrawObject?>>,linesList: MutableList<MutableList<Point>>)

        fun onDataNotAvailable(error: UseCase.Error)
    }

    interface CacheDataCallback {

        fun onSaved()
    }

    fun getCircuit(callback: LoadCircuitCallback)

    fun cacheCircuit(circuit: Circuit,circuitName: String, callback: CacheDataCallback)

    fun cacheDrawMatrix(drawMatrix: Array<Array<DrawObject?>>, callback: CacheDataCallback)
}