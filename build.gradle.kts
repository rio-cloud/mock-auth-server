plugins {
    base
    java
    val kotlinVersion = "1.3.61"
    kotlin("jvm") version kotlinVersion
    `maven-publish`
    signing
}

version = "0.2.0-SNAPSHOT"
group = "cloud.rio"

repositories {
    jcenter()
}

val junitVersion = "5.5.2"
val junitPlatformVersion = "1.5.2"
val http4kVersion = "3.200.0"

val repositoryUser: String by project
val repositoryPassword: String by project
val repositoryUrl: String by project
val releasePath: String by project
val snapshotPath: String by project

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.1")

    implementation("org.http4k:http4k-core:$http4kVersion")
    implementation("org.http4k:http4k-server-jetty:$http4kVersion")
    implementation("org.http4k:http4k-format-jackson:$http4kVersion")

    implementation("com.nimbusds:nimbus-jose-jwt:8.2.1")

    // Test
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testRuntime("org.junit.platform:junit-platform-engine:$junitPlatformVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")

    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.20")
    testImplementation("com.squareup.okhttp3:okhttp:4.2.2")
    testImplementation("org.skyscreamer:jsonassert:1.5.0")
}

tasks.getByName<Wrapper>("wrapper").gradleVersion = "5.4"

val sourcesJar by tasks.registering(Jar::class) {
    from(sourceSets.main.get().allSource)
    classifier = "sources"
}

val javadocJar by tasks.registering(Jar::class) {
    from(tasks.javadoc)
    classifier = "javadoc"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "mock-auth-server"
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            pom {
                name.set("Mock Auth Server")
                description.set("Integrate a mock auth server within your Kotlin application to issue tokens for development and testing purpose.")
                url.set("https://github.com/rio-cloud/mock-auth-server")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("fdomig")
                        name.set("Franziskus Domig")
                        email.set("franziskus.domig@rio.cloud")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/rio-cloud/mock-auth-server.git")
                    developerConnection.set("scm:git:git://github.com/rio-cloud/mock-auth-server.git")
                    url.set("https://github.com/rio-cloud/mock-auth-server")
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri(repositoryUrl + releasePath)
            val snapshotsRepoUrl = uri(repositoryUrl + snapshotPath)
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = repositoryUser
                password = repositoryPassword
            }
        }
    }
}

signing {
    if (project.hasProperty("signing.keyId")
        && project.hasProperty("signing.password")
        && project.hasProperty("signing.secretKeyRingFile")
    ) {
        sign(publishing.publications["mavenJava"])
    }
}

