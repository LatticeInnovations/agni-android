package com.latticeonfhir.android.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.latticeonfhir.android.ui.main.MainActivity
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class AdvancedSearchPatientKtTest {
    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    val searchIcon = hasContentDescription("SEARCH_ICON")
    val advancedSearchButton = hasText("Advanced search") and hasClickAction()

    // Placeholders
    val title = hasText("Advanced Search")
    val heading = hasText("Search using any of the field below")
    val ageRangeTitle = hasText("Select age range")
    val gender = hasText("Gender")
    val address = hasText("Address")
    val searchTitle = hasTestTag("SEARCH_TITLE_TEXT")
    val addPatientText = hasTestTag("ADD_PATIENT_TEXT")

    // Icons
    val clearIcon = hasContentDescription("CLEAR_ICON")

    // chips
    val chipCategory1 = hasText("Category 1")
    val chipCategory2 = hasText("Category 2")
    val chipCategory3 = hasText("Category 3")

    // Input Fields
    val patientName = hasTestTag("Patient Name") and hasClickAction()
    val patientId = hasTestTag("Patient Id") and hasClickAction()
    val postalCode = hasTestTag("Postal Code *") and hasClickAction()
    val addressLine1 = hasTestTag("Address Line 1 *") and hasClickAction()
    val addressLine2 = hasTestTag("Address Line 2") and hasClickAction()
    val city = hasTestTag("City *") and hasClickAction()
    val district = hasTestTag("District") and hasClickAction()
    val minValue = hasTestTag("MIN_VALUE") and hasClickAction()
    val maxValue = hasTestTag("MAX_VALUE") and hasClickAction()

    // Selection Chips
    val femaleChip = hasTestTag("female") and hasClickAction()
    val maleChip = hasTestTag("male") and hasClickAction()
    val othersChip = hasTestTag("other") and hasClickAction()

    // Button
    val searchBtn = hasText("Search") and hasClickAction()

    // Slider
    val ageRangeSlider = hasTestTag("age range slider")

    // list
    val patientList = hasTestTag("patients list") and hasScrollAction()

    // dropdown
    val lastFacilityVisit = hasTestTag("last facility visit") and hasClickAction()
    val state = hasTestTag("State *") and hasClickAction()

    // end of screen
    val endOfScreen = hasTestTag("END_OF_SCREEN")
    val rootLayout = hasTestTag("ROOT_LAYOUT")

    // bottom nav bar
    val bottomNavBar = hasTestTag("BOTTOM_NAV_BAR")

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
    val profile_tab = hasTestTag("Profile tab") and hasClickAction()
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
    fun advancedSearch_verify_if_all_views_exist() {
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(advancedSearchButton).performClick()
        composeTestRule.onNode(title).assertExists("Title should be \"Advanced Search\".")
        composeTestRule.onNode(clearIcon, useUnmergedTree = true).assertExists("Clear icon should exists.")
        composeTestRule.onNode(patientId).assertExists("Patient Id input field should be displayed.")
        composeTestRule.onNode(patientName).assertExists("Patient Name input field should be displayed.")
        composeTestRule.onNode(heading).assertExists("Heading \"Search using any of the field below\" should be displayed.")
        composeTestRule.onNode(ageRangeTitle).assertExists("Title \"Select age range\" should be displayed.")
        composeTestRule.onNode(ageRangeSlider).assertExists("Age Range Slider should be displayed.")
        composeTestRule.onNode(minValue).assertExists("Min age input field should be displayed.")
        composeTestRule.onNode(maxValue).assertExists("Max age input field should be displayed.")
        composeTestRule.onNode(gender).assertExists("Gender title should be displayed.")
        composeTestRule.onNode(femaleChip).assertExists("Female gender selection chip should be displayed.")
        composeTestRule.onNode(maleChip).assertExists("Male gender selection chip should be displayed.")
        composeTestRule.onNode(othersChip).assertExists("Others gender selection chip should be displayed.")
        composeTestRule.onNode(lastFacilityVisit).assertExists("Last facility visit dropdown should be displayed.")
        composeTestRule.onNode(rootLayout).performScrollToNode(endOfScreen)
        composeTestRule.onNode(address).assertExists("Address heading should exist.")
        composeTestRule.onNode(postalCode).assertExists("Postal code input field should exist.")
        composeTestRule.onNode(state).assertExists("State dropdown should exist.")
        composeTestRule.onNode(addressLine1).assertExists("Address Line 1 input field should exist.")
        composeTestRule.onNode(addressLine2).assertExists("Address Line 2 input field should exist.")
        composeTestRule.onNode(city).assertExists("City input field should exist.")
        composeTestRule.onNode(district).assertExists("District input field should exist.")
        composeTestRule.onNode(searchBtn).assertExists("Search button should be displayed.")
    }

    @Test
    fun advancedSearch_verify_last_facility_visit_dropdown() {
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(advancedSearchButton).performClick()
        composeTestRule.onNode(lastFacilityVisit).performClick()
        composeTestRule.onAllNodesWithText("Last week")[1].assertExists("Last week option should be displayed.")
        composeTestRule.onNodeWithText("Last month").assertExists("Last month option should be displayed.")
        composeTestRule.onNodeWithText("Last 3 months").assertExists("Last 3 months option should be displayed.")
        composeTestRule.onNodeWithText("Last year").assertExists("Last year option should be displayed.")
    }

    @Test
    fun advancedSearch_verify_clear_icon_click() {
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(advancedSearchButton).performClick()
        composeTestRule.onNode(clearIcon, useUnmergedTree = true).performClick()
        composeTestRule.onAllNodesWithText("My Patients")[0].assertExists("My Patients screen should be displayed.")
    }

    @Test
    fun advancedSearch_check_if_search_btn_is_enabled(){
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(advancedSearchButton).performClick()
        composeTestRule.onNode(searchBtn).assertIsEnabled()
    }

    @Test
    fun advancedSearch_select_single_gender_chip(){
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(advancedSearchButton).performClick()
        composeTestRule.onNode(femaleChip).performClick()
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(maleChip).assertIsNotSelected()
        composeTestRule.onNode(othersChip).assertIsNotSelected()
    }

    @Test
    fun advancedSearch_select_multiple_gender_chips(){
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(advancedSearchButton).performClick()
        composeTestRule.onNode(femaleChip).performClick()
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(maleChip).performClick()
        composeTestRule.onNode(femaleChip).assertIsNotSelected()
        composeTestRule.onNode(maleChip).assertIsSelected()
        composeTestRule.onNode(othersChip).performClick()
        composeTestRule.onNode(maleChip).assertIsNotSelected()
        composeTestRule.onNode(femaleChip).assertIsNotSelected()
        composeTestRule.onNode(othersChip).assertIsSelected()
    }

    @Test
    fun advancedSearch_verify_input_in_patient_name_input_field() {
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(advancedSearchButton).performClick()
        composeTestRule.onNode(patientName).performTextInput("input123")
        composeTestRule.onNode(patientName).assertTextEquals("Patient Name", "input123")
    }

    @Test
    fun advancedSearch_verify_input_in_patient_id_input_field() {
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(advancedSearchButton).performClick()
        composeTestRule.onNode(patientId).performTextInput("input12345")
        composeTestRule.onNode(patientId).assertTextEquals("Patient Id", "input12345")
    }

    @Test
    fun advancedSearch_default_min_age_value(){
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(advancedSearchButton).performClick()
        composeTestRule.onNode(minValue).assertTextEquals("0")
    }

    @Test
    fun advancedSearch_default_max_age_value(){
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(advancedSearchButton).performClick()
        composeTestRule.onNode(maxValue).assertTextEquals("100")
    }

    @Test
    fun advancedSearch_verify_search_btn_click(){
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(advancedSearchButton).performClick()
        composeTestRule.onNode(searchBtn).performClick()
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).assertExists("Search icon should be displayed.")
        composeTestRule.onNode(clearIcon, useUnmergedTree = true).assertExists("Clear icon should be displayed.")
        composeTestRule.onNode(bottomNavBar).assertExists("Bottom nav bar should be displayed.")
        composeTestRule.onNode(addPatientText, useUnmergedTree = true).assertExists("Add Patient button should be displayed.")
        composeTestRule.onNode(chipCategory1).assertExists(errorMessageOnFail = "Category 1 chip should be displayed.")
        composeTestRule.onNode(chipCategory2).assertExists(errorMessageOnFail = "Category 2 chip should be displayed.")
        composeTestRule.onNode(chipCategory3).assertExists(errorMessageOnFail = "Category 3 chip should be displayed.")
        composeTestRule.onNode(patientList).assertExists(errorMessageOnFail = "Patient List should be displayed.")
    }

    @Test
    fun zzzz_logout(){
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(logoutIcon).performClick()
        composeTestRule.onNodeWithText("Logout").performClick()
    }
}
