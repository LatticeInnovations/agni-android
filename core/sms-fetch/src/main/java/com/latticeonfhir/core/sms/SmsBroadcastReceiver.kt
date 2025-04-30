package com.latticeonfhir.core.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.latticeonfhir.core.sms.OtpRegex.otpPattern
import java.util.regex.Pattern

class SmsBroadcastReceiver(private val onOtpReceived: (String) -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val retrieveSMSStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (retrieveSMSStatus.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                    val otpPattern = Pattern.compile(otpPattern.toString())
                    val matcher = otpPattern.matcher(message.toString())
                    if (matcher.find()) {
                        onOtpReceived(matcher.group(0) as String)
                    }
                }

                CommonStatusCodes.TIMEOUT -> {
                    // Handle timeout error
                }
            }
        }
    }
}