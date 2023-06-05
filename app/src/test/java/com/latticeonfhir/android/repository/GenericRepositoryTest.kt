package com.latticeonfhir.android.repository

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.repository.generic.GenericRepositoryImpl
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.toJson
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class GenericRepositoryTest: BaseClass() {

    @Mock
    lateinit var genericDao: GenericDao
    lateinit var genericRepositoryImpl: GenericRepositoryImpl

    @Before
    public override fun setUp() {
        MockitoAnnotations.initMocks(this)
        genericRepositoryImpl = GenericRepositoryImpl(genericDao)
    }

    val genericId = UUIDBuilder.generateUUID()
    val genericEntityPost = GenericEntity(genericId, id, patientResponse.toJson(), GenericTypeEnum.PATIENT, SyncType.POST)
    val genericEntityPatch = GenericEntity(genericId, id, patientResponse.toJson(), GenericTypeEnum.PATIENT, SyncType.PATCH)

    @Test
    fun insertOrUpdatePostEntityTest() = runBlocking {
        `when`(genericDao.getGenericEntityById(id, GenericTypeEnum.PATIENT, SyncType.POST)).thenReturn(genericEntityPost)
        `when`(genericDao.insertGenericEntity(genericEntityPost)).thenReturn(listOf(-1))
        val actual = genericRepositoryImpl.insertOrUpdatePostEntity(id, patientResponse, GenericTypeEnum.PATIENT)
        Assert.assertEquals(-1, actual)
    }

//    @Test
//    fun insertOrUpdatePatchEntityTest() = runBlocking{
//        `when`(genericDao.getGenericEntityById(id, GenericTypeEnum.PATIENT, SyncType.PATCH)).thenReturn(genericEntityPatch)
//        `when`(genericDao.insertGenericEntity(genericEntityPatch)).thenReturn(-1)
//        val actual = genericRepositoryImpl.insertOrUpdatePatchEntity(id, patientResponse.json, GenericTypeEnum.PATIENT)
//        Assert.assertEquals(-1, actual)
//    }
}