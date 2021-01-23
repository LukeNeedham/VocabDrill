package com.lukeneedham.vocabdrill.presentation.util.extension

import android.content.Context
import android.content.SharedPreferences
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService

fun Context.showKeyboard() {
    val imm = getSystemService<InputMethodManager>() ?: return
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Context.getSharedPreferences(name: String): SharedPreferences =
    getSharedPreferences(name, Context.MODE_PRIVATE)
