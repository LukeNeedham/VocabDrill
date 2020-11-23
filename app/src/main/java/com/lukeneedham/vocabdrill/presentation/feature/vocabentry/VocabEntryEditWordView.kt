package com.lukeneedham.vocabdrill.presentation.feature.vocabentry

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import kotlinx.android.synthetic.main.view_vocab_entry_edit_word.view.*

class VocabEntryEditWordView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflateFrom(R.layout.view_vocab_entry_edit_word)
    }

    fun setWord(word: String) {
        textView.text = word
    }
}
