package com.github.moohoorama.mgbase.game

import android.graphics.*
import com.github.moohoorama.mgbase.R
import com.github.moohoorama.mgbase.core.*
import com.github.moohoorama.mgbase.layout.Layer
import com.github.moohoorama.mgbase.layout.UI.ButtonObj
import com.github.moohoorama.mgbase.layout.UI.CustomTexture
import com.github.moohoorama.mgbase.layout.UI.UIObj
import com.github.moohoorama.mgbase.layout.UILayer
import java.util.*

/**
 * Created by kyw on 2017-12-31
 */

class ScoreUIObj(private val beginClock:Long,private val score:Int,private val x:Float,private val y:Float) : UIObj() {
    private val life = 60L
    private val size = 120
    override fun act(clock: Long, touchEV: TouchEV):Boolean {
        return (clock - beginClock < life)
    }

    override fun draw(layer: UILayer, clock: Long) {
        val progress = (clock-beginClock).toFloat()/life
        layer.drawText(x,y,size*Math.pow(progress.toDouble(),0.2).toFloat(),Paint.Align.CENTER,"$score",TColor.WHITE.transparent(1-progress))
    }
}

class Shariki(private val activity: MainActivity) : MyGame {
    private var uiLayer= UILayer(activity = activity, bitmapSize = 1024, fontSize = 48, shapeSize = 64, bufferMax = 1024)

    private val resetButton = ButtonObj(activity,100f,50f,"Reset",TColor.RED,200f,160f).attach(uiLayer)

    private val width=8
    private val height=8
    private val pad=1
    private val size=1024/(width+pad*2)
    private val round=size/6f
    private var blocks=IntArray(width*height,fun(_:Int)=0)
    private val blockCount=6
    private var rand= Random()

    private val changeClock = fps/4
    private var score = 0
    private var combo = 1

    enum class Status(val value: Int) {
        WAIT(1),
        CHOOSE(1),
        CHANGE(2),
        CRASH(3),
        BREAKDOWN(4);

        companion object {
            fun from(findValue: Int): Status = Status.values().first { it.value == findValue }
        }
    }
    private var status:Status = Status.WAIT
    private var statusClock:Long = 0
    private var speed=1

    private var selectedPos=ArrayList<Point>()

    private val blockTx= CustomTexture(size,size,fun(canvas: Canvas, area:RectF) {
        val inArea=RectF(area.left+round,area.top+round,area.right-round,area.bottom-round)

        val high = TColor.WHITE
        val mid = TColor.WHITE.addRGB(-0.2f)
        val low = TColor.WHITE.addRGB(-0.4f)

        val paint= Paint()
        paint.color = mid.int()
        canvas.drawRect(inArea, paint)

        val path = Path()
        paint.shader = LinearGradient(inArea.left,area.top,inArea.left,inArea.top, high.int(), mid.int(), Shader.TileMode.MIRROR)
        path.reset() // only needed when reusing this path for a new build
        path.moveTo(area.left, area.top) // used for first point
        path.lineTo(area.right,area.top)
        path.lineTo(inArea.right,inArea.top)
        path.lineTo(inArea.left,inArea.top)
        path.lineTo(area.left, area.top)
        canvas.drawPath(path, paint)

        paint.shader = LinearGradient(inArea.left,inArea.bottom,inArea.left,area.bottom, mid.int(), low.int(), Shader.TileMode.MIRROR)
        path.reset() // only needed when reusing this path for a new build
        path.moveTo(area.left, area.bottom) // used for first point
        path.lineTo(area.right,area.bottom)
        path.lineTo(inArea.right,inArea.bottom)
        path.lineTo(inArea.left,inArea.bottom)
        path.lineTo(area.left, area.bottom)
        canvas.drawPath(path, paint)


        paint.shader = LinearGradient(area.left,inArea.top,inArea.left,inArea.top, high.int(), mid.int(), Shader.TileMode.MIRROR)
        path.reset() // only needed when reusing this path for a new build
        path.moveTo(area.left, area.top) // used for first point
        path.lineTo(area.left,area.bottom)
        path.lineTo(inArea.left,inArea.bottom)
        path.lineTo(inArea.left,inArea.top)
        path.lineTo(area.left, area.top)
        canvas.drawPath(path, paint)

        paint.shader = LinearGradient(inArea.right,inArea.top,area.right,inArea.top, mid.int(), low.int(), Shader.TileMode.MIRROR)
        path.reset() // only needed when reusing this path for a new build
        path.moveTo(area.right, area.top) // used for first point
        path.lineTo(area.right,area.bottom)
        path.lineTo(inArea.right,inArea.bottom)
        path.lineTo(inArea.right,inArea.top)
        path.lineTo(area.right, area.top)
        canvas.drawPath(path, paint)
    }).attach(uiLayer)


