package com.aziz.sabbela

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/**
 * Makes the status bar and navigation bar work with Sabbela's dark-blue headers.
 *
 * Why this exists: on Android 15+ (and the app targets API 36) the system forces
 * edge-to-edge. The status bar becomes transparent and the app draws underneath it, so
 * `window.statusBarColor` and the old `fitsSystemWindows` trick no longer colour it —
 * the page background showed through and the white clock/battery icons vanished on it.
 *
 * - [root]   the screen's outermost view. It gets bottom padding for the navigation bar
 *            and the keyboard, so content is never hidden behind them.
 * - [header] the dark-blue header, if the screen has one. It gets extra top padding equal
 *            to the status bar height, so the blue extends behind the status bar and the
 *            white icons sit on blue.
 * - [lightNavBar] true when the bottom of the screen is light (dark gesture handle/icons).
 */
fun Activity.setupEdgeToEdge(root: View, header: View? = null, lightNavBar: Boolean = true) {
    val controller = WindowCompat.getInsetsController(window, window.decorView)
    controller.isAppearanceLightStatusBars = false          // white icons on the blue header
    controller.isAppearanceLightNavigationBars = lightNavBar

    if (Build.VERSION.SDK_INT < 35) {
        // Older Android still honours a coloured status bar; match the header so they merge.
        @Suppress("DEPRECATION")
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        return
    }

    val headerTop = header?.paddingTop ?: 0
    val rootBottom = root.paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
        val bars = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        )
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
        header?.updatePadding(top = headerTop + bars.top)
        root.updatePadding(bottom = rootBottom + maxOf(bars.bottom, ime.bottom))
        insets
    }
    ViewCompat.requestApplyInsets(root)
}
