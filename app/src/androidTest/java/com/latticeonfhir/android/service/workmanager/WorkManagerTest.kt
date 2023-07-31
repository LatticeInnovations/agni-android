package com.latticeonfhir.android.service.workmanager

import androidx.lifecycle.asLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.WorkInfo
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import com.latticeonfhir.android.service.workmanager.utils.PeriodicSyncConfiguration
import com.latticeonfhir.android.service.workmanager.utils.RepeatInterval
import com.latticeonfhir.android.service.workmanager.utils.Sync
import com.latticeonfhir.android.service.workmanager.utils.defaultRetryConfiguration
import com.latticeonfhir.android.service.workmanager.workers.download.appointment.AppointmentDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.medication.MedicationDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.medicinedosage.MedicineDosageDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.patient.PatientDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.prescription.PrescriptionDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.relation.RelationDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.download.schedule.ScheduleDownloadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.appointment.patch.AppointmentPatchUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.appointment.post.AppointmentUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.patch.PatientPatchUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.patient.post.PatientUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.prescription.PrescriptionUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.relation.patch.RelationPatchUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.relation.post.RelationUploadSyncWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.upload.schedule.ScheduleUploadSyncWorkerImpl
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import junit.framework.TestCase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

// TODO: update tests according to one time sync
@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class WorkManagerTest : TestCase() {

    @Mock
    private lateinit var syncRepository: SyncRepository

    private val countDownLatch = CountDownLatch(1)

    private val applicationContext = ApplicationProvider.getApplicationContext<FhirApp>()

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        FhirApp.syncRepository = syncRepository
    }

    @Test
    fun uploadPatientWorker() = runTest {

        `when`(syncRepository.sendPersonPostData()).thenReturn(ApiEmptyResponse())

        var result = WorkInfo.State.RUNNING

        Sync.oneTimeSync<PatientUploadSyncWorkerImpl>(
            applicationContext, defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true)
                    .build()
            )
        ).asLiveData().let {
            GlobalScope.launch(Dispatchers.Main) {
                it.observeForever { workInfo ->
                    if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                        result = WorkInfo.State.SUCCEEDED
                        countDownLatch.countDown()
                    }
                }
            }
        }
        countDownLatch.await()
        assertEquals(WorkInfo.State.SUCCEEDED, result)
    }

    @Test
    fun uploadRelationWorker() = runTest {
        `when`(syncRepository.sendRelatedPersonPostData()).thenReturn(ApiEmptyResponse())

        var result = WorkInfo.State.RUNNING

        Sync.oneTimeSync<RelationUploadSyncWorkerImpl>(
            applicationContext, defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true)
                    .build()
            )
        ).asLiveData().let {
            GlobalScope.launch(Dispatchers.Main) {
                it.observeForever { workInfo ->
                    if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                        result = workInfo.state
                        countDownLatch.countDown()
                    }
                }
            }
        }
        countDownLatch.await()
        assertEquals(WorkInfo.State.SUCCEEDED, result)
    }

    @Test
    fun uploadPrescriptionWorker() = runTest {
        `when`(syncRepository.sendPrescriptionPostData()).thenReturn(ApiEmptyResponse())

        var result = WorkInfo.State.RUNNING

        Sync.oneTimeSync<PrescriptionUploadSyncWorkerImpl>(
            applicationContext, defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true)
                    .build()
            )
        ).asLiveData().let {
            GlobalScope.launch(Dispatchers.Main) {
                it.observeForever { workInfo ->
                    if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                        result = workInfo.state
                        countDownLatch.countDown()
                    }
                }
            }
        }
        countDownLatch.await()
        assertEquals(WorkInfo.State.SUCCEEDED, result)
    }

    @Test
    fun uploadScheduleWorker() = runTest {

        `when`(syncRepository.sendSchedulePostData()).thenReturn(ApiEmptyResponse())

        var result = WorkInfo.State.RUNNING

        Sync.oneTimeSync<ScheduleUploadSyncWorkerImpl>(
            applicationContext, defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true)
                    .build()
            )
        ).asLiveData().let {
            GlobalScope.launch(Dispatchers.Main) {
                it.observeForever { workInfo ->
                    if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                        result = workInfo.state
                        countDownLatch.countDown()
                    }
                }
            }
        }
        countDownLatch.await()
        assertEquals(WorkInfo.State.SUCCEEDED, result)
    }

    @Test
    fun uploadAppointmentWorker() = runTest {
        `when`(syncRepository.sendAppointmentPostData()).thenReturn(ApiEmptyResponse())

        var result = WorkInfo.State.RUNNING

        Sync.oneTimeSync<AppointmentUploadSyncWorkerImpl>(
            applicationContext, defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true)
                    .build()
            )
        ).asLiveData().let {
            GlobalScope.launch(Dispatchers.Main) {
                it.observeForever { workInfo ->
                    if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                        result = workInfo.state
                        countDownLatch.countDown()
                    }
                }
            }
        }
        countDownLatch.await()
        assertEquals(WorkInfo.State.SUCCEEDED, result)
    }

    @Test
    fun downloadPatientWorker() = runTest {

        `when`(syncRepository.getAndInsertListPatientData(0)).thenReturn(ApiEndResponse(body = listOf()))

        var result = WorkInfo.State.RUNNING

        Sync.oneTimeSync<PatientDownloadSyncWorkerImpl>(
            applicationContext,
            defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).asLiveData().let {
            GlobalScope.launch(Dispatchers.Main) {
                it.observeForever { workInfo ->
                    if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                        result = workInfo.state
                        countDownLatch.countDown()
                    }
                }
            }
        }
        countDownLatch.await()
        assertEquals(WorkInfo.State.SUCCEEDED, result)
    }

    @Test
    fun downloadRelationWorker() = runTest {

        `when`(syncRepository.getAndInsertRelation()).thenReturn(ApiEndResponse(body = listOf()))

        var result = WorkInfo.State.RUNNING

        Sync.oneTimeSync<RelationDownloadSyncWorkerImpl>(
            applicationContext,
            defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).asLiveData().let {
            GlobalScope.launch(Dispatchers.Main) {
                it.observeForever { workInfo ->
                    if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                        result = workInfo.state
                        countDownLatch.countDown()
                    }
                }
            }
        }
        countDownLatch.await()
        assertEquals(WorkInfo.State.SUCCEEDED, result)
    }

    @Test
    fun downloadPrescriptionWorker() = runTest {

        `when`(syncRepository.getAndInsertPrescription("")).thenReturn(ApiEndResponse(body = listOf()))

        var result = WorkInfo.State.RUNNING

        Sync.oneTimeSync<PrescriptionDownloadSyncWorkerImpl>(
            applicationContext,
            defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).asLiveData().let {
            GlobalScope.launch(Dispatchers.Main) {
                it.observeForever { workInfo ->
                    if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                        result = workInfo.state
                        countDownLatch.countDown()
                    }
                }
            }
        }
        countDownLatch.await()
        assertEquals(WorkInfo.State.SUCCEEDED, result)
    }

    @Test
    fun downloadMedicationWorker() = runTest {

        `when`(syncRepository.getAndInsertMedication(0)).thenReturn(ApiEndResponse(body = listOf()))

        var result = WorkInfo.State.RUNNING

        Sync.oneTimeSync<MedicationDownloadSyncWorkerImpl>(
            applicationContext,
            defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).asLiveData().let {
            GlobalScope.launch(Dispatchers.Main) {
                it.observeForever { workInfo ->
                    if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                        result = workInfo.state
                        countDownLatch.countDown()
                    }
                }
            }
        }
        countDownLatch.await()
        assertEquals(WorkInfo.State.SUCCEEDED, result)
    }

    @Test
    fun downloadMedicineTimingWorker() = runTest {

        `when`(syncRepository.getMedicineTime()).thenReturn(ApiEndResponse(body = listOf()))

        var result = WorkInfo.State.RUNNING

        Sync.oneTimeSync<MedicineDosageDownloadSyncWorkerImpl>(
            applicationContext,
            defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).asLiveData().let {
            GlobalScope.launch(Dispatchers.Main) {
                it.observeForever { workInfo ->
                    if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                        result = workInfo.state
                        countDownLatch.countDown()
                    }
                }
            }
        }
        countDownLatch.await()
        assertEquals(WorkInfo.State.SUCCEEDED, result)
    }

    @Test
    fun downloadScheduleWorker() = runTest {

        `when`(syncRepository.getAndInsertSchedule(0)).thenReturn(ApiEndResponse(body = listOf()))

        var result = WorkInfo.State.RUNNING

        Sync.oneTimeSync<ScheduleDownloadSyncWorkerImpl>(
            applicationContext,
            defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).asLiveData().let {
            GlobalScope.launch(Dispatchers.Main) {
                it.observeForever { workInfo ->
                    if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                        result = workInfo.state
                        countDownLatch.countDown()
                    }
                }
            }
        }
        countDownLatch.await()
        assertEquals(WorkInfo.State.SUCCEEDED, result)
    }

    @Test
    fun downloadAppointmentWorker() = runTest {

        `when`(syncRepository.getAndInsertAppointment(0)).thenReturn(ApiEndResponse(body = listOf()))

        var result = WorkInfo.State.RUNNING

        Sync.oneTimeSync<AppointmentDownloadSyncWorkerImpl>(
            applicationContext,
            defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).asLiveData().let {
            GlobalScope.launch(Dispatchers.Main) {
                it.observeForever { workInfo ->
                    if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                        result = workInfo.state
                        countDownLatch.countDown()
                    }
                }
            }
        }
        countDownLatch.await()
        assertEquals(WorkInfo.State.SUCCEEDED, result)
    }

    @Test
    fun uploadPatientPatchWorker() = runTest {

        `when`(syncRepository.sendPersonPatchData()).thenReturn(ApiEmptyResponse())

        var result = WorkInfo.State.RUNNING

        Sync.oneTimeSync<PatientPatchUploadSyncWorkerImpl>(
            applicationContext,
            defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).asLiveData().let {
            GlobalScope.launch(Dispatchers.Main) {
                it.observeForever { workInfo ->
                    if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                        result = WorkInfo.State.SUCCEEDED
                        countDownLatch.countDown()
                    }
                }
            }
        }
        countDownLatch.await()
        assertEquals(WorkInfo.State.SUCCEEDED, result)
    }

    @Test
    fun uploadRelationPatchWorker() = runTest {

        `when`(syncRepository.sendRelatedPersonPatchData()).thenReturn(ApiEmptyResponse())

        var result = WorkInfo.State.RUNNING

        Sync.oneTimeSync<RelationPatchUploadSyncWorkerImpl>(
            applicationContext,
            defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).asLiveData().let {
            GlobalScope.launch(Dispatchers.Main) {
                it.observeForever { workInfo ->
                    if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                        result = WorkInfo.State.SUCCEEDED
                        countDownLatch.countDown()
                    }
                }
            }
        }
        countDownLatch.await()
        assertEquals(WorkInfo.State.SUCCEEDED, result)
    }

    @Test
    fun uploadAppointmentPatchWorker() = runTest {

        `when`(syncRepository.sendAppointmentPatchData()).thenReturn(ApiEmptyResponse())

        var result = WorkInfo.State.RUNNING

        Sync.oneTimeSync<AppointmentPatchUploadSyncWorkerImpl>(
            applicationContext,
            defaultRetryConfiguration.copy(
                syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        ).asLiveData().let {
            GlobalScope.launch(Dispatchers.Main) {
                it.observeForever { workInfo ->
                    if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                        result = WorkInfo.State.SUCCEEDED
                        countDownLatch.countDown()
                    }
                }
            }
        }
        countDownLatch.await()
        assertEquals(WorkInfo.State.SUCCEEDED, result)
    }
}