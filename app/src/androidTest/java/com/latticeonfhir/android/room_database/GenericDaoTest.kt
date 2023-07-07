package com.latticeonfhir.android.room_database

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.toJson
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class GenericDaoTest : FhirAppDatabaseTest() {

    val genericId = UUIDBuilder.generateUUID()
    val genericEntity = GenericEntity(
        id = genericId,
        patientId = id,
        payload = patientResponse.toPatientEntity().toJson(),
        type = GenericTypeEnum.PATIENT,
        syncType = SyncType.POST
    )

    val relationGenericEntity = GenericEntity(
        id = UUIDBuilder.generateUUID(),
        patientId = id,
        payload = relationEntity.toJson(),
        type = GenericTypeEnum.RELATION,
        syncType = SyncType.POST
    )

    @Test
    fun insertGenericEntityTest() = runBlocking {
        val result = genericDao.insertGenericEntity(genericEntity)
        Assert.assertNotEquals("Generic entity not inserted.", -1, result)
    }

    @Test
    fun getGenericEntityByIdTest() = runBlocking {
        genericDao.insertGenericEntity(genericEntity)
        val result = genericDao.getGenericEntityById(id, GenericTypeEnum.PATIENT, SyncType.POST)

        Assert.assertEquals(
            "The requested generic entity of given id is not returned.",
            id,
            result?.patientId
        )
    }

    @Test
    fun getChangeRequestPayloadByIdTest() = runBlocking {
        genericDao.insertGenericEntity(genericEntity)
        val result = genericDao.getChangeRequestPayloadById(genericId)
        Assert.assertEquals("payload returned is not correct", genericEntity.payload, result)
    }

    @Test
    fun getSameTypeGenericEntityPayloadTest() = runBlocking {
        genericDao.insertGenericEntity(genericEntity)
        val result = genericDao.getSameTypeGenericEntityPayload(
            GenericTypeEnum.PATIENT, SyncType.POST
        )
        Assert.assertEquals("genericEntity returned not correct", listOf(genericEntity) , result)
    }

    @Test
    fun deleteSyncPayloadTest() = runBlocking{
        genericDao.insertGenericEntity(genericEntity)
        val result = genericDao.deleteSyncPayload(listOf(genericId))
        Assert.assertEquals("sync payload not deleted", 1 , result)
    }

    @Test
    fun getNotSyncedPostRelationTest() = runBlocking {
        genericDao.insertGenericEntity(relationGenericEntity)
        val result = genericDao.getNotSyncedData(GenericTypeEnum.RELATION)
        Assert.assertEquals("list of generic entity not returned correctly", listOf(relationGenericEntity), result)
    }
}