package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.EditText
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.flowerpotrecycler.RecyclerAdapterCreator
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.flowerpotrecycler.adapter.delegates.feature.config.FeatureConfig
import com.lukeneedham.flowerpotrecycler.adapter.itemtype.builderbinder.implementation.view.RecyclerItemViewBuilderBinder
import com.lukeneedham.flowerpotrecycler.util.ItemTypeConfigCreator
import com.lukeneedham.flowerpotrecycler.util.extensions.addItemLayoutParams
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagCreateCallback
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagCreateView
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagExistingView
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.FocusItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.InteractionSection
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.ViewMode
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItemPresentationData
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
    RecyclerItemView<VocabEntryItemPresentationData.Create>,
    KoinComponent {

    private val tagsAdapter = RecyclerAdapterCreator.fromItemTypeConfigs(
        listOf(
            ItemTypeConfigCreator.fromBuilderBinder(
                RecyclerItemViewBuilderBinder.newInstance {
                    TagCreateView(context).apply {
                        callback = tagCreateCallback
                    }
                },
                FeatureConfig<TagItem.Create, TagCreateView>().apply {
                    addItemLayoutParams(RecyclerView.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
                }
            ),
            ItemTypeConfigCreator.fromRecyclerItemView<TagItem.Existing, TagExistingView> {
                addItemLayoutParams(RecyclerView.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
            }
        )
    )

    private var wordATextWatcher: TextWatcher? = null
    private var wordBTextWatcher: TextWatcher? = null

    var vocabEntryCreateCallback: VocabEntryCreateCallback? = null
    var tagCreateCallback: TagCreateCallback? = null

    init {
        inflateFrom(R.layout.view_item_create_vocab_entry)

        tagsRecycler.layoutManager = LinearLayoutManager(context).apply {
            orientation = RecyclerView.HORIZONTAL
        }
        tagsRecycler.adapter = tagsAdapter
    }

    override fun setItem(position: Int, item: VocabEntryItemPresentationData.Create) {
        setupTextWatchers(item)
        setupTextFocus(item)
        refreshSaveButtonEnabled()

        saveButton.setOnClickListener {
            val wordA = getWordAInput() ?: error("Word A must have input")
            val wordB = getWordBInput() ?: error("Word B must have input")
            val tags = tagsAdapter.positionDelegate.getItems()
                .filterIsInstance<TagItem.Existing>()
                .map { it.data }
            val proto = VocabEntryProto(wordA, wordB, item.data.languageId, tags)
            requireCallback().save(proto)
        }
        val existingTagItems = item.data.tags.map {
            TagItem.Existing(item.data, it)
        }
        val createTagItem = TagItem.Create(item.data, "")
        val allTagItems = existingTagItems + createTagItem
        tagsAdapter.submitList(allTagItems)
    }

    private fun setupTextWatchers(item: VocabEntryItemPresentationData.Create) {
        wordAInputView.removeTextChangedListener(wordATextWatcher)
        wordBInputView.removeTextChangedListener(wordBTextWatcher)

        wordAInputView.setText(item.data.wordA)
        wordBInputView.setText(item.data.wordB)

        wordATextWatcher = object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                requireCallback().onWordAChanged(s.toString(), wordAInputView.getSelection())
            }
        }
        wordBTextWatcher = object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                requireCallback().onWordBChanged(s.toString(), wordBInputView.getSelection())
            }
        }
        wordAInputView.addTextChangedListener(wordATextWatcher)
        wordBInputView.addTextChangedListener(wordBTextWatcher)
    }

    private fun setupTextFocus(item: VocabEntryItemPresentationData.Create) {
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
                requireCallback().onInteraction(
                    InteractionSection.WordAInput,
                    wordAInputView.getSelection()
                )
            }
        }
        wordBInputView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                requireCallback().onInteraction(
                    InteractionSection.WordBInput,
                    wordBInputView.getSelection()
                )
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

    private fun requireCallback() = vocabEntryCreateCallback ?: error("Callback must be set")
}
