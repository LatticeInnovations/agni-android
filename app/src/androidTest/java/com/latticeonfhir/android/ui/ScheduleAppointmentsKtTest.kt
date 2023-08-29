package com.latticeonfhir.android.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toMonth
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toSlotDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toWeekDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toYear
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.tomorrow
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.Date

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class ScheduleAppointmentsKtTest: UiTestsBase() {
    @Test
    fun aaaa_login() {
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
    fun verify_all_components_and_their_functions(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(appointmentsCard).performClick()
        composeTestRule.onNode(addAppointmentFab).performClick()
        composeTestRule.onNode(addScheduleFab).performClick()
        composeTestRule.onNode(heading).assertTextEquals("Schedule appointment")
        composeTestRule.onNode(backIcon).assertExists("back icon should exists.")
        composeTestRule.onNode(resetBtn).assertExists("reset btn should exists.")
        composeTestRule.onNode(dateDropDown).assertExists("Date drop down should exists.")
        val date = Date()
        composeTestRule.onNode(dateDropDown).assertTextEquals(date.toMonth(), date.toYear())
        composeTestRule.onNode(daysTabRow).assertExists("Days tab row should exists.")
        //composeTestRule.onAllNodes(daysChip).assertCountEquals(8)
        composeTestRule.onAllNodes(daysChip)[0].assertTextEquals(date.tomorrow().toWeekDay(), date.tomorrow().toSlotDate())

        // check for slots
        composeTestRule.onNode(morningSlotsHeading).assertExists("Morning slots heading should exists.")
        composeTestRule.onNode(eveningSlotsHeading).assertExists("Evening slots heading should exists.")
        composeTestRule.onNode(afternoonSlotsHeading).assertExists("Afternoon slots heading should exists.")
        composeTestRule.onAllNodes(morningSlotsChip).assertCountEquals(6)
        composeTestRule.onAllNodes(morningSlotsChip)[0].assertTextContains("09:00 AM")
        composeTestRule.onAllNodes(morningSlotsChip)[1].assertTextContains("09:30 AM")
        composeTestRule.onAllNodes(morningSlotsChip)[2].assertTextContains("10:00 AM")
        composeTestRule.onAllNodes(morningSlotsChip)[3].assertTextContains("10:30 AM")
        composeTestRule.onAllNodes(morningSlotsChip)[4].assertTextContains("11:00 AM")
        composeTestRule.onAllNodes(morningSlotsChip)[5].assertTextContains("11:30 AM")
        composeTestRule.onAllNodes(afternoonSlotsChip).assertCountEquals(8)
        composeTestRule.onAllNodes(afternoonSlotsChip)[0].assertTextContains("12:00 PM")
        composeTestRule.onAllNodes(afternoonSlotsChip)[1].assertTextContains("12:30 PM")
        composeTestRule.onAllNodes(afternoonSlotsChip)[2].assertTextContains("01:00 PM")
        composeTestRule.onAllNodes(afternoonSlotsChip)[3].assertTextContains("01:30 PM")
        composeTestRule.onAllNodes(afternoonSlotsChip)[4].assertTextContains("02:00 PM")
        composeTestRule.onAllNodes(afternoonSlotsChip)[5].assertTextContains("02:30 PM")
        composeTestRule.onAllNodes(afternoonSlotsChip)[6].assertTextContains("03:00 PM")
        composeTestRule.onAllNodes(afternoonSlotsChip)[7].assertTextContains("03:30 PM")
        composeTestRule.onAllNodes(eveningSlotsChip).assertCountEquals(2)
        composeTestRule.onAllNodes(eveningSlotsChip)[0].assertTextContains("04:00 PM")
        composeTestRule.onAllNodes(eveningSlotsChip)[1].assertTextContains("04:30 PM")

        // date picker on click
        composeTestRule.onNode(dateDropDown).performClick()
        composeTestRule.onNode(datePickerDialog).assertExists("date picker dialog should be displayed.")
        composeTestRule.onNodeWithText("OK").assertExists("ok btn on dialog should be displayed.")
        composeTestRule.onNodeWithText("Cancel").assertExists("cancel btn on dialog should be displayed.")
        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.onNode(datePickerDialog).assertDoesNotExist()
    }

    @Test
    fun zzzz_logout() {
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(logoutIcon).performClick()
        composeTestRule.onNodeWithText("Logout").performClick()
    }
}