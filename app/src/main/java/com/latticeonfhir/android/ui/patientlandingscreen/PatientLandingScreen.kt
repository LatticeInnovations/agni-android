package com.latticeonfhir.android.ui.patientlandingscreen

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import androidx.lifecycle.viewmodel.compose.*
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.utils.converters.responseconverter.NameConverter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeInMilli

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientLandingScreen(navController: NavController, viewModel: PatientLandingScreenViewModel = hiltViewModel()) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient = navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                "patient"
            )
            viewModel.patient?.fhirId?.let { patientFhirId -> viewModel.downloadPrescriptions(patientFhirId) }
        }
        viewModel.patient= viewModel.getPatientData(viewModel.patient!!.id)

        viewModel.isLaunched = true
        FhirApp.isProfileUpdated=false
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
                    val age = viewModel.patient?.birthDate?.toTimeInMilli()?.toAge()
                    val subTitle = "${viewModel.patient?.gender?.get(0)?.uppercase()}/$age . +91 ${viewModel.patient?.mobileNumber} · ${viewModel.patient?.fhirId} "
                    Column {
                        Text(text = NameConverter.getFullName(viewModel.patient?.firstName, viewModel.patient?.middleName, viewModel.patient?.lastName), style = MaterialTheme.typography.titleLarge, modifier = Modifier.testTag("TITLE"))
                        Text(text = subTitle, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.testTag("SUBTITLE"))
                    }
                },
                actions = {
                    IconButton(onClick = {

                        navController.currentBackStackEntry?.savedStateHandle?.set(key = "patient_details", value = viewModel.patient)
                        navController.navigate(Screen.EditPatient.route)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.profile_icon),
                            contentDescription = "profile icon",
                            tint= MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp)) {
                    CardComposable(
                        navController,
                        viewModel.patient,
                        "HOUSEHOLD_MEMBER",
                        Screen.HouseholdMembersScreen.route,
                        stringResource(id = R.string.household_members),
                        R.drawable.group_icon
                    )
                    CardComposable(
                        navController,
                        viewModel.patient,
                        "PRESCRIPTION",
                        Screen.Prescription.route,
                        stringResource(id = R.string.prescription),
                        R.drawable.prescriptions_icon
                    )
                }
            }
        }
    )
}

@Composable
fun CardComposable(
    navController: NavController,
    patient: PatientResponse?,
    tag: String,
    route: String,
    label: String,
    icon: Int
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
            .clickable {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "patient",
                    patient
                )
                navController.navigate(route)
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
                painter = painterResource(id = icon),
                contentDescription = tag+"_ICON",
                tint = MaterialTheme.colorScheme.surfaceTint,
                modifier = Modifier.size(30.dp, 21.dp)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "RIGHT_ARROW")
        }
    }
}