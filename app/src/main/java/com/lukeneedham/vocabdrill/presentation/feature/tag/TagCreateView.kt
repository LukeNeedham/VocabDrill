package com.lukeneedham.vocabdrill.presentation.feature.tag

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.util.BaseTextWatcher
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import com.lukeneedham.vocabdrill.presentation.util.extension.showKeyboard
import kotlinx.android.synthetic.main.view_tag_create.view.*
import org.koin.core.KoinComponent

class TagCreateView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), RecyclerItemView<TagPresentItem.Create>,
    KoinComponent {

    private val tagNameTextWatcher: TextWatcher = object : BaseTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            requireCallback().onNameChanged(s.toString(), this@TagCreateView)
        }
    }

    var callback: TagCreateViewCallback? = null

    init {
        inflateFrom(R.layout.view_tag_create)
        tagNameInput.setOnFocusChangeListener { _, hasFocus ->
            requireCallback().onFocused(tagNameInput.text.toString(), this, hasFocus)
            if (!hasFocus) {
                tagNameInput.setText("")
                tagNameInput.visibility = View.GONE
                addIconView.visibility = View.VISIBLE
            }

            val inputBackgroundRes = if (hasFocus) {
                R.drawable.background_tag_create_selected
            } else {
                R.drawable.background_tag_create_unselected
            }
            bubble.background = ContextCompat.getDrawable(context, inputBackgroundRes)
        }
        addIconView.setOnClickListener {
            addIconView.visibility = View.GONE
            tagNameInput.visibility = View.VISIBLE
            tagNameInput.requestFocus()
            context.showKeyboard()
        }
    }

    override fun setItem(position: Int, item: TagPresentItem.Create) {
        tagNameInput.removeTextChangedListener(tagNameTextWatcher)
        tagNameInput.setText("")
        tagNameInput.addTextChangedListener(tagNameTextWatcher)
    }

    private fun requireCallback() = callback ?: error("Callback must be set")
}
