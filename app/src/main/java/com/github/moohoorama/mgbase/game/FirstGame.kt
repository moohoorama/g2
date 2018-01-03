package com.github.moohoorama.mgbase.game

import android.graphics.RectF
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.util.Log
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
class FirstGame(activity: MainActivity) : MyGame {
    private var uiLayer= UILayer(activity,512,48,64,512)
    private var lbut= ButtonObj(150f, 150f,"Left", TColor.RED)
    private var rbut= ButtonObj(350f, 150f,"Right", TColor.RED)
    private var soundPool:SoundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        SoundPool.Builder().setMaxStreams(10).build();
    } else {
        SoundPool(10, AudioManager.STREAM_MUSIC, 1);
    }
    private var bgm = soundPool.load(activity, R.raw.bgm,1)
    private var beep = soundPool.load(activity, R.raw.uncap,1)

    private var x=0.0f
    private var y=0.0f
    private var size=100.0f

    init {
        uiLayer.addUI(lbut).addUI(rbut)
    }
    override fun getLayers(): Array<Layer> {
        return arrayOf(uiLayer)
    }

    override fun act(clock: Long, touchEV: TouchEV) : MyGame? {
        x=Math.cos(clock.toDouble()*Math.PI/60.0).toFloat()*size+200
        y=Math.sin(clock.toDouble()*Math.PI/60.0).toFloat()*size+200

        if (touchEV.pressed()) {
            soundPool.play(beep,1.0f,1.0f,1,1,1.0f)
            size += (160-size)*0.2f
        } else {
            size += (80-size)*0.2f
        }

        return null
    }
    override fun begin(clock:Long) {
        soundPool.play(bgm,1.0f,1.0f,1,-1,1.0f)
    }
    override fun end(clock:Long) {

    }

    override fun draw(clock: Long) {
        uiLayer.drawRect(RectF(x,y,x+50,y+100),TColor.BLUE)
    }
}