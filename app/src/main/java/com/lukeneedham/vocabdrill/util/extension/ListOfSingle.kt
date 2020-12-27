package com.lukeneedham.vocabdrill.util.extension

import io.reactivex.Single

inline fun <reified T> List<Single<T>>.zip(): Single<List<T>> {
    if(isEmpty()) {
        return Single.just(emptyList())
    }
    return Single.zip(this) {
        it.filterIsInstance<T>()
    }
}
