package com.latticeonfhir.android.repository

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.data.local.repository.generic.GenericRepositoryImpl
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.toJson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class GenericRepositoryTest : BaseClass() {

    @Mock
    lateinit var genericDao: GenericDao
    private lateinit var genericRepositoryImpl: GenericRepositoryImpl

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        genericRepositoryImpl = GenericRepositoryImpl(genericDao)
    }

    private val genericId = UUIDBuilder.generateUUID()

    private val genericEntityPost = GenericEntity(
        genericId,
        id,
        patientResponse.toJson(),
        GenericTypeEnum.PATIENT,
        SyncType.POST
    )

    private val relationGenericEntity = GenericEntity(
        genericId,
        id,
        relationResponse.toJson(),
        GenericTypeEnum.RELATION,
        SyncType.POST
    )

    private val updatedRelationGenericEntity = GenericEntity(
        genericId,
        id,
        updatedRelationResponse.toJson(),
        GenericTypeEnum.RELATION,
        SyncType.POST
    )

    private val prescriptionGenericEntity = GenericEntity(
        genericId,
        id,
        prescribedResponse.toJson(),
        GenericTypeEnum.PRESCRIPTION,
        SyncType.POST
    )

    private val genericEntityPatch = GenericEntity(
        genericId,
        patientResponse.fhirId!!,
        mapOf(
            Pair(
                "permanentAddress", ChangeRequest(
                    value = patientResponse,
                    operation = ChangeTypeEnum.REPLACE.value
                )
            ),
            Pair(
                "id", patientResponse.fhirId!!
            )
        ).toJson(),
        GenericTypeEnum.PATIENT,
        SyncType.PATCH
    )

    private val genericEntityPatchWithoutId = GenericEntity(
        genericId,
        patientResponse.fhirId!!,
        mapOf(
            Pair(
                "permanentAddress", ChangeRequest(
                    value = patientResponse,
                    operation = ChangeTypeEnum.REPLACE.value
                )
            )
        ).toJson(),
        GenericTypeEnum.PATIENT,
        SyncType.PATCH
    )

    private val genericEntityPatchAddOperation = GenericEntity(
        genericId,
        id,
        mapOf(
            Pair(
                "permanentAddress", ChangeRequest(
                    value = patientResponse,
                    operation = ChangeTypeEnum.ADD.value
                )
            ),
            Pair(
                "id", patientResponse.fhirId!!
            )
        ).toJson(),
        GenericTypeEnum.PATIENT,
        SyncType.PATCH
    )

