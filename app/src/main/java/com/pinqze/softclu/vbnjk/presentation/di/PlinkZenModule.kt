package com.pinqze.softclu.vbnjk.presentation.di

import com.pinqze.softclu.vbnjk.data.repo.PlinkZenRepository
import com.pinqze.softclu.vbnjk.data.shar.PlinkZenSharedPreference
import com.pinqze.softclu.vbnjk.data.utils.PlinkZenPushToken
import com.pinqze.softclu.vbnjk.data.utils.PlinkZenSystemService
import com.pinqze.softclu.vbnjk.domain.usecases.PlinkZenGetAllUseCase
import com.pinqze.softclu.vbnjk.presentation.pushhandler.PlinkZenPushHandler
import com.pinqze.softclu.vbnjk.presentation.ui.load.PlinkZenLoadViewModel
import com.pinqze.softclu.vbnjk.presentation.ui.view.PlinkZenViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val plinkZenModule = module {
    factory {
        PlinkZenPushHandler()
    }
    single {
        PlinkZenRepository()
    }
    single {
        PlinkZenSharedPreference(get())
    }
    factory {
        PlinkZenPushToken()
    }
    factory {
        PlinkZenSystemService(get())
    }
    factory {
        PlinkZenGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        PlinkZenViFun(get())
    }
    viewModel {
        PlinkZenLoadViewModel(get(), get(), get())
    }
}