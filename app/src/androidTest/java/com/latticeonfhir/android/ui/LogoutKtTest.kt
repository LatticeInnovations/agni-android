package com.latticeonfhir.android.ui

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasNoClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.latticeonfhir.android.ui.main.MainActivity
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class LogoutKtTest {
    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    val profile_tab = hasTestTag("Profile tab") and hasClickAction()
    val profileTitle = hasText("Profile") and hasNoClickAction()
    val heading = hasTestTag("HEADING_TAG")

    // dialog
    val dialogTitle = hasTestTag("DIALOG_TITLE")
    val dialogDesc = hasTestTag("DIALOG_DESCRIPTION")
    val dialogPositiveBtn = hasTestTag("POSITIVE_BTN")
    val dialogNegativeBtn = hasTestTag("NEGATIVE_BTN")

    // for login
    val inputField = hasTestTag("INPUT_FIELD")
    val button = hasTestTag("BUTTON")
    val firstDigit = hasTestTag("FIRST_DIGIT")
    val secondDigit = hasTestTag("SECOND_DIGIT")
    val thirdDigit = hasTestTag("THIRD_DIGIT")
    val fourDigit = hasTestTag("FOUR_DIGIT")
    val fiveDigit = hasTestTag("FIVE_DIGIT")
    val sixDigit = hasTestTag("SIX_DIGIT")

    // for logout
    val logoutIcon = hasContentDescription("LOG_OUT_ICON")

    @Test
    fun aaaa_login(){
        composeTestRule.onNode(inputField).performTextInput("9876543210")
        composeTestRule.onNode(button).performClick()
        Thread.sleep(2000)
        composeTestRule.onNode(firstDigit).performTextInput("2")
        composeTestRule.onNode(secondDigit).performTextInput("2")
        composeTestRule.onNode(thirdDigit).performTextInput("2")
        composeTestRule.onNode(fourDigit).performTextInput("2")
        composeTestRule.onNode(fiveDigit).performTextInput("2")
        composeTestRule.onNode(sixDigit).performTextInput("2")
        composeTestRule.onNode(button).performClick()
        Thread.sleep(2000)
    }

    @Test
    fun check_for_logout_icon(){
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(logoutIcon).assertExists("Logout icon should exists.")
    }

    @Test
    fun check_for_dialog_on_click_on_logout_icon(){
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(logoutIcon).performClick()
        composeTestRule.onNode(dialogTitle).assertTextEquals("Confirm logout"). assertExists("Dialog with title Confirm logout should be displayed.")
    }

    @Test
    fun check_content_of_dialog(){
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(logoutIcon).performClick()
        composeTestRule.onNode(dialogTitle).assertTextEquals("Confirm logout")
        composeTestRule.onNode(dialogDesc).assertTextEquals("Are you sure you want to logout")
        composeTestRule.onNode(dialogPositiveBtn).assertTextEquals("Logout")
        composeTestRule.onNode(dialogNegativeBtn).assertTextEquals("No, go back")
    }

    @Test
    fun click_on_no_go_back(){
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(logoutIcon).performClick()
        composeTestRule.onNode(dialogNegativeBtn).performClick()
        composeTestRule.onNode(profile_tab).assertExists()
        composeTestRule.onNode(dialogTitle).assertDoesNotExist()
    }

    @Test
    fun check_if_user_navigated_to_profile_screen_after_clicking_on_no_go_back(){
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(logoutIcon).performClick()
        composeTestRule.onNode(dialogNegativeBtn).performClick()
        composeTestRule.onNode(profileTitle).assertExists("Should navigate to profile screen.")
    }

    @Test
    fun z_check_if_user_logged_out_after_clicking_on_logout(){
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(logoutIcon).performClick()
        composeTestRule.onNode(dialogPositiveBtn).performClick()
        composeTestRule.onNode(profileTitle).assertDoesNotExist()
        composeTestRule.onNode(heading).assertTextEquals("Login with phone number or email address")
    }
}