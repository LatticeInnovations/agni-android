package com.latticeonfhir.android.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class HouseholdMemberKtTest: UiTestsBase() {
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
    fun verify_patient_item_click_navigate_to_patient_landing_screen(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        //composeTestRule.onNode(titleAdvancedSearch).assertTextEquals("Mansi")
        composeTestRule.onNode(householdMemberCard).assertExists("Should have navigated to patient landing screen.")
    }

    @Test
    fun patientLandingScreen_verify_if_all_components_exists(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        //composeTestRule.onNode(titleAdvancedSearch).assertTextEquals("Mansi")
        composeTestRule.onNode(backIcon, useUnmergedTree = true).assertExists("Back icon should be displayed.")
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).assertExists("Profile icon should be displayed.")
        composeTestRule.onNode(householdMemberCard).assertExists("Household member card should be displayed.")
    }

    @Test
    fun patientLandingScreen_verify_back_btn_click(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        //composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        //composeTestRule.onNode(titleAdvancedSearch).assertTextEquals("Mansi")
        composeTestRule.onNode(backIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(titleMyPatients).assertExists("Should have navigated to My Patients screen.")
    }

    @Test
    fun patientLandingScreen_verify_household_member_card_click_navigate_to_household_members_screen(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        //composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(householdMemberCard).performClick()
        composeTestRule.onNode(title).assertTextEquals("Household members")
    }

    @Test
    fun householdMemberScreen_verify_all_components() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(householdMemberCard).performClick()
        //composeTestRule.onNode(subtitle).assertTextEquals("Mansi, F/22")
        composeTestRule.onNode(backIcon, useUnmergedTree = true).assertExists("back button should exists")
        composeTestRule.onNode(tabRow).assertExists("Tabs should exists")
        composeTestRule.onNode(membersTab).assertExists("Members tab should exists")
        composeTestRule.onNode(suggestionsTab).assertExists("Suggestions tab should exists")
        composeTestRule.onNode(membersTab).assertIsSelected()
    }

    @Test
    fun householdMemberScreen_verify_tab_change() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(householdMemberCard).performClick()
        composeTestRule.onNode(title).assertTextEquals("Household members")
        composeTestRule.onNode(suggestionsTab).performClick()
        composeTestRule.onNode(suggestionsTab).assertIsSelected()
        composeTestRule.onNode(membersTab).assertIsNotSelected()
        composeTestRule.onNode(membersTab).performClick()
        composeTestRule.onNode(membersTab).assertIsSelected()
        composeTestRule.onNode(suggestionsTab).assertIsNotSelected()
    }

    @Test
    fun householdMemberScreen_verify_members_tab_components() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(householdMemberCard).performClick()
        composeTestRule.onNode(title).assertTextEquals("Household members")
        composeTestRule.onNode(membersTab).performClick()
        composeTestRule.onNode(membersTab).assertIsSelected()
        composeTestRule.onNode(updateFab).assertExists("Update fab should be displayed.")
    }

    @Test
    fun householdMemberScreen_verify_suggestions_tab_components() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(householdMemberCard).performClick()
        composeTestRule.onNode(title).assertTextEquals("Household members")
        composeTestRule.onNode(suggestionsTab).performClick()
        composeTestRule.onNode(suggestionsTab).assertIsSelected()
        composeTestRule.onNode(updateFab).assertDoesNotExist()
    }

    @Test
    fun householdMemberScreen_verify_update_fab_click() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(householdMemberCard).performClick()
        composeTestRule.onNode(title).assertTextEquals("Household members")
        composeTestRule.onNode(updateFab).performClick()
        composeTestRule.onNode(updateFab).assertDoesNotExist()
        composeTestRule.onNode(addMemberFab).assertExists("Add member fab should be visible.")
        composeTestRule.onNode(editExistingFab).assertExists("Edit existing fab should be visible.")
        composeTestRule.onNode(clearFab).assertExists("Clear fab should be visible.")
    }

    @Test
    fun householdMemberScreen_verify_clear_fab_click() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(householdMemberCard).performClick()
        composeTestRule.onNode(updateFab).performClick()
        composeTestRule.onNode(clearFab).performClick()
        composeTestRule.onNode(updateFab).assertExists("Update fab should be visible")
        composeTestRule.onNode(addMemberFab).assertDoesNotExist()
        composeTestRule.onNode(editExistingFab).assertDoesNotExist()
        composeTestRule.onNode(clearFab).assertDoesNotExist()
    }

    @Test
    fun householdMemberScreen_verify_back_btn_click() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(householdMemberCard).performClick()
        composeTestRule.onNode(title).assertTextEquals("Household members")
        composeTestRule.onNode(backIcon, useUnmergedTree = true).performClick()
        //composeTestRule.onNode(titleAdvancedSearch).assertTextEquals("Mansi")
        composeTestRule.onNode(householdMemberCard).assertExists("Should have navigated to patient landing screen.")
    }

    @Test
    fun householdMemberScreen_verify_add_member_fab_click() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(householdMemberCard).performClick()
        composeTestRule.onNode(title).assertTextEquals("Household members")
        composeTestRule.onNode(updateFab).performClick()
        composeTestRule.onNode(addMemberFab).performClick()
        composeTestRule.onNode(title).assertTextEquals("Add a household member")
    }

    @Test
    fun zzzz_logout(){
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(logoutIcon).performClick()
        composeTestRule.onNodeWithText("Logout").performClick()
    }
}