package com.latticeonfhir.android.smsretreiver

import android.app.Activity
import com.google.android.gms.auth.api.phone.SmsRetriever
import timber.log.Timber

fun startSmsRetriever(activity: Activity) {
    SmsRetriever.getClient(activity).also {
        it.startSmsUserConsent(null)
            .addOnSuccessListener {
                Timber.d("manseeyy sms retrieval started")
            }
            .addOnFailureListener {
                Timber.d("manseeyy sms detect fail")
            }
    }
}