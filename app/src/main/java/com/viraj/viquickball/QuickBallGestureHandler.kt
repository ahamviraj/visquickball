package com.viraj.viquickball

import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import kotlin.math.abs

/** Resolves taps only after the double-tap window; long press and movement cancel them. */
internal class QuickBallGestureHandler(
    view: View,
    private val onAction: (Action) -> Unit,
    private val onDrag: (MotionEvent) -> Unit,
    private val onDragEnd: () -> Unit,
    private val onPressedChanged: (Boolean) -> Unit = {}
) : View.OnTouchListener {
    enum class Action { BACK, HOME, RECENTS }
    private val handler = Handler(Looper.getMainLooper())
    private val touchSlop = ViewConfiguration.get(view.context).scaledTouchSlop
    private val longPressMs = ViewConfiguration.getLongPressTimeout().toLong()
    private val doubleTapMs = ViewConfiguration.getDoubleTapTimeout().toLong()
    private var downX = 0f
    private var downY = 0f
    private var dragging = false
    private var longPressed = false
    private var activePointer = -1
    private var pendingTap: Runnable? = null
    private val longPress = Runnable {
        if (!dragging && activePointer != -1) {
            longPressed = true
            cancelTap()
            onAction(Action.RECENTS)
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.actionMasked != MotionEvent.ACTION_DOWN && activePointer == -1) return true
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                activePointer = event.getPointerId(0); downX = event.rawX; downY = event.rawY
                dragging = false; longPressed = false
                onPressedChanged(true)
                handler.removeCallbacks(longPress)
                handler.postDelayed(longPress, longPressMs)
            }
            MotionEvent.ACTION_POINTER_UP -> if (event.getPointerId(event.actionIndex) == activePointer) {
                cancelGesture(); onPressedChanged(false)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - downX; val dy = event.rawY - downY
                if (!dragging && (abs(dx) > touchSlop || abs(dy) > touchSlop)) {
                    dragging = true; handler.removeCallbacks(longPress); cancelTap()
                }
                if (dragging) { downX = event.rawX; downY = event.rawY; onDrag(event) }
            }
            MotionEvent.ACTION_UP -> {
                handler.removeCallbacks(longPress)
                if (dragging) onDragEnd()
                onPressedChanged(false)
                if (!dragging && !longPressed) {
                    if (pendingTap != null) { cancelTap(); onAction(Action.HOME) }
                    else {
                        val task = Runnable { pendingTap = null; onAction(Action.BACK) }
                        pendingTap = task; handler.postDelayed(task, doubleTapMs)
                    }
                }
                dragging = false
                activePointer = -1
            }
            MotionEvent.ACTION_CANCEL -> { cancelGesture(); onPressedChanged(false) }
        }
        return true
    }

    fun dispose() { handler.removeCallbacks(longPress); cancelTap(); activePointer = -1; onPressedChanged(false) }
    fun cancelForVisibilityChange(): Boolean {
        val wasDragging = dragging
        handler.removeCallbacks(longPress); cancelTap(); activePointer = -1
        dragging = false; longPressed = false; onPressedChanged(false)
        return wasDragging
    }
    private fun cancelTap() { pendingTap?.let(handler::removeCallbacks); pendingTap = null }
    private fun cancelGesture() { handler.removeCallbacks(longPress); cancelTap(); if (dragging) onDragEnd(); dragging = false; activePointer = -1 }
}
