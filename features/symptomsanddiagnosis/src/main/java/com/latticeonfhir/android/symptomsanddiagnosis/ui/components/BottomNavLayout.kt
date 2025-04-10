package com.latticeonfhir.android.symptomsanddiagnosis.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.latticeonfhir.core.data.server.model.symptomsanddiagnosis.SymptomsAndDiagnosisItem
import com.latticeonfhir.android.symptomsanddiagnosis.R
import com.latticeonfhir.android.symptomsanddiagnosis.ui.addSymptomsanddiagnosis.AddSymptomsAndDiagnosisViewModel
import com.latticeonfhir.core.symptomsanddiagnosis.ui.addSymptomsanddiagnosis.handleNavigate

@Composable
fun BottomNavLayout(viewModel: AddSymptomsAndDiagnosisViewModel, navController: NavController) {
    val rotationState by animateFloatAsState(
        targetValue = if (viewModel.bottomNavExpanded) 180f else 0f,
        label = "Rotation state of expand icon button",
    )
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    AnimatedVisibility(
        visible = true, enter = expandVertically(), exit = shrinkHorizontally()
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
                            IconButton(onClick = {
                                viewModel.bottomNavExpanded = false
                            }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = stringResource(R.string.clear_icon)
                                )
                            }
                            Text(
                                text = stringResource(R.string.bottom_nav_title_diagnosis),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                viewModel.clearAllConfirmDialog = true
                            }) {
                                Text(
                                    text = stringResource(id = R.string.clear_all),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        }
                        HorizontalDivider()
                        LazyColumn(
                            modifier = Modifier.heightIn(0.dp, 450.dp)
                        ) {
                            itemsIndexed(viewModel.selectedActiveDiagnosisList, key = { _, it -> it }) { _, diagnosis ->
                                SwipeToDeleteContainer(item = diagnosis, onDelete = {
                                    viewModel.selectedActiveDiagnosisList.remove(diagnosis)
                                    viewModel.selectedActiveDiagnosisList.ifEmpty {
                                        viewModel.bottomNavExpanded = false
                                    }
                                }) { _, triggerSwipe ->
                                    SelectedCompoundCard(
                                        diagnosis, triggerSwipe
                                    )
                                }

                            }
                        }
                    }
                }
            }
            Surface(
                modifier = Modifier.fillMaxWidth(),
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
                            text = "${viewModel.selectedActiveDiagnosisList.size} ${
                                stringResource(
                                    id = R.string.diagnosis_selected_lable
                                )
                            }",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            handleNavigate(
                                viewModel, coroutineScope, navController, context
                            ) {
                                it.navigateUp()
                            }

                        }, modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(R.string.save_diagnosis))
                    }
                }
            }
        }
    }
}

@Composable
fun SelectedCompoundCard(
    diagnosis: String, triggerSwipe: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {

            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f)
            ) {
                Text(
                    text = diagnosis,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

            }
            IconButton(onClick = {
                triggerSwipe()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.delete_icon),
                    contentDescription = "EDIT_ICON",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}


@Composable
fun BottomSheetLayout(viewModel: AddSymptomsAndDiagnosisViewModel) {
    val list = viewModel.bodyParts.collectAsState()
    val context = LocalContext.current
    AnimatedVisibility(
        visible = viewModel.bodyPartBottomNavExpanded,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column {
            AnimatedVisibility(true) {
                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .height(350.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(bottom = 15.dp)

                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Text(
                                modifier = Modifier.padding(start = 16.dp),
                                text = viewModel.clickedBodyPart,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = {
                                viewModel.bodyPartBottomNavExpanded = false
                            }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = stringResource(R.string.clear_icon)
                                )
                            }
                        }
                        LazyColumn(
                            modifier = Modifier.heightIn(0.dp, 450.dp)
                        ) {
                            itemsIndexed(list.value.filter { it.type == viewModel.clickedBodyPart }
                                .toList()) { _, symptom ->
                                Text(
                                    modifier = Modifier
                                        .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                                        .clickable {
                                            viewModel.apply {
                                                selectedSymptom = SymptomsAndDiagnosisItem(
                                                    code = symptom.code,
                                                    display = symptom.display
                                                )
                                                if (!selectedActiveSymptomsList.contains(
                                                        selectedSymptom
                                                    )
                                                ) {
                                                    selectedActiveSymptomsList.add(selectedSymptom!!)
                                                    viewModel.showSelectSymptomScreen = false
                                                    viewModel.bodyPartBottomNavExpanded = false
                                                } else {
                                                    viewModel.msg =
                                                        context.getString(R.string.already_added)
                                                }
                                                viewModel.isNoSymptomChecked = false

                                                insertRecentSearch()
                                                isSearching = false
                                            }
                                        },
                                    text = symptom.display,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


