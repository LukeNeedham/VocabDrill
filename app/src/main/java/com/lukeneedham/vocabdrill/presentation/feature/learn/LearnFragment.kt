package com.lukeneedham.vocabdrill.presentation.feature.learn

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.util.DefaultAnimationListener
import com.lukeneedham.vocabdrill.presentation.util.extension.hideKeyboard
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
        viewModel.feedbackStateLiveData.observe(viewLifecycleOwner) {
            textInputViewLayout.endIconDrawable =
                ContextCompat.getDrawable(requireContext(), it.inputIconRes)

            when (it) {
                FeedbackState.Incorrect -> {
                    textInputView.setText("")
                    giveIncorrectFeedback()
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
        textInputView.requestFocus()

        textInputViewLayout.setEndIconOnClickListener {
            submitInput()
        }

        val colourScheme = viewModel.colourScheme
        flipBookView.setPaperColour(colourScheme.mainColour)
        flipBookView.setTextColour(colourScheme.textColour)
        flipBookView.setBorderColour(colourScheme.borderColour)
        flipBookView.setPageTurnAnimationListener(object : DefaultAnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                onPageTurnAnimationCompleted()
            }
        })
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    private fun submitInput() {
        val feedbackState = viewModel.feedbackStateLiveData.value
        if (feedbackState != FeedbackState.Ready) {
            return
        }

        val input = textInputView.text.toString()
        viewModel.onInput(input)
    }

    private fun giveIncorrectFeedback() {
        val animation = AnimationUtils.loadAnimation(context, R.anim.shake)
        animation.setAnimationListener(object : DefaultAnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                requireView().postDelayed(INCORRECT_FEEDBACK_END_WAIT) {
                    viewModel.onFeedbackGiven()
                }
            }
        })
        textInputViewLayout.startAnimation(animation)
    }

    private fun onPageTurnAnimationCompleted() {
        viewModel.onFeedbackGiven()
    }

    companion object {
        const val INCORRECT_FEEDBACK_END_WAIT = 200L
    }
}
