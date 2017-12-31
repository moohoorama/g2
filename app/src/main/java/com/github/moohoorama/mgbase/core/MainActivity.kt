package com.github.moohoorama.mgbase.core

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.LinearLayout
import com.github.moohoorama.mgbase.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class MainActivity : Activity() {
    private val handler = Handler()

    lateinit var glView: MyGLSurfaveView

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
        mLostFocus = true
        glView.onPause()

        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        mToBeResumed = mLostFocus
        if (!mLostFocus) {
            glView.onResume()
            glView.renderer.reload()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        mLostFocus = !hasFocus
        if (mToBeResumed && hasFocus) {
            mToBeResumed = false
            glView.onResume()
        }
    }
}
