package com.latticeonfhir.android.api

import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.server.api.AuthenticationApiService
import com.latticeonfhir.android.data.server.model.authentication.Login
import com.latticeonfhir.android.data.server.model.authentication.Otp
import com.latticeonfhir.android.utils.ResponseHelper
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
class AuthenticationApiServiceTest: BaseClass() {


    private lateinit var mockWebServer: MockWebServer
    private lateinit var authenticationApiService: AuthenticationApiService
    val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjQsInVzZXJOYW1lIjoiRGV2IHRlc3QgMiIsImlhdCI6MTY4NTk0MTgxOCwiZXhwIjoxNjg2MzczODE4fQ.FznpiHCc7q0gl4XtC5Q3a0WGr_OZk4DHP2i_GQ_zRJA"

    @Before
    public override fun setUp() {
        mockWebServer = MockWebServer()
        authenticationApiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(FhirApp.gson))
            .build()
            .create(AuthenticationApiService::class.java)
    }

    @Test
    internal fun loginUser_Return_Success() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/loginResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = authenticationApiService.login(Login("9876543210"))

        mockWebServer.takeRequest()

        assertEquals(1,response.body()?.status)
    }

    @Test
    internal fun validateOtp_Returns_Success() = runTest {
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/validateotpResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = authenticationApiService.validateOtp(Otp("9876543210",222222))
        mockWebServer.takeRequest()

        assertEquals(token,response.body()?.data?.token)
    }

    @Test
    internal fun getUserData_Return_Success() = runTest {
        val userName = "Dev test 2"
        val mockResponse = MockResponse().run {
            setResponseCode(HttpsURLConnection.HTTP_OK)
            setBody(ResponseHelper.readJsonResponse("/userResponse.json"))
        }
        mockWebServer.enqueue(mockResponse)

        val response = authenticationApiService.getUserDetails()
        mockWebServer.takeRequest()

        assertEquals(userName,response.body()?.data?.userName)
    }

    @After
    public override fun tearDown() {
        mockWebServer.shutdown()
    }
}