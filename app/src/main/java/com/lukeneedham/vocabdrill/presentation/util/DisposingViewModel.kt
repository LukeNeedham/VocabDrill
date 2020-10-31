package com.lukeneedham.vocabdrill.presentation.util

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class DisposingViewModel : ViewModel() {
    protected val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
