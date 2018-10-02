package com.study.thesuperiorstanislav.edaapp.data.source

import com.study.thesuperiorstanislav.edaapp.UseCase
import com.study.thesuperiorstanislav.edaapp.main.domain.model.Circuit

interface CircuitDataSource {
    interface LoadCircuitCallback {

        fun onCircuitLoaded(circuit: Circuit)

        fun onDataNotAvailable(error: UseCase.Error)
    }

    interface CacheCircuitCallback {

        fun onSaved()
    }

    fun getCircuit(callback: LoadCircuitCallback)

    fun cacheCircuit(circuit: Circuit, callback: CacheCircuitCallback)


}