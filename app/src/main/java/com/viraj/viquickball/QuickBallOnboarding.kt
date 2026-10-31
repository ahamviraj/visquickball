package com.viraj.viquickball

/** Pure route selection keeps first-run and resume behavior explicit and testable. */
internal object QuickBallOnboarding {
    enum class Route { WELCOME, OVERLAY, ACCESSIBILITY, READY, DASHBOARD }

    fun resolve(
        hasSeenIntro: Boolean,
        inProgress: Boolean,
        overlayGranted: Boolean,
        accessibilityEnabled: Boolean
    ): Route = when {
        !hasSeenIntro -> Route.WELCOME
        !inProgress -> Route.DASHBOARD
        !overlayGranted -> Route.OVERLAY
        !accessibilityEnabled -> Route.ACCESSIBILITY
        else -> Route.READY
    }
}
