package com.latticeonfhir.android.di

import android.content.Context
import com.latticeonfhir.android.data.local.roomdb.FhirAppDatabase
import com.latticeonfhir.android.data.local.roomdb.dao.AppointmentDao
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.dao.IdentifierDao
import com.latticeonfhir.android.data.local.roomdb.dao.MedicationDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.dao.PrescriptionDao
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.dao.ScheduleDao
import com.latticeonfhir.android.data.local.roomdb.dao.SearchDao
import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorage
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
}