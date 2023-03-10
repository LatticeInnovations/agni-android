package com.latticeonfhir.android.data.local.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.latticeonfhir.android.data.local.roomdb.dao.IdentifierDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.entities.GenericEntity
import com.latticeonfhir.android.data.local.roomdb.entities.IdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.PatientEntity
import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorage
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.util.UUID

@Database(
    entities = [PatientEntity::class, GenericEntity::class, IdentifierEntity::class],
    version = 1,
    exportSchema = true
)
abstract class FhirAppDatabase : RoomDatabase() {

    abstract fun getPatientDao(): PatientDao
    abstract fun getIdentifierDao(): IdentifierDao

    companion object {
        @Volatile
        private var instance: FhirAppDatabase? = null
        fun getInstance(context: Context, preferenceStorage: PreferenceStorage): FhirAppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context, preferenceStorage).also { instance = it }
            }
        }

        private fun buildDatabase(
            context: Context,
            preferenceStorage: PreferenceStorage
        ): FhirAppDatabase {

            if (preferenceStorage.uuid.isEmpty()) {
                preferenceStorage.uuid = UUID.randomUUID().toString()
            }

            val passphrase: ByteArray = SQLiteDatabase.getBytes(preferenceStorage.uuid.toCharArray())
            val factory = SupportFactory(passphrase)

            return Room.databaseBuilder(context, FhirAppDatabase::class.java, "fhir_android.db")
                .openHelperFactory(factory)
                .build()
        }
    }
}