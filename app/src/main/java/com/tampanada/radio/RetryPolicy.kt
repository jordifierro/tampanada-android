package com.tampanada.radio

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
import com.google.android.exoplayer2.upstream.HttpDataSource
import java.io.IOException


class RetryPolicy : DefaultLoadErrorHandlingPolicy() {
    override fun getRetryDelayMsFor(
        dataType: Int,
        loadDurationMs: Long,
        exception: IOException?,
        errorCount: Int
    ): Long {
        return if (exception is HttpDataSource.HttpDataSourceException) {
            5000
        } else {
            C.TIME_UNSET
        }
    }

    override fun getMinimumLoadableRetryCount(dataType: Int): Int {
        return Int.MAX_VALUE
    }
}