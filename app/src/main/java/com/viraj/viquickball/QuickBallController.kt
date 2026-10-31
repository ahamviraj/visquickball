package com.viraj.viquickball

import android.content.Context
import android.widget.Toast

/** Coordinates the one persisted enabled flag across the app, widget, and Quick Settings tile. */
internal object QuickBallController {
    fun isEnabled(context: Context) = QuickBallPreferences.isEnabled(context)

    fun prerequisitesMet(context: Context) = QuickBallPreferences.overlayAllowed(context) &&
        QuickBallPreferences.accessibilityEnabled(context)

    /** Returns false without changing state when an enable request lacks either prerequisite. */
    fun setEnabled(context: Context, enabled: Boolean): Boolean {
        if (enabled && !prerequisitesMet(context)) return false
        QuickBallPreferences.writeEnabled(context, enabled)
        QuickBallAccessibilityService.refresh(context.applicationContext)
        refreshControls(context.applicationContext)
        return true
    }

    fun refreshControls(context: Context) {
        QuickBallWidgetProvider.refreshAll(context)
        QuickBallTileService.requestStateUpdate(context)
    }

    fun explainSetupRequired(context: Context) {
        val message = when {
            !QuickBallPreferences.overlayAllowed(context) -> "Grant overlay permission in Vi's Quick Ball setup."
            !QuickBallPreferences.accessibilityEnabled(context) -> "Enable the Quick Ball Accessibility service in setup."
            else -> "Quick Ball could not be enabled. Open setup to check permissions."
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
