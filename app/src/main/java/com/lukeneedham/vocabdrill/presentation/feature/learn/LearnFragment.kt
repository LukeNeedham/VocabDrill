package com.lukeneedham.vocabdrill.presentation.feature.learn

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.util.extension.popBackStackSafe
import com.lukeneedham.vocabdrill.presentation.util.extension.setOnDoneListener
import kotlinx.android.synthetic.main.fragment_learn.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LearnFragment : Fragment(R.layout.fragment_learn) {

    private val navArgs: LearnFragmentArgs by navArgs()
    private val viewModel: LearnViewModel by viewModel { parametersOf(navArgs.learnBook) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.bookStateLiveData.observe(viewLifecycleOwner) {
            textInputView.setText("")

            val exhaustive: Any = when (it) {
                is BookState.Finished -> {
                    // TODO: Handle finished state
                    popBackStackSafe()
                }
                is BookState.Page -> {
                    flipBookView.post {
                        flipBookView.showPage(it.contents)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        closeButton.setOnClickListener {
            popBackStackSafe()
        }

        textInputView.setOnDoneListener {
            submitInput()
        }

        submitButton.setOnClickListener {
            submitInput()
        }

        val colourScheme = viewModel.colourScheme
        flipBookView.setPaperColour(colourScheme.mainColour)
        flipBookView.setTextColour(colourScheme.textColour)
        flipBookView.setBorderColour(colourScheme.borderColour)
    }

    private fun submitInput() {
        viewModel.onInput(textInputView.text.toString())
    }
}
