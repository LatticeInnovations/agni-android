package com.latticeonfhir.android.ui.main.patientlandingscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import androidx.lifecycle.viewmodel.compose.*
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientLandingScreen(navController: NavController) {
    val patient = navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
        "patient"
    )!!
    Scaffold(
        topBar = {
            LargeTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "patient",
                            patient
                        )
                        navController.navigate(Screen.LandingScreen.route)
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "back icon")
                    }
                },
                title = {
                    val name = patient.firstName +
                            if (patient.middleName.isNullOrEmpty()) "" else {
                                " " + patient.middleName
                            } +
                            if (patient.lastName.isNullOrEmpty()) "" else {
                                " " + patient.lastName
                            }
                    val age = Period.between(
                        Instant.ofEpochMilli(patient.birthDate.toTimeInMilli()).atZone(ZoneId.systemDefault()).toLocalDate(),
                        LocalDate.now()
                    ).years
                    val subTitle = "${patient.gender[0].uppercase()}/$age · ${patient.fhirId}"
                    Column {
                        Text(text = name, style = MaterialTheme.typography.titleLarge)
                        Text(text = subTitle, style = MaterialTheme.typography.bodyLarge)
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "more icon"
                        )
                    }
                }
            )
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)) {
                    CardComposable(navController, patient)
                }
            }
        }
    )
}

@Composable
fun CardComposable(navController: NavController, patient: PatientResponse) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "patient",
                    patient
                )
                navController.navigate(Screen.HouseholdMembersScreen.route)
            },
        shadowElevation = 5.dp,
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.group_icon),
                contentDescription = "group icon",
                tint = MaterialTheme.colorScheme.surfaceTint,
                modifier = Modifier.size(30.dp, 21.dp)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text(text = "Household members", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "next icon")
        }
    }
}