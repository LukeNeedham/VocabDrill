package com.lukeneedham.vocabdrill.presentation.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

object LiveDataZipper {
    fun <Input1, Input2, Output> biZip(
        inputLiveData1: LiveData<Input1>,
        inputLiveData2: LiveData<Input2>,
        transform: (data1: Input1, data2: Input2) -> Output
    ): LiveData<Output> {
        var lastInput1: Input1? = null
        var lastInput2: Input2? = null
        val mediatorLiveData = MediatorLiveData<Output>()

        mediatorLiveData.addSource(inputLiveData1) { input1 ->
            lastInput1 = input1
            val input2 = lastInput2
            if (input2 != null) {
                mediatorLiveData.value = transform(input1, input2)
            }
        }
        mediatorLiveData.addSource(inputLiveData2) { input2 ->
            lastInput2 = input2
            val input1 = lastInput1
            if (input1 != null) {
                mediatorLiveData.value = transform(input1, input2)
            }
        }

        return mediatorLiveData
    }
}
