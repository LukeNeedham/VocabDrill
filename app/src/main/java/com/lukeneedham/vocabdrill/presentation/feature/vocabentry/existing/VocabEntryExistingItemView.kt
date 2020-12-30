package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.setHintEnabledMaintainMargin
import com.lukeneedham.flowerpotrecycler.RecyclerAdapterCreator
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.flowerpotrecycler.adapter.delegates.feature.config.FeatureConfig
import com.lukeneedham.flowerpotrecycler.adapter.itemtype.builderbinder.implementation.view.RecyclerItemViewBuilderBinder
import com.lukeneedham.flowerpotrecycler.util.ItemTypeConfigCreator
import com.lukeneedham.flowerpotrecycler.util.extensions.addItemLayoutParams
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagCreateCallback
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagCreateView
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagExistingView
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.FocusItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.ViewMode
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItemPresentationData
import com.lukeneedham.vocabdrill.presentation.util.BaseTextWatcher
import com.lukeneedham.vocabdrill.presentation.util.TextSelection
import com.lukeneedham.vocabdrill.presentation.util.extension.getSelection
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import com.lukeneedham.vocabdrill.presentation.util.extension.setSelection
import kotlinx.android.synthetic.main.view_item_vocab_entry.view.*

class VocabEntryExistingItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    RecyclerItemView<VocabEntryItemPresentationData.Existing> {

    // TODO: Extract into custom adapter, for re-use
    private val tagsAdapter = RecyclerAdapterCreator.fromItemTypeConfigs(
        listOf(
            ItemTypeConfigCreator.fromBuilderBinder(
                RecyclerItemViewBuilderBinder.newInstance {
                    TagCreateView(context).apply {
                        callback = tagCreateCallback
                    }
                },
                FeatureConfig<TagItem.Create, TagCreateView>().apply {
                    addItemLayoutParams(
                        RecyclerView.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    )
                }
            ),
            ItemTypeConfigCreator.fromRecyclerItemView<TagItem.Existing, TagExistingView> {
                addItemLayoutParams(
                    RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
            }
        )
    )

    private var wordATextWatcher: TextWatcher? = null
    private var wordBTextWatcher: TextWatcher? = null

    var vocabEntryExistingCallback: VocabEntryExistingCallback? = null
    var tagCreateCallback: TagCreateCallback? = null

    init {
        inflateFrom(R.layout.view_item_vocab_entry)
        tagsRecycler.layoutManager = LinearLayoutManager(context).apply {
            orientation = RecyclerView.HORIZONTAL
        }
        wordAInputViewLayout.setHintEnabledMaintainMargin(false)
        wordBInputViewLayout.setHintEnabledMaintainMargin(false)

        tagsRecycler.adapter = tagsAdapter
    }

    override fun setItem(position: Int, item: VocabEntryItemPresentationData.Existing) {
        setupTextWatchers(item)
        setupTextFocus(item)

        tagsAdapter.submitList(item.data.tags.map { TagItem.Existing(item.data, it) })

        deleteButton.setOnClickListener {
            requireCallback().onDelete(item)
        }

        backgroundView.setOnClickListener { flipMode(item) }
        chevronIconView.setOnClickListener { flipMode(item) }
    }

    private fun setupTextWatchers(item: VocabEntryItemPresentationData.Existing) {
        wordAInputView.removeTextChangedListener(wordATextWatcher)
        wordBInputView.removeTextChangedListener(wordBTextWatcher)

        wordAInputView.setText(item.data.wordA)
        wordBInputView.setText(item.data.wordB)

        wordATextWatcher = object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                requireCallback().onWordAChanged(item, s.toString(), wordAInputView.getSelection())
            }
        }
        wordBTextWatcher = object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                requireCallback().onWordBChanged(item, s.toString(), wordBInputView.getSelection())
            }
        }
        wordAInputView.addTextChangedListener(wordATextWatcher)
        wordBInputView.addTextChangedListener(wordBTextWatcher)
    }

    private fun setupTextFocus(item: VocabEntryItemPresentationData.Existing) {
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
                    requireCallback().onViewModeChanged(
                        item,
                        ViewMode.Active(FocusItem.WordA(wordAInputView.getSelection()))
                    )
                }
            }
        }
        wordBInputView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                wordBInputView.post {
                    requireCallback().onViewModeChanged(
                        item,
                        // selection is incorrect. Maybe needs a post?
                        ViewMode.Active(FocusItem.WordB(wordBInputView.getSelection()))
                    )
                }
            }
        }
    }

    private fun flipMode(item: VocabEntryItemPresentationData.Existing) {
        val viewMode = item.viewMode
        val newMode = getFlippedMode(viewMode)
        requireCallback().onViewModeChanged(item, newMode)
    }

    private fun requireCallback() = vocabEntryExistingCallback ?: error("Callback must be set")

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

    private fun setMode(mode: ViewMode) {
        val isExpanded = mode is ViewMode.Active
        deleteButton.visibility = if (isExpanded) View.VISIBLE else View.GONE
        val chevronResId = if (isExpanded) R.drawable.ic_chevron_up else R.drawable.ic_chevron_down
        chevronIconView.setImageResource(chevronResId)

        val textInputEnabled = isExpanded
        //wordAInputViewLayout.isEnabled = textInputEnabled
        //wordBInputViewLayout.isEnabled = textInputEnabled
    }
}
