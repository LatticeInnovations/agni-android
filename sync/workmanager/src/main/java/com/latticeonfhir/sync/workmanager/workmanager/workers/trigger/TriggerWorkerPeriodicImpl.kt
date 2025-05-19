package com.latticeonfhir.sync.workmanager.workmanager.workers.trigger

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.core.data.repository.server.sync.SyncRepository
import com.latticeonfhir.core.sharedpreference.preferencestorage.PreferenceStorage
import com.latticeonfhir.sync.workmanager.sync.SyncService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class TriggerWorkerPeriodicImpl @AssistedInject constructor(@Assisted context: Context,@Assisted workerParameters: WorkerParameters,
                                syncService: SyncService, private val syncRepository: SyncRepository,
                                preferenceStorage: PreferenceStorage
) :
    TriggerWorkerPeriodic(context, workerParameters, syncService, syncRepository, preferenceStorage) {
    override fun getSyncRepository() = syncRepository
}