package com.viraj.viquickball

import org.junit.Assert.assertEquals
import org.junit.Test

class QuickBallDockingTest {
    private val bounds = DockBounds(10, 24, 1010, 1824)
    private val size = 52

    @Test fun defaultPositionIsCenteredOnTheRightEdge() {
        val point = QuickBallDocking.point(DockPosition(DockEdge.RIGHT, 0.5f), size, bounds)
        assertEquals(bounds.right - size, point.x)
        assertEquals((bounds.top + bounds.bottom - size) / 2, point.y)
    }

    @Test fun aCenterReleaseAlwaysSnapsToAnEdge() {
        val centerX = (bounds.left + bounds.right - size) / 2
        val centerY = (bounds.top + bounds.bottom - size) / 2
        val result = QuickBallDocking.nearest(centerX, centerY, size, bounds)
        assertEquals(result.point, QuickBallDocking.point(result.position, size, bounds))
        assertEquals(DockEdge.LEFT, result.position.edge)
    }

    @Test fun normalizedLocationRestoresOnAnotherScreenSize() {
        val position = DockPosition(DockEdge.BOTTOM, 0.72f)
        val small = QuickBallDocking.point(position, size, DockBounds(0, 0, 500, 800))
        val large = QuickBallDocking.point(position, size, DockBounds(0, 0, 1200, 2000))
        assertEquals(0.72f, small.x.toFloat() / (500 - size), 0.002f)
        assertEquals(0.72f, large.x.toFloat() / (1200 - size), 0.002f)
        assertEquals(800 - size, small.y)
        assertEquals(2000 - size, large.y)
    }
}

class QuickBallOnboardingTest {
    @Test fun firstLaunchStartsWithWelcome() {
        assertEquals(QuickBallOnboarding.Route.WELCOME,
            QuickBallOnboarding.resolve(false, false, false, false))
    }

    @Test fun setupRequestsOverlayBeforeAccessibility() {
        assertEquals(QuickBallOnboarding.Route.OVERLAY,
            QuickBallOnboarding.resolve(true, true, false, false))
        assertEquals(QuickBallOnboarding.Route.ACCESSIBILITY,
            QuickBallOnboarding.resolve(true, true, true, false))
    }

    @Test fun readyRequiresBothActualPermissions() {
        assertEquals(QuickBallOnboarding.Route.READY,
            QuickBallOnboarding.resolve(true, true, true, true))
    }

    @Test fun returningUserGoesToDashboardEvenIfPermissionWasRevoked() {
        assertEquals(QuickBallOnboarding.Route.DASHBOARD,
            QuickBallOnboarding.resolve(true, false, false, false))
    }
}
