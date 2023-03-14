package com.latticeonfhir.android.di

import android.content.Context
import com.latticeonfhir.android.data.local.roomdb.FhirAppDatabase
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.dao.IdentifierDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
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
    fun provideAppDatabase(@ApplicationContext context: Context, preferenceStorage: PreferenceStorage): FhirAppDatabase {
        return FhirAppDatabase.getInstance(context,preferenceStorage)
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
}