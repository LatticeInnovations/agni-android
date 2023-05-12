package com.latticeonfhir.android.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.latticeonfhir.android.base.activity.BaseActivity
import com.latticeonfhir.android.navigation.NavigationAppHost
import com.latticeonfhir.android.ui.theme.FHIRAndroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            FHIRAndroidTheme {
                val navController = rememberNavController()
                NavigationAppHost(navController = navController)
            }
        }
        viewModel.toString()
    }

    override fun viewModel() = viewModel
}