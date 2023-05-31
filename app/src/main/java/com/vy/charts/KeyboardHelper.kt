package com.vy.charts


import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService


class KeyboardHelper {
    companion object {
        fun show(context: Context, view: View) {
            context.getSystemService<InputMethodManager>()
                ?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            view.requestFocus()
        }

        fun hide(context: Context, view: View) {
            context.getSystemService<InputMethodManager>()
                ?.hideSoftInputFromWindow(view.applicationWindowToken, 0)
        }
    }
}
