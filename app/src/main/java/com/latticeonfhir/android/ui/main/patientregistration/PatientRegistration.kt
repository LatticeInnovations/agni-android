package com.latticeonfhir.android.ui.main.patientregistration

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.latticeonfhir.android.ui.main.MainActivity
import com.latticeonfhir.android.ui.main.ui.theme.FHIRAndroidTheme
import com.latticeonfhir.android.ui.main.ui.theme.Primary95

class PatientRegistration : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel by viewModels<PatientRegistrationViewModel>()
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            FHIRAndroidTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = if(viewModel.step == 4) "Preview"
                                        else "Patient Registration",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                            navigationIcon = {
                                if (viewModel.step != 1) {
                                    IconButton(onClick = {
                                        viewModel.step -= 1
                                    }) {
                                        Icon(
                                            Icons.Default.ArrowBack,
                                            contentDescription = "Back button"
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = Primary95
                            ),
                            actions = {
                                IconButton(onClick = {
                                    context.startActivity(Intent(context, MainActivity::class.java))
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = null)
                                }
                            }
                        )
                    },
                    content = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it)
                        ) {
                            if (viewModel.step == 1)
                                PatientRegistrationStepOne(viewModel = viewModel)
                            else if (viewModel.step == 2)
                                PatientRegistrationStepTwo(viewModel = viewModel)
                            else if (viewModel.step == 3)
                                PatientRegistrationStepThree(viewModel = viewModel)
                            else if (viewModel.step == 4)
                                PatientRegistrationPreview(viewModel = viewModel)
                        }
                    }
                )
            }
        }
    }
}
