package com.github.moohoorama.mgbase.layout.UI

import com.github.moohoorama.mgbase.core.TouchEV
import com.github.moohoorama.mgbase.layout.UILayer

/**
 * Created by Yanoo on 2017. 12. 29
 */
abstract class UIObj {
    /* false이면 삭제 */
    abstract fun act(clock: Long, touchEV: TouchEV):Boolean
    abstract fun draw(layer: UILayer, clock: Long)

    fun attach(layer:UILayer) : UIObj {
        layer.addUI(this)
        return this
    }
}