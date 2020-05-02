plugins {
    java
    application
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.serialization") version "1.3.72"
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}


val processResources by tasks.existing(ProcessResources::class)
val clientDestinationDir = (project(":client").tasks.getByName("browserDevelopmentWebpack")
        as org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack).destinationDirectory

processResources {
    dependsOn(project(":client").getTasksByName("browserDevelopmentWebpack", false))
    from(clientDestinationDir)
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.72")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.72")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
    implementation("io.ktor:ktor-server-netty:1.3.2")
    implementation("io.ktor:ktor-serialization:1.3.2")
    implementation("io.ktor:ktor-server-core:1.3.2")

    implementation("ch.qos.logback:logback-classic:1.2.1")

    implementation("com.h2database:h2:1.4.200")
    implementation("org.jetbrains.exposed:exposed-core:0.23.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.23.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.23.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.23.1")
}
