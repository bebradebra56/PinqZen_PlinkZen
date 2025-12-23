package com.pinqze.softclu.vbnjk.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.pinqze.softclu.vbnjk.presentation.di.plinkZenModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface PlinkZenAppsFlyerState {
    data object PlinkZenDefault : PlinkZenAppsFlyerState
    data class PlinkZenSuccess(val plinkZenData: MutableMap<String, Any>?) :
        PlinkZenAppsFlyerState

    data object PlinkZenError : PlinkZenAppsFlyerState
}

interface PlinkZenAppsApi {
    @Headers("Content-Type: application/json")
    @GET(PLINK_ZEN_LIN)
    fun plinkZenGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val PLINK_ZEN_APP_DEV = "ScszsFnAvQnAKFxqWQtm3H"
private const val PLINK_ZEN_LIN = "com.pinqze.softclu"

class PlinkZenApplication : Application() {

    private var plinkZenIsResumed = false
//    private var plinkZenConversionTimeoutJob: Job? = null
    private var plinkZenDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        plinkZenSetDebufLogger(appsflyer)
        plinkZenMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        plinkZenExtractDeepMap(p0.deepLink)
                        Log.d(PLINK_ZEN_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(PLINK_ZEN_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(PLINK_ZEN_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            PLINK_ZEN_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
//                    plinkZenConversionTimeoutJob?.cancel()
                    Log.d(PLINK_ZEN_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = plinkZenGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.plinkZenGetClient(
                                    devkey = PLINK_ZEN_APP_DEV,
                                    deviceId = plinkZenGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(PLINK_ZEN_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    plinkZenResume(
                                        PlinkZenAppsFlyerState.PlinkZenSuccess(
                                            p0
                                        )
                                    )
                                } else {
                                    plinkZenResume(
                                        PlinkZenAppsFlyerState.PlinkZenSuccess(
                                            resp
                                        )
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(PLINK_ZEN_MAIN_TAG, "Error: ${d.message}")
                                plinkZenResume(PlinkZenAppsFlyerState.PlinkZenError)
                            }
                        }
                    } else {
                        plinkZenResume(PlinkZenAppsFlyerState.PlinkZenSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
//                    plinkZenConversionTimeoutJob?.cancel()
                    Log.d(PLINK_ZEN_MAIN_TAG, "onConversionDataFail: $p0")
                    plinkZenResume(PlinkZenAppsFlyerState.PlinkZenError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(PLINK_ZEN_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(PLINK_ZEN_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, PLINK_ZEN_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(PLINK_ZEN_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(PLINK_ZEN_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
//        plinkZenStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@PlinkZenApplication)
            modules(
                listOf(
                    plinkZenModule
                )
            )
        }
    }

    private fun plinkZenExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(PLINK_ZEN_MAIN_TAG, "Extracted DeepLink data: $map")
        plinkZenDeepLinkData = map
    }

//    private fun plinkZenStartConversionTimeout() {
//        plinkZenConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
//            delay(30000)
//            if (!plinkZenIsResumed) {
//                Log.d(PLINK_ZEN_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
//                plinkZenResume(PlinkZenAppsFlyerState.PlinkZenError)
//            }
//        }
//    }

    private fun plinkZenResume(state: PlinkZenAppsFlyerState) {
//        plinkZenConversionTimeoutJob?.cancel()
        if (state is PlinkZenAppsFlyerState.PlinkZenSuccess) {
            val convData = state.plinkZenData ?: mutableMapOf()
            val deepData = plinkZenDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!plinkZenIsResumed) {
                plinkZenIsResumed = true
                plinkZenConversionFlow.value =
                    PlinkZenAppsFlyerState.PlinkZenSuccess(merged)
            }
        } else {
            if (!plinkZenIsResumed) {
                plinkZenIsResumed = true
                plinkZenConversionFlow.value = state
            }
        }
    }

    private fun plinkZenGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(PLINK_ZEN_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun plinkZenSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun plinkZenMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun plinkZenGetApi(url: String, client: OkHttpClient?): PlinkZenAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {

        var plinkZenInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val plinkZenConversionFlow: MutableStateFlow<PlinkZenAppsFlyerState> = MutableStateFlow(
            PlinkZenAppsFlyerState.PlinkZenDefault
        )
        var PLINK_ZEN_FB_LI: String? = null
        const val PLINK_ZEN_MAIN_TAG = "PlinkZenMainTag"
    }
}