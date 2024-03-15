package com.latticeonfhir.android.ui.householdmember.connectpatient

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.logicalId
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.local.model.relation.RelationFhir
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.RelationConverter
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
class ConnectPatientViewModel @Inject constructor(
    private val fhirEngine: FhirEngine
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    var discardAllRelationDialog by mutableStateOf(false)
    var showConfirmDialog by mutableStateOf(false)

    var patientFrom by mutableStateOf(Patient())
    var selectedMembersList = mutableListOf<Patient?>(null)
    var connectedMembersList = mutableStateListOf<RelationFhir>()
    var membersList = mutableStateListOf(*selectedMembersList.toTypedArray())

    internal fun editRelationList(
        relationFhir: RelationFhir,
        relation: String
    ) {
        connectedMembersList.remove(relationFhir)
        RelationConverter.getInverseRelation(
            fromGender = relationFhir.patient.gender.toCode(),
            toGender = relationFhir.relative.gender.toCode(),
            relation = RelationEnum.fromString(RelationConverter.getRelationEnumFromString(relationFhir.relation))
        ) { inverseRelation ->
            connectedMembersList.remove(
                RelationFhir(
                    relationFhir.relative, relationFhir.patient, RelationEnum.fromString(inverseRelation.value).display
                ))
        }
        addToRelationList(
            relationFhir.patient,
            relationFhir.relative,
            relation
        )
    }

    internal fun addToRelationList(
        patient: Patient,
        relative: Patient,
        relation: String
    ) {
        connectedMembersList.add(
            RelationFhir(
                patient, relative, relation
            )
        )
        RelationConverter.getInverseRelation(
            fromGender = patient.gender.toCode(),
            toGender = relative.gender.toCode(),
            relation = RelationEnum.fromString(RelationConverter.getRelationEnumFromString(relation))
        ) { inverseRelation ->
            connectedMembersList.add(
                RelationFhir(
                relative, patient, RelationEnum.fromString(inverseRelation.value).display
            ))
        }
    }

    internal fun addRelations(added: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            connectedMembersList.forEach { relationFhir ->
                val relatedPersonRelative = RelatedPerson().apply {
                    id = UUIDBuilder.generateUUID()
                    patient.reference = "${relationFhir.relative.fhirType()}/${relationFhir.relative.logicalId}"
                    relationship.add(
                        CodeableConcept(
                            Coding(
                                "http://terminology.hl7.org/CodeSystem/v3-RoleCode",
                                RelationEnum.fromDisplay(relationFhir.relation).value,
                                ""
                            )
                        )
                    )
                }
                val personPatient = getPersonResource(
                    fhirEngine,
                    relationFhir.patient.logicalId
                ).apply {
                    link.add(
                        Person.PersonLinkComponent()
                            .setAssurance(Person.IdentityAssuranceLevel.LEVEL3)
                            .setTarget(Reference("${relatedPersonRelative.fhirType()}/${relatedPersonRelative.logicalId}"))
                    )
                }
                fhirEngine.create(relatedPersonRelative)
                fhirEngine.update(personPatient)
            }
            added()
        }
    }
}