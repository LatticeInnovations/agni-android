package com.latticeonfhir.android.ui.householdmember.suggestions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.logicalId
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.RelationConverter.getInverseRelation
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getPersonResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Person
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.RelatedPerson
import javax.inject.Inject

@HiltViewModel
class SuggestionsScreenViewModel @Inject constructor(
    private val fhirEngine: FhirEngine,
    private val searchRepository: SearchRepository
) : BaseViewModel() {
    var loading by mutableStateOf(true)
    var membersList by mutableStateOf(listOf<Patient>())

    internal fun getQueueItems(patient: Patient) {
        viewModelScope.launch(Dispatchers.IO) {
            membersList = searchRepository.getFiveSuggestedMembersFhir(
                patient.logicalId,
                patient.addressFirstRep
            )
            loading = false
        }
    }

    fun addRelation(
        patientResource: Patient,
        relativeResource: Patient,
        relation: String,
        relationAdded: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val relatedPersonPatient = RelatedPerson().apply {
                id = UUIDBuilder.generateUUID()
                patient.reference = "${patientResource.fhirType()}/${patientResource.logicalId}"
                relationship.add(
                    CodeableConcept(
                        Coding(
                            "http://terminology.hl7.org/CodeSystem/v3-RoleCode",
                            relation,
                            ""
                        )
                    )
                )
            }
            val relatedPersonRelative = RelatedPerson()
            getInverseRelation(
                fromGender = patientResource.gender.toCode(),
                toGender = relativeResource.gender.toCode(),
                relation = RelationEnum.fromString(relation)
            ) { inverseRelation ->
                relatedPersonRelative.apply {
                    id = UUIDBuilder.generateUUID()
                    patient.reference = "${relativeResource.fhirType()}/${relativeResource.logicalId}"
                    relationship.add(
                        CodeableConcept(
                            Coding(
                                "http://terminology.hl7.org/CodeSystem/v3-RoleCode",
                                inverseRelation.value,
                                ""
                            )
                        )
                    )
                }
            }
            val personPatient = getPersonResource(
                fhirEngine,
                patientResource.logicalId
            ).apply {
                link.add(
                    Person.PersonLinkComponent()
                        .setAssurance(Person.IdentityAssuranceLevel.LEVEL3)
                        .setTarget(Reference("${relatedPersonRelative.fhirType()}/${relatedPersonRelative.logicalId}"))
                )
            }
            val personRelative = getPersonResource(
                fhirEngine,
                relativeResource.logicalId
            ).apply {
                link.add(
                    Person.PersonLinkComponent()
                        .setAssurance(Person.IdentityAssuranceLevel.LEVEL3)
                        .setTarget(Reference("${relatedPersonPatient.fhirType()}/${relatedPersonPatient.logicalId}"))
                )
            }
            fhirEngine.create(relatedPersonPatient, relatedPersonRelative)
            fhirEngine.update(personPatient, personRelative)
            relationAdded()
        }
    }
}