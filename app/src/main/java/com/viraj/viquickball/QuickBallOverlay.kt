// Copyright (C) 2026 Aham Viraj
// SPDX-License-Identifier: GPL-3.0-only
// This program is licensed under the GNU General Public License version 3 only.
// See the repository LICENSE file for the complete license text.

package com.viraj.viquickball

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.RequiresApi
import kotlin.math.roundToInt
import kotlin.math.max
import kotlin.math.min

internal class QuickBallOverlay(
    private val context: Context,
    private val action: (QuickBallGestureHandler.Action) -> Unit
) {
    companion object { private const val TAG = "ViQuickBall" }

    private val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var view: QuickBallView? = null
    private var params: WindowManager.LayoutParams? = null
    private var gesture: QuickBallGestureHandler? = null
    private var bounds = DockBounds(0, 0, 0, 0)
    private var downX = 0f
    private var downY = 0f
    private var originX = 0
    private var originY = 0
    private var blockTouchUntilUp = false
    private var dockAnimator: ValueAnimator? = null
    private val size get() = (QuickBallView.DIAMETER_DP * context.resources.displayMetrics.density).roundToInt()

    @Suppress("DEPRECATION")
    fun show(): Boolean {
        if (!QuickBallPreferences.overlayAllowed(context)) return false
        view?.let { existing ->
            if (existing.isAttachedToWindow) { existing.visibility = View.VISIBLE; return true }
            disposeView(existing)
        }
        bounds = safeBounds()
        val ball = QuickBallView(context).apply {
            contentDescription = "Quick Ball: tap Back, double tap Home, hold for Recent Apps, drag to dock to an edge"
        }
        val point = QuickBallDocking.point(QuickBallPreferences.dockPosition(context), size, bounds)
        val p = WindowManager.LayoutParams(
            size, size,
            if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.LEFT
            x = point.x; y = point.y
        }
        params = p
        val handler = QuickBallGestureHandler(ball, action, { event ->
            val next = QuickBallDocking.clamp(
                originX + (event.rawX - downX).roundToInt(),
                originY + (event.rawY - downY).roundToInt(), size, bounds
            )
            p.x = next.x; p.y = next.y
            runCatching { wm.updateViewLayout(ball, p) }.onFailure { Log.w(TAG, "Unable to move overlay", it) }
        }, { snapToNearestEdge(animate = true) }, ball::setPressedVisual)
        ball.setOnTouchListener { v, event ->
            if (blockTouchUntilUp) {
                if (event.actionMasked == android.view.MotionEvent.ACTION_UP || event.actionMasked == android.view.MotionEvent.ACTION_CANCEL) blockTouchUntilUp = false
                return@setOnTouchListener true
            }
            if (event.actionMasked == android.view.MotionEvent.ACTION_DOWN) {
                if (dockAnimator?.isRunning == true) { blockTouchUntilUp = true; return@setOnTouchListener true }
                originX = p.x; originY = p.y; downX = event.rawX; downY = event.rawY
            }
            handler.onTouch(v, event)
        }
        gesture = handler
        return try {
            wm.addView(ball, p); view = ball; true
        } catch (failure: Exception) {
            Log.e(TAG, "Unable to add overlay", failure)
            handler.dispose(); gesture = null; params = null; false
        }
    }

    fun setTemporarilyVisible(visible: Boolean) {
        val ball = view ?: return
        if (!visible) {
            blockTouchUntilUp = false
            val wasDragging = gesture?.cancelForVisibilityChange() == true
            dockAnimator?.cancel(); dockAnimator = null
            ball.resetPressAnimation()
            if (wasDragging) snapToNearestEdge(animate = false)
            ball.visibility = View.GONE
        } else if (QuickBallPreferences.overlayAllowed(context) && ball.isAttachedToWindow) {
            bounds = safeBounds()
            val point = QuickBallDocking.point(QuickBallPreferences.dockPosition(context), size, bounds)
            params?.let { it.x = point.x; it.y = point.y; runCatching { wm.updateViewLayout(ball, it) } }
            ball.visibility = View.VISIBLE
        }
    }

    fun cancelGestureState() {
        val wasDragging = gesture?.cancelForVisibilityChange() == true
        dockAnimator?.cancel(); dockAnimator = null
        view?.resetPressAnimation()
        if (wasDragging && view?.isAttachedToWindow == true) snapToNearestEdge(animate = false)
    }

    /** Called on explicit disable or service shutdown; releases the window and callbacks. */
    fun remove() {
        dockAnimator?.cancel(); dockAnimator = null
        blockTouchUntilUp = false
        gesture?.dispose(); gesture = null
        view?.let(::disposeView)
        view = null; params = null
    }

    fun isVisible() = view?.let { it.isAttachedToWindow && it.visibility == View.VISIBLE } == true
    fun isAttached() = view?.isAttachedToWindow == true

    fun onDisplayChanged() {
        bounds = safeBounds()
        val p = params ?: return
        val point = QuickBallDocking.point(QuickBallPreferences.dockPosition(context), size, bounds)
        p.x = point.x; p.y = point.y
        view?.let { runCatching { wm.updateViewLayout(it, p) }.onFailure { e -> Log.w(TAG, "Unable to reposition overlay", e) } }
    }

    private fun snapToNearestEdge(animate: Boolean) {
        val p = params ?: return
        val ball = view ?: return
        bounds = safeBounds()
        val docked = QuickBallDocking.nearest(p.x, p.y, size, bounds)
        QuickBallPreferences.saveDockPosition(context, docked.position)
        dockAnimator?.cancel(); dockAnimator = null
        if (!animate || !ball.isAttachedToWindow) {
            p.x = docked.point.x; p.y = docked.point.y
            runCatching { wm.updateViewLayout(ball, p) }.onFailure { Log.w(TAG, "Unable to snap overlay", it) }
            return
        }
        val fromX = p.x; val fromY = p.y
        dockAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 190L
            interpolator = android.view.animation.DecelerateInterpolator()
            addUpdateListener { value ->
                val fraction = value.animatedValue as Float
                p.x = (fromX + (docked.point.x - fromX) * fraction).roundToInt()
                p.y = (fromY + (docked.point.y - fromY) * fraction).roundToInt()
                runCatching { wm.updateViewLayout(ball, p) }.onFailure { Log.w(TAG, "Unable to animate dock", it) }
            }
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    if (dockAnimator === animation) dockAnimator = null
                }
            })
            start()
        }
    }

    private fun disposeView(ball: QuickBallView) {
        ball.dispose()
        runCatching { wm.removeView(ball) }.onFailure { Log.w(TAG, "Unable to remove overlay", it) }
    }

    @Suppress("DEPRECATION")
    private fun safeBounds(): DockBounds {
        if (Build.VERSION.SDK_INT >= 30) {
            val metrics = wm.currentWindowMetrics
            val rect = metrics.bounds
            val insets = metrics.windowInsets.getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
            return DockBounds(rect.left + insets.left, rect.top + insets.top, rect.right - insets.right, rect.bottom - insets.bottom)
        }
        val real = DisplayMetrics()
        wm.defaultDisplay.getRealMetrics(real)
        val resources = context.resources
        fun bar(name: String): Int {
            val id = resources.getIdentifier(name, "dimen", "android")
            return if (id != 0) resources.getDimensionPixelSize(id) else 0
        }
        val status = bar("status_bar_height")
        val landscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val navBottom = if (landscape) 0 else bar("navigation_bar_height")
        val navRight = if (landscape) bar("navigation_bar_width") else 0
        val cutoutInsets = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) cutoutSafeInsets() else IntArray(4)
        return DockBounds(
            max(0, cutoutInsets[0]), max(status, cutoutInsets[1]),
            min(real.widthPixels - navRight, real.widthPixels - cutoutInsets[2]),
            min(real.heightPixels - navBottom, real.heightPixels - cutoutInsets[3])
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @Suppress("DEPRECATION")
    private fun cutoutSafeInsets(): IntArray {
        val cutout = wm.defaultDisplay.cutout ?: return IntArray(4)
        return intArrayOf(cutout.safeInsetLeft, cutout.safeInsetTop, cutout.safeInsetRight, cutout.safeInsetBottom)
    }
}
