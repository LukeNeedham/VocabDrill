package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
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
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItem
import com.lukeneedham.vocabdrill.presentation.util.BaseTextWatcher
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import kotlinx.android.synthetic.main.view_item_vocab_entry.view.*

class VocabEntryExistingItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    RecyclerItemView<VocabEntryItem.Existing> {

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
                    addItemLayoutParams(RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ))
                }
            ),
            ItemTypeConfigCreator.fromRecyclerItemView<TagItem.Existing, TagExistingView> {
                addItemLayoutParams(RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ))
            }
        )
    )

    private var mode: ViewMode = ViewMode.Compressed
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

        topLayer.setOnClickListener {
            toggleMode()
        }
        chevronIconView.setOnClickListener {
            toggleMode()
        }

        setMode(ViewMode.Compressed)
    }

    override fun setItem(position: Int, item: VocabEntryItem.Existing) {
        setMode(ViewMode.Compressed)

        wordAInputView.removeTextChangedListener(wordATextWatcher)
        wordBInputView.removeTextChangedListener(wordBTextWatcher)

        wordAInputView.setText(item.wordA)
        wordBInputView.setText(item.wordB)

        wordATextWatcher = object : BaseTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                requireCallback().onWordAChanged(item, s.toString())
            }
        }
        wordBTextWatcher = object : BaseTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                requireCallback().onWordBChanged(item, s.toString())
            }
        }
        wordAInputView.addTextChangedListener(wordATextWatcher)
        wordBInputView.addTextChangedListener(wordBTextWatcher)

        tagsAdapter.submitList(item.tags.map { TagItem.Existing(item, it) })

        deleteButton.setOnClickListener {
            requireCallback().onDelete(item)
        }
    }

    private fun requireCallback() = vocabEntryExistingCallback ?: error("Callback must be set")

    private fun toggleMode() {
        val mode = when (mode) {
            ViewMode.Compressed -> ViewMode.Expanded
            ViewMode.Expanded -> ViewMode.Compressed
        }
        setMode(mode)
    }

    private fun setMode(mode: ViewMode) {
        this.mode = mode
        val isExpanded = mode == ViewMode.Expanded
        deleteButton.visibility = if (isExpanded) View.VISIBLE else View.GONE
        val chevronResId = if (isExpanded) R.drawable.ic_chevron_up else R.drawable.ic_chevron_down
        chevronIconView.setImageResource(chevronResId)
        // This view intercepts all clicks when in compressed view
        topLayer.visibility = if(isExpanded) View.GONE else View.VISIBLE

        val textInputEnabled = isExpanded
        wordAInputViewLayout.isEnabled = textInputEnabled
        wordBInputViewLayout.isEnabled = textInputEnabled
    }
}
