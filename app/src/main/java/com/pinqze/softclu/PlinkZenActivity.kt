package com.pinqze.softclu

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.pinqze.softclu.vbnjk.PlinkZenGlobalLayoutUtil
import com.pinqze.softclu.vbnjk.presentation.app.PlinkZenApplication
import com.pinqze.softclu.vbnjk.presentation.pushhandler.PlinkZenPushHandler
import com.pinqze.softclu.vbnjk.plinkZenSetupSystemBars
import org.koin.android.ext.android.inject

class PlinkZenActivity : AppCompatActivity() {

    private val plinkZenPushHandler by inject<PlinkZenPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        plinkZenSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_plink_zen)

        val plinkZenRootView = findViewById<View>(android.R.id.content)
        PlinkZenGlobalLayoutUtil().plinkZenAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(plinkZenRootView) { plinkZenView, plinkZenInsets ->
            val plinkZenSystemBars = plinkZenInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val plinkZenDisplayCutout = plinkZenInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val plinkZenIme = plinkZenInsets.getInsets(WindowInsetsCompat.Type.ime())


            val plinkZenTopPadding = maxOf(plinkZenSystemBars.top, plinkZenDisplayCutout.top)
            val plinkZenLeftPadding = maxOf(plinkZenSystemBars.left, plinkZenDisplayCutout.left)
            val plinkZenRightPadding = maxOf(plinkZenSystemBars.right, plinkZenDisplayCutout.right)
            window.setSoftInputMode(PlinkZenApplication.plinkZenInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "ADJUST PUN")
                val plinkZenBottomInset = maxOf(plinkZenSystemBars.bottom, plinkZenDisplayCutout.bottom)

                plinkZenView.setPadding(plinkZenLeftPadding, plinkZenTopPadding, plinkZenRightPadding, 0)

                plinkZenView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = plinkZenBottomInset
                }
            } else {
                Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "ADJUST RESIZE")

                val plinkZenBottomInset = maxOf(plinkZenSystemBars.bottom, plinkZenDisplayCutout.bottom, plinkZenIme.bottom)

                plinkZenView.setPadding(plinkZenLeftPadding, plinkZenTopPadding, plinkZenRightPadding, 0)

                plinkZenView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = plinkZenBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "Activity onCreate()")
        plinkZenPushHandler.plinkZenHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            plinkZenSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        plinkZenSetupSystemBars()
    }
}