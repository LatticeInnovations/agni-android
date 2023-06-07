package com.latticeonfhir.android.repository

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.repository.identifier.IdentifierRepositoryImpl
import com.latticeonfhir.android.data.local.roomdb.dao.IdentifierDao
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfIdentifierEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class IdentifierRepositoryTest: BaseClass() {
    @Mock
    lateinit var identifierDao: IdentifierDao
    lateinit var identifierRepositoryImpl: IdentifierRepositoryImpl

    @Before
    public override fun setUp() {
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