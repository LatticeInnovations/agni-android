package com.latticeonfhir.android.ui.main.patientlandingscreen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.householdmember.addhouseholdmember.AddHouseholdMemberViewModel
import androidx.lifecycle.viewmodel.compose.*
import com.latticeonfhir.android.data.local.constants.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHouseholdMember(
    navController: NavController,
    viewModel: AddHouseholdMemberViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    "patient"
                )
        }
        viewModel.isLaunched = true
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Add a household member")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "back icon")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                )
            )
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 40.dp, horizontal = 60.dp)
                ) {
                    viewModel.patient?.let { it1 ->
                        CardLayout(
                            navController,
                            R.drawable.person_add,
                            "Create a new patient, and\ninclude them in the household",
                            "Add a patient",
                            it1,
                            "register",
                            viewModel
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        CardLayout(
                            navController,
                            R.drawable.patient_list,
                            "Search for an existing patient, and\ninclude them in the household",
                            "Search patients",
                            it1,
                            "search",
                            viewModel
                        )
                    }

                    if (viewModel.showRelationDialogue) {
                        AlertDialog(
                            onDismissRequest = {
                                viewModel.showRelationDialogue = false
                            },
                            title = {
                                Text(
                                    text = "Nearby matches",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            },
                            text = {
                                LazyColumn {
                                    items(viewModel.suggestedMembersList) { member ->
                                        DialogPatientRow(member, viewModel)
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                              // navigate to connect patient screen
                                    },
                                    enabled = viewModel.selectedSuggestedMembersList.isNotEmpty()
                                ) {
                                    Text(
                                        "Connect nearby"
                                    )
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        viewModel.showRelationDialogue = false
                                        navController.currentBackStackEntry?.savedStateHandle?.set(
                                            "fromHouseholdMember",
                                            true
                                        )
                                        navController.currentBackStackEntry?.savedStateHandle?.set(
                                            "patient",
                                            viewModel.patient
                                        )
                                        navController.navigate(Screen.SearchPatientScreen.route)
                                    }) {
                                    Text(
                                        "Search patients"
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun CardLayout(
    navController: NavController,
    icon: Int,
    description: String,
    btnText: String,
    patient: PatientResponse,
    route: String,
    viewModel: AddHouseholdMemberViewModel
) {
    OutlinedCard(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.surfaceTint
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    if (route == "register") {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "fromHouseholdMember",
                            true
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "patient",
                            patient
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "currentStep",
                            1
                        )
                        navController.navigate(Screen.PatientRegistrationScreen.route)
                    } else {
                        viewModel.getSuggestions(patient) {
                            if (viewModel.suggestedMembersList.isNotEmpty()) viewModel.showRelationDialogue = true
                            else {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "fromHouseholdMember",
                                    true
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "patient",
                                    patient
                                )
                                navController.navigate(Screen.SearchPatientScreen.route)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text(text = btnText)
            }
        }
    }
}

@Composable
fun DialogPatientRow(member: PatientResponse, viewModel: AddHouseholdMemberViewModel) {
    val checkedState = remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = checkedState.value,
                onCheckedChange = {
                    checkedState.value = it
                    if (it) {
                        viewModel.selectedSuggestedMembersList.add(member)
                    } else {
                        viewModel.selectedSuggestedMembersList.remove(member)
                    }
                },
            )
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = Constants.GetFullName(
                        member.firstName,
                        member.middleName,
                        member.lastName
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = Constants.GetAddress(member.permanentAddress),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Divider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }

}