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
    private var size = maxOf(width,height)
    private var press = 0
    private var action = -1
    private var interval = 12
    private var pressClock = 0

    fun setInterval(v:Int):ButtonObj {
        interval = v
        return this
    }
    fun setSize(v:Float):ButtonObj{
        if (v > 50f) {
            size = v
        }
        return this
    }

    override fun act(clock: Long, touchEV: TouchEV) {
        action = -1
        val prevPress = press
        if (touchEV.minDistance(x, y) < size*2/3) {
            press/=2
            if (pressClock > 0) {
                pressClock--
            } else {
                action = 1
                if (prevPress != 0 && press==0) {
                    activity.soundMgr.playSound(R.raw.uncap, 100)
                }
            }
        } else {
            pressClock = 0
            press = (size.toInt()/8+press*7)/8
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
            layer.drawRoundRect(makeCenterRect(x,y,width-press,height-press),round,color)
            var textRect =makeCenterRect(x,y,(width-press)/2,(height-press)/2)
            layer.drawText(x,y,(width-press)/4, Paint.Align.CENTER, msg, TColor.WHITE)
        }
    }
}