package com.lukeneedham.vocabdrill.presentation.util

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.reactivex.disposables.CompositeDisposable

/** Base class for compound views which subscribe to Rx. Subscribe in [onAttachedToWindow] */
abstract class DisposingFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    protected val disposables = CompositeDisposable()

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposables.dispose()
    }
}
