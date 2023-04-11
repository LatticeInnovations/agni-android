package com.latticeonfhir.android.ui.patientregistration.step4

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.ui.theme.Neutral40
import androidx.lifecycle.viewmodel.compose.*
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmRelationship(
    navController: NavController,
    viewModel: ConfirmRelationshipViewModel = hiltViewModel()
) {
    viewModel.relation = navController.previousBackStackEntry?.savedStateHandle?.get<String>(
        key = "relation"
    )!!
    viewModel.patientId = navController.previousBackStackEntry?.savedStateHandle?.get<String>(
        key = "patientId"
    )!!
    viewModel.relativeId = navController.previousBackStackEntry?.savedStateHandle?.get<String>(
        key = "relativeId"
    )!!
    Log.d("manseeyy", viewModel.patientId + viewModel.relation + viewModel.relativeId)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Confirm Relationship",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                        IconButton(onClick = {
//                                navController.currentBackStackEntry?.savedStateHandle?.set(
//                                    key = "patient_register_details",
//                                    value = patientRegister
//                                )
//                                navController.navigate(Screen.PatientRegistrationPreviewScreen.route)
                            navController.popBackStack()
                        }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back button"
                            )
                        }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                actions = {
                    IconButton(onClick = {
                        //navController.navigate(Screen.LandingScreen.route)
                        navController.popBackStack(Screen.LandingScreen.route, false)
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "clear icon")
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
                ConfirmRelationshipScreen(navController, viewModel)
            }
        }
    )
}

@Composable
fun ConfirmRelationshipScreen(navController: NavController, viewModel: ConfirmRelationshipViewModel){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Add member",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Page 4/4",
                style = MaterialTheme.typography.bodySmall,
                color = Neutral40
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
        ) {
            MemberCard(viewModel)
            Spacer(modifier = Modifier.height(24.dp))
            MemberCard(viewModel)
            Spacer(modifier = Modifier.height(24.dp))
        }
        Button(
            onClick = {
                // add to relation table here
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Text(text = "Connect Patient")
        }

        if (viewModel.openDeleteDialog) {
            DeleteDialog(viewModel)
        }

        if (viewModel.openEditDialog) {
            EditDialog(viewModel)
        }
    }
}

@Composable
fun MemberCard(viewModel: ConfirmRelationshipViewModel) {
    Row(
        modifier = Modifier.padding(14.dp)
    ) {
        Text(
            text = "Vikram Pandey is the father of Alok Pandey",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(20.dp))
        FilledTonalIconButton(onClick = { viewModel.openDeleteDialog = true }) {
            Icon(
                painterResource(id = R.drawable.delete_icon),
                contentDescription = "delete member",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        FilledTonalIconButton(onClick = { viewModel.openEditDialog = true }) {
            Icon(
                painterResource(id = R.drawable.edit_icon),
                contentDescription = "edit member",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun DeleteDialog(viewModel: ConfirmRelationshipViewModel) {
    AlertDialog(
        onDismissRequest = {
            viewModel.openDeleteDialog = false
        },
        title = {
            Text(
                text = "Remove relationship?",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.testTag("delete dialog title")
            )
        },
        text = {
            Column() {
                Text(
                    "Are you sure you want to remove this relationship? Patient records will not be affected.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("delete dialog description 1")
                )
                Text(
                    "Vikram Pandey is the father of Alok Pandey",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .testTag("delete dialog description 2")
                        .padding(start = 10.dp, top = 10.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.openDeleteDialog = false
                }) {
                Text(
                    "Confirm",
                    modifier = Modifier.testTag("delete dialog confirm btn")
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.openDeleteDialog = false
                }) {
                Text(
                    "Cancel",
                    modifier = Modifier.testTag("delete dialog cancel btn")
                )
            }
        }
    )
}

@Composable
fun EditDialog(viewModel: ConfirmRelationshipViewModel) {
    var expanded by remember {
        mutableStateOf(false)
    }
    AlertDialog(
        onDismissRequest = {
            viewModel.openEditDialog = false
        },
        title = {
            Text(
                text = "Edit relationship",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.testTag("edit dialog title")
            )
        },
        text = {
            Column {
                Text(
                    "Alok Pandey",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("edit dialog description 1")
                )
                Spacer(modifier = Modifier.height(23.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "is the",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    TextField(
                        value = viewModel.editRelation,
                        onValueChange = {
                            viewModel.editRelation = it
                        },
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "")
                            }
                        },
                        readOnly = true,
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(23.dp))
                Text(
                    text = "of Vikram Pandey.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.openEditDialog = false
                }) {
                Text(
                    "Save",
                    modifier = Modifier.testTag("edit dialog save btn")
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.openEditDialog = false
                }) {
                Text(
                    "Cancel",
                    modifier = Modifier.testTag("edit dialog cancel btn")
                )
            }
        }
    )
}