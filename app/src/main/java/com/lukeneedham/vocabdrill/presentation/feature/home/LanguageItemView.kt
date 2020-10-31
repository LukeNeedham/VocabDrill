package com.lukeneedham.vocabdrill.presentation.feature.home

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lukeneedham.flowerpotrecycler.adapter.RecyclerItemView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.Language
import com.lukeneedham.vocabdrill.presentation.util.extension.getFlagDrawable
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import kotlinx.android.synthetic.main.view_item_language.view.*

class LanguageItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), RecyclerItemView<Language> {
    init {
        inflateFrom(R.layout.view_item_language)
    }

    override fun setItem(position: Int, item: Language) {
        nameView.text = item.name
        imageView.setImageDrawable(item.country.getFlagDrawable(context))
    }
}
