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
import androidx.compose.ui.platform.testTag
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
import com.latticeonfhir.android.data.local.constants.Constants
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
                    val age = viewModel.patient?.birthDate?.let { Constants.GetAge(it) }
                    val subTitle = "${viewModel.patient?.gender?.get(0)?.uppercase()}/$age · ${viewModel.patient?.fhirId}"
                    Column {
                        Text(text = Constants.GetFullName(viewModel.patient?.firstName, viewModel.patient?.middleName, viewModel.patient?.lastName), style = MaterialTheme.typography.titleLarge, modifier = Modifier.testTag("TITLE"))
                        Text(text = subTitle, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.testTag("SUBTITLE"))
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
                    CardComposable(navController, viewModel.patient, "HOUSEHOLD_MEMBER")
                }
            }
        }
    )
}

@Composable
fun CardComposable(navController: NavController, patient: PatientResponse?, tag: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "patient",
                    patient
                )
                navController.navigate(Screen.HouseholdMembersScreen.route)
            }
            .testTag(tag),
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