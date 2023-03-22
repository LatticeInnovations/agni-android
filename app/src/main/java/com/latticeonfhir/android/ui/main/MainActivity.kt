package com.latticeonfhir.android.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.navigation.compose.rememberNavController
import com.latticeonfhir.android.base.activity.BaseActivity
import com.latticeonfhir.android.navigation.NavigationAppHost
import com.latticeonfhir.android.ui.main.patientregistration.*
import com.latticeonfhir.android.ui.main.ui.theme.FHIRAndroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    //private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FHIRAndroidTheme {
                val navController = rememberNavController()
                NavigationAppHost(navController = navController)
            }
        }
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
    }

    override fun viewModel() = viewModel
}