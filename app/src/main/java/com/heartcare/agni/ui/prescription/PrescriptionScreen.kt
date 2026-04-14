package com.heartcare.agni.ui.prescription

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.RecordType
import com.heartcare.agni.data.local.model.prescription.medication.MedicationResponseWithMedication
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.ui.common.AppointmentCompletedDialog
import com.heartcare.agni.ui.common.CustomDialog
import com.heartcare.agni.ui.common.RecordTypeSelectionContent
import com.heartcare.agni.ui.common.ScreeningSiteListContent
import com.heartcare.agni.ui.common.TabRowComposable
import com.heartcare.agni.ui.patientlandingscreen.AllSlotsBookedDialog
import com.heartcare.agni.ui.prescription.filldetails.FillDetailsScreen
import com.heartcare.agni.ui.prescription.previousprescription.PreviousPrescriptionsScreen
import com.heartcare.agni.ui.prescription.previousprescription.saveRePrescription
import com.heartcare.agni.ui.prescription.quickselect.QuickSelectScreen
import com.heartcare.agni.ui.prescription.search.PrescriptionSearchResult
import com.heartcare.agni.ui.prescription.search.SearchPrescription
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.constants.ScreenSiteConstants.SITE_LIST
import com.heartcare.agni.utils.converters.responseconverter.medication.MedicationInfoConverter.getMedInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionScreen(
    navController: NavController,
    viewModel: PrescriptionViewModel = hiltViewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        viewModel.tabs.size
    }

    var currentStep by remember { mutableIntStateOf(0) }
    var selectedSite by remember { mutableStateOf<String?>(null) }
    var selectedType by remember { mutableStateOf<RecordType?>(null) }

    fun handleAddPrescriptionAction(onWizardReset: () -> Unit) {
        viewModel.getAppointmentInfo(
            callback = {
                when {
                    viewModel.existsInOtherHospital -> {
                        onWizardReset()
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(
                                message = context.getString(R.string.appointment_exists_in_other_hospital)
                            )
                        }
                    }

                    viewModel.canAddAssessment -> {
                        onWizardReset()
                        if (viewModel.isReprescribing) {
                             saveRePrescription(context, viewModel, viewModel.represcribingPrescription!!, coroutineScope, snackBarHostState, pagerState)
                        } else {
                             viewModel.setTodayData()
                             coroutineScope.launch {
                                 pagerState.animateScrollToPage(1)
                             }
                        }
                    }

                    viewModel.isAppointmentCompleted -> {
                        onWizardReset()
                        viewModel.showAppointmentCompletedDialog = true
                    }

                    else -> {
                        onWizardReset()
                        viewModel.showAddToQueueDialog = true
                    }
                }
            }
        )
    }

    SetBackHandler(viewModel, navController, pagerState, coroutineScope, currentStep) { currentStep = it }
    HandleLaunchedEffect(viewModel, navController)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = if (pagerState.pageCount == 1) 60.dp else 0.dp),
        topBar = {
            PrescriptionTopAppBar(viewModel, navController, pagerState, coroutineScope)
        },
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
            ) {
                when (currentStep) {
                    0 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            TabRowComposable(
                                viewModel.tabs,
                                pagerState
                            ) { index ->
                                handleTabNavigation(
                                    index = index,
                                    viewModel = viewModel,
                                    pagerState = pagerState,
                                    coroutineScope = coroutineScope,
                                    snackBarHostState = snackBarHostState,
                                    context = context,
                                    onTabRequiresWizard = {
                                        if (viewModel.todayPrescription != null && !viewModel.existsInOtherHospital) {
                                            handleAddPrescriptionAction { currentStep = 0 }
                                        } else {
                                            currentStep = 1
                                        }
                                    }
                                )
                            }
                            HorizontalPager(
                                state = pagerState,
                                userScrollEnabled = viewModel.canAddAssessment && viewModel.patient!!.patientDeceasedReason.isNullOrBlank()
                            ) { index ->
                                when (index) {
                                    0 -> PreviousPrescriptionsScreen(
                                        snackBarHostState = snackBarHostState,
                                        coroutineScope = coroutineScope,
                                        pagerState = pagerState,
                                        viewModel = viewModel,
                                        onRequireWizard = {
                                            if (viewModel.todayPrescription != null && !viewModel.existsInOtherHospital) {
                                                handleAddPrescriptionAction { currentStep = 0 }
                                            } else {
                                                currentStep = 1
                                            }
                                        }
                                    )

                                    1 -> QuickSelectScreen()
                                }
                            }
                        }
                    }
                    1 -> RecordTypeSelectionContent(
                        modifier = Modifier.fillMaxSize(),
                        selectedType = selectedType,
                        onTypeSelected = { selectedType = it },
                        onContinueClick = {
                            if (selectedType == RecordType.FACILITY) {
                                handleAddPrescriptionAction() { currentStep = 0 }
                            } else if (selectedType == RecordType.SCREENING_SITE) {
                                currentStep = 2
                            }
                        }
                    )
                    2 -> ScreeningSiteListContent(
                        modifier = Modifier.fillMaxSize(),
                        sites = SITE_LIST,
                        selectedSite = selectedSite,
                        onSiteSelected = { selectedSite = it },
                        onBackClick = { currentStep = 1 },
                        onContinueClick = {
                            handleAddPrescriptionAction() { currentStep = 0 }
                        }
                    )
                }
            }
        }
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .padding(bottom = if (viewModel.selectedMedicationsList.isNotEmpty() && pagerState.currentPage == 1) 70.dp else 6.dp)
                .navigationBarsPadding()
        )
    }
    PrescriptionSearchResult(viewModel)
    BottomNavLayout(viewModel, snackBarHostState, pagerState, coroutineScope, context)
    FillDetailsScreen(prescriptionViewModel = viewModel)
    SearchPrescription(viewModel)
    PrescriptionDialogs(
        viewModel = viewModel,
        snackBarHostState = snackBarHostState,
        pagerState = pagerState,
        coroutineScope = coroutineScope,
        context = context
    )
}

