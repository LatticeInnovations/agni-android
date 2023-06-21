package com.latticeonfhir.android.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class OtpScreenKtTest: UiTestsBase() {
    private fun login(){
        composeTestRule.onNode(inputField).performTextInput("9876543210")
        composeTestRule.onNode(button).performClick()
        Thread.sleep(2000)
    }

    @Test
    fun check_message_when_login_with_number(){
        login()
        composeTestRule.onNode(heading).assertTextEquals("Enter the OTP we sent to")
        composeTestRule.onNode(subHeading).assertTextEquals("9876543210")
    }

    @Test
    fun check_message_when_login_with_email(){
        composeTestRule.onNode(inputField).performTextInput("dev2@gmail.com")
        composeTestRule.onNode(button).performClick()
        Thread.sleep(5000)
        composeTestRule.onNode(heading).assertTextEquals("Enter the OTP we sent to")
        composeTestRule.onNode(subHeading).assertTextEquals("dev2@gmail.com")
    }

    @Test
    fun verify_fields_and_buttons() {
        login()
        composeTestRule.onNode(firstDigit).assertExists()
        composeTestRule.onNode(secondDigit).assertExists()
        composeTestRule.onNode(thirdDigit).assertExists()
        composeTestRule.onNode(fourDigit).assertExists()
        composeTestRule.onNode(fiveDigit).assertExists()
        composeTestRule.onNode(sixDigit).assertExists()
        composeTestRule.onNode(button).assertExists().assertTextEquals("Verify").assertIsNotEnabled()
        composeTestRule.onNode(twoMinTimer).assertExists()
    }

    @Test
    fun verify_button() {
        login()
        composeTestRule.onNode(button).assertIsNotEnabled()
    }

    @Test
    fun enter_invalid_otp_check_error_message() {
        login()
        composeTestRule.onNode(firstDigit).performTextInput("1")
        composeTestRule.onNode(secondDigit).performTextInput("1")
        composeTestRule.onNode(thirdDigit).performTextInput("1")
        composeTestRule.onNode(fourDigit).performTextInput("1")
        composeTestRule.onNode(fiveDigit).performTextInput("1")
        composeTestRule.onNode(sixDigit).performTextInput("1")
        composeTestRule.onNode(button).performClick()
        Thread.sleep(2000)
        composeTestRule.onNode(errorMsg).assertIsDisplayed().assertTextEquals("Invalid OTP")
    }

    @Test
    fun enter_less_than_6_digits_and_check_btn(){
        login()
        composeTestRule.onNode(firstDigit).performTextInput("1")
        composeTestRule.onNode(secondDigit).performTextInput("1")
        composeTestRule.onNode(thirdDigit).performTextInput("1")
        composeTestRule.onNode(fourDigit).performTextInput("1")
        composeTestRule.onNode(fiveDigit).performTextInput("1")
        composeTestRule.onNode(button).assertIsNotEnabled()
    }

    @Test
    fun enter_alphabets_in_otp_fields() {
        login()
        composeTestRule.onNode(firstDigit).performTextInput("a")
        composeTestRule.onNode(firstDigit).assertTextEquals("")
        composeTestRule.onNode(secondDigit).performTextInput("a")
        composeTestRule.onNode(secondDigit).assertTextEquals("")
        composeTestRule.onNode(thirdDigit).performTextInput("a")
        composeTestRule.onNode(thirdDigit).assertTextEquals("")
        composeTestRule.onNode(fourDigit).performTextInput("a")
        composeTestRule.onNode(fourDigit).assertTextEquals("")
        composeTestRule.onNode(fiveDigit).performTextInput("a")
        composeTestRule.onNode(fiveDigit).assertTextEquals("")
        composeTestRule.onNode(sixDigit).performTextInput("a")
        composeTestRule.onNode(sixDigit).assertTextEquals("")
    }

    @Test
    fun enter_special_characters_in_otp_fields() {
        login()
        composeTestRule.onNode(firstDigit).performTextInput("@")
        composeTestRule.onNode(firstDigit).assertTextEquals("")
        composeTestRule.onNode(secondDigit).performTextInput("!")
        composeTestRule.onNode(secondDigit).assertTextEquals("")
        composeTestRule.onNode(thirdDigit).performTextInput("#")
        composeTestRule.onNode(thirdDigit).assertTextEquals("")
        composeTestRule.onNode(fourDigit).performTextInput("^")
        composeTestRule.onNode(fourDigit).assertTextEquals("")
        composeTestRule.onNode(fiveDigit).performTextInput("%")
        composeTestRule.onNode(fiveDigit).assertTextEquals("")
        composeTestRule.onNode(sixDigit).performTextInput("*")
        composeTestRule.onNode(sixDigit).assertTextEquals("")
    }

    @Test
    fun click_back_and_regenerate_otp() {
        login()
        composeTestRule.onNode(backIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(button).performClick()
        Thread.sleep(5000)
        composeTestRule.onNode(heading).assertTextEquals("Enter the OTP we sent to")
    }

    @Test
    fun check_error_msg_on_multiple_incorrect_attempts(){
        composeTestRule.onNode(inputField).performTextInput("8279784095")
        composeTestRule.onNode(button).performClick()
        Thread.sleep(2000)
        composeTestRule.onNode(dialogPositiveBtn).performClick()
        Thread.sleep(2000)
        composeTestRule.onNode(firstDigit).performTextInput("1")
        composeTestRule.onNode(secondDigit).performTextInput("1")
        composeTestRule.onNode(thirdDigit).performTextInput("1")
        composeTestRule.onNode(fourDigit).performTextInput("1")
        composeTestRule.onNode(fiveDigit).performTextInput("1")
        composeTestRule.onNode(sixDigit).performTextInput("1")
        composeTestRule.onNode(button).performClick()
        Thread.sleep(2000)
        composeTestRule.onNode(errorMsg).assertTextEquals("Invalid OTP")

        // second time
        composeTestRule.onNode(firstDigit).performTextInput("1")
        composeTestRule.onNode(secondDigit).performTextInput("1")
        composeTestRule.onNode(thirdDigit).performTextInput("1")
        composeTestRule.onNode(fourDigit).performTextInput("1")
        composeTestRule.onNode(fiveDigit).performTextInput("1")
        composeTestRule.onNode(sixDigit).performTextInput("1")
        composeTestRule.onNode(button).performClick()
        Thread.sleep(2000)
        composeTestRule.onNode(errorMsg).assertTextEquals("Invalid OTP")

        //third time
        composeTestRule.onNode(firstDigit).performTextInput("1")
        composeTestRule.onNode(secondDigit).performTextInput("1")
        composeTestRule.onNode(thirdDigit).performTextInput("1")
        composeTestRule.onNode(fourDigit).performTextInput("1")
        composeTestRule.onNode(fiveDigit).performTextInput("1")
        composeTestRule.onNode(sixDigit).performTextInput("1")
        composeTestRule.onNode(button).performClick()
        Thread.sleep(2000)
        composeTestRule.onNode(errorMsg).assertTextEquals("Invalid OTP")

        // fourth time
        composeTestRule.onNode(firstDigit).performTextInput("1")
        composeTestRule.onNode(secondDigit).performTextInput("1")
        composeTestRule.onNode(thirdDigit).performTextInput("1")
        composeTestRule.onNode(fourDigit).performTextInput("1")
        composeTestRule.onNode(fiveDigit).performTextInput("1")
        composeTestRule.onNode(sixDigit).performTextInput("1")
        composeTestRule.onNode(button).performClick()
        Thread.sleep(2000)
        composeTestRule.onNode(errorMsg).assertTextEquals("Invalid OTP")

        // fifth time
        composeTestRule.onNode(firstDigit).performTextInput("1")
        composeTestRule.onNode(secondDigit).performTextInput("1")
        composeTestRule.onNode(thirdDigit).performTextInput("1")
        composeTestRule.onNode(fourDigit).performTextInput("1")
        composeTestRule.onNode(fiveDigit).performTextInput("1")
        composeTestRule.onNode(sixDigit).performTextInput("1")
        composeTestRule.onNode(button).performClick()
        Thread.sleep(2000)
        composeTestRule.onNode(errorMsg).assertTextEquals("Too many attempts. Please try after 5 mins")

        // check button validity
        composeTestRule.onNode(button).assertIsNotEnabled()
    }

    @Test
    fun z_check_log_in_on_authorized_details(){
        login()
        composeTestRule.onNode(firstDigit).performTextInput("2")
        composeTestRule.onNode(secondDigit).performTextInput("2")
        composeTestRule.onNode(thirdDigit).performTextInput("2")
        composeTestRule.onNode(fourDigit).performTextInput("2")
        composeTestRule.onNode(fiveDigit).performTextInput("2")
        composeTestRule.onNode(sixDigit).performTextInput("2")
        composeTestRule.onNode(button).performClick()
        Thread.sleep(2000)
        composeTestRule.onNode(titleMyPatients).assertExists("Should navigate to \"My Patients\".")

    }

    @Test
    fun zzzz_logout(){
        composeTestRule.onNode(profileTab).performClick()
        composeTestRule.onNode(logoutIcon).performClick()
        composeTestRule.onNodeWithText("Logout").performClick()
    }
}