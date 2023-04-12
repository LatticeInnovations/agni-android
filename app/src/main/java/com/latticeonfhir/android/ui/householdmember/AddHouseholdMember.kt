package com.latticeonfhir.android.ui.main.patientlandingscreen

import android.graphics.drawable.Icon
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHouseholdMember(navController: NavController) {
    val patient = navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
        "patient"
    )!!
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Add a household member")
                },
                navigationIcon = {
                    IconButton(onClick = {
//                        navController.currentBackStackEntry?.savedStateHandle?.set(
//                            "patient",
//                            patient
//                        )
//                        navController.navigate(Screen.HouseholdMembersScreen.route)
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
                    CardLayout(
                        navController,
                        R.drawable.person_add,
                        "Create a new patient, and\ninclude them in the household",
                        "Add a patient",
                        patient,
                        Screen.PatientRegistrationScreen.route
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    CardLayout(
                        navController,
                        R.drawable.patient_list,
                        "Search for an existing patient, and\ninclude them in the household",
                        "Search patients",
                        patient,
                        Screen.SearchPatientScreen.route
                    )
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
    route: String
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
                },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text(text = btnText)
            }
        }
    }
}