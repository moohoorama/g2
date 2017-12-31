package com.github.moohoorama.mgbase.layout

import android.graphics.*
import com.github.moohoorama.mgbase.core.MainActivity
import com.github.moohoorama.mgbase.core.TColor
import com.github.moohoorama.mgbase.core.TouchEV
import com.github.moohoorama.mgbase.layout.Layer
import com.github.moohoorama.mgbase.layout.UI.UIObj
import java.util.HashMap
import javax.microedition.khronos.opengles.GL10

/**
 * Created by Yanoo on 2017. 12. 29
 */
class UILayer(activity: MainActivity, private val blinkSize:Int, private val fontSize:Int): Layer(activity,16384  ) {
    private val typeface = Typeface.DEFAULT
    private val baseSize = fontSize*2

    private var bitmap:Bitmap=Bitmap.createBitmap(blinkSize, blinkSize, Bitmap.Config.ARGB_4444)
    private var canvas: Canvas = Canvas(bitmap)
    private val msgMap = HashMap<String, RectF>()
    private val whiteTx=RectF(
            (blinkSize-2).toFloat()/blinkSize.toFloat(),
            (blinkSize-2).toFloat()/blinkSize.toFloat(),
            (blinkSize-1).toFloat()/blinkSize.toFloat(),
            (blinkSize-1).toFloat()/blinkSize.toFloat())

    private var UIs = ArrayList<UIObj>()

    override fun getWidth(): Int {
        return blinkSize
    }

    override fun getHeight(): Int {
        return blinkSize
    }

    private fun init() {
        bitmap.eraseColor(Color.TRANSPARENT)
        msgMap.clear()
        x = 0
        y = 0
        val paint= Paint()
        TColor.WHITE.setPaint(paint)
        canvas.drawRect(Rect(blinkSize-3,blinkSize-3,blinkSize,blinkSize), paint)
        setDirty()
    }

    override fun clear() {
        if (y + baseSize >= blinkSize / 2) {
            reload()
            init()
        }
    }
    override fun getBitmap(): Bitmap? {
        return bitmap
    }

    override fun drawRect(loc: RectF, tc: TColor): Boolean {
        addRect(loc, whiteTx, tc)
        return true
    }

    override fun reload() {
        super.reload()
        init()
    }

    fun addUI(ui: UIObj) :UILayer {
        UIs.add(ui)
        return this
    }

    override fun act(clock: Long, touchEV: TouchEV) {
        super.act(clock, touchEV)

        for (ui in UIs) {
            ui.act(clock,touchEV)
        }
    }

    override fun draw(clock: Long) {
        super.draw(clock)

        for (ui in UIs) {
            ui.draw(this,clock)
        }
    }

    fun drawText(x:Float, y:Float, size:Float, msg:String, tc: TColor) {
        val tx=getText(msg)

        if (tx != null && tx.width() > 0 && tx.height() > 0) {
            val width = size/2*tx.width()/tx.height()
            val height = size/2
            val loc = RectF(x-width,y-height,x+width,y+height)

            addRect(loc, tx, tc)
        }
    }
    fun drawText(loc: RectF, msg:String, tc: TColor) {
        val tx=getText(msg)

        if (tx != null && tx.width() > 0 && tx.height() > 0) {
            if (loc.right < 0 && loc.bottom < 0) {
                loc.bottom = loc.top + fontSize
            }
            if (loc.right < 0) {
                loc.right = loc.left+loc.height()*tx.width()/tx.height()
            }
            if (loc.bottom < 0) {
                loc.bottom = loc.top+loc.width()*tx.height()/tx.width()
            }
            addRect(loc, tx, tc)
        }
    }
    fun getText(msg: String): RectF? {
        if (msgMap.containsKey(msg)) {
            return msgMap[msg]
        }
        val ret = renderText(msg)
        if (ret!=null) {
            msgMap.put(msg, ret)
        }
        return ret
    }

    private var x=0
    private var y=0

    private fun renderText(msg: String): RectF? {
        val textPaint = Paint()
        textPaint.textSize = fontSize.toFloat()
        textPaint.setAntiAlias(true)
        TColor.WHITE.setPaint(textPaint)
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.typeface = typeface
        textPaint.strokeWidth = 0f
        textPaint.style = Paint.Style.FILL
        textPaint.setShadowLayer(1f,0f,1f,Color.WHITE)

        val bound = Rect()
        textPaint.getTextBounds(msg, 0, msg.length, bound)
        if (x + bound.right - bound.left >= blinkSize) { /* carriage return*/
            x = 0
            y += baseSize
            if (y >= blinkSize) {
                return null
            }
        }
        /* left,top쪽 방향으로 조금 오버해서 그릴 수도 있음 */
        canvas.drawText(msg, (x + -bound.left).toFloat(), (y + (-bound.top + bound.bottom)).toFloat(), textPaint)
        setDirty()
        val ret = RectF(
                x.toFloat() / blinkSize,
                y.toFloat() / blinkSize,
                (x + bound.right - bound.left).toFloat() / blinkSize,
                (y + baseSize).toFloat() / blinkSize)

        x += bound.width() + 2
        return ret
    }

    override fun render(gl: GL10) {
        super.render(gl)
    }
}