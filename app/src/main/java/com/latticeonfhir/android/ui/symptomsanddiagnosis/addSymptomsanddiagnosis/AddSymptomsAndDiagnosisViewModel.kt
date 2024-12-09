package com.latticeonfhir.android.ui.symptomsanddiagnosis.addSymptomsanddiagnosis

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.enums.GenderEnum
import com.latticeonfhir.android.data.local.enums.SearchTypeEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.local.repository.symptomsanddiagnosis.SymDiagRepository
import com.latticeonfhir.android.data.local.roomdb.entities.symptomsanddiagnosis.SymptomsAndDiagnosisLocal
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.symptomsanddiagnosis.SymptomsAndDiagnosisItem
import com.latticeonfhir.android.data.server.model.symptomsanddiagnosis.SymptomsItem
import com.latticeonfhir.android.data.server.repository.symptomsanddiagnosis.SymptomsAndDiagnosisRepository
import com.latticeonfhir.android.utils.common.Queries
import com.latticeonfhir.android.utils.common.Queries.checkAndUpdateAppointmentStatusToInProgress
import com.latticeonfhir.android.utils.common.Queries.updatePatientLastUpdated
import com.latticeonfhir.android.utils.constants.SymptomsAndDiagnosisConstants.CREATED_ON
import com.latticeonfhir.android.utils.constants.SymptomsAndDiagnosisConstants.DIAGNOSIS
import com.latticeonfhir.android.utils.constants.SymptomsAndDiagnosisConstants.SYMPTOMS
import com.latticeonfhir.android.utils.constants.SymptomsAndDiagnosisConstants.SYM_DIAG_FHIR_ID
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.toJson
import com.latticeonfhir.android.utils.converters.responseconverter.SymDiagConverter.splitString
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.convertedDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import com.latticeonfhir.android.utils.converters.responseconverter.toSymDiagData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddSymptomsAndDiagnosisViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val symDiagRepository: SymDiagRepository,
    private val appointmentRepository: AppointmentRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val preferenceRepository: PreferenceRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    private val symptomsAndDiagnosisRepository: SymptomsAndDiagnosisRepository

) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var isSearching by mutableStateOf(false)
    var isSearchResult by mutableStateOf(false)
    var searchQuery by mutableStateOf("")
    var showSelectSymptomScreen by mutableStateOf(false)

    // symptoms
    var selectedSymptom by mutableStateOf<SymptomsAndDiagnosisItem?>(null)
    private var symptoms = MutableStateFlow<List<String>>(listOf())
    var symptomsFlow: StateFlow<List<String>> = symptoms
    private var mostRecentSymptoms = MutableStateFlow<List<String>>(listOf())
    var mostRecentSymptomsFlow: StateFlow<List<String>> = mostRecentSymptoms
    var isNoSymptomChecked by mutableStateOf(false)


    // diagnosis
    private var diagnosis = MutableStateFlow<List<String>>(listOf())
    var diagnosisFlow: StateFlow<List<String>> = diagnosis
    private var mostRecentDiagnosis = MutableStateFlow<List<String>>(listOf())
    var mostRecentDiagnosisFlow: StateFlow<List<String>> = mostRecentDiagnosis
    var selectedDiagnosis by mutableStateOf<SymptomsAndDiagnosisItem?>(null)

    var isSearchForDiagnosis by mutableStateOf(false)
    var selectedActiveSymptomsList = mutableStateListOf<SymptomsAndDiagnosisItem>()
    var selectedActiveDiagnosisList = mutableStateListOf<String>()
    var bottomNavExpanded by mutableStateOf(false)
    var clearAllConfirmDialog by mutableStateOf(false)

    internal var appointmentResponseLocal: AppointmentResponseLocal? = null
    var patient by mutableStateOf<PatientResponse?>(null)
    var local by mutableStateOf<SymptomsAndDiagnosisLocal?>(null)

    var map = mapOf<String, Any>()
    val symDiagUuid: String = UUID.randomUUID().toString()
    var msg by mutableStateOf("")
    var isSearchingInProgress by mutableStateOf(false)

    var state by mutableIntStateOf(0)
    val tabs = mutableListOf("Predefined list", "Manual entry")
    var additionSymptoms by mutableStateOf("")

    private var _bodyPartList = MutableStateFlow<MutableSet<String>>(mutableSetOf())
    var bodyPartList: StateFlow<Set<String>> = _bodyPartList
    private var _bodyParts = MutableStateFlow<MutableSet<SymptomsItem>>(mutableSetOf())
    var bodyParts: StateFlow<Set<SymptomsItem>> = _bodyParts
    var clickedBodyPart by mutableStateOf("")
    var bodyPartBottomNavExpanded by mutableStateOf(false)

    internal suspend fun getStudentTodayAppointment(
        startDate: Date, endDate: Date, patientId: String
    ) {
        appointmentResponseLocal =
            appointmentRepository.getAppointmentListByDate(startDate.time, endDate.time)
                .firstOrNull { appointmentEntity ->
                    appointmentEntity.patientId == patientId && appointmentEntity.status != AppointmentStatusEnum.CANCELLED.value
                }
    }

    internal fun getPreviousSearches() {
        viewModelScope.launch(Dispatchers.IO) {
            if (isSearchForDiagnosis) {
                mostRecentDiagnosis.value =
                    searchRepository.getRecentSymptomAndDiagnosisSearches(searchTypeEnum = SearchTypeEnum.DIAGNOSIS)
                        .toMutableList()
                Timber.d("List: ${mostRecentDiagnosis.value}")
            } else {
                mostRecentSymptoms.value =
                    searchRepository.getRecentSymptomAndDiagnosisSearches(searchTypeEnum = SearchTypeEnum.SYMPTOM)
                        .toMutableList()
                Timber.d("List: ${mostRecentSymptoms.value}")

            }
        }
    }

    internal fun insertRecentSearch() {
        viewModelScope.launch(Dispatchers.IO) {
            if (isSearchForDiagnosis) searchRepository.insertRecentSymptomAndDiagnosisSearch(
                searchQuery = "${selectedDiagnosis?.code}, ${selectedDiagnosis?.display}",
                searchTypeEnum = SearchTypeEnum.DIAGNOSIS,
                size = 5
            )
            else searchRepository.insertRecentSymptomAndDiagnosisSearch(
                searchQuery = selectedSymptom?.display ?: "",
                searchTypeEnum = SearchTypeEnum.SYMPTOM,
                size = 8
            )
        }
    }

    fun searchSymptomsByQuery() {
        viewModelScope.launch(Dispatchers.IO) {
            symptoms.value = searchRepository.searchSymptoms(
                searchQuery.trim(),
                patient?.gender
            ).toSet().toList()
            Timber.d("Symptoms: ${symptoms.value.toJson()}")
            isSearchingInProgress = false
        }
    }

    fun searchDiagnosisByQuery() {
        viewModelScope.launch(Dispatchers.IO) {
            diagnosis.value = searchRepository.searchDiagnosis(
                searchQuery.trim()
            ).also {
                isSearchingInProgress = false
            }
        }
    }

    fun clearSearchDiagnosis() {
        searchQuery = ""
        isSearching = false
        isSearchForDiagnosis = false
        symptoms.value = emptyList()
        isSearchingInProgress = false
        isSearchResult = false
        mostRecentDiagnosis.value = emptyList()
        diagnosis.value = emptyList()
        selectedActiveDiagnosisList = mutableStateListOf()
        selectedDiagnosis = null

    }

    fun clearSymptomsList() {
        symptoms.value = emptyList()
    }
    fun clearDiagnosisList() {
        diagnosis.value = emptyList()
    }

    internal fun insertSymDiag(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO, inserted: (Long) -> Unit
    ) {
        Timber.d("UUID: $symDiagUuid")
        viewModelScope.launch(ioDispatcher) {
            // if there is no appointment, create appointment with walk in status
            if (appointmentResponseLocal == null) {
                Queries.addPatientToQueue(
                    patient!!,
                    scheduleRepository,
                    genericRepository,
                    preferenceRepository,
                    appointmentRepository,
                    patientLastUpdatedRepository
                ) {
                    viewModelScope.launch(ioDispatcher) {
                        getStudentTodayAppointment(
                            Date(Date().toTodayStartDate()), Date(Date().toEndOfDay()), patient!!.id
                        )
                        createSymDiag(
                            symDiagUuid, ioDispatcher, inserted
                        )

                    }
                }
            } else {
                createSymDiag(
                    symDiagUuid, ioDispatcher, inserted
                )
            }
        }
    }

    private suspend fun createSymDiag(
        symDiagUuid: String, ioDispatcher: CoroutineDispatcher, inserted: (Long) -> Unit
    ) {
        val createdOn = Date()
        inserted(withContext(ioDispatcher) {
            insertSymDiagLocal(
                getSymDiagDetails(
                    symDiagUuid = symDiagUuid,
                    fhirId = null,
                    practitionerName = preferenceRepository.getUserName(),
                    createdOn = createdOn,
                    patient!!.id
                )
            ).also {
                insertGenericEntityInDB(
                    getSymDiagDetails(
                        symDiagUuid, null, null, Date(), patient!!.id

                    )
                )
                checkAndUpdateAppointmentStatusToInProgress(
                    inProgressTime = createdOn,
                    patient = patient!!,
                    appointmentResponseLocal = appointmentResponseLocal!!,
                    appointmentRepository = appointmentRepository,
                    scheduleRepository = scheduleRepository,
                    genericRepository = genericRepository
                )
                updatePatientLastUpdated(
                    patient!!.id,
                    patientLastUpdatedRepository,
                    genericRepository
                )
                getStudentTodayAppointment(
                    Date(Date().toTodayStartDate()), Date(Date().toEndOfDay()), patient!!.id
                )
            }
        })
    }

    private fun getSymDiagDetails(
        symDiagUuid: String,
        fhirId: String?,
        practitionerName: String?,
        createdOn: Date,
        patientId: String?

    ): SymptomsAndDiagnosisLocal {
        return SymptomsAndDiagnosisLocal(symDiagUuid = symDiagUuid,
            appointmentId = appointmentResponseLocal?.appointmentId
                ?: appointmentResponseLocal!!.uuid,
            symDiagFhirId = fhirId,
            createdOn = createdOn,
            diagnosis = when {
                local != null && selectedActiveDiagnosisList.isEmpty() -> {
                    listOf()
                }

                (local != null && selectedActiveDiagnosisList.isNotEmpty()) || selectedActiveDiagnosisList.isNotEmpty() -> {
                    selectedActiveDiagnosisList.map {
                        SymptomsAndDiagnosisItem(
                            code = it.splitString().first, display = it.splitString().second
                        )
                    }
                }

                else -> {
                    listOf()
                }
            },
            symptoms = if (isNoSymptomChecked) listOf() else selectedActiveSymptomsList,
            practitionerName = practitionerName,
            patientId = patientId)
    }

    private suspend fun insertSymDiagLocal(
        symptomsAndDiagnosisLocal: SymptomsAndDiagnosisLocal
    ): Long {
        return symDiagRepository.insertSymptomsAndDiagnosis(local = symptomsAndDiagnosisLocal)[0]
    }

    private suspend fun insertGenericEntityInDB(
        local: SymptomsAndDiagnosisLocal
    ): Long {
        return genericRepository.insertSymDiag(
            local.toSymDiagData()
        )
    }

    internal fun updateSymDiag(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO, updated: (Int) -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            // if there is no appointment, create appointment with walk in status
            if (appointmentResponseLocal != null) {
                insertUpdateSymDiag(ioDispatcher, updated)
            }
        }
    }

    private suspend fun insertUpdateSymDiag(
        ioDispatcher: CoroutineDispatcher, updated: (Int) -> Unit
    ) {

        viewModelScope.launch(ioDispatcher) {
            updateSymDiagInDB(
                getSymDiagDetails(
                    symDiagUuid = local!!.symDiagUuid, fhirId = local?.symDiagFhirId,
                    practitionerName = preferenceRepository.getUserName(),
                    createdOn = local!!.createdOn,
                    patient!!.id

                )
            ).also {
                if (it > 0 && local?.symDiagFhirId != null) {
                    checkAndUpdateSymDiag(local)
                } else {
                    genericRepository.insertSymDiag(
                        getSymDiagDetails(
                            symDiagUuid = local!!.symDiagUuid, fhirId = local?.symDiagFhirId,
                            practitionerName = preferenceRepository.getUserName(),
                            createdOn = local!!.createdOn,
                            patient!!.id

                        ).toSymDiagData()
                    )
                }
                updatePatientLastUpdated(
                    patient!!.id,
                    patientLastUpdatedRepository,
                    genericRepository
                )
                updated(it)
            }
        }
    }

    private suspend fun updateSymDiagInDB(
        local: SymptomsAndDiagnosisLocal
    ): Int {
        return symDiagRepository.updateSymDiagData(
            local
        )
    }

    private suspend fun checkAndUpdateSymDiag(
        local: SymptomsAndDiagnosisLocal?
    ) {
        val vitalFhirId = local!!.symDiagFhirId

        if (!local.symptoms.contains(selectedSymptom) || local.diagnosis != selectedActiveDiagnosisList.map {
                SymptomsAndDiagnosisItem(
                    it.splitString().first, it.splitString().second
                )
            }) map = mapOf(SYM_DIAG_FHIR_ID to vitalFhirId!!,
            CREATED_ON to local.createdOn.convertedDate(),
            SYMPTOMS to if (selectedActiveSymptomsList.isNotEmpty()) selectedActiveSymptomsList.map { it.code } else if (isNoSymptomChecked) listOf() else local.symptoms.map { it.code },
            DIAGNOSIS to if (selectedActiveDiagnosisList.isNotEmpty()) selectedActiveDiagnosisList.map { it.splitString().first } else if (local.diagnosis.isNotEmpty() && selectedActiveDiagnosisList.isEmpty()) listOf() else local.diagnosis.map { it.code })
        // Only make the repository call if there are updates
        if (map.isNotEmpty()) {
            genericRepository.insertOrUpdateSymDiagPatchEntity(
                fhirId = vitalFhirId!!, map = map
            )
        }
    }

    fun setLocalData() {
        if (local?.symptoms?.isNotEmpty() == true) {
            selectedActiveSymptomsList.addAll(local!!.symptoms)
        } else {
            local?.let {
                isNoSymptomChecked = true
            }
        }
        if (local?.diagnosis?.isNotEmpty() == true) {
            selectedActiveDiagnosisList.addAll(local?.diagnosis!!.map { "${it.code}, ${it.display}" })
        }
    }

    fun removeSymptom(symptom: SymptomsAndDiagnosisItem) {
        selectedActiveSymptomsList.remove(symptom)
    }

    fun removeSymptoms() {
        selectedActiveSymptomsList.clear()
    }


    internal fun getSymptoms() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = symptomsAndDiagnosisRepository.getSymptoms()
            if (patient?.gender != GenderEnum.OTHER.value) {
                list.filter { it.gender == null || it.gender == patient?.gender }.map {
                    _bodyPartList.value.add(it.type ?: "--")
                    _bodyParts.value.add(it)
                }
            } else {
                list.map {
                    _bodyPartList.value.add(it.type ?: "--")
                    _bodyParts.value.add(it)
                }
            }
        }
    }

}