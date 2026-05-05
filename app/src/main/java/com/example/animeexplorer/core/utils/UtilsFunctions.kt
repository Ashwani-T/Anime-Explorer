package com.example.animeexplorer.core.utils

import android.util.Log
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlin.coroutines.cancellation.CancellationException

suspend fun <T> safeApiCall(
    tag: String = "API_CALL",
    call: suspend ()->T
): T?{
    return try {
        call()
    }catch (e: CancellationException){
        throw e
    }catch (e: Exception){
        Log.e(tag, "API call failed: ${e.message}")
        null
    }
}