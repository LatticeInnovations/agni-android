package com.latticeonfhir.android.api

import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.base.BaseClass
import kotlinx.coroutines.test.*
import com.latticeonfhir.android.data.server.api.ScheduleAndAppointmentApiService
import com.latticeonfhir.android.utils.ResponseHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.HttpsURLConnection

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class ScheduleAndAppointmentApiServiceTest: BaseClass() {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var scheduleAndAppointmentApiService: ScheduleAndAppointmentApiService

    @Before
    public override fun setUp() {
        mockWebServer = MockWebServer()
        scheduleAndAppointmentApiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(FhirApp.gson))
            .build()
            .create(ScheduleAndAppointmentApiService::class.java)
    }

    @Test
    fun `get schedule list`() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/scheduleResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = scheduleAndAppointmentApiService.getScheduleList(emptyMap())
        assertEquals("23233", response.body()?.data?.get(0)?.scheduleId)
    }

    @Test
    fun `get schedule list return error response`() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/errorResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = scheduleAndAppointmentApiService.getScheduleList(emptyMap())
        assertEquals(true,response.body()?.data?.isEmpty())
        assertEquals(0,response.body()?.status)
    }

    @Test
    fun `create schedule returns success`() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/createResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = scheduleAndAppointmentApiService.postScheduleData(emptyList())
        assertEquals("21292",response.body()?.data?.get(0)?.fhirId)
    }

    @Test
    fun `create schedule returns error`() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/errorResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = scheduleAndAppointmentApiService.postScheduleData(emptyList())
        assertEquals(0,response.body()?.status)
    }

    @Test
    fun `get appointment list`() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/appointmentResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = scheduleAndAppointmentApiService.getAppointmentList(emptyMap())
        assertEquals("23303", response.body()?.data?.get(0)?.appointmentId)
    }

    @Test
    fun `get appointment list return error response`() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/errorResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = scheduleAndAppointmentApiService.getAppointmentList(emptyMap())
        assertEquals(true,response.body()?.data?.isEmpty())
        assertEquals(0,response.body()?.status)
    }

    @Test
    fun `create appointment returns success`() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/createResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = scheduleAndAppointmentApiService.createAppointment(emptyList())
        assertEquals("21292",response.body()?.data?.get(0)?.fhirId)
    }

    @Test
    fun `create appointment returns error`() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/errorResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = scheduleAndAppointmentApiService.createAppointment(emptyList())
        assertEquals(0,response.body()?.status)
    }

    @Test
    fun `patch list of changes returns success`() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/createResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = scheduleAndAppointmentApiService.patchListOfChanges(emptyList())
        assertEquals("21292",response.body()?.data?.get(0)?.fhirId)
    }

    @Test
    fun `patch list of changes returns error`() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/errorResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = scheduleAndAppointmentApiService.patchListOfChanges(emptyList())
        assertEquals(0,response.body()?.status)
    }}