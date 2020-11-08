package com.lukeneedham.vocabdrill.presentation.feature.language

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.VocabGroupColourScheme
import com.lukeneedham.vocabdrill.domain.model.VocabGroupRelations
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import com.sdsmdg.harjot.vectormaster.VectorMasterDrawable
import group.infotech.drawable.dsl.shapeDrawable
import kotlinx.android.synthetic.main.view_item_vocab_group.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class VocabGroupItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), RecyclerItemView<VocabGroupRelations>,
    KoinComponent {

    private val viewModel: VocabGroupItemViewModel by inject()

    private val detailsBorderDrawable = shapeDrawable {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = context.resources.getDimension(R.dimen.vocab_group_description_border_radius)
    }

    init {
        inflateFrom(R.layout.view_item_vocab_group)
    }

    override fun setItem(position: Int, item: VocabGroupRelations) {
        val colourScheme = viewModel.getColourScheme(item.vocabGroup)
        recolourBook(colourScheme)

        val name = item.vocabGroup.name
        nameView.maxLines = if (" " in name) Int.MAX_VALUE else 1
        nameView.text = name
        val textColour = colourScheme.textColour
        nameView.setTextColor(textColour)
        val entriesCount =  item.entries.size
        detailsView.text =
            resources.getQuantityString(R.plurals.vocab_group_details, entriesCount, entriesCount)
        detailsView.setTextColor(textColour)

        detailsBorderDrawable.setStroke(
            context.resources.getDimensionPixelSize(R.dimen.vocab_group_description_border_width),
            textColour
        )
        detailsView.background = detailsBorderDrawable
    }

    private fun recolourBook(colourScheme: VocabGroupColourScheme) {
        val backgroundBook = VectorMasterDrawable(context, R.drawable.background_book)

        val lineColour = colourScheme.borderColour
        val bookCoverPath = backgroundBook.getPathModelByName("book_cover")
        bookCoverPath.fillColor = colourScheme.mainColour
        bookCoverPath.strokeColor = lineColour

        val bookPagesPath = backgroundBook.getPathModelByName("book_pages")
        bookPagesPath.strokeColor = lineColour

        backgroundView.setImageDrawable(backgroundBook)
    }
}