    init {
        makeRandomStage()
    }
    override fun getLayers(): Array<Layer> {
        return arrayOf(uiLayer)
    }

    private fun topPad() = activity.glView.renderer.getHeight()-1024f

    private fun getX(x:Int) = (pad+x)*size.toFloat()
    private fun getY(y:Int) = ((pad+y)*size+topPad()).toFloat()
    private fun getLoc(x:Int, y:Int) = TPoint(getX(x), getY(y))
    private fun getRectF(x:Int, y:Int,xx:Int=x+1,yy:Int=y+1):RectF {
        val leftTop=getLoc(x,y)
        val rightBot=getLoc(xx,yy)
        return RectF(leftTop.x, leftTop.y, rightBot.x, rightBot.y)
    }
    private fun getAdrXY(x:Int, y:Int) = (x+y*width)
    private fun getAdr(pos:Point) = getAdrXY(pos.x,pos.y)

    private fun drawSlot(x:Int, y:Int, tColor: TColor) {
        uiLayer.addRect(getX(x),getY(y),getX(x+1),getY(y+1),blockTx.get(),tColor)
    }
    private fun drawCrashSlot(x:Int, y:Int, ratio:Float, tColor: TColor) {
        uiLayer.addRect(getX(x),getY(y),getX(x+1),getY(y+1),blockTx.get(),tColor.multyplyRGB(1-ratio))
    }
    private fun drawMoveSlot(x:Int, y:Int,x2:Int, y2:Int, _ratio:Float, tColor: TColor) {
        val beforePos = getLoc(x,y)
        val afterPos = getLoc(x2,y2)
        val ratio = if (_ratio>1.0f) {
            1.0f
        } else {
            _ratio
        }
        val afterRatio = 1-ratio
        val pos = PointF(((beforePos.x*afterRatio) + (afterPos.x*ratio)),
                (beforePos.y*afterRatio) + (afterPos.y*ratio))
        uiLayer.addRect(pos.x,pos.y,pos.x+size,pos.y+size,blockTx.get(),tColor)
    }

    private fun makeRandomStage() {
        do {
            for (i in 0 until blocks.size) {
                blocks[i] = rand.nextInt(blockCount) + 1
            }
        } while(checkMatch())
        selectedPos.clear()
        score = 0
        combo = 1
    }

    private fun swapBlock(p1:Point, p2:Point) {
        if (p2.y >= 0) {
            val src = blocks[getAdr(p1)]
            blocks[getAdr(p1)] = blocks[getAdr(p2)]
            blocks[getAdr(p2)] = src
        }
    }

    private fun findBrokenBotom(i:Int, j:Int):Int {
        return (j until height).firstOrNull { blocks[getAdrXY(i, it)] == 0 }
                ?: -1
    }

    override fun draw(clock: Long) {
        if (status == Status.CHANGE) {
            if (selectedPos.size == 2) {
                drawMoveSlot(selectedPos[0].x,selectedPos[0].y,
                        selectedPos[1].x,selectedPos[1].y,statusClock.toFloat()/changeClock,
                        TColor.IDX[blocks[getAdr(selectedPos[0])]])
                drawMoveSlot(selectedPos[1].x,selectedPos[1].y,
                        selectedPos[0].x,selectedPos[0].y,statusClock.toFloat()/changeClock,
                        TColor.IDX[blocks[getAdr(selectedPos[1])]])
            }
        }

        val bright = ((Math.cos(clock.toDouble()/30.0*Math.PI))/8).toFloat()
        for (j in 1 until height-1) {
            for (i in 1 until width-1) {
                val bot = findBrokenBotom(i,j)
                if (bot != -1) {
                    val upIdx = blocks[getAdrXY(i, j-1)]
                    if (upIdx > 0) {
                        drawMoveSlot(i, j - 1,
                                i, j,
                                statusClock.toFloat()/changeClock,
                                TColor.IDX[upIdx])
                    }
                    continue
                }
                val idx = blocks[getAdrXY(i, j)]
                val color = TColor.IDX[idx]
                when(status) {
                    Status.CRASH->
                        if (selectedPos.any { it.equals(i,j) }) {
                            drawCrashSlot(i, j, statusClock.toFloat()/changeClock,color)
                        } else {
                            drawSlot(i, j, color)
                        }
                    Status.CHANGE->
                        if (selectedPos.any { it.equals(i,j) }) {
                        } else {
                            drawSlot(i, j, color)
                        }
                    Status.BREAKDOWN->
                        if (selectedPos.any { it.equals(i,j) } ||
                                selectedPos.any { it.equals(i,j+1) } ) {
                        } else {
                            drawSlot(i, j, color)
                        }
                    else ->
                            if (selectedPos.any { it.equals(i,j) }) {
                                drawSlot(i, j, color.addRGB(bright))
                            } else {
                                drawSlot(i, j, color)
                            }
                }
            }
        }
        for (j in 0 until height) {
            for (i in 0 until width) {
                if (i <= 0 || j <= 0 || i >= width - 1 || j >= height - 1) {
                    drawSlot(i, j, TColor.GRAY)
                    continue
                }
            }
        }
        uiLayer.drawText(50f,150f,50f, Paint.Align.LEFT, "점수 $score",TColor.WHITE)
    }

