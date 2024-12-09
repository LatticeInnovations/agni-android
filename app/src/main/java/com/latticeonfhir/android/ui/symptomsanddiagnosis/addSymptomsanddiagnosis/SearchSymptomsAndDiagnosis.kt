package com.latticeonfhir.android.ui.symptomsanddiagnosis.addSymptomsanddiagnosis

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.symptomsanddiagnosis.SymptomsAndDiagnosisItem
import com.latticeonfhir.android.ui.symptomsanddiagnosis.components.BottomNavLayout
import com.latticeonfhir.android.utils.converters.responseconverter.SymDiagConverter.splitString
import java.util.Locale

@Composable
fun SearchSymptomsAndDiagnosis(
    viewModel: AddSymptomsAndDiagnosisViewModel, navController: NavController
) {
    HandleBackPress(viewModel, navController)
    val focusRequester = remember { FocusRequester() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .clickable {

            }, verticalArrangement = Arrangement.Top
    ) {
        val containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        SymDiagSearchField(viewModel, focusRequester, containerColor)
        if (viewModel.isSearchForDiagnosis) {
            DiagnosisListView(viewModel)
        } else {
            SymptomsView(viewModel)
        }

        ShowDiscardDialog(viewModel)
    }
    AnimatedVisibility(visible = viewModel.isSearchForDiagnosis && (viewModel.selectedActiveDiagnosisList.isNotEmpty() || viewModel.local?.diagnosis?.isNotEmpty() == true)) {
        Box(
            modifier = if (!(viewModel.bottomNavExpanded && viewModel.selectedActiveDiagnosisList.isNotEmpty())) Modifier
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0f))
            else Modifier
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                .clickable(enabled = false) { }, contentAlignment = Alignment.BottomCenter
        ) {
            BottomNavLayout(viewModel, navController)
        }

    }
}

@Composable
fun HandleBackPress(viewModel: AddSymptomsAndDiagnosisViewModel, navController: NavController) {
    BackHandler {
        viewModel.apply {
            if (isSearching) {
                isSearching = false
                state = 0
                if (!isSearchForDiagnosis) {
                    showSelectSymptomScreen = true
                }
                clearSearchDiagnosis()
            } else {
                navController.navigateUp()
            }
        }
    }
}

@Composable
fun ShowDiscardDialog(viewModel: AddSymptomsAndDiagnosisViewModel) {
    if (viewModel.clearAllConfirmDialog) {
        AlertDialog(onDismissRequest = { viewModel.clearAllConfirmDialog = false }, title = {
            Text(
                text = stringResource(id = R.string.discard_diagnosis_dialog_title),
                modifier = Modifier.testTag("DIALOG_TITLE")
            )
        }, text = {
            Text(
                text = stringResource(id = R.string.discard_diagnosis_dialog_description),
                modifier = Modifier.testTag("DIALOG_DESCRIPTION")
            )
        }, confirmButton = {
            TextButton(
                onClick = {
                    viewModel.selectedActiveDiagnosisList = mutableStateListOf()
                    viewModel.bottomNavExpanded = false
                    viewModel.clearAllConfirmDialog = false
                }, modifier = Modifier.testTag("POSITIVE_BTN")
            ) {
                Text(
                    stringResource(id = R.string.yes_discard)
                )
            }
        }, dismissButton = {
            TextButton(
                onClick = {
                    viewModel.clearAllConfirmDialog = false
                }, modifier = Modifier.testTag("NEGATIVE_BTN")
            ) {
                Text(
                    stringResource(id = R.string.no_go_back)
                )
            }
        })
    }

}

