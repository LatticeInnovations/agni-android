@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.latticeonfhir.android.ui.landingscreen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum.Companion.fromValue
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.ui.appointments.CancelAppointmentDialog
import com.latticeonfhir.android.ui.theme.ArrivedContainer
import com.latticeonfhir.android.ui.theme.ArrivedLabel
import com.latticeonfhir.android.ui.theme.CancelledContainer
import com.latticeonfhir.android.ui.theme.CancelledLabel
import com.latticeonfhir.android.ui.theme.CompletedContainer
import com.latticeonfhir.android.ui.theme.CompletedLabel
import com.latticeonfhir.android.ui.theme.InProgressContainer
import com.latticeonfhir.android.ui.theme.InProgressLabel
import com.latticeonfhir.android.ui.theme.NoShowContainer
import com.latticeonfhir.android.ui.theme.NoShowLabel
import com.latticeonfhir.android.ui.theme.TodayScheduledContainer
import com.latticeonfhir.android.ui.theme.TodayScheduledLabel
import com.latticeonfhir.android.ui.theme.WalkInContainer
import com.latticeonfhir.android.ui.theme.WalkInLabel
import com.latticeonfhir.android.utils.constants.NavControllerConstants
import com.latticeonfhir.android.utils.converters.responseconverter.NameConverter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to14DaysWeek
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAppointmentDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAppointmentTime
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toMonth
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toOneYearFuture
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toOneYearPast
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toSlotDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toWeekDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toYear
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import timber.log.Timber
import java.util.Date

