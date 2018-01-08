package com.github.moohoorama.mgbase.layout.UI

import android.graphics.Paint
import android.graphics.RectF
import com.github.moohoorama.mgbase.R
import com.github.moohoorama.mgbase.core.MainActivity
import com.github.moohoorama.mgbase.core.TColor
import com.github.moohoorama.mgbase.core.TouchEV
import com.github.moohoorama.mgbase.layout.UILayer
import com.github.moohoorama.mgbase.layout.makeCenterRect

/**
 * Created by Yanoo on 2017. 12. 29
 */
class ButtonObj(private val activity: MainActivity, private val x:Float, private val y:Float, private val msg:String, private val color: TColor, private val width:Float=240f, private val height:Float=160f): UIObj() {

    private val round = minOf(width,height)/4
    private val size = maxOf(width,height)
    private var press = 0
    private val pressMax = size.toInt()/4
    private var action = -1
    private var interval = 12
    private var pressClock = 0

    fun setInterval(v:Int):ButtonObj {
        interval = v
        return this
    }

    override fun act(clock: Long, touchEV: TouchEV) {
        action = -1
        val prevPress = press
        if (touchEV.minDistance(x, y) < size/2) {
            if (pressClock > 0) {
                pressClock--
            } else {
                action = 1
                if (press==0) {
                    activity.soundMgr.playSound(R.raw.uncap, 100)
                }
            }
            press = (pressMax*7+press*1)/8
        } else {
            press = (press*7)/8
            pressClock = 0
        }
//        Log.d("button", "button $press")
    }

    fun press() : Boolean{
        if (action != -1) {
            pressClock = interval
            return true
        }
        return false
    }

    override fun draw(layer: UILayer, clock: Long) {
        if (color.a > 0) {
//            layer.drawRoundRect(makeCenterRect(x,y,width+press-pressMax,height+press-pressMax),round,color)
            val loc=makeCenterRect(x,y,width+press-pressMax,height+press-pressMax)
            layer.drawBlock(loc.left,loc.top,loc.right,loc.bottom,round,color)
            layer.drawText(x,y,(width+press-pressMax)/4, Paint.Align.CENTER, msg, TColor.WHITE)
        }
    }
}