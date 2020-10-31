package com.lukeneedham.vocabdrill.presentation.feature.language.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.util.extension.getFlagDrawable
import com.lukeneedham.vocabdrill.presentation.util.extension.popBackStackSafe
import kotlinx.android.synthetic.main.fragment_language_settings.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LanguageSettingsFragment : Fragment(R.layout.fragment_language_settings) {

    private val navArgs: LanguageSettingsFragmentArgs by navArgs()
    private val viewModel: LanguageSettingsViewModel by viewModel { parametersOf(navArgs.languageId) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.nameLiveData.observe(viewLifecycleOwner) {
            titleView.text = it
        }
        viewModel.flagCountryLiveData.observe(viewLifecycleOwner) {
            editFlagIconView.setImageDrawable(it.getFlagDrawable(requireContext()))
        }
        viewModel.stateLiveData.observe(viewLifecycleOwner) {
            val buttonsEnabled = it is LanguageSettingsViewModel.State.Ready
            editNameView.isEnabled = buttonsEnabled
            editFlagView.isEnabled = buttonsEnabled
            deleteView.isEnabled = buttonsEnabled

            if (it is LanguageSettingsViewModel.State.Invalid) {
                popBackStackSafe(R.id.homeFragment, false)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backButton.setOnClickListener {
            popBackStackSafe()
        }
        editNameView.setOnClickListener {
            // TODO
        }
        editFlagView.setOnClickListener {
            // TODO
        }
        deleteView.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.language_delete_confirm_message)
                .setPositiveButton(R.string.language_delete_confirm_yes) { dialog, _ ->
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
