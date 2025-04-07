package com.latticeonfhir.core.sharedpreference.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.latticeonfhir.core.sharedpreference.preferencestorage.PreferenceStorage
import com.latticeonfhir.core.sharedpreference.preferencestorage.impl.PreferenceStorageImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedPreferencesModule {

    @Provides
    @Singleton
    fun provideSharedPrefs(@ApplicationContext context: Context): SharedPreferences {
        return EncryptedSharedPreferences.create(
            PreferenceStorageImpl.PREFS_NAME,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    fun providePrefsStore(sharedPreferences: SharedPreferences): PreferenceStorage {
        return PreferenceStorageImpl(sharedPreferences)
    }
}