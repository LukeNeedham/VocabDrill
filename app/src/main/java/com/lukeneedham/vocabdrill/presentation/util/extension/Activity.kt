package com.lukeneedham.vocabdrill.presentation.util.extension

import android.app.Activity
import android.graphics.Point
import android.graphics.Rect
import android.view.View

fun View.getLocationInDisplayFrame(activity: Activity): Point {
    val outLocation = IntArray(2)
    getLocationInWindow(outLocation)

    val rectangle = Rect()
    activity.window.decorView.getWindowVisibleDisplayFrame(rectangle)
    val statusBarHeight = rectangle.top

    val x = outLocation[0]
    val y = outLocation[1] - statusBarHeight
    return Point(x, y)
}
