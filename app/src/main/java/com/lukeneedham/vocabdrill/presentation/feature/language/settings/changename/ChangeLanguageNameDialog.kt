package com.lukeneedham.vocabdrill.presentation.feature.language.settings.changename

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.util.BaseBottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_change_language_name.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ChangeLanguageNameDialog : BaseBottomSheetDialogFragment() {
    private val viewModel: ChangeLanguageNameViewModel by viewModel {
        val languageId = requireArguments().getLong(ARG_LANGUAGE)
        parametersOf(languageId)
    }

    override val layoutResId = R.layout.dialog_change_language_name

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.isValidNameLiveData.observe(viewLifecycleOwner) {
            confirmButton.isEnabled = it
        }
        viewModel.languageLiveData.observe(viewLifecycleOwner) {
            textInputView.setText(it.name)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        textInputView.doOnTextChanged { text: CharSequence?, _, _, _ ->
            viewModel.onNameChange(text.toString())
        }

        confirmButton.setOnClickListener {
            viewModel.updateLanguage()
            dismiss()
        }

        closeButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        private const val ARG_LANGUAGE = "ARG_LANGUAGE"

        fun newInstance(languageId: Long) = ChangeLanguageNameDialog().apply {
            arguments = Bundle().apply {
                putLong(ARG_LANGUAGE, languageId)
            }
        }
    }
}
