package com.latticeonfhir.android.symptomsanddiagnosis.ui.addSymptomsanddiagnosis

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.data.server.model.symptomsanddiagnosis.SymptomsAndDiagnosisItem
import com.latticeonfhir.android.symptomsanddiagnosis.R
import com.latticeonfhir.android.symptomsanddiagnosis.ui.components.BodyPartCard
import com.latticeonfhir.android.symptomsanddiagnosis.ui.components.BottomSheetLayout
import com.latticeonfhir.android.symptomsanddiagnosis.ui.components.SymptomsCustomChip
import com.latticeonfhir.android.symptomsanddiagnosis.utils.constants.SymptomsAndDiagnosisConstants.SYM_DIAG_UPDATE_OR_ADD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSymptomsScreen(
    navController: NavController, viewModel: AddSymptomsAndDiagnosisViewModel = hiltViewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    HandleLaunchedEffect(viewModel, snackBarHostState)
    BackHandler {
        viewModel.apply {
            if (showSelectSymptomScreen) {
                showSelectSymptomScreen = false
                state = 0
            } else {
                navController.navigateUp()
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Scaffold(modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
            snackbarHost = {
                SnackbarHost(
                    hostState = snackBarHostState,
                    modifier = if (viewModel.state == 1) Modifier.padding(bottom = 66.dp) else Modifier
                )
        }, topBar = {
            TopAppBarLayout(navController, viewModel)
        }, content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    PrimaryTabRow(
                        modifier = Modifier.fillMaxWidth(), selectedTabIndex = viewModel.state
                    ) {
                        viewModel.tabs.forEachIndexed { index, title ->
                            Tab(selected = viewModel.state == index,
                                onClick = { viewModel.state = index },
                                text = {
                                    Text(
                                        text = title, maxLines = 2, overflow = TextOverflow.Ellipsis
                                    )
                                })
                        }

                    }
                    when {
                        viewModel.state == 0 -> {
                            ShowPredefinedListView(
                                viewModel = viewModel, context = context
                            )
                        }

                        else -> {
                            ShowManualEntryView(viewModel)
                        }
                    }
                }

                Box(
                    modifier = if (!(viewModel.bodyPartBottomNavExpanded)) Modifier
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0f))
                    else Modifier
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        .clickable(enabled = false) { }, contentAlignment = Alignment.BottomCenter
                ) {
                    BottomSheetLayout(viewModel = viewModel)
                }


            })
    }

}

@Composable
fun ShowManualEntryView(viewModel: AddSymptomsAndDiagnosisViewModel) {
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.padding(
                start = 16.dp, top = 16.dp
            ),
            text = stringResource(R.string.additional_symptoms),
            style = MaterialTheme.typography.labelLarge
        )

        OutlinedTextField(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                focusRequester.requestFocus()
            }
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 4.dp)
            .wrapContentHeight()
            .focusRequester(focusRequester)
            .focusable(),
            value = viewModel.additionSymptoms,
            maxLines = 3,
            minLines = 2,
            onValueChange = { input ->
                if (input.length <= 100 && input.all { it.isLetterOrDigit() || it.isWhitespace() }) {
                    viewModel.additionSymptoms = input
                }
            },
            placeholder = {
                Text(
                    text = "Describe other symptoms here...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            },
            supportingText = {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${viewModel.additionSymptoms.length}/100",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words
            )
        )


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                enabled = viewModel.additionSymptoms.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (!viewModel.selectedActiveSymptomsList.contains(
                            SymptomsAndDiagnosisItem(
                                code = viewModel.additionSymptoms.trim(),
                                display = viewModel.additionSymptoms.trim()
                            )
                        )
                    ) {
                        viewModel.selectedActiveSymptomsList.add(
                            SymptomsAndDiagnosisItem(
                                code = viewModel.additionSymptoms.trim(),
                                display = viewModel.additionSymptoms.trim()
                            )
                        )
                        viewModel.showSelectSymptomScreen = false
                        viewModel.state = 0
                        viewModel.additionSymptoms = ""
                    } else {
                        viewModel.msg = context.getString(R.string.already_added)
                    }
                    viewModel.isNoSymptomChecked = false
                },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Text(
                    stringResource(id = R.string.add_symtoms_title)
                )
            }
        }


    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShowPredefinedListView(
    viewModel: AddSymptomsAndDiagnosisViewModel, context: Context
) {
    val bodyPartList = viewModel.bodyPartList.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                SearchField(viewModel)
                val list = viewModel.mostRecentSymptomsFlow.collectAsState()
                if (list.value.isNotEmpty()) {
                    Text(
                        modifier = Modifier.padding(
                            start = 16.dp, top = 16.dp
                        ),
                        text = stringResource(R.string.most_searched),
                        style = MaterialTheme.typography.labelLarge
                    )
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, bottom = 8.dp, top = 16.dp, end = 8.dp)
                    ) {
                        list.value.forEachIndexed { index, symptom ->
                            SymptomsCustomChip(
                                modifier = Modifier.padding(start = 8.dp),
                                idSelected = false,
                                label = symptom
                            ) {
                                if (!viewModel.selectedActiveSymptomsList.contains(
                                        SymptomsAndDiagnosisItem(
                                            code = list.value[index],
                                            display = list.value[index]
                                        )
                                    )
                                ) {
                                    viewModel.selectedActiveSymptomsList.add(
                                        SymptomsAndDiagnosisItem(
                                            code = list.value[index],
                                            display = list.value[index]
                                        )
                                    )
                                    viewModel.showSelectSymptomScreen = false
                                } else {
                                    viewModel.msg = context.getString(R.string.already_added)
                                }
                                viewModel.isNoSymptomChecked = false
                            }
                        }
                    }
                }
                Text(
                    modifier = Modifier.padding(
                        start = 16.dp, top = 16.dp, bottom = 16.dp
                    ),
                    text = stringResource(R.string.select_by_body_part),
                    style = MaterialTheme.typography.labelLarge
                )

            }
        }

        itemsIndexed(bodyPartList.value.toList()) { index, item ->
            BodyPartCard(
                title = item, index = index, lastIndex = bodyPartList.value.toList().lastIndex
            ) {
                viewModel.bodyPartBottomNavExpanded = true
                viewModel.clickedBodyPart = item
            }

        }
    }
}

