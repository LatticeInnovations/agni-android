package com.latticeonfhir.android.ui.appointments.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import androidx.lifecycle.viewmodel.compose.*
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.common.NonLazyGrid
import com.latticeonfhir.android.ui.theme.Green
import com.latticeonfhir.android.utils.constants.NavControllerConstants.SCHEDULED
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toMonth
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toOneYearFuture
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toSlotDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toCurrentTimeInMillis
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toWeekDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toWeekList
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toYear
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.tomorrow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    "patient"
                )
        }
        viewModel.isLaunched = true
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
                        text = stringResource(id = R.string.schedule_appointment),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("HEADING_TAG"),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "BACK_ICON")
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = {
                            composableScope.launch {
                                dateScrollState.animateScrollToItem(0)
                            }
                            viewModel.selectedDate = Date().tomorrow()
                            viewModel.weekList = viewModel.selectedDate.toWeekList()
                        },
                        enabled = viewModel.selectedDate.toSlotDate() != Date().tomorrow()
                            .toSlotDate(),
                        modifier = Modifier.testTag("RESET_BTN")
                    ) {
                        Text(text = stringResource(id = R.string.reset))
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 15.dp, bottom = 15.dp)
                        .wrapContentSize()
                ) {
                    Row(
                        modifier = Modifier
                            .testTag("DATE_DROPDOWN")
                            .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ){
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
                    LazyRow(
                        state = dateScrollState,
                        modifier = Modifier.testTag("DAYS_TAB_ROW")
                    ) {
                        items(viewModel.weekList) { date ->
                            SuggestionChip(
                                onClick = {
                                    viewModel.selectedDate = date
                                    viewModel.selectedSlot = ""
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
                                    .padding(horizontal = 5.dp)
                                    .testTag("DAYS_CHIP"),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (viewModel.selectedDate == date) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surface,
                                    labelColor = if (viewModel.selectedDate == date) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.outline
                                ),
                                border = SuggestionChipDefaults.suggestionChipBorder(
                                    borderColor = Color.Transparent
                                ),
                                enabled = date < Date().toOneYearFuture()
                            )
                        }
                    }
                }
                Divider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
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
                            mutableStateOf(0)
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
                            mutableStateOf(0)
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
                            mutableStateOf(0)
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
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = viewModel.selectedDate.time
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
                        dateValidator = { date ->
                            date >= Date().tomorrow()
                                .toTodayStartDate() && date <= Date().toOneYearFuture().time
                        },
                        modifier = Modifier.testTag("DATE_PICKER_DIALOG")
                    )
                }
            }
        },
        floatingActionButton = {
            if (viewModel.selectedSlot.isNotBlank()) {
                Button(
                    onClick = {
                        viewModel.insertScheduleAndAppointment {
                            CoroutineScope(Dispatchers.Main).launch {
                                navController.popBackStack(Screen.PatientLandingScreen.route, false)
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    SCHEDULED,
                                    true
                                )
                                navController.navigate(Screen.Appointments.route)
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
        modifier = Modifier.padding(top = 12.dp, start = 17.dp, bottom = 18.dp)
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
    index: Int, slotTimings: Array<String>, slots: Int, selectedSlot: String,
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
                    text = if (slots < 1) "1 slot" else "0 slot",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (slots > 0) MaterialTheme.colorScheme.outline
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
            labelColor = if (slots > 0) MaterialTheme.colorScheme.outline
            else MaterialTheme.colorScheme.primary
        ),
        border = SuggestionChipDefaults.suggestionChipBorder(
            borderColor = if (slots > 0) MaterialTheme.colorScheme.outline
            else MaterialTheme.colorScheme.primary
        ),
        enabled = slots < 1
    )
}