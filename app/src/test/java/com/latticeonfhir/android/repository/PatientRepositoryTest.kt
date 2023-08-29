package com.latticeonfhir.android.repository

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.repository.patient.PatientRepositoryImpl
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientAndIdentifierEntityResponse
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class PatientRepositoryTest: BaseClass() {
    @Mock
    private lateinit var patientDao: PatientDao
    lateinit var patientRepositoryImpl: PatientRepositoryImpl

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        patientRepositoryImpl = PatientRepositoryImpl(patientDao)
    }

    @Test
    fun addPatientTest() = runBlocking{
        `when`(patientDao.insertPatientData(patientResponse.toPatientEntity())).thenReturn(listOf<Long>(-1))
        val actual = patientRepositoryImpl.addPatient(patientResponse)
        Assert.assertEquals(listOf<Long>(-1), actual)
    }

    @Test
    fun updatePatientDataTest() = runBlocking{
        `when`(patientDao.updatePatientData(patientResponse.toPatientEntity())).thenReturn(1)
        val actual = patientRepositoryImpl.updatePatientData(patientResponse)
        Assert.assertEquals(1, actual)
    }

    @Test
    fun getPatientByIdTest() = runBlocking{
        `when`(patientDao.getPatientDataById(id)).thenReturn(listOf(patientResponse.toPatientAndIdentifierEntityResponse()))
        val actualUser = patientRepositoryImpl.getPatientById(id)
        Assert.assertEquals(listOf(patientResponse), actualUser)
    }
}