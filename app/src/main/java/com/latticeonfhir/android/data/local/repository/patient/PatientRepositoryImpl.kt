package com.latticeonfhir.core.data.local.repository.patient

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import androidx.paging.map
import com.latticeonfhir.core.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.core.data.server.model.patient.PatientResponse
import com.latticeonfhir.core.utils.constants.Paging.PAGE_SIZE
import com.latticeonfhir.core.utils.converters.responseconverter.toPatientEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientResponse
import javax.inject.Inject

class PatientRepositoryImpl @Inject constructor(private val patientDao: PatientDao) :
    PatientRepository {

    override suspend fun addPatient(patientResponse: PatientResponse): List<Long> {
        return patientDao.insertPatientData(patientResponse.toPatientEntity())
    }

    override suspend fun getPatientList(): LiveData<PagingData<PatientResponse>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { patientDao.getListPatientData() }
        ).liveData.map { pagingData ->
            pagingData.map { patientAndIdentifierEntity ->
                patientAndIdentifierEntity.toPatientResponse()
            }
        }
    }

    override suspend fun updatePatientData(patientResponse: PatientResponse): Int {
        return patientDao.updatePatientData(patientResponse.toPatientEntity())
    }

    override suspend fun getPatientById(vararg patientId: String): List<PatientResponse> {
        return patientDao.getPatientDataById(*patientId).map { patientAndIdentifierEntity ->
            patientAndIdentifierEntity.toPatientResponse()
        }
    }
}