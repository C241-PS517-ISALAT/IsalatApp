package com.isalatapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import android.text.method.PasswordTransformationMethod
import com.isalatapp.R


class MyEditTextCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var clearButtonImage: Drawable? = null
    private var minLength: Int = 0

    fun updateMinLength(length: Int) {
        minLength = length
        if ((text?.length ?: 0) < minLength) {
            error = context.getString(R.string.password_caracter, minLength)
        }
    }

    init {
        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_close_black_24dp)
        setOnTouchListener(this)
        transformationMethod = PasswordTransformationMethod.getInstance()
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length >= minLength) {
                    showClearButton()
                    error = null
                } else {
                    hideClearButton()
                    error = "Password must be at least $minLength characters"
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Input Password"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun showClearButton() {
        setButtonDrawables(endOfTheText = clearButtonImage)
    }

    private fun hideClearButton() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (paddingStart + (clearButtonImage?.intrinsicWidth ?: 0)).toFloat()
                isClearButtonClicked = event.x < clearButtonEnd
            } else {
                clearButtonStart = (width - paddingEnd - (clearButtonImage?.intrinsicWidth ?: 0)).toFloat()
                isClearButtonClicked = event.x > clearButtonStart
            }

            if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_close_black_24dp)
                        showClearButton()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_close_black_24dp)
                        if (!text.isNullOrEmpty()) text?.clear()
                        hideClearButton()
                        return true
                    }
                    else -> return false
                }
            }
        }
        return false
    }
}
