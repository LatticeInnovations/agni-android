package com.latticeonfhir.android.ui

import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasNoClickAction
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.latticeonfhir.android.ui.main.MainActivity
import org.junit.Rule

open class UiTestsBase {
    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    val heading = hasTestTag("HEADING_TAG")
    val subHeading = hasTestTag("SUB_HEADING_TAG")
    val inputField = hasTestTag("INPUT_FIELD")
    val button = hasTestTag("BUTTON")
    val resendButton = hasTestTag("RESEND_BUTTON")
    val firstDigit = hasTestTag("FIRST_DIGIT")
    val secondDigit = hasTestTag("SECOND_DIGIT")
    val thirdDigit = hasTestTag("THIRD_DIGIT")
    val fourDigit = hasTestTag("FOUR_DIGIT")
    val fiveDigit = hasTestTag("FIVE_DIGIT")
    val sixDigit = hasTestTag("SIX_DIGIT")
    val errorMsg = hasTestTag("ERROR_MSG")
    val twoMinTimer = hasTestTag("TWO_MIN_TIMER")
    val backIcon = hasContentDescription("BACK_ICON")
    val profileIcon = hasContentDescription("profile icon")

    // dialog
    val dialogTitle = hasTestTag("DIALOG_TITLE")
    val dialogDesc = hasTestTag("DIALOG_DESCRIPTION")
    val dialogPositiveBtn = hasTestTag("POSITIVE_BTN")
    val dialogNegativeBtn = hasTestTag("NEGATIVE_BTN")

    // for logout
    val profileTab = hasTestTag("Profile tab") and hasClickAction()
    val logoutIcon = hasContentDescription("LOG_OUT_ICON")

    // advanced search
    val searchIcon = hasContentDescription("SEARCH_ICON")
    val advancedSearchButton = hasText("Advanced search") and hasClickAction()

    // Placeholders
    val titleAdvancedSearch = hasText("Advanced Search")
    val headingSearch = hasText("Search using any of the field below")
    val ageRangeTitle = hasText("Select age range")
    val gender = hasText("Gender")
    val address = hasText("Address")
    val searchTitle = hasTestTag("SEARCH_TITLE_TEXT")
    val addPatientText = hasTestTag("ADD_PATIENT_TEXT")

    // Icons
    val clearIcon = hasContentDescription("CLEAR_ICON")

    // chips
    val chipCategory1 = hasText("Category 1")
    val chipCategory2 = hasText("Category 2")
    val chipCategory3 = hasText("Category 3")

    // Input Fields
    val patientName = hasTestTag("Patient Name") and hasClickAction()
    val patientId = hasTestTag("Patient ID") and hasClickAction()
    val postalCode = hasTestTag("Postal Code *") and hasClickAction()
    val addressLine1 = hasTestTag("Address Line 1 *") and hasClickAction()
    val addressLine2 = hasTestTag("Address Line 2") and hasClickAction()
    val city = hasTestTag("City *") and hasClickAction()
    val district = hasTestTag("District") and hasClickAction()
    val minValue = hasTestTag("MIN_VALUE") and hasClickAction()
    val maxValue = hasTestTag("MAX_VALUE") and hasClickAction()

    // Selection Chips
    val femaleChip = hasTestTag("female") and hasClickAction()
    val maleChip = hasTestTag("male") and hasClickAction()
    val othersChip = hasTestTag("other") and hasClickAction()

    // Button
    val searchBtn = hasText("Search") and hasClickAction()
    val connectBtn = hasTestTag("CONNECT_BTN") and hasClickAction()

    // Slider
    val ageRangeSlider = hasTestTag("age range slider")

    // list
    val patientList = hasTestTag("patients list") and hasScrollAction()

    // dropdown
    val lastFacilityVisit = hasTestTag("last facility visit") and hasClickAction()
    val state = hasTestTag("State *") and hasClickAction()
    val stateDropDownList = hasTestTag("STATE_DROP_DOWN")

    // end of screen
    val endOfScreen = hasTestTag("END_OF_SCREEN")
    val rootLayout = hasTestTag("ROOT_LAYOUT")

    // bottom nav bar
    val bottomNavBar = hasTestTag("BOTTOM_NAV_BAR")

    // landing screen

    // PlaceHolders
    val titleMyPatients = hasText("My Patients") and hasNoClickAction()
    val queueTitle = hasText("Queue") and hasNoClickAction()
    val profileTitle = hasText("Profile") and hasNoClickAction()

    // Icons
    val addPatientIcon = hasContentDescription("ADD_PATIENT_ICON")

    // list
    val previousSearchList = hasTestTag("PREVIOUS_SEARCHES") and hasScrollAction()

    // bottom nav bar icons
    val my_patients_icon = hasContentDescription("My Patients")
    val queue_icon = hasContentDescription("Queue")
    val profile_icon = hasContentDescription("Profile")

