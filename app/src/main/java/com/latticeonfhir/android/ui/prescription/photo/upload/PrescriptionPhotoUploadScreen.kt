package com.latticeonfhir.android.ui.prescription.photo.upload

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.latticeonfhir.android.R
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.navigation.Screen
import com.latticeonfhir.android.utils.constants.NavControllerConstants
import com.latticeonfhir.android.utils.constants.NavControllerConstants.PATIENT
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.Date

@Composable
fun PrescriptionPhotoUploadScreen(
    navController: NavController,
    viewModel: PrescriptionPhotoUploadViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var hasFlashUnit by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var camera: Camera? by remember { mutableStateOf(null) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var preview by remember { mutableStateOf<Preview?>(null) }
    var enterTransition by remember { mutableStateOf(slideInVertically(initialOffsetY = { it })) }
    var exitTransition by remember { mutableStateOf(slideOutVertically(targetOffsetY = { it })) }

    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                viewModel.isImageCaptured = true
                viewModel.selectedImageUri = it
            }
        }

    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient =
                navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                    PATIENT
                )
            viewModel.getPatientTodayAppointment(
                Date(Date().toTodayStartDate()),
                Date(Date().toEndOfDay()),
                viewModel.patient!!.id
            )
            viewModel.isLaunched = true
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f),
            contentAlignment = Alignment.TopCenter
        ) {
            TopRow(
                navController,
                viewModel.flashOn,
                hasFlashUnit
            ) {
                viewModel.flashOn = it
                camera?.cameraControl?.enableTorch(it)
            }
        }
        key(viewModel.cameraSelector) {
            AndroidView(
                factory = { context ->
                    val previewView = PreviewView(context)
                    val cameraProvider = cameraProviderFuture.get()
                    val previewUseCase = Preview.Builder()
                        .build()
                        .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                    cameraProviderFuture.addListener({
                        try {
                            cameraProvider.unbindAll()

                            imageCapture = ImageCapture.Builder().build()
                            camera = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                viewModel.cameraSelector,
                                previewUseCase,
                                imageCapture
                            )
                            hasFlashUnit = camera?.cameraInfo?.hasFlashUnit() == true
                        } catch (ex: Exception) {
                            Timber.e("manseeyy Use case binding failed", ex)
                        }
                        preview = previewUseCase
                    }, ContextCompat.getMainExecutor(context))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column {
                RecentImages { uri ->
                    enterTransition = slideInVertically(initialOffsetY = { it })
                    exitTransition = slideOutVertically(targetOffsetY = { it })
                    viewModel.flashOn = false
                    camera?.cameraControl?.enableTorch(false)
                    viewModel.selectedImageUri = uri
                    viewModel.isImageCaptured = true
                }
                BottomRow(
                    galleryIconClick = {
                        pickImageLauncher.launch("image/*")
                    },
                    cameraSwitchClick = {
                        viewModel.cameraSelector =
                            if (viewModel.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) CameraSelector.DEFAULT_BACK_CAMERA
                            else CameraSelector.DEFAULT_FRONT_CAMERA
                    },
                    imageCaptured = {
                        val photoFile = File(
                            context.filesDir,
                            "${Date().time}.jpg"
                        )

                        val outputOptions =
                            ImageCapture.OutputFileOptions.Builder(photoFile).build()
                        imageCapture?.takePicture(
                            outputOptions,
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                    viewModel.flashOn = false
                                    camera?.cameraControl?.enableTorch(false)
                                    enterTransition = fadeIn()
                                    exitTransition = fadeOut()
                                    viewModel.isImageCaptured = true
                                    viewModel.selectedImageUri =
                                        output.savedUri ?: Uri.fromFile(photoFile)
                                }

                                override fun onError(exc: ImageCaptureException) {
                                    onError(exc)
                                }
                            }
                        )
                    }
                )
            }
        }
    }
    AnimatedVisibility(
        visible = viewModel.isImageCaptured,
        enter = enterTransition,
        exit = exitTransition
    ) {
        DisplayImage(viewModel, navController)
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun DisplayImage(
    viewModel: PrescriptionPhotoUploadViewModel,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = {
                viewModel.isImageCaptured = false
                viewModel.selectedImageUri = null
            },
            modifier = Modifier
                .zIndex(2f)
                .padding(8.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
        }
        Image(
            painter = rememberImagePainter(viewModel.selectedImageUri),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black),
            contentScale = ContentScale.Fit
        )
        Button(
            onClick = {
                // save prescription
                viewModel.insertPrescription {
                    viewModel.isImageCaptured = false
                    viewModel.selectedImageUri = null
                    coroutineScope.launch {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            PATIENT,
                            viewModel.patient!!
                        )
                        navController.navigate(Screen.PrescriptionPhotoViewScreen.route)
                    }
                }
            },
            modifier = Modifier
                .zIndex(2f)
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text(text = stringResource(id = R.string.save))
        }
    }
}

@Composable
private fun TopRow(
    navController: NavController,
    flashOn: Boolean,
    hasFlashUnit: Boolean,
    updateFlashOn: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        IconButton(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = {
                updateFlashOn(!flashOn)
            },
            enabled = hasFlashUnit
        ) {
            Icon(
                if (flashOn) painterResource(id = R.drawable.flash_on)
                else painterResource(id = R.drawable.flash_off),
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun RecentImages(onImageClick: (Uri) -> Unit) {
    val context = LocalContext.current
    val images = remember { mutableStateListOf<Uri>() }

    LaunchedEffect(Unit) {
        val uriExternal: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor = context.contentResolver.query(
            uriExternal,
            null,
            null,
            null,
            "${MediaStore.Images.ImageColumns.DATE_TAKEN} DESC"
        )
        cursor?.use {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val uri = ContentUris.withAppendedId(uriExternal, cursor.getLong(columnIndex))
                images.add(uri)
            }
        }
    }

    LazyRow(
        modifier = Modifier
            .background(color = Color.Transparent),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(images) { uri ->
            Image(
                painter = rememberImagePainter(uri),
                contentDescription = null,
                modifier = Modifier
                    .size(85.dp)
                    .background(color = Color.Transparent)
                    .clickable { onImageClick(uri) },
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

@Composable
private fun BottomRow(
    galleryIconClick: () -> Unit,
    cameraSwitchClick: () -> Unit,
    imageCaptured: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconWithBackground(
            icon = painterResource(id = R.drawable.gallery_image),
            onClick = galleryIconClick
        )

        Box(
            modifier = Modifier
                .clickable {
                    imageCaptured()
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(89.dp)
                    .background(color = Color.Transparent, shape = CircleShape)
                    .border(width = 4.dp, color = Color.White, shape = CircleShape)
            )
            Box(
                modifier = Modifier
                    .padding(20.dp)
                    .size(60.dp)
                    .background(color = Color.White, shape = CircleShape)
            )
        }

        IconWithBackground(
            icon = painterResource(id = R.drawable.camera_switch),
            onClick = cameraSwitchClick
        )
    }
}

@Composable
private fun IconWithBackground(
    icon: Painter,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(30.dp)
                .size(48.dp)
                .alpha(0.5f)
                .background(color = Color.Black, shape = CircleShape)
        )
        IconButton(
            onClick = { onClick() }
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}
