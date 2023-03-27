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
    val title = hasText("My Patients")
    val addPatientText = hasTestTag("add patient text")

    // Icons
    val menuIcon = hasTestTag("menu icon")
    val searchIcon = hasTestTag("search icon")
    val userIcon = hasTestTag("user icon")
    val addPatientIcon = hasTestTag("add patient icon")

    // chips
    val chipCategory1 = hasText("Category 1")
    val chipCategory2 = hasText("Category 2")
    val chipCategory3 = hasText("Category 3")

    // list
    val patientList = hasTestTag("patients list") and hasScrollAction()

    @Test
    fun landingScreen_verify_if_all_views_exist() {
        composeTestRule.onNode(title).assertExists(errorMessageOnFail = "Title should be \"My Patients\".")
        composeTestRule.onNode(menuIcon, useUnmergedTree = true).assertExists(errorMessageOnFail = "Menu Icon should be displayed.")
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).assertExists(errorMessageOnFail = "Search Icon should be displayed.")
        composeTestRule.onNode(userIcon, useUnmergedTree = true).assertExists(errorMessageOnFail = "User Icon should be displayed.")
        composeTestRule.onNode(chipCategory1).assertExists(errorMessageOnFail = "Category 1 chip should be displayed.")
        composeTestRule.onNode(chipCategory2).assertExists(errorMessageOnFail = "Category 2 chip should be displayed.")
        composeTestRule.onNode(chipCategory3).assertExists(errorMessageOnFail = "Category 3 chip should be displayed.")
        composeTestRule.onNode(patientList).assertExists(errorMessageOnFail = "Patient List should be displayed.")
        composeTestRule.onNode(addPatientIcon, useUnmergedTree = true).assertExists(errorMessageOnFail = "Add Patient Icon should be displayed.")
        composeTestRule.onNode(addPatientText, useUnmergedTree = true).assertExists(errorMessageOnFail = "Add Patient Text should be displayed.")
    }

    @Test
    fun landingScreen_verify_search_icon_click(){
        composeTestRule.onNode(searchIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithText("Search Patient").assertExists(errorMessageOnFail = "Search Patient screen should be displayed.")
    }

    @Test
    fun landingScreen_verify_add_patient_button_click(){
        composeTestRule.onNode(addPatientText, useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithText("Patient Registration").assertExists(errorMessageOnFail = "Patient Registration screen should be displayed.")
    }
}