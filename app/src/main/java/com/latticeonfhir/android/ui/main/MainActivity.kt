package com.latticeonfhir.android.ui.main

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.latticeonfhir.android.base.activity.BaseActivity
import com.latticeonfhir.android.navigation.NavigationAppHost
import com.latticeonfhir.android.ui.theme.FHIRAndroidTheme
import com.latticeonfhir.android.utils.regex.OtpRegex.otpPattern
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.regex.Pattern

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private val SMS_VERIFICATION_REQUEST = 2
    var otp by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            FHIRAndroidTheme {
                val navController = rememberNavController()
                NavigationAppHost(navController = navController, startDest = viewModel.startDestination)
            }
        }
        viewModel.toString()
    }

    private val smsVerificationBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val retrieveSMSStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

                when (retrieveSMSStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        // Retrieve sms consent intent
                        val smsConsentIntent =
                            extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                        try {
                            // Display sms consent dialog
                            if (smsConsentIntent != null) {
                                startActivityForResult(smsConsentIntent, SMS_VERIFICATION_REQUEST)
                            }
                        } catch (e: ActivityNotFoundException) {
                            e.printStackTrace()
                        }
                    }

                    CommonStatusCodes.TIMEOUT -> {
                        // Handle timeout error
                        Timber.d("manseeyy in timeout")
                    }
                }
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SMS_VERIFICATION_REQUEST ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    val otpPattern = Pattern.compile(otpPattern.toString())
                    val matcher = otpPattern.matcher(message)
                    if (matcher.find()) {
                        otp = matcher.group(0) as String
                    }
                } else {
                    // Sms user consent denied
                    Timber.d("manseeyy request denied")
                }
        }
    }

    fun registerBroadcastReceiver(){
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsVerificationBroadcastReceiver, intentFilter)
    }

    fun unregisterBroadcastReceiver(){
        unregisterReceiver(smsVerificationBroadcastReceiver)
    }

    override fun viewModel() = viewModel
}