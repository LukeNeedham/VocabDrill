package com.lukeneedham.vocabdrill.presentation.feature.vocabentry

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagItemProps
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import kotlinx.android.synthetic.main.view_tags.view.*

class TagsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val viewModel: TagsViewModel = TagsViewModel()

    init {
        inflateFrom(R.layout.view_tags)
        composeView.setContent {
            val items by viewModel.items.observeAsState(initial = emptyList())
            val createTagText by viewModel.createTagText.observeAsState(initial = "")

            // TODO: Should be a flex eventually, when that is added
            LazyRow {
                items(items) {
                    when (it) {
                        is TagItemProps.Existing -> ExistingTag(it)
                        is TagItemProps.Create -> CreateTag(createTagText, ::onCreateItemTextChange)
                    }
                }
            }
        }
    }

    fun setItems(items: List<TagItemProps>) {
        viewModel.setItems(items)
    }

    private fun onCreateItemTextChange(text: String) {
        viewModel.onCreateTagTextChange(text)
    }
}

@Composable
fun CreateTag(text: String, onTextChange: (String) -> Unit) {
    TextField(value = text, onValueChange = onTextChange)
}

@Composable
fun ExistingTag(tag: TagItemProps.Existing) {
    val data = tag.data
    val color = Color(data.color)
    val textColor = Color(tag.textColor)

    Text(
        text = data.name,
        color = textColor,
        modifier = Modifier.background(
            color,
            shape = RoundedCornerShape(10.dp)
        )
    )
}
