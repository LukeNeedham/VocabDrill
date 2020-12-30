package com.lukeneedham.vocabdrill.presentation.feature.tag

interface TagCreateCallback {
    fun onNameChanged(tagItem: TagItem.Create, name: String)
    fun onStartNameInput()
}
