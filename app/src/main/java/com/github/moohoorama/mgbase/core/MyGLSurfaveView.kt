package com.github.moohoorama.mgbase.core

import android.opengl.GLSurfaceView
import com.github.moohoorama.mgbase.game.Shariki

/**
 * Created by kyw on 2017-12-30
 */
class MyGLSurfaveView(activity: MainActivity) : GLSurfaceView(activity) {
    var renderer: MyGLRenderer = MyGLRenderer(context, Shariki(activity))

    init {
        setOnTouchListener(renderer)
        setRenderer(renderer)
        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }
}