@Composable
fun SymDiagSearchField(
    viewModel: AddSymptomsAndDiagnosisViewModel,
    focusRequester: FocusRequester,
    containerColor: Color
) {
    OutlinedTextField(value = viewModel.searchQuery,
        onValueChange = {
            viewModel.searchQuery = it
        },
        placeholder = {
            Text(
                text = if (viewModel.isSearchForDiagnosis) stringResource(R.string.diagnosis_lable) else stringResource(
                    R.string.symptoms_lable
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline
            )
        },
        leadingIcon = {
            IconButton(onClick = {
                viewModel.apply {
                    if (!isSearchForDiagnosis) {
                        showSelectSymptomScreen = true
                    }
                    clearSearchDiagnosis()
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON")
            }
        },
        trailingIcon = {
            if (viewModel.searchQuery.isNotBlank()) {
                IconButton(onClick = {
                    viewModel.searchQuery = ""
                    viewModel.clearSymptomsList()
                    viewModel.clearDiagnosisList()
                    viewModel.isSearchResult = false
                    viewModel.isSearchingInProgress = false
                }) {
                    Icon(Icons.Default.Clear, contentDescription = "CLEAR_ICON")
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onGloballyPositioned {
                focusRequester.requestFocus()
            }
            .testTag("SEARCH_TEXT_FIELD"),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            disabledContainerColor = containerColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search, keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(onSearch = {
            handleOnSearch(viewModel)
        }),
        singleLine = true
    )


}

fun handleOnSearch(viewModel: AddSymptomsAndDiagnosisViewModel) {
    if (viewModel.searchQuery.isNotBlank()) {
        viewModel.isSearchResult = true
        if (viewModel.isSearchForDiagnosis) {
            viewModel.isSearchingInProgress=true
            viewModel.searchDiagnosisByQuery()
        } else {
            viewModel.isSearchingInProgress=true
            viewModel.searchSymptomsByQuery()
        }
    } else {
        viewModel.isSearching = false
        viewModel.showSelectSymptomScreen = false
        viewModel.searchQuery = ""
    }
}

@Composable
fun SymptomsView(viewModel: AddSymptomsAndDiagnosisViewModel) {
    val list = viewModel.symptomsFlow.collectAsState()
    LazyColumn(modifier = Modifier.testTag("PREVIOUS_SEARCHES")) {
        item {
            AnimatedVisibility(visible = viewModel.isSearchingInProgress) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(modifier = Modifier.height(30.dp))
                }
            }
        }
        item {
            if (!viewModel.isSearchingInProgress && viewModel.searchQuery.isNotBlank() && viewModel.isSearchResult && list.value.isEmpty()) Text(
                text = stringResource(R.string.no_matches_found),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.outline
            )
        }
        itemsIndexed(list.value) { index, symptom ->
            if (viewModel.searchQuery.isNotBlank() && viewModel.isSearching) PreviousSearches(
                index = index, symptom = symptom, viewModel = viewModel
            )
        }
    }

}

@Composable
fun PreviousSearches(index: Int, symptom: String, viewModel: AddSymptomsAndDiagnosisViewModel) {
    viewModel.isSearchingInProgress=false
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() }, indication = null
            ) {
                viewModel.apply {
                    selectedSymptom = SymptomsAndDiagnosisItem(
                        code = symptomsFlow.value[index],
                        display = symptomsFlow.value[index]
                    )
                    if (!selectedActiveSymptomsList.contains(selectedSymptom)) {
                        selectedActiveSymptomsList.add(selectedSymptom!!)
                        viewModel.showSelectSymptomScreen = false
                    } else {
                        viewModel.msg = context.getString(R.string.already_added)
                        if (!isSearchForDiagnosis) {
                            showSelectSymptomScreen = true
                        }
                    }
                    viewModel.isNoSymptomChecked = false

                    insertRecentSearch()
                    clearSearchDiagnosis()
                    isSearching = false
                }
            }, verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = symptom,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DiagnosisListView(viewModel: AddSymptomsAndDiagnosisViewModel) {
    val list = viewModel.diagnosisFlow.collectAsState()
    val mostRecentList = viewModel.mostRecentDiagnosisFlow.collectAsState()

    if (!viewModel.isSearchingInProgress && viewModel.searchQuery.isNotBlank() && viewModel.isSearchResult && list.value.isEmpty()) Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.no_matches_found),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.outline
        )

    }
    AnimatedVisibility(visible = viewModel.isSearchingInProgress) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {

                CircularProgressIndicator(modifier = Modifier.height(30.dp))
            }
        }


    AnimatedVisibility(visible = !viewModel.isSearchingInProgress && (list.value.isNotEmpty() || mostRecentList.value.isNotEmpty())) {
        Column(modifier = Modifier.wrapContentHeight()) {
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                text = if (viewModel.isSearchForDiagnosis && viewModel.isSearchResult) "${list.value.size} results" else "Frequently diagnosed",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            LazyColumn {
                itemsIndexed(list.value.ifEmpty { mostRecentList.value }) { index, diagnosis ->
                    CompoundRow(index, activeDiagnosis = diagnosis, viewModel)
                }
            }
        }
    }
}

@Composable
fun CompoundRow(index: Int, activeDiagnosis: String, viewModel: AddSymptomsAndDiagnosisViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = viewModel.selectedActiveDiagnosisList.contains(activeDiagnosis),
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    viewModel.selectedDiagnosis =
                        if (viewModel.diagnosisFlow.value.isNotEmpty()) SymptomsAndDiagnosisItem(
                            code = viewModel.diagnosisFlow.value[index].splitString().first,
                            display = viewModel.diagnosisFlow.value[index].splitString().second
                        ) else SymptomsAndDiagnosisItem(
                            code = viewModel.mostRecentDiagnosisFlow.value[index].splitString().first,
                            display = viewModel.mostRecentDiagnosisFlow.value[index].splitString().second
                        )
                    viewModel.insertRecentSearch()
                    viewModel.selectedActiveDiagnosisList.add(activeDiagnosis)
                } else {
                    viewModel.selectedActiveDiagnosisList.remove(activeDiagnosis)
                }
            },
        )
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = activeDiagnosis.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}









