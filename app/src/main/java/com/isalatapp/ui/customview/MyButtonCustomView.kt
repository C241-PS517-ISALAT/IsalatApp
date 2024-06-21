package com.isalatapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import com.isalatapp.R

class MyButtonCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    private var enabledBackground: Drawable? = null
    private var disabledBackground: Drawable? = null

    init {
        enabledBackground =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_button_enable, null)
        disabledBackground =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_button_disable, null)

        background = enabledBackground
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isEnabled) enabledBackground else disabledBackground
    }
}
