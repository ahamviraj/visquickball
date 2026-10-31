// Copyright (C) 2026 Aham Viraj
// SPDX-License-Identifier: GPL-3.0-only
// This program is licensed under the GNU General Public License version 3 only.
// See the repository LICENSE file for the complete license text.

package com.viraj.viquickball

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/** Offline-first policy reader. The WebView is limited to the bundled document. */
class PrivacyPolicyActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Privacy Policy"

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
        }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(8), dp(16), dp(10))
        }
        val back = Button(this).apply {
            text = "Back"
            setOnClickListener { finish() }
        }
        header.addView(back, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        val online = PrivacyPolicyConfig.configuredUrl()
        val note = TextView(this).apply {
            text = if (online == null) "Offline copy · online policy link not configured" else "Offline copy · open current online version"
            textSize = 13f
            setTextColor(Color.rgb(88, 88, 88))
            setPadding(0, dp(4), 0, dp(2))
        }
        header.addView(note)
        if (online != null) {
            val open = Button(this).apply {
                text = "Open online Privacy Policy"
                setOnClickListener { openOnlinePolicy(online) }
            }
            header.addView(open, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        }
        root.addView(header)

        val web = WebView(this)
        web.settings.apply {
            javaScriptEnabled = false
            domStorageEnabled = false
            allowFileAccess = true // Required only to load the packaged asset below.
            allowContentAccess = false
            allowFileAccessFromFileURLs = false
            allowUniversalAccessFromFileURLs = false
            setSupportMultipleWindows(false)
            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }
        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
                !isBundledPolicyUrl(request.url.toString())

            @Deprecated("Deprecated in API 24")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean = !isBundledPolicyUrl(url)
        }
        web.loadUrl("file:///android_asset/privacy-policy.html")
        root.addView(web, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        setContentView(root)
    }

    private fun isBundledPolicyUrl(url: String): Boolean =
        url == "file:///android_asset/privacy-policy.html" ||
            url.startsWith("file:///android_asset/privacy-policy.html#")

    private fun openOnlinePolicy(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url)))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "No browser is available to open the online policy.", Toast.LENGTH_LONG).show()
        } catch (_: SecurityException) {
            Toast.makeText(this, "Android couldn't open the online policy.", Toast.LENGTH_LONG).show()
        }
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density + 0.5f).toInt()
}
