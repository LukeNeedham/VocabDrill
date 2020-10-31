package com.lukeneedham.vocabdrill.usecase

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject

// TODO: Unused - delete?
class ExtractSwatches {
    operator fun invoke(drawable: Drawable): Single<List<Palette.Swatch>> {
        val bitmap = drawable.toBitmap()
        val observable = SingleSubject.create<List<Palette.Swatch>>()
        Palette.from(bitmap).generate { palette ->
            if (palette == null) {
                observable.onError(Exception("Palette could not be initialised"))
            } else {
                observable.onSuccess(palette.swatches)
            }
        }
        return observable
    }
}
