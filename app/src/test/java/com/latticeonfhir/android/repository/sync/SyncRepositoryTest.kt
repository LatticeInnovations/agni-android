package com.latticeonfhir.android.repository.sync

import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.dao.MedicationDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.dao.PrescriptionDao
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity
import com.latticeonfhir.android.data.server.api.PatientApiService
import com.latticeonfhir.android.data.server.api.PrescriptionApiService
import com.latticeonfhir.android.data.server.constants.ConstantValues
import com.latticeonfhir.android.data.server.constants.EndPoints.PATIENT
import com.latticeonfhir.android.data.server.constants.QueryParameters
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import com.latticeonfhir.android.data.server.repository.sync.SyncRepositoryImpl
import com.latticeonfhir.android.utils.ResponseHelper
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeStampDate
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.HttpsURLConnection

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class SyncRepositoryTest : BaseClass() {

    @Mock
    private lateinit var patientApiService: PatientApiService

    @Mock
    private lateinit var prescriptionApiService: PrescriptionApiService

    @Mock
    private lateinit var patientDao: PatientDao

    @Mock
    private lateinit var relationDao: RelationDao

    @Mock
    private lateinit var genericDao: GenericDao

    @Mock
    private lateinit var preferenceRepository: PreferenceRepository

    @Mock
    private lateinit var medicationDao: MedicationDao

    @Mock
    private lateinit var prescriptionDao: PrescriptionDao

    @Mock
    private lateinit var syncRepository: SyncRepository

    private lateinit var syncRepositoryImpl: SyncRepositoryImpl

    private lateinit var mockWebServer: MockWebServer

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)

        mockWebServer = MockWebServer()

        prescriptionApiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(FhirApp.gson))
            .build()
            .create(PrescriptionApiService::class.java)

        syncRepositoryImpl = SyncRepositoryImpl(
            patientApiService,
            prescriptionApiService,
            patientDao,
            genericDao,
            preferenceRepository,
            relationDao,
            medicationDao,
            prescriptionDao
        )

        runTest {
            `when`(preferenceRepository.getLastSyncPatient()).thenReturn(200L)

            `when`(patientDao.getPatientIdByFhirId("21028")).thenReturn("PATIENT_ID")

            `when`(
                patientDao.updateFhirId(
                    "78e2d936-39e4-42c3-abf4-b96274726c27",
                    "21292"
                )
            ).thenReturn(1)

            `when`(genericDao.deleteSyncPayload(listOf("ID"))).thenReturn(0)
        }
    }

    @Test
    internal fun getAndInsertPatientData_Returns_ListOfPatient() = runTest {
        patientApiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(FhirApp.gson))
            .build()
            .create(PatientApiService::class.java)

        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/patientResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)
        val response = syncRepositoryImpl.getAndInsertListPatientData(0)
        mockWebServer.takeRequest()
        assertEquals(
            (response as ApiEndResponse).body[0].id,
            "05d03012-d7f6-4fa7-919b-786573972f46"
        )
    }

    @Test
    internal fun getAndInsertPatientData_Returns_Error() = runTest {
        patientApiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(FhirApp.gson))
            .build()
            .create(PatientApiService::class.java)

        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/errorResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)
        val response = syncRepositoryImpl.getAndInsertListPatientData(0)
        mockWebServer.takeRequest()
        assertEquals((response as ApiErrorResponse).statusCode, 0)
    }

    @Test
    internal fun getAndInsertPatientData_Returns_Continue() = runTest {
        val map = mutableMapOf<String, String>()
        map[QueryParameters.COUNT] = ConstantValues.COUNT_VALUE.toString()
        map[QueryParameters.OFFSET] = 200.toString()
        map[QueryParameters.SORT] = "-${QueryParameters.ID}"
        if (preferenceRepository.getLastSyncPatient() != 0L) map[QueryParameters.LAST_UPDATED] =
            String.format(
                QueryParameters.GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncPatient().toTimeStampDate()
            )
        `when`(patientApiService.getListData(PATIENT,map)).thenReturn(Response.success(
            BaseResponse(
                status = 2,
                message = "Success",
                data = listOf(patientResponse),
                offset = null,
                total = null,
                error = null
            )
        ))

        val newmap = mutableMapOf<String, String>()
        newmap[QueryParameters.COUNT] = ConstantValues.COUNT_VALUE.toString()
        newmap[QueryParameters.OFFSET] = 0.toString()
        newmap[QueryParameters.SORT] = "-${QueryParameters.ID}"
        if (preferenceRepository.getLastSyncPatient() != 0L) map[QueryParameters.LAST_UPDATED] =
            String.format(
                QueryParameters.GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncPatient().toTimeStampDate()
            )
        `when`(patientApiService.getListData(PATIENT,newmap)).thenReturn(Response.success(
            BaseResponse(
                status = 1,
                message = "Success",
                data = listOf(patientResponse),
                offset = null,
                total = null,
                error = null
            )
        ))

        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/patientContinueResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = syncRepositoryImpl.getAndInsertListPatientData(0)
        mockWebServer.takeRequest()
        assertEquals(true, response is ApiEndResponse)
    }

    @Test
    internal fun getAndInsertPatientDataById_Returns_Patient() = runTest {
        patientApiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(FhirApp.gson))
            .build()
            .create(PatientApiService::class.java)

        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/patientResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)
        val response =
            syncRepositoryImpl.getAndInsertPatientDataById("05d03012-d7f6-4fa7-919b-786573972f46")
        mockWebServer.takeRequest()
        assertEquals(
            (response as ApiEndResponse).body[0].id,
            "05d03012-d7f6-4fa7-919b-786573972f46"
        )
    }

    @Test
    internal fun getAndInsertRelation_Returns_ListOfRelation() = runTest {
        `when`(patientDao.getPatientIdByFhirId("666")).thenReturn("ID")
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                GenericTypeEnum.FHIR_IDS,
                SyncType.POST,
                ConstantValues.COUNT_VALUE
            )
        ).thenReturn(
            listOf(
                GenericEntity(
                    id = "ID",
                    patientId = "PATIENT_ID",
                    payload = "FHIR_ID",
                    type = GenericTypeEnum.FHIR_IDS,
                    SyncType.POST
                )
            )
        )
        `when`(genericDao.deleteSyncPayload(listOf("666"))).thenReturn(0)
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/relationResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)
        val response = syncRepositoryImpl.getAndInsertRelation()
        mockWebServer.takeRequest()
        assertEquals((response is ApiEndResponse), true)
    }

    @Test
    internal fun getAndInsertRelation_Returns_Success() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                GenericTypeEnum.FHIR_IDS,
                SyncType.POST,
                ConstantValues.COUNT_VALUE
            )
        ).thenReturn(emptyList())
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/relationResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)
        val response = syncRepositoryImpl.getAndInsertRelation()
        assertEquals((response is ApiEmptyResponse), true)
    }

    @Test
    internal fun getAndInsertPrescription_Returns_Success() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/prescriptionResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)
        val response = syncRepositoryImpl.getAndInsertPrescription("FHIR_ID")
        mockWebServer.takeRequest()
        assertEquals(
            (response as ApiEndResponse).body[0].prescriptionId,
            "e3488798-ff88-4b67-88b3-3f7df487fc71"
        )
    }

    @Test
    internal fun getAndInsertPrescription_Returns_Error() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/errorResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)
        val response = syncRepositoryImpl.getAndInsertPrescription("FHIR_ID")
        mockWebServer.takeRequest()
        assertEquals((response is ApiErrorResponse), true)
    }

    @Test
    internal fun getAndInsertMedication_Returns_Success() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/medicationResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)
        val response = syncRepositoryImpl.getAndInsertMedication(0)
        mockWebServer.takeRequest()
        assertEquals((response as ApiEndResponse).body[0].medFhirId, "21111")
    }

    @Test
    internal fun getAndInsertMedication_Returns_Error() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/errorResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)
        val response = syncRepositoryImpl.getAndInsertMedication(0)
        mockWebServer.takeRequest()
        assertEquals((response is ApiErrorResponse), true)
    }

    @Test
    internal fun getMedicineTime_Returns_Success() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/medicationTimeResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)
        val response = syncRepositoryImpl.getMedicineTime()
        mockWebServer.takeRequest()
        assertEquals((response as ApiEndResponse).body[0].medInstructionCode, "307165006")
    }

    @Test
    internal fun getMedicineTime_Returns_Error() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/errorResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)
        val response = syncRepositoryImpl.getMedicineTime()
        mockWebServer.takeRequest()
        assertEquals((response is ApiErrorResponse), true)
    }

    @Test
    internal fun sendPersonPostData_Return_Success() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.PATIENT,
                syncType = SyncType.POST
            )
        ).thenReturn(emptyList())
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/createResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)
        val response = syncRepositoryImpl.sendPersonPostData()
        assertEquals(true, response is ApiEmptyResponse)
    }

    @Test
    internal fun sendPersonPostData_InteractServer_Return_Success() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.PATIENT,
                syncType = SyncType.POST
            )
        ).thenReturn(
            listOf(
                GenericEntity(
                    id = "ID",
                    patientId = "PATIENT_ID",
                    payload = "{\n" +
                            "            \"fhirId\": \"20212\",\n" +
                            "            \"firstName\": \"Natalie\",\n" +
                            "            \"lastName\": \"Wiggins\",\n" +
                            "            \"identifier\": [\n" +
                            "                {\n" +
                            "                    \"identifierType\": \"https://www.thelattice.in/\",\n" +
                            "                    \"identifierNumber\": \"05d03012-d7f6-4fa7-919b-786573972f46\",\n" +
                            "                    \"code\": \"MR\"\n" +
                            "                },\n" +
                            "                {\n" +
                            "                    \"identifierType\": \"https://www.passportindia.gov.in/\",\n" +
                            "                    \"identifierNumber\": \"247075737566\",\n" +
                            "                    \"code\": null\n" +
                            "                }\n" +
                            "            ],\n" +
                            "            \"id\": \"05d03012-d7f6-4fa7-919b-786573972f46\",\n" +
                            "            \"active\": true,\n" +
                            "            \"gender\": \"female\",\n" +
                            "            \"birthDate\": \"2021-03-09\",\n" +
                            "            \"mobileNumber\": \"9134145072\",\n" +
                            "            \"email\": \"nataliewiggins@accel.com\",\n" +
                            "            \"permanentAddress\": {\n" +
                            "                \"city\": \"Villarreal\",\n" +
                            "                \"state\": \"Uttarakhand\",\n" +
                            "                \"postalCode\": \"250012\",\n" +
                            "                \"country\": \"India\",\n" +
                            "                \"addressLine1\": \"Plot number: 609\",\n" +
                            "                \"addressLine2\": \"Desmond Court\"\n" +
                            "            },\n" +
                            "            \"tempAddress\": {\n" +
                            "                \"city\": \"Brookfield\",\n" +
                            "                \"state\": \"Andhra Pradesh\",\n" +
                            "                \"postalCode\": \"912156\",\n" +
                            "                \"country\": \"India\",\n" +
                            "                \"addressLine1\": \"Plot number: 282\",\n" +
                            "                \"addressLine2\": \"Canton Court\"\n" +
                            "            }\n" +
                            "        }",
                    GenericTypeEnum.PATIENT,
                    SyncType.POST
                )
            )
        )
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/createResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)
        val response = syncRepositoryImpl.sendPersonPostData()
        assertEquals(
            "78e2d936-39e4-42c3-abf4-b96274726c27",
            (response as ApiEndResponse).body[0].id
        )
    }

    @Test
    internal fun sendRelationPostData_Return_Success() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.RELATION,
                syncType = SyncType.POST
            )
        ).thenReturn(emptyList())
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/createResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)
        val response = syncRepositoryImpl.sendRelatedPersonPostData()
        assertEquals(true, response is ApiEmptyResponse)
    }

    @Test
    internal fun sendRelationPostData_InteractServer_Return_Success() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.RELATION,
                syncType = SyncType.POST
            )
        ).thenReturn(
            listOf(
                GenericEntity(
                    id = "ID",
                    patientId = "PATIENT_ID",
                    payload = "{\n" +
                            "      \"id\": \"666\",\n" +
                            "      \"relationship\": [\n" +
                            "        {\n" +
                            "          \"relativeId\": \"21028\",\n" +
                            "          \"patientIs\": \"BRO\"\n" +
                            "        }\n" +
                            "      ]\n" +
                            "    }",
                    GenericTypeEnum.RELATION,
                    SyncType.POST
                )
            )
        )
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/createResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)
        val response = syncRepositoryImpl.sendRelatedPersonPostData()
        assertEquals(
            "78e2d936-39e4-42c3-abf4-b96274726c27",
            (response as ApiEndResponse).body[0].id
        )
    }

    @Test
    internal fun sendPrescriptionPostData_Return_Success() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.PRESCRIPTION,
                syncType = SyncType.POST
            )
        ).thenReturn(emptyList())
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/createResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)
        val response = syncRepositoryImpl.sendPrescriptionPostData()
        assertEquals(true, response is ApiEmptyResponse)
    }

    @Test
    internal fun sendPrescriptionPostData_InteractServer_Return_Success() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.PRESCRIPTION,
                syncType = SyncType.POST
            )
        ).thenReturn(
            listOf(
                GenericEntity(
                    id = "ID",
                    patientId = "PATIENT_ID",
                    payload = "{\n" +
                            "      \"prescriptionId\": \"e3488798-ff88-4b67-88b3-3f7df487fc71\",\n" +
                            "      \"prescriptionFhirId\": \"21214\",\n" +
                            "      \"generatedOn\": \"2023-05-19T11:00:35+05:30\",\n" +
                            "      \"patientId\": \"21028\",\n" +
                            "      \"prescription\": [\n" +
                            "        {\n" +
                            "          \"medFhirId\": \"21117\",\n" +
                            "          \"note\": \"Swallow with water\",\n" +
                            "          \"qtyPerDose\": 1,\n" +
                            "          \"frequency\": 1,\n" +
                            "          \"doseForm\": \"Tablet\",\n" +
                            "          \"doseFormCode\": \"421026006\",\n" +
                            "          \"duration\": 3,\n" +
                            "          \"timing\": \"769557005\",\n" +
                            "          \"qtyPrescribed\": 3\n" +
                            "        },\n" +
                            "        {\n" +
                            "          \"medFhirId\": \"21131\",\n" +
                            "          \"note\": \"Swallow with water\",\n" +
                            "          \"qtyPerDose\": 2,\n" +
                            "          \"frequency\": 3,\n" +
                            "          \"doseForm\": \"Tablet\",\n" +
                            "          \"doseFormCode\": \"421026006\",\n" +
                            "          \"duration\": 3,\n" +
                            "          \"timing\": \"769557005\",\n" +
                            "          \"qtyPrescribed\": 18\n" +
                            "        }\n" +
                            "      ]\n" +
                            "    }",
                    GenericTypeEnum.PRESCRIPTION,
                    SyncType.POST
                )
            )
        )
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/createResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)
        val response = syncRepositoryImpl.sendPrescriptionPostData()
        assertEquals(
            "78e2d936-39e4-42c3-abf4-b96274726c27",
            (response as ApiEndResponse).body[0].id
        )
    }

    @After
    public override fun tearDown() {
        mockWebServer.shutdown()
    }
}