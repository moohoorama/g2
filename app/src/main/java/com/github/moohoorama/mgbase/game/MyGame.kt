package com.github.moohoorama.mgbase.game

import com.github.moohoorama.mgbase.core.TouchEV
import com.github.moohoorama.mgbase.layout.Layer

/**
 * Created by Yanoo on 2017. 12. 25
 */
interface MyGame {
    fun getLayers():Array<Layer>
    fun act(clock: Long, touchEV: TouchEV) : MyGame?
    fun draw(clock: Long)
}
