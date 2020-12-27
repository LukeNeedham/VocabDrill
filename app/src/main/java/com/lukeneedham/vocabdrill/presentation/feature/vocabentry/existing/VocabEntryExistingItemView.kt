package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.setHintEnabledMaintainMargin
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItem
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import kotlinx.android.synthetic.main.view_item_vocab_entry.view.*

class VocabEntryExistingItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    RecyclerItemView<VocabEntryItem.Existing> {

    // TODO: Extract into custom adapter, for re-use
//    private val tagsAdapter = RecyclerAdapterCreator.fromItemTypeConfigs(
//        listOf(
//            ItemTypeConfigCreator.fromRecyclerItemView<Tag, TagView> {
//                addItemLayoutParams(RecyclerView.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
//            }
//        )
//    )

    private var mode: ViewMode = ViewMode.Compressed

    var callback: VocabEntryExistingCallback? = null

    init {
        inflateFrom(R.layout.view_item_vocab_entry)
        tagsRecycler.layoutManager = LinearLayoutManager(context).apply {
            orientation = RecyclerView.HORIZONTAL
        }
        wordAInputViewLayout.setHintEnabledMaintainMargin(false)
        wordAInputViewLayout.isEnabled = false
        wordBInputViewLayout.setHintEnabledMaintainMargin(false)
        wordBInputViewLayout.isEnabled = false

        //tagsRecycler.adapter = tagsAdapter

        topLayer.setOnClickListener {
            toggleMode()
        }
        chevronIconView.setOnClickListener {
            toggleMode()
        }
    }

    override fun setItem(position: Int, item: VocabEntryItem.Existing) {
        setMode(ViewMode.Compressed)

        val data = item.data
        //tagsAdapter.submitList(data.tags)
        val entry = data.vocabEntry
        wordAInputView.setText(entry.wordA)
        wordBInputView.setText(entry.wordB)

        deleteButton.setOnClickListener {
            requireCallback().onDelete(item)
        }
    }

    private fun requireCallback() = callback ?: error("Callback must be set")

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
    }
}
