import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin(Plugins.JVM.module) version Plugins.JVM.version

    id(Plugins.SPRING_BOOT.module) version Plugins.SPRING_BOOT.version
    id(Plugins.SPRING_DEPENDENCY_MANAGEMENT.module) version Plugins.SPRING_DEPENDENCY_MANAGEMENT.version

    kotlin(Plugins.KOTLIN_SPRING.module) version Plugins.KOTLIN_SPRING.version
    kotlin(Plugins.KOTLIN_JPA.module) version Plugins.KOTLIN_JPA.version

    // test fixtures
    `java-test-fixtures`

    id("jacoco")
    id("jacoco-report-aggregation")
    id("org.sonarqube") version "5.1.0.4882"
}

allprojects {
    group = "com.asap"
    version = ""

    tasks.withType<JavaCompile> {
        sourceCompatibility = Versions.JAVA_VERSION
        targetCompatibility = Versions.JAVA_VERSION
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = Versions.JAVA_VERSION
        }
    }

    tasks.withType<BootJar> {
        enabled = project.name == Versions.ROOT_MODULE
    }

    repositories {
        mavenCentral()
    }

    apply {
        plugin(Plugins.JVM.id)
        plugin(Plugins.KOTLIN_SPRING.id)
        plugin(Plugins.SPRING_BOOT.id)
        plugin(Plugins.SPRING_DEPENDENCY_MANAGEMENT.id)
        plugin(Plugins.TEST_FIXTURES.id)

        plugin("jacoco")
        plugin("jacoco-report-aggregation")
        plugin("org.sonarqube")
    }

    dependencies {
        implementation(Dependencies.Spring.BOOT)
        implementation(Dependencies.Kotlin.KOTLIN_REFLECT)
        testImplementation(Dependencies.Spring.TEST)
        testFixturesImplementation(Dependencies.Spring.TEST)
        testRuntimeOnly(Dependencies.Junit.JUNIT_LAUNCHER)
        testImplementation(Dependencies.Kotlin.KOTLIN_TEST_JUNIT5)
        testFixturesImplementation(Dependencies.Kotlin.KOTLIN_TEST_JUNIT5)
        testImplementation(Dependencies.Kotest.KOTEST_RUNNER)
        testImplementation(Dependencies.Kotest.KOTEST_ASSERTIONS_CORE)
        testImplementation(Dependencies.Kotest.KOTEST_PROPERTY)
        testImplementation(Dependencies.Mockk.MOCKK)
        implementation(Dependencies.Jackson.KOTLIN)
        implementation(Dependencies.Jackson.JAVA_TIME)
        implementation(Dependencies.Logger.KOTLIN_OSHAI)
        implementation(Dependencies.Instancio.CORE)
        implementation(Dependencies.Instancio.JUNIT)
    }

    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    jacoco {
        toolVersion = "0.8.7"
    }
}

tasks.testCodeCoverageReport {
    reports {
        xml.required = true
    }
}

dependencies {
    allprojects.forEach {
        add("jacocoAggregation", project(it.path))
    }
}

sonar {
    properties {
        property("sonar.projectKey", "ASAP-Lettering_Lettering-Backend")
        property("sonar.organization", "asap-lettering")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.java.coveragePlugin", "jacoco")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            rootProject.layout.buildDirectory.file("reports/jacoco/testCodeCoverageReport/testCodeCoverageReport.xml"),
        )
    }
}
