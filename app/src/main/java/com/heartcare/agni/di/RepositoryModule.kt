package com.heartcare.agni.di

import com.heartcare.agni.data.local.repository.labtest.LabTestRepository
import com.heartcare.agni.data.local.repository.labtest.LabTestRepositoryImpl
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepositoryImpl
import com.heartcare.agni.data.local.repository.cvd.chart.RiskPredictionChartRepository
import com.heartcare.agni.data.local.repository.cvd.chart.RiskPredictionChartRepositoryImpl
import com.heartcare.agni.data.local.repository.cvd.records.CVDAssessmentRepository
import com.heartcare.agni.data.local.repository.cvd.records.CVDAssessmentRepositoryImpl
import com.heartcare.agni.data.local.repository.dispense.DispenseRepository
import com.heartcare.agni.data.local.repository.dispense.DispenseRepositoryImpl
import com.heartcare.agni.data.local.repository.file.DownloadedFileRepository
import com.heartcare.agni.data.local.repository.file.DownloadedFileRepositoryImpl
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepositoryImpl
import com.heartcare.agni.data.local.repository.identifier.IdentifierRepository
import com.heartcare.agni.data.local.repository.identifier.IdentifierRepositoryImpl
import com.heartcare.agni.data.local.repository.levels.LevelRepository
import com.heartcare.agni.data.local.repository.levels.LevelRepositoryImpl
import com.heartcare.agni.data.local.repository.medication.MedicationRepository
import com.heartcare.agni.data.local.repository.medication.MedicationRepositoryImpl
import com.heartcare.agni.data.local.repository.patient.PatientRepository
import com.heartcare.agni.data.local.repository.patient.PatientRepositoryImpl
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepositoryImpl
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepositoryImpl
import com.heartcare.agni.data.local.repository.prescription.PrescriptionRepository
import com.heartcare.agni.data.local.repository.prescription.PrescriptionRepositoryImpl
import com.heartcare.agni.data.local.repository.relation.RelationRepository
import com.heartcare.agni.data.local.repository.relation.RelationRepositoryImpl
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepositoryImpl
import com.heartcare.agni.data.local.repository.search.SearchRepository
import com.heartcare.agni.data.local.repository.search.SearchRepositoryImpl
import com.heartcare.agni.data.local.repository.vital.VitalRepository
import com.heartcare.agni.data.local.repository.vital.VitalRepositoryImpl
import com.heartcare.agni.data.local.repository.symptomsanddiagnosis.SymDiagRepository
import com.heartcare.agni.data.local.repository.symptomsanddiagnosis.SymDiagRepositoryImpl
import com.heartcare.agni.data.local.repository.vaccination.ImmunizationRecommendationRepository
import com.heartcare.agni.data.local.repository.vaccination.ImmunizationRepository
import com.heartcare.agni.data.local.repository.vaccination.ManufacturerRepository
import com.heartcare.agni.data.local.repository.vaccination.impl.ImmunizationRecommendationRepositoryImpl
import com.heartcare.agni.data.local.repository.vaccination.impl.ImmunizationRepositoryImpl
import com.heartcare.agni.data.local.repository.vaccination.impl.ManufacturerRepositoryImpl
import com.heartcare.agni.data.server.repository.authentication.AuthenticationRepository
import com.heartcare.agni.data.server.repository.authentication.AuthenticationRepositoryImpl
import com.heartcare.agni.data.server.repository.file.FileSyncRepository
import com.heartcare.agni.data.server.repository.file.FileSyncRepositoryImpl
import com.heartcare.agni.data.server.repository.signup.SignUpRepository
import com.heartcare.agni.data.server.repository.signup.SignUpRepositoryImpl
import com.heartcare.agni.data.server.repository.symptomsanddiagnosis.SymptomsAndDiagnosisRepository
import com.heartcare.agni.data.server.repository.symptomsanddiagnosis.SymptomsAndDiagnosisRepositoryImpl
import com.heartcare.agni.data.server.repository.sync.SyncRepository
import com.heartcare.agni.data.server.repository.sync.SyncRepositoryImpl
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

    @Binds
    @ViewModelScoped
    abstract fun provideMLevelsRepository(levelRepositoryImpl: LevelRepositoryImpl): LevelRepository
}