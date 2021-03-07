package com.lukeneedham.vocabdrill.presentation.feature.tag

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.util.BaseTextWatcher
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import com.lukeneedham.vocabdrill.presentation.util.extension.setSelection
import com.lukeneedham.vocabdrill.presentation.util.extension.showKeyboard
import kotlinx.android.synthetic.main.view_tag_create.view.*

class TagCreateView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), RecyclerItemView<TagPresentItem.Create> {

    private var item: TagPresentItem.Create? = null

    private val tagNameTextWatcher: TextWatcher = object : BaseTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            requireCallback().onUpdateName(tagNameInput)
        }
    }

    var callback: TagCreateViewCallback? = null

    init {
        inflateFrom(R.layout.view_tag_create)
        addIconView.setOnClickListener {
            requireCallback().onUpdateName(tagNameInput)
        }
    }

    override fun setItem(position: Int, item: TagPresentItem.Create) {
        if (item == this.item) {
            // The recyclerview may rebind the same item multiple times.
            // We want to ignore repeat binds, as they will override state internal to this view.
            // So internal state wins over input state in this case
            return
        }
        this.item = item

        val isInInputMode = item.selection != null

        addIconView.visibility = if (isInInputMode) View.GONE else View.VISIBLE
        tagNameInput.visibility = if (isInInputMode) View.VISIBLE else View.GONE

        val inputBackgroundRes = if (isInInputMode) {
            R.drawable.background_tag_create_selected
        } else {
            R.drawable.background_tag_create_unselected
        }
        bubble.background = ContextCompat.getDrawable(context, inputBackgroundRes)

        // Creates a seamless margin effect into the tag suggestion view below
        val bubbleBottomMargin =
            if (isInInputMode) 0 else context.resources.getDimensionPixelSize(R.dimen.tag_item_margin_bottom)
        bubble.updateLayoutParams<MarginLayoutParams> {
            bottomMargin = bubbleBottomMargin
        }

        tagNameInput.removeTextChangedListener(tagNameTextWatcher)
        tagNameInput.setText(item.text)
        tagNameInput.addTextChangedListener(tagNameTextWatcher)

        if (item.selection != null) {
            tagNameInput.requestFocus()
            tagNameInput.setSelection(item.selection)
            context.showKeyboard()
        } else {
            tagNameInput.clearFocus()
        }

    }

    private fun requireCallback() = callback ?: error("Callback must be set")
}
