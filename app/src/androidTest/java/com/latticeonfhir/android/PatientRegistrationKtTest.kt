package com.latticeonfhir.android

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.latticeonfhir.android.ui.main.patientregistration.PatientRegistration
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PatientRegistrationKtTest {
    @get: Rule
    val composeTestRule = createAndroidComposeRule<PatientRegistration>()

    // Placeholders
    val title = hasText("Patient Registration")

    // Input Fields
    val firstName = hasTestTag("First Name") and hasClickAction()
    val middleName = hasTestTag("Middle Name") and hasClickAction()
    val lastName = hasTestTag("Last Name") and hasClickAction()
    val phoneNo = hasTestTag("Phone Number") and hasClickAction()
    val dobInputField = hasTestTag("dobInputField") and hasClickAction()
    val email = hasTestTag("Email") and hasClickAction()
    val years = hasTestTag("Years") and hasClickAction()
    val months = hasTestTag("Months") and hasClickAction()
    val days = hasTestTag("Days") and hasClickAction()
    val passportId = hasTestTag("Passport Id") and hasClickAction()
    val voterId = hasTestTag("Voter Id") and hasClickAction()
    val patientId = hasTestTag("Patient Id") and hasClickAction()
    val postalCode = hasTestTag("Postal Code") and hasClickAction()
    val state = hasTestTag("State") and hasClickAction()
    val area = hasTestTag("House No., Building, Street, Area") and hasClickAction()
    val town = hasTestTag("Town/ Locality") and hasClickAction()
    val city = hasTestTag("City/ District") and hasClickAction()

    // Selection Chips
    val dobChip = hasTestTag("dob") and hasClickAction()
    val ageChip = hasTestTag("age") and hasClickAction()
    val femaleChip = hasTestTag("female") and hasClickAction()
    val maleChip = hasTestTag("male") and hasClickAction()
    val othersChip = hasTestTag("others") and hasClickAction()
    val passportIdChip = hasTestTag("Passport Id chip") and hasClickAction()
    val voterIdChip = hasTestTag("Voter Id chip") and hasClickAction()
    val patientIdChip = hasTestTag("Patient Id chip") and hasClickAction()

    // Button
    val nextBtn = hasText("Next")
    val submitBtn = hasText("Submit")
    val addWorkAddressBtn = hasTestTag("add work address btn")
    val saveBtn = hasText("Save")
    val editBtn1 = hasTestTag("edit btn 1")
    val editBtn2 = hasTestTag("edit btn 2")
    val editBtn3 = hasTestTag("edit btn 3")

    // Icons
    val backBtn = hasContentDescription("Back button")
    val clearIcon = hasContentDescription("clear icon")
    val clearWorkAddressFields = hasContentDescription("disable work address")
    val addWorkAddressIcon = hasContentDescription("add work address icon")

    // alert dialog
    val alertDialogTitle = hasTestTag("alert dialog title")
    val alertDialogDesc = hasTestTag("alert dialog description")
    val alertDialogConfirmBtn = hasTestTag("alert dialog confirm btn")
    val alertDialogCancelBtn = hasTestTag("alert dialog cancel btn")

    // Patient Registration Step 1 Tests
    @Test
    fun patientRegistrationStepOne_verify_if_all_views_exists() {
        composeTestRule.onNode(title).assertExists("Title should be \"Patient Registration\".")
        composeTestRule.onNode(clearIcon, useUnmergedTree = true).assertExists("Clear icon  should be displayed.")
        composeTestRule.onNode(backBtn, useUnmergedTree = true).assertDoesNotExist()
        composeTestRule.onNodeWithText("Basic Information").assertExists()
        composeTestRule.onNodeWithText("Page 1/3").assertExists()
        composeTestRule.onNode(firstName).assertExists("First Name input field should be displayed.")
        composeTestRule.onNode(middleName).assertExists("Middle Name input field should be displayed.")
        composeTestRule.onNode(lastName).assertExists("Last Name input field should be displayed.")
        composeTestRule.onNode(phoneNo).assertExists("Phone number input field should be displayed.")
        composeTestRule.onNode(dobInputField).assertExists("DOB input field should be displayed by default.")
        composeTestRule.onNode(email).assertExists("Email input field should be displayed.")
        composeTestRule.onNode(dobChip).assertExists("DOB selection chip should be displayed.")
        composeTestRule.onNode(dobChip).assertIsSelected()
        composeTestRule.onNode(ageChip).assertExists("Age selection chip should be displayed.")
        composeTestRule.onNode(femaleChip).assertExists("Female gender selection chip should be displayed.")
        composeTestRule.onNode(maleChip).assertExists("Male gender selection chip should be displayed.")
        composeTestRule.onNode(othersChip).assertExists("Others gender selection chip should be displayed.")
        composeTestRule.onNode(nextBtn).assertExists("Next button should be displayed.").assertIsNotEnabled()
    }

    @Test
    fun patientRegistrationStepOne_dob_selected() {
        composeTestRule.onNode(dobChip).performClick()
        composeTestRule.onNode(dobChip).assertIsSelected()
        composeTestRule.onNode(ageChip).assertIsNotSelected()
        composeTestRule.onNode(dobInputField).assertExists("DOB input field should be displayed.")
        composeTestRule.onNode(years).assertDoesNotExist()
        composeTestRule.onNode(months).assertDoesNotExist()
        composeTestRule.onNode(days).assertDoesNotExist()
    }

    @Test
    fun patientRegistrationStepOne_age_selected() {
        composeTestRule.onNode(ageChip).performClick()
        composeTestRule.onNode(ageChip).assertIsSelected()
        composeTestRule.onNode(dobChip).assertIsNotSelected()
        composeTestRule.onNode(dobInputField).assertDoesNotExist()
        composeTestRule.onNode(years).assertExists("Years input field should be displayed.")
        composeTestRule.onNode(months).assertExists("Months input field should be displayed.")
        composeTestRule.onNode(days).assertExists("Days input field should be displayed.")
    }

    @Test
    fun patientRegistrationStepOne_next_btn_enabled_on_valid_inputs() {
        composeTestRule.onNode(firstName).performTextInput("mansi")
        composeTestRule.onNode(firstName).performImeAction()
        composeTestRule.onNode(middleName).performImeAction()
        composeTestRule.onNode(lastName).performImeAction()
        Thread.sleep(2000)
        composeTestRule.onNode(dobInputField).performClick()
        composeTestRule.onNodeWithText("17").performClick()
        composeTestRule.onNodeWithText("OK").performClick()
        Thread.sleep(2000)
        composeTestRule.onNode(phoneNo).performTextInput("9876543210")
        composeTestRule.onNode(phoneNo).performImeAction()
        composeTestRule.onNode(email).performTextInput("mansi@gmail.com")
        composeTestRule.onNode(email).performImeAction()
        composeTestRule.onNodeWithTag("columnLayout").performScrollToNode(hasTestTag("genderRow"))
        composeTestRule.onNode(femaleChip).performClick()
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
    }

    @Test
    fun patientRegistrationStepOne_next_btn_disabled_on_invalid_firstName() {
        composeTestRule.onNode(firstName).performTextInput("ma")
        composeTestRule.onNode(firstName).performImeAction()
        composeTestRule.onNode(middleName).performImeAction()
        composeTestRule.onNode(lastName).performImeAction()
        composeTestRule.onNode(dobInputField).performClick()
        composeTestRule.onNodeWithText("17").performClick()
        composeTestRule.onNodeWithText("OK").performClick()
        Thread.sleep(2000)
        composeTestRule.onNode(phoneNo).performTextInput("9876543210")
        composeTestRule.onNode(phoneNo).performImeAction()
        composeTestRule.onNode(email).performTextInput("mansi@gmail.com")
        composeTestRule.onNode(email).performImeAction()
        composeTestRule.onNodeWithTag("columnLayout").performScrollToNode(hasTestTag("genderRow"))
        composeTestRule.onNode(femaleChip).performClick()
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsNotEnabled()
    }

    @Test
    fun patientRegistrationStepOne_next_btn_disabled_on_invalid_phoneNo() {
        composeTestRule.onNode(firstName).performTextInput("mansi")
        composeTestRule.onNode(firstName).performImeAction()
        composeTestRule.onNode(middleName).performImeAction()
        composeTestRule.onNode(lastName).performImeAction()
        Thread.sleep(2000)
        composeTestRule.onNode(dobInputField).performClick()
        composeTestRule.onNodeWithText("17").performClick()
        composeTestRule.onNodeWithText("OK").performClick()
        Thread.sleep(2000)
        composeTestRule.onNode(phoneNo).performTextInput("987654320")
        composeTestRule.onNode(phoneNo).performImeAction()
        composeTestRule.onNode(email).performTextInput("mansi@gmail.com")
        composeTestRule.onNode(email).performImeAction()
        composeTestRule.onNodeWithTag("columnLayout").performScrollToNode(hasTestTag("genderRow"))
        composeTestRule.onNode(femaleChip).performClick()
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsNotEnabled()
    }

    @Test
    fun patientRegistrationStepOne_next_btn_disabled_on_invalid_email() {
        composeTestRule.onNode(firstName).performTextInput("mansi")
        composeTestRule.onNode(firstName).performImeAction()
        composeTestRule.onNode(middleName).performImeAction()
        composeTestRule.onNode(lastName).performImeAction()
        Thread.sleep(2000)
        composeTestRule.onNode(dobInputField).performClick()
        composeTestRule.onNodeWithText("17").performClick()
        composeTestRule.onNodeWithText("OK").performClick()
        Thread.sleep(2000)
        composeTestRule.onNode(phoneNo).performTextInput("9876543210")
        composeTestRule.onNode(phoneNo).performImeAction()
        composeTestRule.onNode(email).performTextInput("mansi373476.com")
        composeTestRule.onNode(email).performImeAction()
        composeTestRule.onNodeWithTag("columnLayout").performScrollToNode(hasTestTag("genderRow"))
        composeTestRule.onNode(femaleChip).performClick()
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsNotEnabled()
    }

    @Test
    fun patientRegistrationStepOne_verify_next_button_click() {
        patientRegistrationStepOne_next_btn_enabled_on_valid_inputs()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNodeWithText("Page 2/3").assertExists("Should have navigated to Page 2/3.")
    }

    @Test
    fun patientRegistrationStepOne_verify_clear_icon_click() {
        composeTestRule.onNode(clearIcon, true).performClick()
        composeTestRule.onNodeWithText("My Patients").assertExists("Should be navigated to My Patients screen.")
    }

    // Patient Registration Step 2 Tests
    @Test
    fun patientRegistrationStepTwo_verify_if_all_views_exists() {
        patientRegistrationStepOne_next_btn_enabled_on_valid_inputs()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(title).assertExists("Title should be \"Patient Registration\".")
        composeTestRule.onNode(backBtn, useUnmergedTree = true).assertExists("Back Button should be displayed.")
        composeTestRule.onNode(clearIcon, useUnmergedTree = true).assertExists("Clear Icon should be displayed.")
        composeTestRule.onNodeWithText("Identification").assertExists("Identification should be displayed.")
        composeTestRule.onNodeWithText("Page 2/3").assertExists("Page 2/3 should be displayed.")
        composeTestRule.onNode(passportIdChip).assertIsSelected()
        composeTestRule.onNode(passportId).assertExists("Passport Id input field should be displayed.")
        composeTestRule.onNode(voterIdChip).assertIsNotSelected()
        composeTestRule.onNode(voterId).assertDoesNotExist()
        composeTestRule.onNode(patientIdChip).assertIsNotSelected()
        composeTestRule.onNode(patientId).assertDoesNotExist()
        composeTestRule.onNode(nextBtn).assertExists("Next button should be displayed.").assertIsNotEnabled()
    }

    @Test
    fun patientRegistrationStepTwo_verify_id_chip_selection(){
        patientRegistrationStepOne_next_btn_enabled_on_valid_inputs()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(voterIdChip).assertIsSelected()
        composeTestRule.onNode(voterId).assertExists("Voter Id input field should be displayed.")
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(patientIdChip).assertIsSelected()
        composeTestRule.onNode(patientId).assertExists("Patient Id input field should be displayed.")
    }

    @Test
    fun patientRegistrationStepTwo_next_btn_enabled_on_valid_passport_id(){
        patientRegistrationStepOne_next_btn_enabled_on_valid_inputs()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
    }

    @Test
    fun patientRegistrationStepTwo_next_btn_disabled_on_no_id_selected() {
        patientRegistrationStepOne_next_btn_enabled_on_valid_inputs()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportIdChip).performClick()
        composeTestRule.onNode(passportIdChip).assertIsNotSelected()
        composeTestRule.onNode(voterIdChip).assertIsNotSelected()
        composeTestRule.onNode(patientIdChip).assertIsNotSelected()
        composeTestRule.onNode(nextBtn).assertIsNotEnabled()
    }

    @Test
    fun patientRegistrationStepTwo_next_btn_disabled_on_invalid_passport_id(){
        patientRegistrationStepOne_next_btn_enabled_on_valid_inputs()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportId).performTextInput("ABC98765")
        composeTestRule.onNode(nextBtn).assertIsNotEnabled()
    }

    @Test
    fun patientRegistrationStepTwo_next_btn_enabled_on_valid_voter_id(){
        patientRegistrationStepOne_next_btn_enabled_on_valid_inputs()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportIdChip).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(voterId).performTextInput("XYZ1234567")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
    }

    @Test
    fun patientRegistrationStepTwo_next_btn_disabled_on_invalid_voter_id(){
        patientRegistrationStepOne_next_btn_enabled_on_valid_inputs()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportIdChip).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(voterId).performTextInput("1234567XYZ")
        composeTestRule.onNode(nextBtn).assertIsNotEnabled()
    }

    @Test
    fun patientRegistrationStepTwo_next_btn_enabled_on_valid_patient_id(){
        patientRegistrationStepOne_next_btn_enabled_on_valid_inputs()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportIdChip).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(patientId).performTextInput("abcde98765")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
    }

    @Test
    fun patientRegistrationStepTwo_next_btn_disabled_on_invalid_patient_id(){
        patientRegistrationStepOne_next_btn_enabled_on_valid_inputs()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportIdChip).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(patientId).performTextInput("abc")
        composeTestRule.onNode(nextBtn).assertIsNotEnabled()
    }

    @Test
    fun patientRegistrationStepTwo_next_btn_enabled_on_valid_id_inputs(){
        patientRegistrationStepOne_next_btn_enabled_on_valid_inputs()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(voterId).performTextInput("XYZ1234567")
        composeTestRule.onNode(patientId).performTextInput("ABCDE12345")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
    }

    @Test
    fun patientRegistrationStepTwo_verify_next_btn_click(){
        patientRegistrationStepTwo_next_btn_enabled_on_valid_id_inputs()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNodeWithText("Page 3/3").assertExists("Should have navigated to Page 3/3.")
    }

    @Test
    fun patientRegistrationStepTwo_verify_clear_icon_click() {
        patientRegistrationStepOne_next_btn_enabled_on_valid_inputs()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(clearIcon, true).performClick()
        composeTestRule.onNodeWithText("My Patients").assertExists("Should be navigated to My Patients screen.")
    }

    @Test
    fun patientRegistrationStepTwo_verify_back_btn_click() {
        patientRegistrationStepOne_next_btn_enabled_on_valid_inputs()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(backBtn, true).performClick()
        composeTestRule.onNodeWithText("Page 1/3").assertExists("Should be navigated to Page 1/3 screen.")
    }

    // Patient Registration Step 3 Tests
    @Test
    fun patientRegistrationStepThree_verify_if_all_views_exists() {
        patientRegistrationStepTwo_next_btn_enabled_on_valid_id_inputs()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(title).assertExists("Title should be \"Patient Registration\".")
        composeTestRule.onNode(backBtn, useUnmergedTree = true).assertExists("Back Button should be displayed.")
        composeTestRule.onNode(clearIcon, useUnmergedTree = true).assertExists("Clear Icon should be displayed.")
        composeTestRule.onNodeWithText("Addresses").assertExists("Addresses should be displayed.")
        composeTestRule.onNodeWithText("Page 3/3").assertExists("Page 3/3 should be displayed.")
        composeTestRule.onNodeWithText("Home Address").assertExists("Home Address title should be displayed.")
        composeTestRule.onNode(postalCode).assertExists("Postal Code input field should be displayed.")
        composeTestRule.onNode(state).assertExists("State input field should be displayed.")
        composeTestRule.onNode(area).assertExists("House No., Building, Street, Area input field should be displayed.")
        composeTestRule.onNode(town).assertExists("Town/ Locality input field should be displayed.")
        composeTestRule.onNode(city).assertExists("City/ District input field should be displayed.")
        composeTestRule.onNode(addWorkAddressIcon, useUnmergedTree = true).assertExists("Add Icon should be displayed.")
        composeTestRule.onNode(addWorkAddressBtn).assertExists("Add a work address button should be displayed.").assertIsEnabled()
        composeTestRule.onNode(submitBtn).assertExists("Submit button should be displayed.").assertIsNotEnabled()
    }

    @Test
    fun patientRegistrationStepThree_submit_btn_enabled_on_valid_home_address() {
        patientRegistrationStepTwo_next_btn_enabled_on_valid_id_inputs()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(postalCode).performTextInput("111111")
        composeTestRule.onNode(state).performClick()
        composeTestRule.onNodeWithText("Andhra Pradesh").performClick()
        composeTestRule.onNode(area).performTextInput("C-416")
        composeTestRule.onNode(town).performTextInput("Sarita Vihar")
        composeTestRule.onNode(city).performTextInput("South Delhi")
        composeTestRule.onNode(submitBtn).assertIsEnabled()
    }

    @Test
    fun patientRegistrationStepThree_verify_add_work_address_btn() {
        patientRegistrationStepTwo_next_btn_enabled_on_valid_id_inputs()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(addWorkAddressBtn).performClick()
        composeTestRule.onNode(addWorkAddressBtn).assertDoesNotExist()
        composeTestRule.onNodeWithText("Work Address").assertExists("Work Address title should be displayed.")
        composeTestRule.onNode(clearWorkAddressFields).assertExists("Clear work address icon should be displayed.")
        composeTestRule.onAllNodes(postalCode).assertCountEquals(2)
        composeTestRule.onAllNodes(state).assertCountEquals(2)
        composeTestRule.onAllNodes(area).assertCountEquals(2)
        composeTestRule.onAllNodes(town).assertCountEquals(2)
        composeTestRule.onAllNodes(city).assertCountEquals(2)
    }

    @Test
    fun patientRegistrationStepThree_verify_clear_work_address_icon() {
        patientRegistrationStepThree_verify_add_work_address_btn()
        composeTestRule.onNode(clearWorkAddressFields).performClick()
        composeTestRule.onNode(addWorkAddressBtn).assertExists("Add a work address btn should be displayed.")
        composeTestRule.onNodeWithText("Work Address").assertDoesNotExist()
        composeTestRule.onNode(clearWorkAddressFields).assertDoesNotExist()
        composeTestRule.onAllNodes(postalCode).assertCountEquals(1)
        composeTestRule.onAllNodes(state).assertCountEquals(1)
        composeTestRule.onAllNodes(area).assertCountEquals(1)
        composeTestRule.onAllNodes(town).assertCountEquals(1)
        composeTestRule.onAllNodes(city).assertCountEquals(1)
    }

    @Test
    fun patientRegistrationStepThree_submit_btn_disabled_with_work_address_fields_empty() {
        patientRegistrationStepThree_submit_btn_enabled_on_valid_home_address()
        composeTestRule.onNode(addWorkAddressBtn).performClick()
        composeTestRule.onNode(submitBtn).assertIsNotEnabled()
    }

    @Test
    fun patientRegistrationStepThree_submit_btn_enabled_with_work_address_valid_input() {
        patientRegistrationStepThree_submit_btn_enabled_on_valid_home_address()
        composeTestRule.onNode(addWorkAddressBtn).performClick()
        composeTestRule.onNodeWithTag("columnLayout").performScrollToNode(hasTestTag("end of page"))
        composeTestRule.onAllNodes(postalCode)[1].performTextInput("111111")
        composeTestRule.onAllNodes(state)[1].performClick()
        composeTestRule.onNodeWithText("Bihar").performClick()
        composeTestRule.onAllNodes(area)[1].performTextInput("C-416")
        composeTestRule.onAllNodes(town)[1].performTextInput("Sarita Vihar")
        composeTestRule.onAllNodes(city)[1].performTextInput("South Delhi")
        composeTestRule.onNode(submitBtn).assertIsEnabled()
    }

    @Test
    fun patientRegistrationStepThree_verify_submit_btn_click() {
        patientRegistrationStepThree_submit_btn_enabled_with_work_address_valid_input()
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNodeWithText("Preview").assertExists("Should be navigated to Preview page.")
    }

    @Test
    fun patientRegistrationStepThree_verify_clear_icon_click() {
        patientRegistrationStepTwo_next_btn_enabled_on_valid_id_inputs()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(clearIcon, true).performClick()
        composeTestRule.onNodeWithText("My Patients").assertExists("Should be navigated to My Patients screen.")
    }

    @Test
    fun patientRegistrationStepThree_verify_back_btn_click() {
        patientRegistrationStepTwo_next_btn_enabled_on_valid_id_inputs()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(backBtn, true).performClick()
        composeTestRule.onNodeWithText("Page 2/3").assertExists("Should be navigated to Page 2/3 screen.")
    }

    // Preview page tests
    @Test
    fun patientRegistrationPreview_verify_if_all_views_exists() {
        patientRegistrationStepThree_submit_btn_enabled_with_work_address_valid_input()
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNodeWithText("Preview").assertExists("Title should be \"Preview\".")
        composeTestRule.onNode(backBtn, useUnmergedTree = true).assertExists("Back Button should be displayed.")
        composeTestRule.onNode(clearIcon, useUnmergedTree = true).assertExists("Clear Icon should be displayed.")
        composeTestRule.onNodeWithText("Basic Information").assertExists("Basic Information card should be displayed.")
        composeTestRule.onNodeWithText("Identification").assertExists("Identification card should be displayed.")
        composeTestRule.onNodeWithText("Addresses").assertExists("Addresses card should be displayed.")
        composeTestRule.onNode(saveBtn).assertExists("Save Button should be displayed.").assertIsEnabled()
        composeTestRule.onNode(editBtn1).assertExists("Basic Info Edit Button should be displayed.").assertIsEnabled()
        composeTestRule.onNode(editBtn2).assertExists("Identification Edit Button should be displayed.").assertIsEnabled()
        composeTestRule.onNode(editBtn3).assertExists("Addresses Edit Button should be displayed.").assertIsEnabled()
    }

    @Test
    fun patientRegistrationPreview_verify_save_btn_click() {
        patientRegistrationStepThree_submit_btn_enabled_with_work_address_valid_input()
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNode(saveBtn).performClick()
        composeTestRule.onNodeWithText("My Patients").assertExists("Should be navigated to My Patients screen.")
    }

    @Test
    fun patientRegistrationPreview_verify_back_btn_click() {
        patientRegistrationStepThree_submit_btn_enabled_with_work_address_valid_input()
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNode(backBtn).performClick()
        composeTestRule.onNodeWithText("Page 3/3").assertExists("Should be navigated to Page 3/3 screen.")
    }

    @Test
    fun patientRegistrationPreview_verify_edit1_btn_click() {
        patientRegistrationStepThree_submit_btn_enabled_with_work_address_valid_input()
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNodeWithText("Page 1/3").assertExists("Should be navigated to Page 1/3 screen.")
    }

    @Test
    fun patientRegistrationPreview_verify_edit2_btn_click() {
        patientRegistrationStepThree_submit_btn_enabled_with_work_address_valid_input()
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNode(editBtn2).performClick()
        composeTestRule.onNodeWithText("Page 2/3").assertExists("Should be navigated to Page 2/3 screen.")
    }

    @Test
    fun patientRegistrationPreview_verify_edit3_btn_click() {
        patientRegistrationStepThree_submit_btn_enabled_with_work_address_valid_input()
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNodeWithTag("columnLayout").performScrollToNode(hasTestTag("end of page"))
        composeTestRule.onNode(editBtn3).performClick()
        composeTestRule.onNodeWithText("Page 3/3").assertExists("Should be navigated to Page 3/3 screen.")
    }

    @Test
    fun patientRegistrationPreview_verify_clear_icon_click() {
        patientRegistrationStepThree_submit_btn_enabled_with_work_address_valid_input()
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNode(clearIcon, true).performClick()
        composeTestRule.onNode(alertDialogTitle).assertTextEquals("Discard Changes ?")
        composeTestRule.onNode(alertDialogDesc).assertTextEquals("Are you sure you want to cancel preview and discard any changes you have made?")
        composeTestRule.onNode(alertDialogConfirmBtn, true).assertTextEquals("Confirm")
        composeTestRule.onNode(alertDialogCancelBtn, true).assertTextEquals("Cancel")
    }

    @Test
    fun patientRegistrationPreview_verify_alert_dialog_cancel_click() {
        patientRegistrationStepThree_submit_btn_enabled_with_work_address_valid_input()
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNode(clearIcon, true).performClick()
        composeTestRule.onNode(alertDialogCancelBtn, true).performClick()
        composeTestRule.onNodeWithText("Preview").assertExists("Should be navigated back to Preview page.")
    }

    @Test
    fun patientRegistrationPreview_verify_alert_dialog_confirm_click() {
        patientRegistrationStepThree_submit_btn_enabled_with_work_address_valid_input()
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNode(clearIcon, true).performClick()
        composeTestRule.onNode(alertDialogConfirmBtn, true).performClick()
        composeTestRule.onNodeWithText("My Patients").assertExists("Should be navigated to My Patients page.")
    }
}