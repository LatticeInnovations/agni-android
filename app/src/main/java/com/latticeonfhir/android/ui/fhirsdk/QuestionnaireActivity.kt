package com.latticeonfhir.android.ui.fhirsdk

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.latticeonfhir.android.R
import com.latticeonfhir.android.base.activity.BaseActivity
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.databinding.ActivityQuestionnaireBinding
import timber.log.Timber

class QuestionnaireActivity : BaseActivity() {

    private var _binding: ActivityQuestionnaireBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityQuestionnaireBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        renderQuestionnaire(savedInstanceState,"patient_registration.json")
        //renderQuestionnaire(savedInstanceState,"new-patient-registration-paginated.json")
    }

    private fun renderQuestionnaire(savedInstanceState: Bundle?, questionnaire: String) {
        val questionnaireJsonString = application.assets.open(questionnaire).bufferedReader().use { it.readText() }
        val bundle = bundleOf(EXTRA_QUESTIONNAIRE_JSON_STRING to questionnaireJsonString)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<QuestionnaireFragment>(R.id.fragment_container_view, args = bundle)
            }
        }
        saveButtonListener()
    }

    private fun saveButtonListener() {
        supportFragmentManager.setFragmentResultListener(QuestionnaireFragment.SUBMIT_REQUEST_KEY, this) { _,_ ->
            captureData()
        }
    }

    private fun captureData() {
        val fragment: QuestionnaireFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as QuestionnaireFragment
        val questionnaireResponse = fragment.getQuestionnaireResponse()

        val questionnaireResponseString =   FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().encodeResourceToString(questionnaireResponse)
        Timber.d("FHIR response $questionnaireResponseString")
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