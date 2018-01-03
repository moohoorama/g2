package com.github.moohoorama.mgbase.layout

import android.graphics.*
import android.util.Log
import com.github.moohoorama.mgbase.core.MainActivity
import com.github.moohoorama.mgbase.core.TColor
import com.github.moohoorama.mgbase.core.TouchEV
import com.github.moohoorama.mgbase.layout.UI.UIObj
import java.util.HashMap
import javax.microedition.khronos.opengles.GL10

/**
 * Created by Yanoo on 2017. 12. 29
 */
class UILayer(activity: MainActivity, private val bitmapSize:Int, private val fontSize:Int, private val shapeSize:Int,private  val bufferMax:Int): Layer(activity,bufferMax) {
    private val typeface = Typeface.DEFAULT

    private var bitmap:Bitmap=Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_4444)
    var canvas: Canvas = Canvas(bitmap)

    /* bitmap에 그릴 공간을 할당하기 위하 사용하는 변수 */
    private var x=0
    private var y=0
    private var maxHeight=0 // 현재 줄의 최대 길이. 이만큼 개행해야함

    /* 이미 할당된 공간에 대한 기록 */
    private val msgMap = HashMap<String, RectF>()
    private var rectTx:RectF=RectF()
    private var roundRectTx:RectF=RectF()
    private var circleTx:RectF=RectF()

    fun getRectTx() = rectTx
    fun getRoundedRectTx() = roundRectTx
    fun getCircleTx() = circleTx

    private var UIs = ArrayList<UIObj>()

    override fun getWidth(): Int {
        return bitmapSize
    }

    override fun getHeight(): Int {
        return bitmapSize
    }

    private fun init() {
        bitmap.eraseColor(Color.TRANSPARENT)
        msgMap.clear()
        x = 0
        y = 0
        maxHeight = 0
        val paint= Paint()
        TColor.WHITE.setPaint(paint)

        val rect = getDrawableArea(shapeSize,shapeSize)
        canvas.drawRect(rect, paint)
        rectTx = rectNormalize(rect!!)

        val rrect = getDrawableArea(shapeSize,shapeSize)
        canvas.drawRoundRect(rrect, shapeSize/4.toFloat(), shapeSize/4.toFloat(),paint)
        roundRectTx = rectNormalize(rrect!!)

        val circle = getDrawableArea(shapeSize,shapeSize)
        if (circle != null) {
            canvas.drawCircle(circle.centerX(),circle.centerY(),shapeSize/2.toFloat(),paint)
            circleTx = rectNormalize(circle)
        }

        setDirty()
    }

    override fun clear() {
        if (y + maxHeight >= bitmapSize / 2) {
            reload()
            init()
        }
    }
    override fun getBitmap(): Bitmap? {
        return bitmap
    }

    override fun drawRect(loc: RectF, tc: TColor): Boolean {
        addRect(loc, rectTx, tc)
        return true
    }

    override fun reload() {
        super.reload()
        init()
        Log.i("Reload","RELOAD!!!!!!!!!")
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
        val _tx=getText(msg)
        if (_tx == null) {
            return
        }
        val tx = rectNormalize(_tx)

        if (tx.width() > 0 && tx.height() > 0) {
            val width = size/2*tx.width()/tx.height()
            val height = size/2
            val loc = RectF(x-width,y-height,x+width,y+height)

            addRect(loc, tx, tc)
        }
    }
    fun drawText(loc: RectF, msg:String, tc: TColor) {
        val _tx=getText(msg)
        if (_tx == null) {
            return
        }
        val tx = rectNormalize(_tx)

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

    private fun getDrawableArea(width: Int, height:Int): RectF? {
        if (x > bitmapSize) {
            return null
        }
        if (x + width >= bitmapSize) { /* carriage return*/
            y += maxHeight+2
            x = 0
            maxHeight = 0
            if (y >= bitmapSize) {
                return null
            }
        }
        if (y+height >= bitmapSize) {
            return null
        }
        if (height > maxHeight) {
            maxHeight = height
        }
        var ret = RectF((x+1).toFloat(),(y+1).toFloat(),(x+1+width).toFloat(),(y+1+height).toFloat())
        x += width+2
        return ret
    }
    fun rectNormalize(rect:RectF):RectF {
        return RectF((rect.left-0.5f)/bitmapSize,(rect.top-0.5f)/bitmapSize,(rect.right+0.5f)/bitmapSize,(rect.bottom+0.5f)/bitmapSize)
    }

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
        val drawArea = getDrawableArea(bound.width(),bound.height())
        if (drawArea==null) {
            return null
        }
        /* left,top쪽 방향으로 조금 오버해서 그릴 수도 있음 */
        canvas.drawText(msg, (drawArea.left + -bound.left).toFloat(), drawArea.bottom, textPaint)
        setDirty()
        return drawArea
    }

    override fun render(gl: GL10) {
        super.render(gl)
    }
}