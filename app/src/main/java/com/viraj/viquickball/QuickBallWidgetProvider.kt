// Copyright (C) 2026 Aham Viraj
// SPDX-License-Identifier: GPL-3.0-only
// This program is licensed under the GNU General Public License version 3 only.
// See the repository LICENSE file for the complete license text.

package com.viraj.viquickball

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class QuickBallWidgetProvider : AppWidgetProvider() {
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_TOGGLE) {
            val enable = !QuickBallController.isEnabled(context)
            if (!QuickBallController.setEnabled(context, enable)) QuickBallController.explainSetupRequired(context)
        }
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == ACTION_TOGGLE) refreshAll(context)
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { update(context, manager, it) }
    }

    companion object {
        const val ACTION_TOGGLE = "com.viraj.viquickball.action.WIDGET_TOGGLE"

        fun refreshAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, QuickBallWidgetProvider::class.java)
            manager.getAppWidgetIds(component).forEach { update(context, manager, it) }
        }

        private fun update(context: Context, manager: AppWidgetManager, id: Int) {
            val views = RemoteViews(context.packageName, R.layout.quick_ball_widget)
            val enabled = QuickBallController.isEnabled(context)
            val prerequisites = QuickBallController.prerequisitesMet(context)
            val overlayAvailable = QuickBallAccessibilityService.isOverlayAvailable()
            val stateText = when {
                !prerequisites -> "SETUP REQUIRED"
                !enabled -> "OFF"
                overlayAvailable -> "ON"
                else -> "STARTING"
            }
            views.setTextViewText(R.id.widget_state, stateText)
            views.setTextViewText(R.id.widget_toggle, if (enabled) "Turn Off" else "Turn On")
            val toggle = PendingIntent.getBroadcast(context, id, Intent(context, QuickBallWidgetProvider::class.java).setAction(ACTION_TOGGLE),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val setup = PendingIntent.getActivity(context, id + 20000, Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_toggle, toggle)
            views.setOnClickPendingIntent(R.id.widget_setup, setup)
            manager.updateAppWidget(id, views)
        }
    }
}
