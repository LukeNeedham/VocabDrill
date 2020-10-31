package com.lukeneedham.vocabdrill.presentation.util.extension

import androidx.lifecycle.LiveData

fun <T> LiveData<T>.toLiveData(): LiveData<T> = this
