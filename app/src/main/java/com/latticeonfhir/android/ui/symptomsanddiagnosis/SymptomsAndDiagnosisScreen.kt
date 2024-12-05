package com.latticeonfhir.android.ui.symptomsanddiagnosis

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.CustomDialog
import com.latticeonfhir.android.ui.patientlandingscreen.AllSlotsBookedDialog
import com.latticeonfhir.android.ui.symptomsanddiagnosis.components.SymptomsAndDiagnosisCard
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.android.utils.constants.SymptomsAndDiagnosisConstants
import com.latticeonfhir.android.utils.constants.SymptomsAndDiagnosisConstants.SYM_DIAG_UPDATE_OR_ADD
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import com.latticeonfhir.android.utils.network.CheckNetwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomsAndDiagnosisScreen(
    navController: NavController, viewModel: SymptomsAndDiagnosisViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    HandleLaunchEffect(viewModel, navController, context, snackBarHostState)
    Scaffold(modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) }, topBar = {
            TopAppBar(modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON"
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.symtoms_title),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("VITAL_TITLE_TEXT")
                    )
                })
        }, content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(paddingValues = paddingValues),
            ) {

                if (viewModel.isSymptomsAndDiagnosisExist) {
                    Text(
                        text = stringResource(R.string.recent_symptoms_diagnosis),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp),
                    )
                    val list = viewModel.symptomsDiagnosisListFlow.collectAsState()

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(list.value) { _, item ->
                            SymptomsAndDiagnosisCard(item)
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = MaterialTheme.colorScheme.inverseOnSurface),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.no_symptoms_found),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                    }
                }
            }

        }, bottomBar = {
            Box(
                modifier = Modifier
                    .padding()
                    .navigationBarsPadding(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(), onClick = {
                            handleNavigation(viewModel, scope, navController)
                        }, contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = stringResource(R.string.add_icon),
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(
                            if (viewModel.isAppointmentExist && viewModel.symptomsAndDiagnosisLocal != null) stringResource(
                                id = R.string.update_symptoms_diagnosis
                            ) else stringResource(
                                id = R.string.add_symptoms_diagnosis
                            )
                        )
                    }
                }


            }

        })

    ShowDialogs(viewModel, navController, scope)


}

fun handleNavigation(
    viewModel: SymptomsAndDiagnosisViewModel,
    scope: CoroutineScope,
    navController: NavController
) {
    viewModel.getAppointmentInfo {
        if (viewModel.canAddAssessment) {
            scope.launch {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    key = SymptomsAndDiagnosisConstants.SYM_DIAG,
                    viewModel.symptomsAndDiagnosisLocal
                )
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    key = PATIENT, viewModel.patient
                )
                navController.navigate(Screen.AddSymptomsScreen.route)
            }

        } else if (viewModel.isAppointmentCompleted) {
            viewModel.showAppointmentCompletedDialog = true
        } else {
            viewModel.showAddToQueueDialog = true
        }
    }

}

@Composable
fun ShowDialogs(
    viewModel: SymptomsAndDiagnosisViewModel, navController: NavController, scope: CoroutineScope
) {
    if (viewModel.showAddToQueueDialog) {
        CustomDialog(title = if (viewModel.appointment != null) stringResource(id = R.string.patient_arrived_question) else stringResource(
            id = R.string.add_to_queue_question
        ),
            text = stringResource(id = R.string.add_to_queue_vital_dialog_description),
            dismissBtnText = stringResource(id = R.string.dismiss),
            confirmBtnText = if (viewModel.appointment != null) stringResource(id = R.string.mark_arrived) else stringResource(
                id = R.string.add_to_queue
            ),
            dismiss = { viewModel.showAddToQueueDialog = false },
            confirm = {
                if (viewModel.appointment != null) {
                    viewModel.updateStatusToArrived(
                        viewModel.patient!!, viewModel.appointment!!
                    ) {
                        scope.launch {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                key = SymptomsAndDiagnosisConstants.SYM_DIAG,
                                viewModel.symptomsAndDiagnosisLocal
                            )
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                key = PATIENT, viewModel.patient
                            )
                            navController.navigate(Screen.AddSymptomsScreen.route)
                        }

                    }
                } else {
                    if (viewModel.ifAllSlotsBooked) {
                        viewModel.showAllSlotsBookedDialog = true
                    } else {
                        viewModel.addPatientToQueue(viewModel.patient!!) {
                            viewModel.showAddToQueueDialog = false

                            scope.launch {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    key = SymptomsAndDiagnosisConstants.SYM_DIAG,
                                    viewModel.symptomsAndDiagnosisLocal
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    key = PATIENT, viewModel.patient
                                )
                                navController.navigate(Screen.AddSymptomsScreen.route)
                            }
                        }


                    }
                }
            })
    }
    if (viewModel.ifAllSlotsBooked) {
        AllSlotsBookedDialog {
            viewModel.showAllSlotsBookedDialog = false
        }
    }
}


@Composable
fun HandleLaunchEffect(
    viewModel: SymptomsAndDiagnosisViewModel,
    navController: NavController,
    context: Context,
    snackBarHostState: SnackbarHostState
) {
    LaunchedEffect(key1 = viewModel.isLaunched) {
        viewModel.apply {

            if (isLaunched) {
                if (CheckNetwork.isInternetAvailable(context)) {
                    getSymptomsAndDiagnosis()
                }
                patient =
                    navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                        PATIENT
                    )
                patient?.let {
                    getStudentTodayAppointment(
                        Date(Date().toTodayStartDate()),
                        Date(Date().toEndOfDay()),
                        patient!!.id
                    )
                    viewModel.getSymDiagnosis()
                    msg = navController.currentBackStackEntry?.savedStateHandle?.get<String>(
                        SYM_DIAG_UPDATE_OR_ADD
                    ) ?: ""
                    Timber.d("msg: $msg")
                }
            }

            viewModel.isLaunched = true
        }

    }

    LaunchedEffect(key1 = viewModel.msg) {
        if (viewModel.msg.isNotBlank()) {
            snackBarHostState.showSnackbar(viewModel.msg)
            viewModel.msg = ""
        }

    }
}



