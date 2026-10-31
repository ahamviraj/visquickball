package com.viraj.viquickball

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import android.view.animation.DecelerateInterpolator

/** Four crisp concentric circles; only the white center changes radius on press. */
internal class QuickBallView(context: Context) : View(context) {
    companion object {
        const val DIAMETER_DP = 52
        private const val PRESS_MS = 100L
        private const val RELEASE_MS = 140L
        private const val PRESSED_INNER_SCALE = 1.16f
    }

    private val colors = intArrayOf(0xFF424242.toInt(), 0xFF888888.toInt(), 0xFFBDBDBD.toInt(), 0xFFFFFFFF.toInt())
    // Radii are fractions of the available outer radius, from outside to inside.
    private val radiusFractions = floatArrayOf(1f, 0.75f, 0.50f, 0.25f)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private var innerScale = 1f
    private var animator: ValueAnimator? = null

    init { setLayerType(LAYER_TYPE_HARDWARE, null) }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val outerRadius = minOf(width, height) / 2f - 0.5f * resources.displayMetrics.density
        for (i in colors.indices) {
            paint.color = colors[i]
            val scale = if (i == colors.lastIndex) innerScale else 1f
            canvas.drawCircle(cx, cy, outerRadius * radiusFractions[i] * scale, paint)
        }
    }

    fun setPressedVisual(pressed: Boolean) {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(innerScale, if (pressed) PRESSED_INNER_SCALE else 1f).apply {
            duration = if (pressed) PRESS_MS else RELEASE_MS
            interpolator = DecelerateInterpolator()
            addUpdateListener { innerScale = it.animatedValue as Float; invalidate() }
            start()
        }
    }

    fun resetPressAnimation() {
        animator?.cancel(); animator = null; innerScale = 1f; invalidate()
    }

    fun dispose() = resetPressAnimation()
}
