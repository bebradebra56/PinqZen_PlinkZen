package com.pinqze.softclu.vbnjk.data.utils

import android.util.Log
import com.pinqze.softclu.vbnjk.presentation.app.PlinkZenApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class PlinkZenPushToken {

    suspend fun plinkZenGetToken(
        plinkZenMaxAttempts: Int = 3,
        plinkZenDelayMs: Long = 1500
    ): String {

        repeat(plinkZenMaxAttempts - 1) {
            try {
                val plinkZenToken = FirebaseMessaging.getInstance().token.await()
                return plinkZenToken
            } catch (e: Exception) {
                Log.e(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(plinkZenDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}