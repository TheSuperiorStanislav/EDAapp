package com.study.thesuperiorstanislav.edaapp.data.source

import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit

object CircuitRepository: CircuitDataSource {

    private var cacheCircuit: Circuit = Circuit(mutableListOf(),
            mutableListOf(), mutableListOf())
    private var circuitName = "Untitled"

    override fun getCircuit(callback: CircuitDataSource.LoadCircuitCallback) {
        callback.onCircuitLoaded(cacheCircuit, circuitName)
    }

    override fun cacheCircuit(circuit: Circuit, circuitName: String, callback: CircuitDataSource.CacheCircuitCallback) {
        cacheCircuit = circuit
        this.circuitName = circuitName
        callback.onSaved()
    }
}