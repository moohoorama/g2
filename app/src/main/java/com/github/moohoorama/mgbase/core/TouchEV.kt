package com.github.moohoorama.mgbase.core

import android.view.MotionEvent
import android.graphics.RectF


data class TPoint(val x:Float=-1f, val y:Float=-1f, val action:Int=0) {
    fun equal(x:Float, y:Float) = this.x == x && this.y == y
}

class TouchEV() {
    val points=HashMap<Int, TPoint>()

    constructor(me: MotionEvent, width:Int, height:Int, realWidth:Int, realHeight:Int) : this() {
        val idx = me.actionIndex
        val id = me.getPointerId(idx)
        val action = me.actionMasked
        val x = (me.getX(idx)*width/realWidth)
        val y = (me.getY(idx)*height/realHeight)
        when(action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> points[id]= TPoint(x,y,action)
            MotionEvent.ACTION_MOVE-> {
                val size = me.getPointerCount()
                for (i in 0 until size) {
                    val x = (me.getX(i)*width/realWidth)
                    val y = (me.getY(i)*height/realHeight)
                    points[i]= TPoint(x,y,action)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> points.remove(id)
        }
    }

    fun pressed(zone: RectF) {
        for ((_,point) in points) {
            if (zone.contains(point.x, point.y)) {
                true
            }
        }
        false
    }
    fun getPress()= if(points.size == 1) { points[0]} else {null}

    fun minDistance(x:Float, y:Float): Float {
        var dis=9999.0
        for ((_,point) in points) {
            val cur =Math.hypot((x-point.x).toDouble(),(y-point.y).toDouble())
            if (cur < dis) {
                dis = cur
            }
        }
        return dis.toFloat()
    }
}
