package com.latticeonfhir.android.ui.fhirsdk

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import com.google.android.fhir.get
import com.google.android.fhir.logicalId
import com.google.android.fhir.sync.Sync
import com.google.android.fhir.sync.SyncJobStatus
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.R
import com.latticeonfhir.android.base.activity.BaseActivity
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.databinding.ActivityQuestionnaireBinding
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.fromJson
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.toJson
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Questionnaire
import timber.log.Timber

class QuestionnaireActivity : BaseActivity() {

    private var _binding: ActivityQuestionnaireBinding? = null
    private val binding get() = _binding
    lateinit var fhirEngine: FhirEngine

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
        val fragment: QuestionnaireFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as QuestionnaireFragment
        val questionnaireResponse = fragment.getQuestionnaireResponse()
        val quuid = UUIDBuilder.generateUUID()
        //questionnaireResponse.id = quuid

        val questionnaireResponseString = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
            .encodeResourceToString(questionnaireResponse)
        Timber.d("FHIR response $questionnaireResponseString")
//        CoroutineScope(Dispatchers.IO).launch {
//            val bundle = ResourceMapper.extract(myQuestionnaire, questionnaireResponse)
//            Timber.d(("bundle typeElement ${bundle.typeElement}"))
//            Timber.d(("bundle entry ${bundle.entry}"))
//            Timber.d(("bundle entryFirstRep ${bundle.entryFirstRep}"))
//            Timber.d(("bundle identifier ${bundle.identifier}"))
//            Timber.d(("bundle link ${bundle.link}"))
//            Timber.d(("bundle signature ${bundle.signature}"))
//            Timber.d(("bundle resourceType ${bundle.resourceType}"))
//            Timber.d(("bundle identifier type ${bundle.identifier.type}"))
//            Timber.d(("bundle identifier valueElement ${bundle.identifier.valueElement}"))
//            Timber.d(("bundle identifier value ${bundle.identifier.value}"))
//            Timber.d(("bundle entry size ${bundle.entry.size}"))
//            Timber.d(("bundle .typeElement.toSystem ${bundle.typeElement.toSystem()}"))
//            Timber.d(("bundle link.sized ${bundle.link.size}"))
//            Timber.d(("bundle totalElement ${bundle.totalElement}"))
//            Timber.d(("bundle logicalId ${bundle.logicalId}"))
//            Timber.d(("bundle idElement ${bundle.idElement}"))
//            Timber.d(("bundle idElement value ${bundle.idElement.value}"))
//            Timber.d(("bundle json ${bundle.toJson()}"))
////            val patient = bundle.toJson().fromJson<Patient>()
////            Timber.d(("patient bundle $patient"))
//        }
        val uuid = UUIDBuilder.generateUUID()
        val patient = Patient().apply {
            id = uuid
            gender = Enumerations.AdministrativeGender.MALE
            addName(
                HumanName().apply {
                    addGiven("Hello")
                    addGiven("Thomas")
                    family = "West"
                }
            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            val bundle = ResourceMapper.extract(myQuestionnaire, questionnaireResponse)
            bundle.id = quuid
            questionnaireResponse.id = quuid
            //val string = fhirEngine.create(questionnaireResponse)
            //Timber.d("patient created $quuid $string")
            Timber.d("patient ${questionnaireResponse.resourceType}")

//            Sync.oneTimeSync<FhirPeriodicSyncWorker>(applicationContext)
//                .shareIn(this, SharingStarted.Eagerly, 0)
//                .collect {
//                    Timber.d("sync done $it")
//                }
        }
        //Timber.d("patient $patient")
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