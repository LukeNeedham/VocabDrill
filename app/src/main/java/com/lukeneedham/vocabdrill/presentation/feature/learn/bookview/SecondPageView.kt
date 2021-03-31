package com.lukeneedham.vocabdrill.presentation.feature.learn.bookview

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.feature.learn.FeedbackState
import com.lukeneedham.vocabdrill.presentation.util.BaseAnimatorListener
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import com.lukeneedham.vocabdrill.presentation.util.extension.setOnDoneListener
import com.lukeneedham.vocabdrill.presentation.util.extension.showKeyboard
import kotlinx.android.synthetic.main.view_flip_book_page_second.view.*

class SecondPageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var state: FeedbackState = FeedbackState.Empty

    var onDoneListener: () -> Unit = {}

    init {
        inflateFrom(R.layout.view_flip_book_page_second)
        animateState(FeedbackState.Empty)

        correctAnim.repeatCount = 0
        incorrectAnim.repeatCount = 0

        inputView.setOnDoneListener {
            onDoneListener()
        }
    }

    fun getState(): FeedbackState {
        return state
    }

    fun setOnFeedbackCompletedListener(listener: () -> Unit) {
        correctAnim.addAnimatorListener(object : BaseAnimatorListener() {
            override fun onAnimationEnd(animation: Animator?) {
                listener()
            }
        })
        incorrectAnim.addAnimatorListener(object : BaseAnimatorListener() {
            override fun onAnimationEnd(animation: Animator?) {
                listener()
            }
        })
    }

    fun setStaticState(state: FeedbackState) {
        setState(state, false)
    }

    fun animateState(state: FeedbackState) {
        setState(state, true)
    }

    fun getInput() = inputView.text.toString()

    fun setInputText(text: String) {
        inputView.setText(text)
    }

    fun setInputEnabled(isEnabled: Boolean) {
        inputView.isEnabled = isEnabled
    }

    private fun setState(state: FeedbackState, animate: Boolean) {
        this.state = state

        inputLayout.visibility = View.GONE
        correctLayout.visibility = View.GONE
        incorrectLayout.visibility = View.GONE

        val exhaustive: Any = when (state) {
            is FeedbackState.Empty -> {
            }
            is FeedbackState.AcceptingInput -> {
                inputLayout.visibility = View.VISIBLE
                inputView.setText("")
                inputView.requestFocus()
                context.showKeyboard()
            }
            is FeedbackState.Correct -> {
                correctLayout.visibility = View.VISIBLE
                if(animate) {
                    correctAnim.playAnimation()
                } else {
                    correctAnim.progress = 1f
                }
            }
            is FeedbackState.Incorrect -> {
                incorrectLayout.visibility = View.VISIBLE
                if(animate) {
                    incorrectAnim.playAnimation()
                } else {
                    incorrectAnim.progress = 1f
                }
            }
        }
    }
}
