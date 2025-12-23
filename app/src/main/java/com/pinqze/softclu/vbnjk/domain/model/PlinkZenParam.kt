package com.pinqze.softclu.vbnjk.domain.model

import com.google.gson.annotations.SerializedName


private const val PLINK_ZEN_A = "com.pinqze.softclu"
private const val PLINK_ZEN_B = "plinkzen"
data class PlinkZenParam (
    @SerializedName("af_id")
    val plinkZenAfId: String,
    @SerializedName("bundle_id")
    val plinkZenBundleId: String = PLINK_ZEN_A,
    @SerializedName("os")
    val plinkZenOs: String = "Android",
    @SerializedName("store_id")
    val plinkZenStoreId: String = PLINK_ZEN_A,
    @SerializedName("locale")
    val plinkZenLocale: String,
    @SerializedName("push_token")
    val plinkZenPushToken: String,
    @SerializedName("firebase_project_id")
    val plinkZenFirebaseProjectId: String = PLINK_ZEN_B,

    )