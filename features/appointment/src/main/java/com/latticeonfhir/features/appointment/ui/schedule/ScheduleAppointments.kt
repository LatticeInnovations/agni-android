package com.latticeonfhir.features.appointment.ui.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.core.model.local.appointment.AppointmentResponseLocal
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.core.theme.Green
import com.latticeonfhir.core.ui.NonLazyGrid
import com.latticeonfhir.core.ui.WeekDaysComposable
import com.latticeonfhir.core.utils.constants.NavControllerConstants
import com.latticeonfhir.core.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.core.utils.constants.NavControllerConstants.SCHEDULED
import com.latticeonfhir.core.utils.converters.TimeConverter.toAppointmentDate
import com.latticeonfhir.core.utils.converters.TimeConverter.toCurrentTimeInMillis
import com.latticeonfhir.core.utils.converters.TimeConverter.toOneYearFuture
import com.latticeonfhir.core.utils.converters.TimeConverter.toSlotDate
import com.latticeonfhir.core.utils.converters.TimeConverter.toTodayStartDate
import com.latticeonfhir.core.utils.converters.TimeConverter.toWeekList
import com.latticeonfhir.core.utils.converters.TimeConverter.tomorrow
import com.latticeonfhir.features.appointment.R
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleAppointments(
    navController: NavController,
    viewModel: ScheduleAppointmentViewModel = hiltViewModel()
) {
    val dateScrollState = rememberLazyListState()
    val composableScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.ifRescheduling =
                navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>(
                    NavControllerConstants.IF_RESCHEDULING
                ) == true
            if (viewModel.ifRescheduling) {
                viewModel.appointment =
                    navController.previousBackStackEntry?.savedStateHandle?.get<AppointmentResponseLocal>(
                        NavControllerConstants.APPOINTMENT_SELECTED
                    )
            }
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    PATIENT
                )
        }
        viewModel.isLaunched = true
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                title = {
                    Text(
                        text = if (viewModel.ifRescheduling) stringResource(id = R.string.reschedule_appointment) else stringResource(
                            id = R.string.schedule_appointment
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("HEADING_TAG"),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON")
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = {
                            composableScope.launch {
                                dateScrollState.animateScrollToItem(0)
                            }
                            viewModel.selectedDate = Date().tomorrow()
                            viewModel.selectedSlot = ""
                            viewModel.weekList = viewModel.selectedDate.toWeekList()
                        },
                        enabled = viewModel.selectedDate.toSlotDate() != Date().tomorrow()
                            .toSlotDate(),
                        modifier = Modifier.testTag("RESET_BTN")
                    ) {
                        Text(text = stringResource(id = R.string.today))
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
            ) {
                if (viewModel.ifRescheduling) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.current_appointment),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = viewModel.appointment?.slot?.start?.toAppointmentDate()
                                    ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                WeekDaysComposable(
                    dateScrollState,
                    viewModel.selectedDate,
                    viewModel.weekList
                ) { showDialog, date ->
                    if (showDialog == true) viewModel.showDatePicker = true
                    else {
                        if (date != null) {
                            viewModel.selectedDate = date
                        }
                        viewModel.selectedSlot = ""
                    }
                }
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    SlotsHeading(
                        R.drawable.sunny_snowing_icon,
                        stringResource(id = R.string.morning_slots),
                        "MORNING_ICON"
                    )
                    NonLazyGrid(
                        columns = 3,
                        itemCount = stringArrayResource(id = R.array.morning_slot_timings).size,
                        modifier = Modifier
                            .padding(horizontal = 17.dp)
                    ) { index ->
                        var slots by remember {
                            mutableIntStateOf(0)
                        }
                        LaunchedEffect(viewModel.selectedDate) {
                            viewModel.getBookedSlotsCount(
                                context.resources.getStringArray(R.array.morning_slot_timings)[index].toCurrentTimeInMillis(
                                    viewModel.selectedDate
                                )
                            ) { slotsCount ->
                                slots = slotsCount
                            }
                        }
                        SlotChips(
                            index,
                            stringArrayResource(id = R.array.morning_slot_timings),
                            slots,
                            viewModel.maxNumberOfSlots,
                            viewModel.selectedSlot,
                            "MORNING_SLOT_CHIPS"
                        ) { slot ->
                            if (viewModel.selectedSlot == slot) viewModel.selectedSlot = ""
                            else viewModel.selectedSlot = slot
                        }
                    }
                    SlotsHeading(
                        R.drawable.light_mode_icon,
                        stringResource(id = R.string.afternoon_slots),
                        "AFTERNOON_ICON"
                    )
                    NonLazyGrid(
                        columns = 3,
                        itemCount = stringArrayResource(id = R.array.afternoon_slot_timings).size,
                        modifier = Modifier
                            .padding(horizontal = 17.dp)
                    ) { index ->
                        var slots by remember {
                            mutableIntStateOf(0)
                        }
                        LaunchedEffect(viewModel.selectedDate) {
                            viewModel.getBookedSlotsCount(
                                context.resources.getStringArray(R.array.afternoon_slot_timings)[index].toCurrentTimeInMillis(
                                    viewModel.selectedDate
                                )
                            ) { slotsCount ->
                                slots = slotsCount
                            }
                        }
                        SlotChips(
                            index,
                            stringArrayResource(id = R.array.afternoon_slot_timings),
                            slots,
                            viewModel.maxNumberOfSlots,
                            viewModel.selectedSlot,
                            "AFTERNOON_SLOT_CHIPS"
                        ) { slot ->
                            if (viewModel.selectedSlot == slot) viewModel.selectedSlot = ""
                            else viewModel.selectedSlot = slot
                        }
                    }
                    SlotsHeading(
                        R.drawable.clear_night_icon,
                        stringResource(id = R.string.evening_slots),
                        "EVENING_ICON"
                    )
                    NonLazyGrid(
                        columns = 3,
                        itemCount = stringArrayResource(id = R.array.evening_slot_timings).size,
                        modifier = Modifier
                            .padding(horizontal = 17.dp)
                    ) { index ->
                        var slots by remember {
                            mutableIntStateOf(0)
                        }
                        LaunchedEffect(viewModel.selectedDate) {
                            viewModel.getBookedSlotsCount(
                                context.resources.getStringArray(R.array.evening_slot_timings)[index].toCurrentTimeInMillis(
                                    viewModel.selectedDate
                                )
                            ) { slotsCount ->
                                slots = slotsCount
                            }
                        }
                        SlotChips(
                            index,
                            stringArrayResource(id = R.array.evening_slot_timings),
                            slots,
                            viewModel.maxNumberOfSlots,
                            viewModel.selectedSlot,
                            "EVENING_SLOT_CHIPS"
                        ) { slot ->
                            if (viewModel.selectedSlot == slot) viewModel.selectedSlot = ""
                            else viewModel.selectedSlot = slot
                        }
                    }
                    if (viewModel.selectedSlot.isNotBlank()) Spacer(modifier = Modifier.height(60.dp))
                }
            }
            if (viewModel.showDatePicker) {
                val selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis >= Date().tomorrow()
                            .toTodayStartDate() && utcTimeMillis <= Date().toOneYearFuture().time
                    }
                }
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = viewModel.selectedDate.time,
                    selectableDates = selectableDates
                )
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
                                composableScope.launch {
                                    dateScrollState.scrollToItem(0)
                                }
                                viewModel.showDatePicker = false
                                viewModel.selectedSlot = ""
                                viewModel.selectedDate =
                                    datePickerState.selectedDateMillis?.let { dateInLong ->
                                        Date(
                                            dateInLong
                                        )
                                    } ?: Date()
                                viewModel.weekList = viewModel.selectedDate.toWeekList()
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
                        modifier = Modifier.testTag("DATE_PICKER_DIALOG")
                    )
                }
            }
        },
        floatingActionButton = {
            if (viewModel.selectedSlot.isNotBlank()) {
                Button(
                    onClick = {
                        if (viewModel.ifRescheduling) {
                            viewModel.ifAnotherAppointmentExists { alreadyExists ->
                                if (alreadyExists) {
                                    composableScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = context.getString(
                                                R.string.appointment_exists
                                            )
                                        )
                                    }
                                } else {
                                    viewModel.rescheduleAppointment {
                                        composableScope.launch {
                                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                                NavControllerConstants.RESCHEDULED,
                                                true
                                            )
                                            navController.popBackStack()
                                        }
                                    }
                                }
                            }
                        } else {
                            viewModel.insertScheduleAndAppointment { appointmentCreated ->
                                if (appointmentCreated == false) {
                                    composableScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = context.getString(R.string.appointment_exists)
                                        )
                                    }
                                } else {
                                    composableScope.launch {
                                        navController.popBackStack(
                                            Screen.PatientLandingScreen.route,
                                            false
                                        )
                                        navController.currentBackStackEntry?.savedStateHandle?.set(
                                            SCHEDULED,
                                            true
                                        )
                                        navController.navigate(Screen.Appointments.route)
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp)
                        .testTag("CONFIRM_APPOINTMENT_BTN")
                ) {
                    Text(
                        text = stringResource(id = R.string.confirm_appointment),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    )
}

@Composable
fun SlotsHeading(icon: Int, heading: String, testTag: String) {
    Row(
        modifier = Modifier
            .padding(top = 12.dp, start = 17.dp, bottom = 18.dp)
            .testTag(heading),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = testTag,
            Modifier
                .size(30.dp)
                .padding(end = 10.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = heading,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SlotChips(
    index: Int,
    slotTimings: Array<String>,
    bookedSlots: Int,
    maxNumberOfSlots: Int,
    selectedSlot: String,
    testTag: String,
    updateSlot: (String) -> Unit
) {
    SuggestionChip(
        onClick = {
            updateSlot(slotTimings[index])
        },
        label = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = slotTimings[index],
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = if (bookedSlots < maxNumberOfSlots) "${maxNumberOfSlots - bookedSlots} slot" else "0 slot",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (maxNumberOfSlots - bookedSlots <= 0) MaterialTheme.colorScheme.outline
                    else Green
                )
            }
        },
        modifier = Modifier
            .padding(bottom = 17.dp)
            .fillMaxWidth()
            .testTag(testTag),
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = if (slotTimings[index] == selectedSlot) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface,
            labelColor = if (maxNumberOfSlots - bookedSlots <= 0) MaterialTheme.colorScheme.outline
            else MaterialTheme.colorScheme.primary
        ),
        border = SuggestionChipDefaults.suggestionChipBorder(
            enabled = true,
            borderColor = if (maxNumberOfSlots - bookedSlots <= 0) MaterialTheme.colorScheme.outline
            else MaterialTheme.colorScheme.primary
        ),
        enabled = bookedSlots < maxNumberOfSlots
    )
}