package com.latticeonfhir.core.sms

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import com.google.android.gms.auth.api.phone.SmsRetriever
import timber.log.Timber

class OtpRetriever(
    private val context: Context,
    private val onOtpReceived: (String) -> Unit
) {
    private var receiver: SmsBroadcastReceiver? = null

    private fun startSMSRetrieverClient() {
        val client = SmsRetriever.getClient(context)
        val task = client.startSmsRetriever()
        task.addOnFailureListener { e ->
            Timber.e(e.localizedMessage)
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun startFetchingOtp() {
        startSMSRetrieverClient()
        receiver = SmsBroadcastReceiver(onOtpReceived)

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(receiver, intentFilter)
        }

        SmsRetriever.getClient(context).startSmsRetriever()
    }

    fun stopFetchingOtp() {
        receiver?.let {
            context.unregisterReceiver(it)
        }
        receiver = null
    }
}