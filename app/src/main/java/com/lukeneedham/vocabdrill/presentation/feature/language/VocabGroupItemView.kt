package com.lukeneedham.vocabdrill.presentation.feature.language

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
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
        val coverColour = item.vocabGroup.colour
        recolourBook(coverColour)

        val name = item.vocabGroup.name
        nameView.maxLines = if (" " in name) Int.MAX_VALUE else 1
        nameView.text = name
        val textColour = viewModel.getTextColor(coverColour)
        nameView.setTextColor(textColour)
        detailsView.text = context.getString(R.string.vocab_group_details, item.entries.size)
        detailsView.setTextColor(textColour)

        detailsBorderDrawable.setStroke(
            context.resources.getDimensionPixelSize(R.dimen.vocab_group_description_border_width),
            textColour
        )
        detailsView.background = detailsBorderDrawable
    }

    private fun recolourBook(coverColour: Int) {
        val backgroundBook = VectorMasterDrawable(context, R.drawable.background_book)

        val lineColour = viewModel.getOutlineColour(coverColour)
        val bookCoverPath = backgroundBook.getPathModelByName("book_cover")
        bookCoverPath.fillColor = coverColour
        bookCoverPath.strokeColor = lineColour

        val bookPagesPath = backgroundBook.getPathModelByName("book_pages")
        bookPagesPath.strokeColor = lineColour

        backgroundView.setImageDrawable(backgroundBook)
    }
}
