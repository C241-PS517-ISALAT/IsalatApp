package com.isalatapp.ui.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.isalatapp.R

class EmailEditText : AppCompatEditText, View.OnTouchListener {

    private lateinit var clearButtonImage: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init()
    }


    private fun init() {
        clearButtonImage =
            ContextCompat.getDrawable(context, R.drawable.ic_close_black_24dp) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!isValidEmail(s.toString())) {
                    setError(resources.getString(R.string.email_reminder), null)
                } else {
                    error = null
                }
                if (s.toString().isNotEmpty()) showClearButton() else hideClearButton()
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    private fun showClearButton() {
        setButtonDrawables(endOfTheText = clearButtonImage)
    }

    private fun hideClearButton() {
        setButtonDrawables()
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val isClearButtonClicked = isClearButtonClicked(event)
            if (isClearButtonClicked) {
                handleClearButtonClick(event)
                return false
            }
            return false
        }
        return false
    }

    private fun isClearButtonClicked(event: MotionEvent): Boolean {
        val clearButtonStart: Float
        val clearButtonEnd: Float
        return if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            clearButtonEnd = (clearButtonImage.intrinsicWidth + paddingStart).toFloat()
            event.x < clearButtonEnd
        } else {
            clearButtonStart = (width - paddingEnd - clearButtonImage.intrinsicWidth).toFloat()
            event.x > clearButtonStart
        }
    }

    private fun handleClearButtonClick(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> showClearButton()
            MotionEvent.ACTION_UP -> {
                when {
                    text != null -> text?.clear()
                }
                hideClearButton()
            }
        }
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText, topOfTheText, endOfTheText, bottomOfTheText
        )
    }

    private fun isValidEmail(email: String): Boolean {
        val regex = Regex("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")
        return regex.matches(email)
    }
}