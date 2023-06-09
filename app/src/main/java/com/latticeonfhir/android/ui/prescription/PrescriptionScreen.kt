package com.latticeonfhir.android.ui.prescription

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.model.prescription.medication.MedicationResponseWithMedication
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.prescription.filldetails.FillDetailsScreen
import com.latticeonfhir.android.ui.prescription.previousprescription.PreviousPrescriptionsScreen
import com.latticeonfhir.android.ui.prescription.quickselect.QuickSelectScreen
import com.latticeonfhir.android.ui.prescription.search.PrescriptionSearchResult
import com.latticeonfhir.android.ui.prescription.search.SearchPrescription
import com.latticeonfhir.android.utils.converters.responseconverter.MedicineInfoConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun PrescriptionScreen(
    navController: NavController,
    viewModel: PrescriptionViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.getActiveIngredients {
                viewModel.activeIngredientsList = it
            }
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    "patient"
                )
            viewModel.patient?.let {
                viewModel.getPreviousPrescription(it.id){
                    viewModel.previousPrescriptionList = it
                }
            }
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
                            text = stringResource(id = R.string.prescription),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (!viewModel.bottomNavExpanded) navController.popBackStack()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "ARROW_BACK")
                        }
                    },
                    actions = {
                        if (viewModel.tabIndex == 1) {
                            IconButton(onClick = {
                                if (!viewModel.bottomNavExpanded) {
                                    viewModel.isSearching = true
                                    viewModel.getPreviousSearch {
                                        viewModel.previousSearchList = it
                                    }
                                }
                            }) {
                                Icon(Icons.Default.Search, contentDescription = "SEARCH_ICON")
                            }
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
                        TabRow(
                            selectedTabIndex = viewModel.tabIndex,
                            modifier = Modifier.testTag("TABS")
                        ) {
                            viewModel.tabs.forEachIndexed { index, title ->
                                Tab(
                                    text = { Text(title) },
                                    modifier = Modifier.testTag(title.uppercase()),
                                    selected = viewModel.tabIndex == index,
                                    onClick = { if (!viewModel.bottomNavExpanded) viewModel.tabIndex = index },
                                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        AnimatedContent(
                            targetState = viewModel.tabIndex,
                            transitionSpec = {
                                if (viewModel.tabIndex == 0) {
                                    slideIntoContainer(
                                        animationSpec = tween(300, easing = EaseIn),
                                        towards = AnimatedContentScope.SlideDirection.Right
                                    ).with(
                                        slideOutOfContainer(
                                            animationSpec = tween(300, easing = EaseIn),
                                            towards = AnimatedContentScope.SlideDirection.Right
                                        )
                                    )
                                } else {
                                    slideIntoContainer(
                                        animationSpec = tween(300, easing = EaseIn),
                                        towards = AnimatedContentScope.SlideDirection.Left
                                    ).with(
                                        slideOutOfContainer(
                                            animationSpec = tween(300, easing = EaseIn),
                                            towards = AnimatedContentScope.SlideDirection.Left
                                        )
                                    )
                                }
                            }
                        ) { targetState ->
                            when (targetState) {
                                0 -> PreviousPrescriptionsScreen()
                                1 -> QuickSelectScreen()
                            }
                        }
                    }
                    if (viewModel.clearAllConfirmDialog) {
                        AlertDialog(
                            onDismissRequest = { viewModel.clearAllConfirmDialog = false },
                            title = {
                                Text(text = stringResource(id = R.string.discard_medications_dialog_title))
                            },
                            text = {
                                Text(text = stringResource(id = R.string.discard_medications_dialog_description))
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        viewModel.selectedActiveIngredientsList = listOf()
                                        viewModel.medicationsResponseWithMedicationList = listOf()
                                        viewModel.bottomNavExpanded = false
                                        viewModel.clearAllConfirmDialog = false
                                    },
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
                                    }
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
            Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.outline.copy(alpha = if (viewModel.bottomNavExpanded && viewModel.selectedActiveIngredientsList.isNotEmpty()) 0.5f else 0f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            BottomNavLayout(viewModel, snackbarHostState, coroutineScope)
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
            modifier = Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.outline.copy(alpha = if (viewModel.isSearching) 0.5f else 0f))
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
fun BottomNavLayout(
    viewModel: PrescriptionViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    val context = LocalContext.current
    val rotationState by animateFloatAsState(
        targetValue = if (viewModel.bottomNavExpanded) 180f else 0f,
        label = "Rotation state of expand icon button",
    )
    AnimatedVisibility(
        visible = viewModel.medicationsResponseWithMedicationList.isNotEmpty(),
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column {
            AnimatedVisibility(viewModel.bottomNavExpanded) {
                Surface(
                    color = MaterialTheme.colorScheme.surface
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
                            Text(text = "Medication (s)")
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                viewModel.clearAllConfirmDialog = true
                            }) {
                                Text(text = stringResource(id = R.string.clear_all))
                            }
                        }
                        Divider()
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
                    .fillMaxWidth(),
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
                            .clickable {
                                viewModel.bottomNavExpanded = !viewModel.bottomNavExpanded
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = "${viewModel.selectedActiveIngredientsList.size} medication",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                viewModel.selectedActiveIngredientsList = listOf()
                                viewModel.medicationsResponseWithMedicationList = emptyList()
                                viewModel.tabIndex = 0
                                viewModel.isSearchResult = false
                                viewModel.patient?.let {
                                    viewModel.getPreviousPrescription(it.id){
                                        viewModel.previousPrescriptionList = it
                                    }
                                }
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = context.getString(R.string.prescribed_successfully),
                                        withDismissAction = true
                                    )
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
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
                        viewModel.selectedActiveIngredientsList = viewModel.selectedActiveIngredientsList - listOf(medication.activeIngredient).toSet()
                        viewModel.medicationsResponseWithMedicationList = viewModel.medicationsResponseWithMedicationList - listOf(medication).toSet()
                        if (viewModel.selectedActiveIngredientsList.isEmpty()) viewModel.bottomNavExpanded = false
                    }
                },
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
                    text = MedicineInfoConverter.getMedInfo(
                        duration = medication.medication.duration,
                        frequency = medication.medication.frequency,
                        medUnit = medication.medUnit,
                        timing = medication.medication.timing,
                        note = medication.medication.note,
                        qtyPerDose = medication.medication.qtyPerDose
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
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}