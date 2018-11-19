package com.study.thesuperiorstanislav.edaapp.usecase

abstract class UseCaseWithProgress<PV: UseCaseWithProgress.ProgressValue,Q : UseCase.RequestValues, P : UseCase.ResponseValue>: UseCase<Q,P>() {

    var useCaseCallbackWithProgress: UseCaseCallback<PV,P>? = null

    interface ProgressValue

    interface UseCaseCallback<P,RV: UseCase.ResponseValue> :UseCase.UseCaseCallback<RV>{
        fun onProgress(progress: P)
    }
}