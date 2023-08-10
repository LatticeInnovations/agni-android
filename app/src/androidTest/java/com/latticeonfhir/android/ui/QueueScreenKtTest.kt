package com.latticeonfhir.android.ui

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toMonth
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toSlotDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toWeekDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toYear
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.Date

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class QueueScreenKtTest: UiTestsBase() {
    @Before
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
    fun verify_all_queue_screen_components_and_their_functions(){
        composeTestRule.onNode(queue_tab).performClick()
        composeTestRule.onNode(heading).assertTextEquals("Queue")
        composeTestRule.onNode(resetBtn).assertExists("reset btn should exists").assertIsNotEnabled()
        composeTestRule.onNode(searchIcon).assertExists("search icon should exists")
        composeTestRule.onNode(dateDropDown).assertExists("date drop down should exists")
        val date = Date()
        composeTestRule.onNode(dateDropDown).assertTextEquals(date.toMonth(), date.toYear())
        composeTestRule.onNode(daysTabRow).assertExists("days tab row should exists.")
        composeTestRule.onAllNodes(daysChip)[1].assertTextEquals(date.toWeekDay(), date.toSlotDate())

        // search icon click
        composeTestRule.onNode(searchIcon).performClick()
        composeTestRule.onNode(queueSearchLayout).assertExists("queue search layout should exists.")
        composeTestRule.onNode(backIcon).assertExists("back icon on search layout should exists.")
        composeTestRule.onNode(clearIcon).assertDoesNotExist()
        composeTestRule.onNode(searchTextField).performTextInput("abc")
        composeTestRule.onNode(searchTextField).assertTextEquals("abc")
        composeTestRule.onNode(clearIcon).assertExists("Clear icon should be displayed")
        composeTestRule.onNode(clearIcon).performClick()
        composeTestRule.onNode(searchTextField).assertTextEquals("")
        composeTestRule.onNode(backIcon).performClick()
        composeTestRule.onNode(queueSearchLayout).assertDoesNotExist()
    }

    @After
    fun zzzz_logout() {
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(logoutIcon).performClick()
        composeTestRule.onNodeWithText("Logout").performClick()
    }
}