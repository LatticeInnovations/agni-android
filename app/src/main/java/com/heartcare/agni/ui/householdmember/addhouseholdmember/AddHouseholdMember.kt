package com.heartcare.agni.ui.householdmember.addhouseholdmember

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.utils.converters.responseconverter.AddressConverter
import com.heartcare.agni.utils.converters.responseconverter.NameConverter
import kotlinx.coroutines.launch

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
                    Text(
                        text = stringResource(id = R.string.add_household_member),
                        modifier = Modifier.testTag("TITLE")
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "BACK_ICON")
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
                    viewModel.patient?.let { patient ->
                        CardLayout(
                            navController,
                            R.drawable.person_add,
                            stringResource(id = R.string.create_new_patient),
                            stringResource(id = R.string.add_a_patient),
                            patient,
                            "register",
                            viewModel
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        CardLayout(
                            navController,
                            R.drawable.patient_list,
                            stringResource(id = R.string.search_existing_patient),
                            stringResource(id = R.string.search_patients),
                            patient,
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
                                    text = stringResource(id = R.string.nearby_matches),
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
                                        viewModel.showRelationDialogue = false
                                        navController.currentBackStackEntry?.savedStateHandle?.set(
                                            "patientFrom",
                                            viewModel.patient
                                        )
                                        navController.currentBackStackEntry?.savedStateHandle?.set(
                                            "selectedMembersList",
                                            viewModel.selectedSuggestedMembersList.toMutableList()
                                        )
                                        navController.navigate(Screen.ConnectPatient.route)
                                    },
                                    enabled = viewModel.selectedSuggestedMembersList.isNotEmpty()
                                ) {
                                    Text(
                                        stringResource(id = R.string.connect_nearby)
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
                                        stringResource(id = R.string.search_patients)
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
    val coroutineScope = rememberCoroutineScope()
    OutlinedCard(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.testTag(btnText)
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
                        coroutineScope.launch {
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
                        }
                    } else {
                        viewModel.selectedSuggestedMembersList.clear()
                        viewModel.getSuggestions(patient) {
                            if (viewModel.suggestedMembersList.isNotEmpty()) viewModel.showRelationDialogue =
                                true
                            else {
                                coroutineScope.launch {
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
                .fillMaxWidth()
                .clickable {
                    checkedState.value = !checkedState.value
                    if (checkedState.value) {
                        viewModel.selectedSuggestedMembersList.add(member)
                    } else {
                        viewModel.selectedSuggestedMembersList.remove(member)
                    }
                },
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
                    text = NameConverter.getFullName(
                        member.firstName,
                        member.middleName,
                        member.lastName
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = AddressConverter.getAddress(member.permanentAddress),
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