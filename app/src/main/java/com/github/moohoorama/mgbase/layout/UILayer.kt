package com.github.moohoorama.mgbase.layout

import android.graphics.*
import android.util.Log
import com.github.moohoorama.mgbase.core.MainActivity
import com.github.moohoorama.mgbase.core.TColor
import com.github.moohoorama.mgbase.core.TouchEV
import com.github.moohoorama.mgbase.layout.UI.CustomTexture
import com.github.moohoorama.mgbase.layout.UI.UIObj
import java.util.HashMap


/**
 * Created by Yanoo on 2017. 12. 29
 */
class UILayer(activity: MainActivity, private val bitmapSize:Int, private val fontSize:Int, private val shapeSize:Int, bufferMax:Int): Layer(activity,bufferMax) {
    private val typeface = Typeface.DEFAULT

    private var bitmap:Bitmap= Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_4444)
    var canvas: Canvas = Canvas(bitmap)

    /* bitmap에 그릴 공간을 할당하기 위하 사용하는 변수 */
    private var x=0
    private var y=0
    private var maxHeight=0 // 현재 줄의 최대 길이. 이만큼 개행해야함

    data class TextLoc(val rect: RectF, val centerPointF: PointF)
    /* 이미 할당된 공간에 대한 기록 */
    private val msgMap = HashMap<String, TextLoc>()

    private var uis = ArrayList<UIObj>()
    private val customTextures=ArrayList<CustomTexture>()

    private val rectTx=CustomTexture(shapeSize,shapeSize,fun(canvas:Canvas,area:RectF) {
        val paint= Paint()
        TColor.WHITE.setPaint(paint)
        canvas.drawRect(area, paint)
    }).attach(this)

    private val roundTx=CustomTexture(shapeSize,shapeSize,fun(canvas:Canvas,rect:RectF) {
        val paint= Paint()
        TColor.WHITE.setPaint(paint)
        val newRect=RectF(rect.left-rect.width(),rect.top-rect.height(),rect.right,rect.bottom)
        canvas.drawArc(newRect, 0f, 90f, true, paint)
    }).attach(this)

    override fun getWidth(): Int {
        return bitmapSize
    }

    override fun getHeight(): Int {
        return bitmapSize
    }

    private fun init() {
        bitmap.eraseColor(TColor.WHITE.transparent(0f).int())
        msgMap.clear()
        x = 0
        y = 0
        maxHeight = 0

        for (customTexture in customTextures) {
            val area = getDrawableArea(customTexture.width,customTexture.height) ?: continue
            val normalArea=rectNormalize(area)
            customTexture.reload(canvas,area,normalArea)
            Log.i("Draw", "   $area,$normalArea")
        }
        /*
        val rect = getDrawableArea(shapeSize,shapeSize)
        if (rect != null) {
            canvas.drawRect(rect, paint)
            rectTx = rectNormalize(rect,-0.5f)
        }

        val rrect = getDrawableArea(shapeSize,shapeSize)
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
        */

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

    override fun drawRect(left:Float, top:Float, right:Float, bottom:Float,  tc: TColor): Boolean {
        return addRect(left,top,right,bottom, rectTx.get(), tc)
    }

    fun drawRoundRect(left:Float, top:Float, right:Float, bottom:Float, round:Float, tc: TColor):Boolean {
        var ret=true
        ret = ret and  addRect(left,top+round,right,bottom-round, rectTx.get(), tc)
        ret = ret and  addRect(left+round,top,right-round,top+round, rectTx.get(), tc)
        ret = ret and  addRect(left+round,bottom-round,right-round,bottom, rectTx.get(), tc)

        val quat=(Math.PI/2).toFloat()
        ret = ret and  addRect(left,top,left+round,top+round, roundTx.get(), tc,quat*2)
        ret = ret and  addRect(left,bottom-round,left+round,bottom, roundTx.get(), tc,quat)
        ret = ret and  addRect(right-round,top,right,top+round, roundTx.get(), tc,quat*3)
        ret = ret and  addRect(right-round,bottom-round,right,bottom, roundTx.get(), tc)
        return ret
    }
    fun drawRoundBlock(left:Float, top:Float, right:Float, bottom:Float, round:Float, tc: TColor):Boolean {
        val lighteness=tc.clone().addRGB(0.2f)
        val darkness=tc.clone().addRGB(-0.2f)

        var ret=true
        ret = ret and  addRectColors(left,top+round,left+round,bottom-round, rectTx.get(), arrayOf(lighteness,lighteness,tc,tc))
        ret = ret and  addRect(left+round,top+round,right-round,bottom-round, rectTx.get(), tc)
        ret = ret and  addRectColors(right-round,top+round,right,bottom-round, rectTx.get(), arrayOf(tc,tc,darkness,darkness))
        ret = ret and  addRectColors(left+round,top,right-round,top+round, rectTx.get(), arrayOf(tc,lighteness,tc,lighteness))
        ret = ret and  addRectColors(left+round,bottom-round,right-round,bottom, rectTx.get(), arrayOf(darkness,tc,darkness,tc))

        val quat=(Math.PI/2).toFloat()
        ret = ret and  addRectColors(left,top,left+round,top+round, roundTx.get(), arrayOf(lighteness,tc,lighteness,lighteness),quat*2)
        ret = ret and  addRectColors(left,bottom-round,left+round,bottom, roundTx.get(), arrayOf(lighteness,tc,tc,darkness),quat)
        ret = ret and  addRectColors(right-round,top,right,top+round, roundTx.get(), arrayOf(darkness,tc,tc,lighteness),quat*3)
        ret = ret and  addRectColors(right-round,bottom-round,right,bottom, roundTx.get(), arrayOf(darkness,tc,darkness,darkness))
        return ret
    }

    fun drawBlock(left:Float, top:Float, right:Float, bottom:Float, round:Float, tc: TColor):Boolean {
        val lighteness=tc.clone().addRGB(0.2f)
        val darkness=tc.clone().addRGB(-0.3f)

        var ret=true
        ret = ret and  addRect(left+round,top+round,right-round,bottom-round, rectTx.get(), tc)
        ret = ret and  addRectColors(left,top+round,left+round,bottom-round, rectTx.get(), arrayOf(lighteness,lighteness,tc,tc))
        ret = ret and  addRectColors(right-round,top+round,right,bottom-round, rectTx.get(), arrayOf(tc,tc,darkness,darkness))
        ret = ret and  addRectColors(left+round,top,right-round,top+round, rectTx.get(), arrayOf(tc,lighteness,tc,lighteness))
        ret = ret and  addRectColors(left+round,bottom-round,right-round,bottom, rectTx.get(), arrayOf(darkness,tc,darkness,tc))

        val quat=(Math.PI/2).toFloat()
        ret = ret and  addRectColors(left,top,left+round,top+round, rectTx.get(), arrayOf(lighteness,tc,lighteness,lighteness),quat*2)
        ret = ret and  addRectColors(left,bottom-round,left+round,bottom, rectTx.get(), arrayOf(lighteness,tc,tc,darkness),quat)
        ret = ret and  addRectColors(right-round,top,right,top+round, rectTx.get(), arrayOf(darkness,tc,tc,lighteness),quat*3)
        ret = ret and  addRectColors(right-round,bottom-round,right,bottom, rectTx.get(), arrayOf(darkness,tc,darkness,darkness))
        return ret
    }


    override fun reload() {
        super.reload()
        init()
        Log.i("Reload","RELOAD!!!!!!!!!")
    }

    fun addCustomTexture(customTexture: CustomTexture):UILayer {
        customTextures.add(customTexture)
        return this
    }

    fun addUI(ui: UIObj) :UILayer {
        uis.add(ui)
        return this
    }

    override fun act(clock: Long, touchEV: TouchEV) {
        super.act(clock, touchEV)

        var removeList=ArrayList<UIObj>()
        uis.filterNotTo(removeList) { it.act(clock, touchEV) }
        for (r in removeList) {
            uis.remove(r)
        }
    }

    override fun draw(clock: Long) {
        super.draw(clock)

        for (ui in uis) {
            ui.draw(this,clock)
        }
    }

    fun drawText(x:Float, y:Float, size:Float, align:Paint.Align, msg:String, tc: TColor) {
        val textLoc=getText(msg)
        if (textLoc != null) {
            val tx = rectNormalize(textLoc.rect, 0.5f)

            if (tx.width() > 0 && tx.height() > 0) {
                val width = size*tx.width()/tx.height()
                val height = size
//                val y = y+ height*textLoc.centerPointF.y
                when(align) {
                    Paint.Align.LEFT -> addRect(x,y-height/2,x+width,y+height/2, tx, tc)
                    Paint.Align.CENTER -> addRect(x-width/2,y-height/2,x+width/2,y+height/2, tx, tc)
                    Paint.Align.RIGHT-> addRect(x-width,y-height/2,x,y+height/2, tx, tc)
                }
            }
        }
    }
    fun drawText(loc: RectF, msg:String, tc: TColor) {
        val textLoc=getText(msg)
        if (textLoc != null) {
            val tx = rectNormalize(textLoc.rect, 0.5f)

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
                val baseBot=loc.height()*textLoc.centerPointF.y
                addRect(loc.left,loc.top+baseBot,loc.right,loc.bottom+baseBot, tx, tc)
            }
        }
    }
    fun getText(msg: String): TextLoc? {
        return msgMap[msg] ?: renderText(msg)
    }

    fun getDrawableArea(width: Int, height:Int): RectF? {
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
        val ret = RectF((x+1).toFloat(),(y+1).toFloat(),(x+1+width).toFloat(),(y+1+height).toFloat())
        x += width+2
        return ret
    }
    private fun rectNormalize(rect:RectF, bound:Float=-0.5f):RectF {
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
                Math.abs(bound.top)+Math.abs(bound.bottom)) ?: return null
        /* left,top쪽 방향으로 조금 오버해서 그릴 수도 있음 */
        canvas.drawText(msg, drawArea.left+Math.abs(bound.left).toFloat(), drawArea.top+Math.abs(bound.top).toFloat(), textPaint)
        setDirty()

        val cx = (bound.exactCenterX()+Math.abs(bound.left))/drawArea.width()
        val cy = (bound.exactCenterY()+Math.abs(bound.top))/drawArea.height()
        if (cy > 1) {
            Log.i("drawText","Warning $cx, $cy")
        }
        val loc = TextLoc(drawArea, PointF(cx,cy))
        msgMap.put(msg, loc)
        return loc
    }

}