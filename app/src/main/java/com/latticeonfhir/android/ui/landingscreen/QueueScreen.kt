@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.latticeonfhir.android.ui.landingscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.theme.ArrivedContainer
import com.latticeonfhir.android.ui.theme.ArrivedLabel
import com.latticeonfhir.android.ui.theme.CancelledContainer
import com.latticeonfhir.android.ui.theme.CancelledLabel
import com.latticeonfhir.android.ui.theme.CompletedContainer
import com.latticeonfhir.android.ui.theme.CompletedLabel
import com.latticeonfhir.android.ui.theme.InProgressContainer
import com.latticeonfhir.android.ui.theme.InProgressLabel
import com.latticeonfhir.android.ui.theme.Neutral90
import com.latticeonfhir.android.ui.theme.NeutralVariant95
import com.latticeonfhir.android.ui.theme.NoShowContainer
import com.latticeonfhir.android.ui.theme.NoShowLabel
import com.latticeonfhir.android.ui.theme.TodayScheduledContainer
import com.latticeonfhir.android.ui.theme.TodayScheduledLabel
import com.latticeonfhir.android.ui.theme.WalkInContainer
import com.latticeonfhir.android.ui.theme.WalkInLabel
import com.latticeonfhir.android.utils.constants.NavControllerConstants
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to14DaysWeek
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toMonth
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toOneYearFuture
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toOneYearPast
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toSlotDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toWeekDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toYear
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun QueueScreen(
    navController: NavController,
    viewModel: LandingScreenViewModel,
    dateScrollState: ScrollState,
    coroutineScope: CoroutineScope
) {
    val verticalScrollState = rememberScrollState()
    LaunchedEffect(true) {
        dateScrollState.scrollTo(dateScrollState.maxValue / 2 + 30)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AnimatedVisibility(verticalScrollState.value == 0) {
            Column {
                Row(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 15.dp, bottom = 15.dp)
                        .wrapContentSize()
                ) {
                    Row(
                        modifier = Modifier.clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) {
                            viewModel.showDatePicker = true
                        }
                    ) {
                        Column {
                            Text(
                                text = viewModel.selectedDate.toMonth(),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = viewModel.selectedDate.toYear(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "DROP_DOWN_ICON")
                    }
                    Divider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .width(1.dp)
                            .height(55.dp)
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(dateScrollState)
                    ) {
                        viewModel.weekList.forEach { date ->
                            SuggestionChip(
                                onClick = {
                                    viewModel.selectedDate = date
                                },
                                label = {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = date.toWeekDay(),
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                        Text(
                                            text = date.toSlotDate(),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .padding(horizontal = 5.dp),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (viewModel.selectedDate == date) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surface,
                                    labelColor = if (viewModel.selectedDate == date) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.outline
                                ),
                                border = SuggestionChipDefaults.suggestionChipBorder(
                                    borderColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
                Divider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
        if (viewModel.appointmentsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(color = NeutralVariant95, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.no_appointment),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 17.dp, start = 17.dp, end = 17.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // total chip
                    InputChip(
                        selected = true,
                        onClick = { },
                        label = {
                            Text(text = stringResource(id = R.string.total_appointment, 10))
                        },
                        colors = InputChipDefaults.inputChipColors(
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        border = InputChipDefaults.inputChipBorder(
                            selectedBorderColor = MaterialTheme.colorScheme.primary,
                            selectedBorderWidth = 1.dp
                        )
                    )
                    AppointmentStatusChips(R.string.waiting_appointment, 4)
                    AppointmentStatusChips(R.string.in_progress_appointment, 2)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AppointmentStatusChips(R.string.scheduled_appointment, 2)
                    AppointmentStatusChips(R.string.cancelled_appointment, 2)
                    AppointmentStatusChips(R.string.completed_appointment, 2)
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(verticalScrollState)
            ) {
                Surface(
                    color = Neutral90,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(18.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.waiting),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.outline
                        )
                        QueuePatientCard(
                            navController,
                            viewModel,
                            stringResource(id = R.string.walk_in)
                        )
                        QueuePatientCard(
                            navController,
                            viewModel,
                            stringResource(id = R.string.arrived)
                        )
                    }
                }
                // in progress
                Column(
                    modifier = Modifier
                        .padding(18.dp)
                ) {
                    QueuePatientCard(
                        navController,
                        viewModel,
                        stringResource(id = R.string.in_progress_heading)
                    )
                }
                // scheduled
                Column(modifier = Modifier.padding(18.dp)) {
                    QueuePatientCard(
                        navController,
                        viewModel,
                        stringResource(id = R.string.scheduled)
                    )
                }

                // completed
                Column(modifier = Modifier.padding(18.dp)) {
                    QueuePatientCard(
                        navController,
                        viewModel,
                        stringResource(id = R.string.completed)
                    )
                }

                // cancelled
                Column(modifier = Modifier.padding(18.dp)) {
                    CancelledQueueCard(
                        stringResource(id = R.string.cancelled)
                    )
                }
            }
        }
    }
//    if (viewModel.showCancelAppointmentDialog) {
//        CancelAppointmentDialog(
//            patient = patient,
//            dateAndTime = "12 Jun, 2023 · 11:00 AM"
//        ) {
//            viewModel.showCancelAppointmentDialog = it
//        }
//    }
    if (viewModel.showDatePicker) {
        val datePickerState = rememberDatePickerState()
        datePickerState.setSelection(viewModel.selectedDate.time)
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }
        DatePickerDialog(
            onDismissRequest = {
                viewModel.showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.showDatePicker = false
                        viewModel.selectedDate =
                            datePickerState.selectedDateMillis?.let { dateInLong ->
                                Date(
                                    dateInLong
                                )
                            } ?: Date()
                        viewModel.weekList = viewModel.selectedDate.to14DaysWeek()
                        coroutineScope.launch {
                            dateScrollState.scrollTo(dateScrollState.maxValue / 2 + 30)
                        }
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.showDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                dateValidator = { date ->
                    date >= Date().toTodayStartDate()
                        .toOneYearPast().time && date <= Date().toOneYearFuture().time
                }
            )
        }
    }
}

@Composable
fun AppointmentStatusChips(label: Int, count: Int) {
    FilterChip(
        selected = true,
        onClick = { /*TODO*/ },
        label = {
            Text(text = stringResource(id = label, count))
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.onPrimary,
            selectedLabelColor = MaterialTheme.colorScheme.outline
        ),
        border = FilterChipDefaults.filterChipBorder(
            selectedBorderWidth = 1.dp,
            selectedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
fun QueuePatientCard(
    navController: NavController,
    viewModel: LandingScreenViewModel,
    label: String
) {
    ElevatedCard(
        modifier = Modifier.padding(top = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 4.dp,
                    end = 24.dp,
                    bottom = 10.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                AssistChip(
                    onClick = {
                        viewModel.showStatusChangeLayout = true
                    },
                    label = {
                        Text(text = label)
                    },
                    trailingIcon = {
                        if (!(label == stringResource(id = R.string.cancelled) || label == stringResource(
                                id = R.string.completed
                            ))
                        ) {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "DROP_DOWN_ICON",
                                tint = when (label) {
                                    stringResource(id = R.string.walk_in) -> WalkInLabel
                                    stringResource(id = R.string.arrived) -> ArrivedLabel
                                    stringResource(id = R.string.scheduled) -> TodayScheduledLabel
                                    stringResource(id = R.string.cancelled) -> CancelledLabel
                                    stringResource(id = R.string.completed) -> CompletedLabel
                                    stringResource(id = R.string.in_progress_heading) -> InProgressLabel
                                    else -> NoShowLabel
                                }
                            )
                        }
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (label) {
                            stringResource(id = R.string.walk_in) -> WalkInContainer
                            stringResource(id = R.string.arrived) -> ArrivedContainer
                            stringResource(id = R.string.scheduled) -> TodayScheduledContainer
                            stringResource(id = R.string.cancelled) -> CancelledContainer
                            stringResource(id = R.string.completed) -> CompletedContainer
                            stringResource(id = R.string.in_progress_heading) -> InProgressContainer
                            else -> NoShowContainer
                        },
                        labelColor = when (label) {
                            stringResource(id = R.string.walk_in) -> WalkInLabel
                            stringResource(id = R.string.arrived) -> ArrivedLabel
                            stringResource(id = R.string.scheduled) -> TodayScheduledLabel
                            stringResource(id = R.string.cancelled) -> CancelledLabel
                            stringResource(id = R.string.completed) -> CompletedLabel
                            stringResource(id = R.string.in_progress_heading) -> InProgressLabel
                            else -> NoShowLabel
                        }
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        borderColor = when (label) {
                            stringResource(id = R.string.walk_in) -> WalkInLabel
                            stringResource(id = R.string.arrived) -> ArrivedLabel
                            stringResource(id = R.string.scheduled) -> TodayScheduledLabel
                            stringResource(id = R.string.cancelled) -> CancelledLabel
                            stringResource(id = R.string.completed) -> CompletedLabel
                            stringResource(id = R.string.in_progress_heading) -> InProgressLabel
                            else -> NoShowLabel
                        }
                    )
                )
                Text(
                    text = "Vikram Kumar Pandey",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "M/55, PID: 374938272929",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (label == stringResource(id = R.string.walk_in) || label == stringResource(id = R.string.arrived)) {
                Icon(
                    painter = painterResource(id = R.drawable.drag_handle_icon),
                    contentDescription = "DRAG_HANDLE",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        if (
            label == stringResource(id = R.string.walk_in)
            || label == stringResource(id = R.string.arrived)
            || label == stringResource(id = R.string.scheduled)
        ) {
            Divider(
                thickness = 1.dp,
                color = Neutral90,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            if (
                label == stringResource(id = R.string.walk_in)
                || label == stringResource(id = R.string.arrived)
                || label == stringResource(id = R.string.scheduled)
            ) {
                TextButton(
                    onClick = {
                        viewModel.showCancelAppointmentDialog = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
            if (label == stringResource(id = R.string.scheduled)) {
                Surface(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    TextButton(
                        onClick = {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                NavControllerConstants.APPOINTMENT_DATE_AND_TIME,
                                "12 Jun, 2023 · 11:00 AM"
                            )
                            navController.navigate(Screen.RescheduleAppointments.route)
                        }
                    ) {
                        Text(text = stringResource(id = R.string.reschedule))
                    }
                }
            }
        }
    }
}

@Composable
fun CancelledQueueCard(label: String) {
    Card(
        modifier = Modifier.padding(top = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 4.dp,
                    end = 24.dp,
                    bottom = 10.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                AssistChip(
                    onClick = { /*TODO*/ },
                    label = {
                        Text(text = label)
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = CancelledContainer,
                        labelColor = CancelledLabel
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        borderColor = CancelledLabel
                    )
                )
                Text(
                    text = "Vikram Kumar Pandey",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "M/55, PID: 374938272929",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}