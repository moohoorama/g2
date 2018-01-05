package com.github.moohoorama.mgbase.game

import android.graphics.Paint
import android.graphics.RectF
import com.github.moohoorama.mgbase.R
import com.github.moohoorama.mgbase.core.MainActivity
import com.github.moohoorama.mgbase.core.TColor
import com.github.moohoorama.mgbase.core.TouchEV
import com.github.moohoorama.mgbase.layout.Layer
import com.github.moohoorama.mgbase.layout.UI.ButtonObj
import com.github.moohoorama.mgbase.layout.UILayer

/**
 * Created by kyw on 2017-12-31
 */
class FirstGame(private val activity: MainActivity) : MyGame {
    private var uiLayer= UILayer(activity,1024,48,64,512)
    private var lbut= ButtonObj(activity,150f, 150f,"Left", TColor.RED)
    private var rbut= ButtonObj(activity,350f, 150f,"Right", TColor.RED)

    private var x=0.0f
    private var y=0.0f
    private var size=100.0f

    private var printText=""

    init {
        uiLayer.addUI(lbut).addUI(rbut)
    }
    override fun getLayers(): Array<Layer> {
        return arrayOf(uiLayer)
    }

    override fun act(clock: Long, touchEV: TouchEV) : MyGame? {
        x=Math.cos(clock.toDouble()*Math.PI/60.0).toFloat()*size+200
        y=Math.sin(clock.toDouble()*Math.PI/60.0).toFloat()*size+200

        if (lbut.press()) {
            activity.readText("읽기",fun (text:String,ret:Boolean){
                if (ret) {
                    printText=text
                }
            })
        }

        size += if (touchEV.pressed()) {
            (160-size)*0.2f
        } else {
            (80-size)*0.2f
        }

        return null
    }
    override fun begin(clock:Long) {
        activity.soundMgr.playMedia(R.raw.bgm)
    }
    override fun end(clock:Long) {

    }

    override fun draw(clock: Long) {
        uiLayer.drawRect(RectF(x,y,x+50,y+100),TColor.BLUE)
        uiLayer.drawText(x, y, 50f, Paint.Align.CENTER, printText, TColor.INDIGO)
        uiLayer.addRect(RectF(100f,500f,600f,1000f),RectF(0f,0f,0.2f,0.2f),TColor.WHITE)
//        uiLayer.addRect(RectF(100f,500f,600f,1000f),RectF(0f,0f,0.2f,0.2f),TColor.WHITE,(clock*Math.PI/600f).toFloat())
        uiLayer.addRect(RectF(300f,300f,500f,350f),uiLayer.getRectTx(),TColor.WHITE,(clock*Math.PI/600f).toFloat())
    }
}