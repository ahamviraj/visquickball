// Copyright (C) 2026 Aham Viraj
// SPDX-License-Identifier: GPL-3.0-only
// This program is licensed under the GNU General Public License version 3 only.
// See the repository LICENSE file for the complete license text.

package com.viraj.viquickball

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.app.PendingIntent

class QuickBallTileService : TileService() {
    override fun onStartListening() { super.onStartListening(); updateTileState() }

    override fun onClick() {
        super.onClick()
        val enable = !QuickBallController.isEnabled(this)
        if (!QuickBallController.setEnabled(this, enable)) {
            QuickBallController.explainSetupRequired(this)
            val setup = Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (Build.VERSION.SDK_INT >= 34) {
                val pending = PendingIntent.getActivity(this, 13, setup, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                startActivityAndCollapse(pending)
            } else startActivityAndCollapse(setup)
        }
        updateTileState()
    }

    private fun updateTileState() {
        val ready = QuickBallController.prerequisitesMet(this) && QuickBallAccessibilityService.isOverlayAvailable()
        val enabled = QuickBallController.isEnabled(this)
        qsTile?.apply {
            label = "Quick Ball"
            state = if (enabled && ready) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            if (Build.VERSION.SDK_INT >= 29) subtitle = when {
                !QuickBallController.prerequisitesMet(this@QuickBallTileService) -> "Setup required"
                enabled && !ready -> "Starting"
                enabled -> "On"
                else -> "Off"
            }
            updateTile()
        }
    }

    companion object {
        fun requestStateUpdate(context: Context) {
            if (Build.VERSION.SDK_INT >= 24) {
                runCatching { TileService.requestListeningState(context, ComponentName(context, QuickBallTileService::class.java)) }
            }
        }
    }
}
