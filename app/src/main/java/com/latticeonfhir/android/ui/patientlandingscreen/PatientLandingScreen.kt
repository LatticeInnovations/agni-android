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
import androidx.compose.runtime.LaunchedEffect
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
import com.latticeonfhir.android.ui.patientlandingscreen.PatientLandingScreenViewModel
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientLandingScreen(navController: NavController, viewModel: PatientLandingScreenViewModel = viewModel()) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient = navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                "patient"
            )
        }
        viewModel.isLaunched = true
    }
    Scaffold(
        topBar = {
            LargeTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "back icon")
                    }
                },
                title = {
                    val name = viewModel.patient?.firstName +
                            if (viewModel.patient?.middleName.isNullOrEmpty()) "" else {
                                " " + viewModel.patient?.middleName
                            } +
                            if (viewModel.patient?.lastName.isNullOrEmpty()) "" else {
                                " " + viewModel.patient?.lastName
                            }
                    val age = viewModel.patient?.birthDate?.toTimeInMilli()
                        ?.let { Period.between(
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() ,
                        LocalDate.now()
                    ).years}
                    val subTitle = "${viewModel.patient?.gender?.get(0)?.uppercase()}/$age · ${viewModel.patient?.fhirId}"
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
                    CardComposable(navController, viewModel.patient)
                }
            }
        }
    )
}

@Composable
fun CardComposable(navController: NavController, patient: PatientResponse?) {
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