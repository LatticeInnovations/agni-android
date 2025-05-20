package com.latticeonfhir.sync.workmanager.workmanager.workers.trigger

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.core.data.repository.server.sync.SyncRepository
import com.latticeonfhir.core.model.enums.SyncStatusMessageEnum
import com.latticeonfhir.core.model.enums.WorkerStatus
import com.latticeonfhir.core.sharedpreference.preferencestorage.PreferenceStorage
import com.latticeonfhir.core.utils.network.CheckNetwork
import com.latticeonfhir.sync.workmanager.sync.SyncService
import com.latticeonfhir.sync.workmanager.workmanager.utils.EventBus.isSyncing
import com.latticeonfhir.sync.workmanager.workmanager.utils.EventBus.sessionExpireFlow
import com.latticeonfhir.sync.workmanager.workmanager.utils.EventBus.syncWorkerStatus
import com.latticeonfhir.sync.workmanager.workmanager.workers.base.SyncWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class TriggerWorkerPeriodic(
    context: Context,
    workerParameters: WorkerParameters,
    private val syncService: SyncService,
    syncRepository: SyncRepository,
    private val preferenceStorage: PreferenceStorage
) : SyncWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        if (isSyncing.compareAndSet(false, true)) {
            try {
                if (CheckNetwork.isInternetAvailable(applicationContext)) {
                    val listOfErrors = mutableListOf<String>()
                    syncWorkerStatus.postValue(WorkerStatus.IN_PROGRESS)
                    preferenceStorage.syncStatus = SyncStatusMessageEnum.SYNCING_IN_PROGRESS.display
                    syncService.syncLauncher { errorReceived, errorMessage ->
                        // as there will be multiple callbacks from different coroutines
                        // list of errors is maintained.
                        // if the list is empty, then all the api calls were successful.
                        listOfErrors.add(errorMessage)
                        CoroutineScope(Dispatchers.Main).launch {
                            sessionExpireFlow.postValue(
                                mapOf(
                                    Pair("errorReceived", errorReceived),
                                    Pair("errorMsg", errorMessage)
                                )
                            )
                        }
                    }.also {
//                        checkPhotoWorkerStatus(listOfErrors)
                    }
                }
            } finally {
                isSyncing.set(false)
            }
        }
        return Result.success()
    }
}