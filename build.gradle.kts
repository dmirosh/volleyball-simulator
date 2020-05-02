plugins {
    kotlin("js") version "1.3.72"
    kotlin("plugin.serialization") version "1.3.72"
}

repositories {
    maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
    mavenCentral()
    jcenter()
}

group = "com.dmirosh"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-js"))

    implementation("org.jetbrains:kotlin-react:16.13.1-pre.103-kotlin-1.3.72")
    implementation("org.jetbrains:kotlin-react-dom:16.13.1-pre.103-kotlin-1.3.72")
    implementation("org.jetbrains:kotlin-react:16.13.1-pre.103-kotlin-1.3.72")
    implementation("org.jetbrains:kotlin-react-dom:16.13.1-pre.103-kotlin-1.3.72")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.20.0")

    implementation("org.jetbrains:kotlin-styled:1.0.0-pre.103-kotlin-1.3.72")
}

kotlin {
    target {
        useCommonJs()
        browser {
        }
    }
}