package com.latticeonfhir.core.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.latticeonfhir.core.BuildConfig
import com.latticeonfhir.core.database.dao.AppointmentDao
import com.latticeonfhir.core.database.dao.CVDDao
import com.latticeonfhir.core.database.dao.DispenseDao
import com.latticeonfhir.core.database.dao.DownloadedFileDao
import com.latticeonfhir.core.database.dao.FileUploadDao
import com.latticeonfhir.core.database.dao.GenericDao
import com.latticeonfhir.core.database.dao.IdentifierDao
import com.latticeonfhir.core.database.dao.LabTestAndMedDao
import com.latticeonfhir.core.database.dao.MedicationDao
import com.latticeonfhir.core.database.dao.PatientDao
import com.latticeonfhir.core.database.dao.PatientLastUpdatedDao
import com.latticeonfhir.core.database.dao.PrescriptionDao
import com.latticeonfhir.core.database.dao.RelationDao
import com.latticeonfhir.core.database.dao.RiskPredictionDao
import com.latticeonfhir.core.database.dao.ScheduleDao
import com.latticeonfhir.core.database.dao.SearchDao
import com.latticeonfhir.core.database.dao.VitalDao
import com.latticeonfhir.core.database.dao.SymptomsAndDiagnosisDao
import com.latticeonfhir.core.database.dao.vaccincation.ImmunizationDao
import com.latticeonfhir.core.database.dao.vaccincation.ImmunizationRecommendationDao
import com.latticeonfhir.core.database.dao.vaccincation.ManufacturerDao
import com.latticeonfhir.core.database.entities.appointment.AppointmentEntity
import com.latticeonfhir.core.database.entities.cvd.CVDEntity
import com.latticeonfhir.core.database.entities.cvd.RiskPredictionCharts
import com.latticeonfhir.core.database.entities.dispense.DispenseDataEntity
import com.latticeonfhir.core.database.entities.dispense.DispensePrescriptionEntity
import com.latticeonfhir.core.database.entities.dispense.MedicineDispenseListEntity
import com.latticeonfhir.core.database.entities.file.DownloadedFileEntity
import com.latticeonfhir.core.database.entities.file.FileUploadEntity
import com.latticeonfhir.core.database.entities.generic.GenericEntity
import com.latticeonfhir.core.database.entities.labtestandmedrecord.LabTestAndMedEntity
import com.latticeonfhir.core.database.entities.labtestandmedrecord.photo.LabTestAndMedPhotoEntity
import com.latticeonfhir.core.database.entities.medication.MedicationEntity
import com.latticeonfhir.core.database.entities.medication.MedicineTimingEntity
import com.latticeonfhir.core.database.entities.medication.StrengthEntity
import com.latticeonfhir.core.database.entities.patient.IdentifierEntity
import com.latticeonfhir.core.database.entities.patient.PatientEntity
import com.latticeonfhir.core.database.entities.patient.PatientLastUpdatedEntity
import com.latticeonfhir.core.database.entities.prescription.PrescriptionDirectionsEntity
import com.latticeonfhir.core.database.entities.prescription.PrescriptionEntity
import com.latticeonfhir.core.database.entities.prescription.photo.PrescriptionPhotoEntity
import com.latticeonfhir.core.database.entities.relation.RelationEntity
import com.latticeonfhir.core.database.entities.schedule.ScheduleEntity
import com.latticeonfhir.core.database.entities.search.SearchHistoryEntity
import com.latticeonfhir.core.database.entities.vitals.VitalEntity
import com.latticeonfhir.core.database.entities.search.SymDiagSearchEntity
import com.latticeonfhir.core.database.entities.symptomsanddiagnosis.DiagnosisEntity
import com.latticeonfhir.core.database.entities.symptomsanddiagnosis.SymptomAndDiagnosisEntity
import com.latticeonfhir.core.database.entities.symptomsanddiagnosis.SymptomsEntity
import com.latticeonfhir.core.database.entities.vaccination.ImmunizationEntity
import com.latticeonfhir.core.database.entities.vaccination.ImmunizationFileEntity
import com.latticeonfhir.core.database.entities.vaccination.ImmunizationRecommendationEntity
import com.latticeonfhir.core.database.entities.vaccination.ManufacturerEntity
import com.latticeonfhir.core.database.typeconverters.SymptomDiagnosisTypeConverter
import com.latticeonfhir.core.database.typeconverters.TypeConverter
import com.latticeonfhir.core.database.views.PrescriptionDirectionAndMedicineView
import com.latticeonfhir.core.database.views.RelationView
import com.latticeonfhir.core.data.local.sharedpreferences.PreferenceStorage
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
        SymDiagSearchEntity::class,
        LabTestAndMedEntity::class,
        LabTestAndMedPhotoEntity::class,
        DispensePrescriptionEntity::class,
        DispenseDataEntity::class,
        MedicineDispenseListEntity::class,
        StrengthEntity::class,
        ImmunizationRecommendationEntity::class,
        ImmunizationEntity::class,
        ImmunizationFileEntity::class,
        ManufacturerEntity::class
    ],
    views = [RelationView::class, PrescriptionDirectionAndMedicineView::class],
    version = 14,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9),
        AutoMigration(from = 9, to = 10),
        AutoMigration(from = 10, to = 11),
        AutoMigration(from = 11, to = 12),
        AutoMigration(from = 12, to = 13),
        AutoMigration(from = 13, to = 14)
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
    abstract fun getLabTestAndMedDao(): LabTestAndMedDao
    abstract fun getDispenseDao(): DispenseDao
    abstract fun getManufacturerDao(): ManufacturerDao
    abstract fun getImmunizationDao(): ImmunizationDao
    abstract fun getImmunizationRecommendationDao(): ImmunizationRecommendationDao

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