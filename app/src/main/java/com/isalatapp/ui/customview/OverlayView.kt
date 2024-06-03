package com.isalatapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class OverlayView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }
    private var boundingBox: RectF? = null
    private var confidence: Float = 0f

    fun updateBoundingBox(box: RectF, confidence: Float) {
        this.boundingBox = box
        this.confidence = confidence
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        boundingBox?.let {
            canvas.drawRect(it, paint)
            canvas.drawText("Stop ${confidence * 100}%", it.left, it.top - 10, paint)
        }
    }
}
