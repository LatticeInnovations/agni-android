package com.latticeonfhir.android.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsSelected
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
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class PreviousPrescriptionKtTest: UiTestsBase() {
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

    private fun add_a_prescription(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(prescriptionCard).performClick()
        composeTestRule.onNode(quickSelectTab).performClick()
        composeTestRule.onAllNodes(checkBoxes)[0].performClick()
        composeTestRule.onAllNodes(formulationsList)[0].performClick()
        composeTestRule.onNode(duration).performTextInput("2")
        composeTestRule.onNode(doneBtn).performClick()
        composeTestRule.onNode(prescribeBtn).performClick()
    }

    @Test
    fun check_for_patient_list(){
        composeTestRule.onNode(titleMyPatients).assertExists()
        composeTestRule.onNode(patientList).assertExists("patient list should exists")
    }

    @Test
    fun check_patient_navigation() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(householdMemberCard).assertExists("Should have navigated to patient landing screen with household members card.")
        composeTestRule.onNode(prescriptionCard).assertExists("Should have navigated to patient landing screen with prescription card.")
    }

    @Test
    fun check_navigation_of_prescription_card() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(prescriptionCard).performClick()
        composeTestRule.onNode(heading).assertTextEquals("Prescription")
    }

    @Test
    fun check_for_tabs() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(prescriptionCard).performClick()
        composeTestRule.onNode(previousPrescriptionTab).assertExists("previous prescription tag should exists.")
        composeTestRule.onNode(quickSelectTab).assertExists("Quick select tab should exists.")
    }

    @Test
    fun check_for_default_tab_selected(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(prescriptionCard).performClick()
        composeTestRule.onNode(previousPrescriptionTab).assertIsSelected()
    }

    @Test
    fun check_for_options_on_previous_prescription_screen(){
        add_a_prescription()
        composeTestRule.onNode(backIcon, useUnmergedTree = true).assertExists()
        composeTestRule.onAllNodes(previousPrescriptionCards).fetchSemanticsNodes(true, "Previous prescription drop down row should be displayed.")
    }

    @Test
    fun check_click_on_previous_prescription_dropdown(){
        add_a_prescription()
        composeTestRule.onAllNodes(previousPrescriptionCardsTitleRow)[0].performClick()
        composeTestRule.onAllNodes(previousPrescriptionExpandedCards)[0].assertExists()
    }

    @Test
    fun check_for_re_prescribe_btn(){
        add_a_prescription()
        composeTestRule.onAllNodes(previousPrescriptionCardsTitleRow)[0].performClick()
        composeTestRule.onNode(represcribeBtn).assertExists()
    }

    @Test
    fun check_if_the_drop_down_collapsed(){
        add_a_prescription()
        composeTestRule.onAllNodes(previousPrescriptionCardsTitleRow)[0].performClick()
        composeTestRule.onAllNodes(previousPrescriptionExpandedCards)[0].assertExists()
        composeTestRule.onAllNodes(previousPrescriptionCardsTitleRow)[0].performClick()
        composeTestRule.onAllNodes(previousPrescriptionExpandedCards)[0].assertDoesNotExist()
    }

    @Test
    fun click_on_re_prescribe_btn() {
        add_a_prescription()
        composeTestRule.onAllNodes(previousPrescriptionCardsTitleRow)[0].performClick()
        composeTestRule.onNode(represcribeBtn).performClick()
        composeTestRule.onNode(bottomNavRow).assertExists()
    }

    @Test
    fun check_for_btns_in_bottom_nav_bar(){
        add_a_prescription()
        composeTestRule.onAllNodes(previousPrescriptionCardsTitleRow)[0].performClick()
        composeTestRule.onNode(represcribeBtn).performClick()
        composeTestRule.onNode(prescribeBtn).assertExists()
        composeTestRule.onNode(upArrowIcon).assertExists()
    }

    @Test
    fun check_click_on_expansion_icon(){
        add_a_prescription()
        composeTestRule.onAllNodes(previousPrescriptionCardsTitleRow)[0].performClick()
        composeTestRule.onNode(represcribeBtn).performClick()
        composeTestRule.onNode(upArrowIcon).performClick()
        composeTestRule.onNode(bottomNavExpanded, useUnmergedTree = true).assertExists()
    }

    @Test
    fun check_title_of_bottom_nav_expanded() {
        add_a_prescription()
        composeTestRule.onAllNodes(previousPrescriptionCardsTitleRow)[0].performClick()
        composeTestRule.onNode(represcribeBtn).performClick()
        composeTestRule.onNode(upArrowIcon).performClick()
        composeTestRule.onNode(medicationTitle, useUnmergedTree = true).assertExists().assertTextEquals("Medication (s)")
    }

    @Test
    fun check_for_btns_on_bottom_nav_expanded(){
        add_a_prescription()
        composeTestRule.onAllNodes(previousPrescriptionCardsTitleRow)[0].performClick()
        composeTestRule.onNode(represcribeBtn).performClick()
        composeTestRule.onNode(upArrowIcon).performClick()
        composeTestRule.onNode(clearIcon, useUnmergedTree = true).assertExists()
        composeTestRule.onNode(clearAllBtn, useUnmergedTree = true).assertExists()
        composeTestRule.onNode(editIcon, useUnmergedTree = true).assertExists()
        composeTestRule.onNode(prescribeBtn, useUnmergedTree = true).assertExists()
    }

    @Test
    fun check_click_on_clear_all(){
        add_a_prescription()
        composeTestRule.onAllNodes(previousPrescriptionCardsTitleRow)[0].performClick()
        composeTestRule.onNode(represcribeBtn).performClick()
        composeTestRule.onNode(upArrowIcon).performClick()
        composeTestRule.onNode(clearAllBtn, useUnmergedTree = true).performClick()
        composeTestRule.onNode(dialogTitle).assertExists("Discard medications dialog should be displayed.")
    }

    @Test
    fun check_contents_of_dialog() {
        add_a_prescription()
        composeTestRule.onAllNodes(previousPrescriptionCardsTitleRow)[0].performClick()
        composeTestRule.onNode(represcribeBtn).performClick()
        composeTestRule.onNode(upArrowIcon).performClick()
        composeTestRule.onNode(clearAllBtn, useUnmergedTree = true).performClick()
        composeTestRule.onNode(dialogTitle).assertTextEquals("Discard medications ?")
        composeTestRule.onNode(dialogDesc).assertTextEquals("Are you sure you want to discard all medications ?")
        composeTestRule.onNode(dialogPositiveBtn).assertTextEquals("Yes, discard")
        composeTestRule.onNode(dialogNegativeBtn).assertTextEquals("No, go back")
    }

    @Test
    fun check_no_go_back_click(){
        add_a_prescription()
        composeTestRule.onAllNodes(previousPrescriptionCardsTitleRow)[0].performClick()
        composeTestRule.onNode(represcribeBtn).performClick()
        composeTestRule.onNode(upArrowIcon).performClick()
        composeTestRule.onNode(clearAllBtn, useUnmergedTree = true).performClick()
        composeTestRule.onNode(dialogNegativeBtn).performClick()
        composeTestRule.onNode(dialogTitle).assertDoesNotExist()
        composeTestRule.onNode(bottomNavRow, useUnmergedTree = true).assertExists()
    }

    @Test
    fun check_yes_discard_click(){
        add_a_prescription()
        composeTestRule.onAllNodes(previousPrescriptionCardsTitleRow)[0].performClick()
        composeTestRule.onNode(represcribeBtn).performClick()
        composeTestRule.onNode(upArrowIcon).performClick()
        composeTestRule.onNode(clearAllBtn, useUnmergedTree = true).performClick()
        composeTestRule.onNode(dialogPositiveBtn).performClick()
        composeTestRule.onNode(dialogTitle).assertDoesNotExist()
        composeTestRule.onNode(heading).assertExists()
        composeTestRule.onNode(bottomNavRow, useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun check_click_on_clear_icon(){
        add_a_prescription()
        composeTestRule.onAllNodes(previousPrescriptionCardsTitleRow)[0].performClick()
        composeTestRule.onNode(represcribeBtn).performClick()
        composeTestRule.onNode(upArrowIcon).performClick()
        composeTestRule.onNode(clearIcon).performClick()
        composeTestRule.onNode(heading).assertExists()
        composeTestRule.onNode(bottomNavRow, useUnmergedTree = true).assertExists()
    }

    @Test
    fun check_click_on_edit_icon() {
        add_a_prescription()
        composeTestRule.onAllNodes(previousPrescriptionCardsTitleRow)[0].performClick()
        composeTestRule.onNode(represcribeBtn).performClick()
        composeTestRule.onNode(upArrowIcon).performClick()
        composeTestRule.onNode(editIcon).performClick()
        composeTestRule.onNodeWithText("Fill details").assertExists()
    }

    @Test
    fun check_btns_on_fill_details(){
        add_a_prescription()
        composeTestRule.onAllNodes(previousPrescriptionCardsTitleRow)[0].performClick()
        composeTestRule.onNode(represcribeBtn).performClick()
        composeTestRule.onNode(upArrowIcon).performClick()
        composeTestRule.onNode(editIcon).performClick()
        composeTestRule.onAllNodes(clearIcon).assertCountEquals(2)
        composeTestRule.onNode(doneBtn).assertExists()
    }

    @Test
    fun zzzz_logout(){
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(logoutIcon).performClick()
        composeTestRule.onNodeWithText("Logout").performClick()
    }
}