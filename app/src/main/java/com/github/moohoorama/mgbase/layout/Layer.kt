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
fun makeCenterRect(x:Float,y:Float,width:Float,height:Float)= RectF(x-width/2,y-height/2,x+width/2,y+height/2)

abstract class Layer(private val activity: MainActivity, private val bufferMax:Int) {
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
    abstract fun drawRect(left:Float, top:Float, right:Float, bottom:Float, tc: TColor):Boolean
    abstract fun getBitmap():Bitmap?
    open fun act(clock: Long, touchEV: TouchEV) {}
    open fun draw(clock: Long) {}

    fun getRenderer() = activity.glView.renderer

    fun setDirty() {
        dirty = true
    }

    companion object {
        var depth = -0.000001f
        val nf = shortArrayOf(0,1,2,2,1,3)
    }

    fun loadBitmap(gl: GL10, textureID:Int) {
        if (dirty){
            val bitmap= getBitmap()
            if (bitmap != null) {
                Log.i("LoadBitmap", "$textureID, Bitmap")
                this.textureID = textureID
                gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID)
                //GL_NEAREST >>> GL_LINEAR
                gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST)
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

    fun addRect(left:Float, top:Float, right:Float, bottom:Float, texture: RectF, tc: TColor, rotate:Float=0f):Boolean {
        if(capacity >= bufferMax) {
            return false
        }

        if (rotate == 0f) {
            vertexBuffer.put(left)
            vertexBuffer.put(bottom)
            vertexBuffer.put(depth)
            vertexBuffer.put(left)
            vertexBuffer.put(top)
            vertexBuffer.put(depth)
            vertexBuffer.put(right)
            vertexBuffer.put(bottom)
            vertexBuffer.put(depth)
            vertexBuffer.put(right)
            vertexBuffer.put(top)
            vertexBuffer.put(depth)
        } else {
            val c=Math.cos(rotate.toDouble()).toFloat()
            val s=Math.sin(rotate.toDouble()).toFloat()
            val cx=(left+right)/2
            val cy=(top+bottom)/2

            vertexBuffer.put(cx+(left-cx)*c -(bottom-cy)*s)
            vertexBuffer.put(cy+(left-cx)*s +(bottom-cy)*c)
            vertexBuffer.put(depth)
            vertexBuffer.put(cx+(left-cx)*c -(top-cy)*s)
            vertexBuffer.put(cy+(left-cx)*s +(top-cy)*c)
            vertexBuffer.put(depth)
            vertexBuffer.put(cx+(right-cx)*c-(bottom-cy)*s)
            vertexBuffer.put(cy+(right-cx)*s+(bottom-cy)*c)
            vertexBuffer.put(depth)
            vertexBuffer.put(cx+(right-cx)*c-(top-cy)*s)
            vertexBuffer.put(cy+(right-cx)*s+(top-cy)*c)
            vertexBuffer.put(depth)
        }

        textureBuffer.put(texture.left)
        textureBuffer.put(texture.bottom)
        textureBuffer.put(texture.left)
        textureBuffer.put(texture.top)
        textureBuffer.put(texture.right)
        textureBuffer.put(texture.bottom)
        textureBuffer.put(texture.right)
        textureBuffer.put(texture.top)

        for (i in 0 until 4) {
            colorBuffer.put(tc.r)
            colorBuffer.put(tc.g)
            colorBuffer.put(tc.b)
            colorBuffer.put(tc.a)
        }
        for (i in 0 until 6) {
            indicesBuffer.put((nf[i]+(capacity*4)).toShort())
        }

        capacity++
        depth -= 0.000001f

        return true
    }

    fun addRectColors(left:Float, top:Float, right:Float, bottom:Float, texture: RectF, tc: Array<TColor>, rotate:Float=0f):Boolean {
        if(capacity >= bufferMax && tc.size != 4) {
            return false
        }

        if (rotate == 0f) {
            vertexBuffer.put(left)
            vertexBuffer.put(bottom)
            vertexBuffer.put(depth)
            vertexBuffer.put(left)
            vertexBuffer.put(top)
            vertexBuffer.put(depth)
            vertexBuffer.put(right)
            vertexBuffer.put(bottom)
            vertexBuffer.put(depth)
            vertexBuffer.put(right)
            vertexBuffer.put(top)
            vertexBuffer.put(depth)
        } else {
            val c=Math.cos(rotate.toDouble()).toFloat()
            val s=Math.sin(rotate.toDouble()).toFloat()
            val cx=(left+right)/2
            val cy=(top+bottom)/2

            vertexBuffer.put(cx+(left-cx)*c -(bottom-cy)*s)
            vertexBuffer.put(cy+(left-cx)*s +(bottom-cy)*c)
            vertexBuffer.put(depth)
            vertexBuffer.put(cx+(left-cx)*c -(top-cy)*s)
            vertexBuffer.put(cy+(left-cx)*s +(top-cy)*c)
            vertexBuffer.put(depth)
            vertexBuffer.put(cx+(right-cx)*c-(bottom-cy)*s)
            vertexBuffer.put(cy+(right-cx)*s+(bottom-cy)*c)
            vertexBuffer.put(depth)
            vertexBuffer.put(cx+(right-cx)*c-(top-cy)*s)
            vertexBuffer.put(cy+(right-cx)*s+(top-cy)*c)
            vertexBuffer.put(depth)
        }

        textureBuffer.put(texture.left)
        textureBuffer.put(texture.bottom)
        textureBuffer.put(texture.left)
        textureBuffer.put(texture.top)
        textureBuffer.put(texture.right)
        textureBuffer.put(texture.bottom)
        textureBuffer.put(texture.right)
        textureBuffer.put(texture.top)

        if (tc.size == 4) {
            for (c in tc) {
                colorBuffer.put(c.r)
                colorBuffer.put(c.g)
                colorBuffer.put(c.b)
                colorBuffer.put(c.a)
            }
        } else {
            for (i in 0 until 4) {
                colorBuffer.put(tc[0].r)
                colorBuffer.put(tc[0].g)
                colorBuffer.put(tc[0].b)
                colorBuffer.put(tc[0].a)
            }
        }
        for (i in 0 until 6) {
            indicesBuffer.put((nf[i]+(capacity*4)).toShort())
        }

        capacity++
        depth -= 0.000001f

        return true
    }

    open fun render(gl: GL10) {
        textureBuffer.position(0)
        vertexBuffer.position(0)
        colorBuffer.position(0)
        indicesBuffer.position(0)

        gl.glBindTexture(GL10.GL_TEXTURE_2D, this.textureID)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer)

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer)
        gl.glDrawElements(GL10.GL_TRIANGLES, capacity*6, GL10.GL_UNSIGNED_SHORT, indicesBuffer)

        capacity=0
        depth = 0.00001f

        textureBuffer.position(0)
        vertexBuffer.position(0)
        colorBuffer.position(0)
        indicesBuffer.position(0)
    }
}