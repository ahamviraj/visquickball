// Copyright (C) 2026 Aham Viraj
// SPDX-License-Identifier: GPL-3.0-only
// This program is licensed under the GNU General Public License version 3 only.
// See the repository LICENSE file for the complete license text.

package com.viraj.viquickball

/** Set this to the final HTTPS policy URL after the site is published. */
internal object PrivacyPolicyConfig {
    const val CANONICAL_URL = ""

    fun configuredUrl(): String? {
        val value = CANONICAL_URL.trim()
        if (value.isEmpty()) return null
        val uri = android.net.Uri.parse(value)
        return value.takeIf {
            uri.scheme == "https" && !uri.host.isNullOrBlank() && uri.userInfo == null
        }
    }
}