    // bottom nav bar tabs
    val my_patients_tab = hasTestTag("My Patients tab") and hasClickAction()
    val queue_tab = hasTestTag("Queue tab") and hasClickAction()
    val profile_tab = hasTestTag("Profile tab") and hasClickAction()

    //search layout
    val searchLayout = hasTestTag("SEARCH_LAYOUT")
    val searchTextField = hasTestTag("SEARCH_TEXT_FIELD")


    // Placeholders
    val titlePatientRegistration = hasText("Patient Registration")

    // Input Fields
    val firstName = hasTestTag("First Name") and hasClickAction()
    val firstNameLength = hasTestTag("FIRST_NAME_LENGTH")
    val middleName = hasTestTag("Middle Name") and hasClickAction()
    val middleNameLength = hasTestTag("MIDDLE_NAME_LENGTH")
    val lastName = hasTestTag("Last Name") and hasClickAction()
    val lastNameLength = hasTestTag("LAST_NAME_LENGTH")
    val countryCode = hasTestTag("COUNTRY_CODE") and hasClickAction()
    val phoneNo = hasTestTag("Phone Number") and hasClickAction()
    val email = hasTestTag("Email") and hasClickAction()
    val year = hasTestTag("Year") and hasClickAction()
    val month = hasTestTag("Month")
    val day = hasTestTag("Day") and hasClickAction()
    val years = hasTestTag("Years") and hasClickAction()
    val months = hasTestTag("Months") and hasClickAction()
    val days = hasTestTag("Days") and hasClickAction()
    val passportId = hasTestTag("Passport ID") and hasClickAction()
    val passportIdLength = hasTestTag("PASSPORT_ID_LENGTH")
    val voterId = hasTestTag("Voter ID") and hasClickAction()
    val voterIdLength = hasTestTag("VOTER_ID_LENGTH")
    val patientIdLength = hasTestTag("PATIENT_ID_LENGTH")

    // Selection Chips
    val dobChip = hasTestTag("dob") and hasClickAction()
    val ageChip = hasTestTag("age") and hasClickAction()
    val passportIdChip = hasTestTag("Passport ID chip") and hasClickAction()
    val voterIdChip = hasTestTag("Voter ID chip") and hasClickAction()
    val patientIdChip = hasTestTag("Patient ID chip") and hasClickAction()

    // Button
    val nextBtn = hasText("Next")
    val submitBtn = hasText("Submit & Preview")
    val addWorkAddressBtn = hasTestTag("add work address btn")
    val saveBtn = hasText("Save")
    val editBtn1 = hasTestTag("edit btn 1")
    val editBtn2 = hasTestTag("edit btn 2")
    val editBtn3 = hasTestTag("edit btn 3")

    // Icons
    val clearWorkAddressFields = hasContentDescription("disable work address")
    val addWorkAddressIcon = hasContentDescription("add work address icon")

    // alert dialog
    val alertDialogTitle = hasTestTag("alert dialog title")
    val alertDialogDesc = hasTestTag("alert dialog description")
    val alertDialogConfirmBtn = hasTestTag("alert dialog confirm btn")
    val alertDialogCancelBtn = hasTestTag("alert dialog cancel btn")

    // preview screen content
    val nameTag = hasTestTag("NAME_TAG")
    val dobTag = hasTestTag("DOB_TAG")
    val phoneNoTag = hasTestTag("PHONE_NO_TAG")
    val passportIdTag = hasTestTag("PASSPORT_ID_TAG")
    val voterIdTag = hasTestTag("VOTER_ID_TAG")
    val patientIdTag = hasTestTag("PATIENT_ID_TAG")
    val addressLine1Tag = hasTestTag("ADDRESS_LINE1_TAG")
    val addressLine2Tag = hasTestTag("ADDRESS_LINE2_TAG")
    val addressLine3Tag = hasTestTag("ADDRESS_LINE3_TAG")

    // landing screen
    val patient = hasTestTag("PATIENT")

    // prescription screen
    val previousPrescriptionTab = hasTestTag("PREVIOUS PRESCRIPTION")
    val quickSelectTab = hasTestTag("QUICK SELECT")
    val prescribeBtn = hasTestTag("PRESCRIBE_BTN")
    val previousPrescriptionCards = hasTestTag("PREVIOUS_PRESCRIPTION_CARDS")
    val previousPrescriptionCardsTitleRow = hasTestTag("PREVIOUS_PRESCRIPTION_TITLE_ROW")
    val previousPrescriptionExpandedCards = hasTestTag("PREVIOUS_PRESCRIPTION_EXPANDED_CARD")
    val represcribeBtn = hasTestTag("RE_PRESCRIBE_BTN")

