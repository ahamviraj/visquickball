// Copyright (C) 2026 Aham Viraj
// SPDX-License-Identifier: GPL-3.0-only
// This program is licensed under the GNU General Public License version 3 only.
// See the repository LICENSE file for the complete license text.

package com.viraj.viquickball

import android.content.ComponentName
import android.content.Context
import android.provider.Settings

/** Persistent source of truth for enabled state and the normalized dock point. */
internal object QuickBallPreferences {
    private const val FILE = "quick_ball"
    private const val ENABLED = "active" // retain the existing key for installed users
    private const val EDGE = "dock_edge"
    private const val ALONG = "dock_along"
    private const val INTRO_SEEN = "intro_seen"
    private const val ONBOARDING_IN_PROGRESS = "onboarding_in_progress"
    private const val ONBOARDING_COMPLETE = "onboarding_complete"

    fun isEnabled(context: Context) = prefs(context).getBoolean(ENABLED, false)
    fun writeEnabled(context: Context, enabled: Boolean) { prefs(context).edit().putBoolean(ENABLED, enabled).apply() }

    fun hasSeenIntro(context: Context) = prefs(context).getBoolean(INTRO_SEEN, false)
    fun setIntroSeen(context: Context, seen: Boolean) { prefs(context).edit().putBoolean(INTRO_SEEN, seen).apply() }
    fun isOnboardingInProgress(context: Context) = prefs(context).getBoolean(ONBOARDING_IN_PROGRESS, false)
    fun setOnboardingInProgress(context: Context, inProgress: Boolean) {
        prefs(context).edit().putBoolean(ONBOARDING_IN_PROGRESS, inProgress).apply()
    }
    fun hasCompletedOnboarding(context: Context) = prefs(context).getBoolean(ONBOARDING_COMPLETE, false)
    fun setOnboardingComplete(context: Context, complete: Boolean) {
        prefs(context).edit().putBoolean(ONBOARDING_COMPLETE, complete).apply()
    }

    fun dockPosition(context: Context): DockPosition {
        val p = prefs(context)
        val edge = runCatching { DockEdge.valueOf(p.getString(EDGE, DockEdge.RIGHT.name) ?: DockEdge.RIGHT.name) }
            .getOrDefault(DockEdge.RIGHT)
        val along = p.getFloat(ALONG, p.getFloat("y_fraction", 0.5f))
        return DockPosition(edge, along.takeIf { it.isFinite() }?.coerceIn(0f, 1f) ?: 0.5f)
    }

    fun saveDockPosition(context: Context, position: DockPosition) {
        prefs(context).edit().putString(EDGE, position.edge.name)
            .putFloat(ALONG, position.along.takeIf { it.isFinite() }?.coerceIn(0f, 1f) ?: 0.5f).apply()
    }

    fun overlayAllowed(context: Context) = Settings.canDrawOverlays(context)

    fun accessibilityEnabled(context: Context): Boolean {
        val enabled = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: return false
        val target = ComponentName(context, QuickBallAccessibilityService::class.java).flattenToString()
        return enabled.split(':').any { it.equals(target, ignoreCase = true) }
    }

    private fun prefs(context: Context) = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
}
