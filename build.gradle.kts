// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kotlin) apply false
    alias(libs.plugins.devtools.ksp) apply false
    alias(libs.plugins.dagger.hilt.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.kapt) apply false
}

//Workaround for "Expecting an expression" build error
println("")