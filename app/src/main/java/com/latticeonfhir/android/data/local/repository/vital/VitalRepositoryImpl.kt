package com.latticeonfhir.android.data.local.repository.vital

import com.latticeonfhir.android.data.local.model.vital.VitalLocal
import com.latticeonfhir.android.data.local.roomdb.dao.VitalDao
import com.latticeonfhir.core.utils.converters.responseconverter.toVitalEntity
import com.latticeonfhir.core.utils.converters.responseconverter.toVitalLocal
import javax.inject.Inject

class VitalRepositoryImpl @Inject constructor(private val vitalDao: VitalDao) : VitalRepository {
    override suspend fun insertVital(vitalLocal: VitalLocal): Long {
        return vitalDao.insertVital(vitalLocal.toVitalEntity())[0]
    }

    override suspend fun getLastVital(patientId: String): List<VitalLocal> {
        return vitalDao.getPastVitals(patientId).map { it.toVitalLocal() }
    }

    override suspend fun getVitalByAppointmentId(appointmentId: String): List<VitalLocal> {
        return vitalDao.getVitalsByAppointmentId(appointmentId).map { it.toVitalLocal() }
    }

    override suspend fun updateVital(vitalLocal: VitalLocal): Int {
        return vitalDao.updateVitalData(vitalLocal.toVitalEntity())
    }

}