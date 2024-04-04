package com.latticeonfhir.android.di

import android.content.Context
import com.latticeonfhir.android.data.local.roomdb.FhirAppDatabase
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
    fun provideSearchDao(fhirAppDatabase: FhirAppDatabase): SearchDao {
        return fhirAppDatabase.getSearchDao()
    }
}