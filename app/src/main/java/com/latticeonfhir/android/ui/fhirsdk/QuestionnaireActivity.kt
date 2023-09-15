package com.latticeonfhir.android.ui.fhirsdk

import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.google.android.fhir.datacapture.allItems
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator
import com.google.android.fhir.sync.Sync
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.R
import com.latticeonfhir.android.base.activity.BaseActivity
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.databinding.ActivityQuestionnaireBinding
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientResourceBirthDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import timber.log.Timber

class QuestionnaireActivity : BaseActivity() {

    private var _binding: ActivityQuestionnaireBinding? = null
    private val binding get() = _binding
    lateinit var fhirEngine: FhirEngine
    var isPatientSaved by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fhirEngine = FhirApp.fhirEngine(application)

        _binding = ActivityQuestionnaireBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        renderQuestionnaire(savedInstanceState, "Patient-Registration.R4.json")
        //renderQuestionnaire(savedInstanceState,"patient_registration.json")
        //renderQuestionnaire(savedInstanceState,"new-patient-registration-paginated.json")
    }

    private fun renderQuestionnaire(savedInstanceState: Bundle?, questionnaire: String) {
        val questionnaireJsonString =
            application.assets.open(questionnaire).bufferedReader().use { it.readText() }
        val bundle = bundleOf(EXTRA_QUESTIONNAIRE_JSON_STRING to questionnaireJsonString)

        val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
        val myQuestionnaire =
            jsonParser.parseResource(questionnaireJsonString) as Questionnaire
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<QuestionnaireFragment>(R.id.fragment_container_view, args = bundle)
            }
        }
        saveButtonListener(myQuestionnaire)
    }

    private fun saveButtonListener(myQuestionnaire: Questionnaire) {
        supportFragmentManager.setFragmentResultListener(
            QuestionnaireFragment.SUBMIT_REQUEST_KEY,
            this
        ) { _, _ ->
            captureData(myQuestionnaire)
        }
    }

    private fun captureData(myQuestionnaire: Questionnaire) {
        val fragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as QuestionnaireFragment
        val questionnaireResponse = fragment.getQuestionnaireResponse()

        val questionnaireResponseString = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
            .encodeResourceToString(questionnaireResponse)
        Timber.d("FHIR response $questionnaireResponseString")
        savePatient(questionnaireResponse, myQuestionnaire)
    }

    private fun savePatient(
        questionnaireResponse: QuestionnaireResponse,
        questionnaire: Questionnaire
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val day = questionnaireResponse.allItems.find { component ->
                component.linkId == "PR-day"
            }?.answer?.get(0)?.valueIntegerType?.value
            val month = questionnaireResponse.allItems.find { component ->
                component.linkId == "PR-month"
            }?.answer?.get(0)?.valueCoding?.display
            val year = questionnaireResponse.allItems.find { component ->
                component.linkId == "PR-year"
            }?.answer?.get(0)?.valueIntegerType?.value
            if (QuestionnaireResponseValidator.validateQuestionnaireResponse(
                    questionnaire,
                    questionnaireResponse,
                    application
                ).values.flatten().any { it is Invalid }
            ) {
                Timber.d("patient invalid")
                isPatientSaved = false
                return@launch
            }

            val entry = ResourceMapper.extract(questionnaire, questionnaireResponse).entryFirstRep
            if (entry.resource !is Patient) {
                Timber.d("patient not")
                return@launch
            }
            val patient = entry.resource as Patient
            patient.id = UUIDBuilder.generateUUID()
            patient.birthDate = toPatientResourceBirthDate(day!!, month!!, year!!)
            val string = fhirEngine.create(patient)
            Timber.d("patient $string")
            isPatientSaved = true
            Sync.oneTimeSync<FhirPeriodicSyncWorker>(applicationContext)
                .shareIn(this, SharingStarted.Eagerly, 0)
                .collect {
                    Timber.d("sync done $it")
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun viewModel() = BaseViewModel()

    companion object {
        private const val EXTRA_QUESTIONNAIRE_JSON_STRING = "questionnaire"
    }
}