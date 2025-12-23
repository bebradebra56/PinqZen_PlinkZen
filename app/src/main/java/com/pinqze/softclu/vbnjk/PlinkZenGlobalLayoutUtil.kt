package com.pinqze.softclu.vbnjk

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.pinqze.softclu.vbnjk.presentation.app.PlinkZenApplication

class PlinkZenGlobalLayoutUtil {

    private var plinkZenMChildOfContent: View? = null
    private var plinkZenUsableHeightPrevious = 0

    fun plinkZenAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        plinkZenMChildOfContent = content.getChildAt(0)

        plinkZenMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val plinkZenUsableHeightNow = plinkZenComputeUsableHeight()
        if (plinkZenUsableHeightNow != plinkZenUsableHeightPrevious) {
            val plinkZenUsableHeightSansKeyboard = plinkZenMChildOfContent?.rootView?.height ?: 0
            val plinkZenHeightDifference = plinkZenUsableHeightSansKeyboard - plinkZenUsableHeightNow

            if (plinkZenHeightDifference > (plinkZenUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(PlinkZenApplication.plinkZenInputMode)
            } else {
                activity.window.setSoftInputMode(PlinkZenApplication.plinkZenInputMode)
            }
//            mChildOfContent?.requestLayout()
            plinkZenUsableHeightPrevious = plinkZenUsableHeightNow
        }
    }

    private fun plinkZenComputeUsableHeight(): Int {
        val r = Rect()
        plinkZenMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}