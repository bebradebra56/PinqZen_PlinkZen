package com.pinqze.softclu.vbnjk.domain.usecases

import android.util.Log
import com.pinqze.softclu.vbnjk.data.repo.PlinkZenRepository
import com.pinqze.softclu.vbnjk.data.utils.PlinkZenPushToken
import com.pinqze.softclu.vbnjk.data.utils.PlinkZenSystemService
import com.pinqze.softclu.vbnjk.domain.model.PlinkZenEntity
import com.pinqze.softclu.vbnjk.domain.model.PlinkZenParam
import com.pinqze.softclu.vbnjk.presentation.app.PlinkZenApplication

class PlinkZenGetAllUseCase(
    private val plinkZenRepository: PlinkZenRepository,
    private val plinkZenSystemService: PlinkZenSystemService,
    private val plinkZenPushToken: PlinkZenPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : PlinkZenEntity?{
        val params = PlinkZenParam(
            plinkZenLocale = plinkZenSystemService.plinkZenGetLocale(),
            plinkZenPushToken = plinkZenPushToken.plinkZenGetToken(),
            plinkZenAfId = plinkZenSystemService.plinkZenGetAppsflyerId()
        )
        Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "Params for request: $params")
        return plinkZenRepository.plinkZenGetClient(params, conversion)
    }



}