package com.latticeonfhir.android.ui.symptomsanddiagnosis.selectsymptoms

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.roomdb.entities.symptomsanddiagnosis.SymptomsAndDiagnosisLocal
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.symptomsanddiagnosis.addSymptomsanddiagnosis.AddSymptomsAndDiagnosisViewModel
import com.latticeonfhir.android.ui.symptomsanddiagnosis.addSymptomsanddiagnosis.AddSymptomsScreen
import com.latticeonfhir.android.ui.symptomsanddiagnosis.addSymptomsanddiagnosis.SearchSymptomsAndDiagnosis
import com.latticeonfhir.android.ui.symptomsanddiagnosis.components.SymptomsCustomChip
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.android.utils.constants.SymptomsAndDiagnosisConstants.SYM_DIAG
import com.latticeonfhir.android.utils.constants.SymptomsAndDiagnosisConstants.SYM_DIAG_UPDATE_OR_ADD
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectSymptomScreen(
    navController: NavController,
    viewModel: AddSymptomsAndDiagnosisViewModel = hiltViewModel()
) {

    val snackBarHostState = remember { SnackbarHostState() }

    HandleLaunchedEffect(
        viewModel,
        navController,
        snackBarHostState
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Scaffold(modifier = Modifier.fillMaxSize(), snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }, topBar = {
            TopAppBarLayout(navController, viewModel)
        }, content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(paddingValues = paddingValues)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 16.dp),
                    text = stringResource(id = R.string.student_symptoms),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                FlowRow(
                    modifier = if (viewModel.selectedActiveSymptomsList.isNotEmpty()) Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        ) else Modifier,
                ) {
                    viewModel.selectedActiveSymptomsList.forEach { symptoms ->

                        SymptomsCustomChip(
                            idSelected = true,
                            label = symptoms.display,
                            icon = Icons.Default.Clear
                        ) {
                            viewModel.removeSymptom(symptoms)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }

                OutlinedButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = if (viewModel.selectedActiveSymptomsList.isEmpty()) Modifier
                        .padding(start = 16.dp, top = 16.dp)
                        .wrapContentWidth() else Modifier
                        .padding(start = 16.dp)
                        .wrapContentWidth(), onClick = {
                        viewModel.showSelectSymptomScreen = true

                    }, contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add_icon),
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Select Symptoms")
                }
                NoSymptomsCheckBox(viewModel)

            }

        }, bottomBar = {
            BottomButtonLayout(viewModel, navController)
        })
    }

    Box(
        Modifier
            .wrapContentHeight()
            .navigationBarsPadding()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            .clickable(enabled = false) { }) {
        AnimatedVisibility(
            visible = viewModel.isSearching || viewModel.isSearchForDiagnosis,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it })
        ) {
            SearchSymptomsAndDiagnosis(viewModel, navController)
        }
    }
    Box(
        Modifier
            .wrapContentHeight()
            .navigationBarsPadding()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            .clickable(enabled = false) { }) {
        AnimatedVisibility(
            visible = viewModel.showSelectSymptomScreen,
            enter = slideInHorizontally(),
            exit = slideOutHorizontally(targetOffsetX = { -it })
        ) {
            AddSymptomsScreen(navController = navController, viewModel = viewModel)
        }
    }

}

@Composable
fun HandleLaunchedEffect(
    viewModel: AddSymptomsAndDiagnosisViewModel,
    navController: NavController,
    snackBarHostState: SnackbarHostState
) {
    LaunchedEffect(key1 = viewModel.isLaunched) {
        viewModel.apply {

            if (!isLaunched) {
                getPreviousSearches()
                patient =
                    navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                        PATIENT
                    )
                local =
                    navController.previousBackStackEntry?.savedStateHandle?.get<SymptomsAndDiagnosisLocal>(
                        SYM_DIAG
                    )

                patient?.let {
                    getStudentTodayAppointment(
                        Date(Date().toTodayStartDate()), Date(Date().toEndOfDay()), patient!!.id
                    )
                    setLocalData()
                    getSymptoms()

                }
            }

            isLaunched = true
        }

    }
    LaunchedEffect(key1 = viewModel.msg) {
        if (viewModel.msg.isNotBlank()) {
            snackBarHostState.showSnackbar(viewModel.msg)
            viewModel.msg = ""
        }
    }
}

@Composable
private fun NoSymptomsCheckBox(viewModel: AddSymptomsAndDiagnosisViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            modifier = Modifier,
            checked = viewModel.isNoSymptomChecked,
            onCheckedChange = { isChecked ->
                viewModel.isNoSymptomChecked = isChecked
                if (viewModel.isNoSymptomChecked) {
                    viewModel.removeSymptoms()
                }
            },
        )
        Column {
            Text(
                text = stringResource(R.string.don_t_have_any_symptoms),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun BottomButtonLayout(
    viewModel: AddSymptomsAndDiagnosisViewModel, navController: NavController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    AnimatedVisibility(visible = viewModel.selectedActiveSymptomsList.isNotEmpty() || viewModel.isNoSymptomChecked) {
        Box(
            modifier = Modifier
                .padding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 1.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .padding(
                                start = 8.dp, bottom = 12.dp, top = 12.dp
                            )
                            .weight(.5f),
                        onClick = {
                            handleNavigate(
                                viewModel,
                                coroutineScope,
                                navController,
                                context
                            ) {

                                it.navigateUp()
                            }
                        },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                    ) {
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(
                            stringResource(id = R.string.save_exit),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Button(
                        modifier = Modifier
                            .padding(
                                end = 12.dp, bottom = 12.dp, top = 12.dp
                            )
                            .weight(.6f), onClick = {
                            viewModel.isSearchForDiagnosis = true
                            viewModel.searchQuery = ""
                            viewModel.isSearchResult = false
                            viewModel.isSearching = true
                            viewModel.isSearchingInProgress = false
                            viewModel.getPreviousSearches()
                            handleNavigate(
                                viewModel,
                                coroutineScope,
                                navController,
                                context
                            ) {}
                        }, contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                    ) {
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(
                            stringResource(id = R.string.save_add_diagnosis),
                            style = MaterialTheme.typography.labelMedium

                        )
                    }


                }
            }


        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarLayout(
    navController: NavController,
    viewModel: AddSymptomsAndDiagnosisViewModel
) {
    TopAppBar(modifier = Modifier.fillMaxWidth(), colors = TopAppBarDefaults.largeTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
    ), actions = {
        IconButton(onClick = {
            navController.previousBackStackEntry?.savedStateHandle?.set(
                SYM_DIAG_UPDATE_OR_ADD,
                ""
            )
            navController.navigateUp()
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

@Preview(showBackground = true)
@Composable
private fun OutlineButtonPreview() {

    Surface {
        OutlinedButton(
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .padding(12.dp)
                .wrapContentWidth(), onClick = {

            }, contentPadding = ButtonDefaults.ButtonWithIconContentPadding
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_icon),
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Select Symptoms")
        }

    }
}



