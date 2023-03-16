package com.latticeonfhir.android.ui.main.searchpatient

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.ui.main.MainActivity
import com.latticeonfhir.android.ui.main.ui.theme.FHIRAndroidTheme

class SearchPatient : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val searchPatientViewModel by viewModels<SearchPatientViewModel>()
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
                                    text = "Search Patient",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                            ),
                            navigationIcon = {
                                 if (searchPatientViewModel.step == 2){
                                     IconButton(onClick = {
                                         searchPatientViewModel.step = 1
                                     }) {
                                         Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.testTag("back icon"))
                                     }
                                 }
                            },
                            actions = {
                                if (searchPatientViewModel.step == 1) {
                                    IconButton(onClick = {
                                        context.startActivity(
                                            Intent(
                                                context,
                                                MainActivity::class.java
                                            )
                                        )
                                    }) {
                                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.testTag("clear icon"))
                                    }
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
                            if (searchPatientViewModel.step == 1)
                                SearchPatientForm(searchPatientViewModel = searchPatientViewModel)
                            else if (searchPatientViewModel.step == 2)
                                SearchPatientResult(searchPatientViewModel = searchPatientViewModel)
                        }
                    }
                )
            }
        }
    }
}
