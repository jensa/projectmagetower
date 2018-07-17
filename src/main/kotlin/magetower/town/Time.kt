package magetower.town

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Time {
    @Transient private var offset = 0L
    @Transient private var stoppedAt = 0L
    @Transient private var isStopped = false
    private var day = 0L

    @Transient val DAY_LENGTH = 1000

    init {
        offset = System.currentTimeMillis() - (DAY_LENGTH * day)
    }

    fun stopTime () {
        stoppedAt = System.currentTimeMillis()
        isStopped = true
    }

    fun startTime () {
        offset += System.currentTimeMillis() - stoppedAt
        isStopped = false
    }

    fun getCurrentDay() : Long {
        return getCurrentTime() / DAY_LENGTH
    }

    fun getCurrentTime() : Long {
        return (if(isStopped) stoppedAt else System.currentTimeMillis()) - offset
    }

    fun saveDay() {
        day = getCurrentDay()
    }
}