@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
fun QueueScreen(
    navController: NavController,
    landingViewModel: LandingScreenViewModel,
    dateScrollState: LazyListState,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    viewModel: QueueViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val queueListState = rememberReorderableLazyListState(onMove = { from, to ->
        if (to.index > 0 && to.index <= viewModel.waitingQueueList.size && from.index > 0 && from.index <= viewModel.waitingQueueList.size) {
            viewModel.waitingQueueList = viewModel.waitingQueueList.toMutableList().apply {
                add(to.index - 1, removeAt(from.index - 1))
            }
        }
    })
    viewModel.rescheduled = navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>(
        NavControllerConstants.RESCHEDULED
    ) == true
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            dateScrollState.scrollToItem(7, scrollOffset = -130)
        }
        viewModel.isLaunched = true
    }
    LaunchedEffect(true) {
        viewModel.getAppointmentListByDate()
        if (viewModel.rescheduled) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.appointment_rescheduled)
                )
            }
            navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>(
                NavControllerConstants.RESCHEDULED
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AnimatedVisibility(queueListState.listState.firstVisibleItemScrollOffset == 0 && queueListState.listState.firstVisibleItemIndex == 0) {
            Column {
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
                    LazyRow(
                        state = dateScrollState,
                        modifier = Modifier.testTag("DAYS_TAB_ROW")
                    ) {
                        items(viewModel.weekList) { date ->
                            SuggestionChip(
                                onClick = {
                                    viewModel.selectedDate = date
                                    viewModel.selectedChip = R.string.total_appointment
                                    viewModel.getAppointmentListByDate()
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
                                enabled = date < Date().toOneYearFuture() && date > Date().toTodayStartDate()
                                    .toOneYearPast()
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
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ),
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
                    AppointmentStatusChips(
                        R.string.total_appointment,
                        viewModel.appointmentsList.size,
                        queueListState.listState,
                        coroutineScope,
                        0,
                        viewModel
                    )
                    if (viewModel.selectedDate.toSlotDate() == Date().toSlotDate())
                        AppointmentStatusChips(
                            R.string.waiting_appointment,
                            viewModel.waitingQueueList.size,
                            queueListState.listState,
                            coroutineScope,
                            0,
                            viewModel
                        )
                    if (viewModel.selectedDate.toSlotDate() == Date().toSlotDate())
                        AppointmentStatusChips(
                            R.string.in_progress_appointment,
                            viewModel.inProgressQueueList.size,
                            queueListState.listState,
                            coroutineScope,
                            viewModel.waitingQueueList.size + if (viewModel.waitingQueueList.isNotEmpty()) 2 else 0,
                            viewModel
                        )
                }
                if (viewModel.selectedDate.toSlotDate() == Date().toSlotDate())
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AppointmentStatusChips(
                            R.string.scheduled_appointment,
                            viewModel.scheduledQueueList.size,
                            queueListState.listState,
                            coroutineScope,
                            viewModel.waitingQueueList.size + viewModel.inProgressQueueList.size + if (viewModel.waitingQueueList.isNotEmpty()) 2 else 0,
                            viewModel
                        )
                        AppointmentStatusChips(
                            R.string.completed_appointment,
                            viewModel.completedQueueList.size,
                            queueListState.listState,
                            coroutineScope,
                            viewModel.waitingQueueList.size + viewModel.inProgressQueueList.size + viewModel.scheduledQueueList.size + if (viewModel.waitingQueueList.isNotEmpty()) 2 else 0,
                            viewModel
                        )
                        AppointmentStatusChips(
                            R.string.cancelled_appointment,
                            viewModel.cancelledQueueList.size,
                            queueListState.listState,
                            coroutineScope,
                            viewModel.waitingQueueList.size + viewModel.inProgressQueueList.size + viewModel.scheduledQueueList.size + viewModel.completedQueueList.size + if (viewModel.waitingQueueList.isNotEmpty()) 2 else 0,
                            viewModel
                        )
                    }
            }
            LazyColumn(
                state = queueListState.listState,
                modifier = Modifier
                    .reorderable(queueListState),
                content = {
                    if (viewModel.waitingQueueList.isNotEmpty())
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                shape = RoundedCornerShape(topEnd = 18.dp, topStart = 18.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(
                                            top = 18.dp,
                                            start = 20.dp,
                                            end = 18.dp
                                        )
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.waiting),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    items(viewModel.waitingQueueList, { it }) { waitingAppointmentResponse ->
                        ReorderableItem(
                            queueListState, key = waitingAppointmentResponse,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                        ) { isDragging ->
                            //val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
                            var patient by remember {
                                mutableStateOf<PatientResponse?>(null)
                            }
                            waitingAppointmentResponse.patientId.let {
                                coroutineScope.launch {
                                    patient = viewModel.getPatientById(
                                        it
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    //.shadow(elevation.value)
                                    .padding(horizontal = 18.dp, vertical = 9.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                QueuePatientCard(
                                    navController,
                                    viewModel,
                                    landingViewModel,
                                    queueListState,
                                    waitingAppointmentResponse,
                                    patient
                                )
                            }
                        }
                    }
                    if (viewModel.waitingQueueList.isNotEmpty())
                        item {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(
                                            bottomEnd = 18.dp,
                                            bottomStart = 18.dp
                                        )
                                    )
                            )
                        }
                    // in-progress
                    items(viewModel.inProgressQueueList) { appointmentResponseLocal ->
                        var patient by remember {
                            mutableStateOf<PatientResponse?>(null)
                        }
                        appointmentResponseLocal.patientId.let {
                            coroutineScope.launch {
                                patient = viewModel.getPatientById(
                                    it
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        ) {
                            QueuePatientCard(
                                navController,
                                viewModel,
                                landingViewModel,
                                queueListState,
                                appointmentResponseLocal,
                                patient
                            )
                        }
                    }
                    // scheduled
                    items(viewModel.scheduledQueueList) { appointmentResponseLocal ->
                        var patient by remember {
                            mutableStateOf<PatientResponse?>(null)
                        }
                        appointmentResponseLocal.patientId.let {
                            coroutineScope.launch {
                                patient = viewModel.getPatientById(
                                    it
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.padding(
                                top = 16.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                        ) {
                            QueuePatientCard(
                                navController,
                                viewModel,
                                landingViewModel,
                                queueListState,
                                appointmentResponseLocal,
                                patient
                            )
                        }
                    }
                    // completed
                    items(viewModel.completedQueueList) { appointmentResponseLocal ->
                        var patient by remember {
                            mutableStateOf<PatientResponse?>(null)
                        }
                        appointmentResponseLocal.patientId.let {
                            coroutineScope.launch {
                                patient = viewModel.getPatientById(
                                    it
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.padding(
                                top = 16.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                        ) {
                            QueuePatientCard(
                                navController,
                                viewModel,
                                landingViewModel,
                                queueListState,
                                appointmentResponseLocal,
                                patient
                            )
                        }
                    }
                    // no show
                    items(viewModel.noShowQueueList) { appointmentResponseLocal ->
                        var patient by remember {
                            mutableStateOf<PatientResponse?>(null)
                        }
                        appointmentResponseLocal.patientId.let {
                            coroutineScope.launch {
                                patient = viewModel.getPatientById(
                                    it
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        ) {
                            QueuePatientCard(
                                navController,
                                viewModel,
                                landingViewModel,
                                queueListState,
                                appointmentResponseLocal,
                                patient
                            )
                        }
                    }
                    // cancelled
                    items(viewModel.cancelledQueueList) { appointmentResponseLocal ->
                        var patient by remember {
                            mutableStateOf<PatientResponse?>(null)
                        }
                        appointmentResponseLocal.patientId.let {
                            coroutineScope.launch {
                                patient = viewModel.getPatientById(
                                    it
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.padding(
                                top = 16.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                        ) {
                            CancelledQueueCard(
                                navController,
                                appointmentResponseLocal,
                                patient
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            )
        }
    }
    if (viewModel.showCancelAppointmentDialog) {
        CancelAppointmentDialog(
            patient = viewModel.patientSelected!!,
            dateAndTime = viewModel.appointmentSelected?.slot?.start?.toAppointmentDate()!!
        ) { cancel ->
            if (cancel) {
                viewModel.cancelAppointment {
                    Timber.d("manseeyy appointment cancelled")
                    viewModel.getAppointmentListByDate()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.appointment_cancelled)
                        )
                    }
                }
            }
            viewModel.showCancelAppointmentDialog = false
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
                        viewModel.showDatePicker = false
                        viewModel.selectedChip = R.string.total_appointment
                        viewModel.selectedDate =
                            datePickerState.selectedDateMillis?.let { dateInLong ->
                                Date(
                                    dateInLong
                                )
                            } ?: Date()
                        viewModel.weekList = viewModel.selectedDate.to14DaysWeek()
                        viewModel.getAppointmentListByDate()
                        coroutineScope.launch {
                            dateScrollState.scrollToItem(7, scrollOffset = -130)
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
fun AppointmentStatusChips(
    label: Int, count: Int,
    listState: LazyListState,
    coroutineScope: CoroutineScope,
    index: Int,
    viewModel: QueueViewModel
) {
    FilterChip(
        selected = viewModel.selectedChip == label,
        onClick = {
            viewModel.selectedChip = label
            coroutineScope.launch {
                listState.animateScrollToItem(index)
            }
        },
        label = {
            Text(text = stringResource(id = label, count))
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            containerColor = MaterialTheme.colorScheme.onPrimary,
            labelColor = MaterialTheme.colorScheme.outline
        ),
        border = FilterChipDefaults.filterChipBorder(
            selectedBorderWidth = 1.dp,
            selectedBorderColor = MaterialTheme.colorScheme.primary,
            borderColor = MaterialTheme.colorScheme.outline,
            borderWidth = 1.dp
        ),
        enabled = count != 0
    )
}

@Composable
fun QueuePatientCard(
    navController: NavController,
    viewModel: QueueViewModel,
    landingViewModel: LandingScreenViewModel,
    queueListState: ReorderableLazyListState,
    appointmentResponseLocal: AppointmentResponseLocal,
    patient: PatientResponse?
) {
    val age = patient?.birthDate?.toTimeInMilli()?.toAge()
    val subTitle = "${
        patient?.gender?.get(0)?.uppercase()
    }/$age${if (patient?.fhirId.isNullOrEmpty()) "" else ", PID: ${patient?.fhirId}"} "

    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (appointmentResponseLocal.status == AppointmentStatusEnum.NO_SHOW.value) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .testTag("QUEUE_PATIENT_CARD")
            .clickable {
            navController.currentBackStackEntry?.savedStateHandle?.set(
                NavControllerConstants.PATIENT,
                patient
            )
            navController.navigate(Screen.PatientLandingScreen.route)
        }
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
                        if ((appointmentResponseLocal.status == AppointmentStatusEnum.SCHEDULED.value
                                    || appointmentResponseLocal.status == AppointmentStatusEnum.IN_PROGRESS.value)
                            && appointmentResponseLocal.slot.start.toEndOfDay() == Date().toEndOfDay()
                        ) {
                            viewModel.statusList = when (appointmentResponseLocal.status) {
                                AppointmentStatusEnum.SCHEDULED.value -> listOf("Arrived")
                                AppointmentStatusEnum.IN_PROGRESS.value -> listOf("Completed")
                                else -> listOf()
                            }
                            viewModel.appointmentSelected = appointmentResponseLocal
                            landingViewModel.showStatusChangeLayout = true
                        }
                    },
                    label = {
                        Text(text = fromValue(appointmentResponseLocal.status).label)
                    },
                    trailingIcon = {
                        if ((appointmentResponseLocal.status == AppointmentStatusEnum.SCHEDULED.value
                                    || appointmentResponseLocal.status == AppointmentStatusEnum.IN_PROGRESS.value)
                            && appointmentResponseLocal.slot.start.toEndOfDay() == Date().toEndOfDay()
                        ) {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "DROP_DOWN_ICON",
                                tint = when (appointmentResponseLocal.status) {
                                    AppointmentStatusEnum.WALK_IN.value -> WalkInLabel
                                    AppointmentStatusEnum.ARRIVED.value -> ArrivedLabel
                                    AppointmentStatusEnum.SCHEDULED.value -> TodayScheduledLabel
                                    else -> InProgressLabel
                                },
                                modifier = Modifier
                                    .size(18.dp)
                            )
                        }
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (appointmentResponseLocal.status) {
                            AppointmentStatusEnum.WALK_IN.value -> WalkInContainer
                            AppointmentStatusEnum.ARRIVED.value -> ArrivedContainer
                            AppointmentStatusEnum.SCHEDULED.value -> TodayScheduledContainer
                            AppointmentStatusEnum.CANCELLED.value -> CancelledContainer
                            AppointmentStatusEnum.COMPLETED.value -> CompletedContainer
                            AppointmentStatusEnum.IN_PROGRESS.value -> InProgressContainer
                            else -> NoShowContainer
                        },
                        labelColor = when (appointmentResponseLocal.status) {
                            AppointmentStatusEnum.WALK_IN.value -> WalkInLabel
                            AppointmentStatusEnum.ARRIVED.value -> ArrivedLabel
                            AppointmentStatusEnum.SCHEDULED.value -> TodayScheduledLabel
                            AppointmentStatusEnum.CANCELLED.value -> CancelledLabel
                            AppointmentStatusEnum.COMPLETED.value -> CompletedLabel
                            AppointmentStatusEnum.IN_PROGRESS.value -> InProgressLabel
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        borderColor = when (appointmentResponseLocal.status) {
                            AppointmentStatusEnum.WALK_IN.value -> WalkInLabel
                            AppointmentStatusEnum.ARRIVED.value -> ArrivedLabel
                            AppointmentStatusEnum.SCHEDULED.value -> TodayScheduledLabel
                            AppointmentStatusEnum.CANCELLED.value -> CancelledLabel
                            AppointmentStatusEnum.COMPLETED.value -> CompletedLabel
                            AppointmentStatusEnum.IN_PROGRESS.value -> InProgressLabel
                            else -> NoShowLabel
                        }
                    )
                )
                Text(
                    text = NameConverter.getFullName(
                        patient?.firstName,
                        patient?.middleName,
                        patient?.lastName
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.schedule_icon),
                        contentDescription = "SCHEDULE_ICON",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = appointmentResponseLocal.slot.start.toAppointmentTime(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
            if (appointmentResponseLocal.status == AppointmentStatusEnum.WALK_IN.value || appointmentResponseLocal.status == AppointmentStatusEnum.ARRIVED.value
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.drag_handle_icon),
                    contentDescription = "DRAG_HANDLE",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .size(36.dp)
                        .detectReorder(queueListState)
                )
            }
        }
        if (
            appointmentResponseLocal.status == AppointmentStatusEnum.WALK_IN.value
            || appointmentResponseLocal.status == AppointmentStatusEnum.ARRIVED.value
            || appointmentResponseLocal.status == AppointmentStatusEnum.SCHEDULED.value
        ) {
            Divider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            if (
                appointmentResponseLocal.status == AppointmentStatusEnum.WALK_IN.value
                || appointmentResponseLocal.status == AppointmentStatusEnum.ARRIVED.value
                || appointmentResponseLocal.status == AppointmentStatusEnum.SCHEDULED.value
            ) {
                TextButton(
                    onClick = {
                        viewModel.showCancelAppointmentDialog = true
                        viewModel.patientSelected = patient
                        viewModel.appointmentSelected = appointmentResponseLocal
                    },
                    modifier = Modifier.weight(1f).testTag("APPOINTMENT_CANCEL_BTN")
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
            if (appointmentResponseLocal.status == AppointmentStatusEnum.SCHEDULED.value) {
                Surface(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    TextButton(
                        onClick = {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                NavControllerConstants.APPOINTMENT_SELECTED,
                                appointmentResponseLocal
                            )
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                NavControllerConstants.PATIENT,
                                patient
                            )
                            navController.navigate(Screen.RescheduleAppointments.route)
                        },
                        modifier = Modifier.testTag("APPOINTMENT_RESCHEDULE_BTN")
                    ) {
                        Text(text = stringResource(id = R.string.reschedule))
                    }
                }
            }
        }
    }
}

@Composable
fun CancelledQueueCard(
    navController: NavController,
    appointmentResponseLocal: AppointmentResponseLocal,
    patient: PatientResponse?
) {
    val age = patient?.birthDate?.toTimeInMilli()?.toAge()
    val subTitle = "${
        patient?.gender?.get(0)?.uppercase()
    }/$age${if (patient?.fhirId.isNullOrEmpty()) "" else ", PID: ${patient?.fhirId}"} "

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        modifier = Modifier.clickable {
            navController.currentBackStackEntry?.savedStateHandle?.set(
                NavControllerConstants.PATIENT,
                patient
            )
            navController.navigate(Screen.PatientLandingScreen.route)
        }
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
                    onClick = { },
                    label = {
                        Text(text = fromValue(appointmentResponseLocal.status).label)
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
                    text = NameConverter.getFullName(
                        patient?.firstName,
                        patient?.middleName,
                        patient?.lastName
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.schedule_icon),
                        contentDescription = "SCHEDULE_ICON",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = appointmentResponseLocal.slot.start.toAppointmentTime(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}