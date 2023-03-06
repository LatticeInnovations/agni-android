package com.latticeonfhir.android.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Space
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.latticeonfhir.android.R
import com.latticeonfhir.android.base.activity.BaseActivity
import com.latticeonfhir.android.databinding.ActivityMainBinding
import com.latticeonfhir.android.ui.main.patientregistration.*
import com.latticeonfhir.android.ui.main.searchpatient.SearchPatient
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
                                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                            ),
                            actions = {
                                IconButton(onClick = {
                                    context.startActivity(Intent(context, SearchPatient::class.java))
                                }) {
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
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Add Patient",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(11.dp)
                        )
                    },
                    content = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(5.dp)
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ){
                                    AssistChip(
                                        onClick = { /*TODO*/ },
                                        label = {
                                            Text(
                                                text = "Category 1",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        },
                                        trailingIcon = {
                                            Icon(
                                                Icons.Default.ArrowDropDown,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    AssistChip(
                                        onClick = { /*TODO*/ },
                                        label = {
                                            Text(
                                                text = "Category 2",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        },
                                        trailingIcon = {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurface)
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    AssistChip(
                                        onClick = { /*TODO*/ },
                                        label = {
                                            Text(
                                                text = "Category 3",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        },
                                        trailingIcon = {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurface)
                                        }
                                    )
                                }
                                Column(
                                    modifier = Modifier.verticalScroll(rememberScrollState())
                                ) {
                                    PatientItemCard(
                                        name = "Chetan A S Ramanathan",
                                        patientId = "M/51 · PID 23456",
                                        metaData = "Referred: 12 Jan"
                                    )
                                    PatientItemCard(
                                        name = "Pilavullakandi Thekkeparambimnsahgfh",
                                        patientId = "F/58 · PID 11111",
                                        metaData = "Seen on 18 May 2022"
                                    )
                                    PatientItemCard(
                                        name = "Pilavullakandi Thekkeparambimnsahgfh",
                                        patientId = "F/58 · PID 11111",
                                        metaData = "Referred: 12 Jan"
                                    )
                                    PatientItemCard(
                                        name = "Pilavullakandi Thekkeparambimnsahgfh",
                                        patientId = "F/58 · PID 11111",
                                        metaData = "Last seen: 23 Jan"
                                    )
                                    PatientItemCard(
                                        name = "Gauri Sharma",
                                        patientId = "F/44 · PID 12345",
                                        metaData = "Last seen: 23 Jan"
                                    )
                                    PatientItemCard(
                                        name = "Ramesh Seksaria",
                                        patientId = "F/44 · PID 12345",
                                        metaData = "Last seen: 23 Jan"
                                    )
                                    PatientItemCard(
                                        name = "Ramesh Seksaria",
                                        patientId = "F/44 · PID 12345",
                                        metaData = "Last seen: 23 Jan"
                                    )
                                    PatientItemCard(
                                        name = "Ramesh Seksaria",
                                        patientId = "F/44 · PID 12345",
                                        metaData = "Last seen: 23 Jan"
                                    )
                                    PatientItemCard(
                                        name = "Ramesh Seksaria",
                                        patientId = "F/44 · PID 12345",
                                        metaData = "Last seen: 23 Jan"
                                    )
                                    PatientItemCard(
                                        name = "Ramesh Seksaria",
                                        patientId = "F/44 · PID 12345",
                                        metaData = "Last seen: 23 Jan"
                                    )
                                    PatientItemCard(
                                        name = "Ramesh Seksaria",
                                        patientId = "F/44 · PID 12345",
                                        metaData = "Last seen: 23 Jan"
                                    )
                                    PatientItemCard(
                                        name = "Ramesh Seksaria",
                                        patientId = "F/44 · PID 12345",
                                        metaData = "Last seen: 23 Jan"
                                    )
                                    PatientItemCard(
                                        name = "Ramesh Seksaria",
                                        patientId = "F/44 · PID 12345",
                                        metaData = "Last seen: 23 Jan"
                                    )
                                    PatientItemCard(
                                        name = "Ramesh Seksaria",
                                        patientId = "F/44 · PID 12345",
                                        metaData = "Last seen: 23 Jan"
                                    )
                                    PatientItemCard(
                                        name = "Ramesh Seksaria",
                                        patientId = "F/44 · PID 12345",
                                        metaData = "Last seen: 23 Jan"
                                    )
                                    PatientItemCard(
                                        name = "Ramesh Seksaria",
                                        patientId = "F/44 · PID 12345",
                                        metaData = "Last seen: 23 Jan"
                                    )
                                }
                            }
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

@Composable
fun PatientItemCard(name: String, patientId: String, metaData: String){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(8f)) {
            Text(
                text = name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = patientId,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = metaData,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}