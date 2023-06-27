package com.latticeonfhir.android.api

import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.server.api.PatientApiService
import com.latticeonfhir.android.data.server.constants.EndPoints
import com.latticeonfhir.android.utils.ResponseHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.HttpsURLConnection

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class PatientApiTest: BaseClass() {


    private lateinit var mockWebServer: MockWebServer
    private lateinit var patientApiService: PatientApiService

    @Before
    public override fun setUp() {
        mockWebServer = MockWebServer()
        patientApiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(FhirApp.gson))
            .build()
            .create(PatientApiService::class.java)
    }

    @Test
    fun `get list data of patient`() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/patientContinueResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = patientApiService.getListData(EndPoints.PATIENT, emptyMap())
        assertEquals("Natalie",response.body()?.data?.get(0)?.firstName)
    }

    @Test
    fun `get list data of return error response`() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/errorResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = patientApiService.getListData(EndPoints.PATIENT, emptyMap())
        assertEquals(true,response.body()?.data?.isEmpty())
        assertEquals(0,response.body()?.status)
    }

    @Test
    fun `create data returns success`() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/createResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = patientApiService.createData(EndPoints.PATIENT, emptyList())
        assertEquals("21292",response.body()?.data?.get(0)?.fhirId)
    }

    @Test
    fun `create data returns error`() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/errorResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = patientApiService.createData(EndPoints.PATIENT, emptyList())
        assertEquals(0,response.body()?.status)
    }


    @Test
    fun `patch list of changes returns success`() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/createResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = patientApiService.patchListOfChanges(EndPoints.PATIENT, emptyList())
        assertEquals("21292",response.body()?.data?.get(0)?.fhirId)
    }

    @Test
    fun `patch list of changes returns error`() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/errorResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = patientApiService.patchListOfChanges(EndPoints.PATIENT, emptyList())
        assertEquals(0,response.body()?.status)
    }

    @Test
    fun `get relation data returns success`() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/relationResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = patientApiService.getRelationData(EndPoints.RELATED_PERSON, emptyMap())
        assertEquals("21028",response.body()?.data?.get(0)?.relationship?.get(0)?.relativeId)
    }

    @Test
    fun `get relation data returns error`() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/errorResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = patientApiService.getRelationData(EndPoints.RELATED_PERSON, emptyMap())
        assertEquals(0,response.body()?.status)
    }
}