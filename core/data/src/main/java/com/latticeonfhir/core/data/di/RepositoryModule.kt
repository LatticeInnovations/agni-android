package com.latticeonfhir.core.data.di

import com.latticeonfhir.core.data.repository.server.symptomsanddiagnosis.SymptomsAndDiagnosisRepositoryImpl
import com.latticeonfhir.core.data.repository.local.appointment.AppointmentRepository
import com.latticeonfhir.core.data.repository.local.appointment.AppointmentRepositoryImpl
import com.latticeonfhir.core.data.repository.local.cvd.chart.RiskPredictionChartRepository
import com.latticeonfhir.core.data.repository.local.cvd.chart.RiskPredictionChartRepositoryImpl
import com.latticeonfhir.core.data.repository.local.cvd.records.CVDAssessmentRepository
import com.latticeonfhir.core.data.repository.local.cvd.records.CVDAssessmentRepositoryImpl
import com.latticeonfhir.core.data.repository.local.dispense.DispenseRepository
import com.latticeonfhir.core.data.repository.local.dispense.DispenseRepositoryImpl
import com.latticeonfhir.core.data.repository.local.file.DownloadedFileRepository
import com.latticeonfhir.core.data.repository.local.file.DownloadedFileRepositoryImpl
import com.latticeonfhir.core.data.repository.local.generic.GenericRepository
import com.latticeonfhir.core.data.repository.local.generic.GenericRepositoryImpl
import com.latticeonfhir.core.data.repository.local.identifier.IdentifierRepository
import com.latticeonfhir.core.data.repository.local.identifier.IdentifierRepositoryImpl
import com.latticeonfhir.core.data.repository.local.labtest.LabTestRepository
import com.latticeonfhir.core.data.repository.local.labtest.LabTestRepositoryImpl
import com.latticeonfhir.core.data.repository.local.medication.MedicationRepository
import com.latticeonfhir.core.data.repository.local.medication.MedicationRepositoryImpl
import com.latticeonfhir.core.data.repository.local.patient.PatientRepository
import com.latticeonfhir.core.data.repository.local.patient.PatientRepositoryImpl
import com.latticeonfhir.core.data.repository.local.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.core.data.repository.local.patient.lastupdated.PatientLastUpdatedRepositoryImpl
import com.latticeonfhir.core.data.repository.local.preference.PreferenceRepository
import com.latticeonfhir.core.data.repository.local.preference.PreferenceRepositoryImpl
import com.latticeonfhir.core.data.repository.local.prescription.PrescriptionRepository
import com.latticeonfhir.core.data.repository.local.prescription.PrescriptionRepositoryImpl
import com.latticeonfhir.core.data.repository.local.relation.RelationRepository
import com.latticeonfhir.core.data.repository.local.relation.RelationRepositoryImpl
import com.latticeonfhir.core.data.repository.local.schedule.ScheduleRepository
import com.latticeonfhir.core.data.repository.local.schedule.ScheduleRepositoryImpl
import com.latticeonfhir.core.data.repository.local.search.SearchRepository
import com.latticeonfhir.core.data.repository.local.search.SearchRepositoryImpl
import com.latticeonfhir.core.data.repository.local.symptomsanddiagnosis.SymDiagRepository
import com.latticeonfhir.core.data.repository.local.symptomsanddiagnosis.SymDiagRepositoryImpl
import com.latticeonfhir.core.data.repository.local.vaccination.ImmunizationRecommendationRepository
import com.latticeonfhir.core.data.repository.local.vaccination.ImmunizationRepository
import com.latticeonfhir.core.data.repository.local.vaccination.ManufacturerRepository
import com.latticeonfhir.core.data.repository.local.vaccination.impl.ImmunizationRecommendationRepositoryImpl
import com.latticeonfhir.core.data.repository.local.vaccination.impl.ImmunizationRepositoryImpl
import com.latticeonfhir.core.data.repository.local.vaccination.impl.ManufacturerRepositoryImpl
import com.latticeonfhir.core.data.repository.local.vital.VitalRepository
import com.latticeonfhir.core.data.repository.local.vital.VitalRepositoryImpl
import com.latticeonfhir.core.data.repository.server.authentication.AuthenticationRepository
import com.latticeonfhir.core.data.repository.server.authentication.AuthenticationRepositoryImpl
import com.latticeonfhir.core.data.repository.server.file.FileSyncRepositoryImpl
import com.latticeonfhir.core.data.repository.server.signup.SignUpRepository
import com.latticeonfhir.core.data.repository.server.signup.SignUpRepositoryImpl
import com.latticeonfhir.core.data.repository.server.sync.SyncRepository
import com.latticeonfhir.core.data.repository.server.sync.SyncRepositoryImpl
import com.latticeonfhir.core.data.repository.server.file.FileSyncRepository
import com.latticeonfhir.core.data.server.repository.symptomsanddiagnosis.SymptomsAndDiagnosisRepository
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

    @Binds
    @ViewModelScoped
    abstract fun provideImmunizationRepository(immunizationRepositoryImpl: ImmunizationRepositoryImpl): ImmunizationRepository

    @Binds
    @ViewModelScoped
    abstract fun provideImmunizationRecommendationRepository(immunizationRecommendationRepositoryImpl: ImmunizationRecommendationRepositoryImpl): ImmunizationRecommendationRepository

    @Binds
    @ViewModelScoped
    abstract fun provideManufacturerRepository(manufacturerRepositoryImpl: ManufacturerRepositoryImpl): ManufacturerRepository
}