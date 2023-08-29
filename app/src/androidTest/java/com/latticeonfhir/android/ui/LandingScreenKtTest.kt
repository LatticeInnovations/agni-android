package com.latticeonfhir.android.ui

import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@OptIn(ExperimentalTestApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class LandingScreenKtTest: UiTestsBase() {
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
    fun landingScreen_verify_if_all_views_exist() {
        composeTestRule.onNode(titleMyPatients).assertExists(errorMessageOnFail = "Title should be \"My Patients\".")
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).assertExists(errorMessageOnFail = "Search Icon should be displayed.")
        composeTestRule.onNode(chipCategory1).assertExists(errorMessageOnFail = "Category 1 chip should be displayed.")
        composeTestRule.onNode(chipCategory2).assertExists(errorMessageOnFail = "Category 2 chip should be displayed.")
        composeTestRule.onNode(chipCategory3).assertExists(errorMessageOnFail = "Category 3 chip should be displayed.")
        composeTestRule.onNode(patientList).assertExists(errorMessageOnFail = "Patient List should be displayed.")
        composeTestRule.onNode(bottomNavBar).assertExists("Bottom nav bar should be displayed.")
        composeTestRule.onNode(addPatientIcon, useUnmergedTree = true).assertExists(errorMessageOnFail = "Add Patient Icon should be displayed.")
        composeTestRule.onNode(addPatientText, useUnmergedTree = true).assertExists(errorMessageOnFail = "Add Patient Text should be displayed.")
        composeTestRule.onNode(my_patients_icon, useUnmergedTree = true).assertExists(errorMessageOnFail = "My Patients icon should be displayed in bottom nav bar.")
        composeTestRule.onNode(queue_icon, useUnmergedTree = true).assertExists(errorMessageOnFail = "Queue should icon be displayed in bottom nav bar.")
        composeTestRule.onNode(profile_icon, useUnmergedTree = true).assertExists(errorMessageOnFail = "Profile icon should be displayed in bottom nav bar.")
        composeTestRule.onNode(my_patients_tab, useUnmergedTree = true).assertExists(errorMessageOnFail = "My Patients text should be displayed in bottom nav bar.")
        composeTestRule.onNode(queue_tab, useUnmergedTree = true).assertExists(errorMessageOnFail = "Queue should text be displayed in bottom nav bar.")
        composeTestRule.onNode(profile_tab, useUnmergedTree = true).assertExists(errorMessageOnFail = "Profile text should be displayed in bottom nav bar.")
    }

    @Test
    fun landingScreen_verify_queue_tab_click (){
        composeTestRule.onNode(queue_tab).performClick()
        composeTestRule.onNode(queueTitle).assertExists("Should have navigated to Queue screen")
    }

    @Test
    fun landingScreen_verify_profile_tab_click (){
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(profileTitle).assertExists("Should have navigated to Profile screen")
    }

    @Test
    fun landingScreen_verify_patient_click (){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(householdMemberCard).assertExists("Should have navigated to patient landing screen.")
    }

    @Test
    fun landingScreen_verify_add_patient_button_click(){
        composeTestRule.onNode(addPatientText, useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithText("Patient Registration").assertExists(errorMessageOnFail = "Patient Registration screen should be displayed.")
    }

    @Test
    fun landingScreen_verify_search_icon_click(){
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(searchLayout).assertExists("Search Layout should be displayed.")
        composeTestRule.onNode(backIcon, useUnmergedTree = true).assertExists("Back icon should be displayed.")
        composeTestRule.onNode(advancedSearchButton).assertExists("Advanced search button should be displayed.")
        composeTestRule.onNode(advancedSearchButton).assertIsEnabled()
        composeTestRule.onNode(previousSearchList, useUnmergedTree = true).assertExists("Previous search list should be displayed.")
    }

    @Test
    fun searchLayout_verify_back_button_click(){
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(backIcon).performClick()
        composeTestRule.onNode(searchLayout).assertDoesNotExist()
        composeTestRule.onNode(titleMyPatients).assertExists("Should navigate back to landing screen.")
    }

    @Test
    fun searchLayout_verify_clear_icon_visibility_on_text_input(){
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(searchTextField).performTextInput("input")
        composeTestRule.onNode(clearIcon, useUnmergedTree = true).assertExists("Clear icon should be visible on input.")
    }

    @Test
    fun searchLayout_verify_clear_icon_click(){
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(searchTextField).performTextInput("input")
        composeTestRule.onNode(searchTextField).assertTextEquals("input")
        composeTestRule.onNode(clearIcon, useUnmergedTree = true).assertExists("Clear icon should be visible on input.")
        composeTestRule.onNode(clearIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(searchTextField).assertTextEquals("")
    }

    @Test
    fun searchLayout_verify_search_field_text_input() {
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(searchTextField).performTextInput("input")
        composeTestRule.onNode(searchTextField).assertTextEquals("input")
    }

    @Test
    fun searchLayout_verify_search_field_number_input() {
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(searchTextField).performTextInput("123456")
        composeTestRule.onNode(searchTextField).assertTextEquals("123456")
    }

    @Test
    fun searchLayout_verify_advanced_search_button_click() {
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(advancedSearchButton).performClick()
        composeTestRule.onNodeWithText("Advanced Search").assertExists("Should be navigated to Advanced Search Screen.")
    }

    @Test
    fun searchLayout_verify_previous_search_list() {
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(searchTextField).performTextInput("input")
        composeTestRule.onNode(searchTextField).assertTextEquals("input")
        composeTestRule.onNode(searchTextField).performImeAction()
        composeTestRule.onNode(clearIcon).performClick()
        composeTestRule.onNode(searchIcon).performClick()
        Thread.sleep(5000)
        composeTestRule.onAllNodesWithText("input")[0].assertExists("Previous search should be present in the list.")
    }

    @Test
    fun searchLayout_verify_search_field_ime_action() {
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(searchTextField).performTextInput("input")
        composeTestRule.onNode(searchTextField).performImeAction()
        composeTestRule.onNode(searchLayout).assertDoesNotExist()
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
    fun searchResultScreen_verify_clear_icon_click() {
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(searchTextField).performTextInput("input")
        composeTestRule.onNode(searchTextField).performImeAction()
        composeTestRule.onNode(searchLayout).assertDoesNotExist()
        composeTestRule.onNode(clearIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(titleMyPatients).assertExists(errorMessageOnFail = "Should be navigated to \"My Patients\" screen.")
    }

    @Test
    fun searchResultScreen_verify_search_icon_click() {
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(searchTextField).performTextInput("input")
        composeTestRule.onNode(searchTextField).performImeAction()
        composeTestRule.onNode(searchLayout).assertDoesNotExist()
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithText("Advanced Search").assertExists("Should be navigated to Advanced Search Screen.")
    }


    @Test
    fun zzzz_logout(){
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(logoutIcon).performClick()
        composeTestRule.onNodeWithText("Logout").performClick()
    }
}