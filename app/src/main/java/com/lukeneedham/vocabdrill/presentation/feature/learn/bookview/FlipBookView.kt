package com.lukeneedham.vocabdrill.presentation.feature.learn.bookview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.feature.learn.FeedbackState
import com.lukeneedham.vocabdrill.presentation.feature.learn.PageContents
import com.lukeneedham.vocabdrill.presentation.feature.learn.RotateZAnimation
import com.lukeneedham.vocabdrill.presentation.util.DefaultAnimationListener
import com.lukeneedham.vocabdrill.presentation.util.extension.inflateFrom
import kotlinx.android.synthetic.main.view_flip_book.view.*

/**
 * A view for flipping through a book.
 * The bounds of this view are for the book in its static state.
 * When animating the page turn, the page will grow to exceed its bounds.
 * For this reason, you may want to set both [ViewGroup.setClipChildren]
 * and [ViewGroup.setClipToPadding] to false in the parent [ViewGroup],
 * and ensure there is enough room around the view to fit the animating bounds.
 * Otherwise, the animating View will be clipped.
 */
class FlipBookView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    /** The duration for each half of the animation */
    private var animationDuration = DEFAULT_ANIMATION_DURATION

    private var currentPageContents: PageContents? = null

    private var turnFirstPageAnimation: Animation? = null
    private var turnSecondPageAnimation: Animation? = null

    private var pageTurnAnimationListener: Animation.AnimationListener? = null

    init {
        inflateFrom(R.layout.view_flip_book)
        clipChildren = false
        clipToPadding = false
    }

    fun getInput() = secondPage.getInput()

    fun setFeedbackState(state: FeedbackState) {
        secondPage.animateState(state)
    }

    /**
     * @param halfDuration the duration for each half of the animation.
     * So, total duration is 2 * halfDuration
     */
    fun setAnimationDuration(halfDuration: Long) {
        animationDuration = halfDuration
    }

    fun setPageTurnAnimationListener(listener: Animation.AnimationListener) {
        pageTurnAnimationListener = listener
    }

    fun setOnFeedbackCompletedListener(listener: () -> Unit) {
        secondPage.setOnFeedbackCompletedListener(listener)
    }

    // TODO: Do we want to keep these?

    fun setPaperColour(colour: Int) {
    }

    fun setBorderColour(colour: Int) {
    }

    fun setTextColour(colour: Int) {
        firstPage.setTextColor(colour)
        firstPageMoving.setTextColor(colour)
    }

    /** Animates to the next page, with contents defined by [nextPage] */
    fun showPage(nextPage: PageContents) {
        turnFirstPageAnimation?.setAnimationListener(null)
        turnFirstPageAnimation?.cancel()
        turnSecondPageAnimation?.setAnimationListener(null)
        turnSecondPageAnimation?.cancel()

        secondPageMoving.setStaticState(secondPage.getState())
        secondPage.animateState(FeedbackState.Empty)

        firstPage.setText(currentPageContents?.firstPageText)
        firstPageMoving.setText(nextPage.firstPageText)

        firstPage.visibility = if (currentPageContents == null) View.INVISIBLE else View.VISIBLE
        firstPageMoving.visibility = View.INVISIBLE
        secondPage.visibility = View.VISIBLE
        secondPageMoving.visibility = View.INVISIBLE

        currentPageContents = nextPage

        val centerY = secondPage.height / 2f

        // Second half of animation - turn first page
        val turnFirstPageAnimation =
            RotateZAnimation(90f, 0f, firstPageMoving.width.toFloat(), centerY).apply {
                duration = DEFAULT_ANIMATION_DURATION
                interpolator = DecelerateInterpolator()
                setAnimationListener(object : DefaultAnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        firstPageMoving.visibility = View.VISIBLE
                        secondPageMoving.visibility = View.INVISIBLE
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        firstPage.setText(nextPage.firstPageText)
                        firstPage.visibility = View.VISIBLE
                        firstPageMoving.visibility = View.INVISIBLE
                        pageTurnAnimationListener?.onAnimationEnd(animation)
                    }
                })
            }
        this.turnFirstPageAnimation = turnFirstPageAnimation

        // First half of animation - turn second page
        val turnSecondPageAnimation = RotateZAnimation(0f, -90f, 0f, centerY).apply {
            duration = DEFAULT_ANIMATION_DURATION
            interpolator = AccelerateInterpolator()
            setAnimationListener(object : DefaultAnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    secondPageMoving.visibility = View.VISIBLE
                    pageTurnAnimationListener?.onAnimationStart(animation)
                }

                override fun onAnimationEnd(animation: Animation?) {
                    firstPageMoving.startAnimation(turnFirstPageAnimation)
                }
            })
        }
        this.turnSecondPageAnimation = turnSecondPageAnimation
        secondPageMoving.startAnimation(turnSecondPageAnimation)
    }

    companion object {
        const val DEFAULT_ANIMATION_DURATION = 500L
    }
}
