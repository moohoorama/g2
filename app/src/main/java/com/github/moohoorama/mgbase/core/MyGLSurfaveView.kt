package com.github.moohoorama.mgbase.core

import android.content.Context
import android.graphics.RectF
import android.opengl.GLSurfaceView
import android.util.Log
import com.github.moohoorama.mgbase.game.FirstGame

/**
 * Created by kyw on 2017-12-30
 */
class MyGLSurfaveView(activity: MainActivity) : GLSurfaceView(activity) {
    var renderer: MyGLRenderer = MyGLRenderer(context, FirstGame(activity))

    init {
        setOnTouchListener(renderer)
        setRenderer(renderer)
        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }
}