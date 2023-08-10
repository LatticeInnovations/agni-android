package com.latticeonfhir.android.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
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
class AppointmentsScreenKtTest : UiTestsBase() {
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
    fun appointments_card_on_patient_landing_screen() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(appointmentsCard).assertExists("Should have appointments card.")
    }

    @Test
    fun number_of_appointments_on_card() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(numberOfAppointments, useUnmergedTree = true)
            .assertExists("Should have no. of appointments on appointments card.")
            .assertTextContains("scheduled", substring = true)
    }

    @Test
    fun verify_appointments_card_click() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(appointmentsCard).performClick()
        composeTestRule.onNode(heading).assertTextEquals("Appointments")
    }

    @Test
    fun verify_add_schedule_fab_click() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(addAppointmentFab).performClick()
        composeTestRule.onNode(addScheduleFab).performClick()
        composeTestRule.onNode(heading).assertTextEquals("Schedule appointment")
        composeTestRule.onNode(backIcon).performClick()
        composeTestRule.onNode(appointmentsCard).performClick()
        composeTestRule.onNode(heading).assertTextEquals("Appointments")
        composeTestRule.onNode(addAppointmentFab).performClick()
        composeTestRule.onNode(addScheduleFab).performClick()
        composeTestRule.onNode(heading).assertTextEquals("Schedule appointment")
    }

    @Test
    fun verify_appointments_screen_components() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(appointmentsCard).performClick()
        composeTestRule.onNode(heading).assertTextEquals("Appointments")
        composeTestRule.onNode(backIcon).assertExists("Back icon should be present.")
        composeTestRule.onNode(addAppointmentFab)
            .assertExists("Add appointment fab should be present.")
    }

    @Test
    fun verify_appointments_tabs() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(appointmentsCard).performClick()
        composeTestRule.onNode(upcomingTab).assertExists("Upcoming tab should be present.")
            .assertIsSelected()
        composeTestRule.onNode(completedTab).assertExists("Completed tab should be present.")
            .assertIsNotSelected()
    }

    @Test
    fun verify_appointments_back_icon_click() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(appointmentsCard).performClick()
        composeTestRule.onNode(backIcon).performClick()
        composeTestRule.onNode(appointmentsCard)
            .assertExists("Should have navigated back to patient landing screen.")
    }

    @Test
    fun create_patient_and_add_to_queue() {
        composeTestRule.onNode(addPatientText, useUnmergedTree = true).performClick()
        composeTestRule.onNode(firstName).performTextInput("appointment")
        composeTestRule.onNode(firstName).performImeAction()
        composeTestRule.onNode(middleName).performImeAction()
        composeTestRule.onNode(lastName).performImeAction()
        composeTestRule.onNode(day).performTextInput("23")
        composeTestRule.onNode(month).performClick()
        composeTestRule.onNodeWithText("January").performClick()
        composeTestRule.onNode(year).performTextInput("2001")
        composeTestRule.onNode(phoneNo).performTextInput("9876543210")
        composeTestRule.onNode(phoneNo).performImeAction()
        composeTestRule.onNodeWithTag("columnLayout").performScrollToNode(hasTestTag("genderRow"))
        composeTestRule.onNode(femaleChip).performClick()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(postalCode).performTextInput("111111")
        composeTestRule.onNode(state).performClick()
        composeTestRule.onNodeWithText("Andhra Pradesh").performClick()
        composeTestRule.onNode(addressLine1).performTextInput("C-416")
        composeTestRule.onNode(addressLine2).performTextInput("Sarita Vihar")
        composeTestRule.onNode(city).performTextInput("South Delhi")
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNode(saveBtn).performClick()
        composeTestRule.onNode(householdMemberCard)
            .assertExists("Should be navigated to Patient Landing screen.")
        composeTestRule.onNode(numberOfAppointments, useUnmergedTree = true)
            .assertExists("Should have no. of appointments on appointments card.")
            .assertTextEquals("0 scheduled")

        // add appointment fab test on patient landing screen
        composeTestRule.onNode(addAppointmentFab)
            .assertExists("Add appointment fab should be displayed")
        composeTestRule.onNode(addAppointmentFab).performClick()
        composeTestRule.onNode(addAppointmentFab).assertDoesNotExist()
        composeTestRule.onNode(addScheduleFab).assertExists("Add schedule fab should be displayed.")
            .assertTextEquals("Schedule appointment")
        composeTestRule.onNode(queueFab).assertExists("Queue fab should be displayed.")
            .assertTextEquals("Add to queue")
        composeTestRule.onNode(clearFab).assertExists("Clear fab should be displayed.")
        composeTestRule.onNode(clearFab).performClick()
        composeTestRule.onNode(clearFab).assertDoesNotExist()
        composeTestRule.onNode(addScheduleFab).assertDoesNotExist()
        composeTestRule.onNode(queueFab).assertDoesNotExist()
        composeTestRule.onNode(addAppointmentFab)
            .assertExists("Add appointment fab should be displayed")

        // navigate to appointments screen and check for components
        composeTestRule.onNode(appointmentsCard).performClick()
        composeTestRule.onNode(heading).assertTextEquals("Appointments")
        composeTestRule.onNode(upcomingAppointmentCard).assertDoesNotExist()
        composeTestRule.onNodeWithText("No upcoming appointments")
            .assertExists("No upcoming appointments should be displayed.")
        composeTestRule.onNode(addAppointmentFab).assertExists("add appointment fab should exists.")
        composeTestRule.onNode(completedTab).performClick()
        composeTestRule.onNodeWithText("No completed appointment").assertIsDisplayed()
        composeTestRule.onNode(upcomingTab).assertIsSelected()
        composeTestRule.onNode(completedTab).assertIsNotSelected()
        composeTestRule.onNode(addAppointmentFab).performClick()
        composeTestRule.onNode(addAppointmentFab).assertDoesNotExist()
        composeTestRule.onNode(addScheduleFab).assertExists("Add schedule fab should be displayed.")
            .assertTextEquals("Schedule appointment")
        composeTestRule.onNode(queueFab).assertExists("Queue fab should be displayed.")
            .assertTextEquals("Add to queue")
        composeTestRule.onNode(clearFab).assertExists("Clear fab should be displayed.")
        composeTestRule.onNode(clearFab).performClick()
        composeTestRule.onNode(clearFab).assertDoesNotExist()
        composeTestRule.onNode(addScheduleFab).assertDoesNotExist()
        composeTestRule.onNode(queueFab).assertDoesNotExist()
        composeTestRule.onNode(addAppointmentFab)
            .assertExists("Add appointment fab should be displayed")

        // add patient to queue
        composeTestRule.onNode(addAppointmentFab).performClick()
        composeTestRule.onNode(queueFab).performClick()
        composeTestRule.onNode(heading).assertTextEquals("Queue")
        composeTestRule.onNodeWithText("Patient added to queue")
            .assertExists("patient added to queue snackbar should be displayed.")
        composeTestRule.waitUntilDoesNotExist(
            hasText("Patient added to queue"),
            timeoutMillis = 15000
        )

        // click on patient card
        composeTestRule.onAllNodes(queuePatientCard)[0].performClick()
        composeTestRule.onNode(householdMemberCard)
            .assertExists("should have navigated to patient landing screen")
        composeTestRule.onNode(backIcon).performClick()

        // check cancel btn
        composeTestRule.onAllNodes(appointmentCancelBtn)[0].performClick()
        composeTestRule.onNode(dialogTitle).assertTextEquals("Cancel appointment ?")
            .assertExists("dialog should appear")
        composeTestRule.onNode(dialogPositiveBtn).assertTextEquals("Yes, I want to cancel")
        composeTestRule.onNode(dialogNegativeBtn).assertTextEquals("No, go back")
        composeTestRule.onNode(dialogNegativeBtn).performClick()
        composeTestRule.onNode(dialogTitle).assertDoesNotExist()
        composeTestRule.onAllNodes(appointmentCancelBtn)[0].performClick()
        composeTestRule.onNode(dialogPositiveBtn).performClick()
        composeTestRule.onNodeWithText("Appointment cancelled")
            .assertExists("appointment cancelled snackbar should be displayed")
    }

    @Test
    fun create_patient_and_schedule_an_appointment_reschedule_and_cancel_an_appointment() {
        composeTestRule.onNode(addPatientText, useUnmergedTree = true).performClick()
        composeTestRule.onNode(firstName).performTextInput("appointment")
        composeTestRule.onNode(firstName).performImeAction()
        composeTestRule.onNode(middleName).performImeAction()
        composeTestRule.onNode(lastName).performImeAction()
        composeTestRule.onNode(day).performTextInput("23")
        composeTestRule.onNode(month).performClick()
        composeTestRule.onNodeWithText("January").performClick()
        composeTestRule.onNode(year).performTextInput("2001")
        composeTestRule.onNode(phoneNo).performTextInput("9876543210")
        composeTestRule.onNode(phoneNo).performImeAction()
        composeTestRule.onNodeWithTag("columnLayout").performScrollToNode(hasTestTag("genderRow"))
        composeTestRule.onNode(femaleChip).performClick()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(postalCode).performTextInput("111111")
        composeTestRule.onNode(state).performClick()
        composeTestRule.onNodeWithText("Andhra Pradesh").performClick()
        composeTestRule.onNode(addressLine1).performTextInput("C-416")
        composeTestRule.onNode(addressLine2).performTextInput("Sarita Vihar")
        composeTestRule.onNode(city).performTextInput("South Delhi")
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNode(saveBtn).performClick()
        composeTestRule.onNode(householdMemberCard)
            .assertExists("Should be navigated to Patient Landing screen.")
        composeTestRule.onNode(numberOfAppointments, useUnmergedTree = true)
            .assertExists("Should have no. of appointments on appointments card.")
            .assertTextEquals("0 scheduled")

        // navigate to appointments screen and check for components
        composeTestRule.onNode(appointmentsCard).performClick()
        composeTestRule.onNode(addAppointmentFab).performClick()
        composeTestRule.onNode(addScheduleFab).performClick()
        composeTestRule.onNode(heading).assertTextEquals("Schedule appointment")
        composeTestRule.onAllNodes(morningSlotsChip)[0].performClick()
        composeTestRule.onNode(confirmAppointmentBtn)
            .assertExists("confirm appointment btn should be displayed")
            .assertIsEnabled().performClick()
        composeTestRule.onNode(heading).assertTextEquals("Appointments")
        composeTestRule.onNodeWithText("Appointment scheduled").assertExists("Appointment scheduled snackbar should be displayed")
        composeTestRule.onAllNodes(upcomingAppointmentCard).assertCountEquals(1)
        composeTestRule.onAllNodes(appointmentRescheduleBtn)[0].performClick()


        // click on reschedule btn
        composeTestRule.onNode(heading).assertTextEquals("Reschedule appointment")
        composeTestRule.onNode(backIcon).assertExists("back icon should exists.")
        composeTestRule.onNode(resetBtn).assertExists("reset btn should exists.")
        composeTestRule.onNode(dateDropDown).assertExists("Date drop down should exists.")
        val date = Date()
        composeTestRule.onNode(dateDropDown).assertTextEquals(date.toMonth(), date.toYear())
        composeTestRule.onNode(daysTabRow).assertExists("Days tab row should exists.")
        composeTestRule.onAllNodes(daysChip)[0].assertTextEquals(
            date.tomorrow().toWeekDay(),
            date.tomorrow().toSlotDate()
        )

        // check for slots
        composeTestRule.onNode(morningSlotsHeading)
            .assertExists("Morning slots heading should exists.")
        composeTestRule.onNode(eveningSlotsHeading)
            .assertExists("Evening slots heading should exists.")
        composeTestRule.onNode(afternoonSlotsHeading)
            .assertExists("Afternoon slots heading should exists.")
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
        composeTestRule.onNode(datePickerDialog)
            .assertExists("date picker dialog should be displayed.")
        composeTestRule.onNodeWithText("OK").assertExists("ok btn on dialog should be displayed.")
        composeTestRule.onNodeWithText("Cancel")
            .assertExists("cancel btn on dialog should be displayed.")
        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.onNode(datePickerDialog).assertDoesNotExist()

        composeTestRule.onAllNodes(afternoonSlotsChip)[0].performClick()
        composeTestRule.onNode(confirmAppointmentBtn).assertExists("confirm appointment btn should be displayed")
            .assertIsEnabled().performClick()
        composeTestRule.onNode(heading).assertTextEquals("Appointments")
        composeTestRule.onNodeWithText("Appointment rescheduled").assertExists("Appointment rescheduled snackbar should be displayed")
        composeTestRule.waitUntilDoesNotExist(hasText("Appointment rescheduled"),15000)

        // check cancel btn
        composeTestRule.onAllNodes(appointmentCancelBtn)[0].performClick()
        composeTestRule.onNode(dialogTitle).assertTextEquals("Cancel appointment ?")
            .assertExists("dialog should appear")
        composeTestRule.onNode(dialogPositiveBtn).assertTextEquals("Yes, I want to cancel")
        composeTestRule.onNode(dialogNegativeBtn).assertTextEquals("No, go back")
        composeTestRule.onNode(dialogNegativeBtn).performClick()
        composeTestRule.onNode(dialogTitle).assertDoesNotExist()
        composeTestRule.onAllNodes(appointmentCancelBtn)[0].performClick()
        composeTestRule.onNode(dialogPositiveBtn).performClick()
        composeTestRule.onNodeWithText("Appointment cancelled")
            .assertExists("appointment cancelled snackbar should be displayed")
    }

    @Test
    fun zzzz_logout() {
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(logoutIcon).performClick()
        composeTestRule.onNodeWithText("Logout").performClick()
    }
}