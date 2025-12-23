package com.pinqze.softclu.vbnjk.domain.model

import com.google.gson.annotations.SerializedName


data class PlinkZenEntity (
    @SerializedName("ok")
    val plinkZenOk: String,
    @SerializedName("url")
    val plinkZenUrl: String,
    @SerializedName("expires")
    val plinkZenExpires: Long,
)