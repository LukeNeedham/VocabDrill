package com.lukeneedham.vocabdrill.util.extension

import io.reactivex.Observable

inline fun <reified T> List<Observable<T>>.zip(): Observable<List<T>> {
    if (isEmpty()) {
        return Observable.just(emptyList())
    }
    return Observable.zip(this) {
        it.filterIsInstance<T>()
    }
}
