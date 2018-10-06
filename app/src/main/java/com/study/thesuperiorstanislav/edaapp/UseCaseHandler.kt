package com.study.thesuperiorstanislav.edaapp

/**
 * Runs [UseCase]s using a [UseCaseScheduler].
 */
object UseCaseHandler {
    private var mUseCaseScheduler: UseCaseScheduler? = UseCaseThreadPoolScheduler()

    fun <T : UseCase.RequestValues, R : UseCase.ResponseValue> execute(
            useCase: UseCase<T, R>, values: T, callback: UseCase.UseCaseCallback<R>) {
        useCase.requestValues = values
        useCase.useCaseCallback = UiCallbackWrapper(callback, this)

        try {
            mUseCaseScheduler?.execute(Runnable { useCase.run() })
        }catch (e:Exception){
            callback.onError(UseCase.Error(UseCase.Error.UNKNOWN_ERROR,"Сложнаа("))
        }
    }

    fun <V : UseCase.ResponseValue> notifyResponse(response: V, useCaseCallback: UseCase.UseCaseCallback<V>) {
        mUseCaseScheduler?.notifyResponse(response, useCaseCallback)
    }

    private fun <V : UseCase.ResponseValue> notifyError(error: UseCase.Error,
                                                        useCaseCallback: UseCase.UseCaseCallback<V>) {
        mUseCaseScheduler?.onError(error,useCaseCallback)
    }

    private class UiCallbackWrapper<V : UseCase.ResponseValue>(private val mCallback: UseCase.UseCaseCallback<V>,
                                                               private val mUseCaseHandler: UseCaseHandler) : UseCase.UseCaseCallback<V> {
        override fun onSuccess(response: V) {
            mUseCaseHandler.notifyResponse(response, mCallback)
        }

        override fun onError(error: UseCase.Error) {
            mUseCaseHandler.notifyError(error,mCallback)
        }
    }

}
