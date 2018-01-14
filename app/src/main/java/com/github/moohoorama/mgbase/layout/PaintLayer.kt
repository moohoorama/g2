package com.github.moohoorama.mgbase.layout

import android.graphics.*
import com.github.moohoorama.mgbase.core.MainActivity
import com.github.moohoorama.mgbase.core.TColor

/**
 * Created by Yanoo on 2017. 12. 28
 */
class PaintLayer(val blinkSize:Int, activity: MainActivity, bitmapId:Int): Layer(activity,1  ) {
    private var bitmap:Bitmap=if (bitmapId != -1) {
            BitmapFactory.decodeResource(activity.resources, bitmapId)
        } else {
            Bitmap.createBitmap(blinkSize, blinkSize, Bitmap.Config.ARGB_4444)
        }
    private var canvas:Canvas=Canvas(bitmap)


    override fun getWidth(): Int {
        return blinkSize
    }

    override fun getHeight(): Int {
        return blinkSize
    }

    override fun clear() {
        bitmap.eraseColor(Color.TRANSPARENT)

        val renderer=getRenderer()
        addRect(0.0f,0.0f,renderer.getWidth().toFloat(),renderer.getHeight().toFloat(), fullTx, TColor.WHITE)
    }
    override fun getBitmap(): Bitmap? {
        return bitmap
    }

    override fun drawRect(left:Float, top:Float, right:Float, bottom:Float, tc: TColor): Boolean {
        val paint= Paint()
        tc.setPaint(paint)
        canvas.drawRect(RectF(left,top,right,bottom), paint)

        setDirty()
        return true
    }
}