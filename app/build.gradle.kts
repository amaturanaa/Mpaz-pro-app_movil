plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    jacoco
}

android {
    namespace = "com.example.mpaz_pro_app_movil"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.mpaz_pro_app_movil"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // Mide cuánto código ejecutan las pruebas unitarias (reporte JaCoCo)
            enableUnitTestCoverage = true
        }
        release {
            // Sin optimizar/ofuscar: así Retrofit y Gson funcionan igual
            // que en el emulador (el recorte de clases los rompe).
            // La firma se agrega al generar la APK desde Android Studio.
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        // En las pruebas unitarias, las llamadas a Log.d / Log.e no hacen nada
        // en vez de fallar (esas clases solo existen dentro del teléfono).
        unitTests.isReturnDefaultValues = true
    }
}

jacoco {
    toolVersion = "0.8.12"
}

// =============================================
// REPORTE DE COBERTURA DE LA LÓGICA (IE 3.2.1)
// =============================================
// Ejecutar:  .\gradlew.bat jacocoReporteLogica
// Resultado: app/build/reports/jacoco/jacocoReporteLogica/html/index.html
// Mide solo la lógica de la app (ViewModels, repositorios, validaciones,
// autenticación y modelos). Las pantallas y componentes visuales (Compose)
// no se incluyen porque no son lógica de negocio.
val jacocoReporteLogica by tasks.registering(JacocoReport::class) {
    group = "verification"
    description = "Reporte de cobertura de la lógica (ViewModels y repositorios)."
    dependsOn("testDebugUnitTest")

    val carpetaBuild = layout.buildDirectory.get().asFile

    executionData.setFrom(
        fileTree(carpetaBuild) {
            include("outputs/unit_test_code_coverage/**/*.exec", "jacoco/*.exec")
        }
    )
    classDirectories.setFrom(
        fileTree("$carpetaBuild/intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes") {
            include(
                "com/example/mpaz_pro_app_movil/viewmodel/**",
                "com/example/mpaz_pro_app_movil/red/Repositorio*",
                "com/example/mpaz_pro_app_movil/validacion/**",
                "com/example/mpaz_pro_app_movil/auth/**",
                "com/example/mpaz_pro_app_movil/datos/Calificacion*",
                "com/example/mpaz_pro_app_movil/datos/model/**"
            )
        }
    )
    sourceDirectories.setFrom(files("src/main/java"))

    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.junit)
    testImplementation("io.mockk:mockk:1.13.17")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation("androidx.compose.material:material-icons-core")

    // Conexión con el backend (Retrofit)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Imágenes de la API externa (Coil)
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")
}