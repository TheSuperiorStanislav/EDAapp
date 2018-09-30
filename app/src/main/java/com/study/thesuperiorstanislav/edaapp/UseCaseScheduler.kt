package com.study.thesuperiorstanislav.edaapp

interface UseCaseScheduler {

    fun execute(runnable: Runnable)

    fun <V : UseCase.ResponseValue> notifyResponse(response: V,
                                                   useCaseCallback: UseCase.UseCaseCallback<V>)

    fun <V : UseCase.ResponseValue> onError(error: UseCase.Error,
                                            useCaseCallback: UseCase.UseCaseCallback<V>)
}
