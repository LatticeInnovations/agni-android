package com.latticeonfhir.android.service.workmanager.workers.status.noshow

import android.content.Context
import androidx.work.WorkerParameters
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.service.workmanager.workers.base.SyncWorker
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.yesterday
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getAppointments
import org.hl7.fhir.r4.model.Appointment
import org.hl7.fhir.r4.model.Encounter
import java.util.Date

abstract class AppointmentNoShowStatusUpdateWorker(
    context: Context,
    workerParameters: WorkerParameters
) :
    SyncWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val fhirEngine = FhirApp.fhirEngine(applicationContext)
        fhirEngine.update(
            *getAppointments(
                fhirEngine,
                Date(Date().yesterday().toEndOfDay()),
                Encounter.EncounterStatus.PLANNED,
                Appointment.AppointmentStatus.PROPOSED
            ).map { searchResult ->
                (searchResult.included?.get(Encounter.APPOINTMENT.paramName)?.get(0) as Appointment).apply {
                    status = Appointment.AppointmentStatus.NOSHOW
                }
            }.toTypedArray()
        )
        return Result.success()
    }
}