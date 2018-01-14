package com.github.moohoorama.mgbase.core

import android.graphics.Color
import android.graphics.Paint

/**
 * Created by Yanoo on 2016. 6. 27
 */
class TColor(r:Float, g:Float, b:Float, a:Float=1.0f) {

    var r=r
        set(value) {
            field=minOf(maxOf(value,0f),1f)
        }
    var g=g
        set(value) {
            field=minOf(maxOf(value,0f),1f)
        }
    var b=b
        set(value) {
            field=minOf(maxOf(value,0f),1f)
        }
    var a=a
        set(value) {
            field=minOf(maxOf(value,0f),1f)
        }

    fun int()= Color.argb(intA(),intR(),intG(),intB())
    fun setPaint(paint:Paint) {paint.setARGB(intA(), intR(), intG(), intB())}
    fun intA()= (a*0xff).toInt()
    fun intR()= (r*0xff).toInt()
    fun intG()= (g*0xff).toInt()
    fun intB()= (b*0xff).toInt()

    fun clone(): TColor {
        return TColor(r,g,b,a)
    }

    fun transparent(v:Float): TColor {
        return TColor(r,g,b,v)
    }
    fun grayscale(): TColor {
        val avg = (this.r + this.g + this.b) / 3
        return TColor(avg, avg, avg, this.a)
    }

    fun multyplyRGB(v: Float): TColor {
        return TColor(this.r * v, this.g * v, this.b * v, this.a)
    }
    fun addRGB(v: Float): TColor {
        return TColor(this.r + v, this.g + v, this.b + v, this.a)
    }

    companion object {

        val WHITE = TColor(1.0f, 1.0f, 1.0f)
        val BLACK = TColor(0f, 0f, 0f)
        val TRANSPARENT = TColor(0f, 0f, 0f, 0f)

        val RED = TColor(244 / 256.0f, 67 / 256.0f, 54 / 256.0f, 1.0f)
        val PINK = TColor(233 / 256.0f, 30 / 256.0f, 99 / 256.0f, 1.0f)
        val PURPLE = TColor(156 / 256.0f, 39 / 256.0f, 176 / 256.0f, 1.0f)
        val INDIGO = TColor(63 / 256.0f, 81 / 256.0f, 181 / 256.0f, 1.0f)
        val BLUE = TColor(33 / 256.0f, 150 / 256.0f, 243 / 256.0f, 1.0f)
        val LIGHTBLUE = TColor(3 / 256.0f, 169 / 256.0f, 244 / 256.0f, 1.0f)
        val CYAN = TColor(0 / 256.0f, 188 / 256.0f, 212 / 256.0f, 1.0f)
        val TEAL = TColor(0 / 256.0f, 150 / 256.0f, 136 / 256.0f, 1.0f)
        val GREEN = TColor(76 / 256.0f, 175 / 256.0f, 80 / 256.0f, 1.0f)
        val LIGHTGREEN = TColor(139 / 256.0f, 195 / 256.0f, 74 / 256.0f, 1.0f)
        val LIME = TColor(205 / 256.0f, 220 / 256.0f, 57 / 256.0f, 1.0f)
        val YELLOW = TColor(255 / 256.0f, 235 / 256.0f, 59 / 256.0f, 1.0f)
        val AMBER = TColor(255 / 256.0f, 193 / 256.0f, 7 / 256.0f, 1.0f)
        val ORANGE = TColor(255 / 256.0f, 152 / 256.0f, 0 / 256.0f, 1.0f)
        val DEEPORANGE = TColor(255 / 256.0f, 87 / 256.0f, 34 / 256.0f, 1.0f)
        val BROWN = TColor(121 / 256.0f, 85 / 256.0f, 72 / 256.0f, 1.0f)
        val GRAY = TColor(158 / 256.0f, 158 / 256.0f, 158 / 256.0f, 1.0f)
        val LIGHTGRAY = TColor(200 / 256.0f, 200 / 256.0f, 200 / 256.0f, 1.0f)

        val IDX = arrayOf(BLACK, BLUE, GREEN, CYAN, RED, PURPLE, YELLOW, GRAY, LIGHTGRAY, LIGHTBLUE, LIGHTGREEN, TEAL, PINK, AMBER, LIME, WHITE)
    }

}
