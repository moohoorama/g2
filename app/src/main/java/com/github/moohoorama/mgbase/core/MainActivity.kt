package com.github.moohoorama.mgbase.core

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import com.github.moohoorama.mgbase.R
import com.github.moohoorama.mgbase.Sound.SoundMgr
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.internal.zzahn

class MainActivity : Activity() {
    private val handler = Handler()

    lateinit var glView: MyGLSurfaveView
    var soundMgr=SoundMgr(this, 64)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_layout)

        var layout = findViewById<View>(R.id.main_layout) as LinearLayout

        MobileAds.initialize(this,getString(R.string.interstitial_ad_app_id))
        var adView = findViewById<AdView>(R.id.adView)
//        adView.adSize = AdSize.SMART_BANNER
//        adView.adUnitId = getString(R.string.ad_unit_id)
        val adRequest = AdRequest.Builder().addTestDevice("0EEA64C597E1820057051401DA6757AA").addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build()
        adView.loadAd(adRequest)

        glView= MyGLSurfaveView(this)
        layout.addView(glView)
    }

    private var mLostFocus = false
    private var mToBeResumed = false

    override fun onPause() {
        super.onPause()

        mLostFocus = true
        glView.onPause()
        soundMgr.onPause()
    }

    override fun onResume() {
        super.onResume()

        Log.i("onResume", "$mToBeResumed $mLostFocus")

        mToBeResumed = mLostFocus
        if (!mLostFocus) {
            glView.onResume()
            glView.renderer.reload()
            soundMgr.onResume()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        Log.i("onWindowFocusChanged", "$mToBeResumed $mLostFocus $hasFocus")

        mLostFocus = !hasFocus
        if (mToBeResumed && hasFocus) {
            mToBeResumed = false
            glView.onResume()
            glView.renderer.reload()
            soundMgr.onResume()
        }
    }

    private var onReadText = false
    fun readText(title:String, cb:(String,Boolean)->Unit) {
        zzahn.runOnUiThread(
                {
                    if (onReadText) {
                        return@runOnUiThread
                    }
                    onReadText = true
                    var builder= AlertDialog.Builder(this)
                    builder.setTitle(title)

                    val input = EditText(this)
    //        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    input.inputType = InputType.TYPE_CLASS_TEXT
                    builder.setView(input)

                    builder.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ -> onReadText=false;cb(input.text.toString(), true) })
                    builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ -> onReadText=false;cb("", false);dialog.cancel() })

                    builder.show()
                }
        )
    }
}
