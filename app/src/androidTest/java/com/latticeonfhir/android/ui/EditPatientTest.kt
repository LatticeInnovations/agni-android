package com.latticeonfhir.android.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasNoClickAction
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.latticeonfhir.android.ui.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class EditPatientTest {
    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    val landingScreenTitle = hasText("My Patients") and hasNoClickAction()

    private val patient = hasTestTag("PATIENT")
    private val patientList = hasTestTag("patients list") and hasScrollAction()

    // icons
    val backIcon = hasContentDescription("back icon")
    val clearIcon = hasContentDescription("clear icon")
    val profileIcon = hasContentDescription("profile icon")
    val houseHoldMemberCard = hasTestTag("HOUSEHOLD_MEMBER") and hasClickAction()
    val prescriptionCard = hasTestTag("PRESCRIPTION") and hasClickAction()

    // Placeholders
    val title = hasText("Patient profile")
    val basicInformationTitle = hasText("Basic information")
    val identificationTitle = hasText("Identification")
    val addressTitle = hasText("Address")
    val undoAll = hasText("Undo all")

    // Input Fields
    val firstName = hasTestTag("First Name") and hasClickAction()
    val middleName = hasTestTag("Middle Name") and hasClickAction()
    val lastName = hasTestTag("Last Name") and hasClickAction()
    val phoneNo = hasTestTag("Phone Number") and hasClickAction()
    val email = hasTestTag("Email") and hasClickAction()
    val year = hasTestTag("Year") and hasClickAction()
    val month = hasTestTag("Month")
    val day = hasTestTag("Day") and hasClickAction()
    val years = hasTestTag("Years") and hasClickAction()
    val months = hasTestTag("Months") and hasClickAction()
    val days = hasTestTag("Days") and hasClickAction()

    val passportId = hasTestTag("Passport Id") and hasClickAction()
    val voterId = hasTestTag("Voter Id") and hasClickAction()
    val patientId = hasTestTag("Patient Id") and hasClickAction()

    val postalCode = hasTestTag("Postal Code *") and hasClickAction()
    val state = hasTestTag("State *") and hasClickAction()
    val addressLine1 = hasTestTag("Address Line 1 *") and hasClickAction()
    val addressLine2 = hasTestTag("Address Line 2") and hasClickAction()
    val city = hasTestTag("City *") and hasClickAction()
    val district = hasTestTag("District") and hasClickAction()

    // Selection Chips
    val dobChip = hasTestTag("dob") and hasClickAction()
    val ageChip = hasTestTag("age") and hasClickAction()
    val femaleChip = hasTestTag("female") and hasClickAction()
    val maleChip = hasTestTag("male") and hasClickAction()
    val othersChip = hasTestTag("other") and hasClickAction()
    val passportIdChip = hasTestTag("Passport Id chip") and hasClickAction()
    val voterIdChip = hasTestTag("Voter Id chip") and hasClickAction()
    val patientIdChip = hasTestTag("Patient Id chip") and hasClickAction()

    val saveBtn = hasText("Save")
    val editBtn1 = hasTestTag("edit btn 1")
    val editBtn2 = hasTestTag("edit btn 2")
    val editBtn3 = hasTestTag("edit btn 3")


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
    fun aaaa_login() {
        composeTestRule.onNode(inputField).performTextInput("1111111111")
        composeTestRule.onNode(button).performClick()
        Thread.sleep(2000)
        composeTestRule.onNode(firstDigit).performTextInput("1")
        composeTestRule.onNode(secondDigit).performTextInput("1")
        composeTestRule.onNode(thirdDigit).performTextInput("1")
        composeTestRule.onNode(fourDigit).performTextInput("1")
        composeTestRule.onNode(fiveDigit).performTextInput("1")
        composeTestRule.onNode(sixDigit).performTextInput("1")
        composeTestRule.onNode(button).performClick()
        Thread.sleep(2000)
    }


    @Test
    fun verify_patient_item_click_navigate_to_patient_landing_screen() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        //composeTestRule.onNode(title).assertTextEquals("Mansi")
        composeTestRule.onNode(profileIcon).assertExists("Should have navigated to profile screen.")
    }

    @Test
    fun patientLandingScreen_verify_if_all_components_exists() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(backIcon, useUnmergedTree = true)
            .assertExists("Back icon should be displayed.")
        composeTestRule.onNode(profileIcon, useUnmergedTree = true)
            .assertExists("profile icon should be displayed.")
        composeTestRule.onNode(houseHoldMemberCard)
            .assertExists("Household member card should be displayed.")
        composeTestRule.onNode(prescriptionCard)
            .assertExists("prescription card should be displayed.")
    }

    @Test
    fun patientProfileIcon_verify_if_exists() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(backIcon, useUnmergedTree = true)
            .assertExists("Back icon should be displayed.")
        composeTestRule.onNode(profileIcon, useUnmergedTree = true)
            .assertExists("profile icon should be displayed.")
    }


    @Test
    fun patientLandingScreen_verify_back_btn_click() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(backIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(landingScreenTitle)
            .assertExists("Should have navigated to My Patients screen.")
    }

    @Test
    fun patientLandingScreen_verify_profile_icon_click_navigate_to_profile_screen() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        //composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
    }

    @Test
    fun check_heading_on_profile_screen() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        //composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
    }

    @Test
    fun check_back_And_Edit_button_on_profile_screen() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        //composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(backIcon, useUnmergedTree = true)
            .assertExists("Back icon should be displayed.")
        composeTestRule.onNode(editBtn1).assertExists("Basic Info Edit Button should be displayed.")
            .assertIsEnabled()
        composeTestRule.onNode(editBtn2)
            .assertExists("Identification Edit Button should be displayed.").assertIsEnabled()
        composeTestRule.onNode(editBtn3).assertExists("Addresses Edit Button should be displayed.")
            .assertIsEnabled()
    }

    @Test
    fun check_Cards_on_profile_screen() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        //composeTestRule.onNode(patientList).performScrollToNode(patient)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNodeWithText("Basic Information")
            .assertExists("Basic Information card should be displayed.")
        composeTestRule.onNodeWithText("Identification")
            .assertExists("Identification card should be displayed.")
        composeTestRule.onNodeWithText("Addresses")
            .assertExists("Addresses card should be displayed.")

    }


    @Test
    fun check_Edit_button_appear_on_each_card() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(backIcon, useUnmergedTree = true)
            .assertExists("Back icon should be displayed.")
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        // Preview page tests
        composeTestRule.onNodeWithText("Basic Information")
            .assertExists("Basic Information card should be displayed.")
        composeTestRule.onNodeWithText("Identification")
            .assertExists("Identification card should be displayed.")
        composeTestRule.onNodeWithText("Addresses")
            .assertExists("Addresses card should be displayed.")

        composeTestRule.onNode(editBtn1).assertExists("Basic Info Edit Button should be displayed.")
            .assertIsEnabled()
        composeTestRule.onNode(editBtn2)
            .assertExists("Identification Edit Button should be displayed.").assertIsEnabled()
        composeTestRule.onNode(editBtn3).assertExists("Addresses Edit Button should be displayed.")
            .assertIsEnabled()
    }


    //     Patient Basic Information Edit Tests

    @Test
    fun clickEditButtonMust_navigateBasicInformation() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
    }

    @Test
    fun verifyHeadingForBasicInformation() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(basicInformationTitle).assertTextEquals("Basic information")

    }

    @Test
    fun verifyCloseSaveAndUndoButtonForBasicInformation() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(clearIcon, useUnmergedTree = true)
            .assertExists("Clear icon  should be displayed.")
        composeTestRule.onNode(saveBtn, useUnmergedTree = true)
            .assertExists("save icon  should be displayed.")
        composeTestRule.onNode(undoAll).assertTextEquals("Undo all")
    }

    @Test
    fun patientEditScreen_verify_clear_icon_click() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(basicInformationTitle).assertTextEquals("Basic information")
        composeTestRule.onNode(clearIcon, true).performClick()
        composeTestRule.onNode(title).assertExists("Should be navigated to My Patients screen.")
    }

    @Test
    fun basicInformation_verify_if_no_edit_undoAllTextDisable() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(basicInformationTitle).assertTextEquals("Basic information")
        composeTestRule.onNode(undoAll).assertIsNotEnabled()
    }

    @Test
    fun basicInformation_verify_if_no_edit_saveButtonDisable() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(basicInformationTitle).assertTextEquals("Basic information")
        composeTestRule.onNode(saveBtn).assertIsNotEnabled()
    }

    @Test
    fun basic_information_edit_screen_verify_if_all_views_exists() {

        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(basicInformationTitle).assertTextEquals("Basic information")

        composeTestRule.onNode(clearIcon, useUnmergedTree = true)
            .assertExists("Clear icon  should be displayed.")
        composeTestRule.onNode(undoAll).assertTextEquals("Undo all")
        composeTestRule.onNode(firstName)
            .assertExists("First Name input field should be displayed.")
        composeTestRule.onNode(middleName)
            .assertExists("Middle Name input field should be displayed.")
        composeTestRule.onNode(lastName).assertExists("Last Name input field should be displayed.")
        composeTestRule.onNode(phoneNo)
            .assertExists("Phone number input field should be displayed.")
        composeTestRule.onNode(email).assertExists("Email input field should be displayed.")
        composeTestRule.onNode(dobChip).assertExists("DOB selection chip should be displayed.")
        composeTestRule.onNode(dobChip).assertIsSelected()
        composeTestRule.onNode(year)
            .assertExists("Year input field should be displayed by default.")
        composeTestRule.onNode(month)
            .assertExists("Month input field should be displayed by default.")
        composeTestRule.onNode(day).assertExists("Day input field should be displayed by default.")
        composeTestRule.onNode(ageChip).assertExists("Age selection chip should be displayed.")
        composeTestRule.onNode(femaleChip)
            .assertExists("Female gender selection chip should be displayed.")
        composeTestRule.onNode(maleChip)
            .assertExists("Male gender selection chip should be displayed.")
        composeTestRule.onNode(othersChip)
            .assertExists("Others gender selection chip should be displayed.")
        composeTestRule.onNode(saveBtn).assertExists("Save button should be displayed.")
            .assertIsNotEnabled()
    }


    @Test
    fun basicInformation_verify_if_edit_undoAllTextEnable() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(basicInformationTitle).assertTextEquals("Basic information")
        composeTestRule.onNode(firstName).performTextClearance()
        composeTestRule.onNode(firstName).performTextInput("ABC")
        composeTestRule.onNode(undoAll).assertIsEnabled()
    }


    @Test
    fun basicInformation_verify_if_edit_and_fields_valid_saveButtonEnable() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(basicInformationTitle).assertTextEquals("Basic information")
        composeTestRule.onNode(middleName).performTextClearance()
        composeTestRule.onNode(middleName).performTextInput("ABC")
        composeTestRule.onNode(saveBtn).assertIsEnabled()
    }


    @Test
    fun patientBasicInfoEdit_save_btn_disabled_on_invalid_firstName() {

        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(basicInformationTitle).assertTextEquals("Basic information")
        composeTestRule.onNode(firstName).performTextClearance()
        composeTestRule.onNode(firstName).performTextInput("aa")
        composeTestRule.onNode(saveBtn).assertIsNotEnabled()
    }

    @Test
    fun patientBasicInfoEdit_save_btn_enabled_on_valid_dob() {

        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(basicInformationTitle).assertTextEquals("Basic information")
        composeTestRule.onNode(dobChip).performClick()
        composeTestRule.onNode(day).performTextClearance()
        composeTestRule.onNode(day).performTextInput("23")
        composeTestRule.onNode(year).performTextClearance()
        composeTestRule.onNode(year).performTextInput("2008")
        composeTestRule.onNode(saveBtn).assertIsEnabled()
    }

    @Test
    fun patientBasicInfoEdit_save_btn_disabled_on_invalid_age() {

        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(basicInformationTitle).assertTextEquals("Basic information")
        composeTestRule.onNode(ageChip).performClick()
        composeTestRule.onNode(days).performTextClearance()
        composeTestRule.onNode(months).performTextClearance()
        composeTestRule.onNode(years).performTextClearance()
        composeTestRule.onNode(days).performTextInput("28")
        composeTestRule.onNode(months).performTextInput("50")
        composeTestRule.onNode(years).performTextInput("23")
        composeTestRule.onNode(saveBtn).assertIsNotEnabled()
    }

    @Test
    fun patientBasicInfoEdit_save_btn_enabled_on_valid_age() {

        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(basicInformationTitle).assertTextEquals("Basic information")
        composeTestRule.onNode(ageChip).performClick()
        composeTestRule.onNode(days).performTextClearance()
        composeTestRule.onNode(months).performTextClearance()
        composeTestRule.onNode(years).performTextClearance()
        composeTestRule.onNode(days).performTextInput("23")
        composeTestRule.onNode(months).performTextInput("7")
        composeTestRule.onNode(years).performTextInput("23")
        composeTestRule.onNode(saveBtn).assertIsEnabled()
        composeTestRule.onNode(saveBtn).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")

    }

    @Test
    fun patientBasicInfoEdit_save_btn_disabled_on_invalid_phoneNumber() {

        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(basicInformationTitle).assertTextEquals("Basic information")
        composeTestRule.onNode(phoneNo).performTextClearance()
        composeTestRule.onNode(phoneNo).performTextInput("111111")
        composeTestRule.onNode(phoneNo).performImeAction()
        composeTestRule.onNode(saveBtn).assertIsNotEnabled()
    }

    @Test
    fun patientBasicInfoEdit_save_btn_disabled_on_enter_alhabet_phoneNumber() {

        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(basicInformationTitle).assertTextEquals("Basic information")
        composeTestRule.onNode(phoneNo).performTextClearance()
        composeTestRule.onNode(phoneNo).performTextInput("avcdsdlf")
        composeTestRule.onNode(phoneNo).performImeAction()
        composeTestRule.onNode(saveBtn).assertIsNotEnabled()
    }

    @Test
    fun patientBasicInfoEdit_save_btn_disabled_on_enter_free_text_phoneNumber() {

        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(basicInformationTitle).assertTextEquals("Basic information")
        composeTestRule.onNode(phoneNo).performTextClearance()
        composeTestRule.onNode(phoneNo).performTextInput("       ")
        composeTestRule.onNode(phoneNo).performImeAction()
        composeTestRule.onNode(saveBtn).assertIsNotEnabled()
    }

    @Test
    fun patientBasicInfoEdit_save_btn_disabled_on_enter_special_char_text_phoneNumber() {

        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(basicInformationTitle).assertTextEquals("Basic information")
        composeTestRule.onNode(phoneNo).performTextClearance()
        composeTestRule.onNode(phoneNo).performTextInput("#$%@#")
        composeTestRule.onNode(phoneNo).performImeAction()
        composeTestRule.onNode(saveBtn).assertIsNotEnabled()
    }

    @Test
    fun patientBasicInfoEdit_check_gender_component_exist() {

        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(basicInformationTitle).assertTextEquals("Basic information")
        composeTestRule.onNode(femaleChip)
            .assertExists("Female gender selection chip should be displayed.")
        composeTestRule.onNode(maleChip)
            .assertExists("Male gender selection chip should be displayed.")
        composeTestRule.onNode(othersChip)
            .assertExists("Others gender selection chip should be displayed.")
    }

    @Test
    fun patientBasicInfoEdit_save_btn_disabled_on_invalid_email() {

        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(basicInformationTitle).assertTextEquals("Basic information")
        composeTestRule.onNode(phoneNo).performTextClearance()
        composeTestRule.onNode(email).performTextInput("abc.co@")
        composeTestRule.onNode(email).performImeAction()
        composeTestRule.onNode(saveBtn).assertIsNotEnabled()
    }

    @Test
    fun patientEditScreen_verify_undo_all_click() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(basicInformationTitle).assertTextEquals("Basic information")
        composeTestRule.onNode(middleName).performTextClearance()
        composeTestRule.onNode(email).performTextInput("abc@gmail.com")
        composeTestRule.onNode(email).performImeAction()
        composeTestRule.onNode(undoAll).assertIsEnabled()
        composeTestRule.onNode(undoAll, true).performClick()
    }

    @Test
    fun patientIdentification_edit_verify_navigation() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn2).performClick()

    }
    @Test
    fun patientIdentification_edit_verify_chips() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn2).performClick()
        composeTestRule.onNode(passportIdChip).assertIsDisplayed()
        composeTestRule.onNode(voterIdChip).assertIsDisplayed()
        composeTestRule.onNode(patientIdChip).assertIsDisplayed()
    }

    @Test
    fun patientIdentification_edit_verify_heading() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn2).performClick()
        composeTestRule.onNode(identificationTitle).assertTextEquals("Identification")
    }

    @Test
    fun patientIdentification_edit_verify_buttons() {
        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn2).performClick()
        composeTestRule.onNode(clearIcon, useUnmergedTree = true)
            .assertExists("Clear icon  should be displayed.")
        composeTestRule.onNode(saveBtn, useUnmergedTree = true)
            .assertExists("save icon  should be displayed.")
        composeTestRule.onNode(undoAll).assertTextEquals("Undo all")
    }


    @Test
    fun patientEditIdentification_verify_clear_icon_click() {

        composeTestRule.waitUntilAtLeastOneExists(
            patient, timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn2).performClick()
        composeTestRule.onNode(identificationTitle).assertTextEquals("Identification")
        composeTestRule.onNode(clearIcon, true).performClick()
    }


    // Patient Address Edit Tests
    @Test
    fun patientAddressEdit_verify_if_all_views_exists() {
        composeTestRule.waitUntilAtLeastOneExists(patient, timeoutMillis = 15000)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn3).performClick()
        composeTestRule.onNode(addressTitle).assertTextEquals("Address")
        composeTestRule.onNode(undoAll).assertTextEquals("Undo all")
            .assertExists("Back Button should be displayed.")
        composeTestRule.onNode(clearIcon, useUnmergedTree = true)
            .assertExists("Clear Icon should be displayed.")
            .assertExists("Home Address title should be displayed.")
        composeTestRule.onNode(postalCode)
            .assertExists("Postal Code input field should be displayed.")
        composeTestRule.onNode(state).assertExists("State input field should be displayed.")
        composeTestRule.onNode(addressLine1)
            .assertExists("Address Line 1 input field should be displayed.")
        composeTestRule.onNode(addressLine2)
            .assertExists("Address Line 2 input field should be displayed.")
        composeTestRule.onNode(city).assertExists("City input field should be displayed.")
        composeTestRule.onNode(district).assertExists("District input field should be displayed.")
        composeTestRule.onNode(saveBtn).assertExists("Save button should be displayed.")
            .assertIsNotEnabled()
    }

    @Test
    fun patientAddressEdit_save_btn_enabled_on_valid_home_address() {
        composeTestRule.waitUntilAtLeastOneExists(patient, timeoutMillis = 15000)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn3).performClick()
        composeTestRule.onNode(addressTitle).assertTextEquals("Address")
        composeTestRule.onNode(addressLine2).performTextInput("ABfsfddC")
        composeTestRule.onNode(saveBtn).assertIsEnabled()
    }


    @Test
    fun patientAddressEdit_verify_save_btn_click() {
        composeTestRule.waitUntilAtLeastOneExists(patient, timeoutMillis = 15000)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn3).performClick()
        composeTestRule.onNode(addressTitle).assertTextEquals("Address")
        composeTestRule.onNode(addressLine1).performTextInput("C-416343")
        composeTestRule.onNode(saveBtn).assertIsEnabled()
        composeTestRule.onNode(saveBtn).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")

    }

    @Test
    fun patientAddressEdit_verify_clear_icon_click() {
        composeTestRule.waitUntilAtLeastOneExists(patient, timeoutMillis = 15000)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn3).performClick()
        composeTestRule.onNode(addressTitle).assertTextEquals("Address")
        composeTestRule.onNode(clearIcon, true).performClick()
    }

    @Test
    fun patientAddressEdit_verify_undo_all_click() {
        composeTestRule.waitUntilAtLeastOneExists(patient, timeoutMillis = 15000)
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(profileIcon, useUnmergedTree = true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Patient profile")
        composeTestRule.onNode(editBtn3).performClick()
        composeTestRule.onNode(addressTitle).assertTextEquals("Address")
        composeTestRule.onNode(addressLine2).performTextInput("dsfsd3")
        composeTestRule.onNode(undoAll).assertIsEnabled()
    }


    @Test
    fun zzzz_logout() {
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(logoutIcon).performClick()
        composeTestRule.onNodeWithText("Logout").performClick()
    }

}