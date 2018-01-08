package com.github.moohoorama.mgbase.core

/**
 * Created by Yanoo on 2018. 1. 7
 */
class Stopwatch {
    private val beginTime: Long
    private var prevTime: Long = 0
    internal var ret = StringBuffer()

    init {
        prevTime = System.currentTimeMillis()
        beginTime = prevTime
    }

    fun event(msg: String) {
        val curTime = System.currentTimeMillis()
        ret.append(String.format("|%d|%s", curTime - prevTime, msg))
        prevTime = curTime
    }

    override fun toString(): String {
        val curTime = System.currentTimeMillis()
        ret.append(String.format("|%d|%s(%d)", curTime - prevTime, "done", curTime - beginTime))
        return ret.toString()
    }

    fun getTotalTime()=prevTime-beginTime
}
