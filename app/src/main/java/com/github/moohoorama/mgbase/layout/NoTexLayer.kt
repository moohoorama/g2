package com.github.moohoorama.mgbase.layout

import android.graphics.Bitmap
import android.graphics.RectF
import com.github.moohoorama.mgbase.core.MainActivity
import com.github.moohoorama.mgbase.core.TColor
import com.github.moohoorama.mgbase.layout.Layer

/**
 * Created by Yanoo on 2017. 12. 28
 */
class NoTexLayer(activity: MainActivity, bufferMax:Int): Layer(activity,bufferMax  ) {
    override fun getWidth(): Int {
        return activity.glView.renderer.getWidth()
    }

    override fun getHeight(): Int {
        return getRenderer().getHeight()
    }

    override fun getBitmap(): Bitmap? {
        return null
    }

    override fun clear() {

    }
    override fun drawRect(loc: RectF, tc: TColor): Boolean {
        this.addRect(loc, fullTx, tc)
        return true
    }
}