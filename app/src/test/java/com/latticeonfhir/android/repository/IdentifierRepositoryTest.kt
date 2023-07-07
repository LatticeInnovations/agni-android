package com.latticeonfhir.android.repository

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.repository.identifier.IdentifierRepositoryImpl
import com.latticeonfhir.android.data.local.roomdb.dao.IdentifierDao
import com.latticeonfhir.android.utils.converters.responseconverter.toIdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfIdentifierEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
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
    fun insertIdentifierListTest() = runTest {
        `when`(patientResponse.toListOfIdentifierEntity()
            .let { identifierDao.insertListOfIdentifier(it) }).thenReturn(Unit)

        val actual = identifierRepositoryImpl.insertIdentifierList(patientResponse)

        Assert.assertEquals(Unit, actual)
    }

    @Test
    fun `delete identifier test`() = runTest {
        val patientIdentifier = listOf(patientIdentifier)
        `when`(identifierDao.deleteIdentifier(*patientIdentifier.map { identifier ->
            identifier.toIdentifierEntity(patientResponse.id)
        }.toTypedArray())).thenReturn(1)

        assertEquals(
            Unit,
            identifierRepositoryImpl.deleteIdentifier(
                *patientIdentifier.toTypedArray(),
                patientId = patientResponse.id
            )
        )
    }
}