@Composable
private fun SetBackHandler(
    viewModel: PrescriptionViewModel,
    navController: NavController,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    currentStep: Int,
    onStepChange: (Int) -> Unit
) {
    BackHandler(enabled = true) {
        when {
            viewModel.isSearching -> viewModel.isSearching = false
            currentStep > 0 -> {
                if (currentStep == 2) onStepChange(1)
                else onStepChange(0)
            }
            viewModel.checkedMedication != null -> {
                viewModel.checkedMedication = null
                viewModel.medicationToEdit = null
            }

            viewModel.bottomNavExpanded -> viewModel.bottomNavExpanded = false
            viewModel.isSearchResult -> viewModel.isSearchResult = false
            pagerState.currentPage == 1 -> coroutineScope.launch { pagerState.animateScrollToPage(0) }
            else -> navController.popBackStack()
        }
    }
}

@Composable
private fun HandleLaunchedEffect(
    viewModel: PrescriptionViewModel,
    navController: NavController
) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.getMedications {
                viewModel.medicationsList = it
            }
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    PATIENT
                )
            viewModel.getPreviousPrescription(viewModel.patient!!.id)
            viewModel.getAppointmentInfo { }
        }
        viewModel.getAllMedicationDirections {
            viewModel.medicationDirectionsList = it
        }
        viewModel.isLaunched = true
    }
}

private fun handleTabNavigation(
    index: Int,
    viewModel: PrescriptionViewModel,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    context: Context,
    onTabRequiresWizard: () -> Unit
) {
    if (index == 1) {
        if (viewModel.patient!!.patientDeceasedReason.isNullOrBlank()) {
             viewModel.isReprescribing = false
             viewModel.represcribingPrescription = null
             onTabRequiresWizard()
        } else {
            coroutineScope.launch {
                snackBarHostState.showSnackbar(
                    context.getString(R.string.patient_deceased_error_msg)
                )
            }
        }
    } else coroutineScope.launch {
        pagerState.animateScrollToPage(
            index
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrescriptionTopAppBar(
    viewModel: PrescriptionViewModel,
    navController: NavController,
    pagerState: PagerState,
    coroutineScope: CoroutineScope
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
        ),
        title = {
            Text(
                text = stringResource(id = R.string.prescription),
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    if (pagerState.currentPage == 1)
                        coroutineScope.launch { pagerState.animateScrollToPage(0) }
                    else navController.navigateUp()
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "BACK_ICON"
                )
            }
        },
        actions = {
            if (pagerState.currentPage == 1) {
                IconButton(onClick = {
                    viewModel.isSearching = true
                    viewModel.getPreviousSearch {
                        viewModel.previousSearchList = it
                    }
                }) {
                    Icon(Icons.Default.Search, contentDescription = "SEARCH_ICON")
                }
            }
        }
    )
}

