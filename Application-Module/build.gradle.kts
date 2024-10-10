dependencies {
    implementation(project(Modules.DOMAIN_MODULE))
    implementation(project(Modules.COMMON_MODULE))

    implementation(Dependencies.Spring.TRANSACTION)

    testImplementation(testFixtures(project(Modules.DOMAIN_MODULE)))
}
