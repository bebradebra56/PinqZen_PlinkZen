package com.pinqze.softclu.vbnjk.data.shar

import android.content.Context
import androidx.core.content.edit

class PlinkZenSharedPreference(context: Context) {
    private val plinkZenPrefs = context.getSharedPreferences("plinkZenSharedPrefsAb", Context.MODE_PRIVATE)

    var plinkZenSavedUrl: String
        get() = plinkZenPrefs.getString(PLINK_ZEN_SAVED_URL, "") ?: ""
        set(value) = plinkZenPrefs.edit { putString(PLINK_ZEN_SAVED_URL, value) }

    var plinkZenExpired : Long
        get() = plinkZenPrefs.getLong(PLINK_ZEN_EXPIRED, 0L)
        set(value) = plinkZenPrefs.edit { putLong(PLINK_ZEN_EXPIRED, value) }

    var plinkZenAppState: Int
        get() = plinkZenPrefs.getInt(PLINK_ZEN_APPLICATION_STATE, 0)
        set(value) = plinkZenPrefs.edit { putInt(PLINK_ZEN_APPLICATION_STATE, value) }

    var plinkZenNotificationRequest: Long
        get() = plinkZenPrefs.getLong(PLINK_ZEN_NOTIFICAITON_REQUEST, 0L)
        set(value) = plinkZenPrefs.edit { putLong(PLINK_ZEN_NOTIFICAITON_REQUEST, value) }

    var plinkZenNotificationRequestedBefore: Boolean
        get() = plinkZenPrefs.getBoolean(PLINK_ZEN_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = plinkZenPrefs.edit { putBoolean(
            PLINK_ZEN_NOTIFICATION_REQUEST_BEFORE, value) }

    companion object {
        private const val PLINK_ZEN_SAVED_URL = "plinkZenSavedUrl"
        private const val PLINK_ZEN_EXPIRED = "plinkZenExpired"
        private const val PLINK_ZEN_APPLICATION_STATE = "plinkZenApplicationState"
        private const val PLINK_ZEN_NOTIFICAITON_REQUEST = "plinkZenNotificationRequest"
        private const val PLINK_ZEN_NOTIFICATION_REQUEST_BEFORE = "plinkZenNotificationRequestedBefore"
    }
}