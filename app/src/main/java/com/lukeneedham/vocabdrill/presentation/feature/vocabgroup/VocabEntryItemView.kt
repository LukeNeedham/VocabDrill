package com.lukeneedham.vocabdrill.presentation.feature.vocabgroup

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.VocabEntryRelations
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import com.sdsmdg.harjot.vectormaster.VectorMasterDrawable
import kotlinx.android.synthetic.main.view_item_vocab_entry.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class VocabEntryItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    RecyclerItemView<VocabEntryRelations>,
    KoinComponent {

    private val viewModel: VocabEntryItemViewModel by inject()

    init {
        inflateFrom(R.layout.view_item_vocab_entry)
    }

    override fun setItem(position: Int, item: VocabEntryRelations) {

        val backgroundColor = ContextCompat.getColor(context, R.color.background)
        val colorScheme = viewModel.getColourScheme(item.vocabGroup.colour, backgroundColor)

        val entryDrawable = VectorMasterDrawable(context, R.drawable.background_entry)

        val sideAPath = entryDrawable.getPathModelByName("side_a")
        sideAPath.fillColor = colorScheme.sideAColor
        sideAPath.strokeColor = colorScheme.borderColor

        val sideBPath = entryDrawable.getPathModelByName("side_b")
        sideBPath.fillColor = colorScheme.sideBColor
        sideBPath.strokeColor = colorScheme.borderColor

        backgroundImageView.setImageDrawable(entryDrawable)

        wordViewA.setTextColor(colorScheme.sideATextColor)
        wordViewB.setTextColor(colorScheme.sideBTextColor)

        wordViewA.text = item.vocabEntry.wordA
        wordViewB.text = item.vocabEntry.wordB
    }
}