@Composable
fun BottomNavLayout(
    viewModel: PrescriptionViewModel,
    snackBarHostState: SnackbarHostState,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    context: Context
) {
    val rotationState by animateFloatAsState(
        targetValue = if (viewModel.bottomNavExpanded) 180f else 0f,
        label = "Rotation state of expand icon button",
    )
    Box(
        modifier =
            if (!viewModel.bottomNavExpanded) Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0f))
            else Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                .clickable(enabled = false) { },
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = pagerState.currentPage == 1,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                AnimatedVisibility(viewModel.bottomNavExpanded) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.testTag("BOTTOM_NAV_EXPANDED")
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(bottom = 15.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { viewModel.bottomNavExpanded = false }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "CLEAR_ICON"
                                    )
                                }
                                Text(
                                    text = "Medication (s)",
                                    modifier = Modifier.testTag("MEDICATION_TITLE")
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(
                                    onClick = {
                                        viewModel.clearAllConfirmDialog = true
                                    },
                                    Modifier.testTag("CLEAR_ALL_BTN")
                                ) {
                                    Text(text = stringResource(id = R.string.clear_all))
                                }
                            }
                            HorizontalDivider()
                            LazyColumn(
                                modifier = Modifier
                                    .heightIn(0.dp, 450.dp)
                            ) {
                                items(viewModel.medicationsResponseWithMedicationList) { medication ->
                                    SelectedCompoundCard(
                                        viewModel = viewModel,
                                        medication = medication
                                    )
                                }
                            }
                        }
                    }
                }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("BOTTOM_NAV_ROW"),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shadowElevation = 15.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedVisibility(visible = viewModel.selectedMedicationsList.isNotEmpty()) {
                            Row(
                                Modifier
                                    .fillMaxWidth(0.5f)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        viewModel.bottomNavExpanded = !viewModel.bottomNavExpanded
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Text(
                                    text = "${viewModel.selectedMedicationsList.size} medication",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.testTag("MEDICATION_TEXT")
                                )
                                Icon(
                                    Icons.Default.KeyboardArrowUp,
                                    contentDescription = "ARROW_UP",
                                    modifier = Modifier.rotate(rotationState)
                                )
                            }
                            Spacer(modifier = Modifier.width(15.dp))
                        }
                        Button(
                            onClick = {
                                // add medications to prescriptions
                                viewModel.insertPrescription {
                                    viewModel.bottomNavExpanded = false
                                    viewModel.selectedMedicationsList = listOf()
                                    viewModel.medicationsResponseWithMedicationList = emptyList()
                                    coroutineScope.launch { pagerState.animateScrollToPage(0) }
                                    viewModel.isSearchResult = false
                                    viewModel.patient?.let {
                                        viewModel.getPreviousPrescription(it.id)
                                    }
                                    coroutineScope.launch {
                                        snackBarHostState.showSnackbar(
                                            message = context.getString(R.string.prescribed_successfully),
                                            withDismissAction = true
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("PRESCRIBE_BTN")
                        ) {
                            Text(text = stringResource(id = R.string.prescribe))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectedCompoundCard(
    viewModel: PrescriptionViewModel,
    medication: MedicationResponseWithMedication
) {
    val context = LocalContext.current
    val checkedState = remember {
        mutableStateOf(true)
    }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Checkbox(
                checked = checkedState.value,
                onCheckedChange = { checked ->
                    if (!checked) {
                        viewModel.selectedMedicationsList =
                            viewModel.selectedMedicationsList.filter {
                                it.medFhirId != medication.medication.medFhirId
                            }
                        viewModel.medicationsResponseWithMedicationList -= listOf(medication).toSet()
                        if (viewModel.selectedMedicationsList.isEmpty()) viewModel.bottomNavExpanded =
                            false
                    }
                },
                modifier = Modifier.testTag("MEDICATION_CHECKBOX")
            )
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = medication.medicationResponse.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${medication.medicationResponse.code} · ${medication.medicationResponse.categoryName} · ${medication.medicationResponse.className} ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                medication.medication.brandName?.ifBlank { null }?.let {
                    Text(
                        text = stringResource(R.string.brand_name, it),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = getMedInfo(
                        duration = medication.medication.duration,
                        frequency = medication.medication.frequency,
                        medUnit = medication.medication.doseForm.lowercase(),
                        timing = medication.medication.timing,
                        note = medication.medication.note,
                        qtyPerDose = medication.medication.qtyPerDose,
                        qtyPrescribed = medication.medication.qtyPrescribed,
                        context = context
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = {
                viewModel.checkedMedication = viewModel.selectedMedicationsList.first {
                    it.medFhirId == medication.medication.medFhirId
                }
                viewModel.medicationToEdit = medication
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.edit_icon),
                    contentDescription = "EDIT_ICON",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun PrescriptionDialogs(
    viewModel: PrescriptionViewModel,
    snackBarHostState: SnackbarHostState,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    context: Context
) {
    if (viewModel.clearAllConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.clearAllConfirmDialog = false },
            title = {
                Text(
                    text = stringResource(id = R.string.discard_medications_dialog_title),
                    modifier = Modifier.testTag("DIALOG_TITLE")
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.discard_medications_dialog_description),
                    modifier = Modifier.testTag("DIALOG_DESCRIPTION")
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.selectedMedicationsList = listOf()
                        viewModel.medicationsResponseWithMedicationList = listOf()
                        viewModel.bottomNavExpanded = false
                        viewModel.clearAllConfirmDialog = false
                    },
                    modifier = Modifier.testTag("POSITIVE_BTN")
                ) {
                    Text(
                        stringResource(id = R.string.yes_discard)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllConfirmDialog = false
                    },
                    modifier = Modifier.testTag("NEGATIVE_BTN")
                ) {
                    Text(
                        stringResource(id = R.string.no_go_back)
                    )
                }
            }
        )
    }
    if (viewModel.showAddToQueueDialog) {
        AddToQueueDialog(viewModel, pagerState, coroutineScope, context, snackBarHostState)
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

@Composable
private fun AddToQueueDialog(
    viewModel: PrescriptionViewModel,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    context: Context,
    snackBarHostState: SnackbarHostState
) {

    fun onAddedSuccessfully() {
        viewModel.showAddToQueueDialog = false
        viewModel.canAddAssessment = true

        if (viewModel.isReprescribing) {
            saveRePrescription(
                context = context,
                viewModel = viewModel,
                prescription = viewModel.represcribingPrescription!!,
                coroutineScope = coroutineScope,
                snackBarHostState = snackBarHostState,
                pagerState = pagerState
            )
        } else {
            coroutineScope.launch {
                pagerState.animateScrollToPage(1)
            }
        }
    }

    CustomDialog(
        title = stringResource(
            if (viewModel.appointment != null) R.string.patient_arrived_question
            else R.string.add_to_queue_question
        ),
        text = stringResource(R.string.add_to_queue_assessment_dialog_description),
        dismissBtnText = stringResource(R.string.dismiss),
        confirmBtnText = stringResource(
            if (viewModel.appointment != null) R.string.mark_arrived
            else R.string.add_to_queue
        ),
        dismiss = { viewModel.showAddToQueueDialog = false },
        confirm = {
            if (viewModel.appointment != null) {
                viewModel.updateStatusToArrived(
                    patient = viewModel.patient!!,
                    appointment = viewModel.appointment!!,
                    updated = { onAddedSuccessfully() }
                )
            } else {
                if (viewModel.ifAllSlotsBooked) {
                    viewModel.showAllSlotsBookedDialog = true
                } else {
                    viewModel.addPatientToQueue(
                        patient = viewModel.patient!!,
                        addedToQueue = { onAddedSuccessfully() }
                    )
                }
            }
        }
    )
}