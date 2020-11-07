package com.lukeneedham.vocabdrill.presentation.feature.learn

import android.graphics.Camera
import android.view.animation.Animation
import android.view.animation.Transformation

class RotateZAnimation(
    private val fromDegrees: Float,
    private val toDegrees: Float,
    private val pivotX: Float,
    private val pivotY: Float
) : Animation() {

    private val camera = Camera().apply {
        setLocation(0f, 0f, -20f)
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val degrees = fromDegrees + (toDegrees - fromDegrees) * interpolatedTime
        val matrix = t.matrix
        camera.save()
        camera.rotateY(degrees)
        camera.getMatrix(matrix)
        camera.restore()
        matrix.preTranslate(-pivotX, -pivotY)
        matrix.postTranslate(pivotX, pivotY)
    }
}
