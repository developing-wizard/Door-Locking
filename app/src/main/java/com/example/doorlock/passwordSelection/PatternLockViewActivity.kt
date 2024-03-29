package com.example.doorlock.passwordSelection

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min
import kotlin.math.pow

class PatternLockViewActivity(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val dotRadius = 25f
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dotTouchRadius = 60f
    private var isDrawing = false

    private val dots = mutableListOf<Dot>()
    private val selectedDots = mutableListOf<Int>()

    init {
        linePaint.color = Color.WHITE
        linePaint.strokeWidth = 5f
        dotPaint.color = Color.WHITE
    }

    interface OnPatternCompleteListener {
        fun onPatternComplete(pattern: List<Int>)
    }

    private var onPatternCompleteListener: OnPatternCompleteListener? = null

    fun setOnPatternCompleteListener(listener: OnPatternCompleteListener) {
        onPatternCompleteListener = listener
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        val centerX = width / 2
        val centerY = height / 2

        val smallerDimen = min(width, height)
        val gap = smallerDimen / 3

        dots.clear()
        for (i in 0..2) {
            for (j in 0..2) {
                val x = centerX - gap + j * gap
                val y = centerY - gap + i * gap
                dots.add(Dot(x, y))
                canvas.drawCircle(x, y, dotRadius, dotPaint)
            }
        }

        if (isDrawing && selectedDots.isNotEmpty()) {
            var lastDot: Dot? = null

            for (index in selectedDots) {
                val dot = dots[index]
                if (lastDot != null) {
                    canvas.drawLine(lastDot.x, lastDot.y, dot.x, dot.y, linePaint)
                }
                lastDot = dot
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val currentX = event.x
        val currentY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrawing = true
                selectedDots.clear()
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDrawing) {
                    val touchedDot = findTouchedDot(currentX, currentY)
                    if (touchedDot != null && !selectedDots.contains(touchedDot)) {
                        selectedDots.add(touchedDot)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                isDrawing = false
                if (selectedDots.size == selectedDots.size && selectedDots.containsAll(selectedDots)) {
                    onPatternCompleteListener?.onPatternComplete(selectedDots)
                } else {
                    onPatternCompleteListener?.onPatternComplete(selectedDots)
                }
                selectedDots.clear()
            }
        }

        invalidate()
        return true
    }

    private fun findTouchedDot(x: Float, y: Float): Int? {
        val centerX = width / 2
        val centerY = height / 2
        val smallerDimen = min(width, height)
        val gap = smallerDimen / 3

        for (i in 0..2) {
            for (j in 0..2) {
                val dotX = centerX - gap + j * gap
                val dotY = centerY - gap + i * gap
                val distance = Math.sqrt((x - dotX).toDouble().pow(2.0) + (y - dotY).toDouble().pow(2.0))
                if (distance <= dotTouchRadius) {
                    return i * 3 + j
                }
            }
        }
        return null
    }
    private inner class Dot(val x: Float, val y: Float)
}
