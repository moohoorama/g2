package com.github.moohoorama.mgbase.game

import android.graphics.RectF
import android.util.Log
import com.github.moohoorama.mgbase.core.MainActivity
import com.github.moohoorama.mgbase.core.TColor
import com.github.moohoorama.mgbase.core.TouchEV
import com.github.moohoorama.mgbase.layout.Layer
import com.github.moohoorama.mgbase.layout.NoTexLayer
import com.github.moohoorama.mgbase.layout.UI.ButtonObj
import com.github.moohoorama.mgbase.layout.UILayer

/**
 * Created by kyw on 2017-12-31
 */
class FirstGame(activity: MainActivity) : MyGame {
    private var uiLayer= UILayer(activity,1024,96)
    private var noTexLayer=NoTexLayer(activity,512)
    private var lbut= ButtonObj(50f, 50f,"Left", TColor.RED)

    private var x=0.0f
    private var y=0.0f
    private var size=100.0f

    init {
        uiLayer.addUI(lbut)
    }

    override fun getLayers(): Array<Layer> {
        return arrayOf(uiLayer, noTexLayer)
    }


    override fun act(clock: Long, touchEV: TouchEV) : MyGame? {
        x=Math.cos(clock.toDouble()*Math.PI/60.0).toFloat()*size+200
        y=Math.sin(clock.toDouble()*Math.PI/60.0).toFloat()*size+200

        if (touchEV.action != -1) {
            size += (160-size)*0.2f
        } else {
            size += (80-size)*0.2f
        }

        return null
    }

    override fun draw(clock: Long) {
        Log.i("draw","${x} ${y}")
        uiLayer.drawRect(RectF(x,y,x+50,y+100),TColor.BLUE)
        noTexLayer.drawRect(RectF(x+50,y+50,x+150,y+100),TColor.GREEN)
    }
}