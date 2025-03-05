package com.latticeonfhir.android.ui.vaccination

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.TabRowComposable
import com.latticeonfhir.android.ui.theme.TakenLabel
import com.latticeonfhir.android.ui.theme.TakenLabelDark
import com.latticeonfhir.android.ui.vaccination.VaccinationViewModel.Companion.MISSED
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.formatAgeInDaysWeeksMonthsYears
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VaccinationScreen(
    navController: NavController,
    viewModel: VaccinationViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        viewModel.tabs.size
    }
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    PATIENT
                )
        }
        viewModel.isLaunched = true
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON")
                    }
                },
                title = {
                    Text(stringResource(R.string.vaccination))
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TabRowComposable(
                        viewModel.tabs,
                        pagerState
                    ) { index ->
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    }
                    HorizontalPager(
                        state = pagerState
                    ) { index ->
                        when (index) {
                            0 -> {
                                AllVaccinationScreen(viewModel)
                            }

                            1 -> {
                                MissedVaccinationScreen(navController, viewModel)
                            }

                            2 -> {
                                TakenVaccinationScreen(navController, viewModel)
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // navigate to add vaccination screen
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        PATIENT,
                        viewModel.patient
                    )
                    navController.navigate(Screen.AddVaccinationScreen.route)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_icon),
                    contentDescription = null,
                    Modifier.size(24.dp)
                )
            }
        }
    )
}

@Composable
fun AgeComposable(viewModel: VaccinationViewModel) {
    Surface(
        shape = RoundedCornerShape(40.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.padding(top = 16.dp, start = 16.dp)
    ) {
        viewModel.patient?.let { patient ->
            Text(
                text = stringResource(
                    R.string.age_string,
                    formatAgeInDaysWeeksMonthsYears(patient.birthDate)
                ),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun VaccineEmptyScreen(
    msg: String,
    info: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 200.dp, horizontal = 64.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = msg,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
        Text(
            text = info,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outlineVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun VaccineCard(
    missedOrTaken: String,
    navController: NavController,
    patient: PatientResponse
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceBright,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    // navigate to add vaccination
                    if (missedOrTaken == MISSED) {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            PATIENT,
                            patient
                        )
                        navController.navigate(Screen.AddVaccinationScreen.route)
                    } else {
                        navController.navigate(Screen.ViewVaccinationScreen.route)
                    }
                }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Hepatitis B Vaccine",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Hep B",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "1st dose",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    Icons.AutoMirrored.Filled.KeyboardArrowRight.name,
                    tint = MaterialTheme.colorScheme.outline
                )
            }
            Text(
                text = "24 Jan 2024",
                style = MaterialTheme.typography.labelMedium,
                color = if (missedOrTaken == MISSED) MaterialTheme.colorScheme.error
                else {
                    if (isSystemInDarkTheme()) TakenLabelDark
                    else TakenLabel
                }
            )
        }
    }
}