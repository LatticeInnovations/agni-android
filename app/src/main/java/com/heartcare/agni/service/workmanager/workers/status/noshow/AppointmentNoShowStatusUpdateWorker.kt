package com.heartcare.agni.service.workmanager.workers.status.noshow

import android.content.Context
import androidx.work.WorkerParameters
import com.heartcare.agni.FhirApp
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.enums.ChangeTypeEnum
import com.heartcare.agni.data.local.enums.GenericTypeEnum
import com.heartcare.agni.data.local.enums.SyncType
import com.heartcare.agni.data.local.model.patch.ChangeRequest
import com.heartcare.agni.data.local.roomdb.entities.appointment.AppointmentEntity
import com.heartcare.agni.data.local.roomdb.entities.generic.GenericEntity
import com.heartcare.agni.service.workmanager.workers.base.SyncWorker
import com.heartcare.agni.utils.builders.UUIDBuilder
import com.heartcare.agni.utils.constants.Id
import com.heartcare.agni.utils.converters.responseconverter.GsonConverters.fromJson
import com.heartcare.agni.utils.converters.responseconverter.GsonConverters.toJson
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.yesterday
import com.heartcare.agni.utils.converters.responseconverter.toAppointmentResponse
import java.util.Date

abstract class AppointmentNoShowStatusUpdateWorker(
    context: Context,
    workerParameters: WorkerParameters
) :
    SyncWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        // update status in appointment entity
        val appointmentDao = (applicationContext as FhirApp).fhirAppDatabase.getAppointmentDao()
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
        val genericDao = (applicationContext as FhirApp).fhirAppDatabase.getGenericDao()
        val scheduleDao = (applicationContext as FhirApp).fhirAppDatabase.getScheduleDao()
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
                                patientId = appointmentEntity.appointmentFhirId,
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
                                patientId = appointmentEntity.appointmentFhirId,
                                payload = map.toMutableMap().let { mutableMap ->
                                    mutableMap[Id.APPOINTMENT_ID] =
                                        appointmentEntity.appointmentFhirId
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