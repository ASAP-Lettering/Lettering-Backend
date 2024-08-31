dependencies{
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")


    implementation(project(":Application-Module"))
    testImplementation(testFixtures(project(":Application-Module")))

    implementation(project(":Common-Module"))

    implementation(project(":Infrastructure-Module:Client"))
    testImplementation(testFixtures(project(":Infrastructure-Module:Client")))
    implementation(project(":Infrastructure-Module:Security"))
    testImplementation(testFixtures(project(":Infrastructure-Module:Security")))
}