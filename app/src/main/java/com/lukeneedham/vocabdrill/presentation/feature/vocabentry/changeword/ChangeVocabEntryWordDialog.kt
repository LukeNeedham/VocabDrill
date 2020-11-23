package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.changeword

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.util.BaseBottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_change_vocab_entry_word.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ChangeVocabEntryWordDialog : BaseBottomSheetDialogFragment() {
    private val viewModel: ChangeVocabEntryWordViewModel by viewModel {
        val vocabGroupId = requireArguments().getLong(ARG_VOCAB_ENTRY_ID)
        val wordType = requireArguments().getSerializable(ARG_WORD_TYPE) as WordType
        parametersOf(vocabGroupId, wordType)
    }

    override val layoutResId = R.layout.dialog_change_vocab_entry_word

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.wordLiveData.observe(viewLifecycleOwner) {
            textInputView.setText(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        textInputView.doOnTextChanged { text: CharSequence?, _, _, _ ->
            viewModel.onWordChange(text.toString())
        }

        confirmButton.setOnClickListener {
            viewModel.updateWord()
            dismiss()
        }

        closeButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        private const val ARG_VOCAB_ENTRY_ID = "ARG_VOCAB_ENTRY_ID"
        private const val ARG_WORD_TYPE = "ARG_WORD_TYPE"

        fun newInstance(vocabGroupId: Long, wordType: WordType) = ChangeVocabEntryWordDialog().apply {
            arguments = Bundle().apply {
                putLong(ARG_VOCAB_ENTRY_ID, vocabGroupId)
                putSerializable(ARG_WORD_TYPE, wordType)
            }
        }
    }
}
