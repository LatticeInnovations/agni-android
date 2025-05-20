package com.latticeonfhir.sync.workmanager.workmanager.workers.status.noshow

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.core.data.repository.server.sync.SyncRepository
import com.latticeonfhir.core.database.FhirAppDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class AppointmentNoShowStatusUpdateWorkerImpl @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val syncRepository: SyncRepository,
    fhirAppDatabase: FhirAppDatabase
) : AppointmentNoShowStatusUpdateWorker(context, workerParameters, syncRepository, fhirAppDatabase) {
    override fun getSyncRepository() = syncRepository
}