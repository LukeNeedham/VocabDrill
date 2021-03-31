package com.lukeneedham.vocabdrill.presentation.feature.learn

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.util.DefaultAnimationListener
import com.lukeneedham.vocabdrill.presentation.util.extension.hideKeyboard
import com.lukeneedham.vocabdrill.presentation.util.extension.popBackStackSafe
import kotlinx.android.synthetic.main.fragment_learn.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LearnFragment : Fragment(R.layout.fragment_learn) {

    private val navArgs: LearnFragmentArgs by navArgs()
    private val viewModel: LearnViewModel by viewModel { parametersOf(navArgs.learnSet) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.bookStateLiveData.observe(viewLifecycleOwner) {
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
//            textInputViewLayout.endIconDrawable =
//                ContextCompat.getDrawable(requireContext(), it.inputIconRes)

            flipBookView.setFeedbackState(it)

//            when (it) {
//                is FeedbackState.Correct -> {
//                    giveCorrectFeedback()
//                }
//                is FeedbackState.Incorrect -> {
//                    giveIncorrectFeedback()
//                }
//            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        closeButton.setOnClickListener {
            popBackStackSafe()
        }

        submitButton.setOnClickListener {
            submitInput()
        }

        //textInputViewLayout.requestFocus()
        //showKeyboard()

//        textInputViewLayout.setEndIconOnClickListener {
//            submitInput()
//        }

        // TODO: Better color setup
        flipBookView.setPaperColour(Color.WHITE)
        flipBookView.setTextColour(Color.BLACK)
        flipBookView.setBorderColour(Color.BLACK)
        flipBookView.setPageTurnAnimationListener(object : DefaultAnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                onPageTurnAnimationCompleted()
            }
        })
        flipBookView.setOnFeedbackCompletedListener {
            viewModel.onFeedbackCompleted()
        }
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    private fun submitInput() {
        val feedbackState = viewModel.feedbackStateLiveData.value
        if (feedbackState != FeedbackState.AcceptingInput) {
            return
        }

        val input = flipBookView.getInput()
        viewModel.onInput(input)
    }

    private fun giveCorrectFeedback(state: FeedbackState.Correct) {
//        flipBookView.setInputEnabled(false)
//        val animation = AnimationUtils.loadAnimation(context, R.anim.pulse)
//        animation.setAnimationListener(object : DefaultAnimationListener {
//            override fun onAnimationEnd(animation: Animation?) {
//                requireView().postDelayed(CORRECT_FEEDBACK_END_WAIT) {
//                    flipBookView.setInputText("")
//                    flipBookView.setInputEnabled(true)
//                }
//            }
//        })
        //textInputViewLayout.startAnimation(animation)
    }

    private fun giveIncorrectFeedback() {
//        val animation = AnimationUtils.loadAnimation(context, R.anim.shake)
//        animation.setAnimationListener(object : DefaultAnimationListener {
//            override fun onAnimationEnd(animation: Animation?) {
//                requireView().postDelayed(INCORRECT_FEEDBACK_END_WAIT) {
//                    viewModel.onFeedbackGiven()
//                }
//            }
//        })
//        textInputViewLayout.startAnimation(animation)
    }

    private fun onPageTurnAnimationCompleted() {
        viewModel.onCurrentPageVisible()
    }

    companion object {
        const val CORRECT_FEEDBACK_END_WAIT = 100L
        const val INCORRECT_FEEDBACK_END_WAIT = 200L
    }
}
