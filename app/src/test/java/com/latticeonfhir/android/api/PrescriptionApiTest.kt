package com.latticeonfhir.android.api

import com.latticeonfhir.android.FhirApp.Companion.gson
import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.server.api.PrescriptionApiService
import com.latticeonfhir.android.data.server.constants.QueryParameters
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicineTimeResponse
import com.latticeonfhir.android.utils.ResponseHelper
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeStampDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.HttpsURLConnection

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class PrescriptionApiTest : BaseClass() {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var prescriptionApiService: PrescriptionApiService

    @Before
    public override fun setUp() {
        mockWebServer = MockWebServer()
        prescriptionApiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(PrescriptionApiService::class.java)
    }

    @Test
    internal fun getAllMedications_ReturnsList_NotEmpty() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/medicationResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = prescriptionApiService.getAllMedications(emptyMap())
        mockWebServer.takeRequest()

        assertEquals(medicationResponse, response.body()?.data?.get(0))
    }

    @Test
    internal fun postPrescriptionData_Return_CreateResponse() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/createResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response =
            prescriptionApiService.postPrescriptionRelatedData("createPrescription", emptyList())
        mockWebServer.takeRequest()

        assertEquals(createResponse, response.body()?.data?.get(0))
    }

    @Test
    internal fun getPastPrescriptionData_Return_List() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/medicationRequest.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = prescriptionApiService.getPastPrescription(emptyMap())
        mockWebServer.takeRequest()

        assertEquals(
            prescribedResponse.prescriptionId,
            response.body()?.data?.get(0)?.prescriptionId
        )
    }

    @Test
    internal fun getMedicationTime_Return_List() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/medicationTimeResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = prescriptionApiService.getMedicineTime(
            mapOf(
                Pair(
                    QueryParameters.LAST_UPDATED, String.format(
                        QueryParameters.GREATER_THAN_BUILDER,
                        200L.toTimeStampDate()
                    )
                )
            )
        )
        mockWebServer.takeRequest()

        assertEquals(medicineTimeResponse, response.body()?.data?.get(0))
    }

    @After
    public override fun tearDown() {
        mockWebServer.shutdown()
    }
}