package com.latticeonfhir.core.ui.prescription.photo.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.latticeonfhir.android.R
import com.latticeonfhir.core.data.local.enums.PrescriptionType
import com.latticeonfhir.core.data.local.enums.WorkerStatus
import com.latticeonfhir.android.data.local.model.prescription.PrescriptionPhotoResponseLocal
import com.latticeonfhir.core.data.local.model.prescription.PrescriptionResponseLocal
import com.latticeonfhir.core.data.server.model.patient.PatientResponse
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.core.ui.CustomDialog
import com.latticeonfhir.core.ui.common.DisplaySyncStatus
import com.latticeonfhir.core.ui.common.Loader
import com.latticeonfhir.core.ui.main.MainActivity
import com.latticeonfhir.core.ui.patientlandingscreen.AllSlotsBookedDialog
import com.latticeonfhir.core.utils.constants.NavControllerConstants
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.isToday
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.isYesterday
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toDate
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toDayFullMonthYear
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toPrescriptionNavDate
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toPrescriptionTime
import com.latticeonfhir.core.utils.converters.responseconverter.medication.MedicationInfoConverter.getMedInfo
import com.latticeonfhir.core.utils.file.FileManager
import com.latticeonfhir.android.utils.file.FileManager.getUriFromFileName
import com.latticeonfhir.android.utils.file.FileManager.shareImageToOtherApps
import com.latticeonfhir.android.utils.network.ConnectivityObserver
import com.latticeonfhir.core.model.enums.PrescriptionType
import com.latticeonfhir.core.model.enums.WorkerStatus
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionPhotoViewScreen(
    navController: NavController,
    viewModel: PrescriptionPhotoViewViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { map ->
            if (!map.values.contains(false)) {
                viewModel.hideSyncStatus()
                viewModel.showAddPrescriptionBottomSheet = false
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    NavControllerConstants.PATIENT,
                    viewModel.patient!!
                )
                navController.navigate(Screen.PrescriptionPhotoUploadScreen.route)
            } else {
                val shouldShowDialog = map.map { (permission, _) ->
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, permission).not()
                }
                if (shouldShowDialog.contains(true)) {
                    viewModel.showOpenSettingsDialog = true
                } else {
                    viewModel.showAddPrescriptionBottomSheet = false
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.please_grant_permission))
                    }
                }
            }
        })
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    NavControllerConstants.PATIENT
                )
            viewModel.getCurrentSyncStatus()
            viewModel.isLaunched = true
            viewModel.isLoading = true
        }
        viewModel.getPastPrescription()
    }

    BackHandler(enabled = true) {
        if (viewModel.isFabSelected) viewModel.isFabSelected = false
        else if (viewModel.selectedFile != null) {
            viewModel.isTapped = false
            viewModel.isLongPressed = false
            viewModel.displayNote = true
            viewModel.selectedFile = null
        } else navController.navigateUp()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            AnimatedContent(targetState = viewModel.isTapped, label = "") {
                when (it) {
                    true -> {
                        MediumTopAppBar(
                            modifier = Modifier.fillMaxWidth(),
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                            ),
                            title = {
                                Text(
                                    text = viewModel.selectedFile?.date?.toPrescriptionNavDate()
                                        ?: "",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.testTag("HEADING_TAG")
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    viewModel.isTapped = false
                                    viewModel.displayNote = true
                                    viewModel.selectedFile = null
                                }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "BACK_ICON"
                                    )
                                }
                            },
                            actions = {
                                NavBarActions(context, viewModel)
                            }
                        )
                    }

                    false -> {
                        TopAppBar(
                            modifier = Modifier.fillMaxWidth(),
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                            ),
                            title = {
                                Text(
                                    text = stringResource(id = R.string.prescriptions),
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.testTag("HEADING_TAG")
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    navController.navigateUp()
                                }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "BACK_ICON"
                                    )
                                }
                            },
                            actions = {
                                AnimatedVisibility(visible = viewModel.isLongPressed) {
                                    NavBarActions(context, viewModel)
                                }
                            }
                        )
                    }
                }
            }
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Column {
                    CheckNetwork(viewModel, activity)
                    if (viewModel.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Loader()
                        }
                    } else {
                        if (viewModel.allPrescriptionList.none { prescription ->
                                !viewModel.deletedPhotos.contains(
                                    prescription
                                )
                            }) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(id = R.string.no_previous_prescription),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        PhotoView(viewModel)
                    }
                }
                AnimatedVisibility(
                    visible = viewModel.isTapped,
                    enter = slideInHorizontally(initialOffsetX = { offset -> offset }),
                    exit = slideOutHorizontally(targetOffsetX = { offset -> offset })
                ) {
                    DisplayImage(context, viewModel)
                }
                if (viewModel.showAllSlotsBookedDialog) {
                    AllSlotsBookedDialog {
                        viewModel.showAllSlotsBookedDialog = false
                    }
                }
            }
        },
        floatingActionButton = {
            if (!viewModel.isTapped) {
                FloatingActionButton(
                    onClick = {
                        viewModel.getAppointmentInfo {
                            if (viewModel.canAddPrescription) {
                                viewModel.showAddPrescriptionBottomSheet = true
                            } else if (viewModel.isAppointmentCompleted) {
                                viewModel.showAppointmentCompletedDialog = true
                            } else {
                                viewModel.showAddToQueueDialog = true
                            }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_icon),
                        contentDescription = null,
                        Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )
    if (viewModel.showDeleteDialog) {
        com.latticeonfhir.core.ui.CustomDialog(
            title = stringResource(id = R.string.discard_prescription),
            text = stringResource(id = R.string.discard_prescription_description),
            dismissBtnText = stringResource(id = R.string.no_go_back),
            confirmBtnText = stringResource(id = R.string.yes_discard),
            dismiss = { viewModel.showDeleteDialog = false },
            confirm = {
                // delete prescription
                viewModel.deletePrescription {
                    FileManager.removeFromInternalStorage(
                        context,
                        (viewModel.selectedFile!!.prescription as PrescriptionPhotoResponseLocal).prescription[0].filename
                    )
                    viewModel.isTapped = false
                    viewModel.isLongPressed = false
                    viewModel.selectedFile = null
                    viewModel.showDeleteDialog = false
                }
            }
        )
    }
    if (viewModel.showNoteDialog) {
        AddNoteDialog(
            image = (viewModel.selectedFile?.prescription as PrescriptionPhotoResponseLocal).prescription[0].filename.getUriFromFileName(
                context
            ),
            note = (viewModel.selectedFile!!.prescription as PrescriptionPhotoResponseLocal).prescription[0].note,
            dismiss = {
                viewModel.showNoteDialog = false
            },
            confirm = { note ->
                // add note to prescription
                viewModel.addNoteToPrescription(note) {
                    if (viewModel.isLongPressed) {
                        viewModel.isLongPressed = false
                        viewModel.selectedFile = null
                    }
                    if (viewModel.isTapped) {
                        viewModel.displayNote = true
                        val prescriptionPhotoResponseLocal =
                            (viewModel.selectedFile!!.prescription as PrescriptionPhotoResponseLocal)
                        viewModel.selectedFile = viewModel.selectedFile?.copy(
                            prescription = prescriptionPhotoResponseLocal.copy(
                                prescription = listOf(
                                    prescriptionPhotoResponseLocal.prescription[0].copy(
                                        note = note
                                    )
                                )
                            )
                        )
                    }
                    viewModel.showNoteDialog = false
                }
            }
        )
    }
    if (viewModel.showAddToQueueDialog) {
        AddToQueueDialog(
            appointment = viewModel.appointment,
            confirm = {
                if (viewModel.appointment != null) {
                    viewModel.updateStatusToArrived(
                        viewModel.patient!!,
                        viewModel.appointment!!
                    ) {
                        viewModel.showAddToQueueDialog = false
                        viewModel.showAddPrescriptionBottomSheet = true
                    }
                } else {
                    if (viewModel.ifAllSlotsBooked) {
                        viewModel.showAllSlotsBookedDialog = true
                    } else {
                        viewModel.addPatientToQueue(viewModel.patient!!) {
                            viewModel.showAddToQueueDialog = false
                            viewModel.showAddPrescriptionBottomSheet = true
                        }
                    }
                }
            },
            dismiss = {
                viewModel.showAddToQueueDialog = false
            }
        )
    }
    if (viewModel.showAppointmentCompletedDialog) {
        AppointmentCompletedDialog {
            viewModel.showAppointmentCompletedDialog = false
        }
    }
    if (viewModel.showOpenSettingsDialog) {
        com.latticeonfhir.core.ui.CustomDialog(
            canBeDismissed = false,
            title = stringResource(id = R.string.permissions_required),
            text = stringResource(id = R.string.permissions_required_description),
            dismissBtnText = stringResource(id = R.string.cancel),
            confirmBtnText = stringResource(id = R.string.go_to_settings),
            dismiss = {
                viewModel.showOpenSettingsDialog = false
                viewModel.showAddPrescriptionBottomSheet = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.please_grant_permission))
                }
            },
            confirm = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
                viewModel.showOpenSettingsDialog = false
            }
        )
    }
    if (viewModel.showAddPrescriptionBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.showAddPrescriptionBottomSheet = false },
            sheetState = rememberModalBottomSheetState(),
            modifier = Modifier
                .navigationBarsPadding(),
            dragHandle = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.add_a_new_prescription),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = { viewModel.showAddPrescriptionBottomSheet = false }
                    ) {
                        Icon(Icons.Default.Clear, Icons.Default.Clear.name)
                    }
                }
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Column {
                    PrescriptionOptionRow(
                        icon = painterResource(R.drawable.camera),
                        label = stringResource(R.string.upload_a_prescription),
                        onClick = {
                            checkPermissions(
                                context = context,
                                requestPermission = { permissionsToBeRequest ->
                                    requestPermissionLauncher.launch(permissionsToBeRequest)
                                },
                                navigate = {
                                    viewModel.hideSyncStatus()
                                    viewModel.showAddPrescriptionBottomSheet = false
                                    coroutineScope.launch {
                                        navController.currentBackStackEntry?.savedStateHandle?.set(
                                            NavControllerConstants.PATIENT,
                                            viewModel.patient!!
                                        )
                                        navController.navigate(Screen.PrescriptionPhotoUploadScreen.route)
                                    }
                                }
                            )
                        }
                    )
                    PrescriptionOptionRow(
                        icon = painterResource(R.drawable.prescriptions),
                        label = stringResource(R.string.fill_prescription),
                        onClick = {
                            viewModel.hideSyncStatus()
                            viewModel.showAddPrescriptionBottomSheet = false
                            coroutineScope.launch {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    NavControllerConstants.PATIENT,
                                    viewModel.patient!!
                                )
                                navController.navigate(Screen.Prescription.route)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PrescriptionOptionRow(
    icon: Painter,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = icon,
            null,
            modifier = Modifier.weight(1f),
            tint = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(9f)
        )
    }
}

private fun checkPermissions(
    context: Context,
    requestPermission: (Array<String>) -> Unit,
    navigate: () -> Unit
) {
    val permissionsToBeRequest = mutableListOf<String>()
    val permissions = mutableListOf(Manifest.permission.CAMERA)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        permissions.addAll(
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }
    permissions.forEach {
        if (ContextCompat.checkSelfPermission(
                context,
                it
            ) != PackageManager.PERMISSION_GRANTED
        ) permissionsToBeRequest.add(it)
    }
    if (permissionsToBeRequest.isNotEmpty()) {
        requestPermission(permissionsToBeRequest.toTypedArray())
    } else {
        navigate()
    }
}

@Composable
fun NavBarActions(
    context: Context,
    viewModel: PrescriptionPhotoViewViewModel
) {
    if (viewModel.selectedFile != null) {
        Row {
            IconButton(onClick = {
                viewModel.showDeleteDialog = true
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.delete_icon),
                    contentDescription = "DELETE_ICON",
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(onClick = {
                shareImageToOtherApps(
                    context,
                    (viewModel.selectedFile!!.prescription as PrescriptionPhotoResponseLocal).prescription[0].filename.getUriFromFileName(
                        context
                    )
                )
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.share),
                    contentDescription = null
                )
            }
            IconButton(onClick = {
                viewModel.showNoteDialog = true
            }) {
                Icon(
                    painter = if ((viewModel.selectedFile!!.prescription as PrescriptionPhotoResponseLocal).prescription[0].note.isEmpty())
                        painterResource(id = R.drawable.note_add)
                    else painterResource(id = R.drawable.edit_note),
                    contentDescription = "NOTE_ICON"
                )
            }
        }
    }
}

@Composable
private fun DisplayImage(
    context: Context,
    viewModel: PrescriptionPhotoViewViewModel
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val imageSize = remember { mutableStateOf(IntSize.Zero) }
    if (viewModel.selectedFile != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface)
                    .clickable(
                        enabled = (viewModel.selectedFile!!.prescription as PrescriptionPhotoResponseLocal).prescription[0].note.isNotBlank(),
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        viewModel.displayNote = !viewModel.displayNote
                    }
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val newScale = scale * zoom
                            if (newScale in 1f..8f) {
                                scale = newScale

                                // calculate maximum allowable translations
                                val maxOffsetX = (imageSize.value.width * (scale - 1)) / 2
                                val maxOffsetY = (imageSize.value.height * (scale - 1)) / 2

                                // update offsets within bounds
                                offsetX = (offsetX + pan.x).coerceIn(-maxOffsetX, maxOffsetX)
                                offsetY = (offsetY + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                            }
                        }
                    }
                    .onSizeChanged {
                        imageSize.value = it
                    },
                painter = rememberImagePainter(
                    (viewModel.selectedFile!!.prescription as PrescriptionPhotoResponseLocal).prescription[0].filename.getUriFromFileName(
                        context
                    )
                ),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
            if ((viewModel.selectedFile!!.prescription as PrescriptionPhotoResponseLocal).prescription[0].note.isNotBlank()) {
                AnimatedVisibility(
                    visible = viewModel.displayNote,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 500.dp)
                            .verticalScroll(rememberScrollState()),
                        color = Color.Black.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = (viewModel.selectedFile!!.prescription as PrescriptionPhotoResponseLocal).prescription[0].note,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoView(viewModel: PrescriptionPhotoViewViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val groupedPrescriptionList =
        remember(viewModel.allPrescriptionList.toList(), viewModel.deletedPhotos.toList()) {
            viewModel.allPrescriptionList.filter {
                !viewModel.deletedPhotos.contains(it)
            }.groupBy { it.date.toDayFullMonthYear() }.toSortedMap()
        }
    val lastItemIndex =
        (groupedPrescriptionList.values.flatten().size + groupedPrescriptionList.keys.size)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = lastItemIndex)

    LaunchedEffect(viewModel.allPrescriptionList.size) {
        coroutineScope.launch {
            if (lastItemIndex >= 0) {
                listState.animateScrollToItem(lastItemIndex)
            }
        }
    }

    LazyColumn(
        state = listState
    ) {
        groupedPrescriptionList.forEach { (date, items) ->
            item {
                val header = when {
                    isToday(date.toDate("dd MMMM yyyy")) -> stringResource(R.string.today)
                    isYesterday(date.toDate("dd MMMM yyyy")) -> stringResource(R.string.yesterday)
                    else -> date
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = header,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                                .padding(8.dp)
                        )
                    }
                }
            }
            items(items) { prescription ->
                if (prescription.type == PrescriptionType.FORM.type) {
                    // form prescription
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem(tween(300, easing = LinearEasing))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Column(
                            modifier = Modifier
                                .background(
                                    shape = RoundedCornerShape(
                                        topStart = 48f,
                                        topEnd = 48f,
                                        bottomStart = 48f,
                                        bottomEnd = 0f
                                    ),
                                    color = MaterialTheme.colorScheme.surface
                                ),
                            horizontalAlignment = Alignment.End
                        ) {
                            Column(
                                modifier = Modifier
                                    .widthIn(250.dp, 300.dp)
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                (prescription.prescription as PrescriptionResponseLocal).prescription.forEach { directionAndMedication ->
                                    MedicineDetails(
                                        medName = directionAndMedication.medName,
                                        details = getMedInfo(
                                            duration = directionAndMedication.duration,
                                            frequency = directionAndMedication.frequency,
                                            medUnit = directionAndMedication.medUnit,
                                            timing = directionAndMedication.timing,
                                            note = directionAndMedication.note,
                                            qtyPerDose = directionAndMedication.qtyPerDose,
                                            qtyPrescribed = directionAndMedication.qtyPrescribed,
                                            context = context
                                        )
                                    )
                                }
                            }
                            Text(
                                text = prescription.date.toPrescriptionTime(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .padding(end = 10.dp, bottom = 6.dp)
                            )
                        }
                    }
                } else {
                    val prescriptionPhoto =
                        (prescription.prescription as PrescriptionPhotoResponseLocal).prescription[0]
                    val uploadFolder = FileManager.createFolder(context)
                    val photoFile = File(
                        uploadFolder,
                        prescriptionPhoto.filename
                    )
                    val uri = Uri.fromFile(photoFile)
                    key(viewModel.recompose) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem(tween(300, easing = LinearEasing))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Column(
                                modifier = Modifier
                                    .shadow(
                                        elevation = if (viewModel.isLongPressed && viewModel.selectedFile == prescription)
                                            11.dp else 0.dp,
                                        shape = RoundedCornerShape(
                                            topStart = 48f,
                                            topEnd = 48f,
                                            bottomStart = 48f,
                                            bottomEnd = 0f
                                        )
                                    )
                                    .background(
                                        shape = RoundedCornerShape(
                                            topStart = 48f,
                                            topEnd = 48f,
                                            bottomStart = 48f,
                                            bottomEnd = 0f
                                        ),
                                        color = if (viewModel.isLongPressed && viewModel.selectedFile == prescription)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .pointerInput(prescription) {
                                        detectTapGestures(
                                            onLongPress = {
                                                viewModel.isLongPressed = true
                                                viewModel.selectedFile = prescription
                                            },
                                            onTap = {
                                                if (viewModel.isLongPressed) {
                                                    viewModel.isLongPressed = false
                                                    viewModel.selectedFile = null
                                                } else {
                                                    viewModel.isTapped = true
                                                    viewModel.selectedFile = prescription
                                                }
                                            }
                                        )
                                    },
                                horizontalAlignment = Alignment.End
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(uri),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(250.dp, 330.dp)
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(16.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                if (prescriptionPhoto.note.isNotBlank()) {
                                    Text(
                                        text = prescriptionPhoto.note,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier
                                            .width(250.dp)
                                            .padding(top = 6.dp, start = 6.dp, end = 6.dp)
                                    )
                                }
                                Text(
                                    text = Date(
                                        prescriptionPhoto.filename.substringBefore(".").toLong()
                                    ).toPrescriptionTime(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier
                                        .width(250.dp)
                                        .padding(end = 10.dp, bottom = 6.dp),
                                    textAlign = TextAlign.End
                                )
                            }

                            AnimatedVisibility(viewModel.isLongPressed && viewModel.selectedFile == prescription) {
                                Icon(
                                    painter = painterResource(id = R.drawable.check_circle),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun AddNoteDialog(
    image: Uri,
    note: String,
    dismiss: () -> Unit,
    confirm: (String) -> Unit
) {
    val maxNoteChar = 200
    var noteValue by remember {
        mutableStateOf(note)
    }
    Dialog(
        onDismissRequest = { dismiss() },
        content = {
            Column(
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                )
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(image),
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            contentScale = ContentScale.Crop
                        )
                        OutlinedTextField(
                            value = noteValue,
                            onValueChange = {
                                if (it.length <= maxNoteChar) noteValue = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.15f),
                            placeholder = {
                                Text(
                                    text = stringResource(id = R.string.add_notes_here),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp),
                            textStyle = MaterialTheme.typography.bodyMedium,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences
                            )
                        )
                    }
                    Button(
                        onClick = { confirm(noteValue.trim()) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = noteValue.trim() != note.trim()
                    ) {
                        Text(text = stringResource(id = R.string.save))
                    }
                }
            }
        }
    )
}

@Composable
private fun CheckNetwork(viewModel: PrescriptionPhotoViewViewModel, context: MainActivity) {
    if (context.connectivityStatus.value == ConnectivityObserver.Status.Available) {
        if (viewModel.syncStatus == WorkerStatus.OFFLINE) viewModel.syncStatus =
            WorkerStatus.TODO
    } else {
        viewModel.syncStatus = WorkerStatus.OFFLINE
    }
    AnimatedVisibility(
        visible = viewModel.syncStatus != WorkerStatus.TODO,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        DisplaySyncStatus(
            viewModel.syncStatus,
            viewModel.getSyncIcon(),
            viewModel.getSyncStatusMessage()
        )
    }
}

@Composable
private fun MedicineDetails(medName: String, details: String) {
    Column {
        Text(
            text = medName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = details,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}