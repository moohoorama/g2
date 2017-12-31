package com.github.moohoorama.mgbase.core

data class TouchEV(val width:Float, val height:Float, val x: Float, val y:Float, val action:Int) {
    fun distance(x:Float, y:Float): Float {
        return Math.hypot((x-this.x).toDouble(),(y-this.y).toDouble()).toFloat()
    }
}
