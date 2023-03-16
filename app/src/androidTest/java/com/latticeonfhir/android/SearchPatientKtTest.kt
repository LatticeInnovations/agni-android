package com.latticeonfhir.android

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.latticeonfhir.android.ui.main.searchpatient.SearchPatient
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchPatientKtTest {
    @get: Rule
    val composeTestRule = createAndroidComposeRule<SearchPatient>()

    // Placeholders
    val title = hasText("Search Patient")
    val heading = hasText("Search using any of the field below")
    val ageRangeTitle = hasText("Select age range")
    val gender = hasText("Gender")

    // Icons
    val clearIcon = hasTestTag("clear icon")
    val backIcon = hasTestTag("back icon")

    // Input Fields
    val patientName = hasTestTag("Patient Name") and hasClickAction()
    val patientId = hasTestTag("Patient Id") and hasClickAction()

    // Selection Chips
    val femaleChip = hasText("Female") and hasClickAction()
    val maleChip = hasText("Male") and hasClickAction()
    val othersChip = hasText("Others") and hasClickAction()

    // Button
    val nextBtn = hasText("Next")

    // Slider
    val ageRangeSlider = hasTestTag("age range slider")

    // list
    val searchResultList = hasTestTag("search result list") and hasScrollAction()

    // dropdown
    val lastFacilityVisit = hasTestTag("last facility visit") and hasClickAction()

    // tests for search patient form
    @Test
    fun searchPatientForm_verify_if_all_views_exist() {
        composeTestRule.onNode(title).assertExists("Title should be \"Search Patient\".")
        composeTestRule.onNode(clearIcon, useUnmergedTree = true).assertExists()
        composeTestRule.onNode(backIcon, useUnmergedTree = true).assertDoesNotExist()
        composeTestRule.onNode(patientId).assertExists("Patient Id input field should be displayed.")
        composeTestRule.onNode(patientName).assertExists("Patient Name input field should be displayed.")
        composeTestRule.onNode(heading).assertExists("Heading \"Search using any of the field below\" should be displayed.")
        composeTestRule.onNode(ageRangeTitle).assertExists("Title \"Select age range\" should be displayed.")
        composeTestRule.onNode(ageRangeSlider).assertExists("Age Range Slider should be displayed.")
        composeTestRule.onNode(gender).assertExists("Gender title should be displayed.")
        composeTestRule.onNode(femaleChip).assertExists("Female gender selection chip should be displayed.")
        composeTestRule.onNode(maleChip).assertExists("Male gender selection chip should be displayed.")
        composeTestRule.onNode(othersChip).assertExists("Others gender selection chip should be displayed.")
        composeTestRule.onNode(lastFacilityVisit).assertExists("Last facility visit dropdown should be displayed.")
        composeTestRule.onNode(nextBtn).assertExists("Next button should be displayed.")
    }

    @Test
    fun searchPatientForm_verify_last_facility_visit_dropdown() {
        composeTestRule.onNode(lastFacilityVisit).performClick()
        composeTestRule.onAllNodesWithText("Last week")[1].assertExists("Last week option should be displayed.")
        composeTestRule.onNodeWithText("Last month").assertExists("Last month option should be displayed.")
        composeTestRule.onNodeWithText("Last 3 months").assertExists("Last 3 months option should be displayed.")
        composeTestRule.onNodeWithText("Last year").assertExists("Last year option should be displayed.")
    }

    @Test
    fun searchPatientForm_verify_next_button_click() {
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(backIcon, useUnmergedTree = true).assertExists("Search Result screen should be displayed.")
    }

    @Test
    fun searchPatientForm_verify_clear_icon_click() {
        composeTestRule.onNode(clearIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithText("My Patients").assertExists("My Patients screen should be displayed.")
    }

    // tests for search patient result
    @Test
    fun searchPatientResult_verify_all_views_exists() {
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(title).assertExists("Title should be \"Search Patient\".")
        composeTestRule.onNode(backIcon, useUnmergedTree = true).assertExists("Back Icon should be displayed.")
        composeTestRule.onNode(clearIcon, useUnmergedTree = true).assertDoesNotExist()
        composeTestRule.onNode(searchResultList).assertExists("Scrollable search result list should be displayed.")
    }
}