package ru.rznnike.demokmp.app.utils

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

/** [Clock] whose instant can be moved forward by the test. */
class MutableClock(
    var current: Instant,
    private val zone: ZoneId = ZoneOffset.UTC
) : Clock() {
    override fun getZone(): ZoneId = zone

    override fun withZone(zone: ZoneId): Clock = MutableClock(current, zone)

    override fun instant(): Instant = current

    fun advanceByMillis(millis: Long) {
        current = current.plusMillis(millis)
    }
}
