package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create

import android.content.Context
import android.graphics.Rect
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItem
import com.lukeneedham.vocabdrill.presentation.util.BaseTextWatcher
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import com.lukeneedham.vocabdrill.presentation.util.extension.showKeyboard
import kotlinx.android.synthetic.main.view_item_create_vocab_entry.view.*
import kotlinx.android.synthetic.main.view_item_create_vocab_entry.view.tagsRecycler
import kotlinx.android.synthetic.main.view_item_create_vocab_entry.view.wordAInputView
import kotlinx.android.synthetic.main.view_item_create_vocab_entry.view.wordAInputViewLayout
import kotlinx.android.synthetic.main.view_item_create_vocab_entry.view.wordBInputView
import kotlinx.android.synthetic.main.view_item_vocab_entry.view.*
import org.koin.core.KoinComponent

class VocabEntryCreateItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    RecyclerItemView<VocabEntryItem.Create>,
    KoinComponent {

//    private val tagsAdapter = RecyclerAdapterCreator.fromItemTypeConfigs(
//        listOf(
//            ItemTypeConfigCreator.fromRecyclerItemView<Tag, TagView> {
//                addItemLayoutParams(RecyclerView.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
//            }
//        )
//    )

    private var wordATextWatcher: TextWatcher? = null
    private var wordBTextWatcher: TextWatcher? = null

    var callback: VocabEntryCreateCallback? = null

    init {
        inflateFrom(R.layout.view_item_create_vocab_entry)

        tagsRecycler.layoutManager = LinearLayoutManager(context).apply {
            orientation = RecyclerView.HORIZONTAL
        }
        //tagsRecycler.adapter = tagsAdapter
    }

    override fun setItem(position: Int, item: VocabEntryItem.Create) {
        wordAInputView.removeTextChangedListener(wordATextWatcher)
        wordBInputView.removeTextChangedListener(wordBTextWatcher)

        wordAInputView.setText(item.wordA)
        wordBInputView.setText(item.wordB)

        wordATextWatcher = object : BaseTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                requireCallback().onWordAChanged(item, s.toString())
                refreshSaveButtonEnabled()
            }
        }
        wordBTextWatcher = object : BaseTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                requireCallback().onWordBChanged(item, s.toString())
                refreshSaveButtonEnabled()
            }
        }
        wordAInputView.addTextChangedListener(wordATextWatcher)
        wordBInputView.addTextChangedListener(wordBTextWatcher)

        refreshSaveButtonEnabled()

        saveButton.setOnClickListener {
            val wordA = getWordAInput() ?: error("Word A must have input")
            val wordB = getWordBInput() ?: error("Word B must have input")
            val proto = VocabEntryProto(wordA, wordB, item.languageId, item.tags)
            requireCallback().save(proto)

            // Reset
            wordAInputView.setText("")
            wordBInputView.setText("")
            refreshSaveButtonEnabled()
            wordAInputViewLayout.requestFocus()
        }
        //tagsAdapter.submitList(item.tags)
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        wordAInputViewLayout.requestFocus()
        context.showKeyboard()
        return true
    }

    private fun refreshSaveButtonEnabled() {
        saveButton.isEnabled = isSaveButtonEnabled(getWordAInput(), getWordBInput())
    }

    private fun getWordAInput() = wordAInputView.text?.toString()

    private fun getWordBInput() = wordBInputView.text?.toString()

    private fun isSaveButtonEnabled(wordA: String?, wordB: String?): Boolean =
        !wordA.isNullOrBlank() && !wordB.isNullOrBlank()

    private fun requireCallback() = callback ?: error("Callback must be set")
}
