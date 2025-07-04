package com.heartcare.agni.data.local.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.heartcare.agni.BuildConfig
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
import com.heartcare.agni.data.local.roomdb.dao.SymptomsAndDiagnosisDao
import com.heartcare.agni.data.local.roomdb.dao.VitalDao
import com.heartcare.agni.data.local.roomdb.dao.vaccincation.ImmunizationDao
import com.heartcare.agni.data.local.roomdb.dao.vaccincation.ImmunizationRecommendationDao
import com.heartcare.agni.data.local.roomdb.dao.vaccincation.ManufacturerDao
import com.heartcare.agni.data.local.roomdb.entities.appointment.AppointmentEntity
import com.heartcare.agni.data.local.roomdb.entities.cvd.CVDEntity
import com.heartcare.agni.data.local.roomdb.entities.cvd.RiskPredictionCharts
import com.heartcare.agni.data.local.roomdb.entities.dispense.DispenseDataEntity
import com.heartcare.agni.data.local.roomdb.entities.dispense.DispensePrescriptionEntity
import com.heartcare.agni.data.local.roomdb.entities.dispense.MedicineDispenseListEntity
import com.heartcare.agni.data.local.roomdb.entities.file.DownloadedFileEntity
import com.heartcare.agni.data.local.roomdb.entities.file.FileUploadEntity
import com.heartcare.agni.data.local.roomdb.entities.generic.GenericEntity
import com.heartcare.agni.data.local.roomdb.entities.labtestandmedrecord.LabTestAndMedEntity
import com.heartcare.agni.data.local.roomdb.entities.labtestandmedrecord.photo.LabTestAndMedPhotoEntity
import com.heartcare.agni.data.local.roomdb.entities.medication.MedicationEntity
import com.heartcare.agni.data.local.roomdb.entities.medication.MedicineTimingEntity
import com.heartcare.agni.data.local.roomdb.entities.medication.StrengthEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.IdentifierEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientLastUpdatedEntity
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionEntity
import com.heartcare.agni.data.local.roomdb.entities.prescription.photo.PrescriptionPhotoEntity
import com.heartcare.agni.data.local.roomdb.entities.relation.RelationEntity
import com.heartcare.agni.data.local.roomdb.entities.schedule.ScheduleEntity
import com.heartcare.agni.data.local.roomdb.entities.search.SearchHistoryEntity
import com.heartcare.agni.data.local.roomdb.entities.search.SymDiagSearchEntity
import com.heartcare.agni.data.local.roomdb.entities.symptomsanddiagnosis.DiagnosisEntity
import com.heartcare.agni.data.local.roomdb.entities.symptomsanddiagnosis.SymptomAndDiagnosisEntity
import com.heartcare.agni.data.local.roomdb.entities.symptomsanddiagnosis.SymptomsEntity
import com.heartcare.agni.data.local.roomdb.entities.vaccination.ImmunizationEntity
import com.heartcare.agni.data.local.roomdb.entities.vaccination.ImmunizationFileEntity
import com.heartcare.agni.data.local.roomdb.entities.vaccination.ImmunizationRecommendationEntity
import com.heartcare.agni.data.local.roomdb.entities.vaccination.ManufacturerEntity
import com.heartcare.agni.data.local.roomdb.entities.vitals.VitalEntity
import com.heartcare.agni.data.local.roomdb.typeconverters.SymptomDiagnosisTypeConverter
import com.heartcare.agni.data.local.roomdb.typeconverters.TypeConverter
import com.heartcare.agni.data.local.roomdb.views.PrescriptionDirectionAndMedicineView
import com.heartcare.agni.data.local.roomdb.views.RelationView
import com.heartcare.agni.data.local.sharedpreferences.PreferenceStorage
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
    version = 1,
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
                Room.databaseBuilder(context, FhirAppDatabase::class.java, "heartcare_agni")
                    .build()
            } else {
                Room.databaseBuilder(context, FhirAppDatabase::class.java, "heartcare_agni")
                    .openHelperFactory(factory)
                    .build()
            }
        }
    }
}