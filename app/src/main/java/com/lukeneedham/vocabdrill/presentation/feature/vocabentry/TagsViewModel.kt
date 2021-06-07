package com.lukeneedham.vocabdrill.presentation.feature.vocabentry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagCreateView
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagItemProps

class TagsViewModel {
    private val existingItems = MutableLiveData<List<TagItemProps>>()
    val items = Transformations.map(existingItems) {
        it + TagItemProps.Create("NEW", null)
    }

    val createTagText = MutableLiveData<String>()

    fun setItems(items: List<TagItemProps>) {
        existingItems.value = items
    }

    fun onCreateTagTextChange(text: String) {
        createTagText.value = text
    }
}
