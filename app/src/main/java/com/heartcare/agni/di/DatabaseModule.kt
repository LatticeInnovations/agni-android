package com.heartcare.agni.di

import android.content.Context
import com.heartcare.agni.data.local.roomdb.FhirAppDatabase
import com.heartcare.agni.data.local.roomdb.dao.AppointmentDao
import com.heartcare.agni.data.local.roomdb.dao.CVDDao
import com.heartcare.agni.data.local.roomdb.dao.DispenseDao
import com.heartcare.agni.data.local.roomdb.dao.DownloadedFileDao
import com.heartcare.agni.data.local.roomdb.dao.FileUploadDao
import com.heartcare.agni.data.local.roomdb.dao.GenericDao
import com.heartcare.agni.data.local.roomdb.dao.IdentifierDao
import com.heartcare.agni.data.local.roomdb.dao.LabTestAndMedDao
import com.heartcare.agni.data.local.roomdb.dao.MedicationDao
import com.heartcare.agni.data.local.roomdb.dao.PatientDao
import com.heartcare.agni.data.local.roomdb.dao.PatientLastUpdatedDao
import com.heartcare.agni.data.local.roomdb.dao.PrescriptionDao
import com.heartcare.agni.data.local.roomdb.dao.RelationDao
import com.heartcare.agni.data.local.roomdb.dao.RiskPredictionDao
import com.heartcare.agni.data.local.roomdb.dao.ScheduleDao
import com.heartcare.agni.data.local.roomdb.dao.SearchDao
import com.heartcare.agni.data.local.roomdb.dao.VitalDao
import com.heartcare.agni.data.local.roomdb.dao.SymptomsAndDiagnosisDao
import com.heartcare.agni.data.local.roomdb.dao.vaccincation.ImmunizationDao
import com.heartcare.agni.data.local.roomdb.dao.vaccincation.ImmunizationRecommendationDao
import com.heartcare.agni.data.local.roomdb.dao.vaccincation.ManufacturerDao
import com.heartcare.agni.data.local.sharedpreferences.PreferenceStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        preferenceStorage: PreferenceStorage
    ): FhirAppDatabase {
        return FhirAppDatabase.getInstance(context, preferenceStorage)
    }

    @Singleton
    @Provides
    fun providePatientDao(fhirAppDatabase: FhirAppDatabase): PatientDao {
        return fhirAppDatabase.getPatientDao()
    }

    @Singleton
    @Provides
    fun provideIdentifierDao(fhirAppDatabase: FhirAppDatabase): IdentifierDao {
        return fhirAppDatabase.getIdentifierDao()
    }

    @Singleton
    @Provides
    fun provideGenericDao(fhirAppDatabase: FhirAppDatabase): GenericDao {
        return fhirAppDatabase.getGenericDao()
    }

    @Singleton
    @Provides
    fun provideRelationDao(fhirAppDatabase: FhirAppDatabase): RelationDao {
        return fhirAppDatabase.getRelationDao()
    }

    @Singleton
    @Provides
    fun provideSearchDao(fhirAppDatabase: FhirAppDatabase): SearchDao {
        return fhirAppDatabase.getSearchDao()
    }

    @Singleton
    @Provides
    fun providePrescriptionDao(fhirAppDatabase: FhirAppDatabase): PrescriptionDao {
        return fhirAppDatabase.getPrescriptionDao()
    }

    @Singleton
    @Provides
    fun provideMedicationDao(fhirAppDatabase: FhirAppDatabase): MedicationDao {
        return fhirAppDatabase.getMedicationDao()
    }

    @Singleton
    @Provides
    fun provideScheduleDao(fhirAppDatabase: FhirAppDatabase): ScheduleDao {
        return fhirAppDatabase.getScheduleDao()
    }

    @Singleton
    @Provides
    fun provideAppointmentDao(fhirAppDatabase: FhirAppDatabase): AppointmentDao {
        return fhirAppDatabase.getAppointmentDao()
    }

    @Singleton
    @Provides
    fun providePatientLastUpdatedDao(fhirAppDatabase: FhirAppDatabase): PatientLastUpdatedDao {
        return fhirAppDatabase.getPatientLastUpdatedDao()
    }

    @Singleton
    @Provides
    fun provideFileUploadDao(appDatabase: FhirAppDatabase): FileUploadDao {
        return appDatabase.getFileUploadDao()
    }

    @Singleton
    @Provides
    fun provideDownloadedFileDao(appDatabase: FhirAppDatabase): DownloadedFileDao {
        return appDatabase.getDownloadedFileDao()
    }

    @Singleton
    @Provides
    fun provideRiskPredictionDao(appDatabase: FhirAppDatabase): RiskPredictionDao {
        return appDatabase.getRiskPredictionDao()
    }

    @Singleton
    @Provides
    fun provideCVDDao(appDatabase: FhirAppDatabase): CVDDao {
        return appDatabase.getCVDDao()
    }
    @Singleton
    @Provides
    fun provideVitalDao(fhirAppDatabase: FhirAppDatabase): VitalDao {
        return fhirAppDatabase.getVitalDao()
    }

    @Singleton
    @Provides
    fun provideSymptomsAndDiagnosisDao(fhirAppDatabase: FhirAppDatabase): SymptomsAndDiagnosisDao {
        return fhirAppDatabase.getSymptomsAndDiagnosisDao()
    }
    @Singleton
    @Provides
    fun provideLabTestAndMedDao(fhirAppDatabase: FhirAppDatabase): LabTestAndMedDao {
        return fhirAppDatabase.getLabTestAndMedDao()
    }

    @Singleton
    @Provides
    fun provideDispenseDao(fhirAppDatabase: FhirAppDatabase): DispenseDao {
        return fhirAppDatabase.getDispenseDao()
    }

    @Singleton
    @Provides
    fun provideImmunizationDao(fhirAppDatabase: FhirAppDatabase): ImmunizationDao {
        return fhirAppDatabase.getImmunizationDao()
    }

    @Singleton
    @Provides
    fun provideImmunizationRecommendationDao(fhirAppDatabase: FhirAppDatabase): ImmunizationRecommendationDao {
        return fhirAppDatabase.getImmunizationRecommendationDao()
    }

    @Singleton
    @Provides
    fun provideManufacturerDao(fhirAppDatabase: FhirAppDatabase): ManufacturerDao {
        return fhirAppDatabase.getManufacturerDao()
    }
}