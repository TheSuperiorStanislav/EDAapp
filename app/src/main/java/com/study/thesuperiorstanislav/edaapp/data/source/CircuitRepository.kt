package com.study.thesuperiorstanislav.edaapp.data.source

import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit

object CircuitRepository: CircuitDataSource {

    private var cacheCircuit: Circuit? = null
    private var circuitName = "Untitled"

    override fun getCircuit(callback: CircuitDataSource.LoadCircuitCallback) {
        if (cacheCircuit == null)
            callback.onCircuitLoaded(Circuit(mutableListOf(),
                    mutableListOf(), mutableListOf()), circuitName)
        else
            callback.onCircuitLoaded(cacheCircuit!!, circuitName)
    }

    override fun cacheCircuit(circuit: Circuit, circuitName: String, callback: CircuitDataSource.CacheCircuitCallback) {
        cacheCircuit = circuit
        this.circuitName = circuitName
        callback.onSaved()
    }
}