package com.pinqze.softclu.vbnjk.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinqze.softclu.vbnjk.data.shar.PlinkZenSharedPreference
import com.pinqze.softclu.vbnjk.data.utils.PlinkZenSystemService
import com.pinqze.softclu.vbnjk.domain.usecases.PlinkZenGetAllUseCase
import com.pinqze.softclu.vbnjk.presentation.app.PlinkZenAppsFlyerState
import com.pinqze.softclu.vbnjk.presentation.app.PlinkZenApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlinkZenLoadViewModel(
    private val plinkZenGetAllUseCase: PlinkZenGetAllUseCase,
    private val plinkZenSharedPreference: PlinkZenSharedPreference,
    private val plinkZenSystemService: PlinkZenSystemService
) : ViewModel() {

    private val _plinkZenHomeScreenState: MutableStateFlow<PlinkZenHomeScreenState> =
        MutableStateFlow(PlinkZenHomeScreenState.PlinkZenLoading)
    val plinkZenHomeScreenState = _plinkZenHomeScreenState.asStateFlow()

    private var plinkZenGetApps = false


    init {
        viewModelScope.launch {
            when (plinkZenSharedPreference.plinkZenAppState) {
                0 -> {
                    if (plinkZenSystemService.plinkZenIsOnline()) {
                        PlinkZenApplication.plinkZenConversionFlow.collect {
                            when(it) {
                                PlinkZenAppsFlyerState.PlinkZenDefault -> {}
                                PlinkZenAppsFlyerState.PlinkZenError -> {
                                    plinkZenSharedPreference.plinkZenAppState = 2
                                    _plinkZenHomeScreenState.value =
                                        PlinkZenHomeScreenState.PlinkZenError
                                    plinkZenGetApps = true
                                }
                                is PlinkZenAppsFlyerState.PlinkZenSuccess -> {
                                    if (!plinkZenGetApps) {
                                        plinkZenGetData(it.plinkZenData)
                                        plinkZenGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _plinkZenHomeScreenState.value =
                            PlinkZenHomeScreenState.PlinkZenNotInternet
                    }
                }
                1 -> {
                    if (plinkZenSystemService.plinkZenIsOnline()) {
                        if (PlinkZenApplication.PLINK_ZEN_FB_LI != null) {
                            _plinkZenHomeScreenState.value =
                                PlinkZenHomeScreenState.PlinkZenSuccess(
                                    PlinkZenApplication.PLINK_ZEN_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > plinkZenSharedPreference.plinkZenExpired) {
                            Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "Current time more then expired, repeat request")
                            PlinkZenApplication.plinkZenConversionFlow.collect {
                                when(it) {
                                    PlinkZenAppsFlyerState.PlinkZenDefault -> {}
                                    PlinkZenAppsFlyerState.PlinkZenError -> {
                                        _plinkZenHomeScreenState.value =
                                            PlinkZenHomeScreenState.PlinkZenSuccess(
                                                plinkZenSharedPreference.plinkZenSavedUrl
                                            )
                                        plinkZenGetApps = true
                                    }
                                    is PlinkZenAppsFlyerState.PlinkZenSuccess -> {
                                        if (!plinkZenGetApps) {
                                            plinkZenGetData(it.plinkZenData)
                                            plinkZenGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "Current time less then expired, use saved url")
                            _plinkZenHomeScreenState.value =
                                PlinkZenHomeScreenState.PlinkZenSuccess(
                                    plinkZenSharedPreference.plinkZenSavedUrl
                                )
                        }
                    } else {
                        _plinkZenHomeScreenState.value =
                            PlinkZenHomeScreenState.PlinkZenNotInternet
                    }
                }
                2 -> {
                    _plinkZenHomeScreenState.value =
                        PlinkZenHomeScreenState.PlinkZenError
                }
            }
        }
    }


    private suspend fun plinkZenGetData(conversation: MutableMap<String, Any>?) {
        val plinkZenData = plinkZenGetAllUseCase.invoke(conversation)
        if (plinkZenSharedPreference.plinkZenAppState == 0) {
            if (plinkZenData == null) {
                plinkZenSharedPreference.plinkZenAppState = 2
                _plinkZenHomeScreenState.value =
                    PlinkZenHomeScreenState.PlinkZenError
            } else {
                plinkZenSharedPreference.plinkZenAppState = 1
                plinkZenSharedPreference.apply {
                    plinkZenExpired = plinkZenData.plinkZenExpires
                    plinkZenSavedUrl = plinkZenData.plinkZenUrl
                }
                _plinkZenHomeScreenState.value =
                    PlinkZenHomeScreenState.PlinkZenSuccess(plinkZenData.plinkZenUrl)
            }
        } else  {
            if (plinkZenData == null) {
                _plinkZenHomeScreenState.value =
                    PlinkZenHomeScreenState.PlinkZenSuccess(plinkZenSharedPreference.plinkZenSavedUrl)
            } else {
                plinkZenSharedPreference.apply {
                    plinkZenExpired = plinkZenData.plinkZenExpires
                    plinkZenSavedUrl = plinkZenData.plinkZenUrl
                }
                _plinkZenHomeScreenState.value =
                    PlinkZenHomeScreenState.PlinkZenSuccess(plinkZenData.plinkZenUrl)
            }
        }
    }


    sealed class PlinkZenHomeScreenState {
        data object PlinkZenLoading : PlinkZenHomeScreenState()
        data object PlinkZenError : PlinkZenHomeScreenState()
        data class PlinkZenSuccess(val data: String) : PlinkZenHomeScreenState()
        data object PlinkZenNotInternet: PlinkZenHomeScreenState()
    }
}