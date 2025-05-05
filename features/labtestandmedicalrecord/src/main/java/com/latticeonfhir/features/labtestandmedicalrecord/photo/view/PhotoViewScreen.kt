package com.latticeonfhir.features.labtestandmedicalrecord.photo.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.latticeonfhir.core.model.enums.PhotoUploadTypeEnum
import com.latticeonfhir.core.model.enums.WorkerStatus
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.core.navigation.Screen
import com.latticeonfhir.core.ui.AddToQueueDialog
import com.latticeonfhir.core.ui.AllSlotsBookedDialog
import com.latticeonfhir.core.ui.DisplaySyncStatus
import com.latticeonfhir.core.ui.main.MainActivity
import com.latticeonfhir.core.utils.constants.NavControllerConstants
import com.latticeonfhir.core.utils.constants.PhotoUploadViewType.PHOTO_VIEW_TYPE
import com.latticeonfhir.core.utils.converters.TimeConverter.toDayFullMonthYear
import com.latticeonfhir.core.utils.converters.TimeConverter.toEndOfDay
import com.latticeonfhir.core.utils.converters.TimeConverter.toPrescriptionNavDate
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.isSameDay
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.isToday
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.isYesterday
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toPrescriptionTime
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import com.latticeonfhir.core.utils.file.FileManager
import com.latticeonfhir.core.utils.file.FileManager.getUriFromFileName
import com.latticeonfhir.core.utils.file.FileManager.shareImageToOtherApps
import com.latticeonfhir.core.utils.network.ConnectivityObserver
import com.latticeonfhir.features.labtestandmedicalrecord.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoViewScreen(
    navController: NavController, viewModel: PhotoViewViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = LocalContext.current as MainActivity
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { map ->
            if (!map.values.contains(false)) {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    NavControllerConstants.PATIENT, viewModel.patient!!
                )
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    PHOTO_VIEW_TYPE,
                    if (checkPhotoViewIsLabTestType(viewModel)) PhotoUploadTypeEnum.LAB_TEST.value else PhotoUploadTypeEnum.MEDICAL_RECORD.value
                )
                navController.navigate(Screen.LabAndMedPhotoUploadScreen.route)
            } else {
                val shouldShowDialog = map.map { (permission, _) ->
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, permission).not()
                }
                if (shouldShowDialog.contains(true)) {
                    viewModel.showOpenSettingsDialog = true
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.please_grant_permission))
                    }
                }
            }
        })


    LaunchedEffect(viewModel.isLaunched) {
        viewModel.apply {

            if (!isLaunched) {
                patient =
                    navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                        NavControllerConstants.PATIENT
                    )

                patient?.let {
                    getStudentTodayAppointment(
                        Date(Date().toTodayStartDate()), Date(Date().toEndOfDay()), patient!!.id
                    )
                }
                photoviewType =
                    navController.previousBackStackEntry?.savedStateHandle?.get<String>(
                        PHOTO_VIEW_TYPE
                    ) ?: ""
                getCurrentSyncStatus()
                getPastLabAndMedTest()
                isLaunched = true
            }
        }
    }


    BackHandler(enabled = true) {
        viewModel.apply {

            if (isFabSelected) isFabSelected = false
            else if (selectedFile != null) {
                isTapped = false
                isLongPressed = false
                displayNote = true
                selectedFile = null
            } else navController.popBackStack(Screen.PatientLandingScreen.route, inclusive = false)
        }
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
                                    text = viewModel.selectedFile?.filename?.substringBefore(".")
                                        ?.toLong()
                                        ?.let { long ->
                                            Date(
                                                long
                                            ).toPrescriptionNavDate()
                                        } ?: "",
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
                                    text = stringResource(
                                        id = if (checkPhotoViewIsLabTestType(viewModel)) R.string.lab_test_title else R.string.medical_record_title
                                    ),
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.testTag("HEADING_TAG")
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    navController.popBackStack(
                                        Screen.PatientLandingScreen.route,
                                        inclusive = false
                                    )
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
                    if (viewModel.labTestPhotos.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = if (checkPhotoViewIsLabTestType(viewModel)) R.string.no_lab_test_added else R.string.no_med_record_fount),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    PhotoView(viewModel)
                }
                AnimatedVisibility(
                    visible = viewModel.isTapped,
                    enter = slideInHorizontally(initialOffsetX = { offset -> offset }),
                    exit = slideOutHorizontally(targetOffsetX = { offset -> offset })
                ) {
                    DisplayImage(context, viewModel)
                }

            }
        },
        floatingActionButton = {
            if (!viewModel.isTapped) {
                FloatingActionButton(
                    onClick = {

                        viewModel.getAppointmentInfo {
                            if (viewModel.canAddAssessment) {
                                checkPermissions(
                                    context = context,
                                    requestPermission = { permissionsToBeRequest ->
                                        requestPermissionLauncher.launch(permissionsToBeRequest)
                                    },
                                    navigate = {
                                        viewModel.hideSyncStatus()
                                        coroutineScope.launch {
                                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                                NavControllerConstants.PATIENT, viewModel.patient!!
                                            )
                                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                                PHOTO_VIEW_TYPE,
                                                if (checkPhotoViewIsLabTestType(viewModel)) PhotoUploadTypeEnum.LAB_TEST.value else PhotoUploadTypeEnum.MEDICAL_RECORD.value
                                            )
                                            navController.navigate(Screen.LabAndMedPhotoUploadScreen.route)
                                        }
                                    }
                                )
                            } else if (viewModel.isAppointmentCompleted) {
                                viewModel.showAppointmentCompletedDialog = true
                            } else {
                                viewModel.showAddToQueueDialog = true
                            }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.camera),
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
            title = stringResource(id = if (checkPhotoViewIsLabTestType(viewModel)) R.string.discard_lab_test else R.string.discard_med_record_test),
            text = stringResource(id = if (checkPhotoViewIsLabTestType(viewModel)) R.string.discard_lab_test_description else R.string.discard_med_record_description),
            dismissBtnText = stringResource(id = R.string.no_go_back),
            confirmBtnText = stringResource(id = R.string.yes_discard),
            dismiss = { viewModel.showDeleteDialog = false },
            confirm = {
                // delete prescription
                viewModel.deleteLabTest {
                    FileManager.removeFromInternalStorage(
                        context,
                        viewModel.selectedFile!!.filename
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
            image = viewModel.selectedFile!!.filename.getUriFromFileName(context),
            note = viewModel.selectedFile!!.note,
            dismiss = {
                viewModel.showNoteDialog = false
            },
            confirm = { note ->
                // add note to prescription
                viewModel.addNoteToLabTest(note) {
                    if (viewModel.isLongPressed) {
                        viewModel.isLongPressed = false
                        viewModel.selectedFile = null
                    }
                    if (viewModel.isTapped) {
                        viewModel.displayNote = true
                        viewModel.selectedFile = viewModel.selectedFile?.copy(
                            note = if (note.length > 100) note.substring(0, 100) else note
                        )
                    }
                    viewModel.showNoteDialog = false
                }
            }
        )
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

    ShowDialogs(viewModel, navController, coroutineScope, context, requestPermissionLauncher)
}

@Composable
fun ShowDialogs(
    viewModel: PhotoViewViewModel,
    navController: NavController,
    coroutineScope: CoroutineScope,
    context: Context,
    requestPermissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
) {
    if (viewModel.showAddToQueueDialog) {
        AddToQueueDialog(
            appointment = viewModel.appointment,
            confirm = {
                if (viewModel.appointment != null) {
                    viewModel.updateStatusToArrived(
                        viewModel.patient!!, viewModel.appointment!!
                    ) {
                        viewModel.showAddToQueueDialog = false
                        checkPermissions(
                            context = context,
                            requestPermission = { permissionsToBeRequest ->
                                requestPermissionLauncher.launch(permissionsToBeRequest)
                            },
                            navigate = {
                                navigateToUploadScreen(viewModel, navController, coroutineScope)
                            }
                        )

                    }
                } else {
                    if (viewModel.ifAllSlotsBooked) {
                        viewModel.showAllSlotsBookedDialog = true
                    } else {
                        viewModel.addPatientToQueue(viewModel.patient!!) {
                            viewModel.showAddToQueueDialog = false

                            checkPermissions(
                                context = context,
                                requestPermission = { permissionsToBeRequest ->
                                    requestPermissionLauncher.launch(permissionsToBeRequest)
                                },
                                navigate = {
                                    navigateToUploadScreen(viewModel, navController, coroutineScope)
                                }
                            )
                        }
                    }
                }
            },
            dismiss = { viewModel.showAddToQueueDialog = false }
        )
    }
    if (viewModel.ifAllSlotsBooked) {
        AllSlotsBookedDialog {
            viewModel.showAllSlotsBookedDialog = false
        }
    }
}

fun navigateToUploadScreen(
    viewModel: PhotoViewViewModel,
    navController: NavController,
    coroutineScope: CoroutineScope
) {
    viewModel.hideSyncStatus()
    coroutineScope.launch {
        navController.currentBackStackEntry?.savedStateHandle?.set(
            NavControllerConstants.PATIENT, viewModel.patient!!
        )
        navController.currentBackStackEntry?.savedStateHandle?.set(
            PHOTO_VIEW_TYPE,
            if (checkPhotoViewIsLabTestType(viewModel)) PhotoUploadTypeEnum.LAB_TEST.value else PhotoUploadTypeEnum.MEDICAL_RECORD.value
        )
        navController.navigate(Screen.LabAndMedPhotoUploadScreen.route)
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
    context: Context, viewModel: PhotoViewViewModel
) {
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
                viewModel.selectedFile!!.filename.getUriFromFileName(context)
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
                painter = if (viewModel.selectedFile?.note.isNullOrEmpty())
                    painterResource(id = R.drawable.note_add)
                else painterResource(id = R.drawable.edit_note),
                contentDescription = "NOTE_ICON"
            )
        }
    }
}

@Composable
private fun DisplayImage(
    context: Context, viewModel: PhotoViewViewModel
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val imageSize = remember { mutableStateOf(IntSize.Zero) }
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
                    enabled = !viewModel.selectedFile?.note.isNullOrBlank(),
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    viewModel.displayNote = !viewModel.displayNote
                }
                .graphicsLayer(
                    scaleX = scale, scaleY = scale, translationX = offsetX, translationY = offsetY
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
            painter = rememberAsyncImagePainter(
                viewModel.selectedFile?.filename?.getUriFromFileName(
                    context
                )
            ),
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
        if (!viewModel.selectedFile?.note.isNullOrBlank()) {
            AnimatedVisibility(
                visible = viewModel.displayNote,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = viewModel.selectedFile!!.note,
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

@Composable
fun PhotoView(viewModel: PhotoViewViewModel) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(viewModel.labTestPhotos.size) {
        coroutineScope.launch {
            if (viewModel.labTestPhotos.isNotEmpty()) listState.scrollToItem(viewModel.labTestPhotos.size - 1)
        }
    }

    LazyColumn(
        state = listState
    ) {
        itemsIndexed(viewModel.labTestPhotos.filter {
            !viewModel.deletedPhotos.contains(it)
        }) { index, file ->
            val currentDate = Date(file.filename.substringBefore(".").toLong())
            val previousDate =
                viewModel.labTestPhotos.getOrNull(index - 1)?.filename?.substringBefore(".")
                    ?.toLong()
                    ?.let { Date(it) }

            val showHeader = when {
                previousDate == null -> true
                !isSameDay(currentDate, previousDate) -> true
                else -> false
            }

            if (showHeader) {
                val headerText = when {
                    isToday(currentDate) -> "Today"
                    isYesterday(currentDate) -> "Yesterday"
                    else -> currentDate.toDayFullMonthYear()
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
                            text = headerText,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                                .padding(8.dp)
                        )
                    }
                }
            }

            val uploadFolder = FileManager.createFolder(context)
            val photoFile = File(
                uploadFolder,
                file.filename
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
                                elevation = if (viewModel.isLongPressed && viewModel.selectedFile == file) 11.dp else 0.dp,
                                shape = RoundedCornerShape(
                                    topStart = 48f, topEnd = 48f, bottomStart = 48f, bottomEnd = 0f
                                )
                            )
                            .background(
                                shape = RoundedCornerShape(
                                    topStart = 48f, topEnd = 48f, bottomStart = 48f, bottomEnd = 0f
                                ),
                                color = if (viewModel.isLongPressed && viewModel.selectedFile == file) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surface
                            )
                            .pointerInput(viewModel.selectedFile) {
                                detectTapGestures(onLongPress = {
                                    viewModel.isLongPressed = true
                                    viewModel.selectedFile = file
                                }, onTap = {
                                    if (viewModel.isLongPressed) {
                                        viewModel.isLongPressed = false
                                        viewModel.selectedFile = null
                                    } else {
                                        viewModel.isTapped = true
                                        viewModel.selectedFile = file
                                    }
                                })
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
                        if (file.note.isNotBlank()) {
                            Text(
                                text = file.note,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier
                                    .width(250.dp)
                                    .padding(top = 6.dp, start = 6.dp, end = 6.dp)
                            )
                        }
                        Text(
                            text = Date(
                                file.filename.substringBefore(".").toLong()
                            ).toPrescriptionTime(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .width(250.dp)
                                .padding(end = 10.dp, bottom = 6.dp),
                            textAlign = TextAlign.End
                        )
                    }

                    AnimatedVisibility(viewModel.isLongPressed && viewModel.selectedFile == file) {
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
    val maxNoteChar = 100
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
private fun CheckNetwork(viewModel: PhotoViewViewModel, context: MainActivity) {
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

fun checkPhotoViewIsLabTestType(viewModel: PhotoViewViewModel): Boolean {
    return viewModel.photoviewType == PhotoUploadTypeEnum.LAB_TEST.value
}