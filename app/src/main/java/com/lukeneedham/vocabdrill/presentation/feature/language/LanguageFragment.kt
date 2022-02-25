package com.lukeneedham.vocabdrill.presentation.feature.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.lukeneedham.vocabdrill.domain.model.LearnSet
import com.lukeneedham.vocabdrill.presentation.navigation.LanguageRoot
import com.lukeneedham.vocabdrill.presentation.util.extension.navigateSafe
import com.lukeneedham.vocabdrill.presentation.util.extension.popBackStackSafe

class LanguageFragment : Fragment() {
    private val navArgs: LanguageFragmentArgs by navArgs()
    private val languageId by lazy { navArgs.languageId }

    @ExperimentalAnimationApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            LanguageRoot(languageId, ::openLearnPage) { popBackStackSafe() }
        }
    }

    private fun openLearnPage(learnSet: LearnSet) {
        navigateSafe(
            LanguageFragmentDirections.actionLanguageFragmentToLearnFragment(
                learnSet
            )
        )
    }
}

