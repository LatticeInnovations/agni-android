package com.latticeonfhir.android.repository

import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.server.api.AuthenticationApiService
import com.latticeonfhir.android.data.server.model.authentication.Login
import com.latticeonfhir.android.data.server.model.authentication.Otp
import com.latticeonfhir.android.data.server.model.authentication.TokenResponse
import com.latticeonfhir.android.data.server.model.user.UserResponse
import com.latticeonfhir.android.data.server.model.user.UserRoleDetails
import com.latticeonfhir.android.data.server.repository.authentication.AuthenticationRepositoryImpl
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiNullResponse
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response

class AutheticationRepositoryTest {
    @Mock
    lateinit var authenticationApiService: AuthenticationApiService

    @Mock
    lateinit var preferenceRepository: PreferenceRepository
    lateinit var authenticationRepositoryImpl: AuthenticationRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        authenticationRepositoryImpl =
            AuthenticationRepositoryImpl(authenticationApiService, preferenceRepository)
    }

    @Test
    fun loginTestWithAuthorizedPhoneNumber() = runBlocking {
        `when`(authenticationApiService.login(Login("9876543210"))).thenReturn(
            Response.success(
                BaseResponse(
                    1,
                    "Authorized user",
                    null,
                    null,
                    null,
                    null
                )
            )
        )
        val actual = authenticationRepositoryImpl.login("9876543210")
        Assert.assertEquals(true, actual is ApiEmptyResponse)
    }

    @Test
    fun loginTestWithUnauthorizedPhoneNumber() = runBlocking {
        `when`(authenticationApiService.login(Login("1234567890"))).thenReturn(
            Response.error(
                401,
                "{\"status\":0, \"message\":\"Unauthorized user\"}".toResponseBody("application/json".toMediaType())
            )
        )
        val actual = authenticationRepositoryImpl.login("1234567890")
        Assert.assertEquals(true, actual is ApiErrorResponse)
        Assert.assertEquals("Unauthorized user", (actual as ApiErrorResponse).errorMessage)
    }

    @Test
    fun loginTestWithAuthorizedEmail() = runBlocking {
        `when`(authenticationApiService.login(Login("devtest@gmail.com"))).thenReturn(
            Response.success(
                BaseResponse(
                    1,
                    "Authorized user",
                    null,
                    null,
                    null,
                    null
                )
            )
        )
        val actual = authenticationRepositoryImpl.login("devtest@gmail.com")
        Assert.assertEquals(true, actual is ApiEmptyResponse)
    }

    @Test
    fun loginTestWithUnauthorizedEmail() = runBlocking {
        `when`(authenticationApiService.login(Login("something@gmail.com"))).thenReturn(
            Response.error(
                401,
                "{\"status\":0, \"message\":\"Unauthorized user\"}".toResponseBody("application/json".toMediaType())
            )
        )
        val actual = authenticationRepositoryImpl.login("something@gmail.com")
        Assert.assertEquals(true, actual is ApiErrorResponse)
        Assert.assertEquals("Unauthorized user", (actual as ApiErrorResponse).errorMessage)
    }

    @Test
    fun validateOtpTestInvalidOTP() = runBlocking {
        `when`(authenticationApiService.validateOtp(Otp("9876543210", 111111))).thenReturn(
            Response.error(
                401,
                "{\"status\":0, \"message\":\"Invalid OTP\"}".toResponseBody("application/json".toMediaType())
            )
        )
        val actual = authenticationRepositoryImpl.validateOtp("9876543210", 111111)
        Assert.assertEquals(true, actual is ApiErrorResponse)
        Assert.assertEquals("Invalid OTP", (actual as ApiErrorResponse).errorMessage)
    }

    @Test
    fun validateOtpTestValidOTP() = runBlocking {
        `when`(authenticationApiService.validateOtp(Otp("9876543210", 222222))).thenReturn(
            Response.success(
                BaseResponse(
                    1,
                    "Logged in successfully",
                    null,
                    null,
                    TokenResponse(
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjQsInVzZXJOYW1lIjoiRGV2IHRlc3QgMiIsImlhdCI6MTY4NTk1NzQ4MCwiZXhwIjoxNjg2Mzg5NDgwfQ.cUPr7qmmsfucjNP-5x2VipHi2DcGuryO643D0NnoN0A"
                    ),
                    null
                )
            )
        )
        `when`(authenticationApiService.getUserDetails()).thenReturn(
            Response.success(
                BaseResponse(
                    1,
                    "Data fetched successfully",
                    null,
                    null,
                    UserResponse(
                        "22545",
                        "Dev Test",
                        listOf(
                            UserRoleDetails(
                                roleId = "doctor",
                                role = "Doctor",
                                orgId = "22539",
                                orgName = "Indraprastha Apollo Hospital"
                            )
                        ),
                        9876543210,
                        "devtest@gmail.com"
                    ),
                    null
                )
            )
        )
        val actual = authenticationRepositoryImpl.validateOtp("9876543210", 222222)
        Assert.assertEquals(true, actual is ApiEndResponse)
        Assert.assertEquals(
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjQsInVzZXJOYW1lIjoiRGV2IHRlc3QgMiIsImlhdCI6MTY4NTk1NzQ4MCwiZXhwIjoxNjg2Mzg5NDgwfQ.cUPr7qmmsfucjNP-5x2VipHi2DcGuryO643D0NnoN0A",
            (actual as ApiEndResponse).body.token
        )
    }
}