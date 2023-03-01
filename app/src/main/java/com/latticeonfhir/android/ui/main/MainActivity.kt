package com.latticeonfhir.android.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Space
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.R
import com.latticeonfhir.android.base.activity.BaseActivity
import com.latticeonfhir.android.databinding.ActivityMainBinding
import com.latticeonfhir.android.ui.main.patientregistration.*
import com.latticeonfhir.android.ui.main.ui.theme.FHIRAndroidTheme
import com.latticeonfhir.android.ui.main.ui.theme.Primary40
import com.latticeonfhir.android.ui.main.ui.theme.Primary95
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    //private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
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
                                    text = "My Patients",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = {}) {
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = null
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = Primary95
                            ),
                            actions = {
                                IconButton(onClick = { /*TODO*/ }) {
                                    Icon(Icons.Default.Search, contentDescription = null)
                                }
                                IconButton(onClick = { /*TODO*/ }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.icon),
                                        contentDescription = null,
                                        Modifier.size(20.dp)
                                    )
                                }
                            },
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            modifier = Modifier.padding(bottom = 30.dp),
                            onClick = {
                                context.startActivity(
                                    Intent(
                                        context,
                                        PatientRegistration::class.java
                                    )
                                )
                            },
                            content = {
                                Row(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.person_add),
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp, 16.dp),
                                        tint = Primary40
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Add Patient",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Primary40
                                    )
                                }
                            },
                            containerColor = Primary95
                        )
                    },
                    content = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it)
                        ) {

                        }
                    }
                )
            }
        }
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
    }

    override fun viewModel() = viewModel
}