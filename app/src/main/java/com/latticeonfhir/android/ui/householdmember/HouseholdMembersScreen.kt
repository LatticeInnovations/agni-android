package com.latticeonfhir.android.ui.householdmember

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.customTabIndicatorOffset
import com.latticeonfhir.android.ui.householdmember.members.MembersScreen
import com.latticeonfhir.android.ui.main.patientlandingscreen.SuggestionsScreen
import com.latticeonfhir.android.utils.converters.responseconverter.NameConverter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HouseholdMembersScreen(
    navController: NavController,
    viewModel: HouseholdMemberViewModel = viewModel()
) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    "patient"
                )
        }
        viewModel.isLaunched = true
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val pagerState = rememberPagerState()

    val density = LocalDensity.current
    val tabWidths = remember {
        val tabWidthStateList = mutableStateListOf<Dp>()
        repeat(viewModel.tabs.size) {
            tabWidthStateList.add(0.dp)
        }
        tabWidthStateList
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "BACK_ICON")
                    }
                },
                title = {
                    val subTitle = "${viewModel.patient?.gender?.get(0)?.uppercase()}/${
                        viewModel.patient?.birthDate?.toTimeInMilli()
                            ?.toAge()
                    }"
                    Column {
                        Text(
                            text = stringResource(id = R.string.household_members),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.testTag("TITLE")
                        )
                        Text(
                            text = "${
                                NameConverter.getFullName(
                                    viewModel.patient?.firstName,
                                    viewModel.patient?.middleName,
                                    viewModel.patient?.lastName
                                )
                            }, $subTitle", style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.testTag("SUBTITLE")
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        modifier = Modifier.testTag("TABS"),
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.customTabIndicatorOffset(
                                    currentTabPosition = tabPositions[pagerState.currentPage],
                                    tabWidth = tabWidths[pagerState.currentPage]
                                )
                            )
                        }
                    ) {
                        viewModel.tabs.forEachIndexed { index, title ->
                            Tab(
                                text = {
                                    Text(title,
                                        onTextLayout = { textLayoutResult ->
                                            tabWidths[index] =
                                                with(density) { textLayoutResult.size.width.toDp() - 10.dp }
                                        })
                                },
                                modifier = Modifier.testTag(title.uppercase()),
                                selected = pagerState.currentPage == index,
                                onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    HorizontalPager(
                        pageCount = viewModel.tabs.size,
                        state = pagerState
                    ) { index ->
                        when (index) {
                            0 -> viewModel.patient?.let { it1 -> MembersScreen(it1) }
                            1 -> viewModel.patient?.let { it1 ->
                                SuggestionsScreen(
                                    it1,
                                    snackbarHostState,
                                    scope
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (pagerState.currentPage == 0) {
                //if (!viewModel.isUpdateSelected) {
                AnimatedVisibility(visible = !viewModel.isUpdateSelected) {
                    FloatingActionButton(
                        onClick = { viewModel.isUpdateSelected = true },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.testTag("UPDATE_FAB")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.group_icon),
                                contentDescription = null,
                                Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Update")
                        }
                    }
                }
                //else {
                AnimatedVisibility(visible = viewModel.isUpdateSelected) {

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Column {
                            FloatingActionButton(
                                onClick = {
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "patient",
                                        viewModel.patient
                                    )
                                    navController.navigate(Screen.AddHouseholdMember.route)
                                    viewModel.isUpdateSelected = false
                                },
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.testTag("ADD_MEMBER_FAB")
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = stringResource(id = R.string.add_member))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            FloatingActionButton(
                                onClick = { },
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.testTag("EDIT_EXISTING_FAB")
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = stringResource(id = R.string.edit_existing))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Icon(
                                        painter = painterResource(id = R.drawable.edit_icon),
                                        contentDescription = null,
                                        Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            FloatingActionButton(
                                onClick = { viewModel.isUpdateSelected = false },
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.testTag("CLEAR_FAB")
                            ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }

            }
        }
    )
}