    private fun checkMatch():Boolean {
        for (j in 1 until height-1) {
            for (i in 1 until width-1) {
                val src = blocks[getAdrXY(i, j)]

                var count = 1 + (1 until 5)
                        .takeWhile { i+ it < width-1 && src == blocks[getAdrXY(i+ it, j)] }
                        .count()
                if (count >= 3) {
                    //score += count*combo
                    (0 until count)
                            .map {
                                Point(i+ it, j)
                            }
                            .forEach { selectedPos.add(it) }
                    return true
                }

                count = 1 + (1 until 5)
                        .takeWhile { j+ it < height-1 &&src == blocks[getAdrXY(i, j+ it)] }
                        .count()
                if (count >= 3) {
                    //score += count*combo
                    (0 until count)
                            .map {
                                Point(i, j+ it)
                                //blocks[getAdr(pos)] = 0
                            }
                            .forEach { selectedPos.add(it) }
                    return true
                }
            }
        }
        return false
    }

    override fun act(clock: Long, touchEV: TouchEV) : MyGame? {
        val prevStatus = status
        val point = touchEV.getPress()
        val choice =
        if (point != null && getRectF(1,1,width-1,height-1).contains(point.x,point.y)) {
            Point((point.x/size-pad).toInt(),((point.y-topPad())/size-pad).toInt())
        } else {
            null
        }

        if (resetButton is ButtonObj) {
            if (resetButton.press()) {
                makeRandomStage()
            }
        }

        when(status) {
            Status.WAIT -> if (choice != null) {
                selectedPos.add(choice)
                status = Status.CHOOSE
                combo = 1
            }
            Status.CHOOSE ->
                if (selectedPos.size != 1) {
                    selectedPos.clear()
                    status = Status.WAIT
                } else {
                    if (choice != null && choice != selectedPos[0]) {
                        status = if (Math.abs(choice.x-selectedPos[0].x)+Math.abs(choice.y-selectedPos[0].y)==1) {
                            selectedPos.add(choice)
                            Status.CHANGE
                        } else {
                            selectedPos.clear()
                            Status.WAIT
                        }
                    }
                }
            Status.CHANGE ->
                if (selectedPos.size != 2) {
                    selectedPos.clear()
                    status = Status.WAIT
                } else {
                    if (statusClock >= changeClock) {
                        swapBlock(selectedPos[0],selectedPos[1])
                        selectedPos.clear()
                        status = if(checkMatch()) {
                            Status.CRASH
                        } else {
                            Status.WAIT
                        }
                    }
                }
            Status.CRASH -> {
                if (statusClock >= changeClock) {
                    for (s in selectedPos) {
                        blocks[getAdr(s)] = 0
                    }
                    val cx = selectedPos.sumBy { it.x } / selectedPos.size
                    val cy = selectedPos.sumBy { it.y } / selectedPos.size
                    val addScore = selectedPos.size*(combo*combo)
                    uiLayer.addUI(ScoreUIObj(clock,addScore,getX(cx),getY(cy)))
                    score += addScore
                    selectedPos.clear()
                    status = Status.BREAKDOWN
                    speed = 2
                }
            }
            Status.BREAKDOWN ->
                if (statusClock >= changeClock) {
                    val nextBlocks = blocks.clone()
                    var zeroBlock = false
                    for (j in 1 until height) {
                        for (i in 0 until width) {
                            val adr = getAdrXY(i,j)

                            if (blocks[adr] == 0) {
                                for (y in 1 .. j) {
                                    val up = getAdrXY(i,y-1)
                                    nextBlocks[getAdrXY(i,y)] = blocks[up]
                                }
                                zeroBlock = true
                            }
                        }
                    }
                    for (i in 0 until width) {
                        nextBlocks[getAdrXY(i,0)] = rand.nextInt(blockCount)+1
//                        nextBlocks[getAdrXY(i,0)] = 1
                    }
                    blocks=nextBlocks
                    if (!zeroBlock) {
                        status = if(checkMatch()) {
                            combo ++
                            speed = 1
                            Status.CRASH
                        } else {
                            Status.WAIT
                        }
                    }
                    statusClock -= changeClock
                } else {
                    speed ++
                    if (speed > 10) {
                        speed = 10
                    }
                }
        }
        if (prevStatus != status) {
            statusClock = 0
            speed = 1
        } else {
            statusClock += speed+1
        }
        return null
    }
    override fun begin(clock:Long) {
        activity.soundMgr.playMedia(R.raw.bgm)
    }
    override fun end(clock:Long) {
    }

}