package com.study.thesuperiorstanislav.decisiontheorylab1

import android.os.Handler

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Executes asynchronous tasks using a [ThreadPoolExecutor].
 */
class UseCaseThreadPoolScheduler : UseCaseScheduler {

    private val mHandler = Handler()

    private var mThreadPoolExecutor: ThreadPoolExecutor

    init {
        mThreadPoolExecutor = ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, TIMEOUT.toLong(),
                TimeUnit.SECONDS, ArrayBlockingQueue<Runnable>(POOL_SIZE))
    }

    override fun execute(runnable: Runnable) {
        mThreadPoolExecutor.execute(runnable)
    }

    override fun <V : UseCase.ResponseValue> notifyResponse(response: V,
                                                            useCaseCallback: UseCase.UseCaseCallback<V>) {
        mHandler.post { useCaseCallback.onSuccess(response) }
    }

    override fun <V : UseCase.ResponseValue> onError(error: UseCase.Error,
                                                     useCaseCallback: UseCase.UseCaseCallback<V>) {
        mHandler.post { useCaseCallback.onError(error) }
    }

    companion object {

        const val POOL_SIZE = 3

        const val MAX_POOL_SIZE = 6

        const val TIMEOUT = 30
    }

}
