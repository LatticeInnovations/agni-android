package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.model.relation.Relation
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.identifier.IdentifierRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.patientregistration.preview.PatientRegistrationPreviewViewModel
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientDate
import com.latticeonfhir.android.utils.converters.responseconverter.RelationConverter
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class PatientRegistrationPreviewViewModelTest {
    @Mock
    lateinit var patientRepository: PatientRepository
    @Mock
    lateinit var relationRepository: RelationRepository
    @Mock
    lateinit var genericRepository: GenericRepository
    @Mock
    lateinit var identifierRepository: IdentifierRepository
    lateinit var viewModel: PatientRegistrationPreviewViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = PatientRegistrationPreviewViewModel(patientRepository, genericRepository, identifierRepository, relationRepository)
    }

    @Test
    fun addPatientTest() = runBlocking{
        viewModel.firstName = "mansi"
        viewModel.phoneNumber = "9876543210"
        viewModel.dobDay = "23"
        viewModel.dobMonth = "January"
        viewModel.dobYear = "2001"
        viewModel.dob = "${viewModel.dobDay}-${viewModel.dobMonth}-${viewModel.dobYear}"
        viewModel.gender = "female"
        viewModel.patientId = "abcde12345"
        viewModel.homeAddress.pincode = "999999"
        viewModel.homeAddress.state = "Uttarakhand"
        viewModel.homeAddress.addressLine1 = "address line 1"
        viewModel.homeAddress.city = "city"
        val formatter = DateTimeFormatter.ofPattern("d-MMMM-yyyy", Locale.getDefault())
        val date = LocalDate.parse(viewModel.dob, formatter)
        if (viewModel.passportId.isNotEmpty()) {
            viewModel.identifierList.add(
                PatientIdentifier(
                    identifierType = "https://www.passportindia.gov.in/",
                    identifierNumber = viewModel.passportId,
                    code = null
                )
            )
        }
        if (viewModel.voterId.isNotEmpty()) {
            viewModel.identifierList.add(
                PatientIdentifier(
                    identifierType = "https://www.nvsp.in/",
                    identifierNumber = viewModel.voterId,
                    code = null
                )
            )
        }
        if (viewModel.patientId.isNotEmpty()) {
            viewModel.identifierList.add(
                PatientIdentifier(
                    identifierType = "https://www.apollohospitals.com/",
                    identifierNumber = viewModel.patientId,
                    code = null
                )
            )
        }
        val patientResponse = PatientResponse(
            id = viewModel.relativeId,
            firstName = viewModel.firstName,
            middleName = if (viewModel.middleName.isEmpty()) null else viewModel.middleName,
            lastName = if (viewModel.lastName.isEmpty()) null else viewModel.lastName,
            birthDate = Date.from(
                date!!.atStartOfDay(ZoneId.systemDefault()).toInstant()
            ).time.toPatientDate(),
            email = if (viewModel.email.isEmpty()) null else viewModel.email,
            active = true,
            gender = viewModel.gender,
            mobileNumber = viewModel.phoneNumber.toLong(),
            fhirId = null,
            permanentAddress = PatientAddressResponse(
                postalCode = viewModel.homeAddress.pincode,
                state = viewModel.homeAddress.state,
                addressLine1 = viewModel.homeAddress.addressLine1,
                addressLine2 = if (viewModel.homeAddress.addressLine2.isEmpty()) null else viewModel.homeAddress.addressLine2,
                city = viewModel.homeAddress.city,
                country = "India",
                district = if (viewModel.homeAddress.district.isEmpty()) null else viewModel.homeAddress.district
            ),
            identifier = viewModel.identifierList
        )

        `when`(patientRepository.addPatient(patientResponse)).thenReturn(listOf(-1))
        `when`(genericRepository.insertOrUpdatePostEntity(
            patientId = patientResponse.id,
            entity = patientResponse,
            typeEnum = GenericTypeEnum.PATIENT
        )).thenReturn(-1)
        `when`(identifierRepository.insertIdentifierList(patientResponse)).thenReturn(Unit)

        val result = viewModel.addPatient(patientResponse)

        Assert.assertEquals(Unit, result)
    }


    @Test
    fun addRelationTest() = runBlocking{
        val relation = Relation(
                patientId = viewModel.patientFromId,
                relativeId = viewModel.relativeId,
                relation = RelationConverter.getRelationEnumFromString(viewModel.relation)
            )
        var actual = listOf<Long>()
        `when`(relationRepository.addRelation(relation){
            actual = listOf(-1)
        }).thenReturn(Unit)
        viewModel.addRelation(relation) {
            Assert.assertEquals(listOf<Long>(-1), actual)
        }
    }
}