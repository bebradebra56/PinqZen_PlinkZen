package com.pinqze.softclu.vbnjk.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.pinqze.softclu.vbnjk.presentation.app.PlinkZenApplication

class PlinkZenPushHandler {
    fun plinkZenHandlePush(extras: Bundle?) {
        Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = plinkZenBundleToMap(extras)
            Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    PlinkZenApplication.PLINK_ZEN_FB_LI = map["url"]
                    Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "Push data no!")
        }
    }

    private fun plinkZenBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}