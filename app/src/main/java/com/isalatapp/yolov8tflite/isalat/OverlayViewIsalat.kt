package com.isalatapp.yolov8tflite.isalat

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.isalatapp.yolov8tflite.BoundingBox

class OverlayViewIsalat(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f
        color = Color.RED
    }
    private val textPaint = Paint().apply {
        color = Color.RED
        textSize = 64f
    }
    private var results: List<BoundingBox> = emptyList()

    fun setResults(results: List<BoundingBox>) {
        this.results = results
    }

    fun clear() {
        results = emptyList()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        results.forEach { result ->
            val left = result.x1 * width
            val top = result.y1 * height
            val right = result.x2 * width
            val bottom = result.y2 * height
            canvas.drawRect(left, top, right, bottom, paint)
            canvas.drawText(result.clsName, left, top, textPaint)
        }
    }
}



