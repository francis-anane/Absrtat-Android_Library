package com.abstratsystems.abstratlibrary

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager

object AbstratUIBehaviour {
    fun showSoftKeyboard(view: View, context: Context){
        // Show the soft keyboard
        val inputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)

    }
}