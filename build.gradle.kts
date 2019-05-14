plugins {
    base
    val kotlinVersion = "1.3.31"
    kotlin("jvm") version kotlinVersion
}

repositories {
    jcenter()
}

val junitVersion = "5.4.2"
val junitPlatformVersion = "1.4.2"
val http4kVersion = "3.140.0"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")

    implementation("org.http4k:http4k-core:$http4kVersion")
    implementation("org.http4k:http4k-server-jetty:$http4kVersion")
    implementation("org.http4k:http4k-format-jackson:$http4kVersion")

    implementation("com.nimbusds:nimbus-jose-jwt:7.1")

    // Test
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testRuntime("org.junit.platform:junit-platform-engine:$junitPlatformVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")

    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.14")
    testImplementation("com.squareup.okhttp3:okhttp:3.14.1")
    testImplementation("org.skyscreamer:jsonassert:1.5.0")
}

