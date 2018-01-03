package com.github.moohoorama.mgbase.Sound

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import com.github.moohoorama.mgbase.core.MainActivity

/**
 * Created by kyw on 2018-01-04
 */
class SoundMgr(private val activity: MainActivity, maxStream:Int) {
    init {
        activity.soundMgr=this
    }

    private var soundPool: SoundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        SoundPool.Builder().setMaxStreams(maxStream).build();
    } else {
        SoundPool(maxStream, AudioManager.STREAM_MUSIC, 1);
    }
    private var media: MediaPlayer = MediaPlayer()
    private var playPosition:Int = 0
    private var soundMap=HashMap<Int,SoundInfo>()

    data class SoundInfo(val rid:Int, var ts:Long)

    fun playSound(resourceID: Int, gapMs:Int) {
        val si = soundMap[resourceID] ?: SoundInfo(soundPool.load(activity, resourceID,1), 0)

        val curTs=System.currentTimeMillis()
        if  (curTs-si.ts > gapMs) {
            while (soundPool.play(si.rid, 1.0f, 1.0f, 1, 1, 1f) == 0) {
            }
            si.ts = System.currentTimeMillis()
        }
        soundMap[resourceID]=si
    }

    fun playMedia(resourceID:Int) {
        media = MediaPlayer.create(activity, resourceID)
        media.start()
    }

    fun onPause() {
        media.pause()
        playPosition =  media.getCurrentPosition();
    }
    fun onResume() {
        media.start()
        media.seekTo(playPosition)
    }
}