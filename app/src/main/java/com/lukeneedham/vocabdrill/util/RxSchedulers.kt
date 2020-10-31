package com.lukeneedham.vocabdrill.util

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object RxSchedulers {
    val database = Schedulers.single()
    val disk = Schedulers.io()
    val network = Schedulers.io()
    val main = AndroidSchedulers.mainThread()
}
