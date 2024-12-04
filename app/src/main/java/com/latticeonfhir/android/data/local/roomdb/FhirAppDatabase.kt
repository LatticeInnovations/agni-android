package com.latticeonfhir.android.data.local.roomdb

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.latticeonfhir.android.BuildConfig
import com.latticeonfhir.android.data.local.roomdb.dao.AppointmentDao
import com.latticeonfhir.android.data.local.roomdb.dao.CVDDao
import com.latticeonfhir.android.data.local.roomdb.dao.DownloadedFileDao
import com.latticeonfhir.android.data.local.roomdb.dao.FileUploadDao
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.dao.IdentifierDao
import com.latticeonfhir.android.data.local.roomdb.dao.MedicationDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.local.roomdb.dao.PatientLastUpdatedDao
import com.latticeonfhir.android.data.local.roomdb.dao.PrescriptionDao
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.data.local.roomdb.dao.RiskPredictionDao
import com.latticeonfhir.android.data.local.roomdb.dao.ScheduleDao
import com.latticeonfhir.android.data.local.roomdb.dao.SearchDao
import com.latticeonfhir.android.data.local.roomdb.dao.VitalDao
import com.latticeonfhir.android.data.local.roomdb.dao.SymptomsAndDiagnosisDao
import com.latticeonfhir.android.data.local.roomdb.entities.appointment.AppointmentEntity
import com.latticeonfhir.android.data.local.roomdb.entities.cvd.CVDEntity
import com.latticeonfhir.android.data.local.roomdb.entities.cvd.RiskPredictionCharts
import com.latticeonfhir.android.data.local.roomdb.entities.file.DownloadedFileEntity
import com.latticeonfhir.android.data.local.roomdb.entities.file.FileUploadEntity
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationEntity
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicineTimingEntity
import com.latticeonfhir.android.data.local.roomdb.entities.patient.IdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientEntity
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientLastUpdatedEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.photo.PrescriptionPhotoEntity
import com.latticeonfhir.android.data.local.roomdb.entities.relation.RelationEntity
import com.latticeonfhir.android.data.local.roomdb.entities.schedule.ScheduleEntity
import com.latticeonfhir.android.data.local.roomdb.entities.search.SearchHistoryEntity
import com.latticeonfhir.android.data.local.roomdb.entities.vitals.VitalEntity
import com.latticeonfhir.android.data.local.roomdb.entities.search.SymDiagSearchEntity
import com.latticeonfhir.android.data.local.roomdb.entities.symptomsanddiagnosis.DiagnosisEntity
import com.latticeonfhir.android.data.local.roomdb.entities.symptomsanddiagnosis.SymptomAndDiagnosisEntity
import com.latticeonfhir.android.data.local.roomdb.entities.symptomsanddiagnosis.SymptomsEntity
import com.latticeonfhir.android.data.local.roomdb.typeconverters.SymptomDiagnosisTypeConverter
import com.latticeonfhir.android.data.local.roomdb.typeconverters.TypeConverter
import com.latticeonfhir.android.data.local.roomdb.views.PrescriptionDirectionAndMedicineView
import com.latticeonfhir.android.data.local.roomdb.views.RelationView
import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorage
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.util.UUID

@Database(
    entities = [
        PatientEntity::class,
        GenericEntity::class,
        IdentifierEntity::class,
        RelationEntity::class,
        SearchHistoryEntity::class,
        MedicationEntity::class,
        PrescriptionEntity::class,
        PrescriptionDirectionsEntity::class,
        MedicineTimingEntity::class,
        ScheduleEntity::class,
        AppointmentEntity::class,
        PatientLastUpdatedEntity::class,
        PrescriptionPhotoEntity::class,
        FileUploadEntity::class,
        DownloadedFileEntity::class,
        RiskPredictionCharts::class,
        CVDEntity::class,
        VitalEntity::class,
        SymptomsEntity::class,
        DiagnosisEntity::class,
        SymptomAndDiagnosisEntity::class,
        SymDiagSearchEntity::class

    ],
    views = [RelationView::class, PrescriptionDirectionAndMedicineView::class],
    version = 10,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9),
        AutoMigration(from = 9, to = 10)
    ],
    exportSchema = true
)
@TypeConverters(TypeConverter::class, SymptomDiagnosisTypeConverter::class)
abstract class FhirAppDatabase : RoomDatabase() {

    abstract fun getPatientDao(): PatientDao
    abstract fun getIdentifierDao(): IdentifierDao
    abstract fun getGenericDao(): GenericDao
    abstract fun getRelationDao(): RelationDao
    abstract fun getSearchDao(): SearchDao
    abstract fun getPrescriptionDao(): PrescriptionDao
    abstract fun getMedicationDao(): MedicationDao
    abstract fun getScheduleDao(): ScheduleDao
    abstract fun getAppointmentDao(): AppointmentDao
    abstract fun getPatientLastUpdatedDao(): PatientLastUpdatedDao
    abstract fun getFileUploadDao(): FileUploadDao
    abstract fun getDownloadedFileDao(): DownloadedFileDao
    abstract fun getRiskPredictionDao(): RiskPredictionDao
    abstract fun getCVDDao(): CVDDao
    abstract fun getVitalDao(): VitalDao

    abstract fun getSymptomsAndDiagnosisDao(): SymptomsAndDiagnosisDao


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