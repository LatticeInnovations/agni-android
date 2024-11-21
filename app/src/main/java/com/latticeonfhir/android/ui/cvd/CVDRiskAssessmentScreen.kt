package com.latticeonfhir.android.ui.cvd

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.common.TabRowComposable
import com.latticeonfhir.android.ui.cvd.form.CVDRiskAssessmentForm
import com.latticeonfhir.android.ui.cvd.form.DisplayField
import com.latticeonfhir.android.ui.cvd.records.CVDRiskAssessmentRecords
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

    BackHandler {
        if (viewModel.showBottomSheet) viewModel.showBottomSheet = false
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
                        scope.launch { pagerState.animateScrollToPage(index) }
                    }
                    HorizontalPager(
                        state = pagerState
                    ) { index ->
                        when (index) {
                            0 -> CVDRiskAssessmentForm(viewModel)
                            1 -> CVDRiskAssessmentRecords(viewModel)
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (pagerState.currentPage == 0) {
                Column {
                    AnimatedVisibility(
                        viewModel.riskPercentage.isNotBlank(),
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = getContainerColor(viewModel.riskPercentage.toInt()),
                            shape = RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.padding(start = 6.dp)
                                ) {
                                    Canvas(modifier = Modifier.size(12.dp), onDraw = {
                                        drawCircle(color = getCircleColor(viewModel.riskPercentage.toInt()))
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.riskPercentage = "4"
                            },
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(text = stringResource(R.string.predict))
                        }
                        if (viewModel.riskPercentage.isNotBlank()) {
                            Button(
                                onClick = {
                                    // save form
                                    // reset form values
                                    scope.launch {
                                        pagerState.animateScrollToPage(1)
                                        snackbarHostState.showSnackbar(
                                            message = context.getString(R.string.assessment_record_saved)
                                        )
                                    }
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
    Box(
        modifier =
        if (!viewModel.showBottomSheet) Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0f))
        else Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            .clickable(enabled = false) { },
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = viewModel.showBottomSheet,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            RecordsFullDetailsComposable(
                onClick = {
                    viewModel.showBottomSheet = false
                }
            )
        }
    }
}

private fun getCircleColor(riskPercentage: Int): Color {
    return if (riskPercentage < 5) LowRiskCircle
    else if (riskPercentage in 5..9) ModerateRiskCircle
    else if (riskPercentage in 10..19) HighRiskCircle
    else if (riskPercentage in 20..29) VeryHighRiskCircle
    else VeryVeryHighRiskCircle
}

@Composable
private fun getContainerColor(riskPercentage: Int): Color {
    return when (isSystemInDarkTheme()) {
        true -> {
            if (riskPercentage < 5) LowRiskDarkContainer
            else if (riskPercentage in 5..9) ModerateRiskDarkContainer
            else if (riskPercentage in 10..19) HighRiskDarkContainer
            else VeryHighRiskDarkContainer
        }

        false -> {
            if (riskPercentage < 5) LowRiskLightContainer
            else if (riskPercentage in 5..9) ModerateRiskLightContainer
            else if (riskPercentage in 10..19) HighRiskLightContainer
            else VeryHighRiskLightContainer
        }
    }
}

@Composable
private fun RecordsFullDetailsComposable(
    onClick: () -> Unit
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
                    text = stringResource(R.string.percentage_and_risk, 12, "High risk"),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "18-Sep-2024",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                shape = RoundedCornerShape(40.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = stringResource(R.string.bmi, "23"),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
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
            DisplayField(stringResource(R.string.diabetic_colon), "Yes")
            DisplayField(stringResource(R.string.current_smoker), "No")
            DisplayField(stringResource(R.string.blood_pressure_colon), "180/110 mmhg")
            DisplayField(stringResource(R.string.total_cholestrol), "4 mmol/L")
            DisplayField(stringResource(R.string.weight), "67 kg")
            DisplayField(stringResource(R.string.height), "170 cm")
        }
    }
}