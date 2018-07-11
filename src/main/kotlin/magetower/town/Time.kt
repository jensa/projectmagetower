package magetower.town

class Time {
    private var offset = System.currentTimeMillis()
    private var stoppedAt = 0L
    private var isStopped = false

    fun stopTime () {
        stoppedAt = System.currentTimeMillis()
        isStopped = true
    }

    fun startTime () {
        offset += System.currentTimeMillis() - stoppedAt
        isStopped = false
    }

    fun getCurrentDay() : Long {
        return getCurrentTime() / 1000
    }

    fun getCurrentTime() : Long {
        return (if(isStopped) stoppedAt else System.currentTimeMillis()) - offset
    }
}