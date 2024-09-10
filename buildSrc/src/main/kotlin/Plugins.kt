enum class Plugins(
    val module: String,
    val id: String,
    val version: String
) {

    JVM(
        module = "jvm",
        id = "kotlin",
        version = Versions.KOTLIN
    ),
    KOTLIN_SPRING(
        module = "plugin.spring",
        id = "kotlin-spring",
        version = Versions.KOTLIN
    ),
    KOTLIN_JPA(
        module = "plugin.jpa",
        id = "kotlin-jpa",
        version = Versions.KOTLIN
    ),
    SPRING_BOOT(
        module = "org.springframework.boot",
        id = "org.springframework.boot",
        version = "3.3.2"
    ),
    SPRING_DEPENDENCY_MANAGEMENT(
        module = "io.spring.dependency-management",
        id = "io.spring.dependency-management",
        version = "1.1.6"
    ),
    TEST_FIXTURES(
        module = "`java-test-fixtures`",
        id = "java-test-fixtures",
        version = "1.6.10"
    );

}