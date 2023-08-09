package com.latticeonfhir.android.repository.sync

import com.google.gson.internal.LinkedTreeMap
import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.roomdb.dao.AppointmentDao
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.dao.MedicationDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.dao.PrescriptionDao
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.dao.ScheduleDao
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity
import com.latticeonfhir.android.data.server.api.PatientApiService
import com.latticeonfhir.android.data.server.api.PrescriptionApiService
import com.latticeonfhir.android.data.server.api.ScheduleAndAppointmentApiService
import com.latticeonfhir.android.data.server.constants.ConstantValues
import com.latticeonfhir.android.data.server.constants.EndPoints
import com.latticeonfhir.android.data.server.constants.EndPoints.PATIENT
import com.latticeonfhir.android.data.server.constants.EndPoints.RELATED_PERSON
import com.latticeonfhir.android.data.server.constants.QueryParameters
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.android.data.server.repository.sync.SyncRepositoryImpl
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.fromJson
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.mapToObject
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.toJson
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeStampDate
import com.latticeonfhir.android.utils.converters.responseconverter.toNoBracketAndNoSpaceString
import com.latticeonfhir.android.utils.converters.responseconverter.toScheduleEntity
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class SyncRepositoryTest : BaseClass() {

    @Mock
    private lateinit var patientApiService: PatientApiService

    @Mock
    private lateinit var prescriptionApiService: PrescriptionApiService

    @Mock
    private lateinit var scheduleAndAppointmentApiService: ScheduleAndAppointmentApiService

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
    private lateinit var scheduleDao: ScheduleDao

    @Mock
    private lateinit var appointmentDao: AppointmentDao

    private lateinit var syncRepositoryImpl: SyncRepositoryImpl

    private val listOfGenericEntity = listOf(
        GenericEntity(
            id = "ID",
            patientId = "PATIENT_ID",
            payload = "FHIR_ID",
            type = GenericTypeEnum.FHIR_IDS,
            SyncType.POST
        )
    )

    private val listOfRelationEntity = listOf(
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

    private val listOfPersonEntity = listOf(
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

    private val listOfPrescriptionEntity = listOf(
        GenericEntity(
            id = "ID",
            patientId = "PATIENT_ID",
            payload = "{\"generatedOn\":\"2023-07-18T15:22:50+05:30\",\"patientId\":\"23058\",\"prescription\":[{\"doseForm\":\"Tablet\",\"duration\":4,\"frequency\":2,\"medFhirId\":\"21132\",\"note\":\"\",\"qtyPerDose\":1,\"qtyPrescribed\":8,\"timing\":\"Before lunch\"}],\"prescriptionId\":\"0ebbef05-d0d7-4c54-9f58-6900d01c54ac\"}",
            GenericTypeEnum.PRESCRIPTION,
            SyncType.POST
        )
    )

    private val listOfScheduleEntity = listOf(
        GenericEntity(
            id = "ID",
            patientId = "SCHEDULE_ID",
            payload = "{\"uuid\":\"78e2d936-39e4-42c3-abf4-b96274726c27\",\"scheduleId\":\"23058\",\"planningHorizon\":{\"start\":\"2023-07-18T09:00:00+05:30\",\"end\":\"2023-07-18T09:30:00+05:30\"},\"orgId\":\"26722\",\"bookedSlots\":3}",
            GenericTypeEnum.SCHEDULE,
            SyncType.POST
        )
    )

    private val listOfAppointmentEntity = listOf(
        GenericEntity(
            id = "ID",
            patientId = "APPOINTMENT_ID",
            payload = "{\"appointmentId\":\"89767\",\"uuid\":\"78e2d936-39e4-42c3-abf4-b96274726c27\",\"patientFhirId\":\"23343\",\"scheduleId\":\"23058\",\"slot\":{\"start\":\"2023-07-18T09:00:00+05:30\",\"end\":\"2023-07-18T09:30:00+05:30\"},\"orgId\":\"26722\",\"createdOn\":\"2023-07-18T09:30:00+05:30\",\"status\":\"scheduled\"}",
            GenericTypeEnum.APPOINTMENT,
            SyncType.POST
        )
    )

    private val listOfPatchEntity = listOf(
        GenericEntity(
            "ID",
            id,
            mapOf(
                Pair(
                    "permanentAddress", ChangeRequest(
                        value = patientResponse.permanentAddress,
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
    )

    private val listOfPatchRelatedPersonEntity = listOf(
        GenericEntity(
            "ID",
            id,
            mapOf(
                Pair(
                    "related", ChangeRequest(
                        value = patientResponse.permanentAddress,
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
    )

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)

        syncRepositoryImpl = SyncRepositoryImpl(
            patientApiService,
            prescriptionApiService,
            scheduleAndAppointmentApiService,
            patientDao,
            genericDao,
            preferenceRepository,
            relationDao,
            medicationDao,
            prescriptionDao,
            scheduleDao,
            appointmentDao
        )

        runTest {
            `when`(preferenceRepository.getLastSyncPatient()).thenReturn(200L)
            `when`(preferenceRepository.getLastMedicationSyncDate()).thenReturn(200L)
            `when`(preferenceRepository.getLastMedicineDosageInstructionSyncDate()).thenReturn(200L)
            `when`(preferenceRepository.getLastSyncSchedule()).thenReturn(200L)
            `when`(preferenceRepository.getLastSyncAppointment()).thenReturn(200L)

            `when`(patientDao.getPatientIdByFhirId("FHIR_ID")).thenReturn("PATIENT_ID")
            `when`(appointmentDao.getAppointmentByFhirId("APPOINTMENT_ID")).thenReturn("APPOINTMENT_ID")

            `when`(
                patientDao.updateFhirId(
                    "78e2d936-39e4-42c3-abf4-b96274726c27",
                    "21292"
                )
            ).thenReturn(1)

            `when`(genericDao.deleteSyncPayload(listOf("ID"))).thenReturn(0)
            `when`(patientDao.getPatientIdByFhirId(appointmentResponse.patientFhirId!!)).thenReturn("PATIENT_ID")
            `when`(scheduleDao.getScheduleByStartTime(appointmentResponseLocal.scheduleId.time)).thenReturn(scheduleResponse.toScheduleEntity())
            `when`(scheduleDao.getScheduleStartTimeByFhirId(scheduleResponse.scheduleId!!)).thenReturn(scheduleResponse.planningHorizon.start)
        }
    }

    @Test
    internal fun getAndInsertPatientData_Returns_ListOfPatient() = runTest {
        val map = mutableMapOf<String, String>()
        map[QueryParameters.COUNT] = ConstantValues.COUNT_VALUE.toString()
        map[QueryParameters.OFFSET] = 0.toString()
        map[QueryParameters.SORT] = "-${QueryParameters.ID}"
        if (preferenceRepository.getLastSyncPatient() != 0L) map[QueryParameters.LAST_UPDATED] =
            String.format(
                QueryParameters.GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncPatient().toTimeStampDate()
            )
        `when`(patientApiService.getListData(PATIENT, map)).thenReturn(
            Response.success(
                BaseResponse(
                    status = 2,
                    message = "Success",
                    data = listOf(patientResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )

        val response = syncRepositoryImpl.getAndInsertListPatientData(0)
        assertEquals(
            (response as ApiEndResponse).body[0].firstName,
            "Test"
        )
    }

    @Test
    internal fun getAndInsertPatientData_Returns_Error() = runTest {
        val map = mutableMapOf<String, String>()
        map[QueryParameters.COUNT] = ConstantValues.COUNT_VALUE.toString()
        map[QueryParameters.OFFSET] = 0.toString()
        map[QueryParameters.SORT] = "-${QueryParameters.ID}"
        if (preferenceRepository.getLastSyncPatient() != 0L) map[QueryParameters.LAST_UPDATED] =
            String.format(
                QueryParameters.GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncPatient().toTimeStampDate()
            )
        `when`(patientApiService.getListData(PATIENT, map)).thenReturn(
            Response.success(
                BaseResponse(
                    status = 0,
                    message = "Error",
                    data = null,
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )
        val response = syncRepositoryImpl.getAndInsertListPatientData(0)
        assertEquals((response as ApiErrorResponse).statusCode, 0)
    }

    @Test
    internal fun getAndInsertPatientData_Returns_Continue() = runTest {
        val oldMap = mutableMapOf<String, String>()
        oldMap[QueryParameters.COUNT] = ConstantValues.COUNT_VALUE.toString()
        oldMap[QueryParameters.OFFSET] = 0.toString()
        oldMap[QueryParameters.SORT] = "-${QueryParameters.ID}"
        if (preferenceRepository.getLastSyncPatient() != 0L) oldMap[QueryParameters.LAST_UPDATED] =
            String.format(
                QueryParameters.GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncPatient().toTimeStampDate()
            )
        `when`(patientApiService.getListData(PATIENT, oldMap)).thenReturn(
            Response.success(
                BaseResponse(
                    status = 1,
                    message = "Success",
                    data = listOf(patientResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )
        val map = mutableMapOf<String, String>()
        map[QueryParameters.COUNT] = ConstantValues.COUNT_VALUE.toString()
        map[QueryParameters.OFFSET] = 200.toString()
        map[QueryParameters.SORT] = "-${QueryParameters.ID}"
        if (preferenceRepository.getLastSyncPatient() != 0L) map[QueryParameters.LAST_UPDATED] =
            String.format(
                QueryParameters.GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncPatient().toTimeStampDate()
            )
        `when`(patientApiService.getListData(PATIENT, map)).thenReturn(
            Response.success(
                BaseResponse(
                    status = 2,
                    message = "Success",
                    data = listOf(patientResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )
        val response = syncRepositoryImpl.getAndInsertListPatientData(0)
        assertEquals(true, response is ApiEndResponse)
    }

    @Test
    internal fun getAndInsertPatientDataById_Returns_Patient() = runTest {
        `when`(
            patientApiService.getListData(
                PATIENT,
                mapOf(Pair(QueryParameters.ID, "05d03012-d7f6-4fa7-919b-786573972f46"))
            )
        ).thenReturn(
            Response.success(
                BaseResponse(
                    status = 1,
                    message = "Success",
                    data = listOf(patientResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )
        val response =
            syncRepositoryImpl.getAndInsertPatientDataById("05d03012-d7f6-4fa7-919b-786573972f46")
        assertEquals(
            (response as ApiEndResponse).body[0].firstName,
            "Test"
        )
    }

    @Test
    internal fun getAndInsertRelation_Returns_ListOfRelation() = runTest {
        `when`(patientDao.getPatientIdByFhirId("FHIR_ID")).thenReturn("ID")
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                GenericTypeEnum.FHIR_IDS,
                SyncType.POST,
                ConstantValues.COUNT_VALUE
            )
        ).thenReturn(
            listOfGenericEntity
        )
        `when`(genericDao.deleteSyncPayload(listOf("ID"))).thenReturn(0)

        val map = mutableMapOf<String, String>()
        map[QueryParameters.PATIENT_ID] =
            listOfGenericEntity.map { it.payload }.toNoBracketAndNoSpaceString()
        map[QueryParameters.COUNT] = ConstantValues.DEFAULT_MAX_COUNT_VALUE.toString()

        `when`(patientApiService.getRelationData(RELATED_PERSON, map)).thenReturn(
            Response.success(
                BaseResponse(
                    status = 1,
                    message = "Success",
                    data = listOf(relationResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )

        val response = syncRepositoryImpl.getAndInsertRelation()
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
        val response = syncRepositoryImpl.getAndInsertRelation()
        assertEquals((response is ApiEmptyResponse), true)
    }

    @Test
    internal fun getAndInsertPrescription_Returns_Success() = runTest {
        `when`(
            prescriptionApiService.getPastPrescription(
                mapOf(
                    Pair(QueryParameters.PATIENT_ID, "FHIR_ID")
                )
            )
        ).thenReturn(
            Response.success(
                BaseResponse(
                    status = 1,
                    message = "Success",
                    data = listOf(prescribedResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )

        val response = syncRepositoryImpl.getAndInsertPrescription("FHIR_ID")
        assertEquals(
            (response as ApiEndResponse).body[0].prescriptionId,
            "78e2d936-39e4-42c3-abf4-b96274726c27"
        )
    }

    @Test
    internal fun getAndInsertPrescription_Returns_Error() = runTest {
        `when`(
            prescriptionApiService.getPastPrescription(
                mapOf(
                    Pair(QueryParameters.PATIENT_ID, "FHIR_ID")
                )
            )
        ).thenReturn(
            Response.success(
                BaseResponse(
                    status = 0,
                    message = "Error",
                    data = null,
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )
        val response = syncRepositoryImpl.getAndInsertPrescription("FHIR_ID")
        assertEquals((response is ApiErrorResponse), true)
    }

    @Test
    internal fun getAndInsertMedication_Returns_Success() = runTest {
        val oldMap = mutableMapOf<String, String>()
        oldMap[QueryParameters.COUNT] = ConstantValues.COUNT_VALUE.toString()
        oldMap[QueryParameters.OFFSET] = 0.toString()
        if (preferenceRepository.getLastMedicationSyncDate() != 0L) oldMap[QueryParameters.LAST_UPDATED] =
            String.format(
                QueryParameters.GREATER_THAN_BUILDER,
                preferenceRepository.getLastMedicationSyncDate().toTimeStampDate()
            )

        `when`(
            prescriptionApiService.getAllMedications(
                oldMap
            )
        ).thenReturn(
            Response.success(
                BaseResponse(
                    status = 1,
                    message = "Success",
                    data = listOf(medicationResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )

        val map = mutableMapOf<String, String>()
        map[QueryParameters.COUNT] = ConstantValues.COUNT_VALUE.toString()
        map[QueryParameters.OFFSET] = 200.toString()
        if (preferenceRepository.getLastMedicationSyncDate() != 0L) map[QueryParameters.LAST_UPDATED] =
            String.format(
                QueryParameters.GREATER_THAN_BUILDER,
                preferenceRepository.getLastMedicationSyncDate().toTimeStampDate()
            )

        `when`(
            prescriptionApiService.getAllMedications(
                map
            )
        ).thenReturn(
            Response.success(
                BaseResponse(
                    status = 2,
                    message = "Success",
                    data = listOf(medicationResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )
        val response = syncRepositoryImpl.getAndInsertMedication(0)
        assertEquals((response as ApiEndResponse).body[0].medFhirId, "21111")
    }

    @Test
    internal fun getAndInsertMedication_Returns_Error() = runTest {
        val map = mutableMapOf<String, String>()
        map[QueryParameters.COUNT] = ConstantValues.COUNT_VALUE.toString()
        map[QueryParameters.OFFSET] = 0.toString()
        if (preferenceRepository.getLastMedicationSyncDate() != 0L) map[QueryParameters.LAST_UPDATED] =
            String.format(
                QueryParameters.GREATER_THAN_BUILDER,
                preferenceRepository.getLastMedicationSyncDate().toTimeStampDate()
            )

        `when`(
            prescriptionApiService.getAllMedications(
                map
            )
        ).thenReturn(
            Response.success(
                BaseResponse(
                    status = 0,
                    message = "Error",
                    data = listOf(medicationResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )
        val response = syncRepositoryImpl.getAndInsertMedication(0)
        assertEquals((response is ApiErrorResponse), true)
    }

    @Test
    internal fun getMedicineTime_Returns_Success() = runTest {
        `when`(
            prescriptionApiService.getMedicineTime(
                mapOf(
                    Pair(
                        QueryParameters.LAST_UPDATED, String.format(
                            QueryParameters.GREATER_THAN_BUILDER,
                            200L.toTimeStampDate()
                        )
                    )
                )
            )
        ).thenReturn(
            Response.success(
                BaseResponse(
                    status = 1,
                    message = "Success",
                    data = listOf(medicineTimeResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )
        val response = syncRepositoryImpl.getMedicineTime()
        assertEquals((response as ApiEndResponse).body[0].medInstructionCode, "307165006")
    }

    @Test
    internal fun getMedicineTime_Returns_Error() = runTest {
        `when`(
            prescriptionApiService.getMedicineTime(
                mapOf(
                    Pair(
                        QueryParameters.LAST_UPDATED, String.format(
                            QueryParameters.GREATER_THAN_BUILDER,
                            200L.toTimeStampDate()
                        )
                    )
                )
            )
        ).thenReturn(
            Response.success(
                BaseResponse(
                    status = 0,
                    message = "Error",
                    data = null,
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )
        val response = syncRepositoryImpl.getMedicineTime()
        assertEquals((response is ApiErrorResponse), true)
    }

    @Test
    internal fun getAndInsertSchedule_Returns_Success() = runTest {
        val map = mutableMapOf<String, String>()
        map[QueryParameters.COUNT] = ConstantValues.COUNT_VALUE.toString()
        map[QueryParameters.OFFSET] = 0.toString()
        map[QueryParameters.SORT] = "-${QueryParameters.ID}"
        if (preferenceRepository.getLastSyncSchedule() != 0L) map[QueryParameters.LAST_UPDATED] =
            String.format(
                QueryParameters.GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncSchedule().toTimeStampDate()
            )
        `when`(scheduleAndAppointmentApiService.getScheduleList(map)).thenReturn(
            Response.success(
                BaseResponse(
                    status = 2,
                    message = "Success",
                    data = listOf(scheduleResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )

        val response = syncRepositoryImpl.getAndInsertSchedule(0)
        assertEquals(
            (response as ApiEndResponse).body[0].scheduleId,
            "SCHEDULE_FHIR_ID"
        )
    }

    @Test
    internal fun getAndInsertSchedule_Returns_Error() = runTest {
        val map = mutableMapOf<String, String>()
        map[QueryParameters.COUNT] = ConstantValues.COUNT_VALUE.toString()
        map[QueryParameters.OFFSET] = 0.toString()
        map[QueryParameters.SORT] = "-${QueryParameters.ID}"
        if (preferenceRepository.getLastSyncSchedule() != 0L) map[QueryParameters.LAST_UPDATED] =
            String.format(
                QueryParameters.GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncSchedule().toTimeStampDate()
            )
        `when`(scheduleAndAppointmentApiService.getScheduleList(map)).thenReturn(
            Response.success(
                BaseResponse(
                    status = 0,
                    message = "Error",
                    data = null,
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )
        val response = syncRepositoryImpl.getAndInsertSchedule(0)
        assertEquals((response as ApiErrorResponse).statusCode, 0)
    }

    @Test
    internal fun getAndInsertSchedule_Returns_Continue() = runTest {
        val oldMap = mutableMapOf<String, String>()
        oldMap[QueryParameters.COUNT] = ConstantValues.COUNT_VALUE.toString()
        oldMap[QueryParameters.OFFSET] = 0.toString()
        oldMap[QueryParameters.SORT] = "-${QueryParameters.ID}"
        if (preferenceRepository.getLastSyncSchedule() != 0L) oldMap[QueryParameters.LAST_UPDATED] =
            String.format(
                QueryParameters.GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncSchedule().toTimeStampDate()
            )
        `when`(scheduleAndAppointmentApiService.getScheduleList(oldMap)).thenReturn(
            Response.success(
                BaseResponse(
                    status = 1,
                    message = "Success",
                    data = listOf(scheduleResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )
        val map = mutableMapOf<String, String>()
        map[QueryParameters.COUNT] = ConstantValues.COUNT_VALUE.toString()
        map[QueryParameters.OFFSET] = 200.toString()
        map[QueryParameters.SORT] = "-${QueryParameters.ID}"
        if (preferenceRepository.getLastSyncSchedule() != 0L) map[QueryParameters.LAST_UPDATED] =
            String.format(
                QueryParameters.GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncSchedule().toTimeStampDate()
            )
        `when`(scheduleAndAppointmentApiService.getScheduleList(map)).thenReturn(
            Response.success(
                BaseResponse(
                    status = 2,
                    message = "Success",
                    data = listOf(scheduleResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )
        val response = syncRepositoryImpl.getAndInsertSchedule(0)
        assertEquals(true, response is ApiEndResponse)
    }

    @Test
    internal fun getAndInsertAppointment_Returns_Success() = runTest {
        val map = mutableMapOf<String, String>()
        map[QueryParameters.COUNT] = ConstantValues.COUNT_VALUE.toString()
        map[QueryParameters.OFFSET] = 0.toString()
        map[QueryParameters.SORT] = "-${QueryParameters.ID}"
        if (preferenceRepository.getLastSyncAppointment() != 0L) map[QueryParameters.LAST_UPDATED] =
            String.format(
                QueryParameters.GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncAppointment().toTimeStampDate()
            )
        `when`(scheduleAndAppointmentApiService.getAppointmentList(map)).thenReturn(
            Response.success(
                BaseResponse(
                    status = 2,
                    message = "Success",
                    data = listOf(appointmentResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )

        val response = syncRepositoryImpl.getAndInsertAppointment(0)
        assertEquals(
            (response as ApiEndResponse).body[0].appointmentId,
            "APPOINTMENT_FHIR_ID"
        )
    }

    @Test
    internal fun getAndInsertAppointment_Returns_Error() = runTest {
        val map = mutableMapOf<String, String>()
        map[QueryParameters.COUNT] = ConstantValues.COUNT_VALUE.toString()
        map[QueryParameters.OFFSET] = 0.toString()
        map[QueryParameters.SORT] = "-${QueryParameters.ID}"
        if (preferenceRepository.getLastSyncAppointment() != 0L) map[QueryParameters.LAST_UPDATED] =
            String.format(
                QueryParameters.GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncAppointment().toTimeStampDate()
            )
        `when`(scheduleAndAppointmentApiService.getAppointmentList(map)).thenReturn(
            Response.success(
                BaseResponse(
                    status = 0,
                    message = "Error",
                    data = null,
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )
        val response = syncRepositoryImpl.getAndInsertAppointment(0)
        assertEquals((response as ApiErrorResponse).statusCode, 0)
    }

    @Test
    internal fun getAndInsertAppointment_Returns_Continue() = runTest {
        val oldMap = mutableMapOf<String, String>()
        oldMap[QueryParameters.COUNT] = ConstantValues.COUNT_VALUE.toString()
        oldMap[QueryParameters.OFFSET] = 0.toString()
        oldMap[QueryParameters.SORT] = "-${QueryParameters.ID}"
        if (preferenceRepository.getLastSyncAppointment() != 0L) oldMap[QueryParameters.LAST_UPDATED] =
            String.format(
                QueryParameters.GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncAppointment().toTimeStampDate()
            )
        `when`(scheduleAndAppointmentApiService.getAppointmentList(oldMap)).thenReturn(
            Response.success(
                BaseResponse(
                    status = 1,
                    message = "Success",
                    data = listOf(appointmentResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )
        val map = mutableMapOf<String, String>()
        map[QueryParameters.COUNT] = ConstantValues.COUNT_VALUE.toString()
        map[QueryParameters.OFFSET] = 200.toString()
        map[QueryParameters.SORT] = "-${QueryParameters.ID}"
        if (preferenceRepository.getLastSyncAppointment() != 0L) map[QueryParameters.LAST_UPDATED] =
            String.format(
                QueryParameters.GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncAppointment().toTimeStampDate()
            )
        `when`(scheduleAndAppointmentApiService.getAppointmentList(map)).thenReturn(
            Response.success(
                BaseResponse(
                    status = 2,
                    message = "Success",
                    data = listOf(appointmentResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )
        val response = syncRepositoryImpl.getAndInsertAppointment(0)
        assertEquals(true, response is ApiEndResponse)
    }

    @Test
    internal fun sendPersonPostData_Return_Success() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.PATIENT,
                syncType = SyncType.POST
            )
        ).thenReturn(emptyList())
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
            listOfPersonEntity
        )

        `when`(patientApiService.createData(PATIENT, listOfPersonEntity.map {
            it.payload.fromJson<LinkedTreeMap<*, *>>()
                .mapToObject(PatientResponse::class.java) as Any
        })).thenReturn(
            Response.success(
                BaseResponse(
                    status = 1,
                    message = "Success",
                    data = listOf(createResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )
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
            listOfRelationEntity
        )
        `when`(patientApiService.createData(RELATED_PERSON, listOfRelationEntity.map {
            it.payload.fromJson<LinkedTreeMap<*, *>>()
                .mapToObject(RelatedPersonResponse::class.java) as Any
        })).thenReturn(
            Response.success(
                BaseResponse(
                    status = 1,
                    message = "Success",
                    data = listOf(createResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )
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
            listOfPrescriptionEntity
        )

        `when`(
            prescriptionApiService.postPrescriptionRelatedData(
                EndPoints.MEDICATION_REQUEST,
                listOfPrescriptionEntity.map { prescriptionGenericEntity ->
                    prescriptionGenericEntity.payload.fromJson<LinkedTreeMap<*, *>>()
                        .mapToObject(PrescriptionResponse::class.java)!!
                }
            )
        ).thenReturn(
            Response.success(
                BaseResponse(
                    status = 1,
                    message = "Success",
                    data = listOf(createResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )

        val response = syncRepositoryImpl.sendPrescriptionPostData()
        assertEquals(
            "78e2d936-39e4-42c3-abf4-b96274726c27",
            (response as ApiEndResponse).body[0].id
        )
    }

    @Test
    internal fun sendSchedulePostData_Return_Success() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.SCHEDULE,
                syncType = SyncType.POST
            )
        ).thenReturn(emptyList())
        val response = syncRepositoryImpl.sendSchedulePostData()
        assertEquals(true, response is ApiEmptyResponse)
    }

    @Test
    internal fun sendSchedulePostData_InteractServer_Return_Success() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.SCHEDULE,
                syncType = SyncType.POST
            )
        ).thenReturn(
            listOfScheduleEntity
        )

        `when`(scheduleAndAppointmentApiService.postScheduleData(listOfScheduleEntity.map {
            it.payload.fromJson<LinkedTreeMap<*, *>>()
                .mapToObject(ScheduleResponse::class.java) as Any
        })).thenReturn(
            Response.success(
                BaseResponse(
                    status = 1,
                    message = "Success",
                    data = listOf(createResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )
        val response = syncRepositoryImpl.sendSchedulePostData()
        assertEquals(
            "78e2d936-39e4-42c3-abf4-b96274726c27",
            (response as ApiEndResponse).body[0].id
        )
    }

    @Test
    internal fun sendAppointmentPostData_Return_Success() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.APPOINTMENT,
                syncType = SyncType.POST
            )
        ).thenReturn(emptyList())
        val response = syncRepositoryImpl.sendAppointmentPostData()
        assertEquals(true, response is ApiEmptyResponse)
    }

    @Test
    internal fun sendAppointmentPostData_InteractServer_Return_Success() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.APPOINTMENT,
                syncType = SyncType.POST
            )
        ).thenReturn(
            listOfAppointmentEntity
        )

        `when`(scheduleAndAppointmentApiService.createAppointment(listOfAppointmentEntity.map {
            it.payload.fromJson<LinkedTreeMap<*, *>>()
                .mapToObject(AppointmentResponse::class.java) as Any
        })).thenReturn(
            Response.success(
                BaseResponse(
                    status = 1,
                    message = "Success",
                    data = listOf(createResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )
        val response = syncRepositoryImpl.sendAppointmentPostData()
        assertEquals(
            "78e2d936-39e4-42c3-abf4-b96274726c27",
            (response as ApiEndResponse).body[0].id
        )
    }

    @Test
    fun `send person patch data return success`() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                GenericTypeEnum.PATIENT,
                SyncType.PATCH
            )
        ).thenReturn(
            emptyList()
        )
        val response = syncRepositoryImpl.sendPersonPatchData()
        assertEquals(true, response is ApiEmptyResponse)
    }

    @Test
    fun `send person patch interact server return success`() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.PATIENT,
                syncType = SyncType.PATCH
            )
        ).thenReturn(
            listOfPatchEntity
        )

        `when`(
            patientApiService.patchListOfChanges(
                PATIENT,
                listOfPatchEntity.map { it.payload.fromJson() }
            )
        ).thenReturn(
            Response.success(
                BaseResponse(
                    status = 1,
                    message = "Success",
                    data = listOf(createResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )

        val response = syncRepositoryImpl.sendPersonPatchData()
        assertEquals(
            "21292",
            (response as ApiEndResponse).body[0].fhirId
        )
    }

    @Test
    fun `send person patch interact server return error`() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.PATIENT,
                syncType = SyncType.PATCH
            )
        ).thenReturn(
            listOfPatchEntity
        )

        `when`(
            patientApiService.patchListOfChanges(
                PATIENT,
                listOfPatchEntity.map { it.payload.fromJson() }
            )
        ).thenReturn(
            Response.success(
                BaseResponse(
                    status = 0,
                    message = "Error",
                    data = null,
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )

        val response = syncRepositoryImpl.sendPersonPatchData()
        assertEquals(
            true,
            (response is ApiErrorResponse)
        )
    }

    @Test
    fun `send relation patch data return success`() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                GenericTypeEnum.RELATION,
                SyncType.PATCH
            )
        ).thenReturn(
            emptyList()
        )
        val response = syncRepositoryImpl.sendRelatedPersonPatchData()
        assertEquals(true, response is ApiEmptyResponse)
    }

    @Test
    fun `send relation patch interact server return success`() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.RELATION,
                syncType = SyncType.PATCH
            )
        ).thenReturn(
            listOfRelationEntity
        )

        `when`(
            patientApiService.patchListOfChanges(
                RELATED_PERSON,
                listOfRelationEntity.map { it.payload.fromJson() }
            )
        ).thenReturn(
            Response.success(
                BaseResponse(
                    status = 1,
                    message = "Success",
                    data = listOf(createResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )

        val response = syncRepositoryImpl.sendRelatedPersonPatchData()
        assertEquals(
            "21292",
            (response as ApiEndResponse).body[0].fhirId
        )
    }

    @Test
    fun `send related patch interact server return error`() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.RELATION,
                syncType = SyncType.PATCH
            )
        ).thenReturn(
            listOfRelationEntity
        )

        `when`(
            patientApiService.patchListOfChanges(
                RELATED_PERSON,
                listOfRelationEntity.map { it.payload.fromJson() }
            )
        ).thenReturn(
            Response.success(
                BaseResponse(
                    status = 0,
                    message = "Error",
                    data = null,
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )

        val response = syncRepositoryImpl.sendRelatedPersonPatchData()
        assertEquals(
            true,
            (response is ApiErrorResponse)
        )
    }

    @Test
    fun `send appointment patch data return success`() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                GenericTypeEnum.APPOINTMENT,
                SyncType.PATCH
            )
        ).thenReturn(
            emptyList()
        )
        val response = syncRepositoryImpl.sendAppointmentPatchData()
        assertEquals(true, response is ApiEmptyResponse)
    }

    @Test
    fun `send appointment patch interact server return success`() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.APPOINTMENT,
                syncType = SyncType.PATCH
            )
        ).thenReturn(
            listOfAppointmentEntity
        )

        `when`(
            scheduleAndAppointmentApiService.patchListOfChanges(
                listOfAppointmentEntity.map { it.payload.fromJson() }
            )
        ).thenReturn(
            Response.success(
                BaseResponse(
                    status = 1,
                    message = "Success",
                    data = listOf(createResponse),
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )

        val response = syncRepositoryImpl.sendAppointmentPatchData()
        assertEquals(
            "21292",
            (response as ApiEndResponse).body[0].fhirId
        )
    }

    @Test
    fun `send appointment patch interact server return error`() = runTest {
        `when`(
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.APPOINTMENT,
                syncType = SyncType.PATCH
            )
        ).thenReturn(
            listOfAppointmentEntity
        )

        `when`(
            scheduleAndAppointmentApiService.patchListOfChanges(
                listOfAppointmentEntity.map { it.payload.fromJson() }
            )
        ).thenReturn(
            Response.success(
                BaseResponse(
                    status = 0,
                    message = "Error",
                    data = null,
                    offset = null,
                    total = null,
                    error = null
                )
            )
        )

        val response = syncRepositoryImpl.sendAppointmentPatchData()
        assertEquals(
            true,
            (response is ApiErrorResponse)
        )
    }
}