@Composable
fun HandleLaunchedEffect(
    viewModel: AddSymptomsAndDiagnosisViewModel,
    snackBarHostState: SnackbarHostState
) {
    LaunchedEffect(key1 = viewModel.msg) {
        if (viewModel.msg.isNotBlank()) {
            snackBarHostState.showSnackbar(viewModel.msg)
            viewModel.msg = ""
        }
    }
}


@Composable
private fun SearchField(viewModel: AddSymptomsAndDiagnosisViewModel) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp)
        .clickable {
            viewModel.isSearching = true
        }) {
        OutlinedTextField(value = "",
            enabled = false,
            onValueChange = { },
            placeholder = { Text(text = stringResource(R.string.type_to_search_symptoms)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search)
                )
            },
            readOnly = true,
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.isSearching = true
                    viewModel.showSelectSymptomScreen = false
                }
                .padding(start = 16.dp, end = 16.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                disabledIndicatorColor = Color.Transparent,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
            ))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarLayout(
    navController: NavController,
    viewModel: AddSymptomsAndDiagnosisViewModel
) {
    TopAppBar(modifier = Modifier.fillMaxWidth(), colors = TopAppBarDefaults.largeTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface
    ), actions = {
        IconButton(onClick = {
            navController.previousBackStackEntry?.savedStateHandle?.set(
                SYM_DIAG_UPDATE_OR_ADD,
                ""
            )
            viewModel.showSelectSymptomScreen = false
            viewModel.state = 0
            viewModel.additionSymptoms = ""
        }) {
            Icon(
                Icons.Filled.Close, contentDescription = "BACK_ICON"
            )
        }
    }, title = {
        Text(
            text = if (viewModel.local != null) stringResource(R.string.edit_symtoms_title) else stringResource(
                R.string.add_symtoms_title
            ),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.testTag("VITAL_TITLE_TEXT")
        )
    })


}

fun handleNavigate(
    viewModel: AddSymptomsAndDiagnosisViewModel,
    coroutineScope: CoroutineScope,
    navController: NavController,
    context: Context,
    navigate: (NavController) -> Unit
) {
    if (viewModel.local != null) {
        viewModel.updateSymDiag {
            coroutineScope.launch {
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    SYM_DIAG_UPDATE_OR_ADD,
                    context.getString(R.string.symdiag_update_successfully)
                )
                navigate(navController)
            }
        }
    } else {
        viewModel.insertSymDiag {
            coroutineScope.launch {
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    SYM_DIAG_UPDATE_OR_ADD,
                    context.getString(R.string.symdiag_added_successfully)
                )
                navigate(navController)
            }
        }
    }

}



