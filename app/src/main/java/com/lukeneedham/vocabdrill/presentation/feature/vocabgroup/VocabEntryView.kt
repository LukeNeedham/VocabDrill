package com.lukeneedham.vocabdrill.presentation.feature.vocabgroup

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.VocabEntry
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import kotlinx.android.synthetic.main.view_item_vocab_entry.view.*

class VocabEntryView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), RecyclerItemView<VocabEntry> {

    init {
        inflateFrom(R.layout.view_item_vocab_entry)
    }

    override fun setItem(position: Int, item: VocabEntry) {
        wordAView.text = item.wordA
        wordBView.text = item.wordB
    }
}
