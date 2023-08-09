package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.roomdb.FhirAppDatabase
import com.latticeonfhir.android.data.server.repository.authentication.AuthenticationRepository
import com.latticeonfhir.android.ui.login.PhoneEmailViewModel
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class PhoneEmailViewModelTest {
    @Mock
    lateinit var authenticationRepository: AuthenticationRepository
    @Mock
    lateinit var preferenceRepository: PreferenceRepository
    @Mock
    lateinit var fhirAppDatabase: FhirAppDatabase
    lateinit var viewModel: PhoneEmailViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = PhoneEmailViewModel(authenticationRepository, preferenceRepository, fhirAppDatabase)
    }

    @Test
    fun enter_valid_phone_number() {
        viewModel.inputValue = "9876543210"
        viewModel.updateError()
        assertEquals("isPhoneNumber should be true, on valid phone number input", true, viewModel.isPhoneNumber)
        assertEquals("isInputInvalid should be false, on valid input", false, viewModel.isInputInvalid)
        assertEquals("isError should be false, on valid input", false, viewModel.isError)
    }

    @Test
    fun enter_valid_email() {
        viewModel.inputValue = "mansi@thelattice.in"
        viewModel.updateError()
        assertEquals("isPhoneNumber should be false, on valid email input", false, viewModel.isPhoneNumber)
        assertEquals("isInputInvalid should be false, on valid input", false, viewModel.isInputInvalid)
        assertEquals("isError should be false, on valid input", false, viewModel.isError)
    }

    @Test
    fun enter_phone_number_length_less_than_10 () {
        viewModel.inputValue = "123456789"
        viewModel.updateError()
        assertEquals("isPhoneNumber should be true, on only numbers input", true, viewModel.isPhoneNumber)
        assertEquals("isInputInvalid should be true, on invalid input", true, viewModel.isInputInvalid)
        assertEquals("isError should be true, on invalid input", true, viewModel.isError)
    }

    @Test
    fun enter_phone_number_length_more_than_10 () {
        viewModel.inputValue = "12345678910"
        viewModel.updateError()
        assertEquals("isPhoneNumber should be false, on invalid phone number input", false, viewModel.isPhoneNumber)
        assertEquals("isInputInvalid should be true, on invalid input", true, viewModel.isInputInvalid)
        assertEquals("isError should be true, on invalid input", true, viewModel.isError)
    }

    @Test
    fun enter_alphanumeric_input () {
        viewModel.inputValue = "123456abcdef"
        viewModel.updateError()
        assertEquals("isPhoneNumber should be false, on alphanumeric input", false, viewModel.isPhoneNumber)
        assertEquals("isInputInvalid should be true, on invalid input", true, viewModel.isInputInvalid)
        assertEquals("isError should be true, on invalid input", true, viewModel.isError)
    }

    @Test
    fun enter_invalid_email () {
        viewModel.inputValue = "abc.com"
        viewModel.updateError()
        assertEquals("isPhoneNumber should be false, on email input", false, viewModel.isPhoneNumber)
        assertEquals("isInputInvalid should be true, on invalid input", true, viewModel.isInputInvalid)
        assertEquals("isError should be true, on invalid input", true, viewModel.isError)
    }

    @Test
    fun login_on_authorised_email () = runTest{
        viewModel.inputValue = "mansi@thelattice.in"
        `when`(authenticationRepository.login(viewModel.inputValue)).thenReturn(
            ResponseMapper.create(
                Response.success(
                    BaseResponse(
                        1,
                        "Authorized user",
                        null,
                        null,
                        null,
                        null
                    )
                ),
                false
            )
        )
        viewModel.login {
            assertEquals("returned value should be true on valid authorised input", true, it)
            assertEquals("isError should be false when logging in with authorised email", false, viewModel.isError)
        }
    }

    @Test
    fun login_on_Unauthorized_email () = runTest{
        viewModel.inputValue = "mansi@gmail.com"
        `when`(authenticationRepository.login(viewModel.inputValue)).thenReturn(
            ResponseMapper.create(
                Response.error(
                    401,
                    "{\"status\":0, \"message\":\"Unauthorized user\"}".toResponseBody("application/json".toMediaType())
                ),
                false
            )
        )
        viewModel.login {
            assertEquals("returned value should be true on valid Unauthorized input", false, it)
            assertEquals("error message should ne 'Unauthorized user'.", "Unauthorized user", viewModel.errorMsg)
            assertEquals("isError should be true when logging in with Unauthorized email", true, viewModel.isError)
        }
    }

    @Test
    fun login_on_authorised_phone () = runTest{
        viewModel.inputValue = "9876543210"
        `when`(authenticationRepository.login(viewModel.inputValue)).thenReturn(
            ResponseMapper.create(
                Response.success(
                    BaseResponse(
                        1,
                        "Authorized user",
                        null,
                        null,
                        null,
                        null
                    )
                ),
                false
            )
        )
        viewModel.login {
            assertEquals("returned value should be true on valid authorised input", true, it)
            assertEquals("isError should be false when logging in with authorised email", false, viewModel.isError)
        }
    }

    @Test
    fun login_on_Unauthorized_phone () = runTest{
        viewModel.inputValue = "98766543210"
        `when`(authenticationRepository.login(viewModel.inputValue)).thenReturn(
            ResponseMapper.create(
                Response.error(
                    401,
                    "{\"status\":0, \"message\":\"Unauthorized user\"}".toResponseBody("application/json".toMediaType())
                ),
                false
            )
        )
        viewModel.login {
            assertEquals("returned value should be true on valid Unauthorized input", false, it)
            assertEquals("error message should ne 'Unauthorized user'.", "Unauthorized user", viewModel.errorMsg)
            assertEquals("isError should be true when logging in with Unauthorized phone", true, viewModel.isError)
        }
    }

    @Test
    fun isDifferentUserTestOnExistingUserWithPhoneNumber(){
        `when`(preferenceRepository.getUserMobile()).thenReturn(9876543210)
        viewModel.inputValue = "9876543210"
        val actual = viewModel.isDifferentUserLogin()
        assertEquals("Should return false on logging in with existing phone number", false, actual)
    }

    @Test
    fun isDifferentUserTestOnExistingUserWithEmail(){
        `when`(preferenceRepository.getUserEmail()).thenReturn("mansi@thelattice.in")
        viewModel.inputValue = "mansi@thelattice.in"
        val actual = viewModel.isDifferentUserLogin()
        assertEquals("Should return false on logging in with existing email", false, actual)
    }

    @Test
    fun isDifferentUserTestOnNewUserWithPhoneNumber(){
        `when`(preferenceRepository.getUserMobile()).thenReturn(0L)
        `when`(preferenceRepository.getUserEmail()).thenReturn("")
        viewModel.inputValue = "9876543210"
        val actual = viewModel.isDifferentUserLogin()
        assertEquals("Should return false on logging in with new phone number", false, actual)
    }

    @Test
    fun isDifferentUserTestOnNewUserWithEmail(){
        `when`(preferenceRepository.getUserMobile()).thenReturn(0L)
        `when`(preferenceRepository.getUserEmail()).thenReturn("")
        viewModel.inputValue = "mansi@thelattice.in"
        val actual = viewModel.isDifferentUserLogin()
        assertEquals("Should return false on logging in with new email", false, actual)
    }

    @Test
    fun isDifferentUserTestOnDifferentUserWithPhoneNumber(){
        `when`(preferenceRepository.getUserMobile()).thenReturn(1111111111)
        viewModel.inputValue = "9876543210"
        val actual = viewModel.isDifferentUserLogin()
        assertEquals("Should return false on logging in with different phone number", true, actual)
    }

    @Test
    fun isDifferentUserTestOnDifferentUserWithEmail(){
        `when`(preferenceRepository.getUserEmail()).thenReturn("test@gmail.com")
        viewModel.inputValue = "mansi@thelattice.in"
        val actual = viewModel.isDifferentUserLogin()
        assertEquals("Should return false on logging in with different email", true, actual)
    }

    @Test
    fun clearAllDataTest() = runTest{
        viewModel.clearAllAppData()
    }
}