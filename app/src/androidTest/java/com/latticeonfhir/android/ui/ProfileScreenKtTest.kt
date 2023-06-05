package com.latticeonfhir.android.ui

import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasNoClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
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
class ProfileScreenKtTest {
    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // profile
    val profileTitle = hasText("Profile") and hasNoClickAction()
    val profile_icon = hasContentDescription("Profile")
    val profile_tab = hasTestTag("Profile tab") and hasClickAction()
    val nameLabel = hasText("Name")
    val nameDetail = hasTestTag("NAME")
    val roleLabel = hasText("Role")
    val roleDetails = hasTestTag("ROLE")
    val numberLabel = hasText("Phone No.")
    val numberDetails = hasTestTag("PHONE_NO")
    val emailLabel = hasText("Email")
    val emailDetails = hasTestTag("EMAIL")

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
    fun check_for_profile_navigation_on_landing_screen(){
        composeTestRule.onNode(profile_icon, useUnmergedTree = true).assertExists(errorMessageOnFail = "Profile icon should be displayed in bottom nav bar.")
        composeTestRule.onNode(profile_tab, useUnmergedTree = true).assertExists(errorMessageOnFail = "Profile text should be displayed in bottom nav bar.")
    }

    @Test
    fun check_if_user_able_to_click_on_profile_tab(){
        composeTestRule.onNode(profile_tab).performClick()
    }

    @Test
    fun check_profile_tab_click_navigation(){
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(profileTitle).assertExists("Should have navigated to Profile screen")
    }

    @Test
    fun check_profile_screen_heading(){
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(profileTitle).assertExists("Heading should be Profile")
    }

    @Test
    fun check_for_logout_button(){
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(logoutIcon).assertExists("Logout icon should be displayed.")
    }

    @Test
    fun check_for_profile_screen_content(){
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(nameLabel).assertExists("Name label should exists")
        composeTestRule.onNode(nameDetail).assertExists("Name detail should exists")
        composeTestRule.onNode(roleLabel).assertExists("Role label should exists")
        composeTestRule.onNode(roleDetails).assertExists("Role detail should exists")
        composeTestRule.onNode(numberLabel).assertExists("Phone no. label should exists")
        composeTestRule.onNode(numberDetails).assertExists("Phone no. detail should exists")
        composeTestRule.onNode(emailLabel).assertExists("Email label should exists")
        composeTestRule.onNode(emailDetails).assertExists("Email detail should exists")
    }

    @Test
    fun zzzz_logout(){
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(logoutIcon).performClick()
        composeTestRule.onNodeWithText("Logout").performClick()
    }
}