package com.viraj.viquickball

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var screen: FrameLayout
    private lateinit var ui: QuickBallSetupUi
    private var pendingSettingsRoute: QuickBallOnboarding.Route? = null
    private var feedback: String? = null
    private var startAttempt = 0
    private var firstResume = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.app_name)
        configureEdgeToEdge()
        screen = FrameLayout(this).apply {
            setBackgroundColor(QuickBallSetupUi.colors(this@MainActivity).background)
        }
        ViewCompat.setOnApplyWindowInsetsListener(screen) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        setContentView(screen)
        ViewCompat.requestApplyInsets(screen)
        ui = QuickBallSetupUi(this)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (QuickBallOnboarding.resolve(
                    QuickBallPreferences.hasSeenIntro(this@MainActivity),
                    QuickBallPreferences.isOnboardingInProgress(this@MainActivity),
                    QuickBallPreferences.overlayAllowed(this@MainActivity),
                    QuickBallPreferences.accessibilityEnabled(this@MainActivity)
                )) {
                    QuickBallOnboarding.Route.OVERLAY -> show(QuickBallOnboarding.Route.WELCOME)
                    QuickBallOnboarding.Route.ACCESSIBILITY -> show(QuickBallOnboarding.Route.OVERLAY)
                    QuickBallOnboarding.Route.READY -> show(QuickBallOnboarding.Route.ACCESSIBILITY)
                    else -> finish()
                }
            }
        })

        val route = QuickBallOnboarding.resolve(
            QuickBallPreferences.hasSeenIntro(this),
            QuickBallPreferences.isOnboardingInProgress(this),
            QuickBallPreferences.overlayAllowed(this),
            QuickBallPreferences.accessibilityEnabled(this)
        )
        show(route, animate = false)
    }

    override fun onResume() {
        super.onResume()
        if (!::ui.isInitialized) return
        QuickBallAccessibilityService.setSetupActivityVisible(true)
        QuickBallAccessibilityService.refresh(this)
        QuickBallController.refreshControls(this)
        if (firstResume) {
            firstResume = false
            return
        }

        val awaited = pendingSettingsRoute
        pendingSettingsRoute = null
        val route = if (QuickBallPreferences.isOnboardingInProgress(this)) {
            QuickBallOnboarding.resolve(
                hasSeenIntro = true,
                inProgress = true,
                overlayGranted = QuickBallPreferences.overlayAllowed(this),
                accessibilityEnabled = QuickBallPreferences.accessibilityEnabled(this)
            )
        } else QuickBallOnboarding.Route.DASHBOARD

        if (awaited != null && route == awaited && !isRoutePermissionGranted(awaited)) {
            feedback = when (awaited) {
                QuickBallOnboarding.Route.OVERLAY -> "Overlay access is still required. You can try again whenever you're ready."
                QuickBallOnboarding.Route.ACCESSIBILITY -> "The service is still off. Enable Vi's Quick Ball in Accessibility settings, then return here."
                else -> null
            }
        } else if (awaited != null) {
            feedback = null
        }
        show(route)
    }

    override fun onPause() {
        QuickBallAccessibilityService.setSetupActivityVisible(false)
        super.onPause()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    @Suppress("DEPRECATION")
    private fun configureEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        val palette = QuickBallSetupUi.colors(this)
        window.decorView.setBackgroundColor(palette.background)
        androidx.core.view.WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = !palette.dark
            isAppearanceLightNavigationBars = !palette.dark
        }
    }

    private fun show(route: QuickBallOnboarding.Route, animate: Boolean = true) {
        val palette = QuickBallSetupUi.colors(this)
        screen.setBackgroundColor(palette.background)
        ui.render(screen, route, DashboardState(
            overlayGranted = QuickBallPreferences.overlayAllowed(this),
            accessibilityEnabled = QuickBallPreferences.accessibilityEnabled(this),
            enabled = QuickBallController.isEnabled(this),
            active = QuickBallController.isEnabled(this) && QuickBallAccessibilityService.isOverlayAvailable(),
            feedback = feedback,
            onBeginSetup = ::beginSetup,
            onSkipIntro = ::skipIntro,
            onOpenOverlaySettings = ::openOverlaySettings,
            onOpenAccessibilitySettings = ::openAccessibilitySettings,
            onStart = { startBall(route == QuickBallOnboarding.Route.READY) },
            onStop = ::stopBall,
            onRefresh = {
                feedback = null
                val refreshed = if (QuickBallPreferences.isOnboardingInProgress(this)) {
                    QuickBallOnboarding.resolve(true, true, QuickBallPreferences.overlayAllowed(this), QuickBallPreferences.accessibilityEnabled(this))
                } else QuickBallOnboarding.Route.DASHBOARD
                show(refreshed)
            },
            onContinueSetup = ::continueSetup,
            onAbout = ::showAbout,
            onPrivacyPolicy = { startActivity(Intent(this, PrivacyPolicyActivity::class.java)) }
        ), animate)
    }

    private fun beginSetup() {
        QuickBallPreferences.setIntroSeen(this, true)
        QuickBallPreferences.setOnboardingInProgress(this, true)
        feedback = null
        show(QuickBallOnboarding.resolve(true, true,
            QuickBallPreferences.overlayAllowed(this), QuickBallPreferences.accessibilityEnabled(this)))
    }

    private fun skipIntro() {
        QuickBallPreferences.setIntroSeen(this, true)
        QuickBallPreferences.setOnboardingInProgress(this, false)
        feedback = null
        show(QuickBallOnboarding.Route.DASHBOARD)
    }

    private fun continueSetup() = beginSetup()

    private fun openOverlaySettings() {
        val route = QuickBallOnboarding.Route.OVERLAY
        pendingSettingsRoute = route
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        runCatching { startActivity(intent) }.onFailure {
            pendingSettingsRoute = null
            feedback = "Android couldn't open overlay settings. Open Settings → Apps → Special app access → Display over other apps."
            show(route)
        }
    }

    private fun openAccessibilitySettings() {
        val route = QuickBallOnboarding.Route.ACCESSIBILITY
        pendingSettingsRoute = route
        // The public API offers the Accessibility Settings page; service-specific detail
        // screens are OEM-specific and are intentionally not addressed with hidden actions.
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        runCatching { startActivity(intent) }.onFailure {
            pendingSettingsRoute = null
            feedback = "Android couldn't open Accessibility settings. Find Vi's Quick Ball under Installed services."
            show(route)
        }
    }

    private fun startBall(fromOnboarding: Boolean) {
        val missingRoute = QuickBallOnboarding.resolve(true, true,
            QuickBallPreferences.overlayAllowed(this), QuickBallPreferences.accessibilityEnabled(this))
        if (missingRoute == QuickBallOnboarding.Route.OVERLAY || missingRoute == QuickBallOnboarding.Route.ACCESSIBILITY) {
            QuickBallPreferences.setIntroSeen(this, true)
            QuickBallPreferences.setOnboardingInProgress(this, true)
            feedback = "Complete the highlighted permission step to start Quick Ball."
            show(missingRoute)
            return
        }
        if (!QuickBallController.setEnabled(this, true)) {
            feedback = "Quick Ball could not be enabled. Check both permissions below and try again."
            show(if (fromOnboarding) QuickBallOnboarding.Route.READY else QuickBallOnboarding.Route.DASHBOARD)
            return
        }
        startAttempt++
        val attempt = startAttempt
        feedback = "Starting Quick Ball…"
        show(if (fromOnboarding) QuickBallOnboarding.Route.READY else QuickBallOnboarding.Route.DASHBOARD)
        verifyStarted(attempt, 0, fromOnboarding)
    }

    private fun verifyStarted(attempt: Int, elapsedMs: Int, fromOnboarding: Boolean) {
        if (attempt != startAttempt || isFinishing || isDestroyed) return
        if (QuickBallAccessibilityService.isOverlayAvailable()) {
            feedback = null
            if (fromOnboarding) {
                QuickBallPreferences.setOnboardingInProgress(this, false)
                QuickBallPreferences.setOnboardingComplete(this, true)
            }
            show(QuickBallOnboarding.Route.DASHBOARD)
            return
        }
        if (elapsedMs >= 3_000) {
            QuickBallController.setEnabled(this, false)
            feedback = "The overlay didn't appear. Check that Accessibility is enabled and the app may display over other apps, then try again."
            show(if (fromOnboarding) QuickBallOnboarding.Route.READY else QuickBallOnboarding.Route.DASHBOARD)
            return
        }
        handler.postDelayed({ verifyStarted(attempt, elapsedMs + 150, fromOnboarding) }, 150)
    }

    private fun stopBall() {
        startAttempt++
        QuickBallController.setEnabled(this, false)
        feedback = "Quick Ball is off."
        show(QuickBallOnboarding.Route.DASHBOARD)
    }

    private fun isRoutePermissionGranted(route: QuickBallOnboarding.Route) = when (route) {
        QuickBallOnboarding.Route.OVERLAY -> QuickBallPreferences.overlayAllowed(this)
        QuickBallOnboarding.Route.ACCESSIBILITY -> QuickBallPreferences.accessibilityEnabled(this)
        else -> true
    }

    private fun showAbout() {
        AlertDialog.Builder(this)
            .setTitle(R.string.quick_ball_about_title)
            .setMessage(
                "A small navigation tool with tap, double-tap, long-press and drag gestures.\n\n" +
                    "Accessibility use: Android sends window-state events. Quick Ball checks event package and class names to detect system panels, and requests Back, Home or Recent Apps only for a gesture. The service is configured not to retrieve window content.\n\n" +
                    "This project contains no advertising SDK or ad placements. Add the home-screen widget from your launcher, or add the Quick Ball tile from the Quick Settings editor."
            )
            .setPositiveButton("Privacy Policy") { _, _ ->
                startActivity(Intent(this, PrivacyPolicyActivity::class.java))
            }
            .setNegativeButton("Done", null)
            .show()
    }
}
