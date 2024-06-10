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


class PwdEditText : AppCompatEditText, View.OnTouchListener {

    private lateinit var passwordButtonImage: Drawable
    private var isPasswordVisible = false

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
        setOnTouchListener(this)
        passwordButtonImage = ContextCompat.getDrawable(
            context,
            if (!isPasswordVisible) com.google.android.material.R.drawable.design_ic_visibility else com.google.android.material.R.drawable.design_ic_visibility_off
        ) as Drawable

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().length < 8) {
                    setError(resources.getString(R.string.minimum_characters), null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })

    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val isPasswordButtonClicked = isPasswordButtonClicked(event.x)
            if (isPasswordButtonClicked && event.action == MotionEvent.ACTION_DOWN) {
                return true
            }
        }
        return false
    }

    private fun isPasswordButtonClicked(x: Float): Boolean {
        val passwordButtonStart: Float
        val passwordButtonEnd: Float

        return if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            passwordButtonEnd = (passwordButtonImage.intrinsicWidth + paddingStart).toFloat()
            x < passwordButtonEnd
        } else {
            passwordButtonStart =
                (width - paddingEnd - passwordButtonImage.intrinsicWidth).toFloat()
            x > passwordButtonStart
        }
    }

}
