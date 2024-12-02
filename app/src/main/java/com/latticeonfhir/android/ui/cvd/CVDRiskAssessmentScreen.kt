package com.latticeonfhir.android.ui.cvd

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.enums.YesNoEnum
import com.latticeonfhir.android.data.server.model.cvd.CVDResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.common.CustomDialog
import com.latticeonfhir.android.ui.common.TabRowComposable
import com.latticeonfhir.android.ui.cvd.form.CVDRiskAssessmentForm
import com.latticeonfhir.android.ui.cvd.form.DisplayField
import com.latticeonfhir.android.ui.cvd.records.CVDRiskAssessmentRecords
import com.latticeonfhir.android.ui.patientlandingscreen.AllSlotsBookedDialog
import com.latticeonfhir.android.ui.prescription.photo.view.AppointmentCompletedDialog
import com.latticeonfhir.android.ui.theme.HighRiskCircle
import com.latticeonfhir.android.ui.theme.HighRiskDarkContainer
import com.latticeonfhir.android.ui.theme.HighRiskLightContainer
import com.latticeonfhir.android.ui.theme.LowRiskCircle
import com.latticeonfhir.android.ui.theme.LowRiskDarkContainer
import com.latticeonfhir.android.ui.theme.LowRiskLightContainer
import com.latticeonfhir.android.ui.theme.ModerateRiskCircle
import com.latticeonfhir.android.ui.theme.ModerateRiskDarkContainer
import com.latticeonfhir.android.ui.theme.ModerateRiskLightContainer
import com.latticeonfhir.android.ui.theme.VeryHighRiskCircle
import com.latticeonfhir.android.ui.theme.VeryHighRiskDarkContainer
import com.latticeonfhir.android.ui.theme.VeryHighRiskLightContainer
import com.latticeonfhir.android.ui.theme.VeryVeryHighRiskCircle
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toddMMMyyyy
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CVDRiskAssessmentScreen(
    navController: NavController,
    viewModel: CVDRiskAssessmentViewModel = hiltViewModel()
) {

    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    PATIENT
                )
            viewModel.getTodayCVDAssessment()
            viewModel.getAppointmentInfo(callback = {})
            viewModel.isLaunched = true
        }
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        viewModel.tabs.size
    }
    val focusManager = LocalFocusManager.current

    BackHandler {
        if (viewModel.selectedRecord != null) viewModel.selectedRecord = null
        else if (pagerState.currentPage == 1) {
            scope.launch {
                pagerState.animateScrollToPage(0)
            }
        } else navController.navigateUp()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON")
                    }
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.cvd_risk_assessment),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("TITLE")
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TabRowComposable(
                        viewModel.tabs,
                        pagerState
                    ) { index ->
                        if (index == 1) {
                            viewModel.getAppointmentInfo(
                                callback = {
                                    if (viewModel.canAddAssessment) {
                                        scope.launch { pagerState.animateScrollToPage(index) }
                                    } else if (viewModel.isAppointmentCompleted) {
                                        viewModel.showAppointmentCompletedDialog = true
                                    } else {
                                        viewModel.showAddToQueueDialog = true
                                    }
                                }
                            )
                        } else scope.launch { pagerState.animateScrollToPage(index) }
                    }
                    HorizontalPager(
                        state = pagerState,
                        userScrollEnabled = viewModel.canAddAssessment
                    ) { index ->
                        when (index) {
                            0 -> CVDRiskAssessmentRecords(viewModel)
                            1 -> CVDRiskAssessmentForm(viewModel)
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (pagerState.currentPage == 1) {
                Column {
                    AnimatedVisibility(
                        viewModel.riskPercentage.isNotBlank(),
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                    ) {
                        if (viewModel.riskPercentage.isNotBlank()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = getContainerColor(viewModel.riskPercentage.toInt()),
                                shape = RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(
                                        horizontal = 20.dp,
                                        vertical = 24.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.padding(start = 6.dp)
                                    ) {
                                        Canvas(modifier = Modifier.size(12.dp), onDraw = {
                                            drawCircle(
                                                color = getCircleColor(
                                                    viewModel.riskPercentage.toIntOrNull() ?: 0
                                                )
                                            )
                                        })
                                        Text(
                                            text = stringResource(
                                                R.string.percentage,
                                                viewModel.riskPercentage
                                            ),
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = stringResource(R.string.risk_info),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.getRisk()
                            },
                            modifier = Modifier
                                .weight(1f),
                            enabled = viewModel.ifFormValid() && viewModel.riskPercentage.isBlank()
                        ) {
                            Text(text = stringResource(R.string.predict))
                        }
                        if (viewModel.riskPercentage.isNotBlank()) {
                            Button(
                                onClick = {
                                    viewModel.saveCVDRecord(
                                        saved = {
                                            focusManager.clearFocus()
                                            scope.launch {
                                                pagerState.animateScrollToPage(0)
                                                snackbarHostState.showSnackbar(
                                                    message = context.getString(R.string.assessment_record_saved)
                                                )
                                            }
                                        }
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                Text(text = stringResource(R.string.save))
                            }
                        }
                    }
                }
            }
        }
    )
    if (viewModel.selectedRecord != null) {
        RecordsFullDetailsComposable(
            record = viewModel.selectedRecord!!,
            onClick = {
                viewModel.selectedRecord = null
            }
        )
    }

    if (viewModel.showAddToQueueDialog) {
        CustomDialog(
            title = if (viewModel.appointment != null) stringResource(id = R.string.patient_arrived_question) else stringResource(
                id = R.string.add_to_queue_question
            ),
            text = stringResource(id = R.string.add_to_queue_assessment_dialog_description),
            dismissBtnText = stringResource(id = R.string.dismiss),
            confirmBtnText = if (viewModel.appointment != null) stringResource(id = R.string.mark_arrived) else stringResource(
                id = R.string.add_to_queue
            ),
            dismiss = { viewModel.showAddToQueueDialog = false },
            confirm = {
                if (viewModel.appointment != null) {
                    viewModel.updateStatusToArrived(
                        viewModel.patient!!,
                        viewModel.appointment!!,
                        updated = {
                            viewModel.showAddToQueueDialog = false
                            scope.launch { pagerState.animateScrollToPage(1) }
                        }
                    )
                } else {
                    if (viewModel.ifAllSlotsBooked) {
                        viewModel.showAllSlotsBookedDialog = true
                    } else {
                        viewModel.addPatientToQueue(
                            viewModel.patient!!,
                            addedToQueue = {
                                viewModel.showAddToQueueDialog = false
                                scope.launch { pagerState.animateScrollToPage(1) }
                            }
                        )
                    }
                }
            }
        )
    }
    if (viewModel.ifAllSlotsBooked) {
        AllSlotsBookedDialog {
            viewModel.showAllSlotsBookedDialog = false
        }
    }
    if (viewModel.showAppointmentCompletedDialog) {
        AppointmentCompletedDialog {
            viewModel.showAppointmentCompletedDialog = false
        }
    }
}

private fun getCircleColor(riskPercentage: Int): Color {
    return when {
        riskPercentage < 5 -> LowRiskCircle
        riskPercentage in 5..9 -> ModerateRiskCircle
        riskPercentage in 10..19 -> HighRiskCircle
        riskPercentage in 20..29 -> VeryHighRiskCircle
        else -> VeryVeryHighRiskCircle
    }
}

@Composable
private fun getContainerColor(riskPercentage: Int): Color {
    return when (isSystemInDarkTheme()) {
        true -> when {
            riskPercentage < 5 -> LowRiskDarkContainer
            riskPercentage in 5..9 -> ModerateRiskDarkContainer
            riskPercentage in 10..19 -> HighRiskDarkContainer
            else -> VeryHighRiskDarkContainer
        }

        false -> when {
            riskPercentage < 5 -> LowRiskLightContainer
            riskPercentage in 5..9 -> ModerateRiskLightContainer
            riskPercentage in 10..19 -> HighRiskLightContainer
            else -> VeryHighRiskLightContainer
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordsFullDetailsComposable(
    record: CVDResponse,
    onClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = {
            onClick()
        },
        sheetState = rememberModalBottomSheetState(),
        modifier = Modifier
            .navigationBarsPadding(),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.percentage, record.risk.toString()),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = record.createdOn.toddMMMyyyy(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(40.dp),
                    color = if (record.bmi == null) MaterialTheme.colorScheme.surfaceVariant
                    else MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = stringResource(
                            R.string.bmi,
                            record.bmi?.toInt()?.toString() ?: stringResource(R.string.dash)
                        ),
                        style = if (record.bmi == null) MaterialTheme.typography.bodyMedium
                        else MaterialTheme.typography.labelLarge,
                        color = if (record.bmi == null) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                    )
                }
                FilledTonalIconButton(
                    onClick = {
                        onClick()
                    }
                ) {
                    Icon(Icons.Default.Clear, Icons.Default.Clear.name)
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DisplayField(
                    stringResource(R.string.diabetic_colon),
                    YesNoEnum.displayFromCode(record.diabetic)
                )
                DisplayField(
                    stringResource(R.string.current_smoker),
                    YesNoEnum.displayFromCode(record.smoker)
                )
                DisplayField(
                    stringResource(R.string.blood_pressure_colon),
                    "${record.bpSystolic}/${record.bpDiastolic} mmhg"
                )
                DisplayField(
                    stringResource(R.string.total_cholestrol),
                    if (record.cholesterol == null) stringResource(R.string.dash)
                    else "${record.cholesterol} ${record.cholesterolUnit}"
                )
                DisplayField(
                    stringResource(R.string.weight),
                    if (record.weight == null) stringResource(R.string.dash)
                    else "${record.weight} kg"
                )
                DisplayField(
                    stringResource(R.string.height),
                    if (record.heightCm != null) "${record.heightCm} cm"
                    else if (record.heightFt != null || record.heightInch != null) "${record.heightFt} ft ${record.heightInch ?: 0} in"
                    else stringResource(R.string.dash)
                )
            }
        }
    }
}