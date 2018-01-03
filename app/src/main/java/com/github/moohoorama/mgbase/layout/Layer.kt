package com.github.moohoorama.mgbase.layout

import android.graphics.Bitmap
import android.graphics.RectF
import android.opengl.GLUtils
import android.util.Log
import com.github.moohoorama.mgbase.core.MainActivity
import com.github.moohoorama.mgbase.core.TColor
import com.github.moohoorama.mgbase.core.TouchEV
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.opengles.GL10

/**
 * Created by Yanoo on 2017. 12. 28
 */
abstract class Layer(val activity: MainActivity, private val bufferMax:Int) {
    val fullTx = RectF(0f,0f,1f,1f)

    private var textureID: Int = -1
    private var indicesBuffer = ByteBuffer.allocateDirect(bufferMax*4*6).order(ByteOrder.nativeOrder()).asShortBuffer()
    private var textureBuffer = ByteBuffer.allocateDirect(bufferMax*4*8).order(ByteOrder.nativeOrder()).asFloatBuffer()
    private var vertexBuffer = ByteBuffer.allocateDirect(bufferMax*4*12).order(ByteOrder.nativeOrder()).asFloatBuffer()
    private var colorBuffer = ByteBuffer.allocateDirect(bufferMax*4*16).order(ByteOrder.nativeOrder()).asFloatBuffer()
    private var capacity = 0

    private var dirty = true

    abstract fun clear()
    abstract fun getWidth():Int
    abstract fun getHeight():Int
    abstract fun drawRect(loc: RectF, tc: TColor):Boolean
    abstract fun getBitmap():Bitmap?
    open fun act(clock: Long, touchEV: TouchEV) {}
    open fun draw(clock: Long) {}

    fun getRenderer() = activity.glView.renderer

    fun setDirty() {
        dirty = true
    }

    fun loadBitmap(gl: GL10, textureID:Int) {
        if (dirty){
            val bitmap= getBitmap()
            if (bitmap != null) {
                Log.i("LoadBitmap", "$textureID, Bitmap")
                this.textureID = textureID
                gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID)
                gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR)
                gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR)
                GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
            } else {
                Log.i("LoadBitmap", "$textureID, NoBitmap")
            }
            dirty = false
        }
    }
    open fun reload() {
        setDirty()
    }
    fun addRect(vertex: RectF, texture: RectF, tc: TColor) {
        val vf = dot4To4Point(3, vertex)
        val tf = dot4To4Point(2, texture)
        val cf = rgbToPoint(tc, 4)
        val nf = shortArrayOf(0,1,2,2,1,3)
        for (i in 0 until nf.size) {
            nf[i] = (nf[i]+(capacity*4)).toShort()
        }
        addPolygon(vf, tf, cf, nf)
    }

    private fun addPolygon(vertex: FloatArray, texture: FloatArray, color: FloatArray, index:ShortArray) {
        if(capacity >= bufferMax) {
            return
        }
        vertexBuffer.put(vertex)
        textureBuffer.put(texture)
        colorBuffer.put(color)
        indicesBuffer.put(index)
        capacity++
    }

    private fun rgbToPoint(tc: TColor, count: Int): FloatArray {
        return rgbToPoint(tc.r, tc.g, tc.b, tc.a, count)
    }

    private fun rgbToPoint(r: Float, g: Float, b: Float, a: Float, count: Int): FloatArray {
        val cf = FloatArray(count * 4)
        for (i in 0 until count) {
            cf[i * 4] = r
            cf[i * 4 + 1] = g
            cf[i * 4 + 2] = b
            cf[i * 4 + 3] = a
        }
        return cf
    }

    private fun dot4To4Point(p_level: Int, rect: RectF): FloatArray {
        return if (p_level == 2) { /* 2d */
            floatArrayOf(
                    rect.left, rect.bottom,
                    rect.left, rect.top,
                    rect.right, rect.bottom,
                    rect.right, rect.top)
        } else {
            val depth=-(capacity+1).toFloat()/bufferMax.toFloat()
            floatArrayOf(
                    rect.left, rect.bottom, depth,
                    rect.left, rect.top, depth,
                    rect.right, rect.bottom, depth,
                    rect.right, rect.top, depth)
        }
    }

    open fun render(gl: GL10) {
        textureBuffer.position(0)
        vertexBuffer.position(0)
        colorBuffer.position(0)
        indicesBuffer.position(0)

        assert(this.textureID != -1)
        if (this.textureID == -1) {
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
            gl.glDisable(GL10.GL_TEXTURE_2D)
        } else {
            gl.glBindTexture(GL10.GL_TEXTURE_2D, this.textureID)
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer)
        }

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer)
        gl.glDrawElements(GL10.GL_TRIANGLES, capacity*6, GL10.GL_UNSIGNED_SHORT, indicesBuffer)

        if (this.textureID == -1) {
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
            gl.glEnable(GL10.GL_TEXTURE_2D)
        }
        capacity=0

        textureBuffer.position(0)
        vertexBuffer.position(0)
        colorBuffer.position(0)
        indicesBuffer.position(0)
    }
}