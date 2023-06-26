package com.latticeonfhir.android.ui

import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class AdvancedSearchPatientKtTest: UiTestsBase() {
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
        composeTestRule.onNode(titleAdvancedSearch).assertExists("Title should be \"Advanced Search\".")
        composeTestRule.onNode(clearIcon, useUnmergedTree = true).assertExists("Clear icon should exists.")
        composeTestRule.onNode(patientId).assertExists("Patient ID input field should be displayed.")
        composeTestRule.onNode(patientName).assertExists("Patient Name input field should be displayed.")
        composeTestRule.onNode(headingSearch).assertExists("Heading \"Search using any of the field below\" should be displayed.")
        composeTestRule.onNode(ageRangeTitle).assertExists("Title \"Select age range\" should be displayed.")
        composeTestRule.onNode(ageRangeSlider).assertExists("Age Range Slider should be displayed.")
        composeTestRule.onNode(minValue).assertExists("Min age input field should be displayed.")
        composeTestRule.onNode(maxValue).assertExists("Max age input field should be displayed.")
        composeTestRule.onNode(gender).assertExists("Gender titleAdvancedSearch should be displayed.")
        composeTestRule.onNode(femaleChip).assertExists("Female gender selection chip should be displayed.")
        composeTestRule.onNode(maleChip).assertExists("Male gender selection chip should be displayed.")
        composeTestRule.onNode(othersChip).assertExists("Others gender selection chip should be displayed.")
        composeTestRule.onNode(lastFacilityVisit).assertExists("Last facility visit dropdown should be displayed.")
        composeTestRule.onNode(rootLayout).performScrollToNode(endOfScreen)
        composeTestRule.onNode(address).assertExists("Address headingSearch should exist.")
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
        composeTestRule.onNode(patientId).assertTextEquals("Patient ID", "input12345")
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
        composeTestRule.onNode(profileTab).performClick()
        composeTestRule.onNode(logoutIcon).performClick()
        composeTestRule.onNodeWithText("Logout").performClick()
    }
}
