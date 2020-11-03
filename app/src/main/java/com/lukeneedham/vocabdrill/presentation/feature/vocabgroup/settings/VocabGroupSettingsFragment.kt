package com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.settings

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.settings.changecolour.ChangeVocabGroupColourDialog
import com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.settings.changename.ChangeVocabGroupNameDialog
import com.lukeneedham.vocabdrill.presentation.util.extension.popBackStackSafe
import com.lukeneedham.vocabdrill.presentation.util.extension.showDialog
import group.infotech.drawable.dsl.shapeDrawable
import group.infotech.drawable.dsl.solidColor
import kotlinx.android.synthetic.main.fragment_vocab_group_settings.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class VocabGroupSettingsFragment : Fragment(R.layout.fragment_vocab_group_settings) {

    private val navArgs: VocabGroupSettingsFragmentArgs by navArgs()
    private val viewModel: VocabGroupSettingsViewModel by viewModel { parametersOf(navArgs.vocabGroupId) }

    private val colourIconShape = shapeDrawable {
        shape = GradientDrawable.OVAL
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.nameLiveData.observe(viewLifecycleOwner) {
            titleView.text = it
        }
        viewModel.colourLiveData.observe(viewLifecycleOwner) {
            colourIconShape.solidColor = it
            editColourView.setIcon(colourIconShape)
        }
        viewModel.stateLiveData.observe(viewLifecycleOwner) {
            val buttonsEnabled = it is VocabGroupSettingsViewModel.State.Ready
            editNameView.isEnabled = buttonsEnabled
            editColourView.isEnabled = buttonsEnabled
            deleteView.isEnabled = buttonsEnabled

            if (it is VocabGroupSettingsViewModel.State.Invalid) {
                popBackStackSafe(R.id.languageFragment, false)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backButton.setOnClickListener {
            popBackStackSafe()
        }

        editNameView.setText(R.string.vocab_group_setting_edit_name)
        editNameView.setIcon(R.drawable.ic_edit)
        editNameView.setOnClickListener {
            showDialog(ChangeVocabGroupNameDialog.newInstance(viewModel.vocabGroupId))
        }

        editColourView.setText(R.string.vocab_group_setting_edit_colour)
        editColourView.setOnClickListener {
            showDialog(ChangeVocabGroupColourDialog.newInstance(viewModel.vocabGroupId))
        }

        deleteView.setText(R.string.vocab_group_setting_delete)
        deleteView.setIcon(R.drawable.ic_delete)
        deleteView.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.vocab_group_delete_confirm_message)
                .setPositiveButton(R.string.vocab_group_delete_confirm_yes) { dialog, _ ->
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
