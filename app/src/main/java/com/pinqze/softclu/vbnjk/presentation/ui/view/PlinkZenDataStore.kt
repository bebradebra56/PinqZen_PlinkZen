package com.pinqze.softclu.vbnjk.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class PlinkZenDataStore : ViewModel(){
    val plinkZenViList: MutableList<PlinkZenVi> = mutableListOf()
    var plinkZenIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var plinkZenContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var plinkZenView: PlinkZenVi

}