package com.study.thesuperiorstanislav.edaapp.usecase

interface UseCaseScheduler {

    fun execute(runnable: Runnable)

    fun <V : UseCase.ResponseValue> notifyResponse(response: V,
                                                   useCaseCallback: UseCase.UseCaseCallback<V>)

    fun <V : UseCase.ResponseValue, PV : UseCaseWithProgress.ProgressValue> notifyProgress(progress: PV,
                                                   useCaseCallback: UseCaseWithProgress.UseCaseCallback<PV,V>)

    fun <V : UseCase.ResponseValue> onError(error: UseCase.Error,
                                            useCaseCallback: UseCase.UseCaseCallback<V>)
}
