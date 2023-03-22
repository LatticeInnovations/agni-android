package com.latticeonfhir.android

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.latticeonfhir.android.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(navController: NavController) {
    val context = LocalContext.current
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
                            contentDescription = null,
                            modifier = Modifier.testTag("menu icon")
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.SearchPatientScreen.route)
                    }) {
                        Icon(
                            Icons.Default.Search, contentDescription = null,
                            modifier = Modifier.testTag("search icon"))
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp).testTag("user icon")
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 30.dp),
                onClick = {
                    navController.navigate(Screen.PatientRegistrationScreen.route)
                },
                content = {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.person_add),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp, 16.dp).testTag("add patient icon"),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Add Patient",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("add patient text")
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
                                Icon(
                                    Icons.Default.ArrowDropDown, contentDescription = null,
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
                                Icon(
                                    Icons.Default.ArrowDropDown, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface)
                            }
                        )
                    }
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()).testTag("patients list")
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