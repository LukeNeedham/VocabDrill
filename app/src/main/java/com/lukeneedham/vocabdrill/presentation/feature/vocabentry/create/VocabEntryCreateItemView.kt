package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.EditText
import android.widget.FrameLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagCreateCallback
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagCreateViewCallback
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagPresentItem
import com.lukeneedham.vocabdrill.presentation.feature.tag.suggestion.TagSuggestion
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.*
import com.lukeneedham.vocabdrill.presentation.util.BaseTextWatcher
import com.lukeneedham.vocabdrill.presentation.util.TextSelection
import com.lukeneedham.vocabdrill.presentation.util.extension.getSelection
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import com.lukeneedham.vocabdrill.presentation.util.extension.setSelection
import kotlinx.android.synthetic.main.view_item_vocab_entry_create.view.*

class VocabEntryCreateItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), RecyclerItemView<VocabEntryCreateProps> {

    private val tagCreateViewCallback = object : TagCreateViewCallback {
        override fun onUpdateName(tagNameInput: EditText) {
            requireTagCreateCallback().onUpdateName(
                requireEntryItem(),
                tagNameInput.text.toString(),
                tagNameInput.getSelection()
            )
        }
    }

    private val tagsLayoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP)
    private val tagsAdapter = TagsAdapter(context, ::onExistingTagClick, tagCreateViewCallback)

    private var wordATextWatcher: TextWatcher? = null
    private var wordBTextWatcher: TextWatcher? = null
    private var props: VocabEntryCreateProps? = null

    var vocabEntryCreateCallback: VocabEntryCreateCallback? = null
    var tagCreateCallback: TagCreateCallback? = null
    var tagExistingClickListener: (VocabEntryEditItem, TagPresentItem.Existing) -> Unit =
        { _, _ -> }
    var tagSuggestionClickListener: (VocabEntryEditItem, TagSuggestion) -> Unit = { _, _ -> }

    init {
        inflateFrom(R.layout.view_item_vocab_entry_create)

        tagsRecycler.layoutManager = tagsLayoutManager
        tagsRecycler.adapter = tagsAdapter
        tagsRecycler.itemAnimator = DefaultItemAnimator().apply {
            supportsChangeAnimations = false
        }

        tagSuggestionsView.onSuggestionClickListener = {
            tagSuggestionClickListener(requireEntryItem(), it)
        }
    }

    override fun setItem(position: Int, item: VocabEntryCreateProps) {
        if (item == this.props) {
            // The recyclerview may rebind the same item multiple times.
            // We want to ignore repeat binds, as they will override state internal to this view.
            // So internal state wins over input state in this case
            return
        }
        this.props = item

        val viewMode = item.viewMode

        tagsAdapter.setItemHeight(MATCH_PARENT)

        val entryItem = item.entryItem
        setupTextWatchers(entryItem)
        setupTextFocus(item)
        refreshSaveButtonEnabled()

        saveButton.setOnClickListener {
            val wordA = getWordAInput() ?: error("Word A must have input")
            val wordB = getWordBInput() ?: error("Word B must have input")
            val tags = tagsAdapter.positionDelegate.getItems()
                .filterIsInstance<TagPresentItem.Existing>()
                .map { it.data }
            val proto = VocabEntryProto(wordA, wordB, entryItem.languageId, tags)
            requireEntryCallback().save(proto)
        }

        tagsAdapter.submitList(entryItem.tagItems)

        if (viewMode is ViewMode.Active && viewMode.focusItem is FocusItem.AddTag) {
            val suggestions = viewMode.focusItem.tagSuggestions
            tagSuggestionsView.visibility = View.VISIBLE
            tagSuggestionsView.setSuggestions(suggestions)
        } else {
            tagSuggestionsView.visibility = View.GONE
        }
    }

    private fun setupTextWatchers(item: VocabEntryEditItem.Create) {
        wordAInputView.removeTextChangedListener(wordATextWatcher)
        wordBInputView.removeTextChangedListener(wordBTextWatcher)

        wordAInputView.setText(item.wordA)
        wordBInputView.setText(item.wordB)

        wordATextWatcher = object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                requireEntryCallback().onWordAChanged(s.toString(), wordAInputView.getSelection())
            }
        }
        wordBTextWatcher = object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                requireEntryCallback().onWordBChanged(s.toString(), wordBInputView.getSelection())
            }
        }
        wordAInputView.addTextChangedListener(wordATextWatcher)
        wordBInputView.addTextChangedListener(wordBTextWatcher)
    }

    private fun setupTextFocus(props: VocabEntryCreateProps) {
        wordAInputView.setOnFocusChangeListener { _, _ -> }
        wordBInputView.setOnFocusChangeListener { _, _ -> }

        val viewMode = props.viewMode
        if (viewMode is ViewMode.Active) {
            val focus = viewMode.focusItem
            when (focus) {
                is FocusItem.WordA -> setFocus(wordAInputView, focus.selection)
                is FocusItem.WordB -> setFocus(wordBInputView, focus.selection)
                is FocusItem.None -> {
                    wordAInputView.clearFocus()
                    wordBInputView.clearFocus()
                }
            }
        }

        wordAInputView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                wordAInputView.post {
                    requireEntryCallback().onInteraction(
                        InteractionSection.WordAInput,
                        wordAInputView.getSelection()
                    )
                }
            }
        }
        wordBInputView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                wordBInputView.post {
                    requireEntryCallback().onInteraction(
                        InteractionSection.WordBInput,
                        wordBInputView.getSelection()
                    )
                }
            }
        }
    }


    private fun refreshSaveButtonEnabled() {
        saveButton.isEnabled = isSaveButtonEnabled(getWordAInput(), getWordBInput())
    }

    private fun setFocus(focusView: EditText, selection: TextSelection) {
        focusView.requestFocus()
        focusView.setSelection(selection)
    }

    private fun getWordAInput() = wordAInputView.text?.toString()

    private fun getWordBInput() = wordBInputView.text?.toString()

    private fun isSaveButtonEnabled(wordA: String?, wordB: String?): Boolean =
        !wordA.isNullOrBlank() && !wordB.isNullOrBlank()

    private fun onExistingTagClick(tag: TagPresentItem.Existing) {
        tagExistingClickListener(requireEntryItem(), tag)
    }

    private fun requireEntryItem() = props?.entryItem ?: error("entryItem must be set")

    private fun requireTagCreateCallback() =
        tagCreateCallback ?: error("Tag create callback must be set")

    private fun requireEntryCallback() =
        vocabEntryCreateCallback ?: error("Create entry callback must be set")
}
