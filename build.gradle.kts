import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    apply("gradle/environment.gradle.kts")
}

plugins {
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.0.20"
    id("org.jetbrains.kotlin.plugin.jpa") version "2.0.20"

    // IntelliJ IDEA
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
    id("org.jetbrains.kotlin.plugin.spring") version "2.0.20"

    // Linting
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

apply("gradle/test.gradle.kts")

idea {
    module {
        isDownloadSources = true
    }
}

sourceSets {
    this.getByName("main") {
        this.java.srcDir("src/main/java")
        this.java.srcDir("src/main/kotlin")
    }
}

tasks.withType<KotlinCompile> {
    println("Configuring KotlinCompile $name in project ${project.name}...")

    compilerOptions {
        languageVersion.set(KOTLIN_2_1)
        apiVersion.set(KOTLIN_2_1)
        jvmTarget.set(JVM_21)
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xemit-jvm-type-annotations")
    }
}

group = "nl.nl-portal.backend"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/releases")
    maven("https://s01.oss.sonatype.org/content/groups/staging/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

val backendLibrariesReleaseVersion = "1.4.21-SNAPSHOT"
val backendLibrariesVersion =
    if (project.hasProperty("libraryVersion") && project.property("libraryVersion").toString().trim() != "") {
        project.property("libraryVersion")
    } else {
        backendLibrariesReleaseVersion
    }
val kotlinLogging = "3.0.5"
val springSecurityOAuth = "6.3.3"
println("version of nl-portal-backend-libraries '$backendLibrariesVersion' will be deployed.")

dependencies {
    // NL Portal Library dependencies
    implementation("nl.nl-portal:core:$backendLibrariesVersion")
    implementation("nl.nl-portal:data:$backendLibrariesVersion")
    implementation("nl.nl-portal:graphql:$backendLibrariesVersion")
    implementation("nl.nl-portal:klant:$backendLibrariesVersion")

    // zgw
    implementation("nl.nl-portal:catalogi-api:$backendLibrariesVersion")
    implementation("nl.nl-portal:common-ground-authentication:$backendLibrariesVersion")
    implementation("nl.nl-portal:common-ground-authentication-test:$backendLibrariesVersion")
    implementation("nl.nl-portal:documenten-api:$backendLibrariesVersion")

    implementation("nl.nl-portal:klant-generiek:$backendLibrariesVersion")
    implementation("nl.nl-portal:zaken-api:$backendLibrariesVersion")

    // Spring dependencies
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.security:spring-security-oauth2-client:$springSecurityOAuth")

    // Kotlin logger dependency
    implementation("io.github.microutils:kotlin-logging:$kotlinLogging")

    // Postgres dependency
    implementation("org.postgresql:postgresql")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

apply(from = "gradle/deployment.gradle")