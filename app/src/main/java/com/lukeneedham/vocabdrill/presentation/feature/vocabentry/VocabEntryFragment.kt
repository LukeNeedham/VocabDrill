package com.lukeneedham.vocabdrill.presentation.feature.vocabentry

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.SettingsState
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.changeword.ChangeVocabEntryWordDialog
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.changeword.WordType
import com.lukeneedham.vocabdrill.presentation.util.extension.popBackStackSafe
import com.lukeneedham.vocabdrill.presentation.util.extension.showDialog
import kotlinx.android.synthetic.main.fragment_vocab_entry.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class VocabEntryFragment : Fragment(R.layout.fragment_vocab_entry) {
    private val navArgs: VocabEntryFragmentArgs by navArgs()
    private val viewModel: VocabEntryViewModel by viewModel { parametersOf(navArgs.vocabEntryId) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.languageNameLiveData.observe(viewLifecycleOwner) {
            languageTitleView.text = it
        }
        viewModel.vocabGroupNameLiveData.observe(viewLifecycleOwner) {
            groupTitleView.text = it
        }
        viewModel.wordALiveData.observe(viewLifecycleOwner) {
            editWordAView.setWord(it)
        }
        viewModel.wordBLiveData.observe(viewLifecycleOwner) {
            editWordBView.setWord(it)
        }
        viewModel.stateLiveData.observe(viewLifecycleOwner) {
            val buttonsEnabled = it is SettingsState.Ready
            editWordAView.isEnabled = buttonsEnabled
            editWordBView.isEnabled = buttonsEnabled
            deleteView.isEnabled = buttonsEnabled

            if (it is SettingsState.Invalid) {
                popBackStackSafe()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backButton.setOnClickListener {
            popBackStackSafe()
        }
        editWordAView.setOnClickListener {
            showDialog(
                ChangeVocabEntryWordDialog.newInstance(viewModel.vocabEntryId, WordType.WordA)
            )
        }
        editWordBView.setOnClickListener {
            showDialog(
                ChangeVocabEntryWordDialog.newInstance(viewModel.vocabEntryId, WordType.WordB)
            )
        }

        deleteView.setText(R.string.vocab_entry_delete)
        deleteView.setIcon(R.drawable.ic_delete)
        deleteView.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.vocab_entry_delete_confirm_message)
                .setPositiveButton(R.string.vocab_entry_delete_confirm_yes) { dialog, _ ->
                    viewModel.onDelete()
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}
