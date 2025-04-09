package com.latticeonfhir.android.prescription.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.latticeonfhir.android.data.local.model.prescription.medication.MedicationResponseWithMedication
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.prescription.R
import com.latticeonfhir.android.prescription.ui.filldetails.FillDetailsScreen
import com.latticeonfhir.android.prescription.ui.quickselect.QuickSelectScreen
import com.latticeonfhir.android.prescription.ui.search.PrescriptionSearchResult
import com.latticeonfhir.android.prescription.ui.search.SearchPrescription
import com.latticeonfhir.android.prescription.utils.MedicationInfoConverter.getMedInfo
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.android.utils.converters.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.TimeConverter.toTodayStartDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionScreen(
    navController: NavController,
    viewModel: PrescriptionViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    SetBackHandler(viewModel, navController)
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.getActiveIngredients {
                viewModel.activeIngredientsList = it
            }
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    PATIENT
                )
            viewModel.getPatientTodayAppointment(
                Date(Date().toTodayStartDate()),
                Date(Date().toEndOfDay()),
                viewModel.patient!!.id
            )
        }
        viewModel.getAllMedicationDirections {
            viewModel.medicationDirectionsList = it
        }
        viewModel.isLaunched = true
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (viewModel.selectedActiveIngredientsList.isNotEmpty()) 60.dp else 0.dp),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                    ),
                    title = {
                        Text(
                            text = stringResource(id = R.string.fill_prescription),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.testTag("HEADING_TAG")
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "BACK_ICON"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.isSearching = true
                            viewModel.getPreviousSearch {
                                viewModel.previousSearchList = it
                            }
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "SEARCH_ICON")
                        }
                    }
                )
            },
            content = {
                Box(
                    modifier = Modifier
                        .padding(it)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        QuickSelectScreen()
                    }
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
                                        viewModel.selectedActiveIngredientsList = listOf()
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
                }
            }
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(bottom = if (viewModel.selectedActiveIngredientsList.isNotEmpty()) 60.dp else 0.dp),
        ) {
            AnimatedVisibility(
                visible = viewModel.isSearchResult,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                PrescriptionSearchResult(viewModel)
            }
        }
        Box(
            modifier =
            if (!(viewModel.bottomNavExpanded && viewModel.selectedActiveIngredientsList.isNotEmpty())) Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0f))
            else Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                .clickable(enabled = false) { },
            contentAlignment = Alignment.BottomCenter
        ) {
            BottomNavLayout(viewModel, navController, coroutineScope)
        }
        Box(
            modifier = Modifier
                .matchParentSize(),
        ) {
            AnimatedVisibility(
                visible = viewModel.checkedActiveIngredient.isNotEmpty(),
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                FillDetailsScreen(prescriptionViewModel = viewModel)
            }
        }
        Box(
            modifier = if (!viewModel.isSearching) Modifier
                .matchParentSize()
                .statusBarsPadding()
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0f))
            else Modifier
                .matchParentSize()
                .statusBarsPadding()
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                .clickable(enabled = false) { }
        ) {
            AnimatedVisibility(
                visible = viewModel.isSearching,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it })
            ) {
                SearchPrescription(viewModel)
            }
        }
    }
}

@Composable
private fun SetBackHandler(viewModel: PrescriptionViewModel, navController: NavController) {
    BackHandler(enabled = true) {
        when {
            viewModel.isSearching -> viewModel.isSearching = false
            viewModel.checkedActiveIngredient.isNotEmpty() -> {
                viewModel.checkedActiveIngredient = ""
                viewModel.medicationToEdit = null
            }

            viewModel.bottomNavExpanded -> viewModel.bottomNavExpanded = false
            viewModel.isSearchResult -> viewModel.isSearchResult = false
            else -> navController.popBackStack()
        }
    }
}

@Composable
fun BottomNavLayout(
    viewModel: PrescriptionViewModel,
    navController: NavController,
    coroutineScope: CoroutineScope
) {
    val rotationState by animateFloatAsState(
        targetValue = if (viewModel.bottomNavExpanded) 180f else 0f,
        label = "Rotation state of expand icon button",
    )
    AnimatedVisibility(
        visible = viewModel.medicationsResponseWithMedicationList.isNotEmpty(),
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column(
            modifier = Modifier.navigationBarsPadding()
        ) {
            AnimatedVisibility(viewModel.bottomNavExpanded) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("BOTTOM_NAV_EXPANDED")
                ) {
                    Column(
                        modifier = Modifier.padding(bottom = 15.dp)
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
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        Modifier
                            .weight(1f)
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
                            text = "${viewModel.selectedActiveIngredientsList.size} medication",
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
                    Button(
                        onClick = {
                            // add medications to prescriptions
                            viewModel.insertPrescription {
                                coroutineScope.launch {
                                    navController.navigateUp()
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
                onCheckedChange = {
                    if (!it) {
                        viewModel.selectedActiveIngredientsList -= listOf(medication.activeIngredient).toSet()
                        viewModel.medicationsResponseWithMedicationList -= listOf(medication).toSet()
                        if (viewModel.selectedActiveIngredientsList.isEmpty()) viewModel.bottomNavExpanded =
                            false
                    }
                },
                modifier = Modifier.testTag("MEDICATION_CHECKBOX")
            )
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f)
            ) {
                Text(
                    text = medication.medName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = getMedInfo(
                        duration = medication.medication.duration,
                        frequency = medication.medication.frequency,
                        medUnit = medication.medUnit,
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
                viewModel.checkedActiveIngredient = medication.activeIngredient
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