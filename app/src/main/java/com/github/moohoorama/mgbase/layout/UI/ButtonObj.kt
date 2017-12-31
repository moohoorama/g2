package com.github.moohoorama.mgbase.layout.UI

import android.graphics.RectF
import android.util.Log
import com.github.moohoorama.mgbase.core.TColor
import com.github.moohoorama.mgbase.core.TouchEV
import com.github.moohoorama.mgbase.layout.UILayer

/**
 * Created by Yanoo on 2017. 12. 29
 */
class ButtonObj(private val x:Float, private val y:Float, private val msg:String, private val color: TColor): UIObj() {

    private val size = 50f
    private var press = size
    private var action = -1
    private var interval = 5
    private var pressClock = 0

    fun setInterval(v:Int):ButtonObj {
        interval = v
        return this
    }

    override fun act(clock: Long, touchEV: TouchEV) {
        var goal= size
        action = -1
        if (touchEV.distance(x.toFloat(),y.toFloat()) < size*3/2 && touchEV.action != -1) {
            goal = size + 10

            if (pressClock > 0) {
                pressClock--
            } else {
                action = touchEV.action
            }
        } else {
            pressClock = 0
        }
        press += (goal - press)/4f
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
//        val msgSize=layer.getText(msg)
        val loc=RectF(x-press,y-press,x+press,y+press)

        layer.drawRect(loc,color)
        layer.drawText(x,y,press,msg,TColor.WHITE)
    }
}