package com.latticeonfhir.android.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
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
import com.latticeonfhir.android.utils.network.ConnectivityObserver
import com.latticeonfhir.android.utils.network.NetworkConnectivityObserver
import com.latticeonfhir.android.utils.regex.OtpRegex.otpPattern
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.regex.Pattern

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val viewModel by viewModels<MainViewModel>()
    var otp by mutableStateOf("")
    private lateinit var connectivityObserver: ConnectivityObserver
    lateinit var connectivityStatus: State<ConnectivityObserver.Status>

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        connectivityObserver = NetworkConnectivityObserver(applicationContext)
        setContent {
            FHIRAndroidTheme {
                connectivityStatus = connectivityObserver.observe()
                    .collectAsState(initial = ConnectivityObserver.Status.Unavailable)
                val navController = rememberNavController()
                NavigationAppHost(
                    navController = navController,
                    startDest = viewModel.startDestination
                )
            }
        }
    }

    private fun startSMSRetrieverClient() {
        val client = SmsRetriever.getClient(this)
        val task = client.startSmsRetriever()
        task.addOnFailureListener { e ->
            Timber.e(e.localizedMessage)
        }
    }

    private val smsVerificationBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val retrieveSMSStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

                when (retrieveSMSStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE)
                        val otpPattern = Pattern.compile(otpPattern.toString())
                        val matcher = otpPattern.matcher(message.toString())
                        if (matcher.find()) {
                            otp = matcher.group(0) as String
                        }
                    }

                    CommonStatusCodes.TIMEOUT -> {
                        // Handle timeout error
                    }
                }
            }
        }
    }

    fun registerBroadcastReceiver() {
        startSMSRetrieverClient()
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(smsVerificationBroadcastReceiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(smsVerificationBroadcastReceiver, intentFilter)
        }
    }

    fun unregisterBroadcastReceiver() {
        unregisterReceiver(smsVerificationBroadcastReceiver)
    }

    override fun viewModel() = viewModel
}