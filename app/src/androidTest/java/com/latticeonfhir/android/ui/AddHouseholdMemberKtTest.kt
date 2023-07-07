package com.latticeonfhir.android.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class AddHouseholdMemberKtTest : UiTestsBase() {
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
        composeTestRule.onNode(backIcon, useUnmergedTree = true)
            .assertExists("Back icon should exists")
        composeTestRule.onNode(addPatientCard).assertExists("Add a patient card should exists")
        composeTestRule.onNode(addPatientBtn).assertExists("Add a patient btn should exists")
//        composeTestRule.onNodeWithText("Create a new patient, and\n" +
//                "include them in the household").assertExists()
//        composeTestRule.onNodeWithText("Search for an existing patient, and\n" +
//                "include them in the household", useUnmergedTree = true).assertExists()
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
        composeTestRule.onNode(dialogRelationDropdown)
            .assertExists("Dialog relation dropdown should exists.")
        composeTestRule.onNode(dialogNegativeBtn).assertTextContains("Go back")
            .assertExists("Dialog negative btn should exists.")
        composeTestRule.onNode(dialogPositiveBtn).assertTextContains("Create")
            .assertExists("Dialog positive btn should exists.")
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
        composeTestRule.onNodeWithText("Patient Registration")
            .assertExists("should have navigated to patient registration screen")
        composeTestRule.onNodeWithText("Page 1/3").assertExists("total steps should be 3")
    }

    @Test
    fun addHouseholdMember_verify_relation_dialog_create_btn_click() {
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
        composeTestRule.onNode(dialogRelationDropdown).performClick()
        composeTestRule.onAllNodes(dialogRelationList)[0].performClick()
        composeTestRule.onNode(dialogPositiveBtn).assertIsEnabled()
        composeTestRule.onNode(dialogPositiveBtn).performClick()
        composeTestRule.onNodeWithText("Patient Registration")
            .assertExists("should have navigated to patient registration screen")
        composeTestRule.onNodeWithText("Page 1/4").assertExists("total steps should be 4")
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
    fun addHouseholdMember_patientRegistrationStepOne_verify_all_components() {
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
        composeTestRule.onNode(dialogRelationDropdown).performClick()
        composeTestRule.onAllNodes(dialogRelationList)[0].performClick()
        composeTestRule.onNode(dialogPositiveBtn).assertIsEnabled()
        composeTestRule.onNode(dialogPositiveBtn).performClick()
        composeTestRule.onNode(titlePatientRegistration)
            .assertExists("Title should be \"Patient Registration\".")
        composeTestRule.onNode(clearIcon, useUnmergedTree = true)
            .assertExists("Clear icon  should be displayed.")
        composeTestRule.onNode(backIcon, useUnmergedTree = true).assertDoesNotExist()
        composeTestRule.onNodeWithText("Basic Information").assertExists()
        composeTestRule.onNodeWithText("Page 1/4").assertExists()
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
        composeTestRule.onNode(nextBtn).assertExists("Next button should be displayed.")
            .assertIsNotEnabled()
    }

    fun navigate_to_patient_registration(){
        composeTestRule.waitUntilAtLeastOneExists(
            patient,
            timeoutMillis = 15000
        )
        composeTestRule.onAllNodes(patient)[0].performClick()
        composeTestRule.onNode(householdMemberCard).performClick()
        composeTestRule.onNode(updateFab).performClick()
        composeTestRule.onNode(addMemberFab).performClick()
        composeTestRule.onNode(addPatientBtn).performClick()
        composeTestRule.onNode(dialogRelationDropdown).performClick()
        composeTestRule.onAllNodes(dialogRelationList)[0].performClick()
        composeTestRule.onNode(dialogPositiveBtn).performClick()
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_check_min_max_chars_in_first_name() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("a")
        composeTestRule.onNode(firstName).assertTextEquals("a", "First Name", "Name length should be between 3 and 100.", includeEditableText = true)
        composeTestRule.onNode(firstName).performTextClearance()
        composeTestRule.onNode(firstName).performTextInput("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvw")
        composeTestRule.onNode(firstName).assertTextEquals("", "First Name", "Name length should be between 3 and 100.", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_alphabets_in_first_name() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("a")
        composeTestRule.onNode(firstName).assertTextEquals("a", "First Name", "Name length should be between 3 and 100.", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_check_if_first_name_value_counter_displayed(){
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("a")
        composeTestRule.onNode(firstNameLength).assertIsDisplayed()
        composeTestRule.onNode(firstNameLength).assertTextEquals("1/100")
        composeTestRule.onNode(firstName).performTextInput("abcdefghijklmnopqrst")
        composeTestRule.onNode(firstNameLength).assertTextEquals("20/100")
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_check_error_message_in_middle_name(){
        navigate_to_patient_registration()
        composeTestRule.onNode(middleName).performTextInput("a")
        composeTestRule.onNode(middleName).assertTextEquals("a", "Middle Name", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_check_max_chars_in_middle_name(){
        navigate_to_patient_registration()
        composeTestRule.onNode(middleName).performTextInput("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvw")
        composeTestRule.onNode(middleName).assertTextEquals("", "Middle Name", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_check_middle_name_length_counter(){
        navigate_to_patient_registration()
        composeTestRule.onNode(middleName).performTextInput("abcde")
        composeTestRule.onNode(middleNameLength).assertTextEquals("5/100")
        composeTestRule.onNode(middleName).performTextClearance()
        composeTestRule.onNode(middleName).performTextInput("abcdefghijklmno")
        composeTestRule.onNode(middleNameLength).assertTextEquals("15/100")
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_check_error_message_in_last_name(){
        navigate_to_patient_registration()
        composeTestRule.onNode(lastName).performTextInput("a")
        composeTestRule.onNode(lastName).assertTextEquals("a", "Last Name", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_check_max_chars_in_last_name(){
        navigate_to_patient_registration()
        composeTestRule.onNode(lastName).performTextInput("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvw")
        composeTestRule.onNode(lastName).assertTextEquals("", "Last Name", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_check_last_name_length_counter(){
        navigate_to_patient_registration()
        composeTestRule.onNode(lastName).performTextInput("abcdefghij")
        composeTestRule.onNode(lastNameLength).assertTextEquals("10/100")
        composeTestRule.onNode(lastName).performTextClearance()
        composeTestRule.onNode(lastName).performTextInput("abcdefghijklmnoabcdefghijklmno")
        composeTestRule.onNode(lastNameLength).assertTextEquals("30/100")
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_enter_0_in_day_field_dob(){
        navigate_to_patient_registration()
        composeTestRule.onNode(day).performTextInput("0")
        composeTestRule.onNode(day).assertTextEquals("0", "Day", "Enter valid day between 1 and 31.", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_alphabet_in_day_field_dob(){
        navigate_to_patient_registration()
        composeTestRule.onNode(day).performTextInput("a")
        composeTestRule.onNode(day).assertTextEquals("", "Day", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_special_char_in_day_field_dob(){
        navigate_to_patient_registration()
        composeTestRule.onNode(day).performTextInput("@")
        composeTestRule.onNode(day).assertTextEquals("", "Day", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_click_on_month_drop_down(){
        navigate_to_patient_registration()
        composeTestRule.onNode(month).performClick()
        composeTestRule.onNodeWithText("January").assertExists("Drop down should have opened and January should be in the list.")
        composeTestRule.onNodeWithText("February").assertExists("February should be in the list..")
        composeTestRule.onNodeWithText("March").assertExists("March should be in the list..")
        composeTestRule.onNodeWithText("April").assertExists("April should be in the list..")
        composeTestRule.onNodeWithText("May").assertExists("May should be in the list..")
        composeTestRule.onNodeWithText("June").assertExists("June should be in the list..")
        composeTestRule.onNodeWithText("July").assertExists("July should be in the list..")
        composeTestRule.onNodeWithText("August").assertExists("August should be in the list..")
        composeTestRule.onNodeWithText("September").assertExists("September should be in the list..")
        composeTestRule.onNodeWithText("October").assertExists("October should be in the list..")
        composeTestRule.onNodeWithText("November").assertExists("November should be in the list..")
        composeTestRule.onNodeWithText("December").assertExists("December should be in the list..")
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_select_feb_and_enter_0_in_day(){
        navigate_to_patient_registration()
        composeTestRule.onNode(month).performClick()
        composeTestRule.onNodeWithText("February").performClick()
        composeTestRule.onNode(day).performTextInput("0")
        composeTestRule.onNode(day).assertTextEquals("0", "Day", "Enter valid day between 1 and 29.", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_enter_31_in_day_check_months_list(){
        navigate_to_patient_registration()
        composeTestRule.onNode(day).performTextInput("31")
        composeTestRule.onNode(month).performClick()
        composeTestRule.onNodeWithText("January").assertExists("January should be in the list.")
        composeTestRule.onNodeWithText("March").assertExists("March should be in the list..")
        composeTestRule.onNodeWithText("May").assertExists("May should be in the list..")
        composeTestRule.onNodeWithText("July").assertExists("July should be in the list..")
        composeTestRule.onNodeWithText("August").assertExists("August should be in the list..")
        composeTestRule.onNodeWithText("October").assertExists("October should be in the list..")
        composeTestRule.onNodeWithText("December").assertExists("December should be in the list..")
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_enter_30_in_day_check_months_list(){
        navigate_to_patient_registration()
        composeTestRule.onNode(day).performTextInput("30")
        composeTestRule.onNode(month).performClick()
        composeTestRule.onNodeWithText("January").assertExists("January should be in the list.")
        composeTestRule.onNodeWithText("March").assertExists("March should be in the list..")
        composeTestRule.onNodeWithText("April").assertExists("April should be in the list..")
        composeTestRule.onNodeWithText("May").assertExists("May should be in the list..")
        composeTestRule.onNodeWithText("June").assertExists("June should be in the list..")
        composeTestRule.onNodeWithText("July").assertExists("July should be in the list..")
        composeTestRule.onNodeWithText("August").assertExists("August should be in the list..")
        composeTestRule.onNodeWithText("September").assertExists("September should be in the list..")
        composeTestRule.onNodeWithText("October").assertExists("October should be in the list..")
        composeTestRule.onNodeWithText("November").assertExists("November should be in the list..")
        composeTestRule.onNodeWithText("December").assertExists("December should be in the list..")
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_enter_29_in_day_check_months_list(){
        navigate_to_patient_registration()
        composeTestRule.onNode(day).performTextInput("29")
        composeTestRule.onNode(month).performClick()
        composeTestRule.onNodeWithText("January").assertExists("January should be in the list.")
        composeTestRule.onNodeWithText("February").assertExists("February should be in the list.")
        composeTestRule.onNodeWithText("March").assertExists("March should be in the list..")
        composeTestRule.onNodeWithText("April").assertExists("April should be in the list..")
        composeTestRule.onNodeWithText("May").assertExists("May should be in the list..")
        composeTestRule.onNodeWithText("June").assertExists("June should be in the list..")
        composeTestRule.onNodeWithText("July").assertExists("July should be in the list..")
        composeTestRule.onNodeWithText("August").assertExists("August should be in the list..")
        composeTestRule.onNodeWithText("September").assertExists("September should be in the list..")
        composeTestRule.onNodeWithText("October").assertExists("October should be in the list..")
        composeTestRule.onNodeWithText("November").assertExists("November should be in the list..")
        composeTestRule.onNodeWithText("December").assertExists("December should be in the list..")
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_enter_30_and_select_January_and_check_error_msg(){
        navigate_to_patient_registration()
        composeTestRule.onNode(day).performTextInput("30")
        composeTestRule.onNode(month).performClick()
        composeTestRule.onNodeWithText("January").performClick()
        composeTestRule.onNode(day).assertTextEquals("30", "Day", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_enter_less_than_1900_in_year(){
        navigate_to_patient_registration()
        composeTestRule.onNode(year).performTextInput("1800")
        composeTestRule.onNode(year).assertTextEquals("1800", "Year", "Enter valid year between 1900 and 2023", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_enter_more_than_2023_in_year(){
        navigate_to_patient_registration()
        composeTestRule.onNode(year).performTextInput("2025")
        composeTestRule.onNode(year).assertTextEquals("2025", "Year", "Enter valid year between 1900 and 2023", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_check_value_of_country_code(){
        navigate_to_patient_registration()
        composeTestRule.onNode(countryCode).assertTextEquals("IND (+91)", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_enter_special_characters_in_phone_number_field(){
        navigate_to_patient_registration()
        composeTestRule.onNode(phoneNo).performTextInput("@#$")
        composeTestRule.onNode(phoneNo).assertTextEquals("", "Enter Phone Number", "Enter Valid Input", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_enter_alphanumeric_in_phone_number_field(){
        navigate_to_patient_registration()
        composeTestRule.onNode(phoneNo).performTextInput("abc123")
        composeTestRule.onNode(phoneNo).assertTextEquals("", "Enter Phone Number", "Enter Valid Input", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_enter_free_text_in_phone_number_field(){
        navigate_to_patient_registration()
        composeTestRule.onNode(phoneNo).performTextInput("abc123%$#")
        composeTestRule.onNode(phoneNo).assertTextEquals("", "Enter Phone Number", "Enter Valid Input", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_enter_numbers_in_phone_number_field(){
        navigate_to_patient_registration()
        composeTestRule.onNode(phoneNo).performTextInput("123")
        composeTestRule.onNode(phoneNo).assertTextEquals("123", "Enter Valid Input", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_enter_special_characters_in_email_field(){
        navigate_to_patient_registration()
        composeTestRule.onNodeWithTag("columnLayout").performScrollToNode(hasTestTag("genderRow"))
        composeTestRule.onNode(email).performTextInput("@#$")
        composeTestRule.onNode(email).assertTextEquals("@#$", "Email", "Enter valid userEmail (eg., abc123@gmail.com)", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_enter_alphanumeric_in_email_field(){
        navigate_to_patient_registration()
        composeTestRule.onNodeWithTag("columnLayout").performScrollToNode(hasTestTag("genderRow"))
        composeTestRule.onNode(email).performTextInput("abc123")
        composeTestRule.onNode(email).assertTextEquals("abc123", "Email", "Enter valid userEmail (eg., abc123@gmail.com)", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_enter_free_text_in_email_field(){
        navigate_to_patient_registration()
        composeTestRule.onNodeWithTag("columnLayout").performScrollToNode(hasTestTag("genderRow"))
        composeTestRule.onNode(email).performTextInput("abc123@#$")
        composeTestRule.onNode(email).assertTextEquals("abc123@#$", "Email", "Enter valid userEmail (eg., abc123@gmail.com)", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_enter_email_without_domain_in_email_field(){
        navigate_to_patient_registration()
        composeTestRule.onNodeWithTag("columnLayout").performScrollToNode(hasTestTag("genderRow"))
        composeTestRule.onNode(email).performTextInput("abc123@")
        composeTestRule.onNode(email).assertTextEquals("abc123@", "Email", "Enter valid userEmail (eg., abc123@gmail.com)", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_enter_valid_email_in_email_field(){
        navigate_to_patient_registration()
        composeTestRule.onNodeWithTag("columnLayout").performScrollToNode(hasTestTag("genderRow"))
        composeTestRule.onNode(email).performTextInput("abc123@gmail.com")
        composeTestRule.onNode(email).assertTextEquals("abc123@gmail.com", "Email", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_enter_more_than_100_chars_in_email_field(){
        navigate_to_patient_registration()
        composeTestRule.onNodeWithTag("columnLayout").performScrollToNode(hasTestTag("genderRow"))
        composeTestRule.onNode(email).performTextInput("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvw")
        composeTestRule.onNode(email).assertTextEquals("", "Email", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_next_btn_enabled_on_valid_inputs() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_verify_next_button_click() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNodeWithText("Page 2/4")
            .assertExists("Should have navigated to Page 2/4.")
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepOne_different_gender_chips_selection() {
        navigate_to_patient_registration()
        composeTestRule.onNodeWithTag("columnLayout").performScrollToNode(hasTestTag("genderRow"))
        composeTestRule.onNode(maleChip).performClick()
        composeTestRule.onNode(maleChip).assertIsSelected()
        composeTestRule.onNode(femaleChip).assertIsNotSelected()
        composeTestRule.onNode(othersChip).assertIsNotSelected()
        composeTestRule.onNode(femaleChip).performClick()
        composeTestRule.onNode(maleChip).assertIsNotSelected()
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(othersChip).assertIsNotSelected()
        composeTestRule.onNode(othersChip).performClick()
        composeTestRule.onNode(maleChip).assertIsNotSelected()
        composeTestRule.onNode(femaleChip).assertIsNotSelected()
        composeTestRule.onNode(othersChip).assertIsSelected()
    }

    // step two
    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_verify_if_all_views_exists() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(titlePatientRegistration).assertExists("Title should be \"Patient Registration\".")
        composeTestRule.onNode(backIcon, useUnmergedTree = true)
            .assertExists("Back Button should be displayed.")
        composeTestRule.onNode(clearIcon, useUnmergedTree = true)
            .assertExists("Clear Icon should be displayed.")
        composeTestRule.onNodeWithText("Identification")
            .assertExists("Identification should be displayed.")
        composeTestRule.onNodeWithText("Page 2/4").assertExists("Page 2/4 should be displayed.")
        composeTestRule.onNode(passportIdChip).assertIsSelected()
        composeTestRule.onNode(passportId)
            .assertExists("Passport ID input field should be displayed.")
        composeTestRule.onNode(voterIdChip).assertIsNotSelected()
        composeTestRule.onNode(voterId).assertDoesNotExist()
        composeTestRule.onNode(patientIdChip).assertIsNotSelected()
        composeTestRule.onNode(patientId).assertDoesNotExist()
        composeTestRule.onNode(nextBtn).assertExists("Next button should be displayed.")
            .assertIsNotEnabled()
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_verify_id_chip_selection() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(voterIdChip).assertIsSelected()
        composeTestRule.onNode(voterId).assertExists("Voter ID input field should be displayed.")
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(patientIdChip).assertIsSelected()
        composeTestRule.onNode(patientId)
            .assertExists("Patient ID input field should be displayed.")
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_next_btn_enabled_on_valid_passport_id() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_next_btn_disabled_on_no_id_selected() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportIdChip).performClick()
        composeTestRule.onNode(passportIdChip).assertIsNotSelected()
        composeTestRule.onNode(voterIdChip).assertIsNotSelected()
        composeTestRule.onNode(patientIdChip).assertIsNotSelected()
        composeTestRule.onNode(nextBtn).assertIsNotEnabled()
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_next_btn_disabled_on_invalid_passport_id() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportId).performTextInput("ABC98765")
        composeTestRule.onNode(nextBtn).assertIsNotEnabled()
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_enter_alphabets_in_passport_id() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportId).performTextInput("ABCDEFGH")
        composeTestRule.onNode(passportId).assertTextEquals("ABCDEFGH", "Passport ID", "Enter valid Passport ID (eg., A1098765)", includeEditableText = true)
        composeTestRule.onNode(nextBtn).assertIsNotEnabled()
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_enter_numeric_in_passport_id() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportId).performTextInput("12345678")
        composeTestRule.onNode(passportId).assertTextEquals("12345678", "Passport ID", "Enter valid Passport ID (eg., A1098765)", includeEditableText = true)
        composeTestRule.onNode(nextBtn).assertIsNotEnabled()
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_enter_special_chars_in_passport_id() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportId).performTextInput("!@#$%^&*")
        composeTestRule.onNode(passportId).assertTextEquals("!@#$%^&*", "Passport ID", "Enter valid Passport ID (eg., A1098765)", includeEditableText = true)
        composeTestRule.onNode(nextBtn).assertIsNotEnabled()
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_passport_id_length_tests(){
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportIdLength).assertExists("Passport Id length should be displayed.")
        composeTestRule.onNode(passportIdLength).assertTextEquals("0/8")
        composeTestRule.onNode(passportId).performTextInput("A")
        composeTestRule.onNode(passportIdLength).assertTextEquals("1/8")
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_next_btn_enabled_on_valid_voter_id() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportIdChip).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(voterId).performTextInput("XYZ1234567")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_next_btn_disabled_on_invalid_voter_id() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportIdChip).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(voterId).performTextInput("1234567XYZ")
        composeTestRule.onNode(nextBtn).assertIsNotEnabled()
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_enter_alphabets_in_voter_id() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(voterId).performTextInput("ABCDEFGHIJ")
        composeTestRule.onNode(voterId).assertTextEquals("ABCDEFGHIJ", "Voter ID", "Enter valid Voter Id (eg., XYZ9876543)", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_enter_numeric_in_voter_id() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(voterId).performTextInput("1234567890")
        composeTestRule.onNode(voterId).assertTextEquals("1234567890", "Voter ID", "Enter valid Voter Id (eg., XYZ9876543)", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_enter_special_chars_in_voter_id() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(voterId).performTextInput("!@#$%^&*()")
        composeTestRule.onNode(voterId).assertTextEquals("!@#$%^&*()", "Voter ID", "Enter valid Voter Id (eg., XYZ9876543)", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_voter_id_length_tests(){
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(voterIdLength).assertExists("Voter Id length should be displayed.")
        composeTestRule.onNode(voterIdLength).assertTextEquals("0/10")
        composeTestRule.onNode(voterId).performTextInput("ABC12")
        composeTestRule.onNode(voterIdLength).assertTextEquals("5/10")
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_next_btn_enabled_on_valid_patient_id() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportIdChip).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(patientId).performTextInput("abcde98765")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_next_btn_disabled_on_invalid_patient_id() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportIdChip).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(patientId).performTextInput("abc")
        composeTestRule.onNode(nextBtn).assertIsNotEnabled()
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_enter_alphabets_in_patient_id() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(patientId).performTextInput("ABCDE")
        composeTestRule.onNode(patientId).assertTextEquals("ABCDE", "Patient ID", "Patient Id should be of length 10.", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_enter_numeric_in_patient_id() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(patientId).performTextInput("12345")
        composeTestRule.onNode(patientId).assertTextEquals("12345", "Patient ID", "Patient Id should be of length 10.", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_enter_special_chars_in_patient_id() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(patientId).performTextInput("!@#$%^")
        composeTestRule.onNode(patientId).assertTextEquals("!@#$%^", "Patient ID", "Patient Id should be of length 10.", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_patient_id_length_tests(){
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(patientIdLength).assertExists("Patient Id length should be displayed.")
        composeTestRule.onNode(patientIdLength).assertTextEquals("0/10")
        composeTestRule.onNode(patientId).performTextInput("ABC123456")
        composeTestRule.onNode(patientIdLength).assertTextEquals("9/10")
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_unselecting_id_fields_tests(){
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(passportIdChip).performClick()
        composeTestRule.onNode(passportId).assertDoesNotExist()
        composeTestRule.onNode(passportIdChip).performClick()
        composeTestRule.onNode(passportId).assertTextEquals("", "Passport ID")
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(patientId).performTextInput("ABCDE12345")
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(patientId).assertDoesNotExist()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(patientId).assertTextEquals("", "Patient ID")
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(voterId).performTextInput("XYZ1234567")
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(voterId).assertDoesNotExist()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(voterId).assertTextEquals("", "Voter ID")
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_next_btn_enabled_on_valid_id_inputs() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(voterId).performTextInput("XYZ1234567")
        composeTestRule.onNode(patientId).performTextInput("ABCDE12345")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_verify_next_btn_click() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(voterId).performTextInput("XYZ1234567")
        composeTestRule.onNode(patientId).performTextInput("ABCDE12345")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNodeWithText("Page 3/4")
            .assertExists("Should have navigated to Page 3/4.")
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_verify_clear_icon_click() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(clearIcon, true).performClick()
        composeTestRule.onNode(alertDialogTitle).assertTextEquals("Discard Changes?")
        composeTestRule.onNode(alertDialogDesc)
            .assertTextEquals("Are you sure you want to discard this patient record?")
        composeTestRule.onNode(alertDialogConfirmBtn, true).assertTextEquals("Yes, discard")
        composeTestRule.onNode(alertDialogCancelBtn, true).assertTextEquals("No, go back")
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_verify_dialog_negative_btn_click() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(clearIcon, true).performClick()
        composeTestRule.onNode(alertDialogCancelBtn, true).performClick()
        composeTestRule.onNode(alertDialogTitle).assertDoesNotExist()
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_verify_dialog_positive_btn_click() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(clearIcon, true).performClick()
        composeTestRule.onNode(alertDialogConfirmBtn, true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Add a household member")
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepTwo_verify_back_btn_click() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(backIcon, true).performClick()
        composeTestRule.onNodeWithText("Page 1/4")
            .assertExists("Should be navigated to Page 1/4 screen.")
    }
    
    // step three
    @Test
    fun addHouseholdMember_patientRegistrationStepThree_verify_if_all_views_exists() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(voterId).performTextInput("XYZ1234567")
        composeTestRule.onNode(patientId).performTextInput("ABCDE12345")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(titlePatientRegistration).assertExists("Title should be \"Patient Registration\".")
        composeTestRule.onNode(backIcon, useUnmergedTree = true)
            .assertExists("Back Button should be displayed.")
        composeTestRule.onNode(clearIcon, useUnmergedTree = true)
            .assertExists("Clear Icon should be displayed.")
        composeTestRule.onNodeWithText("Addresses").assertExists("Addresses should be displayed.")
        composeTestRule.onNodeWithText("Page 3/4").assertExists("Page 3/4 should be displayed.")
        composeTestRule.onNodeWithText("Home Address")
            .assertExists("Home Address subtitle should be displayed.")
        composeTestRule.onNode(postalCode)
            .assertExists("Postal Code input field should be displayed.")
        composeTestRule.onNode(state).assertExists("State input field should be displayed.")
        composeTestRule.onNode(addressLine1)
            .assertExists("Address Line 1 input field should be displayed.")
        composeTestRule.onNode(addressLine2)
            .assertExists("Address Line 2 input field should be displayed.")
        composeTestRule.onNode(city).assertExists("City input field should be displayed.")
        composeTestRule.onNode(district).assertExists("District input field should be displayed.")
        composeTestRule.onNode(submitBtn).assertExists("Submit button should be displayed.")
            .assertIsNotEnabled()
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_submit_btn_enabled_on_valid_home_address() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(voterId).performTextInput("XYZ1234567")
        composeTestRule.onNode(patientId).performTextInput("ABCDE12345")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(postalCode).performTextInput("111111")
        composeTestRule.onNode(state).performClick()
        composeTestRule.onNodeWithText("Andhra Pradesh").performClick()
        composeTestRule.onNode(addressLine1).performTextInput("C-416")
        composeTestRule.onNode(addressLine2).performTextInput("Sarita Vihar")
        composeTestRule.onNode(city).performTextInput("South Delhi")
        composeTestRule.onNode(submitBtn).assertIsEnabled()
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_enter_alphabets_in_postal_address() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(postalCode).performTextInput("abcdef")
        composeTestRule.onNode(postalCode).assertTextEquals("", "Postal Code *", "Enter valid 6 digit postal code", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_enter_special_chars_in_postal_address() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(postalCode).performTextInput("!@#$%^")
        composeTestRule.onNode(postalCode).assertTextEquals("", "Postal Code *", "Enter valid 6 digit postal code", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_enter_numbers_in_postal_address() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(postalCode).performTextInput("123456")
        composeTestRule.onNode(postalCode).assertTextEquals("123456", "Postal Code *", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_error_states_on_different_values_postal_address() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(postalCode).performTextInput("12")
        composeTestRule.onNode(postalCode).assertTextEquals("12", "Postal Code *", "Enter valid 6 digit postal code", includeEditableText = true)
        composeTestRule.onNode(postalCode).performTextClearance()
        composeTestRule.onNode(postalCode).performTextInput("12345")
        composeTestRule.onNode(postalCode).assertTextEquals("12345", "Postal Code *", "Enter valid 6 digit postal code", includeEditableText = true)
        composeTestRule.onNode(postalCode).performTextClearance()
        composeTestRule.onNode(postalCode).performTextInput("123456")
        composeTestRule.onNode(postalCode).assertTextEquals("123456", "Postal Code *", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_check_if_state_drop_down_displayed() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(state).performClick()
        composeTestRule.onNode(stateDropDownList).assertExists("State dropdown list should be displayed.")
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_check_default_value_of_state() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(state).assertTextEquals("", "State *", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_keeping_state_empty() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(postalCode).performTextInput("111111")
        composeTestRule.onNode(addressLine1).performTextInput("C-416")
        composeTestRule.onNode(addressLine2).performTextInput("Sarita Vihar")
        composeTestRule.onNode(city).performTextInput("South Delhi")
        composeTestRule.onNode(submitBtn).assertIsNotEnabled()
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_150_chars_in_address_line_1() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(addressLine1).performTextInput("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrst")
        composeTestRule.onNode(addressLine1).assertTextEquals("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrst", "Address Line 1 *", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_more_than_150_chars_in_address_line_1() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(addressLine1).performTextInput("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstu")
        composeTestRule.onNode(addressLine1).assertTextEquals("", "Address Line 1 *", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_free_text_in_address_line_2() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(addressLine2).performTextInput("abcd123!@#$")
        composeTestRule.onNode(addressLine2).assertTextEquals("abcd123!@#$", "Address Line 2", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_150_chars_in_address_line_2() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(addressLine2).performTextInput("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrst")
        composeTestRule.onNode(addressLine2).assertTextEquals("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrst", "Address Line 2", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_more_than_150_chars_in_address_line_2() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(addressLine2).performTextInput("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstu")
        composeTestRule.onNode(addressLine2).assertTextEquals("", "Address Line 2", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_free_text_in_city() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(city).performTextInput("abcd123!@#$")
        composeTestRule.onNode(city).assertTextEquals("abcd123!@#$", "City *", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_150_chars_in_city() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(city).performTextInput("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrst")
        composeTestRule.onNode(city).assertTextEquals("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrst", "City *", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_more_than_150_chars_in_city() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(city).performTextInput("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstu")
        composeTestRule.onNode(city).assertTextEquals("", "City *", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_free_text_in_district() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(district).performTextInput("abcd123!@#$")
        composeTestRule.onNode(district).assertTextEquals("abcd123!@#$", "District", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_150_chars_in_district() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(district).performTextInput("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrst")
        composeTestRule.onNode(district).assertTextEquals("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrst", "District", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_more_than_150_chars_in_district() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(district).performTextInput("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstu")
        composeTestRule.onNode(district).assertTextEquals("", "District", includeEditableText = true)
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_verify_submit_btn_click() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(voterId).performTextInput("XYZ1234567")
        composeTestRule.onNode(patientId).performTextInput("ABCDE12345")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(postalCode).performTextInput("111111")
        composeTestRule.onNode(state).performClick()
        composeTestRule.onNodeWithText("Andhra Pradesh").performClick()
        composeTestRule.onNode(addressLine1).performTextInput("C-416")
        composeTestRule.onNode(addressLine2).performTextInput("Sarita Vihar")
        composeTestRule.onNode(city).performTextInput("South Delhi")
        composeTestRule.onNode(submitBtn).assertIsEnabled()
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNodeWithText("Preview")
            .assertExists("Should be navigated to Preview page.")
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_verify_clear_icon_click() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(voterId).performTextInput("XYZ1234567")
        composeTestRule.onNode(patientId).performTextInput("ABCDE12345")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(clearIcon, true).performClick()
        composeTestRule.onNode(alertDialogTitle).assertTextEquals("Discard Changes?")
        composeTestRule.onNode(alertDialogDesc)
            .assertTextEquals("Are you sure you want to discard this patient record?")
        composeTestRule.onNode(alertDialogConfirmBtn, true).assertTextEquals("Yes, discard")
        composeTestRule.onNode(alertDialogCancelBtn, true).assertTextEquals("No, go back")
    }

    @Test
    fun addHouseholdMember_patientRegistrationStepThree_verify_back_btn_click() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(voterId).performTextInput("XYZ1234567")
        composeTestRule.onNode(patientId).performTextInput("ABCDE12345")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(backIcon, true).performClick()
        composeTestRule.onNodeWithText("Page 2/4")
            .assertExists("Should be navigated to Page 2/4 screen.")
    }

    // preview page
    @Test
    fun addHouseholdMember_patientRegistrationPreview_verify_if_all_views_exists() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(voterId).performTextInput("XYZ1234567")
        composeTestRule.onNode(patientId).performTextInput("ABCDE12345")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(postalCode).performTextInput("111111")
        composeTestRule.onNode(state).performClick()
        composeTestRule.onNodeWithText("Andhra Pradesh").performClick()
        composeTestRule.onNode(addressLine1).performTextInput("C-416")
        composeTestRule.onNode(addressLine2).performTextInput("Sarita Vihar")
        composeTestRule.onNode(city).performTextInput("South Delhi")
        composeTestRule.onNode(submitBtn).assertIsEnabled()
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNodeWithText("Preview").assertExists("Title should be \"Preview\".")
        composeTestRule.onNode(backIcon, useUnmergedTree = true)
            .assertExists("Back Button should be displayed.")
        composeTestRule.onNode(clearIcon, useUnmergedTree = true)
            .assertExists("Clear Icon should be displayed.")
        composeTestRule.onNodeWithText("Basic Information")
            .assertExists("Basic Information card should be displayed.")
        composeTestRule.onNodeWithText("Identification")
            .assertExists("Identification card should be displayed.")
        composeTestRule.onNodeWithText("Addresses")
            .assertExists("Addresses card should be displayed.")
        composeTestRule.onNode(saveBtn).assertExists("Save Button should be displayed.")
            .assertIsEnabled()
        composeTestRule.onNode(editBtn1).assertExists("Basic Info Edit Button should be displayed.")
            .assertIsEnabled()
        composeTestRule.onNode(editBtn2)
            .assertExists("Identification Edit Button should be displayed.").assertIsEnabled()
        composeTestRule.onNode(editBtn3).assertExists("Addresses Edit Button should be displayed.")
            .assertIsEnabled()
    }

    @Test
    fun addHouseholdMember_patientRegistrationPreview_verify_clear_icon_click() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(voterId).performTextInput("XYZ1234567")
        composeTestRule.onNode(patientId).performTextInput("ABCDE12345")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(postalCode).performTextInput("111111")
        composeTestRule.onNode(state).performClick()
        composeTestRule.onNodeWithText("Andhra Pradesh").performClick()
        composeTestRule.onNode(addressLine1).performTextInput("C-416")
        composeTestRule.onNode(addressLine2).performTextInput("Sarita Vihar")
        composeTestRule.onNode(city).performTextInput("South Delhi")
        composeTestRule.onNode(submitBtn).assertIsEnabled()
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNode(clearIcon, true).performClick()
        composeTestRule.onNode(alertDialogTitle).assertTextEquals("Discard Changes?")
        composeTestRule.onNode(alertDialogDesc).assertTextEquals("Are you sure you want to discard this patient record?")
        composeTestRule.onNode(alertDialogConfirmBtn, true).assertTextEquals("Yes, discard")
        composeTestRule.onNode(alertDialogCancelBtn, true).assertTextEquals("No, go back")
    }

    @Test
    fun addHouseholdMember_patientRegistrationPreview_verify_preview_content(){
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(voterId).performTextInput("XYZ1234567")
        composeTestRule.onNode(patientId).performTextInput("ABCDE12345")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(postalCode).performTextInput("111111")
        composeTestRule.onNode(state).performClick()
        composeTestRule
            .onNodeWithText("Andhra Pradesh").performClick()
        composeTestRule.onNode(addressLine1).performTextInput("C-416")
        composeTestRule.onNode(addressLine2).performTextInput("Sarita Vihar")
        composeTestRule.onNode(city).performTextInput("South Delhi")
        composeTestRule.onNode(submitBtn).assertIsEnabled()
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNode(nameTag).assertTextEquals("Relative, Female")
        composeTestRule.onNode(dobTag).assertTextEquals("23-01-2001")
        composeTestRule.onNode(phoneNoTag).assertTextEquals("+91 9876543210")
        composeTestRule.onNode(passportIdTag).assertTextEquals("A1098765")
        composeTestRule.onNode(voterIdTag).assertTextEquals("XYZ1234567")
        composeTestRule.onNode(patientIdTag).assertTextEquals("ABCDE12345")
        composeTestRule.onNode(addressLine1Tag).assertTextEquals("C-416, Sarita Vihar")
        composeTestRule.onNode(addressLine2Tag).assertTextEquals("South Delhi")
        composeTestRule.onNode(addressLine3Tag).assertTextEquals("Andhra Pradesh, 111111")
    }

    @Test
    fun addHouseholdMember_patientRegistrationPreview_edit_first_name_and_verify_content_on_preview(){
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(voterId).performTextInput("XYZ1234567")
        composeTestRule.onNode(patientId).performTextInput("ABCDE12345")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(postalCode).performTextInput("111111")
        composeTestRule.onNode(state).performClick()
        composeTestRule.onNodeWithText("Andhra Pradesh").performClick()
        composeTestRule.onNode(addressLine1).performTextInput("C-416")
        composeTestRule.onNode(addressLine2).performTextInput("Sarita Vihar")
        composeTestRule.onNode(city).performTextInput("South Delhi")
        composeTestRule.onNode(submitBtn).assertIsEnabled()
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNode(editBtn1).performClick()
        composeTestRule.onNode(firstName).performTextClearance()
        composeTestRule.onNode(firstName).performTextInput("edited")
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNode(nameTag).assertTextEquals("Edited, Female")
    }

    @Test
    fun addHouseholdMember_patientRegistrationPreview_verify_alert_dialog_cancel_click() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(voterId).performTextInput("XYZ1234567")
        composeTestRule.onNode(patientId).performTextInput("ABCDE12345")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(postalCode).performTextInput("111111")
        composeTestRule.onNode(state).performClick()
        composeTestRule.onNodeWithText("Andhra Pradesh").performClick()
        composeTestRule.onNode(addressLine1).performTextInput("C-416")
        composeTestRule.onNode(addressLine2).performTextInput("Sarita Vihar")
        composeTestRule.onNode(city).performTextInput("South Delhi")
        composeTestRule.onNode(submitBtn).assertIsEnabled()
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNode(clearIcon, true).performClick()
        composeTestRule.onNode(alertDialogCancelBtn, true).performClick()
        composeTestRule.onNodeWithText("Preview")
            .assertExists("Should be navigated back to Preview page.")
    }

    @Test
    fun addHouseholdMember_patientRegistrationPreview_verify_alert_dialog_confirm_click() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(voterIdChip).performClick()
        composeTestRule.onNode(patientIdChip).performClick()
        composeTestRule.onNode(passportId).performTextInput("A1098765")
        composeTestRule.onNode(voterId).performTextInput("XYZ1234567")
        composeTestRule.onNode(patientId).performTextInput("ABCDE12345")
        composeTestRule.onNode(nextBtn).assertIsEnabled()
        composeTestRule.onNode(nextBtn).performClick()
        composeTestRule.onNode(postalCode).performTextInput("111111")
        composeTestRule.onNode(state).performClick()
        composeTestRule.onNodeWithText("Andhra Pradesh").performClick()
        composeTestRule.onNode(addressLine1).performTextInput("C-416")
        composeTestRule.onNode(addressLine2).performTextInput("Sarita Vihar")
        composeTestRule.onNode(city).performTextInput("South Delhi")
        composeTestRule.onNode(submitBtn).assertIsEnabled()
        composeTestRule.onNode(submitBtn).performClick()
        composeTestRule.onNode(clearIcon, true).performClick()
        composeTestRule.onNode(alertDialogConfirmBtn, true).performClick()
        composeTestRule.onNode(title).assertTextEquals("Add a household member")
    }
    
    @Test
    fun addHouseholdMember_patientRegistrationPreview_verify_save_btn_click() {
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
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
        composeTestRule.onNode(heading).assertTextEquals("Confirm Relationship")
    }

    @Test
    fun addHouseholdMember_confirmRelationship_verify_if_all_components_exists(){
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
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
        composeTestRule.onNode(heading).assertTextEquals("Confirm Relationship")
        composeTestRule.onNode(clearIcon).assertExists("Clear icon should exists.")
        composeTestRule.onNode(connectBtn).assertExists("Connect button should exists.")
        composeTestRule.onNodeWithText("Add member").assertExists("Add member heading should exists.")
        composeTestRule.onNodeWithText("Page 4/4").assertExists("Page 4/4 pagination should exists.")
        composeTestRule.onAllNodes(memberDetailCards).assertCountEquals(2)
        composeTestRule.onAllNodes(deleteMemberIcon).assertCountEquals(2)
        composeTestRule.onAllNodes(editMemberIcon).assertCountEquals(2)
    }

    @Test
    fun addHouseholdMember_confirmRelationship_verify_edit_btn_click(){
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
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
        composeTestRule.onAllNodes(editMemberIcon)[0].performClick()
        composeTestRule.onNode(dialogTitle).assertTextEquals("Edit relationship").assertExists("edit dialog should be displayed.")
    }

    @Test
    fun addHouseholdMember_confirmRelationship_verify_delete_btn_click(){
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
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
        composeTestRule.onAllNodes(deleteMemberIcon)[0].performClick()
        composeTestRule.onNode(dialogTitle).assertTextEquals("Remove relationship?").assertExists("dialog should be displayed.")
        composeTestRule.onNode(dialogPositiveBtn).assertTextEquals("Confirm").assertExists("confirm btn should be displayed.")
        composeTestRule.onNode(dialogNegativeBtn).assertTextEquals("Cancel").assertExists("cancel should be displayed.")
    }

    @Test
    fun addHouseholdMember_confirmRelationship_verify_connect_patient_btn_navgation(){
        navigate_to_patient_registration()
        composeTestRule.onNode(firstName).performTextInput("relative")
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
        composeTestRule.onNode(femaleChip).assertIsSelected()
        composeTestRule.onNode(nextBtn).assertIsEnabled()
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
        composeTestRule.onNode(connectBtn).performClick()
        composeTestRule.onNode(title).assertTextEquals("Household members")
    }

    @Test
    fun zzzz_logout() {
        composeTestRule.onNode(profile_tab).performClick()
        composeTestRule.onNode(logoutIcon).performClick()
        composeTestRule.onNodeWithText("Logout").performClick()
    }
}