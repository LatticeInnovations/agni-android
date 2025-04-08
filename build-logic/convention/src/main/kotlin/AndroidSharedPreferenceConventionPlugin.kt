import com.latticeonfhir.configs.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidSharedPreferenceConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {

            dependencies {
                "implementation"(libs.findLibrary("androidx.security.crypto").get())
            }
        }
    }
}