    val bottomNavRow = hasTestTag("BOTTOM_NAV_ROW")
    val bottomNavExpanded = hasTestTag("BOTTOM_NAV_EXPANDED")
    val medicationTitle = hasTestTag("MEDICATION_TITLE")
    val upArrowIcon = hasContentDescription("ARROW_UP")
    val editIcon = hasContentDescription("EDIT_ICON")
    val clearAllBtn = hasTestTag("CLEAR_ALL_BTN")

    // quick selection screen
    val checkBoxes = hasTestTag("ACTIVE_INGREDIENT_CHECK_BOX")

    // fill details
    val formulationsList = hasTestTag("FORMULATION_LIST")
    val duration = hasTestTag("DURATION")
    val doneBtn = hasTestTag("DONE_BTN")

    // patient landing screen
    val appointmentsCard = hasTestTag("APPOINTMENTS") and hasClickAction()
    val householdMemberCard = hasTestTag("HOUSEHOLD_MEMBER") and hasClickAction()
    val prescriptionCard = hasTestTag("PRESCRIPTION") and hasClickAction()

    // profile screen
    val nameLabel = hasText("Name")
    val nameDetail = hasTestTag("NAME")
    val roleLabel = hasText("Role")
    val roleDetails = hasTestTag("ROLE")
    val numberLabel = hasText("Phone No.")
    val numberDetails = hasTestTag("PHONE_NO")
    val emailLabel = hasText("Email")
    val emailDetails = hasTestTag("EMAIL")

    val activeIngredientList = hasTestTag("ACTIVE_INGREDIENT_LIST") and hasScrollAction()

    // quick selection screen
    val dropdownIcon = hasContentDescription("DROP_DOWN_ARROW")

    // fill details
    val activeIngredientField = hasTestTag("ACTIVE_INGREDIENT_FIELD") and hasClickAction()
    val activeIngredientDropdownList = hasTestTag("ACTIVE_INGREDIENT_DROPDOWN_LIST")


    val activeIngredientName = hasTestTag("ACTIVE_INGREDIENT_NAME")

    // fill details form validations
    val qtyPerDoseTextField = hasTestTag("QUANTITY_PER_DOSE")
    val freqTextField = hasTestTag("FREQUENCY")
    val timingField = hasTestTag("TIMING")
    val durationField = hasTestTag("DURATION")
    val qtyPrescribedField = hasTestTag("QUANTITY_PRESCRIBED")
    val notesField = hasTestTag("NOTES")


    val moreIcon = hasContentDescription("more icon")

    // placeholders
    val title = hasTestTag("TITLE")

    // cards
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
    val dialogDismissIcon = hasContentDescription("DIALOG_CLEAR_ICON")
    val dialogRelationDropdown = hasTestTag("RELATIONS_DROPDOWN")
    val dialogRelationList = hasTestTag("RELATION")

    val memberDetailCards = hasTestTag("MEMBER_DETAIL_CARDS")
    val deleteMemberIcon = hasContentDescription("delete member")
    val editMemberIcon = hasContentDescription("edit member")

    // appointments
    val numberOfAppointments = hasTestTag("NUMBER_OF_APPOINTMENTS")
    val addAppointmentFab = hasTestTag("ADD_APPOINTMENT_FAB")
    val addScheduleFab = hasTestTag("ADD_SCHEDULE_FAB")
    val queueFab = hasTestTag("QUEUE_FAB")
    val upcomingTab = hasTestTag("UPCOMING")
    val completedTab = hasTestTag("COMPLETED")
    val upcomingAppointmentCard = hasTestTag("UPCOMING_APPOINTMENT_CARD")
    val appointmentDateTime = hasTestTag("APPOINTMENT_DATE_AND_TIME")
    val appointmentCancelBtn = hasTestTag("APPOINTMENT_CANCEL_BTN")
    val appointmentRescheduleBtn = hasTestTag("APPOINTMENT_RESCHEDULE_BTN")

    // schedule screen
    val resetBtn = hasTestTag("RESET_BTN")
    val dateDropDown = hasTestTag("DATE_DROPDOWN")
    val daysTabRow = hasTestTag("DAYS_TAB_ROW")
    val daysChip = hasTestTag("DAYS_CHIP")
    val morningSlotsHeading = hasTestTag("Morning slots")
    val afternoonSlotsHeading = hasTestTag("Afternoon slots")
    val eveningSlotsHeading = hasTestTag("Evening slots")
    val morningSlotsChip = hasTestTag("MORNING_SLOT_CHIPS")
    val afternoonSlotsChip = hasTestTag("AFTERNOON_SLOT_CHIPS")
    val eveningSlotsChip = hasTestTag("EVENING_SLOT_CHIPS")
    val datePickerDialog = hasTestTag("DATE_PICKER_DIALOG")
    val confirmAppointmentBtn = hasTestTag("CONFIRM_APPOINTMENT_BTN")

    // queue
    val queueSearchLayout = hasTestTag("QUEUE_SEARCH_LAYOUT")
    val queuePatientCard = hasTestTag("QUEUE_PATIENT_CARD")
}