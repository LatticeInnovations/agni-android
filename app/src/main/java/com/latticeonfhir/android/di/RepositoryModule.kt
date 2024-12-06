package com.latticeonfhir.android.di

import com.latticeonfhir.android.data.local.repository.labtest.LabTestRepository
import com.latticeonfhir.android.data.local.repository.labtest.LabTestRepositoryImpl
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepositoryImpl
import com.latticeonfhir.android.data.local.repository.cvd.chart.RiskPredictionChartRepository
import com.latticeonfhir.android.data.local.repository.cvd.chart.RiskPredictionChartRepositoryImpl
import com.latticeonfhir.android.data.local.repository.cvd.records.CVDAssessmentRepository
import com.latticeonfhir.android.data.local.repository.cvd.records.CVDAssessmentRepositoryImpl
import com.latticeonfhir.android.data.local.repository.dispense.DispenseRepository
import com.latticeonfhir.android.data.local.repository.dispense.DispenseRepositoryImpl
import com.latticeonfhir.android.data.local.repository.file.DownloadedFileRepository
import com.latticeonfhir.android.data.local.repository.file.DownloadedFileRepositoryImpl
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepositoryImpl
import com.latticeonfhir.android.data.local.repository.identifier.IdentifierRepository
import com.latticeonfhir.android.data.local.repository.identifier.IdentifierRepositoryImpl
import com.latticeonfhir.android.data.local.repository.medication.MedicationRepository
import com.latticeonfhir.android.data.local.repository.medication.MedicationRepositoryImpl
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepositoryImpl
import com.latticeonfhir.android.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.android.data.local.repository.patient.lastupdated.PatientLastUpdatedRepositoryImpl
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepositoryImpl
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepository
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepositoryImpl
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepositoryImpl
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepositoryImpl
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.local.repository.search.SearchRepositoryImpl
import com.latticeonfhir.android.data.local.repository.vital.VitalRepository
import com.latticeonfhir.android.data.local.repository.vital.VitalRepositoryImpl
import com.latticeonfhir.android.data.local.repository.symptomsanddiagnosis.SymDiagRepository
import com.latticeonfhir.android.data.local.repository.symptomsanddiagnosis.SymDiagRepositoryImpl
import com.latticeonfhir.android.data.server.repository.authentication.AuthenticationRepository
import com.latticeonfhir.android.data.server.repository.authentication.AuthenticationRepositoryImpl
import com.latticeonfhir.android.data.server.repository.file.FileSyncRepository
import com.latticeonfhir.android.data.server.repository.file.FileSyncRepositoryImpl
import com.latticeonfhir.android.data.server.repository.signup.SignUpRepository
import com.latticeonfhir.android.data.server.repository.signup.SignUpRepositoryImpl
import com.latticeonfhir.android.data.server.repository.symptomsanddiagnosis.SymptomsAndDiagnosisRepository
import com.latticeonfhir.android.data.server.repository.symptomsanddiagnosis.SymptomsAndDiagnosisRepositoryImpl
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import com.latticeonfhir.android.data.server.repository.sync.SyncRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun provideSyncRepository(syncRepositoryImpl: SyncRepositoryImpl): SyncRepository

    @Binds
    @ViewModelScoped
    abstract fun providePatientRepository(patientRepositoryImpl: PatientRepositoryImpl): PatientRepository

    @Binds
    @ViewModelScoped
    abstract fun provideGenericRepository(genericRepositoryImpl: GenericRepositoryImpl): GenericRepository

    @Binds
    @ViewModelScoped
    abstract fun provideRelationRepository(relationRepositoryImpl: RelationRepositoryImpl): RelationRepository

    @Binds
    @ViewModelScoped
    abstract fun provideIdentifierRepository(identifierRepositoryImpl: IdentifierRepositoryImpl): IdentifierRepository

    @Binds
    @ViewModelScoped
    abstract fun provideSearchRepository(searchRepositoryImpl: SearchRepositoryImpl): SearchRepository

    @Binds
    @ViewModelScoped
    abstract fun providePreferenceRepository(preferenceRepositoryImpl: PreferenceRepositoryImpl): PreferenceRepository

    @Binds
    @ViewModelScoped
    abstract fun provideAuthenticationRepository(authenticationRepositoryImpl: AuthenticationRepositoryImpl): AuthenticationRepository

    @Binds
    @ViewModelScoped
    abstract fun providePrescriptionRepository(prescriptionRepositoryImpl: PrescriptionRepositoryImpl): PrescriptionRepository

    @Binds
    @ViewModelScoped
    abstract fun provideMedicationRepository(medicationRepositoryImpl: MedicationRepositoryImpl): MedicationRepository

    @Binds
    @ViewModelScoped
    abstract fun provideScheduleRepository(scheduleRepositoryImpl: ScheduleRepositoryImpl): ScheduleRepository

    @Binds
    @ViewModelScoped
    abstract fun provideAppointmentRepository(appointmentRepositoryImpl: AppointmentRepositoryImpl): AppointmentRepository

    @Binds
    @ViewModelScoped
    abstract fun providePatientLastUpdatedRepository(patientLastUpdatedRepositoryImpl: PatientLastUpdatedRepositoryImpl): PatientLastUpdatedRepository

    @Binds
    @ViewModelScoped
    abstract fun provideFileSyncRepository(fileUploadRepositoryImpl: FileSyncRepositoryImpl): FileSyncRepository

    @Binds
    @ViewModelScoped
    abstract fun provideDownloadedFileRepository(downloadedFileRepositoryImpl: DownloadedFileRepositoryImpl): DownloadedFileRepository

    @Binds
    @ViewModelScoped
    abstract fun provideSignUpRepository(signUpRepositoryImpl: SignUpRepositoryImpl): SignUpRepository

    @Binds
    @ViewModelScoped
    abstract fun provideRiskPredictionRepository(riskPredictionChartRepositoryImpl: RiskPredictionChartRepositoryImpl): RiskPredictionChartRepository

    @Binds
    @ViewModelScoped
    abstract fun provideCVDAssessmentRepository(cvdAssessmentRepositoryImpl: CVDAssessmentRepositoryImpl): CVDAssessmentRepository

    @Binds
    @ViewModelScoped
    abstract fun provideVitalRepository(vitalRepositoryImpl: VitalRepositoryImpl): VitalRepository

    @Binds
    @ViewModelScoped
    abstract fun provideSymptomsAndDiagnosisRepository(symptomsAndDiagnosisRepositoryImpl: SymptomsAndDiagnosisRepositoryImpl): SymptomsAndDiagnosisRepository

    @Binds
    @ViewModelScoped
    abstract fun provideSymDiagRepository(symDiagRepositoryImpl: SymDiagRepositoryImpl): SymDiagRepository

    @Binds
    @ViewModelScoped
    abstract fun provideLabTestRepository(labTestRepositoryImpl: LabTestRepositoryImpl): LabTestRepository

    @Binds
    @ViewModelScoped
    abstract fun provideDispenseRepository(dispenseRepositoryImpl: DispenseRepositoryImpl): DispenseRepository
}