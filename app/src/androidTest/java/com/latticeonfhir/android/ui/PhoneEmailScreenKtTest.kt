package com.latticeonfhir.android.ui

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.latticeonfhir.android.ui.main.MainActivity
import org.junit.Rule
import org.junit.Test

class PhoneEmailScreenKtTest {

    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    val heading = hasTestTag("HEADING_TAG")
    val subHeading = hasTestTag("SUB_HEADING_TAG")
    val inputField = hasTestTag("INPUT_FIELD")
    val button = hasTestTag("BUTTON")

    @Test
    fun verify_heading(){
        composeTestRule.onNode(heading).assertTextEquals("Login with phone number or email address")
    }

    @Test
    fun verify_input_field() {
        composeTestRule.onNode(inputField).assertExists("Input field must be displayed")
    }

    @Test
    fun verify_button() {
        composeTestRule.onNode(button).assertExists("Send me OTP button should be displayed.")
            .assertTextEquals("Send me OTP").assertIsNotEnabled()
    }

    @Test
    fun enter_input_and_check_input_field() {
        composeTestRule.onNode(inputField).performTextInput("devtest@gmail.com")
        composeTestRule.onNode(inputField).assertTextEquals("devtest@gmail.com")
    }

    @Test
    fun enter_alphabets_and_check_input_field() {
        composeTestRule.onNode(inputField).performTextInput("abc")
        composeTestRule.onNode(inputField).assertTextEquals("Enter valid phone number or Email address", "abc")
    }

    @Test
    fun enter_numbers_and_check_input_field() {
        composeTestRule.onNode(inputField).performTextInput("123456")
        composeTestRule.onNode(inputField).assertTextEquals(" +91", "Enter valid phone number or Email address", "123456")
    }

    @Test
    fun enter_special_characters_and_check_input_field() {
        composeTestRule.onNode(inputField).performTextInput("!@#$%^&*")
        composeTestRule.onNode(inputField).assertTextEquals("Enter valid phone number or Email address", "!@#$%^&*")
    }

    @Test
    fun enter_alphanumeric_and_check_input_field() {
        composeTestRule.onNode(inputField).performTextInput("abcd1234")
        composeTestRule.onNode(inputField).assertTextEquals("Enter valid phone number or Email address", "abcd1234")
    }

    @Test
    fun enter_free_text_and_check_input_field() {
        composeTestRule.onNode(inputField).performTextInput("abcd1234!@.")
        composeTestRule.onNode(inputField).assertTextEquals("Enter valid phone number or Email address", "abcd1234!@.")
    }

    @Test
    fun enter_less_than_50_chars_and_check_input_field() {
        composeTestRule.onNode(inputField).performTextInput("abcd1234!@.")
        composeTestRule.onNode(inputField).assertTextEquals("Enter valid phone number or Email address", "abcd1234!@.")
    }

    @Test
    fun enter_50_chars_and_check_input_field() {
        composeTestRule.onNode(inputField).performTextInput("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwx")
        composeTestRule.onNode(inputField).assertTextEquals("Enter valid phone number or Email address", "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwx")
    }

    @Test
    fun enter_more_than_50_chars_and_check_input_field() {
        composeTestRule.onNode(inputField).performTextInput("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz")
        composeTestRule.onNode(inputField).assertTextEquals("")
    }

    @Test
    fun enter_valid_input_and_check_button(){
        composeTestRule.onNode(inputField).performTextInput("devtest@gmail.com")
        composeTestRule.onNode(button).assertIsEnabled()
    }

    @Test
    fun check_error_msg_when_user_inputs(){
        composeTestRule.onNode(inputField).performTextInput("a")
        composeTestRule.onNode(inputField).assertTextEquals("Enter valid phone number or Email address", "a")
    }

    @Test
    fun check_error_msg_when_user_starts_entering_number(){
        composeTestRule.onNode(inputField).performTextInput("9")
        composeTestRule.onNode(inputField).assertTextEquals(" +91", "Enter valid phone number or Email address", "9")
    }

    @Test
    fun check_error_msg_when_user_enters_9_numbers(){
        composeTestRule.onNode(inputField).performTextInput("123456789")
        composeTestRule.onNode(inputField).assertTextEquals(" +91", "Enter valid phone number or Email address", "123456789")
    }

    @Test
    fun check_error_msg_when_user_enters_valid_phone_number(){
        composeTestRule.onNode(inputField).performTextInput("1234567890")
        composeTestRule.onNode(inputField).assertTextEquals(" +91", "1234567890")
        composeTestRule.onNode(button).assertIsEnabled()
    }

    @Test
    fun check_error_msg_when_user_enters_valid_email(){
        composeTestRule.onNode(inputField).performTextInput("dev@gmail.com")
        composeTestRule.onNode(inputField).assertTextEquals("dev@gmail.com")
        composeTestRule.onNode(button).assertIsEnabled()
    }

    @Test
    fun check_country_code_when_user_enters_numbers(){
        composeTestRule.onNode(inputField).performTextInput("123456")
        composeTestRule.onNode(inputField).assertTextEquals(" +91", "Enter valid phone number or Email address", "123456")
    }

    @Test
    fun check_country_code_when_user_enters_11_numbers(){
        composeTestRule.onNode(inputField).performTextInput("12345678910")
        composeTestRule.onNode(inputField).assertTextEquals("Enter valid phone number or Email address", "12345678910")
    }

    @Test
    fun check_country_code_when_user_enters_email(){
        composeTestRule.onNode(inputField).performTextInput("dev@gmail.com")
        composeTestRule.onNode(inputField).assertTextEquals("dev@gmail.com")
    }

    @Test
    fun enter_authorized_number_and_check_btn_navigation(){
        composeTestRule.onNode(inputField).performTextInput("1111111111")
        composeTestRule.onNode(button).performClick()
        Thread.sleep(2000)
        composeTestRule.onNode(heading).assertTextEquals("Enter the OTP we sent to")
    }

    @Test
    fun enter_authorized_email_and_check_btn_navigation(){
        composeTestRule.onNode(inputField).performTextInput("devtest@gmail.com")
        composeTestRule.onNode(button).performClick()
        Thread.sleep(2000)
        composeTestRule.onNode(heading).assertTextEquals("Enter the OTP we sent to")
    }
}