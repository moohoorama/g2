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

    data class TextLoc(val rect: RectF, val bounds:RectF)
    /* 이미 할당된 공간에 대한 기록 */
    private val msgMap = HashMap<String, TextLoc>()
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
        paint.isAntiAlias = false

        TColor.WHITE.setPaint(paint)

        val rect = getDrawableArea(shapeSize,shapeSize)
        if (rect != null) {
            canvas.drawRect(rect, paint)
            rectTx = rectNormalize(rect,-0.5f)
        }

        val rrect = getDrawableArea(shapeSize,shapeSize)
//        canvas.drawRoundRect(rrect, shapeSize/4.toFloat(), shapeSize/4.toFloat(),paint)
        if (rrect != null) {
            val newRect=RectF(rrect.left-rrect.width(),rrect.top-rrect.height(),rrect.right,rrect.bottom)
            canvas.drawArc(newRect, 0f, 90f, true, paint)
            roundRectTx = rectNormalize(rrect,-0.5f)
        }


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
        return addRect(loc, rectTx, tc)
    }

    fun drawRoundRect(loc: RectF, round:Float, tc: TColor):Boolean {
        var ret=true
        ret = ret and  addRect(RectF(loc.left,loc.top+round,loc.right,loc.bottom-round), rectTx, tc)
        ret = ret and  addRect(RectF(loc.left+round,loc.top,loc.right-round,loc.top+round), rectTx, tc)
        ret = ret and  addRect(RectF(loc.left+round,loc.bottom-round,loc.right-round,loc.bottom), rectTx, tc)

        val quat=(Math.PI/2).toFloat()
        ret = ret and  addRect(RectF(loc.left,loc.top,loc.left+round,loc.top+round), roundRectTx, tc,quat*2)
        ret = ret and  addRect(RectF(loc.left,loc.bottom-round,loc.left+round,loc.bottom), roundRectTx, tc,quat)
        ret = ret and  addRect(RectF(loc.right-round,loc.top,loc.right,loc.top+round), roundRectTx, tc,quat*3)
        ret = ret and  addRect(RectF(loc.right-round,loc.bottom-round,loc.right,loc.bottom), roundRectTx, tc)
        return ret
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

    fun drawText(x:Float, y:Float, size:Float, align:Paint.Align, msg:String, tc: TColor) {
        val textLoc=getText(msg)
        if (textLoc != null) {
            val tx = rectNormalize(textLoc.rect)

            if (tx.width() > 0 && tx.height() > 0) {
                val width = size*tx.width()/tx.height()
                val height = size
                val baseTop=0;
                //height*(textLoc.bounds.top/textLoc.rect.height())
                when(align) {
                    Paint.Align.LEFT -> addRect(RectF(x,y-height/2+baseTop,x+width,y+height/2+baseTop), tx, tc)
                    Paint.Align.CENTER -> addRect(RectF(x-width/2,y-height/2+baseTop,x+width/2,y+height/2+baseTop), tx, tc)
                    Paint.Align.RIGHT-> addRect(RectF(x-width,y-height/2+baseTop,x,y+height/2+baseTop), tx, tc)
                }
            }
        }
    }
    fun drawText(loc: RectF, msg:String, tc: TColor) {
        val textLoc=getText(msg)
        if (textLoc != null) {
            val tx = rectNormalize(textLoc.rect)

            if (tx.width() > 0 && tx.height() > 0) {
                if (loc.right < 0 && loc.bottom < 0) {
                    loc.bottom = loc.top + fontSize
                }
                if (loc.right < 0) {
                    loc.right = loc.left + loc.height() * tx.width() / tx.height()
                }
                if (loc.bottom < 0) {
                    loc.bottom = loc.top + loc.width() * tx.height() / tx.width()
                }
                addRect(loc, tx, tc)
            }
        }
    }
    fun getText(msg: String): TextLoc? {
        return msgMap[msg] ?: renderText(msg)
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
    fun rectNormalize(rect:RectF,bound:Float=0.5f):RectF {
        return RectF((rect.left-bound)/bitmapSize,(rect.top-bound)/bitmapSize,(rect.right+bound)/bitmapSize,(rect.bottom+bound)/bitmapSize)
    }

    private fun renderText(msg: String): TextLoc? {
        if (msg.isEmpty()){
            return null
        }
        val textPaint = Paint()
        textPaint.textSize = fontSize.toFloat()
        textPaint.isAntiAlias = false
        TColor.WHITE.setPaint(textPaint)
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.typeface = typeface
        textPaint.strokeWidth = 0f
        textPaint.style = Paint.Style.FILL
        textPaint.setShadowLayer(1f,0f,1f,Color.WHITE)

        val bound = Rect()
        textPaint.getTextBounds(msg, 0, msg.length, bound)
        val drawArea = getDrawableArea(Math.abs(bound.left)+Math.abs(bound.right),
                                       Math.abs(bound.top)+Math.abs(bound.bottom))
        if (drawArea==null) {
            return null
        }
        /* left,top쪽 방향으로 조금 오버해서 그릴 수도 있음 */
        canvas.drawText(msg, drawArea.left+Math.abs(bound.left).toFloat(), drawArea.top+Math.abs(bound.top).toFloat(), textPaint)
        setDirty()

        val loc = TextLoc(drawArea, RectF(bound))
        msgMap.put(msg, loc)
        return loc
    }

    override fun render(gl: GL10) {
        super.render(gl)
    }
}