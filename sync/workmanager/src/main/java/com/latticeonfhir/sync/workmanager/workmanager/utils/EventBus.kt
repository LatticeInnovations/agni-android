package com.latticeonfhir.sync.workmanager.workmanager.utils

import androidx.lifecycle.MutableLiveData
import com.latticeonfhir.core.model.enums.WorkerStatus
import java.util.concurrent.atomic.AtomicBoolean

object EventBus {

    var syncWorkerStatus = MutableLiveData<WorkerStatus>()
    var photosWorkerStatus = MutableLiveData<WorkerStatus>()
    val sessionExpireFlow = MutableLiveData<Map<String, Any>>(emptyMap())
    val isSyncing = AtomicBoolean(false)

}