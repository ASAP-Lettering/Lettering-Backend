import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm") version "1.9.24"

    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"

    kotlin("plugin.spring") version "1.9.24"
    kotlin("plugin.jpa") version "1.9.24"

    // test fixtures
    `java-test-fixtures`
}


allprojects {
    group = "com.asap"
    version = ""

    val javaVersion = "17"
    val kotestVersion = "5.9.1"
    val mockkVersion = "1.13.12"

    tasks.withType<JavaCompile> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = javaVersion
        }
    }

    tasks.withType<BootJar> {
        enabled = project.name == "Bootstrap-Module"
    }


    repositories {
        mavenCentral()
    }

    apply {
        plugin("kotlin")
        plugin("kotlin-spring")

        plugin("org.jetbrains.kotlin.plugin.spring")
        plugin("org.jetbrains.kotlin.plugin.jpa")

        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")


        plugin("java-test-fixtures")
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.jetbrains.kotlin:kotlin-reflect")

        // kotlin test
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
        testFixturesImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

        // spring boot test
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testFixturesImplementation("org.springframework.boot:spring-boot-starter-test")

        testRuntimeOnly("org.junit.platform:junit-platform-launcher")

        // kotest
        testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
        testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
        testImplementation("io.kotest:kotest-property:$kotestVersion")

        // mockk
        testImplementation("io.mockk:mockk:${mockkVersion}")

        // jackson
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    }

    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

}
