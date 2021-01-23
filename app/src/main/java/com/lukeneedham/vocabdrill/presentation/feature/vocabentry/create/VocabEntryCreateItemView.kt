package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagCreateCallback
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.*
import com.lukeneedham.vocabdrill.presentation.util.BaseTextWatcher
import com.lukeneedham.vocabdrill.presentation.util.TextSelection
import com.lukeneedham.vocabdrill.presentation.util.extension.getSelection
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import com.lukeneedham.vocabdrill.presentation.util.extension.setSelection
import kotlinx.android.synthetic.main.view_item_create_vocab_entry.view.*
import org.koin.core.KoinComponent

class VocabEntryCreateItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    RecyclerItemView<VocabEntryEditItem.Create>,
    KoinComponent {

    private val tagsAdapter = TagsAdapter(context, ::onExistingTagClick, ::onCreateTagNameChanged)

    private var wordATextWatcher: TextWatcher? = null
    private var wordBTextWatcher: TextWatcher? = null
    private var entryItem: VocabEntryEditItem.Create? = null

    var vocabEntryCreateCallback: VocabEntryCreateCallback? = null
    var tagCreateCallback: TagCreateCallback? = null
    var tagExistingClickListener: (VocabEntryEditItem, TagItem.Existing) -> Unit = { _, _ -> }

    init {
        inflateFrom(R.layout.view_item_create_vocab_entry)

        tagsRecycler.layoutManager = LinearLayoutManager(context).apply {
            orientation = RecyclerView.HORIZONTAL
        }
        tagsRecycler.adapter = tagsAdapter
    }

    override fun setItem(position: Int, item: VocabEntryEditItem.Create) {
        this.entryItem = item
        setupTextWatchers(item)
        setupTextFocus(item)
        refreshSaveButtonEnabled()

        saveButton.setOnClickListener {
            val wordA = getWordAInput() ?: error("Word A must have input")
            val wordB = getWordBInput() ?: error("Word B must have input")
            val tags = tagsAdapter.positionDelegate.getItems()
                .filterIsInstance<TagItem.Existing>()
                .map { it.data }
            val proto = VocabEntryProto(wordA, wordB, item.languageId, tags)
            requireEntryCallback().save(proto)
        }

        tagsAdapter.submitList(item.tags)
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

    private fun setupTextFocus(item: VocabEntryEditItem.Create) {
        wordAInputView.setOnFocusChangeListener { _, _ -> }
        wordBInputView.setOnFocusChangeListener { _, _ -> }

        val viewMode = item.viewMode
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

    private fun onExistingTagClick(tag: TagItem.Existing) {
        tagExistingClickListener(requireEntryItem(), tag)
    }

    private fun onCreateTagNameChanged(tag: TagItem.Create, name: String, nameInputView: View) {
        requireTagCreateCallback().onNameChanged(requireEntryItem(), tag, name, nameInputView)
    }

    private fun requireEntryItem() = entryItem ?: error("entryItem must be set")

    private fun requireTagCreateCallback() =
        tagCreateCallback ?: error("Tag create callback must be set")

    private fun requireEntryCallback() =
        vocabEntryCreateCallback ?: error("Create entry callback must be set")
}
