package com.latticeonfhir.android.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasNoClickAction
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.latticeonfhir.android.ui.main.MainActivity
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class HouseholdMemberKtTest {
    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    val landingScreenTitle = hasText("My Patients") and hasNoClickAction()
    val patient = hasTestTag("PATIENT")
    val patientList = hasTestTag("patients list") and hasScrollAction()
    val searchicon = hasContentDescription("SEARCH_ICON")

    // icons
    val backIcon = hasContentDescription("back icon")
    val clearIcon = hasContentDescription("clear icon")
    val moreIcon = hasContentDescription("more icon")

    // placeholders
    val title = hasTestTag("TITLE")
    val subtitle = hasTestTag("SUBTITLE")

    // cards
    val householdMemberCard = hasTestTag("HOUSEHOLD_MEMBER") and hasClickAction()
    val addPatientCard = hasTestTag("Add a patient") and hasNoClickAction()
    val searcbPatientCard = hasTestTag("Search patients") and hasNoClickAction()

    // buttons
    val addPatientBtn = hasText("Add a patient") and hasClickAction()
    val searchPatientBtn = hasText("Search patients") and hasClickAction()

    // tabs
    val tabRow = hasTestTag("TABS")
    val membersTab = hasTestTag("MEMBERS")
    val suggestionsTab = hasTestTag("SUGGESTIONS")

    // fabs
    val updateFab = hasTestTag("UPDATE_FAB")
    val addMemberFab = hasTestTag("ADD_MEMBER_FAB")
    val editExistingFab = hasTestTag("EDIT_EXISTING_FAB")
    val clearFab = hasTestTag("CLEAR_FAB")

    // relation dialog
    val dialogTitle = hasTestTag("DIALOG_TITLE")
    val dialogDismissIcon = hasContentDescription("CLEAR_ICON")
    val dialogRelationDropdown = hasTestTag("RELATIONS_DROPDOWN")
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
    fun verify_patient_item_click_navigate_to_patient_landing_screen(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        //composeTestRule.onNode(title).assertTextEquals("Mansi")
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
        //composeTestRule.onNode(title).assertTextEquals("Mansi")
        composeTestRule.onNode(backIcon, useUnmergedTree = true).assertExists("Back icon should be displayed.")
        composeTestRule.onNode(moreIcon, useUnmergedTree = true).assertExists("More icon should be displayed.")
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
        //composeTestRule.onNode(title).assertTextEquals("Mansi")
        composeTestRule.onNode(backIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(landingScreenTitle).assertExists("Should have navigated to My Patients screen.")
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
        //composeTestRule.onNode(title).assertTextEquals("Mansi")
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
    fun addHouseholdMember_verify_all_components() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(householdMemberCard).performClick()
        composeTestRule.onNode(title).assertTextEquals("Household members")
        composeTestRule.onNode(updateFab).performClick()
        composeTestRule.onNode(addMemberFab).performClick()
        composeTestRule.onNode(backIcon, useUnmergedTree = true).assertExists("Back icon should exists")
        composeTestRule.onNode(addPatientCard).assertExists("Add a patient card should exists")
        composeTestRule.onNode(addPatientBtn).assertExists("Add a patient btn should exists")
        composeTestRule.onNodeWithText("Create a new patient, and\n" +
                "include them in the household").assertExists()
        composeTestRule.onNodeWithText("Search for an existing patient, and\n" +
                "include them in the household").assertExists()
        composeTestRule.onNode(searcbPatientCard).assertExists("Search patients card should exists")
        composeTestRule.onNode(searchPatientBtn).assertExists("Search patients btn should exists")
    }

    @Test
    fun addHouseholdMember_verify_back_btn_click() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(householdMemberCard).performClick()
        composeTestRule.onNode(title).assertTextEquals("Household members")
        composeTestRule.onNode(updateFab).performClick()
        composeTestRule.onNode(addMemberFab).performClick()
        composeTestRule.onNode(backIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextContains("Household members")
    }

    @Test
    fun addHouseholdMember_verify_add_a_patient_btn_click() {
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
        composeTestRule.onNode(addPatientBtn).performClick()
        composeTestRule.onNode(dialogTitle).assertTextContains("Establish relation")
    }

    @Test
    fun addHouseholdMember_verify_relation_dialog_components() {
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
        composeTestRule.onNode(addPatientBtn).performClick()
        composeTestRule.onNode(dialogTitle).assertTextContains("Establish relation")
        composeTestRule.onNode(dialogDismissIcon).assertExists("Dialog dismiss icon should exists.")
        composeTestRule.onNode(dialogRelationDropdown).assertExists("Dialog relation dropdown should exists.")
        composeTestRule.onNode(dialogNegativeBtn).assertTextContains("Go back").assertExists("Dialog negative btn should exists.")
        composeTestRule.onNode(dialogPositiveBtn).assertTextContains("Create").assertExists("Dialog positive btn should exists.")
    }

    @Test
    fun addHouseholdMember_verify_relation_dialog_positive_button_disabled() {
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
        composeTestRule.onNode(addPatientBtn).performClick()
        composeTestRule.onNode(dialogPositiveBtn).assertIsNotEnabled()
    }

    @Test
    fun addHouseholdMember_verify_relation_dialog_negative_button_click() {
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
        composeTestRule.onNode(addPatientBtn).performClick()
        composeTestRule.onNode(dialogNegativeBtn).performClick()
        composeTestRule.onNode(title).assertTextEquals("Add a household member")
    }

    @Test
    fun addHouseholdMember_verify_relation_dialog_dismiss_icon_click() {
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
        composeTestRule.onNode(addPatientBtn).performClick()
        composeTestRule.onNode(dialogTitle).assertTextContains("Establish relation")
        composeTestRule.onNode(dialogDismissIcon).performClick()
        composeTestRule.onNodeWithText("Patient Registration").assertExists("should have navigated to patient registration screen")
        composeTestRule.onNodeWithText("Page 1/3").assertExists("total steps should be 3")
    }

    @Test
    fun addHouseholdMember_patient_Registration_clear_button_click() {
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
        composeTestRule.onNode(addPatientBtn).performClick()
        composeTestRule.onNode(dialogTitle).assertTextContains("Establish relation")
        composeTestRule.onNode(dialogDismissIcon).performClick()
        composeTestRule.onNode(clearIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Add a household member")
    }

    @Test
    fun zzzz_logout(){
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(logoutIcon).performClick()
        composeTestRule.onNodeWithText("Logout").performClick()
    }
}