package com.latticeonfhir.android.data.local.roomdb

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.latticeonfhir.android.BuildConfig
import com.latticeonfhir.android.data.local.roomdb.dao.SearchDao
import com.latticeonfhir.android.data.local.roomdb.entities.search.SearchHistoryEntity
import com.latticeonfhir.android.data.local.roomdb.typeconverters.TypeConverter
import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorage
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.util.UUID

@Database(
    entities = [SearchHistoryEntity::class],
    version = 4,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4, spec = FhirAppDatabase.Migration3to4::class)
    ],
    exportSchema = true
)
@TypeConverters(TypeConverter::class)
abstract class FhirAppDatabase : RoomDatabase() {
    @DeleteTable.Entries(
        value = [
            DeleteTable(
                tableName = "PatientEntity"
            ),
            DeleteTable(
                tableName = "IdentifierEntity"
            ),
            DeleteTable(
                tableName = "PatientAndIdentifierEntity"
            ),
            DeleteTable(
                tableName = "PermanentAddressEntity"
            ),
            DeleteTable(
                tableName = "AppointmentEntity"
            ),
            DeleteTable(
                tableName = "GenericEntity"
            ),
            DeleteTable(
                tableName = "MedicationEntity"
            ),
            DeleteTable(
                tableName = "PrescriptionDirectionsEntity"
            ),
            DeleteTable(
                tableName = "PrescriptionEntity"
            ),
            DeleteTable(
                tableName = "RelationEntity"
            ),
            DeleteTable(
                tableName = "ScheduleEntity"
            ),
            DeleteTable(
                tableName = "MedicineTimingEntity"
            )
        ]
    )
    class Migration3to4 : AutoMigrationSpec

    abstract fun getSearchDao(): SearchDao

    companion object {
        @Volatile
        private var instance: FhirAppDatabase? = null
        fun getInstance(context: Context, preferenceStorage: PreferenceStorage): FhirAppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context, preferenceStorage).also { instance = it }
            }
        }

        private fun buildDatabase(
            context: Context, preferenceStorage: PreferenceStorage
        ): FhirAppDatabase {

            if (preferenceStorage.roomDBEncryptionKey.isBlank()) {
                preferenceStorage.roomDBEncryptionKey = UUID.randomUUID().toString()
            }

            val passphrase: ByteArray =
                SQLiteDatabase.getBytes(preferenceStorage.roomDBEncryptionKey.toCharArray())
            val factory = SupportFactory(passphrase)

            return if (BuildConfig.DEBUG) {
                Room.databaseBuilder(context, FhirAppDatabase::class.java, "fhir_android")
                    .build()
            } else {
                Room.databaseBuilder(context, FhirAppDatabase::class.java, "fhir_android")
                    .openHelperFactory(factory)
                    .build()
            }
        }
    }
}