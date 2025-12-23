package com.pinqze.softclu.vbnjk.presentation.ui.view

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlinkZenViFun(private val context: Context) {
    fun plinkZenSavePhoto() : Uri {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
        val df = sdf.format(Date())
        val dir = context.filesDir.absoluteFile
        if (!dir.exists()) {
            dir.mkdir()
        }
        return FileProvider.getUriForFile(
            context,
            "com.pinqze.softcluuuuuuu",
            File(dir, "/$df.jpg")
        )
    }

}