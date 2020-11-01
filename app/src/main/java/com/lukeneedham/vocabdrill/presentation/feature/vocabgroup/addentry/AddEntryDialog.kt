package com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.addentry

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.util.BaseBottomSheetDialogFragment
import com.lukeneedham.vocabdrill.util.extension.TAG
import kotlinx.android.synthetic.main.dialog_add_entry.*
import kotlinx.android.synthetic.main.dialog_add_language.closeButton
import kotlinx.android.synthetic.main.dialog_add_language.confirmButton
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AddEntryDialog : BaseBottomSheetDialogFragment() {
    override val layoutResId = R.layout.dialog_add_entry

    private val viewModel: AddEntryViewModel by viewModel {
        val vocabGroupId = requireArguments().getLong(ARG_VOCAB_GROUP)
        parametersOf(vocabGroupId)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.isValidPairLiveData.observe(viewLifecycleOwner) {
            confirmButton.isEnabled = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        wordAInputView.doOnTextChanged { text: CharSequence?, _, _, _ ->
            viewModel.onWordAChange(text.toString())
        }
        wordBInputView.doOnTextChanged { text: CharSequence?, _, _, _ ->
            viewModel.onWordBChange(text.toString())
        }

        confirmButton.setOnClickListener {
            viewModel.createNewEntry()
            dismiss()
        }

        closeButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        private const val ARG_VOCAB_GROUP = "ARG_VOCAB_GROUP"

        fun newInstance(vocabGroupId: Long) = AddEntryDialog().apply {
            arguments = Bundle().apply {
                putLong(ARG_VOCAB_GROUP, vocabGroupId)
            }
        }
    }
}
