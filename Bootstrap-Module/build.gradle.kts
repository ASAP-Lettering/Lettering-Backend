dependencies {
    implementation(Dependencies.Spring.WEB)
    implementation(Dependencies.Spring.OPEN_API)

    implementation(project(Modules.DOMAIN_MODULE))

    implementation(project(Modules.APPLICATION_MODULE))
    testImplementation(testFixtures(project(Modules.APPLICATION_MODULE)))
    testFixturesImplementation(testFixtures(project(Modules.APPLICATION_MODULE)))

    implementation(project(Modules.COMMON_MODULE))

    implementation(project(Modules.INFRASTRUCTURE_CLIENT_MODULE))
    testImplementation(testFixtures(project(Modules.INFRASTRUCTURE_CLIENT_MODULE)))

    implementation(project(Modules.INFRASTRUCTURE_SECURITY_MODULE))
    testImplementation(testFixtures(project(Modules.INFRASTRUCTURE_SECURITY_MODULE)))
    testFixturesImplementation(testFixtures(project(Modules.INFRASTRUCTURE_SECURITY_MODULE)))

    implementation(project(Modules.INFRASTRUCTURE_AWS_MODULE))
    testFixturesImplementation(testFixtures(project(Modules.INFRASTRUCTURE_AWS_MODULE)))

    implementation(project(Modules.INFRASTRUCTURE_PERSISTENCE_MODULE))
    testFixturesImplementation(testFixtures(project(Modules.INFRASTRUCTURE_PERSISTENCE_MODULE)))

    implementation(project(Modules.INFRASTRUCTURE_EVENT_MODULE))
}
