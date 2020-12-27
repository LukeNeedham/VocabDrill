package com.lukeneedham.vocabdrill.presentation.util.extension

import io.reactivex.Observable

/**
 * Seems like a strange function?
 * It's used to convert [Subject]s, etc, to Observable without having to re-declare types
 */
fun <T> Observable<T>.toObservable(): Observable<T> = this
