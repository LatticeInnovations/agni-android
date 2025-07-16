package com.heartcare.agni.ui.historyandtests

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.ui.common.ScrollableTabRowComposable
import com.heartcare.agni.ui.historyandtests.priordx.PriorDxView
import com.heartcare.agni.ui.theme.Black
import com.heartcare.agni.ui.theme.White
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTakingAndTestsScreen(
    navController: NavController,
    viewModel: HistoryTakingAndTestsViewModel = viewModel()
) {
    val tabs = stringArrayResource(R.array.history_and_tests_tabs).toList()
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        tabs.size
    }
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    PATIENT
                )
            viewModel.isLaunched = true
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.history_taking_and_tests),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ScrollableTabRowComposable(
                        tabs,
                        pagerState
                    ) { index ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                index
                            )
                        }
                    }
                    HorizontalPager(
                        state = pagerState
                    ) { index ->
                        when (index) {
                            0 -> PriorDxView(viewModel)
                            else -> Text(tabs[index])
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomAppBar(pagerState, coroutineScope)
        }
    )
}

@Composable
private fun BottomAppBar(
    pagerState: PagerState,
    coroutineScope: CoroutineScope
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isSystemInDarkTheme()) Black else White
            )
            .padding(12.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // add action perform
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.add_icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                when (pagerState.currentPage) {
                    0 -> stringResource(R.string.add_prior_dx)
                    1 -> stringResource(R.string.add_medication)
                    2 -> stringResource(R.string.add_family_history)
                    3 -> stringResource(R.string.add_allergies)
                    4 -> stringResource(R.string.add_risk_factor)
                    5 -> stringResource(R.string.add_tobacco_cessation)
                    else -> ""
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage - 1
                        )
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = pagerState.canScrollBackward
            ) {
                Text(stringResource(R.string.back))
            }
            OutlinedButton(
                onClick = {
                    if (pagerState.canScrollForward) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                pagerState.currentPage + 1
                            )
                        }
                    } else {
                        // perform done action
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    when (pagerState.currentPage) {
                        5 -> stringResource(R.string.done)
                        else -> stringResource(R.string.next)
                    }
                )
            }
        }
    }
}