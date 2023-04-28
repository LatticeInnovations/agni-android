package com.latticeonfhir.android.repository

import com.latticeonfhir.android.data.local.repository.identifier.IdentifierRepositoryImpl
import com.latticeonfhir.android.data.local.roomdb.dao.IdentifierDao
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfIdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class IdentifierRepositoryTest: BaseClass() {
    @Mock
    lateinit var identifierDao: IdentifierDao
    lateinit var identifierRepositoryImpl: IdentifierRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        identifierRepositoryImpl = IdentifierRepositoryImpl(identifierDao)
    }

    @Test
    fun insertIdentifierListTest() = runBlocking {
        `when`(patientResponse.toListOfIdentifierEntity()
            ?.let { identifierDao.insertListOfIdentifier(it) }).thenReturn(Unit)

        val actual = identifierRepositoryImpl.insertIdentifierList(patientResponse)

        Assert.assertEquals(Unit, actual)
    }
}