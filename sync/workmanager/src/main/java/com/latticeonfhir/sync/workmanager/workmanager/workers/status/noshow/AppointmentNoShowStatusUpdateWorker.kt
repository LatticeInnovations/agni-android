package com.latticeonfhir.sync.workmanager.workmanager.workers.status.noshow

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.core.data.repository.server.sync.SyncRepository
import com.latticeonfhir.core.database.FhirAppDatabase
import com.latticeonfhir.core.database.entities.appointment.AppointmentEntity
import com.latticeonfhir.core.database.entities.generic.GenericEntity
import com.latticeonfhir.core.model.enums.AppointmentStatusEnum
import com.latticeonfhir.core.model.enums.ChangeTypeEnum
import com.latticeonfhir.core.model.enums.GenericTypeEnum
import com.latticeonfhir.core.model.enums.SyncType
import com.latticeonfhir.core.network.utils.responseconverter.toAppointmentResponse
import com.latticeonfhir.core.utils.builders.UUIDBuilder
import com.latticeonfhir.core.utils.constants.Id
import com.latticeonfhir.core.utils.converters.responseconverter.GsonConverters.fromJson
import com.latticeonfhir.core.utils.converters.responseconverter.GsonConverters.toJson
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.yesterday
import com.latticeonfhir.sync.workmanager.workmanager.workers.base.SyncWorker
import java.util.Date

abstract class AppointmentNoShowStatusUpdateWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val syncRepository: SyncRepository,
    private val fhirAppDatabase: FhirAppDatabase
) :
    SyncWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        // update status in appointment entity
        val appointmentDao = fhirAppDatabase.getAppointmentDao()
        appointmentDao.getTodayScheduledAppointments(
            status = AppointmentStatusEnum.SCHEDULED.value,
            endOfDay = Date().yesterday().toEndOfDay()
        ).let { scheduledAppointmentEntities ->
            scheduledAppointmentEntities.forEach { appointmentEntity ->
                appointmentDao.updateAppointmentEntity(
                    appointmentEntity.copy(
                        status = AppointmentStatusEnum.NO_SHOW.value
                    )
                ).also { response ->
                    if (response > 0) {
                        insertInGenericEntity(appointmentEntity)
                    }
                }
            }
        }
        return Result.success()
    }

    private suspend fun insertInGenericEntity(appointmentEntity: AppointmentEntity): Long {
        val genericDao = fhirAppDatabase.getGenericDao()
        val scheduleDao = fhirAppDatabase.getScheduleDao()
        return genericDao.getGenericEntityById(
            patientId = appointmentEntity.id,
            genericTypeEnum = GenericTypeEnum.APPOINTMENT,
            syncType = SyncType.POST
        ).let { appointmentGenericEntity ->
            // already existing post
            if (appointmentGenericEntity != null) {
                genericDao.insertGenericEntity(
                    appointmentGenericEntity.copy(
                        payload = appointmentEntity.copy(
                            status = AppointmentStatusEnum.NO_SHOW.value
                        ).toAppointmentResponse(scheduleDao).toJson()
                    )
                )[0]
            } else {
                // Already Existing Patch
                genericDao.getGenericEntityById(
                    appointmentEntity.appointmentFhirId!!,
                    GenericTypeEnum.APPOINTMENT,
                    SyncType.PATCH
                ).let { appointmentGenericPatchEntity ->
                    val map = mutableMapOf<String, Any>()
                    map["status"] = ChangeRequest(
                        operation = ChangeTypeEnum.REPLACE.value,
                        value = AppointmentStatusEnum.NO_SHOW.value
                    )
                    if (appointmentGenericPatchEntity != null) {
                        val existingMap =
                            appointmentGenericPatchEntity.payload.fromJson<MutableMap<String, Any>>()
                        map.entries.forEach { mapEntry ->
                            existingMap[mapEntry.key] = mapEntry.value
                        }
                        genericDao.insertGenericEntity(
                            GenericEntity(
                                id = appointmentGenericPatchEntity.id,
                                patientId = appointmentEntity.appointmentFhirId!!,
                                payload = existingMap.toJson(),
                                type = GenericTypeEnum.APPOINTMENT,
                                syncType = SyncType.PATCH
                            )
                        )[0]
                    } else {
                        // new patch
                        genericDao.insertGenericEntity(
                            GenericEntity(
                                id = UUIDBuilder.generateUUID(),
                                patientId = appointmentEntity.appointmentFhirId!!,
                                payload = map.toMutableMap().let { mutableMap ->
                                    mutableMap[Id.APPOINTMENT_ID] =
                                        appointmentEntity.appointmentFhirId!!
                                    mutableMap
                                }.toJson(),
                                type = GenericTypeEnum.APPOINTMENT,
                                syncType = SyncType.PATCH
                            )
                        )[0]
                    }
                }
            }
        }
    }
}