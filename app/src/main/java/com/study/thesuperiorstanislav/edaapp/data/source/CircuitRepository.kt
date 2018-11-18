package com.study.thesuperiorstanislav.edaapp.data.source

import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Circuit
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject

object CircuitRepository: CircuitDataSource {


    private var cacheCircuit: Circuit = Circuit(mutableListOf(),
            mutableListOf(), mutableListOf())
    private var cacheCircuitName = "Untitled"
    private var cacheDrawMatrix: Array<Array<DrawObject?>> = emptyArray()

    override fun getCircuit(callback: CircuitDataSource.LoadCircuitCallback) {
        callback.onCircuitLoaded(cacheCircuit, cacheCircuitName,cacheDrawMatrix)
    }

    override fun cacheCircuit(circuit: Circuit, circuitName: String, callback: CircuitDataSource.CacheDataCallback) {
        cacheCircuit = circuit
        cacheCircuitName = circuitName
        cacheDrawMatrix = emptyArray()
        callback.onSaved()
    }

    override fun cacheDrawMatrix(drawMatrix: Array<Array<DrawObject?>>, callback: CircuitDataSource.CacheDataCallback) {
        cacheDrawMatrix = drawMatrix
        callback.onSaved()
    }
}