package com.github.moohoorama.mgbase.layout

import android.graphics.*
import com.github.moohoorama.mgbase.core.MainActivity
import com.github.moohoorama.mgbase.core.TColor
import com.github.moohoorama.mgbase.layout.Layer

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
        addRect(RectF(0.0f,0.0f,renderer.getWidth().toFloat(),renderer.getHeight().toFloat()), fullTx, TColor.WHITE)
    }
    override fun getBitmap(): Bitmap? {
        return bitmap
    }

    override fun drawRect(loc: RectF, tc: TColor): Boolean {
        val paint= Paint()
        tc.setPaint(paint)
        canvas.drawRect(loc, paint)

        setDirty()
        return true
    }
}