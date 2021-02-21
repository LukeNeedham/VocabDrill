package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.EditText
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.textfield.setHintEnabledMaintainMargin
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagCreateCallback
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagCreateViewCallback
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagPresentItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.FocusItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.TagsAdapter
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.ViewMode
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryEditItem
import com.lukeneedham.vocabdrill.presentation.util.BaseTextWatcher
import com.lukeneedham.vocabdrill.presentation.util.TextSelection
import com.lukeneedham.vocabdrill.presentation.util.extension.getSelection
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import com.lukeneedham.vocabdrill.presentation.util.extension.setSelection
import com.lukeneedham.vocabdrill.presentation.util.recyclerview.RecyclerViewClickInterceptor
import kotlinx.android.synthetic.main.view_item_vocab_entry_existing.view.*


class VocabEntryExistingItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    RecyclerItemView<VocabEntryEditItem.Existing> {

    private val tagCreateViewCallback = object : TagCreateViewCallback {
        override fun onFocused(name: String, nameInputView: View, hasFocus: Boolean) {
            requireTagCreateCallback().onFocusChange(
                requireEntryItem(),
                name,
                nameInputView,
                hasFocus
            )
        }

        override fun onNameChanged(name: String, nameInputView: View) {
            requireTagCreateCallback().onNameChanged(requireEntryItem(), name, nameInputView)
        }
    }

    private val tagsLayoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP)
    private val tagsAdapter = TagsAdapter(context, ::onExistingTagClick, tagCreateViewCallback)

    private var wordATextWatcher: TextWatcher? = null
    private var wordBTextWatcher: TextWatcher? = null
    private var entryItem: VocabEntryEditItem.Existing? = null
    private var viewMode: ViewMode? = null

    var vocabEntryExistingCallback: VocabEntryExistingCallback? = null
    var tagCreateCallback: TagCreateCallback? = null
    var tagExistingClickListener: (VocabEntryEditItem, TagPresentItem.Existing) -> Unit =
        { _, _ -> }

    init {
        inflateFrom(R.layout.view_item_vocab_entry_existing)
        tagsRecycler.layoutManager = tagsLayoutManager
        wordAInputViewLayout.setHintEnabledMaintainMargin(false)
        wordBInputViewLayout.setHintEnabledMaintainMargin(false)

        tagsRecycler.adapter = tagsAdapter

        deleteButton.setOnClickListener {
            requireEntryCallback().onDelete(requireEntryItem())
        }

        tagsRecycler.setOnTouchListener(RecyclerViewClickInterceptor())
        tagsRecycler.setOnClickListener {
            flipMode()
        }
        tagsRecyclerOverlay.setOnClickListener {
            flipMode()
        }
        backgroundView.setOnClickListener { flipMode() }
        chevronIconView.setOnClickListener { flipMode() }
    }

    override fun setItem(position: Int, item: VocabEntryEditItem.Existing) {
        requireTagCreateCallback().onBound(item)
        this.entryItem = item

        setupTextWatchers(item)
        setupTextFocus(item)

        tagsAdapter.submitList(item.tagItems)
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

    private fun clearAllFocus() {
        wordAInputView.clearFocus()
        wordBInputView.clearFocus()
    }

    private fun setupTextFocus(item: VocabEntryEditItem.Existing) {
        wordAInputView.setOnFocusChangeListener { _, _ -> }
        wordBInputView.setOnFocusChangeListener { _, _ -> }

        val viewMode = item.viewMode
        setViewMode(viewMode)
        if (viewMode is ViewMode.Active) {
            val focus = viewMode.focusItem
            when (focus) {
                is FocusItem.WordA -> setFocus(wordAInputView, focus.selection)
                is FocusItem.WordB -> setFocus(wordBInputView, focus.selection)
                is FocusItem.None -> clearAllFocus()
            }
        } else {
            clearAllFocus()
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

    private fun flipMode() {
        val item = requireEntryItem()
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
            is ViewMode.Inactive -> ViewMode.Active(FocusItem.None)
            is ViewMode.Active -> ViewMode.Inactive
        }
    }

    private fun setViewMode(mode: ViewMode) {
        this.viewMode = mode

        val isExpanded = mode is ViewMode.Active
        deleteButton.visibility = if (isExpanded) View.VISIBLE else View.GONE
        val chevronResId = if (isExpanded) R.drawable.ic_chevron_up else R.drawable.ic_chevron_down
        chevronIconView.setImageResource(chevronResId)

        val tagCollapsedItemHeight =
            context.resources.getDimensionPixelSize(R.dimen.tag_item_height_collapsed)
        val tagExpandedItemHeight =
            context.resources.getDimensionPixelSize(R.dimen.tag_item_height_expanded)
        val tagItemHeight = if (isExpanded) tagExpandedItemHeight else tagCollapsedItemHeight
        tagsAdapter.setItemHeight(tagItemHeight)

        // Tags overlay intercepts click events in collapsed mode only
        tagsRecyclerOverlay.isClickable = !isExpanded

        tagsRecycler.updateLayoutParams<ConstraintLayout.LayoutParams> {
            height = if (isExpanded) {
                WRAP_CONTENT
            } else {
                // When in collapsed view, the height of the recyclerview is the height of an item
                // This will therefore show only a single row of items
                tagCollapsedItemHeight
            }
        }
    }

    private fun onExistingTagClick(tag: TagPresentItem.Existing) {
        tagExistingClickListener(requireEntryItem(), tag)
    }

    private fun requireEntryItem() = entryItem ?: error("entryItem must be set")

    private fun requireEntryCallback() =
        vocabEntryExistingCallback ?: error("Existing entry callback must be set")

    private fun requireTagCreateCallback() =
        tagCreateCallback ?: error("Tag create callback must be set")
}
