package com.lukeneedham.vocabdrill.presentation.util

class InvalidInputException(inputName: String, inputValue: Any?) :
        Exception("Invalid Input for $inputName: $inputValue") {
    }