//    @Test
//    fun `insert patient generic entity`() = runTest {
//        `when`(
//            genericDao.getGenericEntityById(
//                id,
//                GenericTypeEnum.PATIENT,
//                SyncType.POST
//            )
//        ).thenReturn(null)
//        `when`(genericDao.insertGenericEntity(genericEntityPost)).thenReturn(listOf(1))
//        val actual = genericRepositoryImpl.insertPatient(
//            patientResponse
//        )
//        Assert.assertEquals(1, actual)
//    }

    @Test
    fun `update patient generic entity`() = runTest {
        `when`(
            genericDao.getGenericEntityById(
                id,
                GenericTypeEnum.PATIENT,
                SyncType.POST
            )
        ).thenReturn(genericEntityPost)
        `when`(genericDao.insertGenericEntity(genericEntityPost)).thenReturn(listOf(1))
        val actual = genericRepositoryImpl.insertPatient(
            patientResponse
        )
        Assert.assertEquals(1, actual)
    }

    @Test
    fun `update relation generic entity`() = runTest {
        `when`(
            genericDao.getGenericEntityById(
                id,
                GenericTypeEnum.RELATION,
                SyncType.POST
            )
        ).thenReturn(relationGenericEntity)
        `when`(genericDao.insertGenericEntity(updatedRelationGenericEntity)).thenReturn(listOf(1))
        val actual = genericRepositoryImpl.insertRelation(
            patientResponse.id,
            newRelationResponse
        )
        Assert.assertEquals(1, actual)
    }

    @Test
    fun `update fhir Id in relation generic entity`() = runTest {
        `when`(genericDao.insertGenericEntity(relationGenericEntity)).thenReturn(listOf(1))
        val actual = genericRepositoryImpl.updateRelationFhirId(
            relationGenericEntity,
            relationResponse
        )
        Assert.assertEquals(1, actual)
    }

    @Test
    fun `update fhir Id in prescription generic entity`() = runTest {
        `when`(genericDao.insertGenericEntity(prescriptionGenericEntity)).thenReturn(listOf(1))
        val actual = genericRepositoryImpl.updatePrescriptionFhirId(
            prescriptionGenericEntity,
            prescribedResponse
        )
        Assert.assertEquals(1, actual)
    }

    @Test
    fun `update patch generic entity`() = runTest {

        `when`(
            genericDao.getGenericEntityById(
                patientResponse.fhirId!!,
                GenericTypeEnum.PATIENT,
                SyncType.PATCH
            )
        ).thenReturn(genericEntityPatch)
        `when`(genericDao.insertGenericEntity(genericEntityPatch)).thenReturn(listOf(-1L))


        val actual = genericRepositoryImpl.insertOrUpdatePatchEntity(
            patientFhirId = patientResponse.fhirId!!,
            map = mapOf(
                Pair(
                    "permanentAddress", ChangeRequest(
                        value = patientResponse,
                        operation = ChangeTypeEnum.REPLACE.value
                    )
                )
            ),
            typeEnum = GenericTypeEnum.PATIENT
        )

        assertEquals(-1L, actual)
    }

    @Test
    fun `insert patch generic entity`() = runTest {

        `when`(
            genericDao.getGenericEntityById(
                patientResponse.fhirId!!,
                GenericTypeEnum.PATIENT,
                SyncType.PATCH
            )
        ).thenReturn(null)
        `when`(genericDao.insertGenericEntity(genericEntityPatch)).thenReturn(listOf(-1L))

        val actual = genericRepositoryImpl.insertOrUpdatePatchEntity(
            patientFhirId = patientResponse.fhirId!!,
            map = mapOf(
                Pair(
                    "permanentAddress", ChangeRequest(
                        value = patientResponse,
                        operation = ChangeTypeEnum.REPLACE.value
                    )
                )
            ),
            typeEnum = GenericTypeEnum.PATIENT,
            uuid = genericId
        )

        assertEquals(-1L, actual)
    }

    @Test
    fun `update patch generic entity no id`() = runTest {

        `when`(
            genericDao.getGenericEntityById(
                patientResponse.fhirId!!,
                GenericTypeEnum.PATIENT,
                SyncType.PATCH
            )
        ).thenReturn(genericEntityPatchWithoutId)
        `when`(genericDao.insertGenericEntity(genericEntityPatch)).thenReturn(listOf(-1L))


        val actual = genericRepositoryImpl.insertOrUpdatePatchEntity(
            patientFhirId = patientResponse.fhirId!!,
            map = mapOf(
                Pair(
                    "permanentAddress", ChangeRequest(
                        value = patientResponse,
                        operation = ChangeTypeEnum.REPLACE.value
                    )
                )
            ),
            typeEnum = GenericTypeEnum.PATIENT
        )

        assertEquals(-1L, actual)
    }

    @Test
    fun `update patch generic entity remove operation`() = runTest {

        `when`(
            genericDao.getGenericEntityById(
                patientResponse.fhirId!!,
                GenericTypeEnum.PATIENT,
                SyncType.PATCH
            )
        ).thenReturn(genericEntityPatchAddOperation)
        `when`(genericDao.deleteSyncPayload(listOf(genericEntityPatchAddOperation.id))).thenReturn(1)


        val actual = genericRepositoryImpl.insertOrUpdatePatchEntity(
            patientFhirId = patientResponse.fhirId!!,
            map = mapOf(
                Pair(
                    "permanentAddress", ChangeRequest(
                        value = patientResponse,
                        operation = ChangeTypeEnum.REMOVE.value
                    )
                )
            ),
            typeEnum = GenericTypeEnum.PATIENT
        )

        assertEquals(1, actual)
    }

    //Identifier Tests

    @Test
    fun `remove identifier test`() = runTest {
        val addIdentifierEntity = GenericEntity(
            genericId,
            id,
            mapOf(
                Pair(
                    "identifier", listOf(
                        ChangeRequest(
                            key = "passport",
                            value = "passport",
                            operation = ChangeTypeEnum.ADD.value
                        )
                    )
                ),
                Pair(
                    "id", patientResponse.fhirId!!
                )
            ).toJson(),
            GenericTypeEnum.PATIENT,
            SyncType.PATCH
        )

        `when`(genericDao.deleteSyncPayload(listOf(addIdentifierEntity.id))).thenReturn(1)

        `when`(
            genericDao.getGenericEntityById(
                patientResponse.fhirId!!,
                GenericTypeEnum.PATIENT,
                SyncType.PATCH
            )
        ).thenReturn(addIdentifierEntity)

        val actual = genericRepositoryImpl.insertOrUpdatePatchEntity(
            patientFhirId = patientResponse.fhirId!!,
            map = mapOf(
                Pair(
                    "identifier", listOf(
                        ChangeRequest(
                            key = "passport",
                            value = "passport",
                            operation = ChangeTypeEnum.REMOVE.value
                        )
                    )
                )
            ),
            typeEnum = GenericTypeEnum.PATIENT
        )

        assertEquals(1, actual)
    }

    @Test
    fun `add identifier test`() = runTest {
        val addIdentifierEntity = GenericEntity(
            genericId,
            id,
            mapOf(
                Pair(
                    "identifier", listOf(
                        ChangeRequest(
                            key = "voterId",
                            value = "voterId",
                            operation = ChangeTypeEnum.REPLACE.value
                        ),
                        ChangeRequest(
                            key = "passport",
                            value = "passport",
                            operation = ChangeTypeEnum.ADD.value
                        )
                    )
                ),
                Pair(
                    "id", patientResponse.fhirId!!
                )
            ).toJson(),
            GenericTypeEnum.PATIENT,
            SyncType.PATCH
        )

        val replaceIdentifier = GenericEntity(
            genericId,
            id,
            mapOf(
                Pair(
                    "identifier", listOf(
                        ChangeRequest(
                            key = "voterId",
                            value = "voterId",
                            operation = ChangeTypeEnum.REPLACE.value
                        )
                    )
                ),
                Pair(
                    "id", patientResponse.fhirId!!
                )
            ).toJson(),
            GenericTypeEnum.PATIENT,
            SyncType.PATCH
        )

        `when`(
            genericDao.getGenericEntityById(
                patientResponse.fhirId!!,
                GenericTypeEnum.PATIENT,
                SyncType.PATCH
            )
        ).thenReturn(replaceIdentifier)

        `when`(genericDao.insertGenericEntity(addIdentifierEntity)).thenReturn(listOf(1))

        val actual = genericRepositoryImpl.insertOrUpdatePatchEntity(
            patientFhirId = patientResponse.fhirId!!,
            map = mapOf(
                Pair(
                    "identifier", listOf(
                        ChangeRequest(
                            key = "passport",
                            value = "passport",
                            operation = ChangeTypeEnum.ADD.value
                        )
                    )
                )
            ),
            typeEnum = GenericTypeEnum.PATIENT
        )

        assertEquals(1, actual)
    }

    @Test
    fun `get non synced post relation`() = runTest {
        `when`(genericDao.getNotSyncedData(GenericTypeEnum.RELATION)).thenReturn(listOf(genericEntityPost))
        assertEquals(listOf(genericEntityPost), genericRepositoryImpl.getNonSyncedPostRelations())
    }

}
