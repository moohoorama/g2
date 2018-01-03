package com.github.moohoorama.mgbase.core

import android.view.MotionEvent
import android.R.attr.y
import android.R.attr.x
import android.support.v4.view.MotionEventCompat.getPointerId
import android.graphics.PointF
import android.support.v4.view.MotionEventCompat.getPointerCount



data class Point(val x:Float,val y:Float,val action:Int)

class TouchEV() {
    val points=HashMap<Int, Point>()

    constructor(me: MotionEvent, width:Int, height:Int, realWidth:Int, realHeight:Int) : this() {
        val idx = me.actionIndex
        val id = me.getPointerId(idx)
        val action = me.actionMasked
        val x = (me.getX(idx)*width/realWidth)
        val y = (me.getY(idx)*height/realHeight)
        when(action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> points[id]=Point(x,y,action)
            MotionEvent.ACTION_MOVE-> {
                val size = me.getPointerCount()
                for (i in 0 until size) {
                    val x = (me.getX(i)*width/realWidth)
                    val y = (me.getY(i)*height/realHeight)
                    points[i]=Point(x,y,action)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> points.remove(id)
        }
    }

    fun pressed():Boolean = points.size > 0
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
