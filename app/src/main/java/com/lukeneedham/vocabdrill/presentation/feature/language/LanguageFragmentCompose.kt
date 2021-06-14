package com.lukeneedham.vocabdrill.presentation.feature.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.feature.language.entries.VocabEntries
import com.lukeneedham.vocabdrill.presentation.util.extension.getFlagDrawableId
import com.lukeneedham.vocabdrill.presentation.util.extension.navigateSafe
import com.lukeneedham.vocabdrill.presentation.util.extension.popBackStackSafe
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LanguageFragmentCompose : Fragment() {
    private val navArgs: LanguageFragmentComposeArgs by navArgs()
    private val languageId by lazy { navArgs.languageId }

    private val viewModel: LanguageViewModel by viewModel { parametersOf(languageId) }

    @ExperimentalAnimationApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            val country by viewModel.countryLiveData.observeAsState()
            val languageName by viewModel.languageNameLiveData.observeAsState()

            Column {
                Row(
                    modifier = Modifier
                        .height(60.dp)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back button",
                        modifier = Modifier.clickable {
                            popBackStackSafe()
                        }
                    )
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val country = country
                        val languageName = languageName
                        if (country != null && languageName != null) {
                            val flagId = country.getFlagDrawableId(requireContext())

                            Image(
                                painter = painterResource(id = flagId),
                                contentDescription = "The flag for $languageName",
                            )
                            Text(text = languageName)
                        }
                    }
                    Image(
                        painter = painterResource(id = R.drawable.ic_learn),
                        contentDescription = "Learn button",
                        modifier = Modifier.clickable {
                            val learnSet = viewModel.getLearnSet()
                            navigateSafe(
                                LanguageFragmentComposeDirections.actionLanguageFragmentToLearnFragment(
                                    learnSet
                                )
                            )
                        }
                    )
                }
                VocabEntries(languageId)
                FloatingActionButton(onClick = ::onAddEntryClicked) {

                }
            }
        }
    }

    private fun onAddEntryClicked() {

    }
}
