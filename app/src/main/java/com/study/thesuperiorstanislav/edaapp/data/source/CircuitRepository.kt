package com.study.thesuperiorstanislav.edaapp.data.source

import com.study.thesuperiorstanislav.edaapp.UseCase
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit

object CircuitRepository: CircuitDataSource {

    var cacheCircuit: Circuit? = null

    override fun getCircuit(callback: CircuitDataSource.LoadCircuitCallback) {
        if (cacheCircuit != null)
            callback.onCircuitLoaded(cacheCircuit!!)
        else
            callback.onDataNotAvailable(UseCase.Error(UseCase.Error.UNKNOWN_ERROR,""))
    }

    override fun cacheCircuit(circuit: Circuit, callback: CircuitDataSource.CacheCircuitCallback) {
        cacheCircuit = circuit
        callback.onSaved()
    }
}