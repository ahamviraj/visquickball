// Copyright (C) 2026 Aham Viraj
// SPDX-License-Identifier: GPL-3.0-only
// This program is licensed under the GNU General Public License version 3 only.
// See the repository LICENSE file for the complete license text.

package com.viraj.viquickball

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

internal data class DashboardState(
    val overlayGranted: Boolean,
    val accessibilityEnabled: Boolean,
    val enabled: Boolean,
    val active: Boolean,
    val feedback: String?,
    val onBeginSetup: () -> Unit,
    val onSkipIntro: () -> Unit,
    val onOpenOverlaySettings: () -> Unit,
    val onOpenAccessibilitySettings: () -> Unit,
    val onStart: () -> Unit,
    val onStop: () -> Unit,
    val onRefresh: () -> Unit,
    val onContinueSetup: () -> Unit,
    val onAbout: () -> Unit,
    val onPrivacyPolicy: () -> Unit
)

/** Native View renderer for the first-run flow and compact Quick Ball dashboard. */
internal class QuickBallSetupUi(private val activity: AppCompatActivity) {
    private val context: Context get() = activity
    private val palette = colors(context)

    fun render(container: FrameLayout, route: QuickBallOnboarding.Route, state: DashboardState, animate: Boolean) {
        val scroll = ScrollView(context).apply {
            isFillViewport = true
            isVerticalScrollBarEnabled = false
            clipToPadding = false
            setBackgroundColor(palette.background)
        }
        val page = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(18), dp(20), dp(30))
        }
        scroll.addView(page, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        container.removeAllViews()
        container.addView(scroll, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        when (route) {
            QuickBallOnboarding.Route.WELCOME -> welcome(page, state)
            QuickBallOnboarding.Route.OVERLAY -> permissionStep(page, state, 1)
            QuickBallOnboarding.Route.ACCESSIBILITY -> permissionStep(page, state, 2)
            QuickBallOnboarding.Route.READY -> ready(page, state)
            QuickBallOnboarding.Route.DASHBOARD -> dashboard(page, state)
        }

        if (animate && android.animation.ValueAnimator.areAnimatorsEnabled()) {
            scroll.alpha = 0.25f
            scroll.translationY = dp(10).toFloat()
            scroll.animate().alpha(1f).translationY(0f).setDuration(190L).start()
        }
    }

    private fun welcome(page: LinearLayout, state: DashboardState) {
        brandHeader(page, "A simpler way to move around")
        addSpace(page, 20)
        card(page) { body ->
            preview(body, 76)
            addSpace(body, 16)
            body.addView(label("Meet Vi's Quick Ball", 28, bold = true))
            addSpace(body, 8)
            body.addView(label(
                "Your favourite navigation actions, right at your fingertips. Use simple gestures on a small floating ball to navigate your phone.",
                16, palette.muted
            ))
            addSpace(body, 16)
            body.addView(pill("FOUR SIMPLE GESTURES", palette.tintSurface, palette.accent))
        }
        addSpace(page, 12)
        primaryButton(page, "Let's Set It Up", state.onBeginSetup)
        textButton(page, "Skip Intro", state.onSkipIntro)
        addSpace(page, 8)
        page.addView(label("Skipping the intro won't grant permissions or turn Quick Ball on.", 13, palette.muted, gravity = Gravity.CENTER))
    }

    private fun permissionStep(page: LinearLayout, state: DashboardState, step: Int) {
        brandHeader(page, "Personalize your setup")
        addSpace(page, 22)
        progress(page, step)
        addSpace(page, 24)
        if (step == 1) {
            page.addView(label("Let Quick Ball Float", 28, bold = true))
            addSpace(page, 8)
            page.addView(label(
                "Quick Ball needs permission to appear above other apps, so you can use it while doing other things.",
                16, palette.muted
            ))
            addSpace(page, 22)
            statusCard(page, "Display over other apps", state.overlayGranted, "Needed to show the floating ball.")
            state.feedback?.let { addFeedback(page, it) }
            addSpace(page, 14)
            primaryButton(page, if (state.overlayGranted) "Continue" else "Grant Overlay Permission") {
                if (state.overlayGranted) state.onContinueSetup() else state.onOpenOverlaySettings()
            }
            textButton(page, "I'll do this later", state.onSkipIntro)
        } else {
            page.addView(label("Enable Navigation Gestures", 28, bold = true))
            addSpace(page, 8)
            page.addView(label(
                "Quick Ball uses Android's Accessibility Service to request Back, Home and Recent Apps when you use its gestures.",
                16, palette.muted
            ))
            addSpace(page, 12)
            page.addView(label(
                "The service also observes window-state metadata to hide the ball over system panels. It is configured not to retrieve window content.",
                14, palette.muted
            ))
            addSpace(page, 22)
            statusCard(page, "Accessibility Service", state.accessibilityEnabled, "Needed for the navigation actions.")
            state.feedback?.let { addFeedback(page, it) }
            addSpace(page, 14)
            primaryButton(page, if (state.accessibilityEnabled) "Continue" else "Open Accessibility Settings") {
                if (state.accessibilityEnabled) state.onContinueSetup() else state.onOpenAccessibilitySettings()
            }
            textButton(page, "I'll do this later", state.onSkipIntro)
        }
        addSpace(page, 10)
        textButton(page, "Refresh permission status", state.onRefresh)
    }

    private fun ready(page: LinearLayout, state: DashboardState) {
        brandHeader(page, "Setup complete")
        addSpace(page, 22)
        progress(page, 3)
        addSpace(page, 24)
        card(page) { body ->
            preview(body, 68)
            addSpace(body, 14)
            body.addView(label("You're All Set!", 28, bold = true, gravity = Gravity.CENTER))
            addSpace(body, 7)
            body.addView(label("Quick Ball is ready. Try a tap, double-tap or long press to get started.", 15, palette.muted, gravity = Gravity.CENTER))
        }
        addSpace(page, 18)
        sectionLabel(page, "YOUR GESTURES")
        addSpace(page, 8)
        gestureGuide(page)
        state.feedback?.let { addFeedback(page, it) }
        addSpace(page, 14)
        primaryButton(page, "Start Quick Ball", state.onStart)
        textButton(page, "Permission status", state.onRefresh)
    }

    private fun dashboard(page: LinearLayout, state: DashboardState) {
        brandHeader(page, "Navigation, within reach")
        addSpace(page, 18)

        card(page) { body ->
            row(body, center = true) { line ->
                preview(line, 70, horizontal = true)
                addSpace(line, 14, horizontal = true)
                val copy = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
                copy.addView(label("Quick Ball", 20, bold = true))
                addSpace(copy, 4)
                val statusText = when {
                    state.active -> "Active"
                    state.enabled -> "Starting"
                    else -> "Not running"
                }
                copy.addView(pill(statusText.uppercase(), if (state.active) palette.tintSurface else palette.background,
                    if (state.active) palette.accent else palette.muted))
                line.addView(copy, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            }
            addSpace(body, 17)
            val missing = !state.overlayGranted || !state.accessibilityEnabled
            when {
                missing -> primaryButton(body, "Complete setup", state.onContinueSetup)
                state.active -> primaryButton(body, "Stop Quick Ball", state.onStop)
                else -> primaryButton(body, if (state.enabled) "Try starting again" else "Start Quick Ball", state.onStart)
            }
            state.feedback?.let { addFeedback(body, it) }
        }

        addSpace(page, 19)
        sectionLabel(page, "PERMISSIONS")
        addSpace(page, 8)
        card(page, 12) { body ->
            statusRow(body, "Display over other apps", state.overlayGranted, state.onOpenOverlaySettings,
                "Allows the ball to float above other apps.")
            divider(body)
            statusRow(body, "Accessibility Service", state.accessibilityEnabled, state.onOpenAccessibilitySettings,
                "Enables Back, Home and Recent Apps gestures.")
        }

        addSpace(page, 19)
        sectionLabel(page, "GESTURE GUIDE")
        addSpace(page, 8)
        card(page, 14) { gestureGuide(it) }

        addSpace(page, 19)
        sectionLabel(page, "MAKE IT YOURS")
        addSpace(page, 8)
        card(page, 16) { body ->
            body.addView(label("Home-screen widget", 16, bold = true))
            addSpace(body, 4)
            body.addView(label("Add Vi's Quick Ball from your launcher's Widgets screen to toggle it from home.", 14, palette.muted))
            addSpace(body, 14)
            body.addView(label("Quick Settings tile", 16, bold = true))
            addSpace(body, 4)
            body.addView(label("Use the Quick Settings edit button and add Quick Ball to your tiles.", 14, palette.muted))
        }

        addSpace(page, 12)
        row(page) { actions ->
            textButton(actions, "Refresh", state.onRefresh, weight = 1f)
            textButton(actions, "About & privacy", state.onAbout, weight = 1f)
        }
        textButton(page, "Privacy Policy", state.onPrivacyPolicy)
    }

    private fun brandHeader(page: LinearLayout, subtitle: String) {
        row(page, center = true) { header ->
            val icon = ImageView(context).apply {
                setImageResource(R.mipmap.ic_launcher_round)
                contentDescription = null
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
            header.addView(icon, LinearLayout.LayoutParams(dp(46), dp(46)))
            addSpace(header, 12, horizontal = true)
            val titles = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
            titles.addView(label("Vi's Quick Ball", 19, bold = true))
            addSpace(titles, 2)
            titles.addView(label(subtitle, 13, palette.muted))
            header.addView(titles, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        }
    }

    private fun progress(page: LinearLayout, current: Int) {
        page.addView(label("STEP $current OF 3", 11, palette.accent, bold = true))
        addSpace(page, 8)
        row(page) { bars ->
            repeat(3) { index ->
                val bar = View(context).apply {
                    background = rounded(if (index < current) palette.accent else palette.outline, 4f)
                    contentDescription = "Setup step ${index + 1}${if (index < current) ", complete" else ""}"
                }
                bars.addView(bar, LinearLayout.LayoutParams(0, dp(4), 1f).apply { if (index < 2) marginEnd = dp(6) })
            }
        }
    }

    private fun preview(parent: LinearLayout, size: Int, horizontal: Boolean = false) {
        val host = FrameLayout(context).apply {
            contentDescription = "Preview of the four-layer gray and white Quick Ball"
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        }
        val ball = QuickBallView(context).apply {
            contentDescription = null
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            isClickable = false
            isFocusable = false
        }
        host.addView(ball, FrameLayout.LayoutParams(dp(size), dp(size), Gravity.CENTER))
        if (horizontal) parent.addView(host, LinearLayout.LayoutParams(dp(size), dp(size)))
        else parent.addView(host, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(size + 8)))
    }

    private fun gestureGuide(parent: LinearLayout) {
        listOf(
            "Tap" to "Back",
            "Double-tap" to "Home",
            "Long press" to "Recent Apps",
            "Drag" to "Move and dock to the nearest edge"
        ).forEachIndexed { index, (gesture, action) ->
            row(parent, center = true) { item ->
                item.addView(label(gesture, 14, bold = true), LinearLayout.LayoutParams(dp(112), ViewGroup.LayoutParams.WRAP_CONTENT))
                item.addView(label(action, 14, palette.muted))
            }
            if (index < 3) {
                addSpace(parent, 8)
                divider(parent)
                addSpace(parent, 8)
            }
        }
    }

    private fun statusCard(parent: LinearLayout, title: String, granted: Boolean, detail: String) {
        card(parent, 14) { body ->
            statusRow(body, title, granted, null, detail)
        }
    }

    private fun statusRow(parent: LinearLayout, title: String, granted: Boolean, action: (() -> Unit)?, detail: String? = null) {
        row(parent, center = true) { line ->
            val indicator = TextView(context).apply {
                text = if (granted) "✓" else "!"
                gravity = Gravity.CENTER
                textSize = 14f
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(if (granted) palette.accent else palette.muted)
                background = rounded(if (granted) palette.tintSurface else palette.background, 50f)
            }
            line.addView(indicator, LinearLayout.LayoutParams(dp(34), dp(34)))
            addSpace(line, 10, horizontal = true)
            val labels = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
            labels.addView(label(title, 14, bold = true))
            addSpace(labels, 2)
            labels.addView(label(detail ?: if (granted) "Granted" else "Required", 12, palette.muted))
            line.addView(labels, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            if (action != null && !granted) {
                textButton(line, "Settings", action, compact = true)
            } else {
                val badge = label(if (granted) "Granted" else "Required", 11,
                    if (granted) palette.accent else palette.muted, bold = true)
                badge.gravity = Gravity.END or Gravity.CENTER_VERTICAL
                line.addView(badge)
            }
        }
    }

    private fun addFeedback(parent: LinearLayout, message: String) {
        addSpace(parent, 10)
        val feedback = TextView(context).apply {
            text = message
            textSize = 13f
            setTextColor(palette.text)
            setPadding(dp(13), dp(11), dp(13), dp(11))
            background = rounded(palette.tintSurface, 14f)
            accessibilityLiveRegion = View.ACCESSIBILITY_LIVE_REGION_POLITE
        }
        parent.addView(feedback, match())
    }

    private fun sectionLabel(parent: LinearLayout, text: String) {
        parent.addView(label(text, 11, palette.muted, bold = true))
    }

    private fun pill(text: String, fill: Int, ink: Int) = TextView(context).apply {
        this.text = text
        textSize = 10f
        letterSpacing = 0.06f
        setTypeface(typeface, Typeface.BOLD)
        setTextColor(ink)
        setPadding(dp(10), dp(6), dp(10), dp(6))
        background = rounded(fill, 50f)
        gravity = Gravity.CENTER
    }

    private fun primaryButton(parent: LinearLayout, text: String, click: () -> Unit) {
        val button = MaterialButton(context).apply {
            this.text = text
            isAllCaps = false
            textSize = 15f
            setTextColor(palette.onAccent)
            backgroundTintList = android.content.res.ColorStateList.valueOf(palette.accent)
            cornerRadius = dp(15)
            insetTop = 0
            insetBottom = 0
            minimumHeight = dp(52)
            contentDescription = text
            setOnClickListener { click() }
        }
        parent.addView(button, match().apply { bottomMargin = dp(4) })
    }

    private fun textButton(
        parent: LinearLayout,
        text: String,
        click: () -> Unit,
        compact: Boolean = false,
        weight: Float = 0f
    ) {
        val button = TextView(context).apply {
            this.text = text
            textSize = if (compact) 12f else 14f
            setTextColor(palette.accent)
            gravity = Gravity.CENTER
            minHeight = dp(48)
            minWidth = dp(48)
            setPadding(dp(8), 0, dp(8), 0)
            isClickable = true
            isFocusable = true
            background = RippleDrawable(
                ColorStateList.valueOf(palette.tintSurface),
                GradientDrawable().apply { setColor(Color.TRANSPARENT); cornerRadius = dp(14).toFloat() },
                GradientDrawable().apply { setColor(Color.WHITE); cornerRadius = dp(14).toFloat() }
            )
            contentDescription = text
            setOnClickListener { click() }
        }
        val params = LinearLayout.LayoutParams(
            if (weight > 0f) 0 else ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            weight
        ).apply {
            gravity = if (weight > 0f) Gravity.CENTER else Gravity.CENTER_HORIZONTAL
            if (!compact) topMargin = dp(2)
        }
        parent.addView(button, params)
    }

    private fun card(parent: LinearLayout, padding: Int = 18, content: (LinearLayout) -> Unit) {
        val shell = MaterialCardView(context).apply {
            radius = dp(20).toFloat()
            setCardBackgroundColor(palette.surface)
            strokeWidth = dp(1)
            strokeColor = palette.outline
            cardElevation = if (palette.dark) 0f else dp(1).toFloat()
            useCompatPadding = false
        }
        val body = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(padding), dp(padding), dp(padding), dp(padding))
        }
        shell.addView(body, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        parent.addView(shell, match().apply { bottomMargin = dp(2) })
        content(body)
    }

    private fun row(parent: LinearLayout, center: Boolean = false, content: (LinearLayout) -> Unit) {
        val line = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = if (center) Gravity.CENTER_VERTICAL else Gravity.CENTER
        }
        parent.addView(line, match())
        content(line)
    }

    private fun divider(parent: LinearLayout) {
        parent.addView(View(context).apply { setBackgroundColor(palette.outline) },
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(1)))
    }

    private fun addSpace(parent: LinearLayout, value: Int, horizontal: Boolean = false) {
        parent.addView(View(context), if (horizontal) LinearLayout.LayoutParams(dp(value), 1)
        else LinearLayout.LayoutParams(1, dp(value)))
    }

    private fun label(text: String, size: Int, color: Int = palette.text, bold: Boolean = false, gravity: Int = Gravity.START) =
        TextView(context).apply {
            this.text = text
            textSize = size.toFloat()
            setTextColor(color)
            if (bold) setTypeface(typeface, Typeface.BOLD)
            this.gravity = gravity
            includeFontPadding = true
            setLineSpacing(dp(2).toFloat(), 1f)
            contentDescription = text
        }

    private fun rounded(color: Int, radiusDp: Float) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radiusDp).toFloat()
    }

    private fun match() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    private fun dp(value: Int) = (value * context.resources.displayMetrics.density + 0.5f).toInt()
    private fun dp(value: Float) = (value * context.resources.displayMetrics.density + 0.5f).toInt()

    companion object {
        fun colors(context: Context): Palette {
            val night = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            return Palette(
                background = ContextCompat.getColor(context, R.color.quick_ball_background),
                surface = ContextCompat.getColor(context, R.color.quick_ball_surface),
                tintSurface = ContextCompat.getColor(context, R.color.quick_ball_tint_surface),
                text = ContextCompat.getColor(context, R.color.quick_ball_text),
                muted = ContextCompat.getColor(context, R.color.quick_ball_muted),
                outline = ContextCompat.getColor(context, R.color.quick_ball_outline),
                accent = ContextCompat.getColor(context, R.color.quick_ball_pink),
                onAccent = if (night) Color.rgb(38, 25, 33) else Color.WHITE,
                dark = night
            )
        }
    }
}

internal data class Palette(
    val background: Int,
    val surface: Int,
    val tintSurface: Int,
    val text: Int,
    val muted: Int,
    val outline: Int,
    val accent: Int,
    val onAccent: Int,
    val dark: Boolean
)
