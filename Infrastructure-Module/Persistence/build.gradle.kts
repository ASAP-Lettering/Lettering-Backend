apply {
    plugin(Plugins.KOTLIN_JPA.id)
}

dependencies {
    implementation(Dependencies.Spring.DATA_JPA)
    runtimeOnly(Dependencies.DATABASE.MYSQL)
    runtimeOnly(Dependencies.DATABASE.H2)

    implementation(Dependencies.Flyway.FLYWAY_CORE)
    implementation(Dependencies.Flyway.FLYWAY_MYSQL)
}
