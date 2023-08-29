package com.latticeonfhir.android.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
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
class SearchPrescriptionKtTest : UiTestsBase(){
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
    fun search_icon_on_previous_prescription_screen(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(prescriptionCard).performClick()
        composeTestRule.onNode(previousPrescriptionTab).assertIsSelected()
        composeTestRule.onNode(searchIcon).assertDoesNotExist()
    }

    @Test
    fun search_icon_on_quick_Select_screen(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(prescriptionCard).performClick()
        composeTestRule.onNode(quickSelectTab).performClick()
        composeTestRule.onNode(searchIcon).assertExists()
    }

    @Test
    fun check_click_on_search_icon(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(prescriptionCard).performClick()
        composeTestRule.onNode(quickSelectTab).performClick()
        composeTestRule.onNode(searchIcon).performClick()
        composeTestRule.onNode(searchLayout).assertExists()
        composeTestRule.onNode(searchTextField).assertExists()
    }

    @Test
    fun check_for_back_icon_on_search_bar(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(prescriptionCard).performClick()
        composeTestRule.onNode(quickSelectTab).performClick()
        composeTestRule.onNode(searchIcon).performClick()
        composeTestRule.onAllNodes(backIcon).assertCountEquals(2)
    }

    @Test
    fun check_back_btn_click(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(prescriptionCard).performClick()
        composeTestRule.onNode(quickSelectTab).performClick()
        composeTestRule.onNode(searchIcon).performClick()
        composeTestRule.onAllNodes(backIcon)[1].performClick()
        composeTestRule.onNode(searchLayout).assertDoesNotExist()
        composeTestRule.onNode(heading).assertTextEquals("Prescription").assertExists()
    }

    @Test
    fun check_for_clear_icon_on_search_bar(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(prescriptionCard).performClick()
        composeTestRule.onNode(quickSelectTab).performClick()
        composeTestRule.onNode(searchIcon).performClick()
        composeTestRule.onNode(searchTextField).performTextInput("input")
        composeTestRule.onNode(clearIcon).assertExists()
    }

    @Test
    fun enter_input_in_search_bar_check_result(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(prescriptionCard).performClick()
        composeTestRule.onNode(quickSelectTab).performClick()
        composeTestRule.onNode(searchIcon).performClick()
        composeTestRule.onNode(searchTextField).performTextInput("ampicillin")
        composeTestRule.onNode(searchTextField).performImeAction()
        composeTestRule.onAllNodes(activeIngredientName, useUnmergedTree = true).assertAny(hasText("ampicillin", ignoreCase = true))
    }

    @Test
    fun check_buttons_on_search_result_screen(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(prescriptionCard).performClick()
        composeTestRule.onNode(quickSelectTab).performClick()
        composeTestRule.onNode(searchIcon).performClick()
        composeTestRule.onNode(searchTextField).performTextInput("ampicillin")
        composeTestRule.onNode(searchTextField).performImeAction()
        composeTestRule.onAllNodes(searchIcon).assertCountEquals(2)
        composeTestRule.onNode(clearIcon).assertExists()
    }

    @Test
    fun check_click_on_any_compound_check_navigation(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(prescriptionCard).performClick()
        composeTestRule.onNode(quickSelectTab).performClick()
        composeTestRule.onNode(searchIcon).performClick()
        composeTestRule.onNode(searchTextField).performTextInput("ampicillin")
        composeTestRule.onNode(searchTextField).performImeAction()
        composeTestRule.onAllNodes(checkBoxes)[0].performClick()
        composeTestRule.onNodeWithText("Fill details").assertExists()
    }

    @Test
    fun check_for_components_on_fill_details(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(prescriptionCard).performClick()
        composeTestRule.onNode(quickSelectTab).performClick()
        composeTestRule.onAllNodes(checkBoxes)[0].performClick()
        composeTestRule.onNode(activeIngredientField).assertExists()
        composeTestRule.onAllNodes(formulationsList).fetchSemanticsNodes(true)
    }

    @Test
    fun check_for_dropdown_icon(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(prescriptionCard).performClick()
        composeTestRule.onNode(quickSelectTab).performClick()
        composeTestRule.onAllNodes(checkBoxes)[0].performClick()
        composeTestRule.onNode(dropdownIcon, useUnmergedTree = true).assertExists()
    }

    @Test
    fun check_click_on_active_ingredient_field(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(prescriptionCard).performClick()
        composeTestRule.onNode(quickSelectTab).performClick()
        composeTestRule.onAllNodes(checkBoxes)[0].performClick()
        composeTestRule.onNode(activeIngredientField).performClick()
        composeTestRule.onNode(activeIngredientDropdownList).assertExists()
    }

    @Test
    fun check_formulation_form_components(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(prescriptionCard).performClick()
        composeTestRule.onNode(quickSelectTab).performClick()
        composeTestRule.onAllNodes(checkBoxes)[0].performClick()
        composeTestRule.onNode(qtyPerDoseTextField).assertDoesNotExist()
        composeTestRule.onNode(freqTextField).assertDoesNotExist()
        composeTestRule.onNode(timingField).assertDoesNotExist()
        composeTestRule.onNode(qtyPrescribedField).assertDoesNotExist()
        composeTestRule.onNode(durationField).assertDoesNotExist()
        composeTestRule.onNode(notesField).assertDoesNotExist()
        composeTestRule.onAllNodes(formulationsList)[0].performClick()
        composeTestRule.onNode(qtyPerDoseTextField).assertExists().assertTextEquals("1", "Qty per dose", includeEditableText = false)
        composeTestRule.onNode(freqTextField).assertExists().assertTextEquals("1",  "Frequency", "dose per day")
        composeTestRule.onNode(timingField).assertExists().assertTextEquals("Timing (optional)", includeEditableText = false)
        composeTestRule.onNode(durationField).assertExists().assertTextEquals("Duration (days) *", includeEditableText = false)
        composeTestRule.onNode(durationField).performTextInput("4")
        composeTestRule.onNode(durationField).assertTextEquals("Duration (days) *", "4")
        composeTestRule.onNode(notesField).assertExists().assertTextEquals("Notes (optional)", includeEditableText = false)
        composeTestRule.onNode(notesField).performTextInput("input")
        composeTestRule.onNode(notesField).assertTextEquals("Notes (optional)", "input")
        composeTestRule.onNode(qtyPrescribedField).assertExists().assertIsNotEnabled()
        composeTestRule.onNode(qtyPrescribedField).assertTextEquals("Quantity prescribed", "4")
        composeTestRule.onNode(doneBtn).performClick()
        composeTestRule.onNode(bottomNavRow).assertExists()
    }

    @Test
    fun zzzz_logout(){
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(logoutIcon).performClick()
        composeTestRule.onNodeWithText("Logout").performClick()
    }
}