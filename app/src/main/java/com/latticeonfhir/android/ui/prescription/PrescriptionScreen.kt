package com.latticeonfhir.android.ui.prescription

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.EaseIn
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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.*
import com.latticeonfhir.android.R
import com.latticeonfhir.android.ui.prescription.filldetails.FillDetailsScreen
import com.latticeonfhir.android.ui.prescription.previousprescription.PreviousPrescriptionsScreen
import com.latticeonfhir.android.ui.prescription.quickselect.QuickSelectScreen
import com.latticeonfhir.android.ui.prescription.search.SearchPrescription

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun PrescriptionScreen(
    navController: NavController,
    viewModel: PrescriptionViewModel = viewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                    ),
                    title = {
                        Text(text = "Prescription", style = MaterialTheme.typography.titleLarge)
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "ARROW_BACK")
                        }
                    },
                    actions = {
                        if (viewModel.tabIndex == 1) {
                            IconButton(onClick = { viewModel.isSearching = true }) {
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
                            .fillMaxWidth()
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
                                    onClick = { viewModel.tabIndex = index },
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
                }
            }
        )
        Box(
            modifier =
            Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.outline.copy(alpha = if (viewModel.bottomNavExpanded && viewModel.selectedCompoundList.isNotEmpty()) 0.5f else 0f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            BottomNavLayout(viewModel)
        }
        Box(
            modifier = Modifier
                .matchParentSize(),
        ) {
            AnimatedVisibility(
                visible = viewModel.checkedCompound.isNotEmpty(),
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
fun BottomNavLayout(viewModel: PrescriptionViewModel) {
    AnimatedVisibility(
        visible = viewModel.selectedCompoundList.isNotEmpty(),
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
                                viewModel.selectedCompoundList.clear()
                                viewModel.bottomNavExpanded = false
                            }) {
                                Text(text = "Clear all")
                            }
                        }
                        Divider()
                        LazyColumn(
                            modifier = Modifier
                                .heightIn(0.dp, 450.dp)
                        ) {
                            items(viewModel.selectedCompoundList) { drug ->
                                SelectedCompoundCard(
                                    viewModel = viewModel,
                                    drugName = drug
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
                        Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = "${viewModel.selectedCompoundList.size} medication",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IconButton(onClick = { viewModel.bottomNavExpanded = true }) {
                            Icon(
                                Icons.Default.KeyboardArrowUp,
                                contentDescription = "ARROW_UP"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    Button(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Prescribe")
                    }
                }
            }
        }
    }
}


@Composable
fun SelectedCompoundCard(viewModel: PrescriptionViewModel, drugName: String) {
    val checkedState = remember {
        mutableStateOf(true)
    }
    Column{
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable {
                    checkedState.value = !checkedState.value
                    if (checkedState.value) {
                        viewModel.selectedCompoundList.add(drugName)
                    } else
                        viewModel.selectedCompoundList.remove(drugName)
                }
        ) {
            Checkbox(
                checked = checkedState.value,
                onCheckedChange = {
                    checkedState.value = it
                    if (it) {
                        viewModel.selectedCompoundList.add(drugName)
                    } else
                        viewModel.selectedCompoundList.remove(drugName)
                },
            )
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f)
            ) {
                Text(
                    text = drugName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "1 ml OD, Before food\n" +
                            "Duration : 7 days , Qty : 7 \n" +
                            "Notes : Take rest ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
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