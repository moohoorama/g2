package com.github.moohoorama.mgbase.layout

import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import com.github.moohoorama.mgbase.core.MainActivity
import com.github.moohoorama.mgbase.core.TColor
import java.util.HashMap


/**
 * Created by Yanoo on 2017. 12. 29
 */
class AsciiLayer(activity: MainActivity, private val bitmapSize:Int, private val fontSize:Int, bufferMax:Int) {

    val uiLayer=UILayer(activity, bitmapSize, fontSize, -1, bufferMax)

    init {
        for (idx in 32 .. 126) {
            val str = String.format("%c",idx)
            Log.i("Ascii",str)

            val area = uiLayer.getText(str)
            if (area != null) {
                Log.i("Ascii","$str Null!")
            }
        }
    }
/*
    fun drawText(loc: RectF, msg:String, tc: TColor) {
        for (i in 0 until msg.length) {
            val oneByte=msg[i].toByte().toInt()
            Log.i("drawText", "$oneByte")
            if (32 <= oneByte && oneByte <= 126) {
                val msgStr = msg.substring(i,i+1)
                val area = uiLayer.getText(msgStr)
                uiLayer.drawText(loc,msgStr,tc)
            }
        }
    }
    */
    fun drawText(x:Float, y:Float, _size:Float, align: Paint.Align, msg:String, tc: TColor) {
//        uiLayer.drawText(x,y,size,align,msg,tc)
        var totalWidth = 0f
        var maxHeight = 0f

        for (i in 0 until msg.length) {
            val oneByte=msg[i].toByte().toInt()
            if (32 <= oneByte && oneByte <= 126) {
                val msgStr = msg.substring(i,i+1)
                val area = uiLayer.getText(msgStr)
                if (area != null) {
                    totalWidth += area.rect.width()
                    maxHeight = maxOf(maxHeight, area.rect.height())
                }
            }
        }

        val size = _size / totalWidth
        var curWidth = 0f
        for (i in 0 until msg.length) {
            val oneByte=msg[i].toByte().toInt()
            if (32 <= oneByte && oneByte <= 126) {
                val msgStr = msg.substring(i,i+1)
                val area = uiLayer.getText(msgStr)
                if (area != null) {
                    val left = curWidth / totalWidth
                    curWidth += area?.rect!!.width()
                    val right = curWidth / totalWidth
                    uiLayer.drawText(RectF(x + (left - 0.5f) * size, y-maxHeight*size,x+(right+0.5f)*size, y+maxHeight*size ), msgStr, tc)
                }
            }
        }
    }
}