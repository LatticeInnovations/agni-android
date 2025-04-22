/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.latticeonfhir.core.service.workmanager.utils

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.latticeonfhir.core.service.workmanager.workers.base.SyncWorker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

object Sync {

    /**
     * Starts a one time sync job based on [SyncWorker].
     *
     * Use the returned [Flow] to get updates of the sync job. Alternatively, use [getWorkerInfo] with
     * the same [SyncWorker] to retrieve the status of the job.
     *
     * @param retryConfiguration configuration to guide the retry mechanism, or `null` to stop retry.
     * @return a [Flow] of [SyncJobStatus]
     */
    inline fun <reified W : SyncWorker> oneTimeSync(
        context: Context,
        retryConfiguration: RetryConfiguration? = defaultRetryConfiguration
    ): Flow<WorkInfo?> {
        val flow = getWorkerInfo<W>(context)
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                W::class.java.name,
                ExistingWorkPolicy.KEEP,
                createOneTimeWorkRequest(retryConfiguration, W::class.java)
            )
        return flow
    }

    /**
     * Starts a periodic sync job based on [SyncWorker].
     *
     * Use the returned [Flow] to get updates of the sync job. Alternatively, use [getWorkerInfo] with
     * the same [SyncWorker] to retrieve the status of the job.
     *
     * @param periodicSyncConfiguration configuration to determine the sync frequency and retry
     * mechanism
     * @return a [Flow] of [SyncJobStatus]
     */
    @ExperimentalCoroutinesApi
    inline fun <reified W : SyncWorker> periodicSync(
        context: Context,
        periodicSyncConfiguration: PeriodicSyncConfiguration
    ): Flow<WorkInfo?> {
        val flow = getWorkerInfo<W>(context)
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                W::class.java.name,
                ExistingPeriodicWorkPolicy.KEEP,
                createPeriodicWorkRequest(periodicSyncConfiguration, W::class.java)
            )
        return flow
    }

    /** Gets the worker info for the [SyncWorker] */
    inline fun <reified W : SyncWorker> getWorkerInfo(context: Context) =
        WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkLiveData(W::class.java.name)
            .map { it.lastOrNull() }
            .asFlow()
//      .flatMapConcat { it.asFlow() }
//      .mapNotNull { workInfo ->
//        workInfo.progress
//          .takeIf { it.keyValueMap.isNotEmpty() && it.hasKeyWithValueOfType<String>("StateType") }
//          ?.let {
//            val state = it.getString("StateType")!!
//            val stateData = it.getString("State")
//            gson.fromJson(stateData, Class.forName(state)) as SyncJobStatus
//          }
//      }

    @PublishedApi
    internal inline fun <W : SyncWorker> createOneTimeWorkRequest(
        retryConfiguration: RetryConfiguration?,
        clazz: Class<W>
    ): OneTimeWorkRequest {
        val oneTimeWorkRequestBuilder = OneTimeWorkRequest.Builder(clazz)
        retryConfiguration?.let {
            oneTimeWorkRequestBuilder.setBackoffCriteria(
                it.backoffCriteria.backoffPolicy,
                it.backoffCriteria.backoffDelay,
                it.backoffCriteria.timeUnit
            )
            oneTimeWorkRequestBuilder.setInputData(
                Data.Builder().putInt(MAX_RETRIES_ALLOWED, it.maxRetries).build()
            )
            oneTimeWorkRequestBuilder.setConstraints(
                it.syncConstraints
            )
        }
        return oneTimeWorkRequestBuilder.build()
    }

    @PublishedApi
    internal inline fun <W : SyncWorker> createPeriodicWorkRequest(
        periodicSyncConfiguration: PeriodicSyncConfiguration,
        clazz: Class<W>
    ): PeriodicWorkRequest {
        val periodicWorkRequestBuilder =
            PeriodicWorkRequest.Builder(
                clazz,
                periodicSyncConfiguration.repeat.interval,
                periodicSyncConfiguration.repeat.timeUnit
            )
                .setConstraints(periodicSyncConfiguration.syncConstraints)

        periodicSyncConfiguration.retryConfiguration?.let {
            periodicWorkRequestBuilder.setBackoffCriteria(
                it.backoffCriteria.backoffPolicy,
                it.backoffCriteria.backoffDelay,
                it.backoffCriteria.timeUnit
            )
            periodicWorkRequestBuilder.setInputData(
                Data.Builder().putInt(MAX_RETRIES_ALLOWED, it.maxRetries).build()
            )
        }
        periodicSyncConfiguration.initialDelay?.let { initialDelay ->
            initialDelay.duration?.let { duration ->
                periodicWorkRequestBuilder.setInitialDelay(
                    duration
                )
            } ?: initialDelay.delay?.let { delay ->
                periodicWorkRequestBuilder.setInitialDelay(
                    delay.initialDelay, delay.timeUnit
                )
            }
        }
        return periodicWorkRequestBuilder.build()
    }
}
