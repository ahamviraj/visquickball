package com.viraj.viquickball

import kotlin.math.max

internal enum class DockEdge { TOP, BOTTOM, LEFT, RIGHT }
internal data class DockPosition(val edge: DockEdge, val along: Float)
internal data class DockBounds(val left: Int, val top: Int, val right: Int, val bottom: Int) {
    val width get() = max(0, right - left)
    val height get() = max(0, bottom - top)
}
internal data class DockPoint(val x: Int, val y: Int)
internal data class DockedPoint(val point: DockPoint, val position: DockPosition)

/** Pure edge geometry used by overlay positioning and unit tests. */
internal object QuickBallDocking {
    fun point(position: DockPosition, size: Int, bounds: DockBounds): DockPoint {
        val along = position.along.takeIf { it.isFinite() }?.coerceIn(0f, 1f) ?: 0.5f
        val maxX = max(bounds.left, bounds.right - size)
        val maxY = max(bounds.top, bounds.bottom - size)
        return when (position.edge) {
            DockEdge.LEFT -> DockPoint(bounds.left, interpolate(bounds.top, maxY, along))
            DockEdge.RIGHT -> DockPoint(maxX, interpolate(bounds.top, maxY, along))
            DockEdge.TOP -> DockPoint(interpolate(bounds.left, maxX, along), bounds.top)
            DockEdge.BOTTOM -> DockPoint(interpolate(bounds.left, maxX, along), maxY)
        }
    }

    fun clamp(x: Int, y: Int, size: Int, bounds: DockBounds) = DockPoint(
        x.coerceIn(bounds.left, max(bounds.left, bounds.right - size)),
        y.coerceIn(bounds.top, max(bounds.top, bounds.bottom - size))
    )

    fun nearest(x: Int, y: Int, size: Int, bounds: DockBounds): DockedPoint {
        val point = clamp(x, y, size, bounds)
        val maxX = max(bounds.left, bounds.right - size)
        val maxY = max(bounds.top, bounds.bottom - size)
        val edge = listOf(
            (point.x - bounds.left) to DockEdge.LEFT,
            (maxX - point.x) to DockEdge.RIGHT,
            (point.y - bounds.top) to DockEdge.TOP,
            (maxY - point.y) to DockEdge.BOTTOM
        ).minBy { it.first }.second
        val dock = when (edge) {
            DockEdge.LEFT -> DockPoint(bounds.left, point.y)
            DockEdge.RIGHT -> DockPoint(maxX, point.y)
            DockEdge.TOP -> DockPoint(point.x, bounds.top)
            DockEdge.BOTTOM -> DockPoint(point.x, maxY)
        }
        val vertical = edge == DockEdge.LEFT || edge == DockEdge.RIGHT
        val denominator = if (vertical) maxY - bounds.top else maxX - bounds.left
        val numerator = if (vertical) dock.y - bounds.top else dock.x - bounds.left
        val along = if (denominator > 0) (numerator.toFloat() / denominator).coerceIn(0f, 1f) else 0.5f
        return DockedPoint(dock, DockPosition(edge, along))
    }

    private fun interpolate(start: Int, end: Int, fraction: Float) = (start + (end - start) * fraction).toInt()
}
