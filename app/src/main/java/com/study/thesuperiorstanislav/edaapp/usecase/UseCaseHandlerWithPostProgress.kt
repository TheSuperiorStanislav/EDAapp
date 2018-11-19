package com.study.thesuperiorstanislav.edaapp.usecase

object UseCaseHandlerWithPostProgress {
    private var mUseCaseScheduler: UseCaseScheduler? = UseCaseThreadPoolScheduler()

    fun <T : UseCase.RequestValues, R : UseCase.ResponseValue, PV : UseCaseWithProgress.ProgressValue> execute(
            useCase: UseCaseWithProgress<PV ,T, R>, values: T, callback: UseCaseWithProgress.UseCaseCallback<PV, R>) {
        useCase.requestValues = values
        useCase.useCaseCallbackWithProgress = UiCallbackWrapper(callback, this)

        try {
            mUseCaseScheduler?.execute(Runnable { useCase.run() })
        } catch (e: Exception) {
            callback.onError(UseCase.Error(UseCase.Error.UNKNOWN_ERROR, "Сложнаа("))
        }
    }

    fun <V : UseCase.ResponseValue> notifyResponse(response: V, useCaseCallback: UseCase.UseCaseCallback<V>) {
        mUseCaseScheduler?.notifyResponse(response, useCaseCallback)
    }

    fun <V : UseCase.ResponseValue, PV : UseCaseWithProgress.ProgressValue> notifyProgress(progress: PV, useCaseCallback: UseCaseWithProgress.UseCaseCallback<PV,V>) {
        mUseCaseScheduler?.notifyProgress(progress, useCaseCallback)
    }

    private fun <V : UseCase.ResponseValue> notifyError(error: UseCase.Error, useCaseCallback: UseCase.UseCaseCallback<V>) {
        mUseCaseScheduler?.onError(error, useCaseCallback)
    }

    private class UiCallbackWrapper<V : UseCase.ResponseValue, PV : UseCaseWithProgress.ProgressValue>(private val mCallback: UseCaseWithProgress.UseCaseCallback<PV, V>,
                                                                                                       private val mUseCaseHandler: UseCaseHandlerWithPostProgress) : UseCaseWithProgress.UseCaseCallback<PV, V> {
        override fun onSuccess(response: V) {
            notifyResponse(response, mCallback)
        }

        override fun onProgress(progress: PV) {
            notifyProgress(progress, mCallback)
        }

        override fun onError(error: UseCase.Error) {
            notifyError(error, mCallback)
        }
    }
}