package com.github.moohoorama.mgbase.layout.UI

import android.graphics.Canvas
import android.graphics.RectF
import com.github.moohoorama.mgbase.layout.UILayer

/**
 * Created by Yanoo on 2018. 1. 8
 */
class CustomTexture(val width:Int, val height:Int,val draw:(canvas:Canvas,area:RectF)->Unit) {
    var area=RectF()
    private var normalArea=RectF()

    fun get()= normalArea

    fun reload(canvas:Canvas, area:RectF, normalRect:RectF) {
        this.area=area
        this.normalArea =normalRect
        draw(canvas,area)
    }

    fun attach(uiLayer: UILayer):CustomTexture {
        uiLayer.addCustomTexture(this)
        return this
    }
}
