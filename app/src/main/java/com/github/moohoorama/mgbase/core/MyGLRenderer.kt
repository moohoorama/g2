package com.github.moohoorama.mgbase.core

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.github.moohoorama.mgbase.game.MyGame
import com.github.moohoorama.mgbase.layout.Layer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by kyw on 2017-12-30
 */
val fps = 60

class MyGLRenderer(private val context: Context, private var game:MyGame): GLSurfaceView.Renderer,View.OnTouchListener {
    private var width = 1
    private var height = 1
    private var realWidth = 1
    private var realHeight = 1
    private var clock: Long = 0
    private val startTS = System.currentTimeMillis()
    private val baseSize = 1024

    private var touchEV=TouchEV()
    private var glTexture:IntArray = intArrayOf()

    fun getWidth()=width
    fun getHeight()=height

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        gl.glShadeModel(GL10.GL_SMOOTH)
        gl.glClearDepthf(1.0f)
        gl.glEnable(GL10.GL_DEPTH_TEST)
        gl.glDepthFunc(GL10.GL_LEQUAL)

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST)

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glEnable(GL10.GL_BLEND)
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
        gl.glFrontFace(GL10.GL_CW)     // 시계방향 그리기 설정
    }

    override fun onSurfaceChanged(gl: GL10, w: Int, h: Int) {
        realWidth = w
        realHeight = h
        if (width==0){
            realWidth = 1
        }
        if (realHeight == 0) {
            realHeight = 1
        }
        if (w > h) {
            height = baseSize
            width = height*realWidth/realHeight
        } else {
            width = baseSize
            height = width*realHeight/realWidth
        }
        Log.i("onSurfaceChanged", "$width $height    $realWidth $realHeight")
        gl.glViewport(0, 0, w, h)         // ViewPort 리셋
        gl.glMatrixMode(GL10.GL_PROJECTION)        // MatrixMode를 Project Mode로
        gl.glLoadIdentity()                        // Matrix 리셋
        gl.glOrthof(0f, width.toFloat(), height.toFloat(), 0f, 1f, -1f)
        gl.glMatrixMode(GL10.GL_MODELVIEW)         // Matrix를 ModelView Mode로 변환
        gl.glLoadIdentity()                        // Matrix 리셋
    }


    override fun onTouch(view: View?, me: MotionEvent?): Boolean {
        if (me != null) {
//            Log.i("onTouch", "$view $me  $me.x $me.y ${me.action} ${me.actionIndex} ${me.pointerCount}")
//            Log.i("onTouch", "${me.x} ${me.y} ${me.action} ${me.actionIndex} ${me.pointerCount}")
//            val action = me.getAction() & MotionEvent.ACTION_MASK;
//            when(action) {

//            }
            touchEV = TouchEV(me,width,height, realWidth, realHeight)
        }
        return true
    }

    fun reload() {
        for (layer in game.getLayers()) {
            layer.reload()
        }
    }

    private fun setBitmap(gl: GL10, layer: Array<Layer>) {
        if (glTexture.size != layer.size) {
            if (glTexture.isNotEmpty()) {
                gl.glDeleteTextures(glTexture.size, glTexture, 0)
            }

            glTexture = IntArray(layer.size)
            gl.glGenTextures(
                    glTexture.size,
                    glTexture,
                    0
            )
            Log.i("LoadBitmap", "Remake")
        }
        for (idx in layer.indices) {
            layer[idx].loadBitmap(gl,glTexture[idx])
        }
    }

    override fun onDrawFrame(gl: GL10) {
        val curClock = (System.currentTimeMillis() - startTS) * fps / 1000
        var nextGame:MyGame?=null
        var logStr=""
        val sw=Stopwatch()

        val layers = game.getLayers()
        for (layer in layers) {
            layer.clear()
        }
        sw.event("layer_clear")

        /* It's first loop */
        if (clock == 0L) {
            game.begin(clock)
        }

        if (curClock - clock >= 2) {
            logStr += "$curClock $clock  ${curClock-clock}"
        }
        while (clock < curClock) {
            for (layer in layers) {
                layer.act(clock, touchEV)
            }
            nextGame = game.act(clock, touchEV)
            if (null != nextGame) {
                game.end(clock)
                game.begin(clock)
                clock = curClock
                break
            }

            clock++
        }
        sw.event("act")

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)
        gl.glClearColor(0f, 0f, 0f, 1.0f)
        gl.glLoadIdentity()
        sw.event("clear")

        game.draw(clock)
        for (layer in layers) {
            layer.draw(clock)
        }
        sw.event("draw")
        setBitmap(gl, layers)
        sw.event("setBitmap")
        for (layer in layers) {
            layer.render(gl)
        }
        sw.event("render")
        gl.glFinish()
        sw.event("finish")
        if (null != nextGame) {
            game = nextGame
        }
        if (logStr != "" || sw.getTotalTime() > 2000 / fps) {
            Log.i("Over!!!!!!!!!!!", "$logStr  +  $sw")

        }
    }
}