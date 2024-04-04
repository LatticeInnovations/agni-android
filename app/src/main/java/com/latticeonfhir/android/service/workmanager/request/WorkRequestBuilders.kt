package com.latticeonfhir.android.service.workmanager.request

import android.content.Context
import androidx.work.Constraints
import com.latticeonfhir.android.service.workmanager.utils.Delay
import com.latticeonfhir.android.service.workmanager.utils.InitialDelay
import com.latticeonfhir.android.service.workmanager.utils.PeriodicSyncConfiguration
import com.latticeonfhir.android.service.workmanager.utils.RepeatInterval
import com.latticeonfhir.android.service.workmanager.utils.Sync
import com.latticeonfhir.android.service.workmanager.workers.status.completed.AppointmentCompletedStatusUpdateWorkerImpl
import com.latticeonfhir.android.service.workmanager.workers.status.noshow.AppointmentNoShowStatusUpdateWorkerImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.time.Duration
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class WorkRequestBuilders(private val applicationContext: Context) {

    /**
     *
     * Periodic Worker that triggers
     * method to update status to "No-Show" at 11:59 PM everyday
     *
     */
    internal suspend fun setPeriodicAppointmentNoShowStatusUpdateWorker(
        duration: Duration?,
        delay: Delay?
    ) {
        Sync.periodicSync<AppointmentNoShowStatusUpdateWorkerImpl>(
            applicationContext,
            PeriodicSyncConfiguration(
                syncConstraints = Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build(),
                repeat = RepeatInterval(24, TimeUnit.HOURS),
                initialDelay = InitialDelay(duration, delay)
            )
        ).collect()
    }

    /**
     *
     * Periodic Worker that triggers
     * method to update status to "Completed" at 11:59 PM everyday
     *
     */
    internal suspend fun setPeriodicAppointmentCompletedStatusUpdateWorker(
        duration: Duration?,
        delay: Delay?
    ) {
        Sync.periodicSync<AppointmentCompletedStatusUpdateWorkerImpl>(
            applicationContext,
            PeriodicSyncConfiguration(
                syncConstraints = Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build(),
                repeat = RepeatInterval(24, TimeUnit.HOURS),
                initialDelay = InitialDelay(duration, delay)
            )
        ).collect()
    }
}