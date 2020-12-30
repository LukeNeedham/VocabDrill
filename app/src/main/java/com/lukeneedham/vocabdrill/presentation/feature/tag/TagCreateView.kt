package com.lukeneedham.vocabdrill.presentation.feature.tag

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.util.BaseTextWatcher
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import com.lukeneedham.vocabdrill.presentation.util.extension.showKeyboard
import kotlinx.android.synthetic.main.view_tag_create.view.*
import org.koin.core.KoinComponent

class TagCreateView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), RecyclerItemView<TagItem.Create>, KoinComponent {

    private var tagNameTextWatcher: TextWatcher? = null

    var callback: TagCreateCallback? = null

    init {
        inflateFrom(R.layout.view_tag_create)
        tagNameInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                tagNameInput.setText("")
                tagNameInput.visibility = View.GONE
                addIconView.visibility = View.VISIBLE
            }
        }
        addIconView.setOnClickListener {
            addIconView.visibility = View.GONE
            tagNameInput.visibility = View.VISIBLE
            tagNameInput.requestFocus()
            context.showKeyboard()
            requireCallback().onStartNameInput()
        }
    }

    override fun setItem(position: Int, item: TagItem.Create) {
        tagNameInput.removeTextChangedListener(tagNameTextWatcher)
        tagNameInput.setText("")
        tagNameTextWatcher = object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                requireCallback().onNameChanged(item, s.toString())
            }
        }
        tagNameInput.addTextChangedListener(tagNameTextWatcher)
    }

    private fun requireCallback() = callback ?: error("Callback must be set")
}
