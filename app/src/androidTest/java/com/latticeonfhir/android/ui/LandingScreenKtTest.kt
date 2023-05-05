package com.latticeonfhir.android.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.latticeonfhir.android.ui.main.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LandingScreenKtTest {
    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // PlaceHolders
    val title = hasText("My Patients") and hasNoClickAction()
    val searchTitle = hasTestTag("SEARCH_TITLE_TEXT")
    val queueTitle = hasText("Queue") and hasNoClickAction()
    val profileTitle = hasText("Profile") and hasNoClickAction()
    val addPatientText = hasTestTag("ADD_PATIENT_TEXT")

    // Icons
    val searchIcon = hasContentDescription("SEARCH_ICON")
    val addPatientIcon = hasContentDescription("ADD_PATIENT_ICON")
    val clearIcon = hasContentDescription("CLEAR_ICON")
    val backIcon = hasContentDescription("BACK_ICON")

    // chips
    val chipCategory1 = hasText("Category 1")
    val chipCategory2 = hasText("Category 2")
    val chipCategory3 = hasText("Category 3")

    // list
    val patientList = hasTestTag("patients list") and hasScrollAction()
    val previousSearchList = hasTestTag("PREVIOUS_SEARCHES") and hasScrollAction()

    // bottom nav bar
    val bottomNavBar = hasTestTag("BOTTOM_NAV_BAR")

    // bottom nav bar icons
    val my_patients_icon = hasContentDescription("My Patients")
    val queue_icon = hasContentDescription("Queue")
    val profile_icon = hasContentDescription("Profile")

    // bottom nav bar tabs
    val my_patients_tab = hasTestTag("My Patients tab") and hasClickAction()
    val queue_tab = hasTestTag("Queue tab") and hasClickAction()
    val profile_tab = hasTestTag("Profile tab") and hasClickAction()

    //search layout
    val searchLayout = hasTestTag("SEARCH_LAYOUT")
    val searchTextField = hasTestTag("SEARCH_TEXT_FIELD")

    // buttons
    val advancedSearchButton = hasText("Advanced search") and hasClickAction()

    @Test
    fun landingScreen_verify_if_all_views_exist() {
        composeTestRule.onNode(title).assertExists(errorMessageOnFail = "Title should be \"My Patients\".")
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

    fun landingScreen_verify_profile_tab_click (){
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(profileTitle).assertExists("Should have navigated to Profile screen")
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
        composeTestRule.onNode(previousSearchList).assertExists("Previous serach list should be displayed.")
    }

    @Test
    fun searchLayout_verify_back_button_click(){
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(backIcon).performClick()
        composeTestRule.onNode(searchLayout).assertDoesNotExist()
        composeTestRule.onNode(title).assertExists("Should navigate back to landing screen.")
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
        composeTestRule.onNode(title).assertExists(errorMessageOnFail = "Should be navigated to \"My Patients\" screen.")
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
}