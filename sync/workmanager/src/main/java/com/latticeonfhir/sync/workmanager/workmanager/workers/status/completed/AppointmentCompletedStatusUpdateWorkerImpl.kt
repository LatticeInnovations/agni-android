package com.latticeonfhir.sync.workmanager.workmanager.workers.status.completed

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.core.data.repository.server.sync.SyncRepository
import com.latticeonfhir.core.database.FhirAppDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class AppointmentCompletedStatusUpdateWorkerImpl @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val syncRepository: SyncRepository,
    fhirAppDatabase: FhirAppDatabase
) : AppointmentCompletedStatusUpdateWorker(context, workerParameters, syncRepository, fhirAppDatabase) {
    override fun getSyncRepository() = syncRepository
}