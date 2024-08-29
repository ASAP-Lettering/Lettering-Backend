dependencies{
    implementation(project(":Application-Module"))
    implementation(project(":Domain-Module"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("io.projectreactor:reactor-test")



}