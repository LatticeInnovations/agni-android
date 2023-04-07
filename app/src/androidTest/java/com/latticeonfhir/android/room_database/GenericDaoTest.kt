package com.latticeonfhir.android.room_database

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.roomdb.entities.GenericEntity
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.toJson
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class GenericDaoTest: BaseClass() {

    @Test
    fun insertGenericEntityTest() = runBlocking{
        val result = genericDao.insertGenericEntity(
            GenericEntity(
                id = UUIDBuilder.generateUUID(),
                patientId = id,
                payload = patientResponse.toPatientEntity().toJson(),
                type = GenericTypeEnum.PATIENT,
                syncType = SyncType.POST
            )
        )
        Assert.assertNotEquals("Generic entity not inserted.", -1, result)
    }

    @Test
    fun getGenericEntityByIdTest() = runBlocking{
        genericDao.insertGenericEntity(
            GenericEntity(
                id = UUIDBuilder.generateUUID(),
                patientId = id,
                payload = patientResponse.toPatientEntity().toJson(),
                type = GenericTypeEnum.PATIENT,
                syncType = SyncType.POST
            )
        )
        val genericEntity = genericDao.getGenericEntityById(id, GenericTypeEnum.PATIENT, SyncType.POST)

        Assert.assertEquals("The requested generic entity of given id is not returned.", id, genericEntity?.patientId)
    }
}