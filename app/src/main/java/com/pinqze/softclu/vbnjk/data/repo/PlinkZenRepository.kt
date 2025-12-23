package com.pinqze.softclu.vbnjk.data.repo

import android.util.Log
import com.pinqze.softclu.vbnjk.domain.model.PlinkZenEntity
import com.pinqze.softclu.vbnjk.domain.model.PlinkZenParam
import com.pinqze.softclu.vbnjk.presentation.app.PlinkZenApplication.Companion.PLINK_ZEN_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PlinkZenApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun plinkZenGetClient(
        @Body jsonString: JsonObject,
    ): Call<PlinkZenEntity>
}


private const val PLINK_ZEN_MAIN = "https://pinqzen.com/"
class PlinkZenRepository {

    suspend fun plinkZenGetClient(
        plinkZenParam: PlinkZenParam,
        plinkZenConversion: MutableMap<String, Any>?
    ): PlinkZenEntity? {
        val gson = Gson()
        val api = plinkZenGetApi(PLINK_ZEN_MAIN, null)

        val plinkZenJsonObject = gson.toJsonTree(plinkZenParam).asJsonObject
        plinkZenConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            plinkZenJsonObject.add(key, element)
        }
        return try {
            val plinkZenRequest: Call<PlinkZenEntity> = api.plinkZenGetClient(
                jsonString = plinkZenJsonObject,
            )
            val plinkZenResult = plinkZenRequest.awaitResponse()
            Log.d(PLINK_ZEN_MAIN_TAG, "Retrofit: Result code: ${plinkZenResult.code()}")
            if (plinkZenResult.code() == 200) {
                Log.d(PLINK_ZEN_MAIN_TAG, "Retrofit: Get request success")
                Log.d(PLINK_ZEN_MAIN_TAG, "Retrofit: Code = ${plinkZenResult.code()}")
                Log.d(PLINK_ZEN_MAIN_TAG, "Retrofit: ${plinkZenResult.body()}")
                plinkZenResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(PLINK_ZEN_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(PLINK_ZEN_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun plinkZenGetApi(url: String, client: OkHttpClient?) : PlinkZenApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
