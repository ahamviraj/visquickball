// Copyright (C) 2026 Aham Viraj
// SPDX-License-Identifier: GPL-3.0-only
// This program is licensed under the GNU General Public License version 3 only.
// See the repository LICENSE file for the complete license text.

package com.viraj.viquickball

import android.accessibilityservice.AccessibilityService
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class QuickBallAccessibilityService : AccessibilityService() {
    companion object {
        private const val TAG = "ViQuickBall"
        @Volatile private var instance: QuickBallAccessibilityService? = null
        @Volatile private var setupActivityVisible = false
        fun refresh(context: Context) { instance?.reconcileVisibility() }
        fun isOverlayAvailable() = instance?.overlay?.isAttached() == true
        fun isVisible() = instance?.overlay?.isVisible() == true
        fun setSetupActivityVisible(visible: Boolean) {
            setupActivityVisible = visible
            instance?.reconcileVisibility()
        }
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private var overlay: QuickBallOverlay? = null
    private var receiverRegistered = false
    private var screenOn = true
    private var notificationPanelOpen = false
    private var panelChange: Runnable? = null
    private val keyguard get() = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_OFF -> { screenOn = false; notificationPanelOpen = false }
                Intent.ACTION_SCREEN_ON -> { screenOn = true; notificationPanelOpen = false }
                Intent.ACTION_USER_PRESENT -> { screenOn = true; notificationPanelOpen = false }
            }
            reconcileVisibility()
        }
    }

    @Suppress("DEPRECATION")
    override fun onCreate() {
        super.onCreate()
        screenOn = (getSystemService(Context.POWER_SERVICE) as PowerManager).isInteractive
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF); addAction(Intent.ACTION_SCREEN_ON); addAction(Intent.ACTION_USER_PRESENT)
        }
        if (Build.VERSION.SDK_INT >= 33) registerReceiver(screenReceiver, filter, Context.RECEIVER_EXPORTED)
        else registerReceiver(screenReceiver, filter)
        receiverRegistered = true
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        if (overlay == null) overlay = QuickBallOverlay(this, ::performNavigation)
        reconcileVisibility()
        QuickBallController.refreshControls(this)
    }

    private fun reconcileVisibility() {
        if (instance !== this) instance = this
        val enabled = QuickBallPreferences.isEnabled(this)
        val permitted = QuickBallPreferences.overlayAllowed(this) && QuickBallPreferences.accessibilityEnabled(this)
        if (!enabled || !permitted) {
            overlay?.remove()
            QuickBallController.refreshControls(applicationContext)
            return
        }
        val unlocked = screenOn && !keyguard.isKeyguardLocked && !keyguard.isDeviceLocked
        if (!unlocked || notificationPanelOpen || setupActivityVisible) {
            if (unlocked && !notificationPanelOpen && setupActivityVisible && overlay?.isAttached() != true) {
                if (overlay?.show() != true) Log.w(TAG, "Overlay could not be attached while setup is open")
            }
            overlay?.setTemporarilyVisible(false)
            QuickBallController.refreshControls(applicationContext)
            return
        }
        if (overlay?.show() != true) Log.w(TAG, "Overlay could not be shown")
        QuickBallController.refreshControls(applicationContext)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            event.eventType != AccessibilityEvent.TYPE_WINDOWS_CHANGED) return
        val pkg = event.packageName?.toString().orEmpty()
        val cls = event.className?.toString().orEmpty()
        val classHintsPanel = listOf("notificationshade", "notificationpanel", "quicksettings", "qscontainer", "controlcenter", "control_center")
            .any { cls.contains(it, ignoreCase = true) }
        val knownSystemUi = pkg == "com.android.systemui" || pkg.contains("systemui", ignoreCase = true) ||
            pkg.contains("controlcenter", ignoreCase = true) || pkg.contains("control_center", ignoreCase = true)
        val indicatesPanelOpen = classHintsPanel || (knownSystemUi && event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            (cls.contains("shade", true) || cls.contains("notification", true)))
        if (indicatesPanelOpen) schedulePanelState(true)
        else if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && pkg.isNotBlank() && !knownSystemUi) {
            schedulePanelState(false)
        }
    }

    private fun schedulePanelState(open: Boolean) {
        panelChange?.let(mainHandler::removeCallbacks)
        val callback = Runnable {
            panelChange = null
            if (notificationPanelOpen != open) {
                notificationPanelOpen = open
                reconcileVisibility()
            }
        }
        panelChange = callback
        mainHandler.postDelayed(callback, 180L)
    }

    private fun performNavigation(action: QuickBallGestureHandler.Action) {
        val global = when (action) {
            QuickBallGestureHandler.Action.BACK -> GLOBAL_ACTION_BACK
            QuickBallGestureHandler.Action.HOME -> GLOBAL_ACTION_HOME
            QuickBallGestureHandler.Action.RECENTS -> GLOBAL_ACTION_RECENTS
        }
        if (!performGlobalAction(global)) {
            Log.w(TAG, "Android rejected global action: $action")
            if (action == QuickBallGestureHandler.Action.RECENTS) {
                Toast.makeText(this, "This device does not allow Recent Apps through Accessibility.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onInterrupt() { overlay?.cancelGestureState() }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        overlay?.onDisplayChanged()
        reconcileVisibility()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        mainHandler.removeCallbacksAndMessages(null)
        overlay?.remove(); overlay = null
        if (instance === this) instance = null
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        mainHandler.removeCallbacksAndMessages(null)
        if (receiverRegistered) runCatching { unregisterReceiver(screenReceiver) }
        overlay?.remove(); overlay = null
        if (instance === this) instance = null
        super.onDestroy()
    }
}
