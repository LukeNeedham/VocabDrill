package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.setHintEnabledMaintainMargin
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagCreateCallback
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.FocusItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.TagsAdapter
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.ViewMode
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryEditItem
import com.lukeneedham.vocabdrill.presentation.util.BaseTextWatcher
import com.lukeneedham.vocabdrill.presentation.util.TextSelection
import com.lukeneedham.vocabdrill.presentation.util.extension.getSelection
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import com.lukeneedham.vocabdrill.presentation.util.extension.setSelection
import kotlinx.android.synthetic.main.view_item_vocab_entry.view.*

class VocabEntryExistingItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    RecyclerItemView<VocabEntryEditItem.Existing> {

    private val tagsAdapter = TagsAdapter(context, ::onExistingTagClick, ::onCreateTagNameChanged)

    private var wordATextWatcher: TextWatcher? = null
    private var wordBTextWatcher: TextWatcher? = null
    private var entryItem: VocabEntryEditItem.Existing? = null

    var vocabEntryExistingCallback: VocabEntryExistingCallback? = null
    var tagCreateCallback: TagCreateCallback? = null
    var tagExistingClickListener: (VocabEntryEditItem, TagItem.Existing) -> Unit = { _, _ -> }

    init {
        inflateFrom(R.layout.view_item_vocab_entry)
        tagsRecycler.layoutManager = LinearLayoutManager(context).apply {
            orientation = RecyclerView.HORIZONTAL
        }
        wordAInputViewLayout.setHintEnabledMaintainMargin(false)
        wordBInputViewLayout.setHintEnabledMaintainMargin(false)

        tagsRecycler.adapter = tagsAdapter
    }

    override fun setItem(position: Int, item: VocabEntryEditItem.Existing) {
        this.entryItem = item

        setupTextWatchers(item)
        setupTextFocus(item)

        tagsAdapter.submitList(item.tagItems)

        deleteButton.setOnClickListener {
            requireEntryCallback().onDelete(item)
        }

        backgroundView.setOnClickListener { flipMode(item) }
        chevronIconView.setOnClickListener { flipMode(item) }
    }

    private fun setupTextWatchers(item: VocabEntryEditItem.Existing) {
        wordAInputView.removeTextChangedListener(wordATextWatcher)
        wordBInputView.removeTextChangedListener(wordBTextWatcher)

        wordAInputView.setText(item.entry.wordA)
        wordBInputView.setText(item.entry.wordB)

        wordATextWatcher = object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                requireEntryCallback().onWordAChanged(
                    item,
                    s.toString(),
                    wordAInputView.getSelection()
                )
            }
        }
        wordBTextWatcher = object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                requireEntryCallback().onWordBChanged(
                    item,
                    s.toString(),
                    wordBInputView.getSelection()
                )
            }
        }
        wordAInputView.addTextChangedListener(wordATextWatcher)
        wordBInputView.addTextChangedListener(wordBTextWatcher)
    }

    private fun setupTextFocus(item: VocabEntryEditItem.Existing) {
        wordAInputView.setOnFocusChangeListener { _, _ -> }
        wordBInputView.setOnFocusChangeListener { _, _ -> }

        val viewMode = item.viewMode
        setMode(viewMode)
        if (viewMode is ViewMode.Active) {
            val focus = viewMode.focusItem
            when (focus) {
                is FocusItem.WordA -> setFocus(wordAInputView, focus.selection)
                is FocusItem.WordB -> setFocus(wordBInputView, focus.selection)
            }
        }

        wordAInputView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                wordAInputView.post {
                    requireEntryCallback().onViewModeChanged(
                        item,
                        ViewMode.Active(FocusItem.WordA(wordAInputView.getSelection()))
                    )
                }
            }
        }
        wordBInputView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                wordBInputView.post {
                    requireEntryCallback().onViewModeChanged(
                        item,
                        ViewMode.Active(FocusItem.WordB(wordBInputView.getSelection()))
                    )
                }
            }
        }
    }

    private fun flipMode(item: VocabEntryEditItem.Existing) {
        val viewMode = item.viewMode
        val newMode = getFlippedMode(viewMode)
        requireEntryCallback().onViewModeChanged(item, newMode)
    }

    private fun setFocus(focusView: EditText, selection: TextSelection) {
        focusView.requestFocus()
        focusView.setSelection(selection)
    }

    private fun getFlippedMode(viewMode: ViewMode): ViewMode {
        return when (viewMode) {
            is ViewMode.Inactive -> ViewMode.Active(FocusItem.WordA(TextSelection.End))
            is ViewMode.Active -> ViewMode.Inactive
        }
    }

    private fun setMode(mode: ViewMode) {
        val isExpanded = mode is ViewMode.Active
        deleteButton.visibility = if (isExpanded) View.VISIBLE else View.GONE
        val chevronResId = if (isExpanded) R.drawable.ic_chevron_up else R.drawable.ic_chevron_down
        chevronIconView.setImageResource(chevronResId)
    }

    private fun onExistingTagClick(tag: TagItem.Existing) {
        tagExistingClickListener(requireEntryItem(), tag)
    }

    private fun onCreateTagNameChanged(tag: TagItem.Create, name: String, nameInputView: View) {
        requireTagCreateCallback().onNameChanged(requireEntryItem(), tag, name, nameInputView)
    }

    private fun requireEntryItem() = entryItem ?: error("entryItem must be set")

    private fun requireEntryCallback() =
        vocabEntryExistingCallback ?: error("Existing entry callback must be set")

    private fun requireTagCreateCallback() =
        tagCreateCallback ?: error("Tag create callback must be set")
}
