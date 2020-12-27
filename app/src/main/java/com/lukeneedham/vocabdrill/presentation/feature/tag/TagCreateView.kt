package com.lukeneedham.vocabdrill.presentation.feature.tag

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import group.infotech.drawable.dsl.shapeDrawable
import kotlinx.android.synthetic.main.view_tag_create.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class TagCreateView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), RecyclerItemView<TagItem.New>, KoinComponent {

    private val viewModel: TagCreateViewModel by inject()

    private val background = shapeDrawable {
        // TODO: Use dimen
        cornerRadius = 10f
        setColor(ContextCompat.getColor(context, R.color.tag_create_background))
    }

    init {
        inflateFrom(R.layout.view_tag_create)
        setBackground(background)
        tagNameInput.doOnTextChanged { text, _, _, _ ->
            viewModel.onNameChanged(text.toString())
        }
    }

    override fun setItem(position: Int, item: TagItem.New) {
        tagNameInput.setText("")
    }
}
